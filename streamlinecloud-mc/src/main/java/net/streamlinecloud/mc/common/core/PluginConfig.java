package net.streamlinecloud.mc.common.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginConfig {

    public String prefix = "§c§lStreamline§bCloud §r§8-> ";

    Permissions permissions = new Permissions();

    @Getter @Setter
    public static class Permissions {
        public String switchServer = "streamlinecloud.command.server";
        public String remoteCLI = "streamlinecloud.command.cli";
    }
}
