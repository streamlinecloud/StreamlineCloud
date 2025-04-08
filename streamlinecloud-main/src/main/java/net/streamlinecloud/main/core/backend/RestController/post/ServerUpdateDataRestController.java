package net.streamlinecloud.main.core.backend.RestController.post;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.api.server.StreamlineServerSerializer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

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

            cs.setOnlinePlayers(s.getOnlinePlayers());
            cs.setServerState(s.getServerState());
            cs.setServerUseState(s.getServerUseState());
            cs.setMaxOnlineCount(s.getMaxOnlineCount());

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CloudServer.class, new StreamlineServerSerializer())
                    .create();

            for (String session : Cache.serverSocket.servers.keySet()) {
                for (StreamlineServer server : Cache.serverSocket.servers.get(session)) {
                    if (server.getUuid().equals(s.getUuid())) {
                        for (StreamlineServer streamlineServer : Cache.serverSocket.servers.get(session)) {
                            if (streamlineServer.getUuid().equals(cs.getUuid())) {
                                Cache.serverSocket.sessionMap.get(session).send(gson.toJson(s));
                            }
                        }
                    }
                }
            }

            ctx.status(200);
        });

    }

}
