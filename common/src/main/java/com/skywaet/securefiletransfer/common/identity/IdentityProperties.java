package com.skywaet.securefiletransfer.common.identity;

import jakarta.annotation.Nonnull;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of {@link org.hyperledger.fabric.client.identity.Identity} creation
 *
 * @param certificatePath path to client certificate
 * @param mspId           ID of application owner MSP
 */
public record IdentityProperties(@Nonnull Path certificatePath,
                                 @Nonnull String mspId) {

    public IdentityProperties {
        requireNonNull(certificatePath, "certificatePath");
        requireNonNull(mspId, "mspId");
    }
}
