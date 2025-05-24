package net.streamlinecloud.main.core.server;

import lombok.Getter;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class CloudServerManager {

    HashMap<CloudServer, CloudServer> overflowServers = new HashMap<>();

    @Getter
    private static CloudServerManager instance;

    boolean firstStartup = true;

    public CloudServerManager() {
        instance = this;
        task();
    }

    public void task() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            CloudServerManager.getInstance().startServersIfNeeded();

            if (!Cache.i().getServersWaitingForStart().isEmpty()) {
                try {
                    CloudServer server = Cache.i().getServersWaitingForStart().getFirst();

                    for (String s : Cache.i().getDataCache()) {
                        if (s.startsWith("blacklistGroup:") && s.endsWith(server.getGroup())) return;
                    }

                    server.start(new File(server.getGroupDirect().getJavaExec().equals("%default") ? Cache.i().getConfig().getDefaultJavaPath() : server.getGroupDirect().getJavaExec()));
                    Cache.i().getServersWaitingForStart().remove(server);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (Cache.i().getServersWaitingForStart().isEmpty()) {

                if (firstStartup) {

                    firstStartup = false;
                }

            }

            for (CloudServer server : Cache.i().getRunningServers()) {

                if (server.getStopTime() == -1) continue;
                if (System.currentTimeMillis() >= server.getStopTime()) server.overflow();

            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }

    public CloudServer getServerByName(String name) {

        for (CloudServer ser : Cache.i().getRunningServers()) {

            if (ser.getName().equals(name)) {

                return ser;
            }
        }
        return null;
    }

    public CloudServer getServerByUuid(String uuid) {

        for (CloudServer ser : Cache.i().getRunningServers()) {

            if (ser.getUuid().equals(uuid)) {

                return ser;
            }
        }
        return null;
    }

    private void startServersIfNeeded(CloudGroup g) {

        List<CloudServer> alLServers = new ArrayList<>(CloudGroupManager.getInstance().getGroupOnlineServers(g));
        for (CloudServer s : Cache.i().getServersWaitingForStart()) if (s.getGroupDirect().equals(g)) alLServers.add(s);

        if (alLServers.size() < g.getMinOnlineCount()) {

            startServerByGroup(g);
        }
    }

    public void startServersIfNeeded() {

        for (CloudGroup g : Cache.i().getActiveGroups()) {

            List<CloudServer> servers = CloudGroupManager.getInstance().getGroupOnlineServers(g);
            startServersIfNeeded(g);
        }
    }

    public String startServerByGroup(CloudGroup cloudGroup) {
        return startServerByGroup(cloudGroup, new ArrayList<>());
    }

    public String startServerByGroup(CloudGroup cloudGroup, List<String> templates) {
        List<CloudServer> allServers = new ArrayList<>(CloudGroupManager.getInstance().getGroupOnlineServers(cloudGroup));
        for (CloudServer s : Cache.i().getServersWaitingForStart()) if (s.getGroupDirect().equals(cloudGroup)) allServers.add(s);

        CloudServer server = new CloudServer(cloudGroup.getName() + "-" + calculateServerNumber(cloudGroup), cloudGroup.getRuntime());
        server.setGroup(cloudGroup.getName());
        server.setCustomTemplates(templates);
        Cache.i().getServersWaitingForStart().add(server);
        return server.getUuid();
    }

    public int calculateServerNumber(CloudGroup g) {

        ArrayList<Integer> usedNumbers = new ArrayList<>();
        for (CloudServer server : CloudGroupManager.getInstance().getGroupOnlineServers(g)) {
            usedNumbers.add(Integer.valueOf(server.getName().split("-")[1]));
        }

        for (int i = 1; true ; i++) {
            if (!usedNumbers.contains(i)) return i;
        }

    }

}
