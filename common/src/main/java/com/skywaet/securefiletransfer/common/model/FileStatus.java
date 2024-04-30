package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum FileStatus {
    CONSUMED("consumed"),
    CONSUMER_ERROR("consumer_error"),
    PROVIDER_ERROR("provider_error"),
    PENDING("pending");

    @Nonnull
    private final String code;

    FileStatus(@Nonnull String code) {
        this.code = Objects.requireNonNull(code);
    }

    @Nonnull
    public String getCode() {
        return code;
    }

    public static Optional<FileStatus> optionalByCode(@Nonnull String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equals(code))
                .findAny();
    }
}
