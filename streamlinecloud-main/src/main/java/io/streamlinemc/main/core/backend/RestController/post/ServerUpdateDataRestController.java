package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ServerUpdateDataRestController {

    public ServerUpdateDataRestController() {

        Cache.i().getBackend().post(mainPath + "post/server/updatedata", ctx -> {
            StreamlineServer s = new Gson().fromJson(ctx.body(), StreamlineServer.class);
            CloudServer cs = StreamlineCloud.getServerByName(s.getName());

            if (cs == null) {
                ctx.status(201);
                return;
            }

            cs.setOnlinePlayers(s.getOnlinePlayers());
            cs.setServerState(s.getServerState());
            cs.setServerUseState(s.getServerUseState());
            cs.setMaxOnlineCount(s.getMaxOnlineCount());

            ctx.status(200);
        });

    }

}
