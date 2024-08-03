package io.streamlinemc.mc;

import io.streamlinemc.api.packet.StaticServerDataPacket;
import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.mc.api.StreamlineCloud;
import io.streamlinemc.mc.command.spigot.ConnectCommand;
import io.streamlinemc.mc.command.spigot.ServerInfoCommand;
import io.streamlinemc.mc.command.spigot.StreamlineCommand;
import io.streamlinemc.mc.command.spigot.TestCommand;
import io.streamlinemc.mc.listener.spigot.ConnectionListener;
import io.streamlinemc.mc.utils.Functions;
import io.streamlinemc.mc.utils.StaticCache;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotSCP extends JavaPlugin {

    StreamlineCloud streamlineCloud;

    @Getter
    private static SpigotSCP instance;

    @Override
    public void onEnable() {
        StaticCache.setRuntime(ServerRuntime.SERVER);
        instance = this;
        this.getLogger().info("streamline enabled");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Functions.startup();

        streamlineCloud = new StreamlineCloud();

        registerEvents();
        registerCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommand() {
        getCommand("test").setExecutor(new TestCommand());
        getCommand("serverinfo").setExecutor(new ServerInfoCommand());
        getCommand("connect").setExecutor(new ConnectCommand());
        getCommand("streamline").setExecutor(new StreamlineCommand());
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ConnectionListener(), this);
    }

    public StaticServerDataPacket getServerData() {
        return StaticCache.serverData;
    }

}
