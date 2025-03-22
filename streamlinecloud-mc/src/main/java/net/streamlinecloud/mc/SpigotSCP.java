package net.streamlinecloud.mc;

import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.api.StreamlineCloud;
import net.streamlinecloud.mc.command.spigot.ConnectCommand;
import net.streamlinecloud.mc.command.spigot.ServerInfoCommand;
import net.streamlinecloud.mc.command.spigot.StreamlineCommand;
import net.streamlinecloud.mc.command.spigot.TestCommand;
import net.streamlinecloud.mc.listener.spigot.ConnectionListener;
import net.streamlinecloud.mc.utils.Functions;
import net.streamlinecloud.mc.utils.StaticCache;
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
        streamlineCloud = new StreamlineCloud();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        registerEvents();
        registerCommand();

        Functions.startup();

        this.getLogger().info("plugin enabled");
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
