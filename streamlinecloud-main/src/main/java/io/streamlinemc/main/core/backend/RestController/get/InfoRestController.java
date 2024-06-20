package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class InfoRestController {

    public InfoRestController() {

        StaticCache.getBackend().get(mainPath + "get/info", ctx -> {

            if (ctx.header("servername") == null) {
                ctx.result("Request Failed: Servername null");
                ctx.status(604);
            }

            ctx.header("servername");
            StreamlineCloud.log(ctx.header("servername"));

            for (CloudServer runningServer : StaticCache.getRunningServers()) {
                if (runningServer.getName().equals(ctx.header("servername"))) {
                    ctx.result(new Gson().toJson(runningServer, CloudServer.class));
                    ctx.status(200);
                    return;
                }

            }
        });

    }

}
