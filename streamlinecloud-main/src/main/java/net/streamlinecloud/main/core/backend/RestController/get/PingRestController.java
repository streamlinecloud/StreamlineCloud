package net.streamlinecloud.main.core.backend.RestController.get;

import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class PingRestController {

    public PingRestController() {
        Cache.i().getBackend().get(mainPath + "ping", ctx -> {
            ctx.result("pong");
            ctx.status(200);
        });
    }

}
