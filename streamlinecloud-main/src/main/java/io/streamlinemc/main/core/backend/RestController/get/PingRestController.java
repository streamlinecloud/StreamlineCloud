package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class PingRestController {

    public PingRestController() {
        Cache.i().getBackend().get(mainPath + "ping", ctx -> {
            ctx.result("pong");
            ctx.status(200);
        });
    }

}
