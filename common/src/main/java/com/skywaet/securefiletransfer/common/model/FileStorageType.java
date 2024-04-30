package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum FileStorageType {
    MINIO("minio");

    @Nonnull
    private final String code;

    FileStorageType(@Nonnull String code) {
        this.code = Objects.requireNonNull(code);
    }

    public static Optional<FileStorageType> optionalByCode(@Nonnull String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equals(code))
                .findAny();
    }

    @Nonnull
    public String getCode() {
        return code;
    }
}
