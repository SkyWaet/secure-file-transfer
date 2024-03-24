package com.skywaet.securefiletransfer.common.connection;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import jakarta.annotation.Nonnull;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Factory for gRPC connections creation
 */
public class ConnectionFactory {

    /**
     * Creates new connection using properties
     *
     * @param properties properties for new connection
     * @return new gRPC connection
     */
    @Nonnull
    public static ManagedChannel createGrpcConnection(@Nonnull ConnectionProperties properties) {
        requireNonNull(properties, "properties");

        try {
            ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                    .trustManager(properties.trustManager())
                    .build();
            var channelBuilder = Grpc.newChannelBuilder(properties.endpoint(), credentials);

            if (properties.overrideAuthority() != null) {
                channelBuilder.overrideAuthority(properties.overrideAuthority());
            }

            return channelBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
