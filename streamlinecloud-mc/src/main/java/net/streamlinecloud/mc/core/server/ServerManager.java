package net.streamlinecloud.mc.core.server;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.core.event.ServerDataReceivedEvent;
import net.streamlinecloud.mc.core.event.ServerDataUpdateEvent;
import net.streamlinecloud.mc.core.player.PlayerManager;
import net.streamlinecloud.mc.utils.Functions;
import net.streamlinecloud.mc.utils.StaticCache;
import lombok.Getter;
import org.bukkit.Bukkit;

import javax.websocket.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ClientEndpoint
public class ServerManager {

    @Getter
    private static ServerManager instance;

    @Getter
    private List<StreamlineServer> subscribedServers = new ArrayList<>();

    WebSocket socket;

    private class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("Connected!");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("Server: " + data);
            webSocket.request(1);

            if (data.toString().equals("heartbeat")) return null;
            if (data.toString().equals("success")) return null;

            StreamlineServer server = new Gson().fromJson(data.toString(), StreamlineServer.class);

            if (subscribedServers.removeIf(subscribedServer -> subscribedServer.getUuid().equals(server.getUuid()))) {
                ServerDataUpdateEvent event = new ServerDataUpdateEvent(server);
                Bukkit.getPluginManager().callEvent(event);

            } else {
                ServerDataReceivedEvent event = new ServerDataReceivedEvent(server);
                Bukkit.getPluginManager().callEvent(event);

            }

            subscribedServers.add(server);

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
        webSocket.sendText("subscribe:server:proxy-1", true);

        this.socket = webSocket;
        uploadServerInfo();
    }

    public void subscribe(StreamlineServer server) {
        for (StreamlineServer subscribedServer : subscribedServers) {
            if (subscribedServer.getUuid().equals(server.getUuid())) return;
        }

        socket.sendText("subscribe:server:" + server.getName(), true);
    }

    public void subscribeToStartingServers(String groupName) {
        socket.sendText("subscribe:starting:" + groupName, true);
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
        for (StreamlineServer s : subscribedServers) {
            if (s != null) if (s.getUuid().equals(uuid.toString())) return s;
        }

        return new Gson().fromJson(Functions.get("get/serverdata/" + uuid.toString()), StreamlineServer.class);
    }

    public StreamlineServer getServer(String name) {
        for (StreamlineServer s : subscribedServers) {
            if (s.getName().equals(name)) return s;
        }

        return new Gson().fromJson(Functions.get("get/serverdata/name/" + name), StreamlineServer.class);
    }
}
