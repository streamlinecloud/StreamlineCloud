package net.streamlinecloud.main.utils;

import net.streamlinecloud.main.CloudLauncher;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    public static boolean isProcessRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static void runMkdir(boolean result) {
        if (Cache.i().isFirstLaunch() && !result) StreamlineCloud.log("StreamlineCloud has failed to create a file. This could be a permission or file system error");

    }

    public static Integer[] getNetworkOnlineCount()  {
        AtomicInteger online = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();

        CloudGroupManager.getInstance().getGroupOnlineServers(CloudGroupManager.getInstance().getGroupByName("proxy")).forEach(server -> {
            online.set(online.get() + server.getOnlinePlayers().size());
            max.set(max.get() + server.getMaxOnlineCount());
        });

        return new Integer[]{online.get(), max.get()};
    }

    public static File getResourceFile(String resourcePath, String filetype) {
        InputStream inputStream = CloudLauncher.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            try {
                File tempFile = File.createTempFile("tempFile_" + UUID.randomUUID(), "." + filetype);

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

    public static void copyResources(File sourceFile, File targetFile) {
        try {
            if (!targetFile.exists()) Utils.runMkdir(targetFile.createNewFile());

            YamlFile tempYamlFile = new YamlFile(sourceFile);
            YamlFile serverYamlFile = new YamlFile(targetFile);
            tempYamlFile.load();
            serverYamlFile.load();

            for (String key : tempYamlFile.getKeys(true)) {
                Object value = tempYamlFile.get(key);
                if (!serverYamlFile.contains(key)) {
                    serverYamlFile.set(key, value);
                }
            }

            serverYamlFile.save();
            tempYamlFile.deleteFile();


        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }


}
