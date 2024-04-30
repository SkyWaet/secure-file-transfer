package com.skywaet.securefiletransfer.test;

import com.skywaet.securefiletransfer.test.util.Bash;
import com.skywaet.securefiletransfer.test.util.FabricState;
import com.skywaet.securefiletransfer.test.util.PathUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class AbstractIntegrationTest {

    private String channelName;

    @BeforeAll
    public static void startNetwork() {
        FabricState.getState().start();
    }

    @AfterAll
    public static void shutdown() {
        FabricState.getState().stop();
    }

    @BeforeEach
    public void prepareChannel() {
        channelName = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        try {
            var createChannel = Bash.newBuilder()
                    .cmd(Path.of(getClass().getResource("/test-network/network.sh").toURI()))
                    .cmdargs("createChannel", "-c", channelName, "-bft")
                    .build();

            createChannel.run();

            var workingDirectory = PathUtils.relativize(Path.of(getClass().getResource("/deploy").toURI()).getParent());
            var setenvPath = PathUtils.relativize((Path.of(getClass().getResource("/deploy/setEnv.sh").toURI())));
            Bash.newBuilder()
                    .cmd(Path.of(getClass().getResource("/deploy/uploadChaincode.sh").toURI()))
                    .cmdargs(setenvPath, workingDirectory)
                    .build()
                    .run();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void test() {
        Assertions.assertEquals(1, 2);
    }

}
