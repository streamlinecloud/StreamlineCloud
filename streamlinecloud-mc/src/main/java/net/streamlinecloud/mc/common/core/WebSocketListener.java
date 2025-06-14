package net.streamlinecloud.mc.common.core;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.paper.manager.ServerManager;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class WebSocketListener implements WebSocket.Listener {

    AbstractServerManager serverManager;

    public WebSocketListener(AbstractServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

        try {
            webSocket.request(1);

            System.out.println(data.toString());

            if (data.toString().equals("heartbeat")) return null;
            if (data.toString().equals("success")) return null;

            if (data.toString().startsWith("move:")) {
                serverManager.moveAllPlayersAndStop(data.toString().split(":")[1]);
                return null;
            }

            StreamlineServer server = new Gson().fromJson(data.toString(), StreamlineServer.class);

            if (serverManager.getSubscribedServers().removeIf(subscribedServer -> subscribedServer.getUuid().equals(server.getUuid()))) {
                serverManager.getSubscribedServers().add(server);

                if (StaticCache.getRuntime().equals(ServerRuntime.SERVER)) {
                    if (server.getServerState().equals(ServerState.STOPPING))
                        serverManager.onSubscribedServerStopped(server);
                    else serverManager.onSubscribedServerUpdated(server);
                }

            } else {
                serverManager.subscribe(server);

                if (StaticCache.getRuntime().equals(ServerRuntime.SERVER)) {
                    serverManager.onSubscribedServerStarted(server);
                }

            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("WS Error: " + error.getMessage());
        ServerManager.getInstance().reinit();
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("Connection closed: " + reason);
        return null;
    }
}