package net.streamlinecloud.main.core.backend.RestController.get;

import io.javalin.http.HttpStatus;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerRconDetailsRestController {

    public ServerRconDetailsRestController() {

        Cache.i().getBackend().get(mainPath + "get/server/rcon-details/{uuid}", ctx -> {

            String uuid = ctx.pathParam("uuid");

            if (uuid == null) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            if (!Cache.i().getRconDetails().containsKey(uuid)) {
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            ctx.status(HttpStatus.OK);
            ctx.result(Cache.i().getGson().toJson(Cache.i().getRconDetails().get(uuid), RconData.class));

        });

    }

}
