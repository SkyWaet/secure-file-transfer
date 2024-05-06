package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;
import java.util.Optional;

/**
 * Metadata of the file, used to store data in blockchain
 */
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@DataType
public final class FileMetadata {
    @Nonnull
    @JsonProperty
    @Property
    private final @NotBlank String fileId;
    @Nullable
    @JsonProperty
    @Property
    private final @NotBlank String fileName;
    @Nullable
    @JsonProperty
    @Property
    private final String description;
    @Nonnull
    @JsonProperty
    @Property
    private final VerificationProperties verificationProperties;
    @Nonnull
    @JsonProperty
    @Property
    private final @NotBlank String sender;
    @Nonnull
    @JsonProperty
    @Property
    private final @NotBlank String storageType;
    @Nonnull
    @JsonProperty
    @Property
    private final @NotBlank String status;

    @JsonCreator
    private FileMetadata(@Nonnull @NotBlank @JsonProperty("fileId") String fileId,
                         @Nullable @JsonProperty("fileName") String fileName,
                         @Nullable @JsonProperty("description") String description,
                         @Nonnull @JsonProperty("verificationProperties") VerificationProperties verificationProperties,
                         @Nonnull @NotBlank @JsonProperty("sender") String sender,
                         @Nonnull @NotBlank @JsonProperty("storageType") String storageType,
                         @Nonnull @NotBlank @JsonProperty("status") String status) {
        this.fileId = Objects.requireNonNull(fileId);
        this.fileName = fileName;
        this.description = description;
        this.verificationProperties = Objects.requireNonNull(verificationProperties);
        this.sender = Objects.requireNonNull(sender);
        this.storageType = Objects.requireNonNull(storageType);
        this.status = Objects.requireNonNull(status);
    }

    @Nonnull
    @JsonProperty("fileId")
    public @NotBlank String fileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty("fileName")
    public Optional<String> fileName() {
        return Optional.ofNullable(fileName);
    }

    @Nonnull
    @JsonProperty("description")
    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    @Nonnull
    @JsonProperty("verificationProperties")
    public VerificationProperties verificationProperties() {
        return verificationProperties;
    }

    @Nonnull
    @JsonProperty("sender")
    public @NotBlank String sender() {
        return sender;
    }

    @Nonnull
    @JsonProperty("storageType")
    public @NotBlank String storageTypeRaw() {
        return storageType;
    }

    @Nonnull
    public Optional<FileStorageType> storageType() {
        return FileStorageType.optionalByCode(storageType);
    }

    @Nonnull
    @JsonProperty("status")
    public @NotBlank String statusRaw() {
        return status;
    }

    @Nonnull
    public Optional<FileStatus> status() {
        return FileStatus.optionalByCode(status);
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
        private VerificationProperties verificationProperties;
        private String sender;
        private String storageType;
        private String status;

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

        public Builder withVerificationProperties(VerificationProperties verificationProperties) {
            this.verificationProperties = verificationProperties;
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

        public Builder withStatus(FileStatus status) {
            this.status = status.getCode();
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(fileId,
                    fileName,
                    description,
                    verificationProperties,
                    sender,
                    storageType,
                    status);
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
                Objects.equals(this.verificationProperties, that.verificationProperties) &&
                Objects.equals(this.sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, fileName, description, verificationProperties, sender);
    }

    @Override
    public String toString() {
        return "FileMetadata[" +
                "fileId=" + fileId + ", " +
                "fileName=" + fileName + ", " +
                "description=" + description + ", " +
                "verificationProperties=" + verificationProperties + ", " +
                "sender=" + sender + ']';
    }

}
