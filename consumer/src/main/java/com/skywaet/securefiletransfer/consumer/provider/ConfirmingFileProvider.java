package com.skywaet.securefiletransfer.consumer.provider;

import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.model.ConfirmReadRequest;
import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

class ConfirmingFileProvider implements FileProvider {
    @Nonnull
    private final FileProvider delegate;

    @Nonnull
    ConfirmingFileProvider(@Nonnull FileProvider delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Nonnull
    @Override
    public FileStorageType getType() {
        return delegate.getType();
    }

    @Override
    public FileMetadata getMetadata() {
        return delegate.getMetadata();
    }

    @Nonnull
    @Override
    public InputStream getFileContent() {
        return delegate.getFileContent();
    }

    private static abstract class StreamWithConfirmation extends InputStream {
        @Nonnull
        private final FileTransferClient client;

        @Nonnull
        private final InputStream delegate;

        @Nonnull
        private final FileMetadata metadata;

        private StreamWithConfirmation(@Nonnull FileTransferClient client,
                                       @Nonnull InputStream delegate,
                                       @Nonnull FileMetadata fileMetadata) {
            this.client = Objects.requireNonNull(client);
            this.delegate = Objects.requireNonNull(delegate);
            this.metadata = fileMetadata;
        }


        @Override
        public void close() throws IOException {
            delegate.close();
            client.confirmReadFile(ConfirmReadRequest.builder()
                    .withFileId(metadata.fileId())
                    .build());
        }
    }
}
