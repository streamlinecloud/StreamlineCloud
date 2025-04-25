package net.streamlinecloud.mc;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.leangen.geantyref.TypeToken;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.StreamlineServerSnapshot;
import net.streamlinecloud.mc.common.core.StreamlineCloud;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.common.utils.Utils;
import lombok.Getter;
import net.streamlinecloud.mc.velocity.listener.ProxyConnectionListener;
import net.streamlinecloud.mc.velocity.manager.ProxyGroupManager;
import net.streamlinecloud.mc.velocity.manager.ProxyServerManager;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Getter
@Plugin(id = "streamlinecloudplugin", name = "Streamlinecloud", version = "0.2",
        url = "https://streamlinemc.cloud", description = "Bridge for Streamlinecloud", authors = {"Quinilo", "creperozelot"})
public class VelocitySCP {

    StreamlineCloud streamlineCloud;
    private final ProxyServer proxy;
    private final Logger logger;
    @Getter
    private static VelocitySCP instance;
    private String playerSpreading;

    List<String> fallbacks = new ArrayList<>();
    List<StreamlineServerSnapshot> servers = new ArrayList<>();

    @Inject
    public VelocitySCP(ProxyServer proxy, Logger logger) {

        this.proxy = proxy;
        this.logger = logger;
        instance = this;

        StaticCache.setRuntime(ServerRuntime.PROXY);
        Functions.startup();

        new ProxyServerManager();
        new ProxyGroupManager();

        String whitelist = Functions.get("whitelist");
        playerSpreading = Functions.get("fallback-spreading");

        assert whitelist != null;
        if (whitelist.equals("false")) {
            StaticCache.whitelistEnabled = false;
        } else {
            StaticCache.whitelistEnabled = true;
            StaticCache.whitelist = new Gson().fromJson(whitelist, List.class);
        }

        task();

    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new ProxyConnectionListener());
    }

    public void task() {
        final List<String>[] allServers = new List[]{new ArrayList<>()};
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {

            servers = new Gson().fromJson(Functions.get("servers/allSnapshots"), new TypeToken<List<StreamlineServerSnapshot>>(){}.getType());

            for (String s : allServers[0]) getProxy().unregisterServer(proxy.getServer(s).get().getServerInfo());
            allServers[0] = new ArrayList<>();

            for (StreamlineServerSnapshot server : servers) {

                if (server.getPort() != 1.0) {
                    if (!server.getName().contains("proxy")) {
                        ServerInfo serverInfo = new ServerInfo(
                                server.getName(),
                                new InetSocketAddress("localhost", Integer.parseInt(String.valueOf(server.getPort()).split("\\.")[0]))
                        );

                        allServers[0].add(server.getName());
                        proxy.registerServer(serverInfo);
                    }
                }

            };

            Utils.servers = servers;

            fallbacks = new Gson().fromJson(Functions.get("servers/fallbackServers"), List.class);

        }, 0, 3, TimeUnit.SECONDS);
    }

    public Optional<RegisteredServer> searchFallback() {
        if (fallbacks.isEmpty()) return null;

        switch (playerSpreading) {
            case "RANDOM" -> {
                System.out.println("RANDOM");
                return proxy.getServer(fallbacks.get(new Random().nextInt(fallbacks.size())));
            }
            case "SPLIT" -> {
                System.out.println("SPLIT");
                AtomicReference<StreamlineServerSnapshot> target = new AtomicReference<>();

                for (String fallback : fallbacks) {
                    StreamlineServerSnapshot serverSnapshot = getServerSnapshot(fallback);

                    if (target.get() == null) {
                        if (serverSnapshot.getOnlineCount() != serverSnapshot.getMaxOnlineCount()) target.set(serverSnapshot);
                    } else {
                        if (target.get().getOnlineCount() > serverSnapshot.getOnlineCount() && serverSnapshot.getOnlineCount() != serverSnapshot.getMaxOnlineCount()) {
                            target.set(serverSnapshot);
                        }
                    }

                }

                return proxy.getServer(target.get().getName());

            }
            case "BUNDLE" -> {
                System.out.println("BUNDLE");
                AtomicReference<StreamlineServerSnapshot> target = new AtomicReference<>();

                for (String fallback : fallbacks) {
                    StreamlineServerSnapshot serverSnapshot = getServerSnapshot(fallback);

                    if (target.get() == null) {
                        if (serverSnapshot.getOnlineCount() != serverSnapshot.getMaxOnlineCount()) target.set(serverSnapshot);
                    } else {
                        if (target.get().getOnlineCount() < serverSnapshot.getOnlineCount() && serverSnapshot.getOnlineCount() != serverSnapshot.getMaxOnlineCount())
                            target.set(serverSnapshot);
                    }

                }

                return proxy.getServer(target.get().getName());

            }
        }

        return null;
    }

    public StreamlineServerSnapshot getServerSnapshot(String name) {
        for (StreamlineServerSnapshot server : servers) {
            if (server.getName().equals(name)) return server;
        }
        return null;
    }

}
