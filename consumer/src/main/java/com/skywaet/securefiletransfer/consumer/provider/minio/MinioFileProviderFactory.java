package com.skywaet.securefiletransfer.consumer.provider.minio;

import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.consumer.provider.FileProviderFactory;
import io.minio.MinioClient;
import jakarta.annotation.Nonnull;

import java.util.Objects;

public class MinioFileProviderFactory implements FileProviderFactory {

    @Nonnull
    private final MinioClient client;

    public MinioFileProviderFactory(@Nonnull MinioClient client) {
        this.client = Objects.requireNonNull(client);
    }

    @Nonnull
    @Override
    public FileStorageType getType() {
        return FileStorageType.MINIO;
    }

    @Nonnull
    public MinioFileProvider get(@Nonnull FileMetadata metadata) {
        return new MinioFileProvider(client, metadata);
    }
}
