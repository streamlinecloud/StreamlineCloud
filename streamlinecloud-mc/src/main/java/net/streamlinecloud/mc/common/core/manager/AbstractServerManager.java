package net.streamlinecloud.mc.common.core.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.socket.SocketMessage;
import net.streamlinecloud.mc.common.core.WebSocketListener;
import net.streamlinecloud.mc.common.server.RunningServer;
import net.streamlinecloud.mc.common.utils.BackendRequest;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;
import net.streamlinecloud.mc.common.utils.Utils;

import javax.websocket.*;
import java.lang.reflect.Type;
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

    int reInits = 0;

    @Getter
    WebSocket socket;

    public void init() {

        try {
            URI uri = URI.create("ws://localhost:5378/socket/server?key=" + StaticCache.accessKey);
            HttpClient client = HttpClient.newHttpClient();
            WebSocket webSocket = client.newWebSocketBuilder().buildAsync(uri, new WebSocketListener(this)).join();

            this.socket = webSocket;
            uploadServerInfo();

            webSocket.sendText(new SocketMessage(SocketMessage.SocketMessageType.IAM, getLocalServerInfo().getUuid()).toString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.serverManager = this;

        uploadServerInfo();
    }

    public void reinit() {
        reInits++;

        if (reInits == 5) {
            closeServer("The server lost connection to the StreamlineCloud backend.");
            return;
        }

        init();
    }

    public void subscribe(StreamlineServer server) {
        for (StreamlineServer subscribedServer : subscribedServers) {
            if (subscribedServer.getUuid().equals(server.getUuid())) return;
        }

        subscribedServers.add(server);
        socket.sendText(new SocketMessage(SocketMessage.SocketMessageType.SUBSCRIBE_SERVER, server.getName().toLowerCase()).toString(), true);
    }

    public void subscribeToGroup(String name) {
        if (subscribedStartingServers.contains(name)) return;

        subscribedStartingServers.add(name);
        socket.sendText(new SocketMessage(SocketMessage.SocketMessageType.SUBSCRIBE_GROUP, name).toString(), true);
    }

    public void uploadServerInfo() {
        StreamlineServer s = getServerByUuid(StaticCache.serverData.getUuid());
        s.setOnlinePlayers(getLocalServerInfo().getOnlinePlayers());
        s.setMaxOnlineCount(getLocalServerInfo().getMaxOnlineCount());
        s.setServerState(getLocalServerInfo().getServerState());
        s.setServerUseState(getLocalServerInfo().getServerUseState());

        new BackendRequest("servers/update").setType(BackendRequest.RestType.POST).withBody(new Gson().toJson(s)).fetch();
    }


    public RunningServer getServerByUuid(String uuid) {
        for (StreamlineServer s : subscribedServers) {
            if (s != null) if (s.getUuid().equals(uuid) || (s.getName() + "_" + s.getUuid()).equals(uuid)) return new RunningServer(s);
        }

        return new RunningServer(new Gson().fromJson(new BackendRequest("servers/" + uuid).fetch().getResponse(), StreamlineServer.class));
    }

    public RunningServer getServerByName(String name) {
        for (StreamlineServer s : subscribedServers) {
            if (s.getName().equals(name)) return new RunningServer(s);
        }

        return new RunningServer(new Gson().fromJson(new BackendRequest("servers/name/" + name).fetch().getResponse(), StreamlineServer.class));
    }

    @Override
    public StreamlineServer[] getServersByGroup(String groupName) {
        String res = new BackendRequest("groups/" + groupName + "/servers").fetch().getResponse();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<StreamlineServer>>() {}.getType();
        List<StreamlineServer> servers = gson.fromJson(res, listType);
        StreamlineServer[] array = servers.toArray(new StreamlineServer[0]);
        return array;
    }
}
