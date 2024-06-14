package io.streamlinemc.mc.listener.bungee;

import com.google.gson.Gson;
import io.streamlinemc.mc.WaterfallSCP;
import io.streamlinemc.mc.utils.Functions;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerConnectListener implements Listener {
    @EventHandler

    public void onServerConnected(ServerConnectEvent event) {


        if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            // Set the default server for new players or players connecting to the proxy
            List<String> servers = new Gson().fromJson(Functions.get("get/fallbackservers"), List.class);

            ServerInfo defaultServer = WaterfallSCP.getInstance().getProxy().getServerInfo(servers.get(0));
            if (defaultServer != null) {
                event.setTarget(defaultServer);
            }
            System.out.println(servers);
            System.out.println("SERVER TO: " + defaultServer);
            System.out.println("SERVER PLAYER: " + event.getTarget());
        }

        System.out.println("SENDING PLAYER TO: " + event.getTarget());


    }
}