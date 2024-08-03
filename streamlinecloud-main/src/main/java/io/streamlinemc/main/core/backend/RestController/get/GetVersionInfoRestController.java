package io.streamlinemc.main.core.backend.RestController.get;

import io.streamlinemc.main.utils.BuildSettings;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class GetVersionInfoRestController {

    public GetVersionInfoRestController() {
        Cache.i().getBackend().get(mainPath + "get/versionInfo", ctx -> {
            String versionInfo = BuildSettings.getVersionInfo();
            ctx.result(versionInfo).status(200);
        });
    }

}
