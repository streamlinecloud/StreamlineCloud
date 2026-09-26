package net.streamlinecloud.main.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.streamlinecloud.api.node.StreamlineNode;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.command.StreamlineCommand;
import net.streamlinecloud.main.terminal.command.StreamlineCommandTree;
import net.streamlinecloud.main.terminal.command.StreamlineSubcommand;
import net.streamlinecloud.main.utils.MainBuildConfig;

@Slf4j
public class VersionCommand extends StreamlineCommand {

    public VersionCommand() {
        super(
                "version", "Shows the current version of the node as also the version of the used backend",
                new StreamlineCommandTree()
                        .add(new StreamlineSubcommand(
                                "show", (vars, logger) -> {

                                    String versionNumber = MainBuildConfig.VERSION;
                                    String buildDate = MainBuildConfig.BUILD_DATE;
                                    String buildNumber = MainBuildConfig.BUILD_NUMBER;

                                    JsonObject backendInformation = JsonParser.parseString(CloudMain.getInstance().getApiClient().getHttpClient().runBlockingGet("/_server/info")).getAsJsonObject();
                                    JsonObject backendUptimeMetrics = JsonParser.parseString(CloudMain.getInstance().getApiClient().getHttpClient().runBlockingGet("/_server/metrics/process.uptime")).getAsJsonObject();

                                    String backendJavaName = backendInformation.getAsJsonObject("java").getAsJsonObject("jvm").get("name").getAsString();
                                    String backendJavaVersion = backendInformation.getAsJsonObject("java").getAsJsonObject("jvm").get("version").getAsString();
                                    String backendOsName = backendInformation.getAsJsonObject("os").get("name").getAsString();;
                                    String backendSpringVersion = backendInformation.getAsJsonObject("spring").get("bootVersion").getAsString();
                                    String backendFrameworkVersion = backendInformation.getAsJsonObject("spring").get("frameworkVersion").getAsString();
                                    double backendUptime = backendUptimeMetrics.getAsJsonArray("measurements").get(0).getAsJsonObject().get("value").getAsDouble();

                                    StreamlineNode node = CloudMain.getInstance().getApiClient().getNode();

                                    logger.info("-----");
                                    logger.info("§BOLDCurrent Node:");
                                    logger.info("Name: §AQUA" + (node != null ? node.getDisplayname() : "Failed to get node information"));
                                    logger.info("UUID: §AQUA" + (node != null ? node.getUuid() : "Failed to get node information"));
                                    logger.info("");
                                    logger.info("Version: §AQUA" + versionNumber);
                                    logger.info("Build Date: §AQUA" + buildDate);
                                    logger.info("Build Number: §AQUA" + buildNumber);
                                    logger.info("");
                                    logger.info("§BOLDConnected Backend:");
                                    logger.info("Backend Java Runtime: §AQUA" + backendJavaName + " (" + backendJavaVersion + ")");
                                    logger.info("Backend Os Name: §AQUA" + backendOsName);
                                    logger.info("Backend Uptime (seconds) §AQUA" + backendUptime + "s");
                                    logger.info("Spring Version §AQUA" + backendSpringVersion + " (" + backendFrameworkVersion + ")");
                                    logger.info("-----");

                        }))
        );
        setDefaultSubCommand("show");
    }
}

/*
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
        StreamlineCloud.log("-> API-Status: " + status);
        StreamlineCloud.log("");
        StreamlineCloud.log("Developed by: §AQUA" + Settings.authors);
        StreamlineCloud.log("");

    }

}*/
