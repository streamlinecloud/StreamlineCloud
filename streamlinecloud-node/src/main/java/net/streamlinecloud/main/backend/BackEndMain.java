package net.streamlinecloud.main.backend;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.backend.socket.ServerSocket;
import net.streamlinecloud.main.utils.Cache;
import io.javalin.Javalin;

import java.util.*;

public class BackEndMain {

    public static final String mainPath = "/streamline/";
    private static Javalin app;
    public static List<String> publicRoutes = new ArrayList<>();
    public static List<BackendSession> customSessions = new ArrayList<>();
    public static Set<String> allowedOrigins = new HashSet<>();

    public static void startBE() {

        if (Cache.i().isFirstLaunch()) return;

        publicRoutes.add(mainPath + "ping");

        app = Javalin.create();
        Cache.i().setBackend(app);

        new AuthMiddleware();

        Cache.i().setServerSocket(new ServerSocket());

        //Cache.i().getBackend().start(Cache.i().getConfig().getNetwork().getBackendPort());
        //StreamlineCloud.log("sc.backend.started", new ReplacePaket[]{new ReplacePaket("%1", Cache.i().getConfig().getNetwork().getBackendPort() + "")});


    }

    public static void stop() {
        if (app != null) app.stop();
        StreamlineCloud.log("Backend offline");
    }

}
