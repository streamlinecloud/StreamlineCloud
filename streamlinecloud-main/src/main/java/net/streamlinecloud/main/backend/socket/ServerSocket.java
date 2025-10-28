package net.streamlinecloud.main.backend.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.javalin.websocket.WsContext;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.api.socket.SocketMessage;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.backend.BackEndMain;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.utils.Cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerSocket {

    public HashMap<String, List<StreamlineServer>> servers = new HashMap<>();
    public HashMap<String, List<StreamlineGroup>> subscribedStartingServers = new HashMap<>();
    public HashMap<String, WsContext> serverSessions = new HashMap<>();
    public Map<String, WsContext> sessionMap = new ConcurrentHashMap<>();

    public ServerSocket() {
        ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(createHeartbeatRunnable(), 0, 20, TimeUnit.SECONDS);


        Cache.i().backend.ws("/socket/server", ws -> {
            ws.onConnect(ctx -> {

                String key = ctx.queryParam("key");

                if (key == null || BackEndMain.customSessions.stream().noneMatch(s -> s.getKey().equals(key)) && !key.equals(Cache.i().getApiKey())) {
                    ctx.send(new SocketMessage(SocketMessage.SocketMessageType.ERROR, "403").toString());
                    ctx.closeSession();
                    return;
                }

                sessionMap.put(ctx.sessionId(), ctx);

                servers.put(ctx.sessionId(), new ArrayList<>());
                subscribedStartingServers.put(ctx.sessionId(), new ArrayList<>());

            });
            ws.onMessage(ctx -> {

                SocketMessage message;

                try {
                    message = SocketMessage.fromJson(ctx.message());
                } catch (JsonSyntaxException e) {
                    ctx.send(new SocketMessage(SocketMessage.SocketMessageType.ERROR, "400").toString());
                    return;
                }

                if (!message.getType().isFromClient()) {
                    ctx.send(new SocketMessage(SocketMessage.SocketMessageType.ERROR, "400").toString());
                    return;
                }

                switch (message.getType()) {

                    case SUBSCRIBE_SERVER -> {

                        List<StreamlineServer> s = servers.get(ctx.sessionId());
                        StreamlineServer streamlineServer = RunningServerManager.getInstance().getServerByName(message.getContent());
                        s.add(streamlineServer);
                        servers.replace(ctx.sessionId(), s);

                    }

                    case SUBSCRIBE_GROUP -> {

                        List<StreamlineGroup> s = subscribedStartingServers.get(ctx.sessionId());
                        s.add(CloudGroupManager.getInstance().getGroupByName(message.getContent()));
                        subscribedStartingServers.replace(ctx.sessionId(), s);

                    }

                    case IAM -> {

                        serverSessions.put(RunningServerManager.getInstance().getServerByUuid(message.getContent()).getUuid(), ctx);

                    }

                }

            });
            ws.onClose(ctx -> {
                servers.remove(ctx.sessionId());
                sessionMap.remove(ctx.sessionId());

            });
            ws.onError(errorContext -> {
                for (String uuid : serverSessions.keySet()) {
                    if (serverSessions.get(uuid).sessionId().equals(errorContext.sessionId())) {
                        StreamlineServer server = RunningServerManager.getInstance().getServerByUuid(uuid);
                        if (server.getServerState().equals(ServerState.STOPPING) || server.getServerState().equals(ServerState.DELETING)) {
                            return;
                        }
                    }
                }
                StreamlineCloud.log("CRITICAL: Socket connection error from " + errorContext.sessionId() + " - " + errorContext.error());
            });
        });
    }

    public void sendTo(StreamlineServer server, SocketMessage message) {
        try {
            serverSessions.forEach((s, ctx) -> {
                if (s.equals(server.getUuid())) {
                    ctx.send(message.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdate(StreamlineServer s) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RunningServer.class, new StreamlineServerSerializer())
                .create();

        for (String session : Cache.i().getServerSocket().servers.keySet()) {
            for (StreamlineServer server : Cache.i().getServerSocket().servers.get(session)) {
                if (server.getUuid().equals(s.getUuid())) {
                    for (StreamlineServer streamlineServer : Cache.i().getServerSocket().servers.get(session)) {
                        if (streamlineServer.getUuid().equals(s.getUuid())) {
                            Cache.i().getServerSocket().sessionMap.get(session).send(new SocketMessage(SocketMessage.SocketMessageType.SERVER_UPDATE, gson.toJson(s)).toString());
                        }
                    }
                }
            }
        }
    }

    private Runnable createHeartbeatRunnable() {
        return () -> {
            for (String s : sessionMap.keySet().stream().toList()) {
                sessionMap.get(s).send(new SocketMessage(SocketMessage.SocketMessageType.HEARTBEAT, "").toString());
            }

        };
    }

}
