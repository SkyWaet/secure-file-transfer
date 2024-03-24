package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;
import java.util.Optional;

/**
 * Metadata of the file, used to store data in blockchain
 */
@JsonPropertyOrder(alphabetic = true)
public final class FileMetadata {
    @Nonnull
    @JsonProperty
    private final @NotBlank String fileId;
    @Nonnull
    @JsonProperty
    private final @NotBlank String fileName;
    @Nullable
    @JsonProperty
    private final String description;
    @Nonnull
    @JsonProperty
    private final byte @NotEmpty [] fileHash;
    @Nonnull
    @JsonProperty
    private final @NotBlank String sender;

    @JsonCreator
    private FileMetadata(@Nonnull @NotBlank @JsonProperty String fileId,
                         @Nonnull @NotBlank @JsonProperty String fileName,
                         @Nullable @JsonProperty String description,
                         @Nonnull @NotEmpty @JsonProperty byte[] fileHash,
                         @Nonnull @NotBlank @JsonProperty String sender) {
        this.fileId = Objects.requireNonNull(fileId);
        this.fileName = Objects.requireNonNull(fileName);
        this.description = description;
        this.fileHash = Objects.requireNonNull(fileHash);
        this.sender = Objects.requireNonNull(sender);
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String getFileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String fileName() {
        return fileName;
    }

    @Nonnull
    @JsonProperty
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Nonnull
    @JsonProperty
    public byte @NotEmpty [] fileHash() {
        return fileHash;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String sender() {
        return sender;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        private String fileId;
        private String fileName;
        private String description;
        private byte[] fileHash;
        private String sender;

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

        public Builder withFileHash(byte[] fileHash) {
            this.fileHash = fileHash;
            return this;
        }

        public Builder withSender(String sender) {
            this.sender = sender;
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(fileId,
                    fileName,
                    description,
                    fileHash,
                    sender);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FileMetadata) obj;
        return Objects.equals(this.fileId, that.fileId) &&
                Objects.equals(this.fileName, that.fileName) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.fileHash, that.fileHash) &&
                Objects.equals(this.sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileName, description, fileHash, sender);
    }

    @Override
    public String toString() {
        return "FileMetadata[" +
                "fileId=" + fileId + ", " +
                "fileName=" + fileName + ", " +
                "description=" + description + ", " +
                "fileHash=" + fileHash + ", " +
                "sender=" + sender + ']';
    }

}
