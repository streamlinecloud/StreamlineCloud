package net.streamlinecloud.mc.common.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginConfig {

    public String prefix = "§8» §c§lStreamline§b§lCloud §8| §7";

    Permissions permissions = new Permissions();

    @Getter @Setter
    public static class Permissions {
        public String serverInfo = "streamlinecloud.command.serverInfo";
        public String switchServer = "streamlinecloud.command.server";
        public String remoteCLI = "streamlinecloud.command.cli";
    }
}
