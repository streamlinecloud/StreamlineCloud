package net.streamlinecloud.main.core.backend.controller;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class GroupsController {

    public GroupsController() {
        Cache.i().getBackend().get(mainPath + "get/allgroups", ctx -> {
            List<String> groups = new ArrayList<>();

            Cache.i().getActiveGroups().forEach(g -> groups.add(g.getName()));

            ctx.result(new Gson().toJson(groups));
            ctx.status(200);
        });

        Cache.i().getBackend().get(mainPath + "get/groupdata/{name}", ctx -> {
            String name = ctx.pathParam("name");
            StreamlineGroup server = StreamlineCloud.getGroupByName(name);

            if (server != null) {
                ctx.result(new Gson().toJson(server, StreamlineGroup.class));
                ctx.status(200);
            } else {
                ctx.result("serverNotFound");
                ctx.status(601);
            }
        });
    }
}
