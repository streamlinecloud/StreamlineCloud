package io.streamlinemc.mc.command.spigot;

import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.mc.api.server.ServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ServerManager serverManager = ServerManager.getInstance();
        sender.sendMessage("Servers:");
        for (StreamlineServer server : serverManager.getOnlineServers()) {
            sender.sendMessage(server.getName() + " - " + server.getGroup() + " - " + server.getOnlinePlayers().size() + "/" + server.getMaxOnlineCount() + " (" + server.getUuid() + ")");
        }
        return true;
    }
}