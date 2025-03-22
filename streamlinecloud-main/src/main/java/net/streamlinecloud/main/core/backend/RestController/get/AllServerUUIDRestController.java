package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.main.utils.Cache;

import java.util.HashMap;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class AllServerUUIDRestController {

    public AllServerUUIDRestController() {

        Cache.i().getBackend().get(mainPath + "get/allserveruuids", ctx -> {
            HashMap<String, String> serverList = new HashMap<>();

            Cache.i().getRunningServers().forEach(server -> serverList.put(server.getUuid(), server.getName()));

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);
        });

    }

}
