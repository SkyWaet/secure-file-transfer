package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

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
    private final HashingProperties hashingProperties;
    @Nonnull
    @JsonProperty
    private final @NotBlank String sender;
    @Nonnull
    @JsonProperty
    private final @NotBlank String storageType;

    @JsonCreator
    private FileMetadata(@Nonnull @NotBlank @JsonProperty String fileId,
                         @Nonnull @NotBlank @JsonProperty String fileName,
                         @Nullable @JsonProperty String description,
                         @Nonnull @JsonProperty HashingProperties hashingProperties,
                         @Nonnull @NotBlank @JsonProperty String sender,
                         @Nonnull String storageType) {
        this.fileId = Objects.requireNonNull(fileId);
        this.fileName = Objects.requireNonNull(fileName);
        this.description = description;
        this.hashingProperties = Objects.requireNonNull(hashingProperties);
        this.sender = Objects.requireNonNull(sender);
        this.storageType = storageType;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String fileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String fileName() {
        return fileName;
    }

    @Nonnull
    @JsonProperty
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    @Nonnull
    @JsonProperty
    public HashingProperties hashingProperties() {
        return hashingProperties;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String sender() {
        return sender;
    }

    @Nonnull
    @JsonProperty
    public @NotBlank String storageTypeRaw() {
        return sender;
    }

    @Nonnull
    public Optional<FileStorageType> storageType() {
        return FileStorageType.optionalByCode(storageType);
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
        private HashingProperties hashingProperties;
        private String sender;
        private String storageType;

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

        public Builder withHashingProperties(HashingProperties hashingProperties) {
            this.hashingProperties = hashingProperties;
            return this;
        }

        public Builder withSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder withStorageType(String storageType) {
            this.storageType = storageType;
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(fileId,
                    fileName,
                    description,
                    hashingProperties,
                    sender,
                    storageType);
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
                Objects.equals(this.hashingProperties, that.hashingProperties) &&
                Objects.equals(this.sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileName, description, hashingProperties, sender);
    }

    @Override
    public String toString() {
        return "FileMetadata[" +
                "fileId=" + fileId + ", " +
                "fileName=" + fileName + ", " +
                "description=" + description + ", " +
                "hashingProperties=" + hashingProperties + ", " +
                "sender=" + sender + ']';
    }

}
