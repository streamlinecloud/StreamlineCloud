package net.streamlinecloud.mc.listener.spigot;

import net.streamlinecloud.mc.api.player.CloudPlayer;
import net.streamlinecloud.mc.api.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerManager manager = PlayerManager.getInstance();

        manager.getOnlinePlayers().add(new CloudPlayer(e.getPlayer()));
        manager.getPlayersMap().put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent e) {
        PlayerManager manager = PlayerManager.getInstance();

        manager.getOnlinePlayers().remove(manager.getPlayer(e.getPlayer().getName()));
        manager.getPlayersMap().remove(e.getPlayer().getUniqueId());
    }
}
