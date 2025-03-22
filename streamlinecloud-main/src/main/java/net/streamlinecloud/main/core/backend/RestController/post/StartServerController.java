package net.streamlinecloud.main.core.backend.RestController.post;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.StartServerPacket;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

import java.util.Arrays;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class StartServerController {

    public StartServerController() {
        Cache.i().getBackend().post(mainPath + "post/server/start", ctx -> {
            StreamlineCloud.log("Start server POST");

            StartServerPacket packet = new Gson().fromJson(ctx.body(), StartServerPacket.class);

            ctx.result(StreamlineCloud.startServerByGroup(StreamlineCloud.getGroupByName(packet.getGroup()), Arrays.asList(packet.getTemplates())));
            ctx.status(200);
        });

    }
}
