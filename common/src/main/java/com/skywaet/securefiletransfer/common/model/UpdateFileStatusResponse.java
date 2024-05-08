package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Optional;

@JsonPropertyOrder(alphabetic = true)
public class UpdateFileStatusResponse {
    private final Boolean success;
    @Nullable
    private final String errorMessage;

    @JsonCreator
    private UpdateFileStatusResponse(@Nonnull @JsonProperty("success") Boolean success,
                                     @Nullable @JsonProperty("errorMessage") String errorMessage) {
        this.success = success;
        if (success && errorMessage != null) {
            throw new IllegalArgumentException("There should be no error message in successful response");
        }
        this.errorMessage = errorMessage;
    }

    @Nonnull
    @JsonProperty("success")
    public Boolean isSuccess() {
        return success;
    }

    @Nonnull
    @JsonProperty("errorMessage")
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

        public UpdateFileStatusResponse build() {
            return new UpdateFileStatusResponse(success, errorMessage);
        }
    }
}
