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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class StreamlineSetup {

    public StreamlineSetup() {
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("StreamlineCloud");
        StreamlineCloud.logSingle("""
                 $$$$$$\\  $$$$$$$$\\ $$$$$$$$\\ $$\\   $$\\ $$$$$$$\\       \s
                $$  __$$\\ $$  _____|\\__$$  __|$$ |  $$ |$$  __$$\\      \s
                $$ /  \\__|$$ |         $$ |   $$ |  $$ |$$ |  $$ |     \s
                \\$$$$$$\\  $$$$$\\       $$ |   $$ |  $$ |$$$$$$$  |     \s
                 \\____$$\\ $$  __|      $$ |   $$ |  $$ |$$  ____/      \s
                $$\\   $$ |$$ |         $$ |   $$ |  $$ |$$ |           \s
                \\$$$$$$  |$$$$$$$$\\    $$ |   \\$$$$$$  |$$ |           \s
                 \\______/ \\________|   \\__|    \\______/ \\__|          \s""");
        StreamlineCloud.logSingle("");

        Cache.i().setConfig(new StreamlineConfig("", 19132, 5378, "lobby"));

        new ConsoleQuestion(ConsoleQuestion.InputType.STRING, "Set up language / Gebe eine Sprache ein [en/de]", output -> {

            if (output.contains("en") || output.contains("de")) {

                Cache.i().getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();
                StreamlineCloud.log("lang.welcome");

                String javaPath = System.getProperty("java.home")  + "/bin/java";
                StreamlineCloud.log("Changing the default Java path to " + javaPath);
                Cache.i().getConfig().setDefaultJavaPath(javaPath);

                new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "Do you want to enable the whitelist", output1 -> {
                    if (output1.equals("yes")) {
                        Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                        StreamlineCloud.log("Whitelist enabled. You can use the whitelist command to add / remove players");
                    }
                    StreamlineConfig.saveConfig();
                    StreamlineCloud.log("Config generated");

                    new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.generateGroups", output2 -> {

                        if (output2.equals("yes")) {

                            CloudGroup lobby = new CloudGroup(
                                    "lobby",
                                    1,
                                    List.of(), ServerRuntime.SERVER);
                            CloudGroup proxy = new CloudGroup(
                                    "proxy",
                                    1,
                                    List.of(), ServerRuntime.PROXY);

                            try {
                                lobby.save();
                                proxy.save();
                            } catch (IOException e) {
                                StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                                return;
                            }

                            StreamlineCloud.log("sl.setup.groupsGenerated");
                            StreamlineCloud.log("sl.setup.downloading");

                            boolean download = StreamlineCloud.download("https://api.papermc.io/v2/projects/velocity/versions/3.4.0-SNAPSHOT/builds/483/downloads/velocity-3.4.0-SNAPSHOT-483.jar", "default/proxy", proxySuccess ->  {
                                StreamlineCloud.download("https://api.papermc.io/v2/projects/paper/versions/1.20.2/builds/318/downloads/paper-1.20.2-318.jar", "default/server", lobbySuccess -> {
                                });
                            });

                            if (download) {

                                finishSetup();

                                try {
                                    Files.copy(Objects.requireNonNull(Utils.getResourceFile("velocity/velocity.toml", "")).toPath(), new File(Cache.i().homeFile + "/templates/default/proxy/velocity.toml").toPath());
                                    Files.writeString(Path.of(Cache.i().homeFile + "/templates/default/proxy/forwarding.secret"), new Random().nextInt(999999999) + "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {

                                StreamlineCloud.logError("WARNING: We couldn't find server jar files. StreamlineCloud wont work out of the box!");

                                Thread.sleep(5000);
                                finishSetup();
                            }

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
        StreamlineCloud.log("sl.setup.finished");
        StreamlineCloud.shutDown();
    }

}
