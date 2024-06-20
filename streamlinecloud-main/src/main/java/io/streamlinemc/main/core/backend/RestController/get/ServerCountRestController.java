package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ServerCountRestController {

    public ServerCountRestController() {

        StaticCache.getBackend().get(mainPath + "get/servercount", ctx -> {
            ctx.result("" + StaticCache.getRunningServers().size());
            ctx.status(200);
        });

    }

}
