package io.streamlinemc.main.core.backend.RestController.post;

import com.google.gson.Gson;
import io.streamlinemc.api.packet.VersionPacket;
import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.CloudTerminalRunner;
import io.streamlinemc.main.utils.Cache;

import static io.streamlinemc.main.core.backend.BackEndMain.mainPath;

public class ExecuteCommandController {

    public ExecuteCommandController() {
        Cache.i().getBackend().post(mainPath + "post/command", ctx -> {

            String cmd = ctx.body().replace("\"", "");

            StreamlineCloud.log("Remote Command: " + cmd);
            CloudTerminalRunner.executeCommand(cmd.split(" "));

            ctx.status(200);
        });
    }
}
