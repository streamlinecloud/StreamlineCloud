package net.streamlinecloud.main.core.server;

import lombok.Getter;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class CloudServerManager {

    List<CloudServer> runningServers = new ArrayList<>();
    List<CloudServer> serversWaitingForStart = new ArrayList<>();
    HashMap<CloudServer, CloudServer> restartingServers = new HashMap<>();
    HashMap<String, CloudServer> serverRegister = new HashMap<>();

    @Getter
    private static CloudServerManager instance;

    boolean firstStartup = true;

    public CloudServerManager() {
        instance = this;

        if (Cache.i().isFirstLaunch()) return;
        task();
        if (Cache.i().getConfig().getFallback().isDynamicFallbackControl()) fallbackControlTask();
    }

    public void task() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            try {

                startServersIfNeeded();
                startNextServer();

                if (getServersWaitingForStart().isEmpty() && firstStartup) firstStartup = false;

                for (CloudServer server : new ArrayList<>(getRunningServers())) {

                    if (server.getStopTime() == -1) continue;
                    if (System.currentTimeMillis() >= server.getStopTime() && !server.isRestarting()) server.restart();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }


    public void startNextServer() {
        if (!getServersWaitingForStart().isEmpty()) {
            try {
                CloudServer server = getServersWaitingForStart().getFirst();

                for (String s : Cache.i().getDataCache()) {
                    if (s.startsWith("blacklistGroup:") && s.endsWith(server.getGroup())) return;
                }

                server.start(new File(server.getGroupDirect().getJavaExec().equals("%default") ? Cache.i().getConfig().getDefaultJavaPath() : server.getGroupDirect().getJavaExec()));
                getServersWaitingForStart().remove(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fallbackControlTask() {
        MainConfig.FallbackConfig config = Cache.i().getConfig().getFallback();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            CloudServer fallbackServer = getServerByName(config.getFallbackGroup() + "-1");
            if (fallbackServer == null) return;

            Integer[] count = Utils.getNetworkOnlineCount();
            int fallbackSize = fallbackServer.getMaxOnlineCount();
            int puffer = config.getDynamicFallbackPuffer();
            int online = count[0];
            int max = count[1];

            if (puffer + online >= max) return;
            int neededServers = (online + puffer) / fallbackSize;

            CloudGroup fallbackGroup = CloudGroupManager.getInstance().getGroupByName(config.getFallbackGroup());
            List<CloudServer> fallbackServers = CloudGroupManager.getInstance().getGroupOnlineServers(fallbackGroup);

            if (neededServers > fallbackServers.size()) startServerByGroup(fallbackGroup);
            if (neededServers < fallbackServers.size()) {
                CloudServer target = fallbackServers.stream()
                        .min(Comparator.comparingInt(s -> s.getOnlinePlayers().size()))
                        .orElse(null);

                target.stop();
            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * @param name The name of the wanted server (without the uuid) (like just lobby-1)
     * @return Returns the prioritized server with this name. Could return different servers if the server gets replaced
     */
    public CloudServer getServerByName(String name) {

        return serverRegister.getOrDefault(name, null);

    }

    /**
     * @param uuid The uuid of the wanted server
     * @return Returns the unique server with the same uuid
     */
    public CloudServer getServerByUuid(String uuid) {

        for (CloudServer ser : getRunningServers()) {

            if (ser.getUuid().equals(uuid)) {

                return ser;
            }
        }
        return null;
    }

    /**
     * This function starts a new server of a group if needed to reach the minOnlineCount of the group
     *
     * @param group The target group
     */
    private void startServersIfNeeded(CloudGroup group) {

        List<CloudServer> alLServers = new ArrayList<>(CloudGroupManager.getInstance().getGroupOnlineServers(group));
        for (CloudServer s : getServersWaitingForStart()) if (s.getGroupDirect().equals(group)) alLServers.add(s);

        if (alLServers.size() < group.getMinOnlineCount()) {

            startServerByGroup(group);
        }
    }

    /**
     * This function executes {@link #startServersIfNeeded(CloudGroup)}) for every active group
     */
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
        CloudServer server = new CloudServer(cloudGroup.getName() + "-" + calculateServerNumber(cloudGroup), cloudGroup.getRuntime());
        server.setGroup(cloudGroup.getName());
        server.setCustomTemplates(templates);
        serverRegister.put(server.getName(), server);
        getServersWaitingForStart().add(server);
        return server.getUuid();
    }

    public int calculateServerNumber(CloudGroup g) {

        ArrayList<Integer> usedNumbers = new ArrayList<>();
        for (CloudServer server : CloudGroupManager.getInstance().getGroupOnlineServers(g)) {
            usedNumbers.add(Integer.valueOf(server.getName().split("-")[1]));
        }

        for (int i = 1; true; i++) {
            if (!usedNumbers.contains(i)) return i;
        }

    }

}
