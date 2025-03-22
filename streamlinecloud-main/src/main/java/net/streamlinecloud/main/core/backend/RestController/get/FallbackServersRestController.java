package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class FallbackServersRestController {

    public FallbackServersRestController() {
        Cache.i().getBackend().get(mainPath + "get/fallbackservers", ctx -> {
            List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(StreamlineCloud.getGroupByName(Cache.i().getConfig().getFallbackGroup()));
            List<String> names = new ArrayList<>();
            for (CloudServer s : servers) names.add(s.getName());

            ctx.result(new Gson().toJson(names));
            ctx.status(200);
        });
    }

}
