package io.streamlinemc.main.utils;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.StaticServerDataPacket;
import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.main.CloudLauncher;
import io.streamlinemc.main.CloudMain;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.core.server.ServerTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

public class FileSystem {

    public static File homeFile;
    public static File templatesFile;
    public static File groupsFile = new File(System.getProperty("user.dir") + "/groups");
    public static File temporaryServersFile;
    public static File apiKeyFile;

    @SneakyThrows
    public static Object init() {
        try {
            StreamlineCloud.log("initializing file system...");

            groupsFile.mkdirs();

            //MainInit
            boolean launchFirstTime = false;
            File templatesDir = new File(System.getProperty("user.dir") + "/templates");
            homeFile = new File(System.getProperty("user.dir"));
            temporaryServersFile = new File(homeFile + "/temp");

            //CopyLangFiles
            File langFile = new File(homeFile + "/data/lang");
            langFile.mkdirs();
            try {

                if (InternalSettings.testBuild) {
                    copyFile(Paths.get(getFileAsURL("de.json").toURI()), Paths.get(homeFile + "/data/lang/de.json"));
                    copyFile(Paths.get(getFileAsURL("en.json").toURI()), Paths.get(homeFile + "/data/lang/en.json"));
                } else {
                    if (!Files.exists(new File(homeFile + "/data/lang/en.json").toPath())) Files.copy(getResourceFile("en.json", "json").toPath(), new File(homeFile + "/data/lang/en.json").toPath());
                    if (!Files.exists(new File(homeFile + "/data/lang/de.json").toPath())) Files.copy(getResourceFile("de.json", "json").toPath(), new File(homeFile + "/data/lang/de.json").toPath());
                }

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    try (Stream<Path> paths = Files.walk(Paths.get(langFile.getPath()))) {
                        paths
                                .filter(Files::isRegularFile)
                                .forEach(path -> readLangFile(path));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //ReadLangFiles
            try {
                try (Stream<Path> paths = Files.walk(Paths.get(langFile.getPath()))) {
                    paths
                            .filter(Files::isRegularFile)
                            .forEach(path -> readLangFile(path));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //GenerateConfig
            try {
                Gson gson = new Gson();
                File configFile = new File(homeFile + "/data/config.json");
                if (!configFile.exists()) {
                    new File(homeFile + "/data").mkdirs();
                    configFile.createNewFile();

                    /* StreamlineCloud.log("Please enter your java path:");
                    new ConsoleInput(ConsoleInput.InputType.STRING, output -> {
                        StreamlineConfig config = new StreamlineConfig(
                                output,
                                19132,
                                5378,
                                "lobby");

                        FileWriter writer = null;
                        try {
                            writer = new FileWriter(configFile);
                            writer.write(gson.toJson(config).replace(",", ",\n"));
                            writer.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        StreamlineCloud.log("Config Updated!");

                    });*/
                    //StaticCache.setConfig(config);
                    StaticCache.setFirstLaunch(true);
                    return false;

                };

                //ReadConfig
                String jsonConfig = new String(Files.readAllBytes(Paths.get(homeFile.getPath() + "/data/config.json")));
                StreamlineConfig config = StreamlineConfig.fromJson(jsonConfig);

                StaticCache.setConfig(config);
                CloudMain.getInstance().initLang();

                //GenerateApiKey
                try {
                    apiKeyFile = new File(homeFile + "/data/apikey.json");
                    new File(homeFile + "/data/").mkdirs();

                    if (!apiKeyFile.exists()) {
                        apiKeyFile.createNewFile();
                        FileWriter writer = new FileWriter(apiKeyFile);
                        writer.write(StreamlineCloud.generateApiKey());
                        writer.write("\n#");
                        writer.write("\n# Dieser Key ist zufällig generiert und nur für die interne Kommunikation wichtig.");
                        writer.write("\n# Gebe diesen Key niemals weiter!");
                        writer.write("\n# Lösche diese Datei um den Key neu zu generieren.");
                        writer.write("\n#");
                        writer.write("\n# Entwickelt von: " + InternalSettings.authors);
                        writer.close();
                    }

                    BufferedReader reader = new BufferedReader(new FileReader(apiKeyFile));
                    String firstLine = reader.readLine();
                    reader.close();
                    StaticCache.setApiKey(firstLine);
                    StreamlineCloud.log("ApiKey: " + firstLine);

                    StaticCache.setDisabledColors(config.disableColors);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //ReadGroups
                File[] dateien = groupsFile.listFiles();

                if (dateien != null) {
                    for (File datei : dateien) {
                        if (datei.isFile()) {
                            StaticCache.getActiveGroups().add(readGroup(datei.getName()));
                        }
                    }
                } else {
                    StreamlineCloud.logImportant("No groups exists: groups help");
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Files.exists(Paths.get(temporaryServersFile.getPath()))) {
                try {
                    deleteDirectory(Paths.get(temporaryServersFile.getPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            temporaryServersFile.mkdirs();

            if (!Files.exists(Paths.get(homeFile.getPath() + "/templates"))) {
                StreamlineCloud.log("generating basic files");

                launchFirstTime = true;
                templatesDir.mkdirs();
                templatesFile = templatesDir;

                new File(templatesFile.getPath() + "/default/server").mkdirs();
                new File(templatesFile.getPath() + "/default/proxy").mkdirs();

                StreamlineCloud.logImportant("please paste a server.jar & proxy.jar to templates/server & templates/proxy");
            }

            if (!Files.exists(Paths.get(homeFile.getPath() + "/staticservers"))) {
                StreamlineCloud.log("generating Static Servers saving folder");

                new File(homeFile.getPath() + "/staticservers").mkdirs();
            }


            return launchFirstTime;
        } catch (FileSystemNotFoundException e) {
            StreamlineCloud.logError("FileSystemNotFound: " + e.getMessage());
            return null;
        }
    }

    public static void saveConfig() {
        Gson gson = new Gson();
        File configFile = new File(homeFile + "/data/config.json");
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            writer.write(gson.toJson(StaticCache.getConfig()).replace(",", ",\n"));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            } finally {
                try {
                    // Schließen Sie den InputStream, wenn Sie fertig sind
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void readLangFile(Path file) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            // Lese den Inhalt der Datei
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            // Konvertiere den Inhalt zu einem JSONObject
            JSONObject configJson = new JSONObject(content.toString());

            // Konvertiere das JSONObject zu einer Map
            HashMap<String, String> config = new HashMap<>();
            for (String key : configJson.keySet()) {
                config.put(key, configJson.getString(key));
            }

            // Füge die Map zur HashMap hinzu
            StaticCache.getLanguages().add(new CloudLanguage(file.getFileName().toString(), config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(Path sourcePath, Path destinationPath) {
        try {
            if (!Files.exists(destinationPath)) {
                Files.copy(sourcePath, destinationPath);
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static URL getFileAsURL(final String fileName) {
        URL resourceUrl = CloudLauncher.class.getClassLoader().getResource(fileName);

        if (resourceUrl == null) {
            System.out.println(resourceUrl);
            throw new IllegalArgumentException(fileName + " is not found");
        }

        return resourceUrl;
    }

    public static CloudGroup readGroup(String fileName) {
        File file = new File(FileSystem.groupsFile.getAbsolutePath() + "/" + fileName);
        CloudGroup group = null;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String firstLine = reader.readLine();
            reader.close();

            group = new Gson().fromJson(firstLine, CloudGroup.class);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return group;
    }

    public static boolean checkTemplate(String path) {
        if (!Files.exists(Paths.get(homeFile.getPath() + path))) return true;
        return false;
    }

    @SneakyThrows
    public static ServerTemplate createStaticServer(CloudServer ser) {
        File file = new File(homeFile + "/staticservers/" + ser.getName());
        if (file.exists()) {
            copyApiKey(file, ser);
            return new ServerTemplate(new File(file + "/server.jar"), file);
        }

        file.mkdirs();

        copyApiKey(file, ser);

        List<String> t = new ArrayList<>();
        t.add(homeFile.getPath() + "/templates/default/" + ser.getRuntime().toString().toLowerCase());
        for (String s : StreamlineCloud.getGroupByName(ser.getGroup()).getTemplates()) t.add(homeFile.getPath() + "/templates/" + s);



        /*try {
            copyOrAddFile(CloudMain.class.getClassLoader().getResource("bungee/config.yml").getPath(), file.getPath(), "config.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        copyFolders(t, file.getPath(), ser.getRuntime());
        return new ServerTemplate(new File(file + "/server.jar"), file);
    }

    @SneakyThrows
    private static void copyApiKey(File file, CloudServer ser) {
        File f = new File(file.getPath() + "/.apikey.json");
        FileWriter writer = new FileWriter(f);
        f.createNewFile();
        try {
            writer.write(StaticCache.getApiKey() + ",_," + new Gson().toJson(new StaticServerDataPacket(ser.getName(), ser.getPort(), "null", ser.getGroupDirect().getName(), ser.getUuid())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.close();
    }

    @SneakyThrows
    public static ServerTemplate createTemporaryServer(List<String> templates, String name, String id, ServerRuntime runtime, CloudServer ser) {
        File serverFile = new File(homeFile + "/temp/" + name + "-" + id);
        serverFile.mkdirs();

        List<String> t = new ArrayList<>();
        t.add(homeFile.getPath() + "/templates/default/" + runtime.toString().toLowerCase());
        for (String s : templates) t.add(homeFile.getPath() + "/templates/" + s);

        copyFolders(t, serverFile.getPath(), runtime);

        copyApiKey(serverFile, ser);

        /*try {

            //copyOrAddFile("resources/bungee/config.yml", targetFolder);
            //copyOrAddFile(CloudMain.class.getClassLoader().getResource("bungee/config.yml").getPath(), serverFile.getPath(), "config.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        return new ServerTemplate(new File(serverFile.getPath() + "/server.jar"), serverFile);
    }

    private static void copyFolders(List<String> folderPaths, String targetFolder, ServerRuntime runtime) {
        /*Set<String> copiedFiles = new HashSet<>();

        for (String folderPath : folderPaths) {
            try {
                Path source = Paths.get(folderPath);
                Path destination = Paths.get(targetFolder);

                Files.walk(source)
                        .filter(Files::isRegularFile)
                        .forEach(sourceFile -> {
                            Path relativePath = source.relativize(sourceFile);
                            Path destinationFile = destination.resolve(relativePath);

                            if (!copiedFiles.contains(destinationFile.toString())) {
                                try {
                                    Files.createDirectories(destinationFile.getParent());
                                    Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                                    copiedFiles.add(destinationFile.toString());
                                } catch (IOException e) {
                                    StreamlineCloud.log("Can't create template: " + e.getMessage());
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

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
                                // Copy directory (create if not exists)
                                try {
                                    Files.createDirectories(destinationPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // Copy file
                                if (!copiedFiles.contains(destinationPath.toString())) {
                                    try {
                                        Files.createDirectories(destinationPath.getParent());
                                        Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                                        copiedFiles.add(destinationPath.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            new File(targetFolder + "/plugins").mkdirs();
            copyOrAddFile("streamlinecloud-mc.jar", targetFolder + "/plugins", "streamlinecloud-mc.jar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (runtime.equals(ServerRuntime.SERVER)) {
            try {
                //copyOrAddFile("resources/spigot/eula.txt", targetFolder);
                //copyOrAddFile("resources/spigot/spigot.yml", targetFolder);
                copyOrAddFile("spigot/eula.txt", targetFolder, "eula.txt");
                copyOrAddFile("spigot/spigot.yml", targetFolder, "spigot.yml");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (runtime.equals(ServerRuntime.PROXY)) {
            try {

                //copyOrAddFile("resources/bungee/config.yml", targetFolder);
                copyOrAddFile("bungee/config.yml", targetFolder, "config.yml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file); // Datei löschen
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); // Verzeichnis löschen
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void copyOrAddFile(String sourcePath, String destinationPath, String fileName) throws IOException {
        BufferedReader readerSource = null;
        BufferedReader readerDestination = null;
        BufferedWriter writer = null;

        try {
            // sourcePath = sourcePath + "/" + fileName;
            destinationPath = destinationPath + "/" + fileName;



            readerSource = new BufferedReader(new FileReader(getResourceFile(sourcePath, "yml").getPath()));
            String lineSource;
            StringBuilder contentSource = new StringBuilder();

            while ((lineSource = readerSource.readLine()) != null) {
                contentSource.append(lineSource).append("\n");
            }

            if (fileExists(destinationPath)) {
                if (InternalSettings.testBuild) {
                    readerDestination = new BufferedReader(new FileReader(destinationPath));
                } else {
                    readerDestination = new BufferedReader(new FileReader(getResourceFile(destinationPath, "yml").getPath()));
                }
                String lineDestination;
                StringBuilder contentDestination = new StringBuilder();

                //if (!Files.exists(new File(homeFile + "/data/lang/de.json").toPath())) Files.copy(getResourceFile("de.json", "json").toPath(), new File(homeFile + "/data/lang/de.json").toPath());

                while ((lineDestination = readerDestination.readLine()) != null) {
                    contentDestination.append(lineDestination).append("\n");
                }

                contentDestination.append(contentSource.toString());

                writer = new BufferedWriter(new FileWriter(destinationPath));
                writer.write(contentDestination.toString());
            } else {
                writer = new BufferedWriter(new FileWriter(destinationPath));
                writer.write(contentSource.toString());
            }

        } catch (Exception ignore) {} finally
         {
            if (readerSource != null) {
                readerSource.close();
            }
            if (readerDestination != null) {
                readerDestination.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static boolean fileExists(String path) {
        return new java.io.File(path).exists();
    }

}
