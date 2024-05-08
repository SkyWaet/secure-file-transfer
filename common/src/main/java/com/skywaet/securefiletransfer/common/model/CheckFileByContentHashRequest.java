package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckFileByContentHashRequest {
    @Nonnull
    private final byte[] hash;

    @JsonCreator
    private CheckFileByContentHashRequest(@Nonnull @JsonProperty("hash") byte[] hash) {
        this.hash = Objects.requireNonNull(hash);
    }

    @Nonnull
    @JsonProperty("hash")
    public byte[] getHash() {
        return hash;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private byte[] hash;

        public Builder withFileId(byte[] hash) {
            this.hash = hash;
            return this;
        }

        public CheckFileByContentHashRequest build() {
            return new CheckFileByContentHashRequest(hash);
        }
    }
}
