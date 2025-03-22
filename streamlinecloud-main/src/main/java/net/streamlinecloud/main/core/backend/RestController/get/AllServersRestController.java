package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.main.utils.Cache;

import java.util.HashMap;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class AllServersRestController {

    public AllServersRestController() {

        Cache.i().getBackend().get(mainPath + "get/allservers", ctx -> {
            HashMap<String, Integer> serverList = new HashMap<>();

            Cache.i().getRunningServers().forEach(server -> serverList.put(server.getName(), server.getPort()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

    }

}
