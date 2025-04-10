package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.HttpStatus;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.api.packet.StartServerPacket;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerController {

    public ServerController() {
        Cache.i().getBackend().get(mainPath + "get/allservers", ctx -> {
            HashMap<String, Integer> serverList = new HashMap<>();

            Cache.i().getRunningServers().forEach(server -> serverList.put(server.getName(), server.getPort()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/allserveruuids", ctx -> {
            HashMap<String, String> serverList = new HashMap<>();

            Cache.i().getRunningServers().forEach(server -> serverList.put(server.getUuid(), server.getName()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/fallbackservers", ctx -> {
            List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName(Cache.i().getConfig().getFallbackGroup()));
            List<String> names = new ArrayList<>();
            for (CloudServer s : servers) names.add(s.getName());

            ctx.result(new Gson().toJson(names));
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/serverdata/{uuid}", ctx -> {
            String uuid = ctx.pathParam("uuid");
            CloudServer server = StreamlineCloud.getServerByUuid(uuid);

            if (server != null) {
                ctx.result(new Gson().toJson(server, StreamlineServer.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });

        Cache.i().getBackend().get(mainPath + "get/serverdata/name/{name}", ctx -> {
            String name = ctx.pathParam("name");
            CloudServer server = StreamlineCloud.getServerByName(name);

            if (server != null) {
                ctx.result(new Gson().toJson(server, StreamlineServer.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });

        Cache.i().getBackend().get(mainPath + "get/servers-by-group/{group}", ctx -> {
            try {
                CloudGroup group = StreamlineCloud.getGroupByName(ctx.pathParam("group"));
                if (group == null) {
                    ctx.result("groupNotFound");
                    ctx.status(200);
                    return;
                }

                List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(group);

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                        .create();

                List<StreamlineServer> streamlineServers = new ArrayList<>(servers);

                ctx.result(gson.toJson(streamlineServers));
                ctx.status(200);
            } catch (Exception e) {
                StreamlineCloud.logError(e.getMessage());
            }
        });

        Cache.i().getBackend().get(mainPath + "get/servercount", ctx -> {
            ctx.result("" + Cache.i().getRunningServers().size());
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/server/rcon-details/{uuid}", ctx -> {

            String uuid = ctx.pathParam("uuid");

            if (uuid == null) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            if (!Cache.i().getRconDetails().containsKey(uuid)) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            ctx.status(HttpStatus.OK);
            ctx.result(Cache.i().getGson().toJson(Cache.i().getRconDetails().get(uuid), RconData.class));

        });

        Cache.i().getBackend().post(mainPath + "post/server/updatedata", ctx -> {
            StreamlineServer s = new Gson().fromJson(ctx.body(), StreamlineServer.class);
            CloudServer cs = StreamlineCloud.getServerByName(s.getName());

            if (cs == null) {
                ctx.status(201);
                return;
            }

            if (cs.getServerState().equals(ServerState.STARTING)) {

                StreamlineCloud.log("sl.server.online", new ReplacePaket[]{new ReplacePaket("%1", cs.getName() + "-" + cs.getShortUuid())});
            }

            cs.setOnlinePlayers(s.getOnlinePlayers());
            cs.setServerState(s.getServerState());
            cs.setServerUseState(s.getServerUseState());
            cs.setMaxOnlineCount(s.getMaxOnlineCount());

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                    .create();

            for (String session : Cache.serverSocket.servers.keySet()) {
                for (StreamlineServer server : Cache.serverSocket.servers.get(session)) {
                    if (server.getUuid().equals(s.getUuid())) {
                        for (StreamlineServer streamlineServer : Cache.serverSocket.servers.get(session)) {
                            if (streamlineServer.getUuid().equals(cs.getUuid())) {
                                Cache.serverSocket.sessionMap.get(session).send(gson.toJson(s));
                            }
                        }
                    }
                }
            }

            ctx.status(200);
        });

        Cache.i().getBackend().post(mainPath + "post/server/start", ctx -> {
            StreamlineCloud.log("Start server POST");

            StartServerPacket packet = new Gson().fromJson(ctx.body(), StartServerPacket.class);

            ctx.result(StreamlineCloud.startServerByGroup(StreamlineCloud.getGroupByName(packet.getGroup()), Arrays.asList(packet.getTemplates())));
            ctx.status(200);
        });
    }
}
