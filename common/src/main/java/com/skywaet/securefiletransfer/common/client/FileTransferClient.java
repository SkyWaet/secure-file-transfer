package com.skywaet.securefiletransfer.common.client;


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
        var proposal = contract.newProposal("SendFile")
                .addArguments(request.getFileId(), request.getFileName(), request.getDescription())
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
    public ConfirmReadResponse confirmReadFile(@Nonnull ConfirmReadRequest request) {
        var proposal = contract.newProposal("ConfirmFileRead")
                .addArguments(request.getFileId())
                .build();
        try {
            var transaction = proposal.endorse().submit();
            return mapper.readValue(transaction, ConfirmReadResponse.class);
        } catch (EndorseException | SubmitException | IOException | CommitException | CommitStatusException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke CheckFile method of smart contract
     *
     * @param request CheckFile method parameters
     * @return object with response data
     */
    @Nonnull
    public CheckFileResponse checkFile(@Nonnull CheckFileRequest request) {
        var proposal = contract.newProposal("CheckFile")
                .addArguments(request.getFileId(), new String(request.getFileHash()))
                .build();
        try {
            var transaction = proposal.endorse().submit();
            return mapper.readValue(transaction, CheckFileResponse.class);
        } catch (EndorseException | SubmitException | IOException | CommitException | CommitStatusException e) {
            throw new RuntimeException(e);
        }
    }


}
