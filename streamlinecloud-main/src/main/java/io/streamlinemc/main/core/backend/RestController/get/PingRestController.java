package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class PingRestController {

    public PingRestController() {
        StaticCache.getBackend().get(mainPath + "ping", ctx -> {
            ctx.result("pong");
            ctx.status(200);
        });
    }

}
