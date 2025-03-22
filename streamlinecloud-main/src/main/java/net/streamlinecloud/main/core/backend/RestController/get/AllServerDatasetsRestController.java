package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

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

                s.setUuid(server.getUuid());
                serverList.add(s);
            }

            ctx.result(new Gson().toJson(serverList));
            ctx.status(200);

        });

    }

}
