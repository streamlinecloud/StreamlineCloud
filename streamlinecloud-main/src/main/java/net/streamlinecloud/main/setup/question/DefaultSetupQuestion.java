package net.streamlinecloud.main.setup.question;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.group.CloudGroupManager;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.utils.Cache;

import java.util.List;

public class DefaultSetupQuestion extends SetupQuestion {

    public DefaultSetupQuestion() {
        super(InputType.BOOLEAN, "sc.setup.generateGroups", output -> {
            if (output.equals("yes")) {

                StreamlineSoftware software = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("paper"));
                StreamlineSoftware proxySoftware = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("velocity"));
                CloudGroupManager groupManager = CloudGroupManager.getInstance();

                Cache.i().getConfig().setDefaultSoftwareName(software.getName());
                StreamlineCloud.logSingle("");
                StreamlineCloud.log("sc.setup.downloaded", new ReplacePaket[]{new ReplacePaket("%0", software.getName()), new ReplacePaket("%1", proxySoftware.getName())});

                CloudGroup lobby = new CloudGroup(
                        "lobby",
                        1,
                        List.of(),
                        ServerRuntime.SERVER,
                        "default");
                CloudGroup proxy = new CloudGroup(
                        "proxy",
                        1,
                        List.of(),
                        ServerRuntime.PROXY,
                        proxySoftware.getName());

                if (!(groupManager.create(lobby) && groupManager.create(proxy))) {
                    StreamlineCloud.log("sc.command.groups.create.cantSave");
                    return false;
                }

                StreamlineCloud.log("sc.setup.groupsGenerated");
                return true;

            } else {
                return true;

            }
        });
    }
}
