package net.streamlinecloud.mc.api.server;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.api.group.GroupManager;
import net.streamlinecloud.mc.api.player.PlayerManager;
import net.streamlinecloud.mc.utils.Functions;
import net.streamlinecloud.mc.utils.StaticCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.ApiStatus;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ClientEndpoint
public class ServerManager {

    @Getter
    private static ServerManager instance;

    @Getter
    List<StreamlineServer> onlineServers = new ArrayList<>();

    @Getter
    HashMap<UUID, Instant> updatedServers = new HashMap<>();

    Session session;

    private static class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("Connected!");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("Server: " + data);
            webSocket.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("Error: " + error.getMessage());
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("Connection closed: " + reason);
            return null;
        }
    }

    public ServerManager() {
        instance = this;

        HttpClient client = HttpClient.newHttpClient();
        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create("ws://localhost:5378/socket/server"), new WebSocketListener())
                .join();
        webSocket.sendText("proxy-1", true);

        uploadServerInfo();
    }

    public void subscribe(StreamlineServer server) {
        try {
            session.getBasicRemote().sendText(server.getUuid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadServerInfo() {
        StreamlineServer s = getServer(UUID.fromString(StaticCache.serverData.getUuid()));
        if (PlayerManager.getInstance() != null) s.setOnlinePlayers(PlayerManager.getInstance().getPlayersMap());
        s.setServerState(ServerState.ONLINE);
        s.setMaxOnlineCount(Bukkit.getMaxPlayers());

        Functions.post(s, "post/server/updatedata");
    }

    /*private void updateServers() {
        HashMap<String, String> servers = new HashMap<>();
        servers = new Gson().fromJson(Functions.get("get/allserveruuids"), servers.getClass());

        for (String uuid : servers.keySet()) {
            if (!serverExists(uuid)) {
                try {
                    StreamlineServer lineServer = new Gson().fromJson(Functions.get("get/serverdata/" + uuid), StreamlineServer.class);
                    onlineServers.add(lineServer);
                } catch (Exception e) {
                    SpigotSCP.getInstance().getLogger().info("error!" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }

        StreamlineServer s = getServer(StaticCache.serverData.getName());
        s.setOnlinePlayers(PlayerManager.getInstance().getPlayersMap());
        s.setServerState(ServerState.ONLINE);
        s.setMaxOnlineCount(Bukkit.getMaxPlayers());

        Functions.post(s, "post/server/updatedata");
    }*/

    public StreamlineServer getServer(UUID uuid) {
        return new Gson().fromJson(Functions.get("get/serverdata/" + uuid.toString()), StreamlineServer.class);
    }

    public StreamlineServer getServer(String name) {
        return null;
    }
}
