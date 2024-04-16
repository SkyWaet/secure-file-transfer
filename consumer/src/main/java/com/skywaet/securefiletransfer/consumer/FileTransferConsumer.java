package com.skywaet.securefiletransfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.consumer.provider.CommonFileProviderFactory;
import com.skywaet.securefiletransfer.consumer.provider.FileProvider;
import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.Checkpointer;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.InMemoryCheckpointer;
import org.hyperledger.fabric.client.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

    @Nonnull
    public List<CompletableFuture<FileProvider>> pullEvents() {
        Checkpointer checkpointer = new InMemoryCheckpointer();
        var request = network.newChaincodeEventsRequest(contractName)
                .checkpoint(checkpointer)
                .build();
        try (var events = request.getEvents()) {
            var result = new ArrayList<CompletableFuture<FileProvider>>();
            while (events.hasNext()) {
                log.info("processing new event");
                var event = events.next();
                var fileId = new String(event.getPayload(), StandardCharsets.UTF_8);
                result.add(readMessage(fileId));

                log.info("found event {}", event.getEventName());
            }
            log.info("No events found. Skip");
            return result;
        }
    }

    @Nonnull
    private CompletableFuture<FileProvider> readMessage(String payload) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return mapper.readValue(payload, FileMetadata.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenApply(providerFactory::createProvider)
                .thenApply(this::verify)
                .thenApply(it -> {
                    contentConsumer.accept(it);
                    return it;
                })
                .handle();
    }

    @Nonnull
    private FileProvider verify(@Nonnull FileProvider provider) {
        if (provider.getMetadata().hashingProperties().isEnabled()) {
            checkHash(provider);
        }
        return provider;
    }

    private void checkHash(@Nonnull FileProvider provider) {
        try (var content = provider.getFileContent()) {

            var messageDigest = MessageDigest.getInstance(provider.getMetadata().hashingProperties().getAlgorithm().orElseThrow());
            var buffer = new byte[1024];

            while (content.read(buffer) > 0) {
                messageDigest.update(buffer);
            }
            var actual = messageDigest.digest();
            var expected = provider.getMetadata().hashingProperties().getHash().orElseThrow();
            if (!Arrays.equals(expected, actual)) {
                throw new IllegalStateException("Actual hash is not equal to expected");
            }
        } catch (IOException |
                 NoSuchAlgorithmException e) {
            log.error("Error while computing file hash: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private FileProvider process
}
