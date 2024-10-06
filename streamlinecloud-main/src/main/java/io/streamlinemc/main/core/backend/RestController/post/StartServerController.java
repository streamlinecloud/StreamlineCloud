package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.StartServerPacket;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.Cache;

import java.util.Arrays;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class StartServerController {

    public StartServerController() {
        Cache.i().getBackend().post(mainPath + "post/server/start", ctx -> {
            StartServerPacket packet = new Gson().fromJson(ctx.body(), StartServerPacket.class);

            ctx.result(StreamlineCloud.startServerByGroup(StreamlineCloud.getGroupByName(packet.getGroup()), Arrays.asList(packet.getTemplates())));
            ctx.status(200);
        });

    }
}
