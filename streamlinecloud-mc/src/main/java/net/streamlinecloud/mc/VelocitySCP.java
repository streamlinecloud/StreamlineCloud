package net.streamlinecloud.mc;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.mc.common.core.StreamlineCloud;
import net.streamlinecloud.mc.common.core.manager.LangManager;
import net.streamlinecloud.mc.common.utils.BackendRequest;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import lombok.Getter;
import net.streamlinecloud.mc.velocity.ProxyFallbackHandler;
import net.streamlinecloud.mc.velocity.command.RefreshWhitelistCommand;
import net.streamlinecloud.mc.velocity.command.ServerCommand;
import net.streamlinecloud.mc.velocity.listener.ProxyConnectionListener;
import net.streamlinecloud.mc.velocity.manager.ProxyGroupManager;
import net.streamlinecloud.mc.velocity.manager.ProxyServerManager;

import java.util.List;
import java.util.logging.Logger;

@Getter
@Plugin(id = "streamlinecloud", name = "Streamlinecloud", version = "1.0.0",
        url = "https://streamlinecloud.net/", description = "This plugin needs to be installed on every server powered by StreamlineCloud", authors = {"Quinilo", "creperozelot"})
public class VelocitySCP {

    StreamlineCloud streamlineCloud;
    private final ProxyServer proxy;
    private final Logger logger;
    @Getter
    private static VelocitySCP instance;

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

        new ProxyServerManager();
        new ProxyGroupManager();

        new LangManager();
        new ProxyFallbackHandler();

        LangManager.getInstance().fetch(new String[]{
                "sl.mc.prefix",
                "sl.mc.motd",
                "sl.mc.notAllowed",
                "sl.mc.noFallbacks",
                "sl.mc.notWhitelisted",
                "sl.mc.connectingTo",
                "sl.mc.serverDoesNotExist",
                "sl.mc.alreadyConnected",
                "sl.mc.proxyShutdown"});

        refreshWhitelist();

    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new ProxyConnectionListener());
        proxy.getCommandManager().register(proxy.getCommandManager().metaBuilder("refreshWhitelist").plugin(this).build(), new RefreshWhitelistCommand());
    }

    public void refreshWhitelist() {
        String whitelist = new BackendRequest("whitelist").fetch().getResponse();

        assert whitelist != null;
        if (whitelist.equals("false")) {
            StaticCache.whitelistEnabled = false;
        } else {
            StaticCache.whitelistEnabled = true;
            StaticCache.whitelist = new Gson().fromJson(whitelist, List.class);
        }
    }


}
