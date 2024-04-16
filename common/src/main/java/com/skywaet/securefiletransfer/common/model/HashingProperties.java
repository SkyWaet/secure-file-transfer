package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Optional;

@JsonPropertyOrder(alphabetic = true)
public class HashingProperties {
    @JsonProperty
    private final boolean enabled;
    @JsonProperty
    @Nullable
    private final String algorithm;
    @JsonProperty
    @Nullable
    private final byte[] hash;

    @JsonCreator
    private HashingProperties(@Nullable @JsonProperty Boolean enabled,
                              @Nullable @JsonProperty String algorithm,
                              @Nullable @JsonProperty byte[] hash) {
        this.enabled = enabled == null || enabled;
        if (this.enabled) {
            if (algorithm == null || algorithm.isBlank()) {
                throw new RuntimeException("Algorithm should be provided");
            }
            if (hash == null || hash.length == 0) {
                throw new RuntimeException("Hash should be provided");
            }
        }
        this.algorithm = algorithm;
        this.hash = hash;
    }

    @JsonProperty
    public boolean isEnabled() {
        return enabled;
    }

    @Nonnull
    @JsonProperty
    public Optional<String> getAlgorithm() {
        return Optional.ofNullable(algorithm);
    }

    @Nonnull
    @JsonProperty
    public Optional<byte[]> getHash() {
        return Optional.ofNullable(hash);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        private Boolean enabled;
        private String algorithm;
        private byte[] hash;

        public Builder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder withHash(byte[] hash) {
            this.hash = hash;
            return this;
        }


        public HashingProperties build() {
            return new HashingProperties(enabled, algorithm, hash);
        }
    }
}
