package net.streamlinecloud.main.core.backend.RestController.get;

import io.javalin.http.HttpStatus;
import io.streamlinemc.api.RestUtils.RconData;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerRconDetailsRestController {

    public ServerRconDetailsRestController() {

        Cache.i().getBackend().get(mainPath + "get/server/rcon-details/{uuid}", ctx -> {

            System.out.println("GET /get/server/rcon-details/{uuid}");

            String uuid = ctx.pathParam("uuid");

            if (uuid == null) {
                System.out.println("UUID not found");
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }

            if (!Cache.i().getRconDetails().containsKey(uuid)) {
                System.out.println("UUID not found");
                ctx.result("UUID not found");
                ctx.status(201);
                return;
            }
            System.out.println("UUID found");

            ctx.status(HttpStatus.OK);
            ctx.result(Cache.i().getGson().toJson(Cache.i().getRconDetails().get(uuid), RconData.class));

        });

    }

}
