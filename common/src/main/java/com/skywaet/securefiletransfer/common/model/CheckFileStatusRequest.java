package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public class CheckFileStatusRequest {
    @Nonnull
    private final String fileId;

    private CheckFileStatusRequest(@Nonnull String fileId) {
        this.fileId = Objects.requireNonNull(fileId);
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

        public CheckFileStatusRequest build() {
            return new CheckFileStatusRequest(fileId);
        }
    }
}
