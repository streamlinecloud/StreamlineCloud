package net.streamlinecloud.main.core.backend;

import com.google.gson.Gson;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;

import java.util.Arrays;
import java.util.Objects;

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

                if (Objects.equals(key, Cache.i().getApiKey())) return;

                System.out.println("Looking for custom session with key: " + key + " ...");
                if (BackEndMain.customSessions.stream().noneMatch(s -> s.getKey().equals(key))) {
                    System.out.println("No custom session found");
                    ctx.result("authFailed");
                    ctx.status(401);
                    ctx.res().sendError(401);
                    return;
                }

                for (BackendSession session : BackEndMain.customSessions) {
                    System.out.println("Custom session: " + new Gson().toJson(session));
                    System.out.println("Path: " + ctx.path());
                    if (!session.getKey().equals(key)) continue;
                    if (session.adminAccess) return;
                    if (Arrays.asList(session.getAllowedRequests()).contains(ctx.path())) return;
                    for (String request : session.getAllowedRequests()) {
                        if (request.endsWith("/*") && ctx.path().startsWith(request)) return;
                    }

                    System.out.println("Auth failed");
                    ctx.result("authFailed");
                    ctx.status(401);
                    ctx.res().sendError(401);
                    return;
                }

                System.out.println("Auth success");

            } catch (Exception e) {
                StreamlineCloud.logError(e.getMessage());
            }
        });
    }

}
