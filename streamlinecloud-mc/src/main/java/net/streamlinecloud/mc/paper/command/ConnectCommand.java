package net.streamlinecloud.mc.paper.command;

import net.streamlinecloud.mc.paper.manager.PaperPlayerManager;
import net.streamlinecloud.mc.paper.manager.PaperServerManager;
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
            PaperPlayerManager.getInstance().sendPlayer(PaperPlayerManager.getInstance().getPlayer(sender.getName()), PaperServerManager.getInstance().getServerByName(args[0]));
        }
        return true;
    }
}
