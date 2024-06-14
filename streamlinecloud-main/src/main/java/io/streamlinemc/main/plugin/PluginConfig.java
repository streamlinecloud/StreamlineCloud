package io.streamlinemc.main.plugin;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PluginConfig {
    String mainClass;
    String name;
    String author;
    String version;
    String description;
}
