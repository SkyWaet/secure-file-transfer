package com.skywaet.securefiletransfer.common.identity;

import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.X509Identity;

import java.io.IOException;
import java.nio.file.Files;
import java.security.cert.CertificateException;

import static java.util.Objects.requireNonNull;

/**
 * Factory for creating {@link org.hyperledger.fabric.client.identity.Identity}
 */
public class IdentityFactory {
    /**
     * Creates new {@link org.hyperledger.fabric.client.identity.Identity} using provided properties
     *
     * @param identityProperties properties of identity creation
     * @return new {@link org.hyperledger.fabric.client.identity.X509Identity}
     */
    @Nonnull
    public static Identity createIdentity(@Nonnull IdentityProperties identityProperties) {
        requireNonNull(identityProperties, "identityProperties");

        try (var certificateReader = Files.newBufferedReader(identityProperties.certificatePath())) {
            var certificate = Identities.readX509Certificate(certificateReader);
            return new X509Identity(identityProperties.mspId(), certificate);
        } catch (IOException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
