package net.streamlinecloud.main.command;

import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Settings;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.MainBuildConfig;

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
        StreamlineCloud.log("Build infos about this node:");
        StreamlineCloud.log("-> Version: §AQUA" + MainBuildConfig.VERSION + " (API: " + StreamlineAPI.getApiVersion() + ")");
        StreamlineCloud.log("-> Build: §AQUA" + MainBuildConfig.BUILD_NUMBER + " (" + MainBuildConfig.BUILD_DATE + ")");
        StreamlineCloud.log("");
        StreamlineCloud.log("Developed by: §AQUA" + Settings.authors);
        StreamlineCloud.log("");

    }

}
