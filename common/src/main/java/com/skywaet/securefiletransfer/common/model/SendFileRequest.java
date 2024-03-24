package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of sendFile method of fileTransfer contract
 */
public final class SendFileRequest {
    /**
     * ID of the file in the storage
     */
    @Nonnull
    private final @NotBlank String fileId;
    /**
     * Name of the file
     */
    @Nonnull
    private final @NotBlank String fileName;
    /**
     * Optional description of file
     */
    @Nullable
    private final String description;


    private SendFileRequest(@Nonnull @NotBlank String fileId,
                            @Nonnull @NotBlank String fileName,
                            @Nullable String description) {
        requireNonNull(fileId, "fileId");
        requireNonNull(fileName, "fileName");
        this.fileId = fileId;
        this.fileName = fileName;
        this.description = description;
    }

    @Nonnull
    public @NotBlank String getFileId() {
        return fileId;
    }

    @Nonnull
    public @NotBlank String getFileName() {
        return fileName;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private String fileId;
        private String fileName;
        private String description;

        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SendFileRequest build() {
            return new SendFileRequest(fileId, fileName, description);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SendFileRequest) obj;
        return Objects.equals(this.fileId, that.fileId) &&
                Objects.equals(this.fileName, that.fileName) &&
                Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileName, description);
    }

    @Override
    public String toString() {
        return "SendFileRequest[" +
                "fileId=" + fileId + ", " +
                "fileName=" + fileName + ", " +
                "description=" + description + ']';
    }

}
