package net.streamlinecloud.main.core.backend.RestController.post;

import com.google.gson.Gson;
import net.streamlinecloud.api.packet.RemoteCommandPacket;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.CloudTerminalRunner;
import net.streamlinecloud.main.utils.Cache;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

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
