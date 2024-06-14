package io.streamlinemc.main.core.task;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.StaticCache;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerStarterTask {

    boolean firstStartup = true;

    public ServerStarterTask() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable runnable = () -> {

            StreamlineCloud.checkGroups();

            if (!StaticCache.getServersWaitingForStart().isEmpty()) {
                CloudServer server = StaticCache.getServersWaitingForStart().get(0);
                server.start(new File(server.getGroupDirect().getJavaExec()));
                StaticCache.getServersWaitingForStart().remove(server);
                StaticCache.getLinkedServers().put(server, StreamlineCloud.getGroupByName(server.getGroupDirect().getName()));
            }

            if (StaticCache.getServersWaitingForStart().isEmpty()) {

                if (firstStartup) {

                    firstStartup = false;
                    StreamlineCloud.log("sl.startup.serversStarting");
                }

            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }
}
