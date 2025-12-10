package net.streamlinecloud.mc;

import net.streamlinecloud.api.packet.StaticServerDataPacket;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.common.core.StreamlineCloud;
import net.streamlinecloud.mc.common.core.manager.LangManager;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;
import net.streamlinecloud.mc.paper.command.ConnectCommand;
import net.streamlinecloud.mc.paper.command.StreamlineCommand;
import net.streamlinecloud.mc.paper.listener.ConnectionListener;
import net.streamlinecloud.mc.paper.listener.ServerListener;
import net.streamlinecloud.mc.paper.manager.PaperPlayerManager;
import net.streamlinecloud.mc.paper.manager.PaperServerManager;
import net.streamlinecloud.mc.paper.task.StopCountdownTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.WebSocket;
import java.util.Objects;

@Getter
public final class PaperSCP extends JavaPlugin {

    StreamlineCloud streamlineCloud;
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

        new LangManager();
        LangManager.getInstance().fetch(new String[]{
                "sc.mc.prefix",
                "sc.mc.notAllowed"});

        new PaperServerManager();
        new PaperPlayerManager();

        new StopCountdownTask();

        this.getLogger().info("plugin enabled");
    }

    @Override
    public void onDisable() {
        PaperServerManager.getInstance().getSocket().sendClose(WebSocket.NORMAL_CLOSURE, "PluginShutdown");
    }

    private void registerCommand() {
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
