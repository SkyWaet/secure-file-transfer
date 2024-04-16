import com.skywaet.securefiletransfer.common.connection.ConnectionFactory;
import com.skywaet.securefiletransfer.common.connection.ConnectionProperties;
import com.skywaet.securefiletransfer.common.identity.IdentityFactory;
import com.skywaet.securefiletransfer.common.identity.IdentityProperties;
import com.skywaet.securefiletransfer.common.signer.SignerFactory;
import com.skywaet.securefiletransfer.common.signer.SignerProperties;
import com.skywaet.securefiletransfer.consumer.FileTransferConsumer;
import org.hyperledger.fabric.client.Gateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String BASE_PATH = "\\\\wsl.localhost\\Ubuntu\\home\\skywaet\\studying\\diploma_mag\\fabric\\test-network\\organizations\\peerOrganizations";
    private static final String TRUST_STORE_PATH = BASE_PATH + "/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt";
    private static final String CERTIFICATE_PATH = BASE_PATH + "/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
    private static final String KEY_PATH = BASE_PATH + "/org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk";

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
                .chaincodeEventsOptions(callOptions -> callOptions.withDeadlineAfter(50, TimeUnit.SECONDS));


        try (var gateway = gatewayBuilder.connect();
             var executor = Executors.newScheduledThreadPool(10)) {

            var consumer = new FileTransferConsumer(gateway, "skywaet-channel", "fileTransfer", "topic");
            var promise = executor.scheduleAtFixedRate(() -> {
                log.info("Pulling new events");
                var events = consumer.pullEvents();
                events.forEach(it -> {
                    try {
                        var result = it.get();
                        log.info(result.fileName());
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("Error while pulling events", e);
                        throw new RuntimeException(e);
                    }
                });
            }, 0, 10, TimeUnit.SECONDS);
            executor.schedule(() -> {
                promise.cancel(false);
            }, 60 * 60, TimeUnit.SECONDS);
            while (true) {
            }
        }
    }

}
