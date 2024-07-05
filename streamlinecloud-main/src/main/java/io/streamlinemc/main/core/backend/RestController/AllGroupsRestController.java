package io.streamlinemc.main.core.backend.RestController;

import com.google.gson.Gson;
import io.streamlinemc.main.utils.StaticCache;

import java.util.ArrayList;
import java.util.List;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class AllGroupsRestController {

    public AllGroupsRestController() {
        StaticCache.getBackend().get(mainPath + "get/allgroups", ctx -> {
            List<String> groups = new ArrayList<>();

            StaticCache.getActiveGroups().forEach(g -> groups.add(g.getName()));

            ctx.result(new Gson().toJson(groups));
            ctx.status(200);
        });
    }

}
