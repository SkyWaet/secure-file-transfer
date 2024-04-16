package com.skywaet.securefiletransfer.consumer.provider;

import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import jakarta.annotation.Nonnull;

import java.io.InputStream;

/**
 * Interface to access file storage
 */
public interface FileProvider {

    /**
     * @return type of this provider
     */
    @Nonnull
    FileStorageType getType();

    /**
     * @return metadata of provided file
     */
    FileMetadata getMetadata();

    /**
     * @return stream with file content
     */
    @Nonnull
    InputStream getFileContent();
}
