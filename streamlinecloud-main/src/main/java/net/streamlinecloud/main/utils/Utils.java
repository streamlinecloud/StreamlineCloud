package net.streamlinecloud.main.utils;

import net.streamlinecloud.main.CloudLauncher;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
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

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static void setFieldsFromMap(Object obj, Map<String, Object> values) throws IllegalAccessException, NoSuchFieldException, NumberFormatException {
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    if (field.getType() == int.class) {
                        field.setInt(obj, Integer.parseInt(entry.getValue().toString()));
                        return;
                    }
                    field.set(obj, entry.getValue());
                } catch (NoSuchFieldException ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }
    }


    public static void runMkdir(boolean result) {
        if (Cache.i().isFirstLaunch() && !result) StreamlineCloud.logDebug("StreamlineCloud has failed to create a file. This could be a permission or file system error");

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

    public static void copyFolder(List<String> folderPaths, String targetFolder) {

        Set<String> copiedFiles = new HashSet<>();

        for (String folderPath : folderPaths) {
            try {
                Path source = Paths.get(folderPath);
                Path destination = Paths.get(targetFolder);

                Files.walk(source)
                        .forEach(sourcePath -> {
                            Path relativePath = source.relativize(sourcePath);
                            Path destinationPath = destination.resolve(relativePath);

                            if (Files.isDirectory(sourcePath)) {
                                try {
                                    Files.createDirectories(destinationPath);
                                } catch (IOException e) {
                                    StreamlineCloud.logError(e.getMessage());
                                }
                            } else {
                                if (!copiedFiles.contains(destinationPath.toString())) {
                                    try {
                                        Files.createDirectories(destinationPath.getParent());
                                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                                        copiedFiles.add(destinationPath.toString());
                                    } catch (IOException e) {
                                        StreamlineCloud.logError(e.getMessage());
                                    }
                                }
                            }
                        });
            } catch (IOException e) {
                StreamlineCloud.logError(e.getMessage());
            }

        }

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
