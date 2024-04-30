package com.skywaet.securefiletransfer.producer.model;

import com.skywaet.securefiletransfer.common.model.FileStorageType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public record SendFileParameters(@Nonnull String fileName,
                                 @Nonnull FileStorageType storageType,
                                 @Nullable String description) {

    public SendFileParameters {
        requireNonNull(fileName, "fileName");
        requireNonNull(storageType, "storageType");
    }
}
