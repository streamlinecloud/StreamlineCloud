package io.streamlinemc.main.core.backend;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.backend.RestController.AllGroupsRestController;
import io.streamlinemc.main.core.backend.RestController.get.*;
import io.streamlinemc.main.core.backend.RestController.post.ProxyVersionRestController;
import io.streamlinemc.main.core.backend.RestController.post.ServerHelloWorldRestController;
import io.streamlinemc.main.core.backend.RestController.post.ServerUpdateDataRestController;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.Cache;
import io.javalin.Javalin;

public class BackEndMain {

    public static final String mainPath = "/streamline/";
    private static Javalin app;

    public static void startBE() {

        StreamlineCloud.log("sl.backend.starting", new ReplacePaket[]{new ReplacePaket("%0", Cache.i().getConfig().getCommunicationBridgePort() + "")});

        Cache.i().setBackend(Javalin.create());

        //Load Rest Classes

        new BeforeRestController();
        new PingRestController();
        new ServerCountRestController();
        new InfoRestController();
        new AllServersRestController();
        new AllServerDatasetsRestController();
        new AllServerUUIDRestController();
        new UptimeRestController();
        new FallbackServersRestController();
        new GetServerdataRestController();
        new AllGroupsRestController();
        new ProxyVersionRestController();
        new ServerHelloWorldRestController();
        new ServerUpdateDataRestController();
        new ServerRconDetailsRestController();
        new GetVersionInfoRestController();
        new GetGroupdataRestController();


        Cache.i().getBackend().start(Cache.i().getConfig().getCommunicationBridgePort());


        StreamlineCloud.log("sl.backend.started");

    }

    public static void stop() {
        StreamlineCloud.log("Shutting down §AQUACommunicationBridge");
        if (app != null) app.stop();
        StreamlineCloud.log("§AQUACommunicationBridge §REDOffline!");
    }

}
