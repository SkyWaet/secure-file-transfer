package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ConfirmReadRequest {
    @Nonnull
    @NotBlank
    private final String fileId;

    private ConfirmReadRequest(@Nonnull String fileId) {
        this.fileId = requireNonNull(fileId, "fileId");
    }

    @Nonnull
    public String getFileId() {
        return fileId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private String fileId;

        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public ConfirmReadRequest build() {
            return new ConfirmReadRequest(fileId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmReadRequest that = (ConfirmReadRequest) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }

    @Override
    public String toString() {
        return "ConfirmReadRequest{" +
                "fileId='" + fileId + '\'' +
                '}';
    }
}
