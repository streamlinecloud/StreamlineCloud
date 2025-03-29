package net.streamlinecloud.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.backend.socket.ServerSocket;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import javax.websocket.Session;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class ServerUpdateDataRestController {

    public ServerUpdateDataRestController() {

        Cache.i().getBackend().post(mainPath + "post/server/updatedata", ctx -> {
            StreamlineServer s = new Gson().fromJson(ctx.body(), StreamlineServer.class);
            CloudServer cs = StreamlineCloud.getServerByName(s.getName());

            if (cs == null) {
                ctx.status(201);
                return;
            }

            System.out.println("Received update from " + s.getName());

            cs.setOnlinePlayers(s.getOnlinePlayers());
            cs.setServerState(s.getServerState());
            cs.setServerUseState(s.getServerUseState());
            cs.setMaxOnlineCount(s.getMaxOnlineCount());

            for (String session : Cache.serverSocket.servers.keySet()) {
                if (Cache.serverSocket.servers.get(session).contains(s)) {
                    for (StreamlineServer streamlineServer : Cache.serverSocket.servers.get(session)) {
                        if (streamlineServer.getUuid().equals(cs.getUuid())) {
                            Cache.serverSocket.sessionMap.get(session).send(new Gson().toJson(s));
                        }
                    }
                }
            }

            ctx.status(200);
        });

    }

}
