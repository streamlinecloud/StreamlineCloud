package net.streamlinecloud.mc.paper.command;

import net.streamlinecloud.mc.paper.manager.PlayerManager;
import net.streamlinecloud.mc.common.core.manager.AbstractServerManager;
import net.streamlinecloud.mc.paper.manager.ServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConnectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) return false;
            PlayerManager.getInstance().sendPlayer(PlayerManager.getInstance().getPlayer(sender.getName()), ServerManager.getInstance().getServer(args[0]));
        }
        return true;
    }
}
