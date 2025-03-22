package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class InfoRestController {

    public InfoRestController() {

        Cache.i().getBackend().get(mainPath + "get/info", ctx -> {

            if (ctx.header("servername") == null) {
                ctx.result("Request Failed: Servername null");
                ctx.status(604);
            }

            ctx.header("servername");
            StreamlineCloud.log(ctx.header("servername"));

            for (CloudServer runningServer : Cache.i().getRunningServers()) {
                if (runningServer.getName().equals(ctx.header("servername"))) {
                    ctx.result(new Gson().toJson(runningServer, CloudServer.class));
                    ctx.status(200);
                    return;
                }

            }
        });

    }

}
