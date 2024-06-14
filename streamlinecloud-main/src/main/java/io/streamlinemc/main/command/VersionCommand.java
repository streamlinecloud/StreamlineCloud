package io.streamlinemc.main.command;

import io.streamlinemc.api.StreamlineAPI;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.InternalSettings;
import io.streamlinemc.main.utils.StaticCache;

public class VersionCommand extends CloudCommand {

    public VersionCommand() {
        setName("version");
        setAliases(new String[]{"v", "ver", "info"});
        setDescription("StreamlineCloud information");
    }

    @Override
    public void execute(String[] args) {

        String status = StreamlineAPI.getApiVersion().equals(StaticCache.getPluginApiVersion()) ? "§GREENOPERATIONAL" : "§GOLDWARNING";
        if (StaticCache.getPluginApiVersion().equals("unknown")) status = "§GRAYNOT_CONECTED";

        StreamlineCloud.log("");
        StreamlineCloud.log("StreamlineCloud-Main");
        StreamlineCloud.log("-> InternalVersion: §AQUA" + InternalSettings.version);
        StreamlineCloud.log("-> InternalApiVersion: §AQUA" + StreamlineAPI.getApiVersion());
        StreamlineCloud.log("-> BuildDate: §AQUA" + InternalSettings.buildDate);
        StreamlineCloud.log("");
        StreamlineCloud.log("StreamlineCloud-MC");
        StreamlineCloud.log("-> PluginVersion: §AQUA" + StaticCache.getPluginVersion());
        StreamlineCloud.log("-> PluginApiVersion: §AQUA" + StaticCache.getPluginApiVersion());
        StreamlineCloud.log("-> PluginBuildDate: §AQUA" + StaticCache.getPluginBuildDate());
        StreamlineCloud.log("");
        StreamlineCloud.log("-> API-Status: " + status);
        StreamlineCloud.log("Developed by: §AQUA" + InternalSettings.authors);
        StreamlineCloud.log("");

    }

}
