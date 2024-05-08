package com.skywaet.securefiletransfer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckFileByContentHashResponse {
    @Nonnull
    private final String status;

    @Nullable
    private final List<String> possibleFileIds;

    @JsonCreator
    private CheckFileByContentHashResponse(@Nonnull @JsonProperty("status") String status,
                                           @Nullable @JsonProperty("possibleFileIds") List<String> fileId) {
        this.status = requireNonNull(status);
        this.possibleFileIds = fileId;
    }

    @Nonnull
    @JsonProperty("status")
    public String getStatusRaw() {
        return status;
    }

    @Nonnull
    public Optional<Status> getStatus() {
        return Status.optionalByCode(status);
    }

    @Nonnull
    @JsonProperty("possibleFileIds")
    public List<String> getPossibleFileIds() {
        return possibleFileIds == null ? List.of() : possibleFileIds;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        private Status status;
        private List<String> possibleFileIds;

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withPossibleFileIds(List<String> possibleFileIds) {
            this.possibleFileIds = possibleFileIds;
            return this;
        }

        public CheckFileByContentHashResponse build() {
            return new CheckFileByContentHashResponse(status.getCode(), possibleFileIds);
        }
    }


    public enum Status {
        SUCCESS("success"),
        NOT_FOUND("not_found");

        private final String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        @Nonnull
        public static Optional<Status> optionalByCode(@Nonnull String code) {
            requireNonNull(code, "code");

            return Arrays.stream(Status.values())
                    .filter(it -> it.code.equals(code))
                    .findAny();
        }
    }
}
