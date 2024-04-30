import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.skywaet.securefiletransfer.common.client.FileTransferClient;
import com.skywaet.securefiletransfer.common.connection.ConnectionFactory;
import com.skywaet.securefiletransfer.common.connection.ConnectionProperties;
import com.skywaet.securefiletransfer.common.identity.IdentityFactory;
import com.skywaet.securefiletransfer.common.identity.IdentityProperties;
import com.skywaet.securefiletransfer.common.model.FileStorageType;
import com.skywaet.securefiletransfer.common.signer.SignerFactory;
import com.skywaet.securefiletransfer.common.signer.SignerProperties;
import com.skywaet.securefiletransfer.consumer.FileTransferConsumer;
import com.skywaet.securefiletransfer.consumer.provider.CommonFileProviderFactory;
import com.skywaet.securefiletransfer.consumer.provider.FileProvider;
import com.skywaet.securefiletransfer.consumer.provider.minio.MinioFileProviderFactory;
import com.skywaet.securefiletransfer.producer.FileTransferProducer;
import com.skywaet.securefiletransfer.producer.model.SendFileParameters;
import com.skywaet.securefiletransfer.producer.storage.MinioFileStorage;
import io.minio.MinioClient;
import org.hyperledger.fabric.client.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String BASE_PATH = "\\\\wsl.localhost\\Ubuntu\\home\\skywaet\\studying\\diploma_mag\\fabric\\test-network\\organizations\\peerOrganizations";
    private static final String TRUST_STORE_PATH = BASE_PATH + "/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt";
    private static final String CERTIFICATE_PATH = BASE_PATH + "/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
    private static final String KEY_PATH = BASE_PATH + "/org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk";

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module());

    public static void main(String[] args) {
        var connectionProperties = new ConnectionProperties("localhost:7051",
                "peer0.org1.example.com",
                new File(TRUST_STORE_PATH)
        );

        var connection = ConnectionFactory.createGrpcConnection(connectionProperties);

        var identityProperties = new IdentityProperties(Path.of(CERTIFICATE_PATH), "Org1MSP");
        var identity = IdentityFactory.createIdentity(identityProperties);

        var signerProperties = new SignerProperties(Path.of(KEY_PATH));
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

        var minioClient =
                MinioClient.builder()
                        .endpoint("http://127.0.0.1:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();


        try (var gateway = gatewayBuilder.connect()) {
            var fileTransferClient = new FileTransferClient(gateway, "skywaet-channel", "fileTransfer", mapper);
            var consumer = createConsumer(gateway, fileTransferClient, minioClient);

            var minioFileStorage = new MinioFileStorage("org1msp", minioClient);
            var producer = new FileTransferProducer(fileTransferClient, List.of(minioFileStorage));

            Thread.ofVirtual().start(() -> {
                try {
                    consumer.poll();
                } catch (Exception e) {
                    log.error("Error", e);
                }
            });

            for (int i = 0; i < 2; i++) {
                var file = producer.sendFile(new SendFileParameters("test-file", FileStorageType.MINIO, null),
                        () -> Main.class.getResourceAsStream("/input/test-file"));
                log.info("Sending file with id {}", file);
            }


            while (true) {
            }
        }
    }

    private static FileTransferConsumer createConsumer(Gateway gateway,
                                                       FileTransferClient fileTransferClient,
                                                       MinioClient minioClient) {
        var minioProviderFactory = new MinioFileProviderFactory(minioClient);
        var commonFactory = new CommonFileProviderFactory(List.of(minioProviderFactory));
        Consumer<FileProvider> contentConsumer = it -> {
            try {
                var output = Path.of(Main.class.getResource("/output").toURI()).resolve(it.getMetadata().fileId());
                try (var stream = new FileOutputStream(output.toFile())) {
                    stream.write(it.getFileContent().readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        };
        return new FileTransferConsumer(gateway,
                "skywaet-channel",
                "fileTransfer",
                fileTransferClient,
                commonFactory,
                contentConsumer
        );
    }

}
