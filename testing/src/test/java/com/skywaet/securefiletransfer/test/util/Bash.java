/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/
package com.skywaet.securefiletransfer.test.util;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the 'peer' cli command
 */
public final class Bash extends Command {

    public static BashBuilder newBuilder() {
        return new BashBuilder();
    }

    static public class BashBuilder extends Command.Builder<Bash> {
        String cmd;
        List<String> args = new ArrayList<>();
        Path workingDirectory;

        public BashBuilder duplicate() {
            try {
                return (BashBuilder) this.clone();
            } catch (CloneNotSupportedException e) {

                e.printStackTrace();
                return null;
            }
        }

        public BashBuilder cmd(String cmd) {
            this.cmd = cmd;
            return this;
        }

        public BashBuilder cmdargs(String... argsArray) {
            this.args = Arrays.asList(argsArray);
            return this;
        }

        public BashBuilder workingDirectory(Path workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }


        public Bash build() {

            ArrayList<String> list = new ArrayList<>();
            list.add(cmd);
            list.addAll(args);

            return new Bash(list, workingDirectory);
        }
    }

    Bash(List<String> cmd, Path workingDirectory) {
        super(cmd, workingDirectory);
    }
}