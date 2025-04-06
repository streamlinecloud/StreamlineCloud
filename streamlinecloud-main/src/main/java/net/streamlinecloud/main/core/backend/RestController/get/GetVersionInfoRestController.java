package net.streamlinecloud.main.core.backend.RestController.get;

import net.streamlinecloud.main.utils.Settings;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class GetVersionInfoRestController {

    public GetVersionInfoRestController() {
        Cache.i().getBackend().get(mainPath + "get/versionInfo", ctx -> {
            String versionInfo = Settings.getVersionInfo();
            ctx.result(versionInfo).status(200);
        });
    }

}
