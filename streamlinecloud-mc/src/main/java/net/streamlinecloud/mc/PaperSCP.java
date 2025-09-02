package net.streamlinecloud.mc;

import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.common.core.StreamlineCloud;
import net.streamlinecloud.mc.common.core.manager.ConfigManager;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;
import net.streamlinecloud.mc.paper.command.ConnectCommand;
import net.streamlinecloud.mc.paper.command.ServerInfoCommand;
import net.streamlinecloud.mc.paper.command.StreamlineCommand;
import net.streamlinecloud.mc.paper.listener.ConnectionListener;
import net.streamlinecloud.mc.paper.listener.ServerListener;
import net.streamlinecloud.mc.paper.manager.PlayerManager;
import net.streamlinecloud.mc.paper.manager.ServerManager;
import net.streamlinecloud.mc.paper.task.StopCountdownTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.WebSocket;
import java.util.Objects;

@Getter
public final class PaperSCP extends JavaPlugin {

    StreamlineCloud streamlineCloud;
    ConfigManager configManager;
    boolean debug = true;

    @Getter
    private static PaperSCP instance;

    @Override
    public void onEnable() {
        StaticCache.setRuntime(ServerRuntime.SERVER);

        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        registerEvents();
        registerCommand();

        Functions.startup();

        configManager = new ConfigManager(getDataFolder());

        new ServerManager();
        new PlayerManager();

        new StopCountdownTask();

        this.getLogger().info("plugin enabled");
    }

    @Override
    public void onDisable() {
        ServerManager.getInstance().getSocket().sendClose(WebSocket.NORMAL_CLOSURE, "PluginShutdown");
    }

    private void registerCommand() {
        Objects.requireNonNull(getCommand("serverinfo")).setExecutor(new ServerInfoCommand());
        Objects.requireNonNull(getCommand("connect")).setExecutor(new ConnectCommand());
        Objects.requireNonNull(getCommand("streamline")).setExecutor(new StreamlineCommand());
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ConnectionListener(), this);
        manager.registerEvents(new ServerListener(), this);
    }

    public StaticServerDataPacket getServerData() {
        return StaticCache.serverData;
    }

    public void debug(String message) {
        if (debug) getLogger().info(message);
    }

}
