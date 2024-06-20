package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class GetServerdataRestController {

    public GetServerdataRestController() {
        StaticCache.getBackend().get(mainPath + "get/serverdata/{uuid}", ctx -> {
            String uuid = ctx.pathParam("uuid");
            CloudServer server = StreamlineCloud.getServerByUuid(uuid);

            if (server != null) {
                StreamlineServer streamlineServer = server;
                ctx.result(new Gson().toJson(streamlineServer, StreamlineServer.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });
    }

}
