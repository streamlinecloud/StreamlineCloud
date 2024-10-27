package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.main.utils.Cache;

import java.util.Calendar;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class WhitelistRestController {

    public WhitelistRestController() {
        Cache.i().getBackend().get(mainPath + "get/whitelist", ctx -> {

            if (!Cache.i().config.isWhitelistEnabled()) {
                ctx.result("false");
                ctx.status(200);
                return;
            }

            ctx.result(new Gson().toJson(Cache.i().config.getWhitelist()));
            ctx.status(200);

        });
    }

}
