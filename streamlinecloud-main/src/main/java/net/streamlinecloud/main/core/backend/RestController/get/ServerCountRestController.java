package net.streamlinecloud.main.core.backend.RestController.get;

import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerCountRestController {

    public ServerCountRestController() {

        Cache.i().getBackend().get(mainPath + "get/servercount", ctx -> {
            ctx.result("" + Cache.i().getRunningServers().size());
            ctx.status(200);
        });

    }

}
