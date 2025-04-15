package net.streamlinecloud.mc;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import io.leangen.geantyref.TypeToken;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSnapshot;
import net.streamlinecloud.mc.core.StreamlineCloud;
import net.streamlinecloud.mc.core.player.PlayerManager;
import net.streamlinecloud.mc.core.server.ServerManager;
import net.streamlinecloud.mc.utils.Functions;
import net.streamlinecloud.mc.utils.StaticCache;
import net.streamlinecloud.mc.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.net.InetSocketAddress;
import java.nio.file.Path;
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
    private String playerSpreading;

    List<String> fallbacks = new ArrayList<>();
    List<StreamlineServerSnapshot> servers = new ArrayList<>();

    @Inject
    public VelocitySCP(ProxyServer proxy, Logger logger, @DataDirectory Path path) {
        this.proxy = proxy;
        this.logger = getLogger();
        this.onLoad();
    }

    @Subscribe
    public EventTask onProxyPing(ProxyPingEvent event) {
        return EventTask.async(() -> this.format(event));
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.onEnable();
    }

    public void onLoad() {
        StaticCache.setRuntime(ServerRuntime.PROXY);
        Functions.startup();
        streamlineCloud = new StreamlineCloud();

        final List<String>[] allServers = new List[]{new ArrayList<>()};
        String whitelist = Functions.get("whitelist");

        playerSpreading = Functions.get("fallback-spreading");

        assert whitelist != null;
        if (whitelist.equals("false")) {
            StaticCache.whitelistEnabled = false;
        } else {
            StaticCache.whitelistEnabled = true;
            StaticCache.whitelist = new Gson().fromJson(whitelist, List.class);
        }

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

    public void uploadServerInfo() {
        StreamlineServer s = getServer(UUID.fromString(StaticCache.serverData.getUuid()));
        HashMap<UUID, String> players = new HashMap<>();
        for (Player player : getProxy().getAllPlayers()) {
            players.put(player.getUniqueId(), player.getUsername());
        }
        s.setOnlinePlayers(players);
        s.setServerState(ServerState.ONLINE);
        s.setMaxOnlineCount(-1);

        Functions.post(s, "servers/update");
    }

    public StreamlineServer getServer(UUID uuid) {
        return new Gson().fromJson(Functions.get("servers/" + uuid.toString()), StreamlineServer.class);
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void registerServer(String serverName, String hostname, int port) {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        ServerInfo serverInfo = new ServerInfo(serverName, address);

        proxy.registerServer(serverInfo);
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        uploadServerInfo();
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        try {
            Player player = event.getPlayer();

            if (StaticCache.whitelistEnabled) {
                if (!StaticCache.whitelist.contains(player.getGameProfile().getName())) {
                    player.disconnect(Component.text("§cYou are not whitelisted :/ \n\n§8»§l§cStreamlineCloud whitelist"));
                    return;
                }
            }

            Optional<RegisteredServer> server = searchFallback();
            if (server == null) {
                player.disconnect(Component.text("§cThere are no fallback servers available\n§8»§l§cStreamlineCloud"));
                return;
            }

            if (event.getPlayer().getCurrentServer().isEmpty()) event.setInitialServer(server.get());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onPlayerKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        if (!fallbacks.contains(event.getServer().getServerInfo().getName())) {

            Optional<RegisteredServer> server = searchFallback();
            if (server == null) {
                player.disconnect(Component.text("§cThere are no fallback servers available\n§8»§l§cStreamlineCloud"));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            return;
        }

        event.getPlayer().disconnect(Component.text(event.getServerKickReason().toString()));
    }

    private void format(ProxyPingEvent e) {

        final ServerPing.Builder ping = e.getPing().asBuilder();

        try {
                ping.description(formatted("<bold><gradient:#ff4040:#d47979>Powered by StreamlineCloud</gradient></bold>\n<#4dffed>Visit streamlinecloud.net"));
        } finally {
            e.setPing(ping.build());
        }
    }

    private static Component formatted(String str) {
        return MiniMessage.miniMessage().deserialize(str);
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
