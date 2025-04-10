package net.streamlinecloud.main.core.backend;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.controller.GroupsController;
import net.streamlinecloud.main.core.backend.controller.ServerController;
import net.streamlinecloud.main.core.backend.controller.UtilController;
import net.streamlinecloud.main.core.backend.socket.ServerSocket;
import net.streamlinecloud.main.utils.Cache;
import io.javalin.Javalin;

public class BackEndMain {

    public static final String mainPath = "/streamline/";
    private static Javalin app;

    public static void startBE() {

        Cache.i().setBackend(Javalin.create());

        new AuthMiddleware();
        new GroupsController();
        new ServerController();
        new UtilController();
        Cache.serverSocket = new ServerSocket();

        Cache.i().getBackend().start(Cache.i().getConfig().getCommunicationBridgePort());
        StreamlineCloud.log("sl.backend.started");


    }

    public static void stop() {
        StreamlineCloud.log("Shutting down §AQUACommunicationBridge");
        if (app != null) app.stop();
        StreamlineCloud.log("§AQUACommunicationBridge §REDOffline!");
    }

}
