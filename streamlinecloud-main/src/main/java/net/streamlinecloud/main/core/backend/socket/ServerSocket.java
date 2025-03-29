package net.streamlinecloud.main.core.backend.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.websocket.WsContext;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.core.server.CloudServerSerializer;
import net.streamlinecloud.main.utils.Cache;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerSocket {

    public HashMap<String, List<StreamlineServer>> servers = new HashMap<>();
    public Map<String, WsContext> sessionMap = new ConcurrentHashMap<>();

    public ServerSocket() {
        ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(createHeartbeatRunnable(), 0, 30, TimeUnit.SECONDS);


        Cache.i().backend.ws("/socket/server", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("Session opened, id: " + ctx.sessionId());
                sessionMap.put(ctx.sessionId(), ctx);

                servers.put(ctx.sessionId(), new ArrayList<>());

            });
            ws.onMessage(ctx -> {
                System.out.println("Received from " + ctx.sessionId() + ": " + ctx.message());

                List<StreamlineServer> s = servers.get(ctx.sessionId());
                s.add(StreamlineCloud.getServerByName(ctx.message()));
                servers.replace(ctx.sessionId(), s);

                try {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(CloudServer.class, new CloudServerSerializer())
                            .create();

                    ctx.send(gson.toJson(StreamlineCloud.getServerByName(ctx.message())));
                    return;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                ctx.send("Something went wrong");

            });
            ws.onClose(ctx -> {
                System.out.println("Session closed, id: " + ctx.sessionId());
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
