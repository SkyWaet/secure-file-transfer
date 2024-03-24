package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Optional;

public class CheckFileResponse {
    private final boolean success;
    @Nullable
    private final String errorMessage;

    @JsonCreator
    private CheckFileResponse(@JsonProperty boolean success,
                              @JsonProperty @Nullable String errorMessage) {
        this.success = success;
        if (success && errorMessage != null) {
            throw new IllegalArgumentException("There should be no error message in successful response");
        }
        this.errorMessage = errorMessage;
    }

    @JsonProperty
    public boolean isSuccess() {
        return success;
    }

    @Nonnull
    @JsonProperty
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private boolean success;
        private String errorMessage;

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public CheckFileResponse build() {
            return new CheckFileResponse(success, errorMessage);
        }
    }
}
