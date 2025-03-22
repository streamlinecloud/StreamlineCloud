package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.api.group.StreamlineGroup;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class GetGroupdataRestController {

    public GetGroupdataRestController() {
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
