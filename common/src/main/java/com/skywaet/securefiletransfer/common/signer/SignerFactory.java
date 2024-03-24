package com.skywaet.securefiletransfer.common.signer;

import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;

import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;

import static java.util.Objects.requireNonNull;

/**
 * Factory for creating {@link org.hyperledger.fabric.client.identity.Signer}
 */
public class SignerFactory {

    /**
     * Creates new instance of {@link org.hyperledger.fabric.client.identity.Signer}
     *
     * @param properties signer properties
     * @return new instance of {@link org.hyperledger.fabric.client.identity.Signer}
     */
    @Nonnull
    public static Signer createSigner(@Nonnull SignerProperties properties) {
        requireNonNull(properties, "properties");

        try (var keyReader = Files.newBufferedReader(properties.privateKeyPath())) {
            var privateKey = Identities.readPrivateKey(keyReader);
            return Signers.newPrivateKeySigner(privateKey);
        } catch (IOException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

}
