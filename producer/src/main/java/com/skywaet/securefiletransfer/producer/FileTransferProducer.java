package com.skywaet.securefiletransfer.producer;

import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.common.model.SendFileRequest;
import com.skywaet.securefiletransfer.producer.model.SendFileParameters;
import com.skywaet.securefiletransfer.producer.storage.FileStorage;
import jakarta.annotation.Nonnull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FileTransferProducer {

    @Nonnull
    private final FileTransferClient fileTransferClient;

    @Nonnull
    private final Map<FileStorageType, FileStorage> storages;

    public FileTransferProducer(@Nonnull FileTransferClient fileTransferClient,
                                   @Nonnull List<FileStorage> storageList) {
        this.fileTransferClient = fileTransferClient;
        this.storages = storageList.stream().collect(Collectors.toMap(FileStorage::type, it -> it));
    }


    @Nonnull
    public String sendFile(@Nonnull SendFileParameters parameters,
                           @Nonnull Supplier<InputStream> content) {
        var fileId = UUID.randomUUID().toString();
        var storage = storages.get(parameters.storageType());

        if (storage == null) {
            throw new IllegalArgumentException("Unsupported storage type " + parameters.storageType());
        }
        var request = SendFileRequest.builder()
                .withFileId(fileId)
                .withDescription(parameters.description())
                .withFileName(parameters.fileName())
                .withStorageType(parameters.storageType().getCode())
                .withHashingAlgorithm("SHA-512")
                .build();
        storage.store(request, content);

        var response = fileTransferClient.sendFile(request);
        return response.getFileId();
    }
}
