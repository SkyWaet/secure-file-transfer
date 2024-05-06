package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationProperties {

    @Nonnull
    private final String hashingAlgorithm;

    @Nonnull
    private final byte[] hash;

    @Nonnull
    private final Boolean isSigned;

    @JsonCreator
    private VerificationProperties(@Nonnull @JsonProperty("hashingAlgorithm") String hashingAlgorithm,
                                   @Nonnull @JsonProperty("hash") byte[] hash,
                                   @Nullable @JsonProperty("isSigned") Boolean isSigned) {
        this.hashingAlgorithm = Objects.requireNonNull(hashingAlgorithm);
        this.hash = Objects.requireNonNull(hash);
        this.isSigned = Boolean.TRUE.equals(isSigned);
    }

    @Nonnull
    @JsonProperty
    public String getHashingAlgorithm() {
        return hashingAlgorithm;
    }

    @Nonnull
    @JsonProperty
    public byte[] getHash() {
        return hash;
    }

    @Nonnull
    @JsonProperty
    public Boolean getSigned() {
        return isSigned;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {
        }

        private String algorithm;
        private byte[] hash;
        private boolean isSigned;

        public Builder withAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder withHash(byte[] hash) {
            this.hash = hash;
            return this;
        }

        public Builder withIsSigned(Boolean isSigned) {
            this.isSigned = isSigned;
            return this;
        }


        public VerificationProperties build() {
            return new VerificationProperties(algorithm, hash, isSigned);
        }
    }
}
