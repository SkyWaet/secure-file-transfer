package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteFileRequest {
    @Nonnull
    private final String fileId;

    @JsonCreator
    private DeleteFileRequest(@Nonnull @JsonProperty("fileId") String fileId) {
        this.fileId = Objects.requireNonNull(fileId);
    }

    @Nonnull
    @JsonProperty("fileId")
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

        public DeleteFileRequest build() {
            return new DeleteFileRequest(fileId);
        }
    }
}
