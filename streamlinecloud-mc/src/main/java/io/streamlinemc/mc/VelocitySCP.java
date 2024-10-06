package io.streamlinemc.mc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.ModInfo;
import io.streamlinemc.api.StreamlineAPI;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.mc.api.StreamlineCloud;
import io.streamlinemc.mc.utils.Functions;
import io.streamlinemc.mc.utils.InternalSettings;
import io.streamlinemc.mc.utils.StaticCache;
import io.streamlinemc.mc.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.server.ServerListPingEvent;

import java.awt.*;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Plugin(id = "streamlinecloudplugin", name = "Streamlinecloud", version = "0.2",
        url = "https://streamlinemc.cloud", description = "Bridge for Streamlinecloud", authors = {"Quinilo", "creperozelot"})
public class VelocitySCP {

    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public VelocitySCP(ProxyServer proxy, Logger logger, @DataDirectory Path path) {
        this.proxy = proxy;
        this.logger = getLogger();

        System.out.println("DEBUG: Plugin enabled");
        //proxy.getConfiguration().get

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
        System.out.println("DEBUG: Plugin loaded");

        Functions.startup();

        HashMap<String, Double> servers = new HashMap<>();
        List<String> allServers = new ArrayList<>();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {



            servers.putAll(new Gson().fromJson(Functions.get("get/allservers"), servers.getClass()));

            for (String s : allServers) getProxy().unregisterServer(proxy.getServer(s).get().getServerInfo());
            allServers.clear();


            servers.forEach((str, port) -> {

                if (port != 1.0) {
                    if (!str.contains("proxy")) {
                        ServerInfo serverInfo = new ServerInfo(
                                str,
                                new InetSocketAddress("localhost", Integer.parseInt(String.valueOf(port).split("\\.")[0]))
                        );

                        allServers.add(str);
                        proxy.registerServer(serverInfo);
                    }
                }

            });

            Utils.servers = servers;

        }, 0, 3, TimeUnit.SECONDS);
    }

    public void onEnable() {
        System.out.println("DEBUG: Plugin enabled");
    }

    public void onDisable() {
        System.out.println("DEBUG: Plugin disabled");
    }

    public void registerServer(String serverName, String hostname, int port) {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        ServerInfo serverInfo = new ServerInfo(serverName, address);

        proxy.registerServer(serverInfo);
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();

        if (event.getPlayer().getCurrentServer().isEmpty()) event.setInitialServer(proxy.getServer("lobby-1").get());

    }

    @Subscribe
    public void onPlayerKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        /*if (event.getServerKickReason().isPresent()) {
            String reason = event.getServerKickReason().get().toString();
            System.out.println("Player kicked for reason: " + reason);
        }*/

        Optional<RegisteredServer> targetServer = proxy.getServer("lobby-1");

        event.setResult(KickedFromServerEvent.RedirectPlayer.create(targetServer.get()));
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

}
