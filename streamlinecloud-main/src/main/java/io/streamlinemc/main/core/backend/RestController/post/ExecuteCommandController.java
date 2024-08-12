package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.RemoteCommandPacket;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.CloudTerminalRunner;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ExecuteCommandController {

    public ExecuteCommandController() {
        Cache.i().getBackend().post(mainPath + "post/command", ctx -> {

            RemoteCommandPacket packet = new Gson().fromJson(ctx.body(), RemoteCommandPacket.class);

            StreamlineCloud.log("Remote Command: §YELLOW" + packet.getCommand() + " §RED(from §YELLOW" + packet.getServer() + " §REDby §YELLOW" + packet.getExecutor() + "§RED)");
            CloudTerminalRunner.executeCommand(packet.getCommand().split(" "));

            ctx.status(200);
        });
    }
}
