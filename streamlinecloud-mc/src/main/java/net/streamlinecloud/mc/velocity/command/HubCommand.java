package net.streamlinecloud.mc.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.streamlinecloud.mc.velocity.ProxyFallbackHandler;

import java.util.Optional;

public class HubCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        Optional<RegisteredServer> server = ProxyFallbackHandler.getInstance().searchFallback();
        Player player = (Player) invocation.source();
        player.createConnectionRequest(server.get()).fireAndForget();
    }
}
