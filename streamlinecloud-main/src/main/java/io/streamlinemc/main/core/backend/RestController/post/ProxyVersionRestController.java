package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ProxyVersionRestController {

    public ProxyVersionRestController() {
        StaticCache.getBackend().post(mainPath + "post/proxy/version", ctx -> {
            VersionPacket packet = new Gson().fromJson(ctx.body(), VersionPacket.class);

            StaticCache.setPluginVersion(packet.getVersion());
            StaticCache.setPluginApiVersion(packet.getApiVersion());
            StaticCache.setPluginBuildDate(packet.getBuildDate());

            ctx.status(200);
        });
    }

}
