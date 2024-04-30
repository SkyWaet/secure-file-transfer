package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

public class CheckFileStatusResponse {

    @Nonnull
    private final String fileId;

    @Nonnull
    private final String fileStatus;

    @JsonCreator
    private CheckFileStatusResponse(@JsonProperty @Nonnull String fileId,
                                    @JsonProperty @Nonnull String fileStatus) {
        this.fileId = Objects.requireNonNull(fileId);
        this.fileStatus = Objects.requireNonNull(fileStatus);
    }

    @Nonnull
    @JsonProperty
    public String getFileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty
    public String getFileStatusRaw() {
        return fileStatus;
    }

    @Nonnull
    public Optional<FileStatus> getFileStatus() {
        return FileStatus.optionalByCode(fileStatus);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private String fileId;

        private String fileStatus;

        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder withFileStatus(String fileStatus) {
            this.fileStatus = fileStatus;
            return this;
        }

        public CheckFileStatusResponse build() {
            return new CheckFileStatusResponse(fileId, fileStatus);
        }
    }
}
