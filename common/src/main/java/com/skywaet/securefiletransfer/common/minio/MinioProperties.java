package com.skywaet.securefiletransfer.common.minio;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Configuration properties with prefix "minio"
 */
public final class MinioProperties {
    private static final String PREFIX = "minio";
    @Nonnull
    private final @NotBlank String endpoint;
    private final int port;
    @Nonnull
    private final @NotBlank String accessKeyId;
    @Nonnull
    private final @NotBlank String secretAccessKeyId;
    private final boolean useSsl;

    private MinioProperties(@Nonnull @NotBlank String endpoint,
                            @Nonnull @NotBlank String port,
                            @Nonnull @NotBlank String accessKeyId,
                            @Nonnull @NotBlank String secretAccessKeyId,
                            @Nullable @NotBlank String useSsl) {
        requireNonNull(endpoint, "getEndpoint");
        requireNonNull(port, "port");
        requireNonNull(accessKeyId, "getAccessKeyId");
        requireNonNull(secretAccessKeyId, "getSecretAccessKeyId");

        this.endpoint = endpoint;
        this.port = Integer.parseInt(port);
        this.accessKeyId = accessKeyId;
        this.secretAccessKeyId = secretAccessKeyId;
        this.useSsl = Boolean.parseBoolean(useSsl);
    }

    @Nonnull
    public @NotBlank String getEndpoint() {
        return endpoint;
    }

    public int getPort() {
        return port;
    }

    @Nonnull
    public @NotBlank String getAccessKeyId() {
        return accessKeyId;
    }

    @Nonnull
    public @NotBlank String getSecretAccessKeyId() {
        return secretAccessKeyId;
    }

    public boolean useSsl() {
        return useSsl;
    }

    public static MinioProperties load() {
        var properties = new Properties();
        try {
            properties.load(MinioProperties.class.getResourceAsStream("/minio.properties"));
            return new MinioProperties(
                    properties.getProperty(String.format("%s.endpoint", PREFIX)),
                    properties.getProperty(String.format("%s.port", PREFIX)),
                    properties.getProperty(String.format("%s.accessKeyId", PREFIX)),
                    properties.getProperty(String.format("%s.secretAccessKeyId", PREFIX)),
                    properties.getProperty(String.format("%s.useSsl", PREFIX), "true")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MinioProperties) obj;
        return Objects.equals(this.endpoint, that.endpoint) &&
                Objects.equals(this.accessKeyId, that.accessKeyId) &&
                Objects.equals(this.secretAccessKeyId, that.secretAccessKeyId) &&
                Objects.equals(this.useSsl, that.useSsl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, accessKeyId, secretAccessKeyId, useSsl);
    }

    @Override
    public String toString() {
        return "MinioProperties[" +
                "getEndpoint=" + endpoint + ", " +
                "getAccessKeyId=" + accessKeyId + ", " +
                "getSecretAccessKeyId=" + secretAccessKeyId + ", " +
                "useSsl=" + useSsl + ']';
    }

}
