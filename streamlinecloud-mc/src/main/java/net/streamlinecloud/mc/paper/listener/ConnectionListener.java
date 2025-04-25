package net.streamlinecloud.mc.paper.listener;

import net.streamlinecloud.mc.paper.StreamlinePlayer;
import net.streamlinecloud.mc.paper.manager.PlayerManager;
import net.streamlinecloud.mc.paper.manager.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerManager manager = PlayerManager.getInstance();

        manager.getOnlinePlayers().add(new StreamlinePlayer(e.getPlayer()));
        manager.getPlayersMap().put(e.getPlayer().getUniqueId(), e.getPlayer().getName());

        ServerManager.getInstance().uploadServerInfo();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerManager manager = PlayerManager.getInstance();

        manager.getOnlinePlayers().remove(manager.getPlayer(e.getPlayer().getName()));
        manager.getPlayersMap().remove(e.getPlayer().getUniqueId());

        ServerManager.getInstance().getQuittingPlayers().add(e.getPlayer().getUniqueId());
        ServerManager.getInstance().uploadServerInfo();
    }
}
