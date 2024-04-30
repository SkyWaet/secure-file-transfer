/*
Copyright IBM Corp. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package com.skywaet.securefiletransfer.test.util;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Command {

    protected final List<String> cmd;
    protected final Path workingDirectory;

    Command(List<String> cmd, Path workingDirectory) {
        this.cmd = cmd;
        this.workingDirectory = workingDirectory;
    }

    public static final class Result {
        public List<String> stdout;
        public List<String> stderr;
        public int exitcode;
    }

    /**
     * Run but don't suppress the output being printed directly
     */
    public Result run() {
        return this.run(false);
    }

    /**
     * Run the command, and process the output to arrays for later parsing and checking
     *
     * @param quiet true if the output should NOT be printed directly to System.out/System.err
     */
    public Result run(boolean quiet) {
        if (System.getProperty("os.name")
                .toLowerCase().startsWith("windows")) {
            cmd.addFirst("wsl.exe");
        }
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory.toFile());
        }
        final Result result = new Result();

        System.out.println("Running:" + this);
        try {

            processBuilder.redirectInput(Redirect.INHERIT);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            System.out.println("Started..... ");

            CompletableFuture<ArrayList<String>> soutFut = readOutStream(process.getInputStream(), quiet ? null : System.out);
            CompletableFuture<ArrayList<String>> serrFut = readOutStream(process.getErrorStream(), quiet ? null : System.err);

            CompletableFuture<Result> resultFut = soutFut.thenCombine(serrFut, (stdout, stderr) -> {
                // print to current stderr the stderr of process and return the stdout
                result.stderr = stderr;
                result.stdout = stdout;
                return result;
            });

            result.exitcode = process.waitFor();
            // get stdout once ready, blocking
            resultFut.get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result.exitcode = -1;
        }

        return result;
    }

    /**
     * Collect the information from the executed process and add them to a result object
     *
     * @param is
     * @param stream
     * @return Completable Future with the array list of the stdout/stderr
     */
    CompletableFuture<ArrayList<String>> readOutStream(InputStream is, PrintStream stream) {
        return CompletableFuture.supplyAsync(() -> {
            try (InputStreamReader isr = new InputStreamReader(is); BufferedReader br = new BufferedReader(isr);) {
                ArrayList<String> res = new ArrayList<>();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    if (stream != null) stream.println(inputLine);
                    res.add(inputLine);
                }
                return res;
            } catch (Throwable e) {
                throw new RuntimeException("problem with executing program", e);
            }
        });
    }

    public String toString() {
        return "[" + String.join(" ", cmd) + "]";
    }

    static public class Builder<T extends Command> implements Cloneable {
        @SuppressWarnings("unchecked")
        public Builder<T> duplicate() {
            try {
                return (Builder<T>) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
