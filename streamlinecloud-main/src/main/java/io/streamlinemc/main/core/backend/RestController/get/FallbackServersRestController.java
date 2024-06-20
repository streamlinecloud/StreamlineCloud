package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.StaticCache;

import java.util.ArrayList;
import java.util.List;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class FallbackServersRestController {

    public FallbackServersRestController() {
        StaticCache.getBackend().get(mainPath + "get/fallbackservers", ctx -> {
            List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName(StaticCache.getConfig().getFallbackGroup()));
            List<String> names = new ArrayList<>();
            for (CloudServer s : servers) names.add(s.getName());

            ctx.result(new Gson().toJson(names));
            ctx.status(200);
        });
    }

}
