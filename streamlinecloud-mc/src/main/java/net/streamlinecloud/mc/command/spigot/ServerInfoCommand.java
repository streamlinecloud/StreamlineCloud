package net.streamlinecloud.mc.command.spigot;

import net.streamlinecloud.mc.utils.InternalSettings;
import net.streamlinecloud.mc.utils.StaticCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ServerInfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        sender.sendMessage(InternalSettings.getPrefix());
        sender.sendMessage(InternalSettings.getPrefix() + "§7Server: §e" + StaticCache.serverData.getName());
        sender.sendMessage(InternalSettings.getPrefix() + "Adress: §e" + StaticCache.serverData.getIp() + ":" + StaticCache.serverData.getPort());
        sender.sendMessage(InternalSettings.getPrefix() + "§7Group: §e" + StaticCache.serverData.getGroup());
        sender.sendMessage(InternalSettings.getPrefix() + "§7UUID: §e" + StaticCache.serverData.getUuid());
        sender.sendMessage(InternalSettings.getPrefix());
        return true;
    }
}
