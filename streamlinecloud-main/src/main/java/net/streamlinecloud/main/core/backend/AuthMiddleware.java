package net.streamlinecloud.main.core.backend;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

public class AuthMiddleware {

    public AuthMiddleware() {
        Cache.i().getBackend().before(ctx -> {

            if (BackEndMain.publicRoutes.contains(ctx.path())) return;

            if (ctx.header("auth_key") == null) {
                ctx.result("authFailed");
                ctx.status(401);
                ctx.res().sendError(401);
                return;
            }

            try {
                String key = ctx.header("auth_key");

                if (!Cache.i().getApiKey().equals(key) && !BackEndMain.additionalKeys.contains(key)) {
                    ctx.result("authFailed");
                    ctx.status(401);
                    ctx.res().sendError(401);
                }
            } catch (Exception e) {
                StreamlineCloud.logError(e.getMessage());
            }
        });
    }

}
