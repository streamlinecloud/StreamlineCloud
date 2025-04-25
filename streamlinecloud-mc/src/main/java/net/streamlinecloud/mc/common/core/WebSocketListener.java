package net.streamlinecloud.mc.common.core;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.common.utils.StaticCache;

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

            if (data.toString().equals("heartbeat")) return null;
            if (data.toString().equals("success")) return null;

            System.out.println("onText: " + data.toString());

            StreamlineServer server = new Gson().fromJson(data.toString(), StreamlineServer.class);

            if (serverManager.getSubscribedServers().removeIf(subscribedServer -> subscribedServer.getUuid().equals(server.getUuid()))) {
                serverManager.getSubscribedServers().add(server);

                if (StaticCache.getRuntime().equals(ServerRuntime.SERVER)) {
                    serverManager.onSubscribedServerUpdated(server);
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
        System.out.println("Error: " + error.getMessage());
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("Connection closed: " + reason);
        return null;
    }
}