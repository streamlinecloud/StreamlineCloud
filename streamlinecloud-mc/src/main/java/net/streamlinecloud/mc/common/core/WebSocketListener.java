package net.streamlinecloud.mc.common.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.socket.SocketMessage;
import net.streamlinecloud.mc.PaperSCP;
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

            SocketMessage message;

            try {
                message = SocketMessage.fromJson(data.toString());
            } catch (JsonSyntaxException e) {
                serverManager.log("[SOCKET] Got invalid message from server: " + data);
                return null;
            }

            if (message.getType().equals(SocketMessage.SocketMessageType.ERROR)) {
                serverManager.log("[SOCKET] Got error from server: " + message.getContent());
                return null;
            }

            if (message.getType().isFromClient()) {
                serverManager.log("[SOCKET] Got invalid message type from server: " + message.getType());
                return null;
            }

            if (message.getType().equals(SocketMessage.SocketMessageType.HEARTBEAT)) return null;
            if (message.getType().equals(SocketMessage.SocketMessageType.SUCCESS)) return null;

            if (message.getType().equals(SocketMessage.SocketMessageType.MOVE_SERVER)) {

                serverManager.moveAllPlayersAndStop(message.getContent());
                return null;

            } else if (message.getType().equals(SocketMessage.SocketMessageType.SERVER_UPDATE)) {

                StreamlineServer server;

                try {
                    server = new Gson().fromJson(message.getContent(), StreamlineServer.class);
                } catch (Exception e) {
                    PaperSCP.getInstance().getLogger().warning("Got invalid message from server: " + data + " (" + e.getMessage() + ")");
                    return null;
                }

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