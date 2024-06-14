package io.streamlinemc.main.plugin;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConfigParser {
    public static PluginConfig parseConfig(String filepath) {
        try {
            Path path = Paths.get(filepath);
            InputStream inputStream = Files.newInputStream(path);
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, PluginConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
