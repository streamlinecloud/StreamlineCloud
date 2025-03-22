package net.streamlinecloud.main.command;

import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.BuildSettings;
import net.streamlinecloud.main.utils.Cache;

public class VersionCommand extends CloudCommand {

    public VersionCommand() {
        setName("version");
        setAliases(new String[]{"v", "ver", "info"});
        setDescription("StreamlineCloud information");
    }

    @Override
    public void execute(String[] args) {

        String status = StreamlineAPI.getApiVersion().equals(Cache.i().getPluginApiVersion()) ? "§GREENOPERATIONAL" : "§GOLDWARNING";
        if (Cache.i().getPluginApiVersion().equals("unknown")) status = "§GRAYNOT_CONECTED";

        StreamlineCloud.log("");
        StreamlineCloud.log("StreamlineCloud-Main");
        StreamlineCloud.log("-> InternalVersion: §AQUA" + BuildSettings.version);
        StreamlineCloud.log("-> InternalApiVersion: §AQUA" + StreamlineAPI.getApiVersion());
        StreamlineCloud.log("-> BuildDate: §AQUA" + BuildSettings.buildDate);
        StreamlineCloud.log("");
        StreamlineCloud.log("StreamlineCloud-MC");
        StreamlineCloud.log("-> PluginVersion: §AQUA" + Cache.i().getPluginVersion());
        StreamlineCloud.log("-> PluginApiVersion: §AQUA" + Cache.i().getPluginApiVersion());
        StreamlineCloud.log("-> PluginBuildDate: §AQUA" + Cache.i().getPluginBuildDate());
        StreamlineCloud.log("");
        StreamlineCloud.log("-> API-Status: " + status);
        StreamlineCloud.log("Developed by: §AQUA" + BuildSettings.authors);
        StreamlineCloud.log("");

    }

}
