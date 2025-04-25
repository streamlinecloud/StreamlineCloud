package net.streamlinecloud.mc.paper.command;

import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.paper.manager.ServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        AbstractServerManager serverManager = ServerManager.getInstance();
        sender.sendMessage("Subscribed servers:");
        for (StreamlineServer server : serverManager.getSubscribedServers()) {
            sender.sendMessage(server.getName() + " - " + server.getGroup() + " - " + server.getOnlinePlayers().size() + "/" + server.getMaxOnlineCount() + " (" + server.getUuid() + ")");
        }
        return true;
    }
}