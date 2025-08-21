package net.streamlinecloud.mc.velocity;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import net.streamlinecloud.api.server.StreamlineServerSnapshot;
import net.streamlinecloud.mc.VelocitySCP;
import net.streamlinecloud.mc.common.utils.BackendRequest;
import net.streamlinecloud.mc.common.utils.Functions;
import net.streamlinecloud.mc.common.utils.Utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class ProxyFallbackHandler {

    @Getter
    private static ProxyFallbackHandler instance;
    private String playerSpreading;

    List<String> fallbacks = new ArrayList<>();
    List<StreamlineServerSnapshot> servers = new ArrayList<>();

    public ProxyFallbackHandler() {
        instance = this;

        playerSpreading = new BackendRequest("fallback-spreading").fetch().getResponse();

        final List<String>[] allServers = new List[]{new ArrayList<>()};
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {

            servers = new Gson().fromJson(Functions.get("servers/allSnapshots"), new TypeToken<List<StreamlineServerSnapshot>>(){}.getType());

            for (String s : allServers[0]) VelocitySCP.getInstance().getProxy().unregisterServer(VelocitySCP.getInstance().getProxy().getServer(s).get().getServerInfo());
            allServers[0] = new ArrayList<>();

            assert servers != null;
            servers.removeIf(streamlineServerSnapshot -> streamlineServerSnapshot.getMaxOnlineCount() == -1);
            for (StreamlineServerSnapshot server : servers) {

                if (server.getPort() != 1.0) {
                    if (!server.getName().contains("proxy")) {
                        ServerInfo serverInfo = new ServerInfo(
                                server.getName(),
                                new InetSocketAddress("localhost", Integer.parseInt(String.valueOf(server.getPort()).split("\\.")[0]))
                        );

                        allServers[0].add(server.getName());
                        VelocitySCP.getInstance().getProxy().registerServer(serverInfo);
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
                return VelocitySCP.getInstance().getProxy().getServer(fallbacks.get(new Random().nextInt(fallbacks.size())));
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

                return VelocitySCP.getInstance().getProxy().getServer(target.get().getName());

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

                return VelocitySCP.getInstance().getProxy().getServer(target.get().getName());

            }
        }

        return Optional.empty();
    }

    public StreamlineServerSnapshot getServerSnapshot(String name) {
        for (StreamlineServerSnapshot server : servers) {
            if (server.getName().equals(name)) return server;
        }
        return null;
    }
}
