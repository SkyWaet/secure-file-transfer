package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum ChaincodeEventType {
    SEND_FILE("sendFile"),
    UPDATE_FILE_STATUS("updateFileStatus");

    @Nonnull
    private final String code;

    ChaincodeEventType(@Nonnull String code) {
        this.code = Objects.requireNonNull(code);
    }

    @Nonnull
    public static Optional<ChaincodeEventType> optionalByCode(String code) {
        return Arrays.stream(ChaincodeEventType.values())
                .filter(it -> it.code.equals(code))
                .findAny();
    }

    @Nonnull
    public String getCode() {
        return code;
    }
}
