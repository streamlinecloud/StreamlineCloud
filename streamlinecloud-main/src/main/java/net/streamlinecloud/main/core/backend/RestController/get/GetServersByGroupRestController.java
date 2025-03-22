package net.streamlinecloud.main.core.backend.RestController.get;

import com.google.gson.Gson;
import net.streamlinecloud.api.server.StreamlineServer;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.utils.Cache;

import java.util.ArrayList;
import java.util.List;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class GetServersByGroupRestController {

    public GetServersByGroupRestController() {
        Cache.i().getBackend().get(mainPath + "get/servers-by-group/{group}", ctx -> {
            try {
                CloudGroup group = StreamlineCloud.getGroupByName(ctx.pathParam("group"));
                if (group == null) {
                    ctx.result("groupNotFound");
                    ctx.status(200);
                    return;
                }

                List<CloudServer> servers = StreamlineCloud.getGroupOnlineServers(group);

                List<StreamlineServer> streamlineServers = new ArrayList<>();

                for (CloudServer server : servers) {
                    StreamlineServer s = new StreamlineServer();
                    s.setName(server.getName());
                    s.setPort(server.getPort());
                    s.setGroup(server.getGroup());
                    s.setOnlinePlayers(server.getOnlinePlayers());
                    s.setMaxOnlineCount(server.getMaxOnlineCount());
                    s.setServerState(server.getServerState());
                    s.setRconUuid(server.getRconUuid());

                    s.setUuid(server.getUuid());
                    streamlineServers.add(s);
                }

                ctx.result(new Gson().toJson(streamlineServers));
                ctx.status(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
