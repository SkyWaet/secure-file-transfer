package com.skywaet.securefiletransfer.producer.storage;

import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.common.model.SendFileRequest;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.function.Supplier;

public class MinioFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioFileStorage.class);
    @Nonnull
    private final String bucketName;
    @Nonnull
    private final MinioClient client;

    public MinioFileStorage(@Nonnull String bucketName,
                            @Nonnull MinioClient client) {
        this.bucketName = Objects.requireNonNull(bucketName);
        this.client = Objects.requireNonNull(client);
    }

    @Nonnull
    @Override
    public FileStorageType type() {
        return FileStorageType.MINIO;
    }

    @Override
    public void store(@Nonnull SendFileRequest request,
                      @Nonnull Supplier<InputStream> content) {
        var putObjectParameters = PutObjectArgs.builder()
                .stream(content.get(), -1, 5 * 1024 * 1024)
                .bucket(bucketName)
                .object(request.getFileId())
                .build();

        try {
            client.putObject(putObjectParameters);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error while sending file with id {}", request.getFileId(), e);
            throw new RuntimeException(e);
        }
    }
}
