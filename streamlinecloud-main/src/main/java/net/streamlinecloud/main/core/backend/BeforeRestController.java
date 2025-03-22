package net.streamlinecloud.main.core.backend;

import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;


public class BeforeRestController {

    public BeforeRestController() {
        Cache.i().getBackend().before(ctx -> {
            if (ctx.path().equals(mainPath + "ping")) return;

            if (ctx.header("auth_key") == null) {
                ctx.result("authFailed");
                ctx.status(401);
                ctx.res().sendError(401);
                return;
            }


            try {
                String key = ctx.header("auth_key");

                if (!Cache.i().getApiKey().equals(key)) {
                    ctx.result("authFailed");
                    ctx.status(401);
                    ctx.res().sendError(401);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
