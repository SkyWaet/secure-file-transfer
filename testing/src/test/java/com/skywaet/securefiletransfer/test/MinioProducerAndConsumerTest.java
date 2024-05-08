package com.skywaet.securefiletransfer.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.connection.ConnectionFactory;
import com.skywaet.securefiletransfer.common.connection.ConnectionProperties;
import com.skywaet.securefiletransfer.common.identity.IdentityFactory;
import com.skywaet.securefiletransfer.common.identity.IdentityProperties;
import com.skywaet.securefiletransfer.common.model.FileStatus;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.common.model.GetFileStatusRequest;
import com.skywaet.securefiletransfer.common.signer.SignerFactory;
import com.skywaet.securefiletransfer.common.signer.SignerProperties;
import com.skywaet.securefiletransfer.consumer.FileTransferConsumer;
import com.skywaet.securefiletransfer.consumer.provider.CommonFileProviderFactory;
import com.skywaet.securefiletransfer.consumer.provider.FileProvider;
import com.skywaet.securefiletransfer.consumer.provider.minio.MinioFileProviderFactory;
import com.skywaet.securefiletransfer.producer.FileTransferProducer;
import com.skywaet.securefiletransfer.producer.model.SendFileParameters;
import com.skywaet.securefiletransfer.producer.storage.MinioFileStorage;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.hyperledger.fabric.client.Gateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class MinioProducerAndConsumerTest extends AbstractIntegrationTest {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module());
    @Container
    public DockerComposeContainer<?> minio = createContainer();

    private MinioClient minioClient;

    private DockerComposeContainer<?> createContainer() {
        try {
            return new DockerComposeContainer<>(
                    new File(getClass().getResource("/docker-compose.yaml").toURI())
            ).withExposedService("minio", 9000);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        minioClient = MinioClient.builder()
                .endpoint("http://%s:%d" .formatted(minio.getServiceHost("minio", 9000), 9000))
                .credentials("minioadmin", "minioadmin")
                .build();

        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket("org1msp")
                .build());
    }


    @Test
    public void shouldSendFile() {
        var basePath = getWorkingDirectory().resolve("test-network/organizations/peerOrganizations");
        var trustStorePath = basePath.resolve("org1.example.com/peers/peer0.org1.example.com/tls/ca.crt");
        var certPath = basePath.resolve("org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem");
        var keyPath = basePath.resolve("org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk");

        var connectionProperties = new ConnectionProperties("localhost:7051",
                "peer0.org1.example.com",
                trustStorePath.toFile()
        );

        var connection = ConnectionFactory.createGrpcConnection(connectionProperties);

        var identityProperties = new IdentityProperties(certPath, "Org1MSP");
        var identity = IdentityFactory.createIdentity(identityProperties);

        var signerProperties = new SignerProperties(keyPath);
        var signer = SignerFactory.createSigner(signerProperties);

        var gatewayBuilder = Gateway.newInstance()
                .connection(connection)
                .identity(identity)
                .signer(signer)
                .evaluateOptions(callOptions -> callOptions.withDeadlineAfter(50, TimeUnit.SECONDS))
                .endorseOptions(callOptions -> callOptions.withDeadlineAfter(50, TimeUnit.SECONDS))
                .submitOptions(callOptions -> callOptions.withDeadlineAfter(50, TimeUnit.SECONDS))
                .commitStatusOptions(callOptions -> callOptions.withDeadlineAfter(50, TimeUnit.SECONDS))
                .chaincodeEventsOptions(callOptions -> callOptions.withDeadlineAfter(5, TimeUnit.SECONDS));


        try (var gateway = gatewayBuilder.connect()) {
            var fileTransferClient = new FileTransferClient(gateway, getChannelName(), "fileTransfer", mapper);
            var consumer = createConsumer(gateway, fileTransferClient, minioClient);

            var minioFileStorage = new MinioFileStorage("org1msp", minioClient);
            var producer = new FileTransferProducer(fileTransferClient, List.of(minioFileStorage));

            Thread.ofVirtual().start(consumer::poll);
            var originalFileContent = Files.readString(getWorkingDirectory().resolve("input/test-file"));
            var fileId = producer.sendFile(new SendFileParameters("test-file", FileStorageType.MINIO, null),
                    () -> new ByteArrayInputStream(originalFileContent.getBytes(StandardCharsets.UTF_8)));

            assertThat(fileId).as("File was send correctly")
                    .asString().isNotBlank();

            var getObjectArgs = GetObjectArgs.builder()
                    .bucket("org1msp")
                    .object(fileId)
                    .build();

            try {
                var objectContent = minioClient.getObject(getObjectArgs);
                assertThat(new String(objectContent.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo(originalFileContent);
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }

            var consumedFilePath = getWorkingDirectory().resolve("output/" + fileId);

            await()
                    .atMost(Duration.of(5, ChronoUnit.SECONDS))
                    .until(() -> Files.exists(consumedFilePath));
            assertThat(Files.exists(consumedFilePath))
                    .as("File was consumed")
                    .isTrue();

            var consumedFileContent = Files.readString(consumedFilePath);
            assertThat(consumedFileContent).isEqualTo(originalFileContent);
            consumer.terminate();

            var fileStatusInTheEnd = fileTransferClient.getFileStatus(GetFileStatusRequest.builder()
                    .withFileId(fileId)
                    .build()).getFileStatus();
            assertThat(fileStatusInTheEnd.isPresent()).as("File status is present").isTrue();

            await()
                    .atMost(Duration.of(5, ChronoUnit.SECONDS))
                    .untilAsserted(() -> assertThat(fileStatusInTheEnd.orElseThrow())
                            .isEqualTo(FileStatus.CONSUMED));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private FileTransferConsumer createConsumer(Gateway gateway,
                                                FileTransferClient fileTransferClient,
                                                MinioClient minioClient) {
        var minioProviderFactory = new MinioFileProviderFactory(minioClient);
        var commonFactory = new CommonFileProviderFactory(List.of(minioProviderFactory));
        Consumer<FileProvider> contentConsumer = it -> {
            var outputPath = getWorkingDirectory().resolve("output");
            if (!Files.exists(outputPath)) {
                try {
                    Files.createDirectories(outputPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            var output = outputPath.resolve(it.getMetadata().fileId());
            try (var stream = new FileOutputStream(output.toFile())) {
                stream.write(it.getFileContent().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        return new FileTransferConsumer(gateway,
                getChannelName(),
                "fileTransfer",
                fileTransferClient,
                commonFactory,
                contentConsumer
        );
    }

}
