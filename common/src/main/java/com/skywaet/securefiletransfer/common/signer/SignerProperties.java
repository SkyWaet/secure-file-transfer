package com.skywaet.securefiletransfer.common.signer;

import jakarta.annotation.Nonnull;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link org.hyperledger.fabric.client.identity.Signer} creation
 *
 * @param privateKeyPath path to application private key
 */
public record SignerProperties(@Nonnull Path privateKeyPath) {
    public SignerProperties {
        requireNonNull(privateKeyPath, "privateKeyPath");
    }
}
