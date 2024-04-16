package com.skywaet.securefiletransfer.common.identity;

import jakarta.annotation.Nonnull;
import org.hyperledger.fabric.client.identity.Identity;

import java.nio.file.Path;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of {@link Identity} creation
 */
public final class IdentityProperties {
    @Nonnull
    private final Path certificatePath;
    @Nonnull
    private final String mspId;


    public IdentityProperties(@Nonnull Path certificatePath, @Nonnull String mspId) {
        requireNonNull(certificatePath, "certificatePath");
        requireNonNull(mspId, "mspId");
        this.certificatePath = certificatePath;
        this.mspId = mspId;
    }

    @Nonnull
    public Path certificatePath() {
        return certificatePath;
    }

    @Nonnull
    public String mspId() {
        return mspId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IdentityProperties) obj;
        return Objects.equals(this.certificatePath, that.certificatePath) &&
                Objects.equals(this.mspId, that.mspId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificatePath, mspId);
    }

    @Override
    public String toString() {
        return "IdentityProperties[" +
                "certificatePath=" + certificatePath + ", " +
                "mspId=" + mspId + ']';
    }

}
