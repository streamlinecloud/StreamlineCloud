package net.streamlinecloud.mc.velocity.manager;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.VelocitySCP;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.common.utils.StaticCache;

import java.util.HashMap;
import java.util.UUID;

public class ProxyServerManager extends AbstractServerManager {

    @Getter
    private static AbstractServerManager instance;

    public ProxyServerManager() {
        instance = this;
        init();
    }

    @Override
    public void moveAllPlayersAndStop(String target) {

    }

    @Override
    public void onSubscribedServerUpdated(StreamlineServer server) {

    }

    @Override
    public void onSubscribedServerStarted(StreamlineServer server) {

    }

    @Override
    public void onSubscribedServerStopped(StreamlineServer server) {

    }

    @Override
    public StreamlineServer getLocalServerInfo() {
        StreamlineServer server = new StreamlineServer();
        server.setName(StaticCache.serverData.getName());
        server.setUuid(StaticCache.serverData.getUuid());
        server.setIp(StaticCache.serverData.getIp());
        server.setPort(StaticCache.serverData.getPort());
        server.setMaxOnlineCount(VelocitySCP.getInstance().getProxy().getConfiguration().getShowMaxPlayers());
        server.setOnlinePlayers(getPlayersMap());
        server.setServerUseState(ServerUseState.UNKNOWN);
        server.setServerState(ServerState.ONLINE);
        server.setGroup(StaticCache.serverData.getGroup());
        server.setRuntime(ServerRuntime.SERVER);
        return server;
    }

    public HashMap<UUID, String> getPlayersMap() {
        HashMap<UUID, String> players = new HashMap<>();
        for (Player player : VelocitySCP.getInstance().getProxy().getAllPlayers()) {
            players.put(player.getUniqueId(), player.getClientBrand());
        }
        return players;
    }
}
