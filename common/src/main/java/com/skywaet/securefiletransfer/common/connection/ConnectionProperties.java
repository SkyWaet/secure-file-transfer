package com.skywaet.securefiletransfer.common.connection;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.File;

import static java.util.Objects.requireNonNull;

/**
 * Settings used to create gRPC connection to Fabric
 *
 * @param endpoint          address of Fabric gateway peer
 * @param overrideAuthority parameter of {@link io.grpc.ManagedChannelBuilder#overrideAuthority(String)}
 * @param trustManager      trust manager used to verify server's identity
 */
public record ConnectionProperties(@Nonnull String endpoint,
                                   @Nullable String overrideAuthority,
                                   @Nonnull File trustManager) {

    public ConnectionProperties {
        requireNonNull(endpoint, "endpoint");
        requireNonNull(trustManager, "trustManager");
    }
}
