package net.streamlinecloud.main.setup.question;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

public class DefaultSetupQuestion extends SetupQuestion {

    public DefaultSetupQuestion() {
        super(InputType.BOOLEAN, "sc.setup.generateGroups", output -> {
            if (output.equals("yes")) {

                StreamlineSoftware software = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("paper"));
                StreamlineSoftware proxySoftware = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("velocity"));

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

                try {
                    lobby.save();
                    proxy.save();
                } catch (IOException e) {
                    StreamlineCloud.log("sc.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                    return false;
                }

                Utils.runMkdir(new File(Cache.i().homeFile + "/templates/default/proxy").mkdirs());
                Utils.runMkdir(new File(Cache.i().homeFile + "/templates/default/server").mkdirs());

                StreamlineCloud.log("sc.setup.groupsGenerated");

                try {
                    Files.copy(Objects.requireNonNull(Utils.getResourceFile("velocity.toml", "")).toPath(), new File(Cache.i().homeFile + "/templates/default/proxy/velocity.toml").toPath());
                    Files.writeString(Path.of(Cache.i().homeFile + "/templates/default/proxy/forwarding.secret"), StreamlineCloud.generateApiKey(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return true;

            } else {
                return true;
            }
        });
    }
}
