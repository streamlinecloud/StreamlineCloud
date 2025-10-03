package net.streamlinecloud.main.core.server;

import lombok.Getter;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class RunningServerManager {

    List<RunningServer> runningServers = new ArrayList<>();
    List<RunningServer> serversWaitingForStart = new ArrayList<>();
    HashMap<RunningServer, RunningServer> restartingServers = new HashMap<>();
    HashMap<String, RunningServer> serverRegister = new HashMap<>();

    @Getter
    private static RunningServerManager instance;

    boolean firstStartup = true;

    public RunningServerManager() {
        instance = this;

        if (Cache.i().isFirstLaunch()) return;
        task();
        if (Cache.i().getConfig().getFallback().isDynamicFallbackControl()) fallbackControlTask();
    }

    public void task() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            if (Cache.i().isStopping()) return;

            try {

                startServersIfNeeded();
                startNextServer();

                if (getServersWaitingForStart().isEmpty() && firstStartup) firstStartup = false;

                for (RunningServer server : new ArrayList<>(getRunningServers())) {

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
            RunningServer server = getServersWaitingForStart().getFirst();

            for (String s : Cache.i().getDataCache()) {
                if (s.startsWith("blacklistGroup:") && s.endsWith(server.getGroup())) return;
            }

            try {
                server.start(new File(server.getGroupDirect().getJavaExec().equals("%default") ? Cache.i().getConfig().getDefaultJavaPath() : server.getGroupDirect().getJavaExec()));
            } catch (Exception e) {
                StreamlineCloud.log("Failed to start " + server.getName() + " - " + e.getMessage() + " - started by " + server.getStartedBy());
                getServersWaitingForStart().remove(server);
                server.kill();
            }
            getServersWaitingForStart().remove(server);
        }
    }

    public void fallbackControlTask() {
        MainConfig.FallbackConfig config = Cache.i().getConfig().getFallback();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            RunningServer fallbackServer = getServerByName(config.getFallbackGroup() + "-1");
            if (fallbackServer == null) return;

            Integer[] count = Utils.getNetworkOnlineCount();
            int fallbackSize = fallbackServer.getMaxOnlineCount();
            int puffer = config.getDynamicFallbackPuffer();
            int online = count[0];
            int max = count[1];

            if (puffer + online >= max) return;
            int neededServers = (int) Math.ceil((double) (online + puffer) / fallbackSize);

            CloudGroup fallbackGroup = CloudGroupManager.getInstance().getGroupByName(config.getFallbackGroup());
            List<RunningServer> fallbackServers = CloudGroupManager.getInstance().getGroupOnlineServers(fallbackGroup);

            if (neededServers > fallbackServers.size()) {
                StreamlineCloud.log("DynamicFallbackControl is starting a fallback server... (needed: " + neededServers + ", online: " + fallbackServers.size() + ")");

                startServerByGroup(fallbackGroup, "Automatic fallback control feature");
            }
            if (neededServers < fallbackServers.size()) {
                StreamlineCloud.log("DynamicFallbackControl is stopping a fallback server... (needed: " + neededServers + ", online: " + fallbackServers.size() + ")");

                RunningServer target = fallbackServers.stream()
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
    public RunningServer getServerByName(String name) {

        return serverRegister.getOrDefault(name, null);

    }

    /**
     * @param uuid The uuid of the wanted server
     * @return Returns the unique server with the same uuid
     */
    public RunningServer getServerByUuid(String uuid) {

        for (RunningServer ser : getRunningServers()) {
            if (ser.getUuid().equals(uuid) || (ser.getName() + "-" + ser.getUuid()).equals(uuid)) {
                return ser;
            }
        }
        return null;
    }

    /**
     * Returns a list of {@link RunningServer CloudServers} by the given name. <br>
     * This function support wildcard and is intended to use for wildcards.<br>
     * Normally it returns only one CloudServer if not used with a wildcard.<br>
     * Example: <code>lobby-1 or proxy-1</code> would return a list with only one server.<br>
     * Example: <code>lobby-* or proxy-*</code> would return a list with all servers in a specific group or with a specific naming pattern.<br>
     * You cannot use <code>*</code> alone. This is intended and disabled for security reasons.<br>
     * @param name name of the server or wildcard.
     * @return list of a CloudServer if found otherwise null
     */
    public @Nullable List<RunningServer> getServersByName(String name) {

        if (name.endsWith("-*")) {

            List<RunningServer> servers = new ArrayList<>();
            for (RunningServer runningServer : RunningServerManager.getInstance().getRunningServers()) {
                if (runningServer.getName().contains(name.substring(0, name.length() - 1))) servers.add(runningServer);
            }
            if (servers.isEmpty()) return null;
            return servers;

        } else {

            RunningServer server = getServerByName(name);
            if (server == null) return null;
            return Collections.singletonList(server);

        }

    }

    /**
     * Starts a new server of a group if needed to reach the minOnlineCount of the group
     *
     * @param group The target group
     */
    private void startServersIfNeeded(CloudGroup group) {
        List<RunningServer> allServers = new ArrayList<>(CloudGroupManager.getInstance().getGroupOnlineServers(group));

        for (RunningServer s : Cache.i().getServersWaitingForStart()) {
            if (s.getGroupDirect().equals(group)) {
                allServers.add(s);
            }
        }

        if (allServers.size() < group.getMinOnlineCount()) {
            startServerByGroup(group, "Autostart (based on group min online count)");
        }
    }

    /**
     * Executes {@link #startServersIfNeeded(CloudGroup)} for every active group.
     * Groups are processed by priority.
     */
    public void startServersIfNeeded() {
        if (!Cache.i().getServersWaitingForStart().isEmpty()) {
            return;
        }

        PriorityQueue<CloudGroup> groups = new PriorityQueue<>(
                Comparator.comparingInt(CloudGroup::getPriority).reversed()
        );
        groups.addAll(Cache.i().getActiveGroups());

        for (CloudGroup group : groups) {
            startServersIfNeeded(group);
        }
    }

    public String startServerByGroup(CloudGroup cloudGroup, String startedBy) {
        return startServerByGroup(cloudGroup, new ArrayList<>(), startedBy);
    }

    public String startServerByGroup(CloudGroup cloudGroup, List<String> templates, String startedBy) {
        RunningServer server = new RunningServer(cloudGroup.getName() + "-" + calculateServerNumber(cloudGroup), cloudGroup.getRuntime(), startedBy);
        server.setGroup(cloudGroup.getName());
        server.setCustomTemplates(templates);
        serverRegister.put(server.getName(), server);
        getServersWaitingForStart().add(server);
        return server.getUuid();
    }

    public int calculateServerNumber(CloudGroup g) {

        ArrayList<Integer> usedNumbers = new ArrayList<>();
        for (RunningServer server : CloudGroupManager.getInstance().getGroupOnlineServers(g)) {
            usedNumbers.add(Integer.valueOf(server.getName().split("-")[1]));
        }

        for (int i = 1; true; i++) {
            if (!usedNumbers.contains(i)) return i;
        }

    }

}
