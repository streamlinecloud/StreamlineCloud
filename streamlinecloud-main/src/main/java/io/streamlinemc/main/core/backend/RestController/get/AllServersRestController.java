package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.main.utils.StaticCache;

import java.util.HashMap;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class AllServersRestController {

    public AllServersRestController() {

        StaticCache.getBackend().get(mainPath + "get/allservers", ctx -> {
            HashMap<String, Integer> serverList = new HashMap<>();

            StaticCache.getRunningServers().forEach(server -> serverList.put(server.getName(), server.getPort()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

    }

}
