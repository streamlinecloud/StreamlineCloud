package io.streamlinemc.mc.command.spigot;

import io.streamlinemc.mc.utils.Functions;
import io.streamlinemc.mc.utils.InternalSettings;
import io.streamlinemc.mc.utils.StaticCache;
import io.streamlinemc.mc.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StreamlineCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;

        if (args[0].equals("cmd") || args[0].equals("command")) {

            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                if (arg.equals(args[0])) continue;
                builder.append(arg);
                builder.append(" ");
            }

            player.sendMessage(builder.toString());
            Functions.post(builder.toString(), "post/command");

        }

        return true;
    }
}
