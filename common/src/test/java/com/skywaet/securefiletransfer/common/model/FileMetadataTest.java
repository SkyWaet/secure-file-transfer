package com.skywaet.securefiletransfer.common.model;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FileMetadataTest {

    @Test
    public void shouldCreateCopyFromPrototype() {
        var prototype = FileMetadata.builder()
                .withFileId("abc")
                .withFileName("fileName")
                .withSender("sender")
                .withStatus(FileStatus.PENDING)
                .withStorageType(FileStorageType.MINIO.getCode())
                .withVerificationProperties(VerificationProperties.builder()
                        .withAlgorithm("SHA-512")
                        .withHash("hello" .getBytes(StandardCharsets.UTF_8))
                        .build())
                .build();

        var modified = FileMetadata.builder()
                .fromPrototype(prototype)
                .withStatus(FileStatus.CONSUMED)
                .build();

        assertThat(modified)
                .usingRecursiveComparison()
                .ignoringFields("status")
                .isEqualTo(prototype);

        assertThat(modified.status())
                .asInstanceOf(InstanceOfAssertFactories.optional(FileStatus.class))
                .contains(FileStatus.CONSUMED);
    }

}