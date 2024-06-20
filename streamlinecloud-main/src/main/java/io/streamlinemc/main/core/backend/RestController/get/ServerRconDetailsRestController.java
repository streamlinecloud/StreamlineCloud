package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.api.RestUtils.RconData;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ServerRconDetailsRestController {

    public ServerRconDetailsRestController() {

        StaticCache.getBackend().get(mainPath + "get/server/rcon-details/{uuid}", ctx -> {

            String uuid = ctx.pathParam("uuid");

            if (uuid == null) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            if (!StaticCache.getRconDetails().containsKey(uuid)) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            ctx.result(StaticCache.getGson().toJson(StaticCache.getRconDetails(), RconData.class));

        });

    }

}
