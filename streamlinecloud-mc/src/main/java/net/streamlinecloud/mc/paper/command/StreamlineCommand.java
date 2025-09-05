package net.streamlinecloud.mc.paper.command;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.RemoteCommandPacket;
import net.streamlinecloud.mc.PaperSCP;
import net.streamlinecloud.mc.common.core.PluginConfig;
import net.streamlinecloud.mc.common.core.manager.LangManager;
import net.streamlinecloud.mc.common.utils.BackendRequest;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

public class StreamlineCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;
        String prefix = LangManager.getInstance().get("sl.mc.prefix");
        PluginConfig config = PaperSCP.getInstance().getConfigManager().getConfig();

        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage("§8| §7This server is powered by");
            player.sendMessage("§8| §c§lStreamline§b§lCloud");
            player.sendMessage("§8| §7Visit streamlinecloud.net for more information");
            player.sendMessage("");
            return true;
        }

        if (!player.hasPermission(config.getPermissions().getServerInfo())) {
            player.sendMessage(prefix + LangManager.getInstance().get("sl.mc.notAllowed"));
            return false;
        }

        if (args[0].equals("cmd") || args[0].equals("command")) {

            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                if (arg.equals(args[0])) continue;
                builder.append(arg);
                builder.append(" ");
            }

            RemoteCommandPacket packet = new RemoteCommandPacket(builder.toString(), StaticCache.serverData.getName(), player.getName());
            player.sendMessage(packet.getCommand());

            new BackendRequest("command").setType(BackendRequest.RestType.POST).withBody(new Gson().toJson(packet)).fetch();

        } else if (args[0].equals("serverinfo")) {

            sender.sendMessage(prefix);
            sender.sendMessage(prefix + "§7Name: §e" + StaticCache.serverData.getName());
            sender.sendMessage(prefix + "§7Online: §e" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            sender.sendMessage(prefix + "§7Uptime: §e" + ManagementFactory.getRuntimeMXBean().getUptime() / 1000 / 60 + "m");
            sender.sendMessage(prefix + "§7Group: §e" + StaticCache.serverData.getGroup());
            sender.sendMessage(prefix + "§7ShortUUID: §e" + StaticCache.serverData.getUuid().split("-")[0]);
            if (StaticCache.serverData.getStopTime() != -1) {
                int minutes = (int) (StaticCache.serverData.getStopTime() - System.currentTimeMillis()) / (1000 * 60);
                sender.sendMessage(prefix + "§cStops in " + (minutes == 0 ? "under one minute" : minutes + " minutes"));
            }
            sender.sendMessage(prefix);
            return true;
        }

        return true;
    }
}
