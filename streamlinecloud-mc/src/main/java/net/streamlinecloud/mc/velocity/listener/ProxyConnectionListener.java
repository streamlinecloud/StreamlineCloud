package net.streamlinecloud.mc.velocity.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.streamlinecloud.mc.VelocitySCP;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.StaticCache;
import net.streamlinecloud.mc.velocity.manager.ProxyServerManager;

import java.util.Optional;

public class ProxyConnectionListener {

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

            Optional<RegisteredServer> server = VelocitySCP.getInstance().searchFallback();
            if (server.isEmpty()) {
                player.disconnect(Component.text("§cThere are no fallback servers available\n§8»§l§cStreamlineCloud"));
                return;
            }

            if (event.getPlayer().getCurrentServer().isEmpty()) event.setInitialServer(server.get());

        } catch (Exception e) {
            e.printStackTrace();
        }

        ProxyServerManager.getInstance().uploadServerInfo();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        ProxyServerManager.getInstance().uploadServerInfo();
    }

    @Subscribe
    public void onPlayerKicked(KickedFromServerEvent event) {
        Player player = event.getPlayer();

        if (!VelocitySCP.getInstance().getFallbacks().contains(event.getServer().getServerInfo().getName())) {

            Optional<RegisteredServer> server = VelocitySCP.getInstance().searchFallback();
            if (server.isEmpty()) {
                player.disconnect(Component.text("§cThere are no fallback servers available\n§8»§l§cStreamlineCloud"));
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

        final ServerPing.Builder ping = e.getPing().asBuilder();
        String count = Functions.get("network-count");

        try {
            assert count != null;
            ping.onlinePlayers(Integer.parseInt(count.split("-")[0]));
            ping.maximumPlayers(Integer.parseInt(count.split("-")[1]));
            ping.description(MiniMessage.miniMessage().deserialize("<bold><gradient:#ff4040:#d47979>Powered by StreamlineCloud</gradient></bold>\n<#4dffed>Visit streamlinecloud.net"));
        } finally {
            e.setPing(ping.build());
        }
    }

}
