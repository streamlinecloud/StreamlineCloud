package net.streamlinecloud.mc.paper.listener;

import net.streamlinecloud.mc.paper.StreamlinePlayer;
import net.streamlinecloud.mc.paper.manager.PaperPlayerManager;
import net.streamlinecloud.mc.paper.manager.PaperServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        PaperPlayerManager manager = PaperPlayerManager.getInstance();

        manager.getOnlinePlayers().add(new StreamlinePlayer(e.getPlayer()));
        manager.getPlayersMap().put(e.getPlayer().getUniqueId(), e.getPlayer().getName());

        PaperServerManager.getInstance().uploadServerInfo();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PaperPlayerManager manager = PaperPlayerManager.getInstance();

        manager.getOnlinePlayers().remove(manager.getPlayer(e.getPlayer().getName()));
        manager.getPlayersMap().remove(e.getPlayer().getUniqueId());

        PaperServerManager.getInstance().getQuittingPlayers().add(e.getPlayer().getUniqueId());
        PaperServerManager.getInstance().uploadServerInfo();
    }
}
