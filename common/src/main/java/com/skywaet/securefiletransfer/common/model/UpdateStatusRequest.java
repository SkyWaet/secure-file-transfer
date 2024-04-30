package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class UpdateStatusRequest {
    @Nonnull
    @NotBlank
    private final String fileId;

    @Nonnull
    private final FileStatus fileStatus;

    private UpdateStatusRequest(@Nonnull String fileId,
                                @Nonnull FileStatus fileStatus) {
        this.fileId = requireNonNull(fileId, "fileId");
        this.fileStatus = requireNonNull(fileStatus, "fileStatus");
    }

    @Nonnull
    public String getFileId() {
        return fileId;
    }

    @Nonnull
    public FileStatus getFileStatus() {
        return fileStatus;
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

        public UpdateStatusRequest build() {
            return new UpdateStatusRequest(fileId, fileStatus);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateStatusRequest that = (UpdateStatusRequest) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }

    @Override
    public String toString() {
        return "UpdateStatusRequest{" +
                "fileId='" + fileId + '\'' +
                '}';
    }
}
