package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Response of fileTransfer smart contract sendFile method
 */
public class SendFileResponse {
    /**
     * ID of the sent file
     */
    @Nonnull
    private final String fileId;

    @JsonCreator
    private SendFileResponse(@Nonnull @JsonProperty String fileId) {
        this.fileId = requireNonNull(fileId, "fileId");
    }

    @Nonnull
    @JsonProperty
    public String getFileId() {
        return fileId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private String fileId;

        public Builder withFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public SendFileResponse build() {
            return new SendFileResponse(fileId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendFileResponse that = (SendFileResponse) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }

    @Override
    public String toString() {
        return "SendFileResponse{" +
                "fileId='" + fileId + '\'' +
                '}';
    }
}
