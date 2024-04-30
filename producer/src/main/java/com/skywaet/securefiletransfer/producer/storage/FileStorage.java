package com.skywaet.securefiletransfer.producer.storage;

import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.common.model.SendFileRequest;
import jakarta.annotation.Nonnull;

import java.io.InputStream;
import java.util.function.Supplier;

public interface FileStorage {
    @Nonnull
    FileStorageType type();

    void store(@Nonnull SendFileRequest request,
               @Nonnull Supplier<InputStream> content);
}
