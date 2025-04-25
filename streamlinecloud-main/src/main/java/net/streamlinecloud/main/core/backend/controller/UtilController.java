package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import io.javalin.http.Context;
import net.streamlinecloud.api.packet.RemoteCommandPacket;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.CloudTerminalRunner;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilController {

    public void version(@NotNull Context context) {
        String versionInfo = Settings.getVersionInfo();
        context.result(versionInfo).status(200);
    }

    public void ping(@NotNull Context context) {
        context.result("pong");
        context.status(200);
    }

    public void uptime(@NotNull Context context) {
        long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartuptime();
        long dif_inmin = (dif_intime / (1000 * 60)) % 60;
        long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;

        String hour = dif_inhour + "";
        String min = dif_inmin + "";

        if (dif_inhour <= 9)  hour = "0" + hour;
        if (dif_inmin <= 9)  min = "0" + min;

        context.result(hour + ":" + min);
        context.status(200);
    }

    public void fallbackSpreading(@NotNull Context context) {
        context.result(Cache.i().getConfig().getFallback().getFallbackPlayerSpreading());
        context.status(200);
    }

    public void networkOnlineCount(@NotNull Context context) {
        AtomicInteger online = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();

        StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName("proxy")).forEach(server -> {
            online.set(online.get() + server.getOnlinePlayers().size());
            max.set(max.get() + server.getMaxOnlineCount());
        });

        context.result(online.intValue() + "-" + max.intValue());
        context.status(200);
    }

    public void whitelist(@NotNull Context context) {
        if (!Cache.i().config.getWhitelist().isWhitelistEnabled()) {
            context.result("false");
            context.status(200);
            return;
        }

        context.result(new Gson().toJson(Cache.i().config.getWhitelist()));
        context.status(200);
    }

    public void command(@NotNull Context context) {
        RemoteCommandPacket packet = new Gson().fromJson(context.body(), RemoteCommandPacket.class);

        StreamlineCloud.log("Remote Command: §YELLOW" + packet.getCommand() + " §RED(from §YELLOW" + packet.getServer() + " §REDby §YELLOW" + packet.getExecutor() + "§RED)");
        CloudTerminalRunner.executeCommand(packet.getCommand().split(" "));

        context.status(200);
    }

    //This needs to be optimized for the cluster feature
    public void reportProxyOnlineCount(@NotNull Context context) {
        String name = context.pathParam("name");

        Cache.i().setNetworkPlayerCount(Integer.parseInt(context.body()));
    }

}
