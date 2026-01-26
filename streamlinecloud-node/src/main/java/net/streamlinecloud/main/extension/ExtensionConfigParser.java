package net.streamlinecloud.main.extension;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtensionConfigParser {
    public static ExtensionConfig parseConfig(String filepath) {
        try {
            Path path = Paths.get(filepath);
            InputStream inputStream = Files.newInputStream(path);
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, ExtensionConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
