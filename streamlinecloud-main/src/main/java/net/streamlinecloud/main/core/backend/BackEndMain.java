package net.streamlinecloud.main.core.backend;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.controller.GroupsController;
import net.streamlinecloud.main.core.backend.controller.ServerController;
import net.streamlinecloud.main.core.backend.controller.UtilController;
import net.streamlinecloud.main.core.backend.socket.ServerSocket;
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

        publicRoutes.add(mainPath + "ping");

        app = Javalin.create();
        Cache.i().setBackend(app);

        new AuthMiddleware();
        GroupsController groupsController = new GroupsController();
        ServerController serverController = new ServerController();
        UtilController utilController = new UtilController();

        app.get("/streamline/version", utilController::version);
        app.get("/streamline/ping", utilController::ping);
        app.get("/streamline/uptime", utilController::uptime);
        app.get("/streamline/whitelist", utilController::whitelist);
        app.get("/streamline/fallback-spreading", utilController::fallbackSpreading);
        app.get("/streamline/network-count", utilController::networkOnlineCount);
        app.post("/streamline/command", utilController::command);
        app.post("/streamline/report-proxy-online-count/{name}", utilController::reportProxyOnlineCount);

        app.get("/streamline/groups", groupsController::getAll);
        app.get("/streamline/groups/{name}", groupsController::get);
        app.get("/streamline/groups/{name}/servers", groupsController::servers);

        app.get("/streamline/servers/allSnapshots", serverController::getAllSnapshots);
        app.get("/streamline/servers/fallbackServers", serverController::getFallbackServers);
        app.get("/streamline/servers/serverCount", serverController::serverCount);
        app.post("/streamline/servers/start", serverController::start);

        app.get("/streamline/servers/{uuid}", serverController::get);
        app.get("/streamline/servers/{uuid}/rconDetails", serverController::getRconDetails);
        app.get("/streamline/servers/{uuid}/autoRestart", serverController::autoRestart);
        app.post("/streamline/servers/update", serverController::update);

        app.get("/streamline/servers/name/{name}", serverController::get);

        Cache.i().setServerSocket(new ServerSocket());

        Cache.i().getBackend().start(Cache.i().getConfig().getNetwork().getBackendPort());
        StreamlineCloud.log("sl.backend.started");


    }

    public static void stop() {
        StreamlineCloud.log("Shutting down §AQUACommunicationBridge");
        if (app != null) app.stop();
        StreamlineCloud.log("§AQUACommunicationBridge §REDOffline!");
    }

}
