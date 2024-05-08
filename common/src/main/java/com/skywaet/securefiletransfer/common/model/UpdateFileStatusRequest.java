package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@JsonPropertyOrder(alphabetic = true)
public class UpdateFileStatusRequest {
    @Nonnull
    @NotBlank
    private final String fileId;

    @Nonnull
    private final String fileStatus;

    @JsonCreator
    private UpdateFileStatusRequest(@Nonnull @JsonProperty("fileId") String fileId,
                                    @Nonnull @JsonProperty("fileStatus") String fileStatus) {
        this.fileId = requireNonNull(fileId, "fileId");
        this.fileStatus = requireNonNull(fileStatus, "fileStatus");
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
        private FileStatus fileStatus;

        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder withFileStatus(FileStatus fileStatus) {
            this.fileStatus = fileStatus;
            return this;
        }

        public UpdateFileStatusRequest build() {
            return new UpdateFileStatusRequest(fileId, fileStatus.getCode());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateFileStatusRequest that = (UpdateFileStatusRequest) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }

    @Override
    public String toString() {
        return "UpdateFileStatusRequest{" +
                "fileId='" + fileId + '\'' +
                '}';
    }
}
