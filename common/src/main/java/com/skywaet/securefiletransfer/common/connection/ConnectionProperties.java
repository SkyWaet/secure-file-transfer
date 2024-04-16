package com.skywaet.securefiletransfer.common.connection;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.File;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Settings used to create gRPC connection to Fabric
 */
public final class ConnectionProperties {
    @Nonnull
    private final String endpoint;
    @Nullable
    private final String overrideAuthority;
    @Nonnull
    private final File trustManager;


    public ConnectionProperties(@Nonnull String endpoint, @Nullable String overrideAuthority, @Nonnull File trustManager) {
        requireNonNull(endpoint, "getEndpoint");
        requireNonNull(trustManager, "trustManager");
        this.endpoint = endpoint;
        this.overrideAuthority = overrideAuthority;
        this.trustManager = trustManager;
    }

    @Nonnull
    public String endpoint() {
        return endpoint;
    }

    @Nullable
    public String overrideAuthority() {
        return overrideAuthority;
    }

    @Nonnull
    public File trustManager() {
        return trustManager;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ConnectionProperties) obj;
        return Objects.equals(this.endpoint, that.endpoint) &&
                Objects.equals(this.overrideAuthority, that.overrideAuthority) &&
                Objects.equals(this.trustManager, that.trustManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, overrideAuthority, trustManager);
    }

    @Override
    public String toString() {
        return "ConnectionProperties[" +
                "endpoint=" + endpoint + ", " +
                "overrideAuthority=" + overrideAuthority + ", " +
                "trustManager=" + trustManager + ']';
    }

}
