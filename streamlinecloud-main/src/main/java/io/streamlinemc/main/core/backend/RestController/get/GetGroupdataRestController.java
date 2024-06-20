package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.api.group.StreamlineGroup;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class GetGroupdataRestController {

    public GetGroupdataRestController() {
        StaticCache.getBackend().get(mainPath + "get/groupdata/{name}", ctx -> {
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
