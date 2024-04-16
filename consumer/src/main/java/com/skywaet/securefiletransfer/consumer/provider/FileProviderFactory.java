package com.skywaet.securefiletransfer.consumer.provider;

import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import jakarta.annotation.Nonnull;

public interface FileProviderFactory {
    @Nonnull
    FileStorageType getType();

    @Nonnull
    FileProvider get(@Nonnull FileMetadata metadata);
}
