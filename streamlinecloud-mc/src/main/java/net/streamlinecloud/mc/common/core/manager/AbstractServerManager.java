package net.streamlinecloud.mc.common.core.manager;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.common.core.WebSocketListener;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;

import javax.websocket.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ClientEndpoint
public abstract class AbstractServerManager implements ServerManagerImpl {

    @Getter
    private List<StreamlineServer> subscribedServers = new ArrayList<>();

    @Getter
    private List<String> subscribedStartingServers = new ArrayList<>();

    @Getter
    public List<UUID> quittingPlayers = new ArrayList<>();

    @Getter
    WebSocket socket;

    public void init() {

        try {
            URI uri = URI.create("ws://localhost:5378/socket/server?key=" + StaticCache.accessKey);
            HttpClient client = HttpClient.newHttpClient();
            WebSocket webSocket = client.newWebSocketBuilder().buildAsync(uri, new WebSocketListener(this)).join();

            this.socket = webSocket;
            uploadServerInfo();

            webSocket.sendText("iam:" + getLocalServerInfo().getUuid(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadServerInfo();
    }

    public void subscribe(StreamlineServer server) {
        for (StreamlineServer subscribedServer : subscribedServers) {
            if (subscribedServer.getUuid().equals(server.getUuid())) return;
        }

        subscribedServers.add(server);
        socket.sendText("subscribe:server:" + server.getName(), true);
    }

    public void subscribeToGroup(String name) {
        if (subscribedStartingServers.contains(name)) return;

        subscribedStartingServers.add(name);
        socket.sendText("subscribe:starting:" + name, true);
    }

    public void uploadServerInfo() {
        StreamlineServer s = getServer(UUID.fromString(StaticCache.serverData.getUuid()));
        s.setOnlinePlayers(getLocalServerInfo().getOnlinePlayers());
        s.setMaxOnlineCount(getLocalServerInfo().getMaxOnlineCount());
        s.setServerState(getLocalServerInfo().getServerState());
        s.setServerUseState(getLocalServerInfo().getServerUseState());

        Functions.post(s, "servers/update");
    }


    public StreamlineServer getServer(UUID uuid) {
        for (StreamlineServer s : subscribedServers) {
            if (s != null) if (s.getUuid().equals(uuid.toString())) return s;
        }

        return new Gson().fromJson(Functions.get("servers/" + uuid.toString()), StreamlineServer.class);
    }

    public StreamlineServer getServer(String name) {
        for (StreamlineServer s : subscribedServers) {
            if (s.getName().equals(name)) return s;
        }

        return new Gson().fromJson(Functions.get("servers/name/" + name), StreamlineServer.class);
    }
}
