package net.streamlinecloud.main.core.task;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerStarterTask {

    boolean firstStartup = true;

    public ServerStarterTask() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            StreamlineCloud.checkGroups();

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
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }
}
