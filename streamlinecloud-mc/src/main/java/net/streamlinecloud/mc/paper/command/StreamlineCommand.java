package net.streamlinecloud.mc.paper.command;

import net.streamlinecloud.api.packet.RemoteCommandPacket;
import net.streamlinecloud.mc.SpigotSCP;
import net.streamlinecloud.mc.common.core.PluginConfig;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StreamlineCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;
        PluginConfig config = SpigotSCP.getInstance().getConfigManager().getConfig();

        if (args[0].equals("cmd") || args[0].equals("command")) {

            if (!player.hasPermission(config.getPermissions().getRemoteCLI())) {
                player.sendMessage(config.getPrefix() + "§cYou don't have permission to use this command!");
                return false;
            }

            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                if (arg.equals(args[0])) continue;
                builder.append(arg);
                builder.append(" ");
            }

            RemoteCommandPacket packet = new RemoteCommandPacket(builder.toString(), StaticCache.serverData.getName(), player.getName());
            player.sendMessage(packet.getCommand());

            Functions.post(packet, "command");

        }

        return true;
    }
}
