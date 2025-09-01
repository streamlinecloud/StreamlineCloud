package net.streamlinecloud.mc.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import net.streamlinecloud.mc.VelocitySCP;

public class RefreshWhitelistCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {

        CommandSource source = invocation.source();

        if (source instanceof ConsoleCommandSource) {
            VelocitySCP.getInstance().refreshWhitelist();
            source.sendMessage(Component.text("Whitelist has been refreshed."));
        }
    }

}
