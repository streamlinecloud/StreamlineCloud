package net.streamlinecloud.mc;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.common.core.StreamlineCloud;
import net.streamlinecloud.mc.common.core.manager.LangManager;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;
import net.streamlinecloud.mc.velocity.ProxyFallbackHandler;
import net.streamlinecloud.mc.velocity.command.HubCommand;
import net.streamlinecloud.mc.velocity.command.ServerCommand;
import net.streamlinecloud.mc.velocity.listener.ProxyConnectionListener;
import net.streamlinecloud.mc.velocity.manager.VelocityGroupManager;
import net.streamlinecloud.mc.velocity.manager.VelocityServerManager;

import java.util.logging.Logger;

@Getter
@Plugin(id = "streamlinecloud",
        name = "Streamlinecloud",
        version = "1.0.0",
        url = "https://streamlinecloud.net/",
        description = "This plugin needs to be installed on every server powered by StreamlineCloud",
        authors = {"Quinilo", "creperozelot"}
)
public class VelocitySCP {

    @Getter
    private static VelocitySCP instance;

    StreamlineCloud streamlineCloud;
    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public VelocitySCP(ProxyServer proxy, Logger logger) {

        this.proxy = proxy;
        this.logger = logger;
        instance = this;

        ServerCommand serverCmd = new ServerCommand(proxy);
        proxy.getCommandManager().register(
                proxy.getCommandManager().metaBuilder(serverCmd.getCommand())
                        .aliases("server", "go")
                        .build(),
                serverCmd.getCommand()
        );

        StaticCache.setRuntime(ServerRuntime.PROXY);
        Functions.startup();

        new VelocityServerManager();
        new VelocityGroupManager();

        new LangManager();
        new ProxyFallbackHandler();

        LangManager.getInstance().fetch(new String[]{
                "sc.mc.prefix",
                "sc.mc.motd",
                "sc.mc.notAllowed",
                "sc.mc.noFallbacks",
                "sc.mc.notWhitelisted",
                "sc.mc.maintenance",
                "sc.mc.connectingTo",
                "sc.mc.serverDoesNotExist",
                "sc.mc.alreadyConnected",
                "sc.mc.proxyShutdown"});

    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new ProxyConnectionListener());
        proxy.getCommandManager().register(proxy.getCommandManager().metaBuilder("hub").plugin(this).build(), new HubCommand());
    }

}
