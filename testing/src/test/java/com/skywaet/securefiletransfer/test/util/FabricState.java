/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package com.skywaet.securefiletransfer.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public final class FabricState {

    private static FabricState state;

    private static final Map<String, Boolean> channelStarted = new HashMap<>();

    // sempaphore to protect access
    private static final Semaphore flag = new Semaphore(1);

    public static FabricState getState() {
        if (state == null) {
            state = new FabricState();
        }

        return state;
    }

    private boolean started = false;

    public synchronized void start() {

        if (!this.started) {
            Bash.BashBuilder bashBuilder = null;
            try {
                bashBuilder = new Bash.BashBuilder()
                        .cmd(Path.of(getClass().getResource("/test-network/network.sh")
                                .toURI()))
                        .cmdargs("up", "-bft");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            bashBuilder.build().run();
            this.started = true;
        } else {
            System.out.println("Fabric already started....");
        }
    }

    public synchronized void stop() {

        if (this.started) {
            Bash.BashBuilder bashBuilder = null;
            try {
                bashBuilder = new Bash.BashBuilder()
                        .cmd(Path.of(getClass().getResource("/test-network/network.sh").toURI()))
                        .cmdargs("down");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            bashBuilder.build().run();
            this.started = true;
        } else {
            System.out.println("Fabric not running....");
        }
    }

    public Map<String, String> commonEnv() {
        try {

            Map<String, String> env = new HashMap<>();
            var binariesPath = PathUtils.relativize(Path.of(getClass().getResource("/bin").toURI()));
            var configPath = PathUtils.relativize(Path.of(getClass().getResource("/config").toURI()));

            env.put("PATH", binariesPath + ":$PATH");
            env.put("FABRIC_CFG_PATH", configPath);
            System.out.println(env);
            return env;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> orgEnv(String org) {
        try {

            Map<String, String> env = new HashMap<>();
            var mspId = org + "MSP";
            var domain = org + ".example.com";
            var networkPath = Path.of(getClass().getResource("/test-network").toURI());
            var binariesPath = Path.of(getClass().getResource("/bin").toURI());
            var configPath = Path.of(getClass().getResource("/config").toURI());

            env.put("PATH", binariesPath + ":$PATH");
            env.put("FABRIC_CFG_PATH", configPath.toString());

            env.put("CORE_PEER_TLS_ENABLED", "true");
            env.put("CORE_PEER_LOCALMSPID", mspId);
            env.put("CORE_PEER_TLS_ROOTCERT_FILE",
                    networkPath.resolve("organizations/peerOrganizations/%s/peers/peer0.%s/tls/ca.crt".formatted(domain, domain)).toString());
            env.put("CORE_PEER_MSPCONFIGPATH", networkPath.resolve(
                    "organizations/peerOrganizations/%s/users/Admin%s/msp".formatted(domain, domain)).toString());
            env.put("CORE_PEER_ADDRESS", switch (org) {
                case "org1" -> "localhost:7051";
                case "org2" -> "localhost:9051";
                default -> throw new IllegalArgumentException("Unknown org " + org);
            });
            System.out.println(env);
            return env;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}