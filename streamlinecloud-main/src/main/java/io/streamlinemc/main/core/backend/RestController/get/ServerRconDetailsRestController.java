package io.streamlinemc.main.core.backend.RestController.get;

import io.javalin.http.HttpStatus;
import io.streamlinemc.api.RestUtils.RconData;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ServerRconDetailsRestController {

    public ServerRconDetailsRestController() {

        StaticCache.getBackend().get(mainPath + "get/server/rcon-details/{uuid}", ctx -> {

            System.out.println("GET /get/server/rcon-details/{uuid}");

            String uuid = ctx.pathParam("uuid");

            if (uuid == null) {
                System.out.println("UUID not found");
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            if (!StaticCache.getRconDetails().containsKey(uuid)) {
                System.out.println("UUID not found");
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }
            System.out.println("UUID found");

            ctx.status(HttpStatus.OK);
            ctx.result(StaticCache.getGson().toJson(StaticCache.getRconDetails().get(uuid), RconData.class));

        });

    }

}
