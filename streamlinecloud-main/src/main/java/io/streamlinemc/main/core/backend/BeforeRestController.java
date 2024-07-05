package io.streamlinemc.main.core.backend;

import io.streamlinemc.main.utils.StaticCache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;


public class BeforeRestController {

    public BeforeRestController() {
        StaticCache.getBackend().before(ctx -> {
            if (ctx.path().equals(mainPath + "ping")) return;

            if (ctx.header("auth_key") == null) {
                ctx.result("authFailed");
                ctx.status(401);
                ctx.res().sendError(401);
                return;
            }


            try {
                String key = ctx.header("auth_key");

                if (!StaticCache.getApiKey().equals(key)) {
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
