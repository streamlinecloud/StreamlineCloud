package net.streamlinecloud.mc.velocity.listener;

import com.google.gson.Gson;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ListenerCloseEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.streamlinecloud.api.player.StreamlinePlayer;
import net.streamlinecloud.mc.VelocitySCP;
import net.streamlinecloud.mc.common.core.manager.LangManager;
import net.streamlinecloud.mc.common.utils.BackendRequest;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.velocity.ProxyFallbackHandler;
import net.streamlinecloud.mc.velocity.manager.VelocityServerManager;

import java.util.Optional;

public class ProxyConnectionListener {

    String onlineCount;
    long lastOnlineCountUpdate = 0;

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();

        try {

            if (StaticCache.whitelistEnabled) {
                if (!StaticCache.whitelist.contains(player.getGameProfile().getName())) {
                    player.disconnect(Component.text(LangManager.getInstance().get("sl.mc.notWhitelisted") + " \n\n§8» " + LangManager.getInstance().get("sl.mc.prefix")));
                    return;
                }
            }

            Optional<RegisteredServer> server = ProxyFallbackHandler.getInstance().searchFallback();
            if (server == null || server.isEmpty()) {
                player.disconnect(Component.text(LangManager.getInstance().get("sl.mc.noFallbacks") + " \n\n " + LangManager.getInstance().get("sl.mc.prefix")));
                return;
            }

            if (event.getPlayer().getCurrentServer().isEmpty()) event.setInitialServer(server.get());

        } catch (Exception e) {
            e.printStackTrace();
        }

        VelocityServerManager.getInstance().uploadServerInfo();
    }

    @Subscribe
    public void onServerConnect(ServerConnectedEvent event) {
        Player player = event.getPlayer();

        new BackendRequest("player/" + player.getUniqueId()).setType(BackendRequest.RestType.POST).withBody(new Gson().toJson(new StreamlinePlayer(player.getUniqueId(), player.getUsername(), StaticCache.serverData.getName(), event.getServer().getServerInfo().getName()))).fetch();
    }

    @Subscribe
    public void onProxyShutdown(ListenerCloseEvent event) {
        VelocitySCP.getInstance().getProxy().getAllPlayers().forEach(player -> {
            player.disconnect(Component.text(LangManager.getInstance().get("sl.mc.proxyShutdown") + " \n\n " + LangManager.getInstance().get("sl.mc.prefix")));
        });
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        VelocityServerManager.getInstance().uploadServerInfo();
        new BackendRequest("player/" + event.getPlayer().getUniqueId()).setType(BackendRequest.RestType.DELETE).fetch();
    }

    @Subscribe
    public void onPlayerKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        if (!ProxyFallbackHandler.getInstance().getFallbacks().contains(event.getServer().getServerInfo().getName())) {

            Optional<RegisteredServer> server = ProxyFallbackHandler.getInstance().searchFallback();
            if (server == null || server.isEmpty()) {
                player.disconnect(Component.text(LangManager.getInstance().get("sl.mc.noFallbacks") + " \n\n§8» " + LangManager.getInstance().get("sl.mc.prefix")));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            return;
        }

        event.getPlayer().disconnect(Component.text(event.getServerKickReason().toString()));
    }

    @Subscribe
    public EventTask onProxyPing(ProxyPingEvent event) {
        return EventTask.async(() -> this.format(event));
    }

    private void format(ProxyPingEvent e) {

        if (lastOnlineCountUpdate + 5000 <= System.currentTimeMillis()) {
            onlineCount = new BackendRequest("network-count").fetch().getResponse();
            lastOnlineCountUpdate = System.currentTimeMillis();
        }

        final ServerPing.Builder ping = e.getPing().asBuilder();

        try {
            assert onlineCount != null;
            try {
                ping.onlinePlayers(Integer.parseInt(onlineCount.split("-")[0]));
                ping.maximumPlayers(Integer.parseInt(onlineCount.split("-")[1]));
            } catch (NumberFormatException ex) {
                ping.onlinePlayers(0);
                ping.maximumPlayers(-1);
            }
            ping.description(MiniMessage.miniMessage().deserialize(LangManager.getInstance().get("sl.mc.motd")));
        } finally {
            e.setPing(ping.build());
        }
    }

}
