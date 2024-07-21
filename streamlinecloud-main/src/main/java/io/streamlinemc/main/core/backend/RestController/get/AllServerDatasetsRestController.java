package io.streamlinemc.main.core.backend.RestController.get;

import com.google.gson.Gson;
import io.streamlinemc.api.server.StreamlineServer;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class AllServerDatasetsRestController {

    public AllServerDatasetsRestController() {

        Cache.i().getBackend().get(mainPath + "get/dataset/servers", ctx -> {

            List<StreamlineServer> serverList = new ArrayList<>();

            for (CloudServer server : Cache.i().getRunningServers()) {
                StreamlineServer s = new StreamlineServer();
                s.setName(server.getName());
                s.setPort(server.getPort());
                s.setGroup(server.getGroup());
                s.setOnlinePlayers(server.getOnlinePlayers());
                s.setMaxOnlineCount(server.getMaxOnlineCount());
                s.setServerState(server.getServerState());
                s.setRconUuid(server.getRconUuid());

                System.out.println(server.getUuid());

                s.setUuid(server.getUuid());
                serverList.add(s);
            }

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);

        });

    }

}
