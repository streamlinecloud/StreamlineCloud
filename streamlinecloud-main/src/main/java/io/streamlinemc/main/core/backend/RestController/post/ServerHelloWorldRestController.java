package io.streamlinemc.main.core.backend.RestController.post;

import io.streamlinemc.api.server.ServerState;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ServerHelloWorldRestController {

    public ServerHelloWorldRestController() {
        Cache.i().getBackend().post(mainPath + "post/server/hello-world", ctx -> {

            CloudServer ser = StreamlineCloud.getServerByUuid(ctx.body().replace('"', ' ').replace(" ", ""));

            if (ser == null) {
                StreamlineCloud.log("Server not found");
                ctx.status(201);
                return;
            }

            StreamlineCloud.log("sl.server.online", new ReplacePaket[]{new ReplacePaket("%1", ser.getName() + "-" + ser.getUuid())});
            ser.setServerState(ServerState.ONLINE);

            ctx.status(201);

        });
    }

}
