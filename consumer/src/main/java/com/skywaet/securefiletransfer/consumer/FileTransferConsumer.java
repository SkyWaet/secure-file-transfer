package com.skywaet.securefiletransfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.model.CheckFileStatusRequest;
import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStatus;
import com.skywaet.securefiletransfer.common.model.UpdateStatusRequest;
import com.skywaet.securefiletransfer.consumer.provider.CommonFileProviderFactory;
import com.skywaet.securefiletransfer.consumer.provider.FileProvider;
import io.grpc.Status;
import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FileTransferConsumer {
    private static final Logger log = LoggerFactory.getLogger(FileTransferConsumer.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Network network;
    private final String contractName;
    @Nonnull
    private final FileTransferClient fileTransferClient;
    @Nonnull
    private final CommonFileProviderFactory providerFactory;
    @Nonnull
    private final Consumer<FileProvider> contentConsumer;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final AtomicBoolean running = new AtomicBoolean(true);

    public FileTransferConsumer(Gateway gateway,
                                String networkName,
                                String contractName,
                                @Nonnull FileTransferClient fileTransferClient,
                                @Nonnull CommonFileProviderFactory providerFactory,
                                @Nonnull Consumer<FileProvider> contentConsumer) {
        this.fileTransferClient = Objects.requireNonNull(fileTransferClient);
        this.providerFactory = Objects.requireNonNull(providerFactory);
        this.contentConsumer = Objects.requireNonNull(contentConsumer);
        this.network = gateway.getNetwork(networkName);
        this.contractName = contractName;
    }

    public void poll() {
        Checkpointer checkpointer = new InMemoryCheckpointer();

        while (running.get()) {
            var request = network.newChaincodeEventsRequest(contractName)
                    .checkpoint(checkpointer)
                    .build();
            try {
                var tasks = readEvents(request);
                if (tasks.isEmpty()) {
                    log.info("No events found. Skip");
                } else {
                    int taskNumber = 0;
                    long maxSuccessBlockNumber = -1;
                    long minFailedBlockNumber = Long.MAX_VALUE;
                    var proceeded = new HashSet<Integer>();
                    while (proceeded.size() < tasks.size()) {
                        if (proceeded.contains(taskNumber)) {
                            continue;
                        }
                        var task = tasks.get(taskNumber);
                        if (task.isDone()) {
                            switch (task.state()) {
                                case SUCCESS -> {
                                    var taskResult = task.get();
                                    var taskBlockNumber = taskResult.getBlockNumber();
                                    maxSuccessBlockNumber = Math.max(taskBlockNumber, maxSuccessBlockNumber);
                                }
                                case FAILED -> {
                                    log.error("Error while processing task", task.exceptionNow());
                                }
                                case CANCELLED -> log.warn("Task was cancelled");
                                case RUNNING -> throw new IllegalStateException("Task could not be at running stage");
                            }
                            proceeded.add(taskNumber);
                            taskNumber = (taskNumber + 1) % tasks.size();
                        }
                    }
                    var checkpointBlock = maxSuccessBlockNumber > 0
                            ? Math.min(maxSuccessBlockNumber, minFailedBlockNumber)
                            : minFailedBlockNumber;
                    log.info("Successfully proceeded blocks up to {}", checkpointBlock);
                    checkpointer.checkpointBlock(checkpointBlock);
                }

            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Future<ChaincodeEvent>> readEvents(ChaincodeEventsRequest request) {
        var tasks = new ArrayList<Future<ChaincodeEvent>>();
        try (var events = request.getEvents()) {
            events.forEachRemaining(event -> {
                log.info("processing new event");
                var payload = new String(event.getPayload(), StandardCharsets.UTF_8);
                tasks.add(executor.submit(() -> processMessage(payload), event));
                log.info("found event {}", event.getEventName());
            });
            return tasks;
        } catch (GatewayRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                log.warn("Deadline exceeded");
                return tasks;
            }
            if (e.getStatus().getCode() == Status.Code.CANCELLED) {
                log.warn("Connection cancelled");
                return tasks;
            }
            log.error("Error while reading events", e);
            throw new RuntimeException(e);
        }
    }

    private void processMessage(@Nonnull String payload) {
        try {
            var metadata = mapper.readValue(payload, FileMetadata.class);
//            if (isProceeded(metadata.fileId())) {
//                return;
//            }
            var provider = providerFactory.createProvider(metadata);
            verify(provider);
            contentConsumer.accept(provider);
          //  confirmRead(metadata);
        } catch (IOException e) {
            log.error("Error while processing payload {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private boolean isProceeded(@Nonnull String fileId) {
        var response = fileTransferClient.checkFileStatus(CheckFileStatusRequest.builder()
                .withFileId(fileId)
                .build());

        return response.getFileStatus().filter(FileStatus.CONSUMED::equals).isPresent();
    }

    private void verify(@Nonnull FileProvider provider) {
        checkHash(provider);
    }

    private void checkHash(@Nonnull FileProvider provider) {
        try (var content = provider.getFileContent()) {

            var messageDigest = MessageDigest.getInstance(provider.getMetadata().hashingProperties().getAlgorithm());
            var buffer = new byte[1024];

            while (content.read(buffer) > 0) {
                messageDigest.update(buffer);
            }
            var actual = messageDigest.digest();
            var expected = provider.getMetadata().hashingProperties().getHash();
            if (!Arrays.equals(expected, actual)) {
                throw new IllegalStateException("Actual hash is not equal to expected");
            }
        } catch (IOException |
                 NoSuchAlgorithmException e) {
            log.error("Error while computing file hash: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void confirmRead(FileMetadata metadata) {
        var response = fileTransferClient.updateStatus(UpdateStatusRequest.builder()
                .withFileId(metadata.fileId())
                .withFileStatus(FileStatus.CONSUMED)
                .build());

        if (!response.isSuccess()) {
            log.warn("Read was not confirmed for file with id {}. Errors = {}", metadata.fileId(),
                    response.getErrorMessage().orElse("No message provided"));
        }
    }

    public void terminate() {
        running.set(false);
    }
}
