package com.skywaet.securefiletransfer.consumer.provider;

import com.skywaet.securefiletransfer.common.model.FileMetadata;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CommonFileProviderFactory {

    @Nonnull
    private final Map<FileStorageType, FileProviderFactory> factories;

    public CommonFileProviderFactory(@Nonnull List<FileProviderFactory> factories) {
        requireNonNull(factories);
        if (factories.isEmpty()) {
            throw new IllegalArgumentException("No file provider factories given");
        }
        this.factories = factories.stream().collect(Collectors.toMap(FileProviderFactory::getType, it -> it));
    }

    public FileProvider createProvider(@Nonnull FileMetadata metadata) {
        requireNonNull(metadata, "metadata");

        return metadata.storageType()
                .map(factories::get)
                .map(it -> it.get(metadata))
                .orElseThrow(() -> new IllegalArgumentException("No provider found for type %s".formatted(metadata.storageTypeRaw())));
    }
}
