package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class GetServerdataRestController {

    public GetServerdataRestController() {
        Cache.i().getBackend().get(mainPath + "get/serverdata/{uuid}", ctx -> {
            String uuid = ctx.pathParam("uuid");
            CloudServer server = StreamlineCloud.getServerByUuid(uuid);

            if (server != null) {
                ctx.result(new Gson().toJson(server, StreamlineServer.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });

        Cache.i().getBackend().get(mainPath + "get/serverdata/name/{name}", ctx -> {
            String name = ctx.pathParam("name");
            CloudServer server = StreamlineCloud.getServerByName(name);

            if (server != null) {
                ctx.result(new Gson().toJson(server, StreamlineServer.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });
    }

}
