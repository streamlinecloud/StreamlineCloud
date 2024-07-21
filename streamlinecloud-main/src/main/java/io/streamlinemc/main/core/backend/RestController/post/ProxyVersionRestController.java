package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ProxyVersionRestController {

    public ProxyVersionRestController() {
        Cache.i().getBackend().post(mainPath + "post/proxy/version", ctx -> {
            VersionPacket packet = new Gson().fromJson(ctx.body(), VersionPacket.class);

            Cache.i().setPluginVersion(packet.getVersion());
            Cache.i().setPluginApiVersion(packet.getApiVersion());
            Cache.i().setPluginBuildDate(packet.getBuildDate());

            ctx.status(200);
        });
    }

}
