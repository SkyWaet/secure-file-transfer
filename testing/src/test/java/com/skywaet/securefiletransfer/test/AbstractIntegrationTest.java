package com.skywaet.securefiletransfer.test;

import com.skywaet.securefiletransfer.test.util.Bash;
import com.skywaet.securefiletransfer.test.util.FabricState;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractIntegrationTest {

    private static final Pattern CHAINCODE_IDENTIFIER = Pattern.compile("fileTransfer_\\d+:\\w+");
    private static Path workingDirectory;

    private String channelName;

    @BeforeAll
    public static void startNetwork() {
        try (var files = Files.list(Path.of("src/test/resources"))) {
            workingDirectory = Files.createTempDirectory("int-test");
            workingDirectory.toFile().deleteOnExit();

            files.forEach(it -> {
                try {
                    FileUtils.copyDirectory(it.toFile(), workingDirectory.resolve(it.getFileName()).toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            var chaincode = Path.of(AbstractIntegrationTest.class.getResource("/chaincode").toURI());
            FileUtils.copyDirectory(chaincode.toFile(), workingDirectory.resolve(chaincode.getFileName()).toFile());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        FabricState.getState().start(workingDirectory);
    }

    @AfterAll
    public static void shutdown() {
        FabricState.getState().stop(workingDirectory);
    }

    @BeforeEach
    public void prepareChannel() {
        channelName = RandomStringUtils.randomAlphabetic(10).toLowerCase();

        var createChannel = Bash.newBuilder()
                .cmd("./test-network/network.sh")
                .cmdargs("createChannel", "-c", channelName, "-bft")
                .workingDirectory(workingDirectory)
                .build();

        createChannel.run();

        var installChaincode = Bash.newBuilder()
                .cmd("./deploy/uploadChaincode.sh")
                .cmdargs("deploy/setEnv.sh")
                .workingDirectory(workingDirectory)
                .build()
                .run();

        var chaincodeIdentifier = installChaincode.stdout.stream()
                .filter(line -> line.contains("Chaincode code package identifier:"))
                .map(CHAINCODE_IDENTIFIER::matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Chaincode identifier not found"));

        Bash.newBuilder()
                .cmd("./deploy/approveChaincode.sh")
                .cmdargs("deploy/setEnv.sh", "1", chaincodeIdentifier, channelName)
                .workingDirectory(workingDirectory)
                .build()
                .run();

    }

    @Test
    public void test() {
        Assertions.assertEquals(1, 2);
    }

}
