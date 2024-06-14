package io.streamlinemc.mc.api.player;

import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.mc.SpigotSCP;
import io.streamlinemc.mc.utils.StaticCache;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerManager {

    @Getter
    private static PlayerManager instance;

    HashMap<UUID, String> playersMap = new HashMap<>();
    List<CloudPlayer> onlinePlayers = new ArrayList<>();

    public PlayerManager() {
        instance = this;
    }

    public void sendPlayer(CloudPlayer player, StreamlineServer server) {
        /*ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server.getName());
        Player p = player.getPlayer();
        p.sendPluginMessage(SpigotSCP.getInstance(), "BungeeCord", out.toByteArray());*/

        if (server.getMaxOnlineCount() == -1) {
            player.getPlayer().sendMessage("§cServer is starting!");
            return;
        }

        if (server.getName().equals(StaticCache.serverData.getName())) {
            player.getPlayer().sendMessage("§cAlready connected!");
            return;
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server.getName());
        } catch (IOException e) {
            Bukkit.getLogger().info("FAILED TO SEND PLAYER " + player.getPlayer().getName() + "(" + player.getPlayer().getUniqueId() + ") send to Server... FOLLOWING ERROR OCCURRED: \n" + e.getMessage());
        }
        player.getPlayer().sendPluginMessage(SpigotSCP.getInstance(), "BungeeCord", b.toByteArray());
    }

    //GetPlayer
    public CloudPlayer getPlayer(String name) {
        for (CloudPlayer p : onlinePlayers) {
            if (p.getPlayer().getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
}
