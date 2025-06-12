package net.streamlinecloud.mc.paper.manager;

import lombok.Getter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.paper.event.ServerDataReceivedEvent;
import net.streamlinecloud.mc.paper.event.ServerDataUpdateEvent;
import net.streamlinecloud.mc.paper.event.ServerDeletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerManager extends AbstractServerManager {

    @Getter
    private static AbstractServerManager instance;

    public ServerManager() {
        instance = this;
        init();
    }

    @Override
    public void moveAllPlayersAndStop(String target) {
        for (Player player : SpigotSCP.getInstance().getServer().getOnlinePlayers()) {
            PlayerManager.getInstance().sendPlayer(PlayerManager.getInstance().getPlayer(player.getName()), ServerManager.getInstance().getServer(target));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotSCP.getInstance(), new Runnable() {
            @Override
            public void run() {
                SpigotSCP.getInstance().getServer().shutdown();
            }
        }, 200L);
    }

    @Override
    public void onSubscribedServerUpdated(StreamlineServer server) {
        Bukkit.getScheduler().runTask(SpigotSCP.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new ServerDataUpdateEvent(server));
        });
    }

    @Override
    public void onSubscribedServerStarted(StreamlineServer server) {
        Bukkit.getScheduler().runTask(SpigotSCP.getInstance(), () -> {
            SpigotSCP.getInstance().getServer().getPluginManager().callEvent(new ServerDataReceivedEvent(server));
        });
    }

    @Override
    public void onSubscribedServerStopped(StreamlineServer server) {
        Bukkit.getScheduler().runTask(SpigotSCP.getInstance(), () -> {
            SpigotSCP.getInstance().getServer().getPluginManager().callEvent(new ServerDeletedEvent(server));
        });
    }

    @Override
    public StreamlineServer getLocalServerInfo() {
        StreamlineServer server = new StreamlineServer();
        server.setName(StaticCache.serverData.getName());
        server.setUuid(StaticCache.serverData.getUuid());
        server.setIp(StaticCache.serverData.getIp());
        server.setPort(StaticCache.serverData.getPort());
        server.setMaxOnlineCount(Bukkit.getMaxPlayers());
        server.setOnlinePlayers(getPlayersMap());
        server.setServerUseState(ServerUseState.UNKNOWN);
        server.setServerState(ServerState.ONLINE);
        server.setGroup(StaticCache.serverData.getGroup());
        server.setRuntime(ServerRuntime.SERVER);
        return server;
    }

    public HashMap<UUID, String> getPlayersMap() {
        HashMap<UUID, String> players = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!quittingPlayers.contains(player.getUniqueId()))
                players.put(player.getUniqueId(), player.getName());
            quittingPlayers.removeIf(player.getUniqueId()::equals);
        }
        return players;
    }

}
