package net.streamlinecloud.mc.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.streamlinecloud.mc.VelocitySCP;
import net.streamlinecloud.mc.common.core.manager.LangManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ServerCommand {

    private final BrigadierCommand command;

    public ServerCommand(ProxyServer proxy) {
        LiteralCommandNode<CommandSource> node = BrigadierCommand
                .literalArgumentBuilder("server")
                .then(
                        BrigadierCommand.requiredArgumentBuilder("target", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    getServerSuggestions(proxy)
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(this::execute)
                )
                .build();

        this.command = new BrigadierCommand(node);
    }

    private int execute(CommandContext<CommandSource> ctx) {
        CommandSource source = ctx.getSource();
        
        // Check if source is a player
        if (!(source instanceof Player)) {
            return 0;
        }
        
        Player player = (Player) source;
        String prefix = LangManager.getInstance().get("sc.mc.prefix");
        
        // Check permission
        if (!player.hasPermission(VelocitySCP.getInstance().getConfigManager().getConfig().getPermissions().getSwitchServer())) {
            player.sendMessage(Component.text(prefix + LangManager.getInstance().get("sc.mc.noPermission")));
            return 0;
        }
        
        String target = ctx.getArgument("target", String.class);
        player.sendMessage(Component.text(prefix + LangManager.getInstance().get("sc.mc.connectingTo").replace("%0", target)));

        List<RegisteredServer> matches = new ArrayList<>();

        for (RegisteredServer server : VelocitySCP.getInstance().getProxy().getAllServers()) {
            if (server.getServerInfo().getName().startsWith(target)) matches.add(server);
        }

        if (matches.isEmpty()) {
            player.sendMessage(Component.text(prefix + LangManager.getInstance().get("sc.mc.serverDoesNotExist")));
            return 0;
        }

        if (matches.size() > 1) {
            player.sendMessage(Component.text(prefix + "The following servers match your request. Please specify a specific one"));
            matches.forEach(match -> player.sendMessage(Component.text("- " + match.getServerInfo().getName())));
            return 0;
        }

        Optional<RegisteredServer> toConnect = matches.stream().findFirst();

        if (player.getCurrentServer().get().getServer().getServerInfo().getName().equals(toConnect.get().getServerInfo().getName())) {
            player.sendMessage(Component.text(prefix + LangManager.getInstance().get("sc.mc.alreadyConnected").replace("%0", toConnect.get().getServerInfo().getName())));
            return 0;
        }
        player.createConnectionRequest(toConnect.get()).fireAndForget();

        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    private List<String> getServerSuggestions(ProxyServer proxy) {
        return proxy.getAllServers().stream()
                .map(server -> {
                    List<String> s = new ArrayList<>(List.of(server.getServerInfo().getName().split("-")));
                    s.remove(s.size() - 1);

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < s.size(); i++) {
                        builder.append(s.get(i));
                        if (i != s.size() -1)builder.append("-");
                    }

                    return builder.toString();
                })
                .toList();
    }


}
