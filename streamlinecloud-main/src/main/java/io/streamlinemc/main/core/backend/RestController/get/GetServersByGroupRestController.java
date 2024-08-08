package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.Cache;

import java.util.List;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class GetServersByGroupRestController {

    public GetServersByGroupRestController() {
        Cache.i().getBackend().get(mainPath + "get/servers-by-group/{group}", ctx -> {
            CloudGroup group = StreamlineCloud.getGroupByName(ctx.pathParam("group"));
            if (group == null) {
                ctx.result("groupNotFound");
                ctx.status(200);
                return;
            }

            List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(group);

            ctx.result(new Gson().toJson(servers));
            ctx.status(200);
        });
    }
}
