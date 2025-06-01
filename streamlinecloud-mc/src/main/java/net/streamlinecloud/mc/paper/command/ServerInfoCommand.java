package net.streamlinecloud.mc.paper.command;

import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.common.core.PluginConfig;
import net.streamlinecloud.mc.common.utils.InternalSettings;
import net.streamlinecloud.mc.common.utils.StaticCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

public class ServerInfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        PluginConfig config = SpigotSCP.getInstance().getConfigManager().getConfig();

        if (sender instanceof Player player) {

            if (!player.hasPermission(config.getPermissions().getServerInfo())) {
                player.sendMessage(config.getPrefix() + "§cYou don't have permission to use this command!");
                return false;
            }
        }

        sender.sendMessage(config.getPrefix());
        sender.sendMessage(config.getPrefix() + "§7Name: §e" + StaticCache.serverData.getName());
        sender.sendMessage(config.getPrefix() + "§7Online: §e" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        sender.sendMessage(config.getPrefix() + "§7Uptime: §e" + ManagementFactory.getRuntimeMXBean().getUptime() / 1000 / 60 + "m");
        sender.sendMessage(config.getPrefix() + "§7Group: §e" + StaticCache.serverData.getGroup());
        sender.sendMessage(config.getPrefix() + "§7ShortUUID: §e" + StaticCache.serverData.getUuid().split("-")[0]);
        if (StaticCache.serverData.getStopTime() != -1) {
            int minutes = (int) (StaticCache.serverData.getStopTime() - System.currentTimeMillis()) / (1000 * 60);
            sender.sendMessage(config.getPrefix() + "§cStops in " + (minutes == 0 ? "under one minute" : minutes + " minutes"));
        }
        sender.sendMessage(config.getPrefix());
        return true;
    }
}
