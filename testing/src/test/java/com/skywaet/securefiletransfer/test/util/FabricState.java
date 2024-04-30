/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package com.skywaet.securefiletransfer.test.util;

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

    public synchronized void start(Path workingDirectory) {

        if (!this.started) {
            var bashBuilder = new Bash.BashBuilder()
                    .cmd("test-network/network.sh")
                    .cmdargs("up", "-bft")
                    .workingDirectory(workingDirectory);
            bashBuilder.build().run();
            this.started = true;

        } else {
            System.out.println("Fabric already started....");
        }
    }

    public synchronized void stop(Path workingDirectory) {

        if (this.started) {
            var bashBuilder = new Bash.BashBuilder()
                    .cmd("test-network/network.sh")
                    .cmdargs("down")
                    .workingDirectory(workingDirectory);
            bashBuilder.build().run();
            this.started = true;

        } else {
            System.out.println("Fabric not running....");
        }
    }

}