package io.streamlinemc.main.core.backend;

import com.google.gson.Gson;
import io.streamlinemc.api.group.StreamlineGroup;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.StaticCache;
import io.streamlinemc.main.core.server.CloudServer;
import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BackEndMain {

    private static final String mainPath = "/streamline/";
    private static Javalin app;

    public static void startBE() {

        StreamlineCloud.log("sl.backend.starting", new ReplacePaket[]{new ReplacePaket("%0", StaticCache.getConfig().getCommunicationBridgePort() + "")});

        app = Javalin.create()

                .before(ctx -> {

                    if (ctx.path().equals(mainPath + "ping")) return;

                    if (ctx.header("auth_key") == null) {
                        ctx.result("authFailed");
                        ctx.status(401);
                        ctx.res().sendError(401);
                        return;
                    }


                    try {
                        String key = ctx.header("auth_key");

                        if (!StaticCache.getApiKey().equals(key)) {
                            ctx.result("authFailed");
                            ctx.status(401);
                            ctx.res().sendError(401);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })

                .get(mainPath + "ping", ctx -> {
                    ctx.result("pong");
                    ctx.status(200);
                })

                .get(mainPath + "get/servercount", ctx -> {


                    ctx.result("" + StaticCache.getRunningServers().size());
                    ctx.status(200);

                })
                .get(mainPath + "get/info", ctx -> {

                    if (ctx.header("servername") == null) {
                        ctx.result("Request Failed: Servername null");
                        ctx.status(604);
                    }

                    ctx.header("servername");
                    StreamlineCloud.log(ctx.header("servername"));

                    for (CloudServer runningServer : StaticCache.getRunningServers()) {
                        if (runningServer.getName().equals(ctx.header("servername"))) {
                            ctx.result(new Gson().toJson(runningServer, CloudServer.class));
                            ctx.status(200);
                            return;
                        }

                    }

                    ctx.result("-1");
                    ctx.status(604);
                })

                .get(mainPath + "get/allservers", ctx -> {

                    HashMap<String, Integer> serverList = new HashMap<>();

                    StaticCache.getRunningServers().forEach(server -> serverList.put(server.getName(), server.getPort()));

                    ctx.result(new Gson().toJson(serverList));
                    ctx.status(200);
                })

                .get(mainPath + "get/dataset/servers", ctx -> {

                    List<StreamlineServer> serverList = new ArrayList<>();

                    for (CloudServer server : StaticCache.getRunningServers()) {
                        StreamlineServer s = new StreamlineServer();
                        s.setName(server.getName());
                        s.setPort(server.getPort());
                        s.setGroup(server.getGroup());
                        s.setOnlinePlayers(server.getOnlinePlayers());
                        s.setMaxOnlineCount(server.getMaxOnlineCount());
                        s.setServerState(server.getServerState());
                        serverList.add(s);
                    }

                    ctx.result(new Gson().toJson(serverList));
                    ctx.status(200);
                })

                .get(mainPath + "get/allserveruuids", ctx -> {

                    HashMap<String, String> serverList = new HashMap<>();

                    StaticCache.getRunningServers().forEach(server -> serverList.put(server.getUuid(), server.getName()));

                    ctx.result(new Gson().toJson(serverList));
                    ctx.status(200);
                })

                .get(mainPath + "get/uptime", ctx -> {
                    long dif_intime = Calendar.getInstance().getTimeInMillis() - StaticCache.getStartuptime();
                    long dif_inmin = (dif_intime / (1000 * 60)) % 60;
                    long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;

                    String hour = dif_inhour + "";
                    String min = dif_inmin + "";

                    if (dif_inhour <= 9)  hour = "0" + hour;
                    if (dif_inmin <= 9)  min = "0" + min;

                    ctx.result(hour + ":" + min);
                    ctx.status(200);
                })

                .get(mainPath + "get/fallbackservers", ctx -> {

                    List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName(StaticCache.getConfig().getFallbackGroup()));
                    List<String> names = new ArrayList<>();
                    for (CloudServer s : servers) names.add(s.getName());

                    ctx.result(new Gson().toJson(names));
                    ctx.status(200);

                })

                .get(mainPath + "get/serverdata/{uuid}", ctx -> {
                    String uuid = ctx.pathParam("uuid");
                    CloudServer server = StreamlineCloud.getServerByUuid(uuid);

                    if (server != null) {
                        StreamlineServer streamlineServer = server;
                        ctx.result(new Gson().toJson(streamlineServer, StreamlineServer.class));
                        ctx.status(200);
                    } else {
                        ctx.result("serverNotFound");
                        ctx.status(601);
                    }
                })

                .get(mainPath + "get/groupdata/{name}", ctx -> {
                    String name = ctx.pathParam("name");
                    StreamlineGroup server = StreamlineCloud.getGroupByName(name);

                    if (server != null) {
                        ctx.result(new Gson().toJson(server, StreamlineGroup.class));
                        ctx.status(200);
                    } else {
                        ctx.result("serverNotFound");
                        ctx.status(601);
                    }
                })

                .get(mainPath + "get/allgroups", ctx -> {

                    List<String> groups = new ArrayList<>();

                    StaticCache.getActiveGroups().forEach(g -> groups.add(g.getName()));

                    ctx.result(new Gson().toJson(groups));
                    ctx.status(200);
                })


                /*.post(mainPath + "post/server/staticstreamlineserverdata", ctx -> {

                    StaticServerDataPacket serverDataPacket = ctx.bodyAsClass(StaticServerDataPacket.class);
                    serverDataPacket.getUuid();

                    ctx.status(200);

                })*/

                .post(mainPath + "post/proxy/version", ctx -> {

                    VersionPacket packet = new Gson().fromJson(ctx.body(), VersionPacket.class);

                    StaticCache.setPluginVersion(packet.getVersion());
                    StaticCache.setPluginApiVersion(packet.getApiVersion());
                    StaticCache.setPluginBuildDate(packet.getBuildDate());

                    ctx.status(200);
                })

                .post(mainPath + "post/server/hello-world", ctx -> {


                    CloudServer ser = StreamlineCloud.getServerByUuid(ctx.body().replace('"', ' ').replace(" ", ""));

                    if (ser == null) {
                        StreamlineCloud.log("Server not found");
                        ctx.status(201);
                        return;
                    }

                    StreamlineCloud.log("sl.server.online", new ReplacePaket[]{new ReplacePaket("%1", ser.getName() + "-" + ser.getUuid())});
                    ser.setServerState(ServerState.ONLINE);

                    ctx.status(201);

                })

                .post(mainPath + "post/server/updatedata", ctx -> {

                    StreamlineServer s = new Gson().fromJson(ctx.body(), StreamlineServer.class);
                    CloudServer cs = StreamlineCloud.getServerByName(s.getName());

                    if (cs == null) {
                        ctx.status(201);
                        return;
                    }

                    cs.setOnlinePlayers(s.getOnlinePlayers());
                    cs.setServerState(s.getServerState());
                    cs.setServerUseState(s.getServerUseState());
                    cs.setMaxOnlineCount(s.getMaxOnlineCount());

                    ctx.status(200);
                })

                .start(StaticCache.getConfig().getCommunicationBridgePort());

        StreamlineCloud.log("sl.backend.started");

    }

    public static void stop() {
        StreamlineCloud.log("Shutting down §AQUACommunicationBridge");
        if (app != null) app.stop();
        StreamlineCloud.log("§AQUACommunicationBridge §REDOffline!");
    }

}
