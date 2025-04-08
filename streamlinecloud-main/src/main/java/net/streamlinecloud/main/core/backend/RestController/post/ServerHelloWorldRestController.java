package net.streamlinecloud.main.core.backend.RestController.post;

import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerHelloWorldRestController {

    public ServerHelloWorldRestController() {
        Cache.i().getBackend().post(mainPath + "post/server/hello-world", ctx -> {

            CloudServer ser = StreamlineCloud.getServerByUuid(ctx.body().replace('"', ' ').replace(" ", ""));

            if (ser == null) {
                StreamlineCloud.log("Server not found");
                ctx.status(201);
                return;
            }

            StreamlineCloud.log("sl.server.online", new ReplacePaket[]{new ReplacePaket("%1", ser.getName() + "-" + ser.getShortUuid())});
            ser.setServerState(ServerState.ONLINE);

            ctx.status(201);

        });
    }

}
