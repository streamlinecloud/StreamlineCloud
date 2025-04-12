package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.api.packet.StartServerPacket;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.api.server.StreamlineServerSnapshot;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServerController {

    public void start(@NotNull Context context) {
        StartServerPacket packet = new Gson().fromJson(context.body(), StartServerPacket.class);

        context.result(StreamlineCloud.startServerByGroup(StreamlineCloud.getGroupByName(packet.getGroup()), Arrays.asList(packet.getTemplates())));
        context.status(200);
    }

    public void getAllSnapshots(@NotNull Context context) {
        AtomicReference<List<StreamlineServerSnapshot>> snapshots = new AtomicReference<>(new ArrayList<>());

        Cache.i().getRunningServers().forEach(server -> snapshots.get().add(new StreamlineServerSnapshot(server.getName(), server.getUuid(), server.getPort())));

        context.result(new Gson().toJson(snapshots.get()));
        context.status(200);
    }

    public void getFallbackServers(@NotNull Context context) {
        List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName(Cache.i().getConfig().getFallback().getFallbackGroup()));
        List<String> names = new ArrayList<>();
        for (CloudServer s : servers) names.add(s.getName());

        context.result(new Gson().toJson(names));
        context.status(200);
    }

    public void serverCount(@NotNull Context context) {
        context.result(String.valueOf(Cache.i().getRunningServers().size()));
        context.status(200);
    }

    public void get(@NotNull Context context) {
        CloudServer server = null;

        if (context.pathParamMap().containsKey("uuid")) {
            String uuid = context.pathParam("uuid");
            server = StreamlineCloud.getServerByUuid(uuid);

        } else if (context.pathParamMap().containsKey("name")) {
            String name = context.pathParam("name");
            server = StreamlineCloud.getServerByName(name);

        }

        if (server != null) {
            context.result(new Gson().toJson(server, StreamlineServer.class));
            context.status(200);
        } else {
            context.result("serverNotFound");
            context.status(601);
        }
    }

    public void getRconDetails(@NotNull Context context) {
        String uuid = context.pathParam("uuid");

        if (uuid == null) {
            context.result("UUID not found");
            context.status(201);
            return;
        }

        if (!Cache.i().getRconDetails().containsKey(uuid)) {
            context.result("UUID not found");
            context.status(201);
            return;
        }

        context.status(HttpStatus.OK);
        context.result(Cache.i().getGson().toJson(Cache.i().getRconDetails().get(uuid), RconData.class));
    }

    public void update(@NotNull Context context) {
        StreamlineServer s = new Gson().fromJson(context.body(), StreamlineServer.class);
        CloudServer cs = StreamlineCloud.getServerByName(s.getName());

        if (cs == null) {
            context.status(201);
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

        context.status(200);
    }

    public void stop(@NotNull Context context) {
        String uuid = context.pathParam("uuid");
    }

    public void kill(@NotNull Context context) {
        String uuid = context.pathParam("uuid");
    }
}
