package com.skywaet.securefiletransfer.common.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record FileMetadata(@Nonnull String fileId,
                           @Nonnull String fileName,
                           @Nullable String description,
                           @Nonnull byte[] fileHash,
                           @Nonnull String sender) {
}
