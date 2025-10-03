package net.streamlinecloud.mc.paper.manager;

import lombok.Getter;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.ServerUseState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.PaperSCP;
import net.streamlinecloud.mc.common.server.AbstractServerManager;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.paper.event.ServerDataReceivedEvent;
import net.streamlinecloud.mc.paper.event.ServerDataUpdateEvent;
import net.streamlinecloud.mc.paper.event.ServerDeletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PaperServerManager extends AbstractServerManager {

    @Getter
    private static AbstractServerManager instance;

    public PaperServerManager() {
        instance = this;
        init();
    }

    @Override
    public void moveAllPlayersAndStop(String target) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(PaperSCP.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : PaperSCP.getInstance().getServer().getOnlinePlayers()) {
                    PaperPlayerManager.getInstance().sendPlayer(PaperPlayerManager.getInstance().getPlayer(player.getName()), PaperServerManager.getInstance().getServerByUuid(target));
                }
            }
        }, 200L);

        Bukkit.getScheduler().runTaskLaterAsynchronously(PaperSCP.getInstance(), new Runnable() {
            @Override
            public void run() {
                PaperSCP.getInstance().getServer().shutdown();
            }
        }, 300L);
    }

    @Override
    public void log(String message) {
        PaperSCP.getInstance().getLogger().info(message);
    }

    @Override
    public void sendMessageToPlayer(UUID playerUuid, String message) {
        Objects.requireNonNull(Bukkit.getPlayer(playerUuid)).sendMessage(message);
    }

    @Override
    public void connectPlayerToServer(UUID playerUuid, String serverId) {
        PaperPlayerManager.getInstance().sendPlayer(PaperSCP.getInstance().getServer().getPlayer(playerUuid), PaperServerManager.getInstance().getServerByUuid(serverId));
    }

    @Override
    public void kickPlayer(UUID playerUuid, String reason) {
        Bukkit.getPlayer(playerUuid).kickPlayer(reason);
    }

    @Override
    public void closeServer(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(message);
        });

        Bukkit.getScheduler().runTaskLaterAsynchronously(PaperSCP.getInstance(), new Runnable() {
            @Override
            public void run() {
                PaperSCP.getInstance().getServer().shutdown();
            }
        }, 100L);
    }

    @Override
    public void onSubscribedServerUpdated(StreamlineServer server) {
        Bukkit.getScheduler().runTask(PaperSCP.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new ServerDataUpdateEvent(server));
        });
    }

    @Override
    public void onSubscribedServerStarted(StreamlineServer server) {
        Bukkit.getScheduler().runTask(PaperSCP.getInstance(), () -> {
            PaperSCP.getInstance().getServer().getPluginManager().callEvent(new ServerDataReceivedEvent(server));
        });
    }

    @Override
    public void onSubscribedServerStopped(StreamlineServer server) {
        Bukkit.getScheduler().runTask(PaperSCP.getInstance(), () -> {
            PaperSCP.getInstance().getServer().getPluginManager().callEvent(new ServerDeletedEvent(server));
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
