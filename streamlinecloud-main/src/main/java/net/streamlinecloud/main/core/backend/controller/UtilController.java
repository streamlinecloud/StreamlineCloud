package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.RemoteCommandPacket;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.CloudTerminalRunner;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Settings;

import java.util.Calendar;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class UtilController {

    public UtilController() {
        Cache.i().getBackend().get(mainPath + "get/versionInfo", ctx -> {
            String versionInfo = Settings.getVersionInfo();
            ctx.result(versionInfo).status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/info", ctx -> {

            if (ctx.header("servername") == null) {
                ctx.result("Request Failed: Servername null");
                ctx.status(604);
            }

            ctx.header("servername");
            StreamlineCloud.log(ctx.header("servername"));

            for (CloudServer runningServer : Cache.i().getRunningServers()) {
                if (runningServer.getName().equals(ctx.header("servername"))) {
                    ctx.result(new Gson().toJson(runningServer, CloudServer.class));
                    ctx.status(200);
                    return;
                }

            }
        });

        Cache.i().getBackend().get(mainPath + "ping", ctx -> {
            ctx.result("pong");
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/uptime", ctx -> {

            long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartuptime();
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;

            String hour = dif_inhour + "";
            String min = dif_inmin + "";

            if (dif_inhour <= 9)  hour = "0" + hour;
            if (dif_inmin <= 9)  min = "0" + min;

            ctx.result(hour + ":" + min);
            ctx.status(200);

        });

        Cache.i().getBackend().get(mainPath + "get/whitelist", ctx -> {

            if (!Cache.i().config.isWhitelistEnabled()) {
                ctx.result("false");
                ctx.status(200);
                return;
            }

            ctx.result(new Gson().toJson(Cache.i().config.getWhitelist()));
            ctx.status(200);

        });

        Cache.i().getBackend().post(mainPath + "post/command", ctx -> {

            RemoteCommandPacket packet = new Gson().fromJson(ctx.body(), RemoteCommandPacket.class);

            StreamlineCloud.log("Remote Command: §YELLOW" + packet.getCommand() + " §RED(from §YELLOW" + packet.getServer() + " §REDby §YELLOW" + packet.getExecutor() + "§RED)");
            CloudTerminalRunner.executeCommand(packet.getCommand().split(" "));

            ctx.status(200);
        });

    }
}
