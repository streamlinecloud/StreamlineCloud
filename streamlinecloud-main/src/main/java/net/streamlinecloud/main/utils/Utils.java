package net.streamlinecloud.main.utils;

import net.streamlinecloud.main.CloudLauncher;
import net.streamlinecloud.main.StreamlineCloud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Utils {

    public static boolean isProcessRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static File getResourceFile(String resourcePath, String filetype) {
        InputStream inputStream = CloudLauncher.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            try {
                File tempFile = File.createTempFile("tempFile_" + UUID.randomUUID().toString(), "." + filetype);

                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                return tempFile;
            } catch (IOException e) {
                StreamlineCloud.logError(e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    StreamlineCloud.logError(e.getMessage());
                }
            }
        }

        return null;
    }

}
