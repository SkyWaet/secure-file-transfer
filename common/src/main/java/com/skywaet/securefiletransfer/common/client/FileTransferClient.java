package com.skywaet.securefiletransfer.common.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skywaet.securefiletransfer.common.model.*;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.hyperledger.fabric.client.*;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Client for secure file transfer contract
 */
public class FileTransferClient {
    @Nonnull
    private final Contract contract;
    @Nonnull
    private final ObjectMapper mapper;

    public FileTransferClient(@Nonnull Gateway gateway,
                              @Nonnull @NotBlank String channelName,
                              @Nonnull @NotBlank String contractName,
                              @Nonnull ObjectMapper mapper) {
        requireNonNull(gateway, "gateway");
        requireNonNull(channelName, "channelName");
        requireNonNull(contractName, "contractName");

        var network = gateway.getNetwork(channelName);
        this.contract = network.getContract(contractName);
        this.mapper = requireNonNull(mapper, "mapper");
    }

    /**
     * Invoke SendFile method of smart contract
     *
     * @param request SendFile method parameters
     * @return object with response data
     */
    @Nonnull
    public SendFileResponse sendFile(@Nonnull SendFileRequest request) {
        var proposal = contract.newProposal("sendFile")
                .addArguments(serialize(request))
                .build();
        try {
            var transaction = proposal.endorse().submit();
            return mapper.readValue(transaction, SendFileResponse.class);
        } catch (EndorseException | SubmitException | IOException | CommitException | CommitStatusException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke ConfirmRead method of smart contract
     *
     * @param request ConfirmRead method parameters
     * @return object with response data
     */
    @Nonnull
    public UpdateStatusResponse updateStatus(@Nonnull UpdateStatusRequest request) {
        var proposal = contract.newProposal("confirmFileRead")
                .addArguments(request.getFileId())
                .addArguments(request.getFileStatus().getCode())
                .build();
        try {
            var transaction = proposal.endorse().submit();
            return mapper.readValue(transaction, UpdateStatusResponse.class);
        } catch (EndorseException | SubmitException | IOException | CommitException | CommitStatusException e) {
            throw new RuntimeException(e);
        }
    }


    @Nonnull
    public CheckFileStatusResponse checkFileStatus(@Nonnull CheckFileStatusRequest request) {
        var proposal = contract.newProposal("checkFileStatus")
                .addArguments(serialize(request))
                .build();
        try {
            var transaction = proposal.evaluate();
            return mapper.readValue(transaction, CheckFileStatusResponse.class);
        } catch (IOException | GatewayException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> byte[] serialize(T obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
