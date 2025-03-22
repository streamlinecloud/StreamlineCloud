package net.streamlinecloud.main.core.backend.RestController;

import com.google.gson.Gson;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class AllGroupsRestController {

    public AllGroupsRestController() {
        Cache.i().getBackend().get(mainPath + "get/allgroups", ctx -> {
            List<String> groups = new ArrayList<>();

            Cache.i().getActiveGroups().forEach(g -> groups.add(g.getName()));

            ctx.result(new Gson().toJson(groups));
            ctx.status(200);
        });
    }

}
