package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.main.utils.InternalSettings;
import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class GetVersionInfoRestController {

    public GetVersionInfoRestController() {
        StaticCache.getBackend().get(mainPath + "get/versionInfo", ctx -> {
            String versionInfo = InternalSettings.getVersionInfo();
            ctx.result(versionInfo).status(200);
        });
    }

}
