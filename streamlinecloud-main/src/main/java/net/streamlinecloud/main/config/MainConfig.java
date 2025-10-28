package net.streamlinecloud.main.config;

import com.google.gson.Gson;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.LoadBalancer;
import net.streamlinecloud.main.core.group.CloudGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Settings;
import net.streamlinecloud.main.utils.StreamlineSetup;
import net.streamlinecloud.main.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MainConfig {

    String language;
    String defaultJavaPath;
    String defaultSoftwareName = "paper";
    String velocitySecret = "/templates/default/proxy/forwarding.secret";
    FallbackConfig fallback = new FallbackConfig();
    WhitelistConfig whitelist = new WhitelistConfig();
    WebSocketConfig websocket = new WebSocketConfig();
    NetworkConfig network = new NetworkConfig();
    AdvancedConfig advanced = new AdvancedConfig();

    public static Gson gson = new Gson().newBuilder()
                .setPrettyPrinting()
            .create();

    @Getter @Setter
    public static class FallbackConfig {
        String fallbackGroup;
        String fallbackPlayerSpreading = "RANDOM";
        boolean dynamicFallbackControl = false;
        int dynamicFallbackPuffer = 20;
        String _info = "dynamicFallbackControl starts and stopps the fallbacks based on the network player count. You can set fallbackPlayerSpreading to SPLIT, BUNDLE, RANDOM";
    }

    @Getter @Setter
    public static class WhitelistConfig {
        boolean whitelistEnabled = false;
        List<String> whitelist = new ArrayList<>();
    }

    @Getter @Setter
    public static class WebSocketConfig {
        boolean useWebSocket = false;
        String websocketUrl = "http://localhost:3000";
    }

    @Getter @Setter
    public static class NetworkConfig {
        int backendPort;
        List<LoadBalancer> loadBalancers = new ArrayList<>();
    }

    @Getter @Setter
    public static class AdvancedConfig {
        boolean disableRemoteCommands = false;
        boolean disableColors = false;
        boolean useLegacyColor = false;
        boolean enableRconSupport = true;
    }

    public static MainConfig fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, MainConfig.class);
    }

    public MainConfig(String defaultJavaPath, int backendPort, String fallbackGroup) {
        this.language = "lang/en.json";
        this.defaultJavaPath = defaultJavaPath;
        this.network.backendPort = backendPort;
        this.fallback.fallbackGroup = fallbackGroup;
    }

    @SneakyThrows
    public static void init() {

        Utils.runMkdir(new File(Cache.i().homeFile + "/data").mkdirs());

        File apiKeyFile = new File(Cache.i().homeFile + "/data/.apikey");
        File configFile = new File(Cache.i().homeFile + "/data/config.json");
        File groupsFolder = new File(Cache.i().homeFile + "/groups");
        File[] files = groupsFolder.listFiles();

        if (!configFile.exists()) {
            Utils.runMkdir(configFile.createNewFile());
            Cache.i().setFirstLaunch(true);
            return;
        }

        String jsonConfig = FileUtils.readFileToString(configFile, Charset.defaultCharset());
        MainConfig config = gson.fromJson(jsonConfig, MainConfig.class);
        Cache.i().setConfig(config);

        if (!apiKeyFile.exists()) {
            Utils.runMkdir(apiKeyFile.createNewFile());
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

        if (Cache.i().getConfig() == null) {
            StreamlineCloud.log("StreamlineCloud couldn't load the config. Loading the setup...");
            Cache.i().setFirstLaunch(true);
            return;
        }

        Cache.i().setDisabledColors(Cache.i().getConfig().advanced.disableColors);
        Cache.i().setUseLgecyColor(Cache.i().getConfig().advanced.useLegacyColor);

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
        String json = gson.toJson(Cache.i().getConfig(), MainConfig.class);
        File config = new File(Cache.i().homeFile + "/data/config.json");
        try {
            Files.writeString(config.toPath(), json);
        } catch (IOException e) {
            StreamlineCloud.logError(e.getMessage());
        }
    }
}
