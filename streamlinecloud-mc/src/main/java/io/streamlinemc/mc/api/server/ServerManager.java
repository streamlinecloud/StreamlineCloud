package io.streamlinemc.mc.api.server;

import com.google.gson.Gson;
import io.streamlinemc.api.group.StreamlineGroup;
import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.mc.SpigotSCP;
import io.streamlinemc.mc.api.group.GroupManager;
import io.streamlinemc.mc.api.player.PlayerManager;
import io.streamlinemc.mc.utils.Functions;
import io.streamlinemc.mc.utils.StaticCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.checkerframework.framework.qual.Unused;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.meta.Exclusive;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerManager {

    @Getter
    private static ServerManager instance;

    @Getter
    List<StreamlineServer> onlineServers = new ArrayList<>();

    @Getter
    HashMap<UUID, Instant> updatedServers = new HashMap<>();

    public ServerManager() {
        instance = this;
        startTask();
    }

    private void updateServers() {
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
    }

    /**
     * Updates a server with newer Information
     * @param ser - StreamlineServer to update(DO NOT OVERRIDE UUID)
     * @notused - Internal use only
     */
    @ApiStatus.Internal
    public void updateServer(StreamlineServer ser) {

        try {

            if (ser.getUuid().equals(StaticCache.serverData.getUuid())) return;

            StreamlineServer lineServer = new Gson().fromJson(Functions.get("get/serverdata/" + ser.getUuid()), StreamlineServer.class);

            StreamlineServer s = getServerIntern(ser.getName());

            s.setOnlinePlayers(lineServer.getOnlinePlayers());
            s.setServerState(lineServer.getServerState());
            s.setServerUseState(lineServer.getServerUseState());
            s.setMaxOnlineCount(lineServer.getMaxOnlineCount());

        } catch (Exception e) {
            System.out.println("errorrr:" + e.getMessage());
        }
    }

    private void checkServerUpdate(StreamlineServer s) {

        if (!updatedServers.containsKey(UUID.fromString(s.getUuid()))) {
            updatedServers.put(UUID.fromString(s.getUuid()), Instant.now());
        }

        Instant current = Instant.now();
        Duration dif = Duration.between(updatedServers.get(UUID.fromString(s.getUuid())), current);

        if (dif.getSeconds() >= 3) {
            updateServer(s);
            updatedServers.remove(UUID.fromString(s.getUuid()));
            updatedServers.put(UUID.fromString(s.getUuid()), Instant.now());
        }
    }

    private void startTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {
            updateServers();
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }

    //ServerExists
    private boolean serverExists(String uuid) {
        for (StreamlineServer server : onlineServers) {
            if (server.getUuid().toString().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    //GetServer

    /**
     * Get all servers by group name
     * @param group String name of the group
     * @return List of StreamlineServers
     */
    public List<StreamlineServer> getServersByGroup(String group) {
        return getServersByGroup(GroupManager.getInstance().getGroup(group));
    }
    public List<StreamlineServer> getServersByGroup(StreamlineGroup group) {
        List<StreamlineServer> servers = new ArrayList<>();
        List<StreamlineServer> serversToUpdate = new ArrayList<>();
        for (StreamlineServer s : onlineServers) {
            if (s.getGroup().equals(group.getName())) {
                serversToUpdate.add(s);
                servers.add(s);
            }
        }
        for (StreamlineServer server : serversToUpdate) checkServerUpdate(server);
        return servers;
    }

    /**
     * Get a server by name
     * @param name String name of the server
     * @return StreamlineServer or null
     */
    public StreamlineServer getServer(String name) {
        for (StreamlineServer s : onlineServers) {
            if (s.getName().equals(name)) {
                checkServerUpdate(s);
                return s;
            }
        }
        return null;
    }

    /**
     * Get a server by UUID
     * @param uuid UUID of the server
     * @return StreamlineServer or null
     */
    public StreamlineServer getServer(UUID uuid) {
        for (StreamlineServer s : onlineServers) {
            if (s.getUuid().equals(uuid.toString())) {
                checkServerUpdate(s);
                return s;
            }
        }
        return null;
    }

    private StreamlineServer getServerIntern(String name) {
        for (StreamlineServer s : onlineServers) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}
