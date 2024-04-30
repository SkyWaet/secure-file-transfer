package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HashingProperties {

    @Nonnull
    private final String algorithm;

    @Nonnull
    private final byte[] hash;

    @JsonCreator
    private HashingProperties(@Nonnull @JsonProperty("algorithm") String algorithm,
                              @Nonnull @JsonProperty("hash") byte[] hash) {
        this.algorithm = Objects.requireNonNull(algorithm);
        this.hash = Objects.requireNonNull(hash);
    }

    @Nonnull
    @JsonProperty
    public String getAlgorithm() {
        return algorithm;
    }

    @Nonnull
    @JsonProperty
    public byte[] getHash() {
        return hash;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        private String algorithm;
        private byte[] hash;

        public Builder withAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder withHash(byte[] hash) {
            this.hash = hash;
            return this;
        }


        public HashingProperties build() {
            return new HashingProperties(algorithm, hash);
        }
    }
}
