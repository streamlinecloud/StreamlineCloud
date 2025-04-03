package net.streamlinecloud.main.core.backend.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.websocket.WsContext;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.core.server.CloudServerSerializer;
import net.streamlinecloud.main.utils.Cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerSocket {

    public HashMap<String, List<StreamlineServer>> servers = new HashMap<>();
    public HashMap<String, List<StreamlineGroup>> subscribedStartingServers = new HashMap<>();
    public Map<String, WsContext> sessionMap = new ConcurrentHashMap<>();

    public ServerSocket() {
        ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(createHeartbeatRunnable(), 0, 30, TimeUnit.SECONDS);


        Cache.i().backend.ws("/socket/server", ws -> {
            ws.onConnect(ctx -> {

                if (!Objects.equals(ctx.queryParam("key"), Cache.i().apiKey)) {
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
                        s.add(StreamlineCloud.getServerByName(ctx.message().split(":")[2]));
                        servers.replace(ctx.sessionId(), s);

                        ctx.send("success");
                        return;

                    } else if (ctx.message().split(":")[1].equals("starting")) {

                        List<StreamlineGroup> s = subscribedStartingServers.get(ctx.sessionId());
                        s.add(StreamlineCloud.getGroupByName(ctx.message().split(":")[2]));
                        subscribedStartingServers.replace(ctx.sessionId(), s);

                        ctx.send("success");
                        return;

                    }

                }


                ctx.send("Something went wrong");

            });
            ws.onClose(ctx -> {
                servers.remove(ctx.sessionId());
                sessionMap.remove(ctx.sessionId());

            });
            ws.onError(ctx -> {
                System.out.println("Error: " + ctx.error());
            });
        });
    }

    private Runnable createHeartbeatRunnable() {
        return () -> {
            for (String s : sessionMap.keySet().stream().toList()) {
                sessionMap.get(s).send("heartbeat");
            }

        };
    }

}
