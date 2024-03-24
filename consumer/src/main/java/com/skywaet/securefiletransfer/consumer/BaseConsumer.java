package com.skywaet.securefiletransfer.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skywaet.securefiletransfer.common.model.FileMetadata;
import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(BaseConsumer.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Network network;
    private final String contractName;
    private final Contract contract;
    private final String topicName;


    public BaseConsumer(Gateway gateway,
                        String networkName,
                        String contractName, String topicName) {
        this.topicName = topicName;
        this.network = gateway.getNetwork(networkName);
        this.contractName = contractName;
        this.contract = network.getContract(contractName);
    }

    public List<CompletableFuture<FileMetadata>> pullEvents() {
        Checkpointer checkpointer = new InMemoryCheckpointer();
        var request = network.newChaincodeEventsRequest(contractName)
                .checkpoint(checkpointer)
                .build();
        try (var events = request.getEvents()) {
            var result = new ArrayList<CompletableFuture<FileMetadata>>();
            while (events.hasNext()) {
                log.info("processing new event");
                var event = events.next();
                if (event.getEventName().endsWith(topicName)) {
                    var fileId = new String(event.getPayload(), StandardCharsets.UTF_8);
                    result.add(readMessage(fileId));
                }
                log.info("found event {}", event.getEventName());
            }
            log.info("No events found. Skip");
            return result;
        }
    }

    private CompletableFuture<FileMetadata> readMessage(String fileId) {
        try {
            var result = contract.submitTransaction("ReadFile", fileId);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return mapper.readValue(result, FileMetadata.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (EndorseException | SubmitException | CommitStatusException | CommitException e) {
            log.error("Error while reading message", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
