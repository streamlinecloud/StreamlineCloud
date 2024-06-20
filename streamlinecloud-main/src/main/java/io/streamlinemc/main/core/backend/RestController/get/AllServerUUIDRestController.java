package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.main.utils.StaticCache;

import java.util.HashMap;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class AllServerUUIDRestController {

    public AllServerUUIDRestController() {

        StaticCache.getBackend().get(mainPath + "get/allserveruuids", ctx -> {
            HashMap<String, String> serverList = new HashMap<>();

            StaticCache.getRunningServers().forEach(server -> serverList.put(server.getUuid(), server.getName()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

    }

}
