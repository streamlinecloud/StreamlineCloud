package net.streamlinecloud.main.utils;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.terminal.input.ConsoleQuestion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class StreamlineSetup {

    public StreamlineSetup() {
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("StreamlineCloud");
        StreamlineCloud.logSingle(" $$$$$$\\  $$$$$$$$\\ $$$$$$$$\\ $$\\   $$\\ $$$$$$$\\        \n" +
                "$$  __$$\\ $$  _____|\\__$$  __|$$ |  $$ |$$  __$$\\       \n" +
                "$$ /  \\__|$$ |         $$ |   $$ |  $$ |$$ |  $$ |      \n" +
                "\\$$$$$$\\  $$$$$\\       $$ |   $$ |  $$ |$$$$$$$  |      \n" +
                " \\____$$\\ $$  __|      $$ |   $$ |  $$ |$$  ____/       \n" +
                "$$\\   $$ |$$ |         $$ |   $$ |  $$ |$$ |            \n" +
                "\\$$$$$$  |$$$$$$$$\\    $$ |   \\$$$$$$  |$$ |            \n" +
                " \\______/ \\________|   \\__|    \\______/ \\__|           ");
        StreamlineCloud.logSingle("");

        Cache.i().setConfig(new StreamlineConfig("", 19132, 5378, "lobby"));

        new ConsoleQuestion(ConsoleQuestion.InputType.STRING, "Set up language / Gebe eine Sprache ein [en/de]", output -> {

            if (output.contains("en") || output.contains("de")) {

                Cache.i().getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();
                StreamlineCloud.log("lang.welcome");

                new ConsoleQuestion(ConsoleQuestion.InputType.STRING, "sl.setup.enterJavaPath", output1 -> {
                    Cache.i().getConfig().setDefaultJavaPath(output1);
                    StreamlineConfig.saveConfig();

                    new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.generateGroups", output2 -> {

                        if (output2.equals("yes")) {

                            CloudGroup lobby = new CloudGroup(
                                    "lobby",
                                    Cache.i().getConfig().getDefaultJavaPath(),
                                    1,
                                    Arrays.asList(new String[]{}), ServerRuntime.SERVER);
                            CloudGroup proxy = new CloudGroup(
                                    "proxy",
                                    Cache.i().getConfig().getDefaultJavaPath(),
                                    1,
                                    Arrays.asList(new String[]{}), ServerRuntime.PROXY);

                            try {
                                lobby.save();
                                proxy.save();
                            } catch (IOException e) {
                                StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                                return;
                            }

                            StreamlineCloud.log("sl.setup.groupsGenerated");
                            StreamlineCloud.log("sl.setup.downloading");

                            StreamlineCloud.download("https://api.papermc.io/v2/projects/waterfall/versions/1.20/builds/560/downloads/waterfall-1.20-560.jar", "default/proxy", proxySuccess ->  {
                                StreamlineCloud.download("https://api.papermc.io/v2/projects/paper/versions/1.20.2/builds/318/downloads/paper-1.20.2-318.jar", "default/server", lobbySuccess -> {
                                    finishSetup();
                                });
                            });

                        } else {
                            finishSetup();
                        }
                    });
                });
            } else {
                new StreamlineSetup();
            }
        });
    }

    private void finishSetup() {
        copyStreamlineMc();
        StreamlineCloud.log("sl.setup.finished");
        StreamlineCloud.shutDown();
    }

    private void copyStreamlineMc() {

        try {

            new File("templates/default/server/plugins").mkdirs();
            new File("templates/default/proxy/plugins").mkdirs();

            Files.copy(new File(Cache.i().homeFile + "/streamlinecloud-mc.jar").toPath(),
                    new File(Cache.i().homeFile + "/templates/default/server/plugins/streamlinecloud-mc.jar").toPath());
            Files.copy(new File(Cache.i().homeFile + "/streamlinecloud-mc.jar").toPath(),
                    new File(Cache.i().homeFile + "/templates/default/proxy/plugins/streamlinecloud-mc.jar").toPath());
        } catch (Exception e) {
            Cache.i().getDataCache().add("streamlinecloud-mc-copy-failed");
        }

    }
}
