package com.skywaet.securefiletransfer.consumer.provider.minio;

import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.consumer.provider.FileProvider;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Provider used to get files from Minio
 */
public class MinioFileProvider implements FileProvider {
    @Nonnull
    private final MinioClient client;
    @Nonnull
    private final FileMetadata metadata;

    public MinioFileProvider(@Nonnull MinioClient client,
                             @Nonnull FileMetadata metadata) {
        this.client = Objects.requireNonNull(client);
        this.metadata = Objects.requireNonNull(metadata);
    }

    @Override
    public @Nonnull InputStream getFileContent() {
        var parameters = GetObjectArgs.builder()
                .bucket(metadata.fileName())
                .object(metadata.fileId())
                .build();
        return getFileInternal(parameters);
    }

    private InputStream getFileInternal(@Nonnull GetObjectArgs args) {
        try {
            return client.getObject(args);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public FileStorageType getType() {
        return FileStorageType.MINIO;
    }

    @Nonnull
    @Override
    public FileMetadata getMetadata() {
        return metadata;
    }

}
