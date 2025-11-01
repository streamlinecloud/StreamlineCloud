package net.streamlinecloud.main.backend;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.backend.controller.GroupsController;
import net.streamlinecloud.main.backend.controller.PlayerController;
import net.streamlinecloud.main.backend.controller.ServerController;
import net.streamlinecloud.main.backend.controller.UtilController;
import net.streamlinecloud.main.backend.socket.ServerSocket;
import net.streamlinecloud.main.lang.ReplacePaket;
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
        GroupsController groupsController = new GroupsController();
        ServerController serverController = new ServerController();
        UtilController utilController = new UtilController();
        PlayerController playerController = new PlayerController();

        app.get("/streamline/version", utilController::version);
        app.get("/streamline/ping", utilController::ping);
        app.get("/streamline/uptime", utilController::uptime);
        app.get("/streamline/whitelist", utilController::whitelist);
        app.get("/streamline/fallback-spreading", utilController::fallbackSpreading);
        app.get("/streamline/network-count", utilController::networkOnlineCount);
        app.post("/streamline/translation", utilController::translations);
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
        app.post("/streamline/servers/{uuid}/stop", serverController::stop);
        app.post("/streamline/servers/{uuid}/kill", serverController::kill);
        app.post("/streamline/servers/{uuid}/restart", serverController::restart);
        app.get("/streamline/servers/name/{name}", serverController::get);
        app.get("/streamline/servers/{uuid}/rconDetails", serverController::getRconDetails);
        app.get("/streamline/servers/{uuid}/autoRestart", serverController::autoRestart);
        app.post("/streamline/servers/update", serverController::update);

        app.get("/streamline/player/{uuid}", playerController::get);
        app.get("streamline/player/byName/{name}", playerController::getByName);
        app.post("/streamline/player/{uuid}", playerController::set);
        app.post("/streamline/player/{uuid}/action/{type}", playerController::action);
        app.delete("/streamline/player/{uuid}", playerController::delete);

        Cache.i().setServerSocket(new ServerSocket());

        Cache.i().getBackend().start(Cache.i().getConfig().getNetwork().getBackendPort());
        StreamlineCloud.log("sc.backend.started", new ReplacePaket[]{new ReplacePaket("%1", Cache.i().getConfig().getNetwork().getBackendPort() + "")});


    }

    public static void stop() {
        if (app != null) app.stop();
        StreamlineCloud.log("Backend offline");
    }

}
