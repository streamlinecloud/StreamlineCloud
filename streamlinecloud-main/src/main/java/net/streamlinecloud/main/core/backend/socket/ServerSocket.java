package net.streamlinecloud.main.core.backend.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.websocket.WsContext;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.BackEndMain;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.core.server.CloudServerManager;
import net.streamlinecloud.main.utils.Cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerSocket {

    public HashMap<String, List<StreamlineServer>> servers = new HashMap<>();
    public HashMap<String, List<StreamlineGroup>> subscribedStartingServers = new HashMap<>();
    public HashMap<StreamlineServer, WsContext> serverSessions = new HashMap<>();
    public Map<String, WsContext> sessionMap = new ConcurrentHashMap<>();

    public ServerSocket() {
        ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(createHeartbeatRunnable(), 0, 20, TimeUnit.SECONDS);


        Cache.i().backend.ws("/socket/server", ws -> {
            ws.onConnect(ctx -> {

                String key = ctx.queryParam("key");

                if ( key == null || BackEndMain.customSessions.stream().noneMatch(s -> s.getKey().equals(key))) {
                    ctx.send("403");
                    ctx.closeSession();
                    return;
                }

                sessionMap.put(ctx.sessionId(), ctx);

                servers.put(ctx.sessionId(), new ArrayList<>());
                subscribedStartingServers.put(ctx.sessionId(), new ArrayList<>());

            });
            ws.onMessage(ctx -> {

                // subscribe:server:{serverName}
                // subscribe:starting:{groupName}

                if (ctx.message().startsWith("subscribe")) {

                    if (ctx.message().split(":")[1].equals("server")) {

                        List<StreamlineServer> s = servers.get(ctx.sessionId());
                        StreamlineServer streamlineServer = CloudServerManager.getInstance().getServerByName(ctx.message().split(":")[2]);
                        s.add(streamlineServer);
                        servers.replace(ctx.sessionId(), s);

                        ctx.send("success");
                        return;

                        } else if (ctx.message().split(":")[1].equals("starting")) {

                        List<StreamlineGroup> s = subscribedStartingServers.get(ctx.sessionId());
                        s.add(CloudGroupManager.getInstance().getGroupByName(ctx.message().split(":")[2]));
                        subscribedStartingServers.replace(ctx.sessionId(), s);

                        ctx.send("success");
                        return;

                    }

                } else if (ctx.message().startsWith("iam")) {

                    serverSessions.put(CloudServerManager.getInstance().getServerByUuid(ctx.message().split(":")[1]), ctx);

                    ctx.send("success");
                    return;

                }

                ctx.send("Something went wrong");

            });
            ws.onClose(ctx -> {
                servers.remove(ctx.sessionId());
                sessionMap.remove(ctx.sessionId());

            });
            ws.onError(ctx -> {
                StreamlineCloud.log("CRITICAL: Socket connection error from " + ctx.sessionId() + " - " + ctx.error() );
            });
        });
    }

    public void sendTo(StreamlineServer server, String message) {
        try {
            serverSessions.forEach((s, ctx) -> {
                if (s.getUuid().equals(server.getUuid())) {
                    ctx.send(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdate(StreamlineServer s) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                .create();

        for (String session : Cache.i().getServerSocket().servers.keySet()) {
            for (StreamlineServer server : Cache.i().getServerSocket().servers.get(session)) {
                if (server.getUuid().equals(s.getUuid())) {
                    for (StreamlineServer streamlineServer : Cache.i().getServerSocket().servers.get(session)) {
                        if (streamlineServer.getUuid().equals(s.getUuid())) {
                            Cache.i().getServerSocket().sessionMap.get(session).send(gson.toJson(s));
                        }
                    }
                }
            }
        }
    }

    private Runnable createHeartbeatRunnable() {
        return () -> {
            for (String s : sessionMap.keySet().stream().toList()) {
                sessionMap.get(s).send("heartbeat");
            }

        };
    }

}
