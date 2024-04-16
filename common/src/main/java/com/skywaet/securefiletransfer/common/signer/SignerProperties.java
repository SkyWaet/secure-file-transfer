package com.skywaet.securefiletransfer.common.signer;

import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.identity.Signer;

import java.nio.file.Path;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link Signer} creation
 */
public final class SignerProperties {
    @Nonnull
    private final Path privateKeyPath;

    public SignerProperties(@Nonnull Path privateKeyPath) {
        requireNonNull(privateKeyPath, "privateKeyPath");
        this.privateKeyPath = privateKeyPath;
    }

    @Nonnull
    public Path privateKeyPath() {
        return privateKeyPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SignerProperties) obj;
        return Objects.equals(this.privateKeyPath, that.privateKeyPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privateKeyPath);
    }

    @Override
    public String toString() {
        return "SignerProperties[" +
                "privateKeyPath=" + privateKeyPath + ']';
    }

}
