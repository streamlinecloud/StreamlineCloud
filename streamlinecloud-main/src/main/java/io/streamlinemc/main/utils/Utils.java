package io.streamlinemc.main.utils;

import io.streamlinemc.api.packet.StaticServerDataPacket;
import io.streamlinemc.main.CloudLauncher;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class Utils {

    public static void downloadFile(String fileUrl, String localFilePath) {

    }

    @SneakyThrows
    private static void copyApiKey(File file, CloudServer ser) {
        File f = new File(file.getPath() + "/.apikey.json");
        FileUtils.writeStringToFile(f, Cache.i().getGson().toJson(new StaticServerDataPacket(
                ser.getName(),
                ser.getPort(),
                "null",
                ser.getGroupDirect().getName(),
                ser.getUuid())), Charset.defaultCharset());
    }

    public static boolean isProcessRunning(Process process) {
        try {
            // Versuchen Sie, den Exit-Wert abzurufen. Wenn der Prozess noch läuft, wirft dies eine Exception.
            process.exitValue();
            return false; // Der Prozess wurde beendet
        } catch (IllegalThreadStateException e) {
            return true; // Der Prozess läuft noch
        }
    }

    public static File getResourceFile(String resourcePath, String filetype) {
        // Erhalten Sie den InputStream von der Ressource
        InputStream inputStream = CloudLauncher.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            try {
                // Erstellen Sie eine temporäre Datei
                File tempFile = File.createTempFile("tempFile_" + UUID.randomUUID().toString(), "." + filetype);

                // Schreiben Sie den Inhalt des InputStream in die temporäre Datei
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                return tempFile;
            } catch (IOException e) {
                e.printStackTrace();
                StreamlineCloud.logError(e.getMessage());
            } finally {
                try {
                    // Schließen Sie den InputStream, wenn Sie fertig sind
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    StreamlineCloud.logError(e.getMessage());
                }
            }
        }

        return null;
    }

}
