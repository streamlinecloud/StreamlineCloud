package io.streamlinemc.main.core.backend;

import com.google.gson.Gson;
import io.streamlinemc.api.group.StreamlineGroup;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.backend.RestController.AllGroupsRestController;
import io.streamlinemc.main.core.backend.RestController.get.*;
import io.streamlinemc.main.core.backend.RestController.post.ProxyVersionRestController;
import io.streamlinemc.main.core.backend.RestController.post.ServerHelloWorldRestController;
import io.streamlinemc.main.core.backend.RestController.post.ServerUpdateDataRestController;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.StaticCache;
import io.streamlinemc.main.core.server.CloudServer;
import io.javalin.Javalin;
import org.eclipse.jetty.server.Server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class BackEndMain {

    public static final String mainPath = "/streamline/";
    private static Javalin app;

    public static void startBE() {

        StreamlineCloud.log("sl.backend.starting", new ReplacePaket[]{new ReplacePaket("%0", StaticCache.getConfig().getCommunicationBridgePort() + "")});

        StaticCache.setBackend(Javalin.create());

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


        StaticCache.getBackend().start(StaticCache.getConfig().getCommunicationBridgePort());


        StreamlineCloud.log("sl.backend.started");

    }

    public static void stop() {
        StreamlineCloud.log("Shutting down §AQUACommunicationBridge");
        if (app != null) app.stop();
        StreamlineCloud.log("§AQUACommunicationBridge §REDOffline!");
    }

}
