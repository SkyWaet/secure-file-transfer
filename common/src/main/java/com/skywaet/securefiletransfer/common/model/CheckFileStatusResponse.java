package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckFileStatusResponse {

    @Nonnull
    private final String fileId;

    @Nonnull
    private final String fileStatus;

    @JsonCreator
    private CheckFileStatusResponse(@JsonProperty("fileId") @Nonnull String fileId,
                                    @JsonProperty("fileStatus") @Nonnull String fileStatus) {
        this.fileId = Objects.requireNonNull(fileId);
        this.fileStatus = Objects.requireNonNull(fileStatus);
    }

    @Nonnull
    @JsonProperty("fileId")
    public String getFileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty("fileStatus")
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

        public Builder withFileStatus(FileStatus fileStatus) {
            this.fileStatus = fileStatus.getCode();
            return this;
        }

        public CheckFileStatusResponse build() {
            return new CheckFileStatusResponse(fileId, fileStatus);
        }
    }
}
