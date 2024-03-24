package com.skywaet.securefiletransfer.common.model;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of CheckFile method
 */
public class CheckFileRequest {
    /**
     * ID of the file to check
     */
    @Nonnull
    @NotBlank
    private final String fileId;
    /**
     * hash to compare with stored in blockchain
     */
    @Nonnull
    @NotEmpty
    private final byte[] fileHash;


    private CheckFileRequest(@Nonnull @NotBlank String fileId,
                             @Nonnull @NotEmpty byte[] fileHash) {
        this.fileId = requireNonNull(fileId, "fileId");
        this.fileHash = requireNonNull(fileHash, "fileHash");
    }

    @Nonnull
    public String getFileId() {
        return fileId;
    }

    @Nonnull
    public byte[] getFileHash() {
        return fileHash;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        private String fileId;
        private byte[] fileHash;


        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder withFileHash(byte[] fileHash) {
            this.fileHash = fileHash;
            return this;
        }

        public CheckFileRequest build() {
            return new CheckFileRequest(fileId,
                    fileHash);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckFileRequest that = (CheckFileRequest) o;
        return Objects.equals(fileId, that.fileId) && Arrays.equals(fileHash, that.fileHash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileId);
        result = 31 * result + Arrays.hashCode(fileHash);
        return result;
    }

    @Override
    public String toString() {
        return "CheckFileRequest{" +
                "fileId='" + fileId + '\'' +
                ", fileHash=" + Arrays.toString(fileHash) +
                '}';
    }
}
