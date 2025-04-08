package net.streamlinecloud.main.utils;

import com.google.gson.Gson;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class StreamlineConfig {

    int defaultProxyPort;
    int communicationBridgePort;

    String language;
    String defaultJavaPath;
    String fallbackGroup;

    boolean useWebSocket = false;
    String websocketUrl = "http://localhost:3000";

    boolean useMultiRoot = false;
    String multiRootConnection = "";

    boolean disableColors = false;
    boolean useLegacyColor = false;
    boolean enableRconSupport = true;

    boolean whitelistEnabled = false;
    List<String> whitelist = new ArrayList<>();

    public static StreamlineConfig fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, StreamlineConfig.class);
    }

    public StreamlineConfig(String defaultJavaPath, int defaultProxyPort, int communicationBridgePort, String fallbackGroup) {
        this.defaultJavaPath = defaultJavaPath;
        this.defaultProxyPort = defaultProxyPort;
        this.communicationBridgePort = communicationBridgePort;
        this.fallbackGroup = fallbackGroup;
        this.language = "en.json";
    }

    @SneakyThrows
    public static void init() {

        new File(Cache.i().homeFile + "/data").mkdirs();

        File apiKeyFile = new File(Cache.i().homeFile + "/data/apikey.json");
        File configFile = new File(Cache.i().homeFile + "/data/config.json");
        File groupsFolder = new File(Cache.i().homeFile + "/groups");
        File[] files = groupsFolder.listFiles();

        if (!configFile.exists()) {
            configFile.createNewFile();
            Cache.i().setFirstLaunch(true);
            return;
        }

        String jsonConfig = FileUtils.readFileToString(configFile, Charset.defaultCharset());
        StreamlineConfig config = new Gson().newBuilder().setPrettyPrinting().create().fromJson(jsonConfig, StreamlineConfig.class);
        Cache.i().setConfig(config);



        if (!apiKeyFile.exists()) {
            apiKeyFile.createNewFile();
            FileWriter writer = new FileWriter(apiKeyFile);
            writer.write(StreamlineCloud.generateApiKey());
            writer.write("\n#");
            writer.write("\n# This key is randomly generated and only used for internal purposes.");
            writer.write("\n# Do NOT share this key!");
            writer.write("\n# Delete this file to regenerate the key.");
            writer.write("\n#");
            writer.write("\n# Developed by: " + Settings.authors);
            writer.close();
        }

        BufferedReader reader = new BufferedReader(new FileReader(apiKeyFile));
        String firstLine = reader.readLine();
        reader.close();
        Cache.i().setApiKey(firstLine);

        Cache.i().setDisabledColors(Cache.i().getConfig().disableColors);
        Cache.i().setUseLgecyColor(Cache.i().getConfig().useLegacyColor);

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Cache.i().getActiveGroups().add(readGroup(file.getName()));
                }
            }
        } else {
            StreamlineCloud.logImportant("No groups exists: groups help");
        }
    }

    public static CloudGroup readGroup(String fileName) {

        File file = new File(Cache.i().homeFile + "/groups/" + fileName);
        CloudGroup group;

        try {
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());
            group = new Gson().fromJson(json, CloudGroup.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return group;

    }

    public static void saveConfig() {
        String json = new Gson().newBuilder().setPrettyPrinting().create().toJson(Cache.i().getConfig(), StreamlineConfig.class);
        File config = new File(Cache.i().homeFile + "/data/config.json");
        try {
            FileUtils.writeStringToFile(config, json);
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }
}
