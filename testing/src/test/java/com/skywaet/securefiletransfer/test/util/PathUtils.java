package com.skywaet.securefiletransfer.test.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathUtils {

    public static String relativize(Path path) {
        var relativePath = Paths.get("").toAbsolutePath().relativize(path).toString();
        if (System.getProperty("os.name")
                .toLowerCase().startsWith("windows")) {
            return relativePath.replaceAll("\\\\", "/");
        }
        return relativePath;
    }
}
