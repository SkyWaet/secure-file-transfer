package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of sendFile method of fileTransfer contract
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Nonnull
    private final String hashingAlgorithm;

    @Nonnull
    private final String storageType;


    @JsonCreator
    private SendFileRequest(@Nonnull @NotBlank @JsonProperty("fileId") String fileId,
                            @Nonnull @NotBlank @JsonProperty("fileName") String fileName,
                            @Nullable @JsonProperty("description") String description,
                            @Nonnull @JsonProperty("hashingAlgorithm") String hashingAlgorithm,
                            @Nonnull @JsonProperty("storageType") String storageType) {
        requireNonNull(fileId, "fileId");
        requireNonNull(fileName, "fileName");
        requireNonNull(hashingAlgorithm, "hashingAlgorithm");
        requireNonNull(storageType, "storageType");

        this.fileId = fileId;
        this.fileName = fileName;
        this.hashingAlgorithm = hashingAlgorithm;
        this.description = description;
        this.storageType = storageType;

    }

    @Nonnull
    @JsonProperty("fileId")
    public String getFileId() {
        return fileId;
    }

    @Nonnull
    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @Nullable
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Nonnull
    @JsonProperty("hashingAlgorithm")
    public String getHashingAlgorithm() {
        return hashingAlgorithm;
    }

    @Nonnull
    @JsonProperty("storageType")
    public String getStorageTypeRaw() {
        return storageType;
    }

    @Nonnull
    public Optional<FileStorageType> getStorageType() {
        return FileStorageType.optionalByCode(storageType);
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private Builder() {
        }

        private String fileId;
        private String fileName;
        private String description;
        private String hashingAlgorithm;
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

        public Builder withHashingAlgorithm(String hashingAlgorithm) {
            this.hashingAlgorithm = hashingAlgorithm;
            return this;
        }

        public Builder withStorageType(String storageType) {
            this.storageType = storageType;
            return this;
        }


        public SendFileRequest build() {
            return new SendFileRequest(fileId, fileName, description, hashingAlgorithm, storageType);
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
