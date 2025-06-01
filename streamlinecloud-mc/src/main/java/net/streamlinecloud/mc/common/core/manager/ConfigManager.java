package net.streamlinecloud.mc.common.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.streamlinecloud.mc.common.core.PluginConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Getter
public class ConfigManager {

    private final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private PluginConfig config;

    public ConfigManager(File dataFolder) {
        dataFolder.mkdirs();
        this.configFile = new File(dataFolder, "config.json");
        loadConfig();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            config = new PluginConfig();
            saveConfig();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, PluginConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                config = new PluginConfig();
            }
        }
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

