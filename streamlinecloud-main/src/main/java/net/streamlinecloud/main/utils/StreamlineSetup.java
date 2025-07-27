package net.streamlinecloud.main.utils;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.core.backend.LoadBalancer;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.software.SoftwareManager;
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

        Cache.i().setConfig(new MainConfig("", 5378, "lobby"));

        new ConsoleQuestion(ConsoleQuestion.InputType.STRING, "Set up language / Gebe eine Sprache ein [en/de]", output -> {

            if (output.contains("en") || output.contains("de")) {

                Cache.i().getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();
                StreamlineCloud.log("lang.welcome");

                String javaPath = System.getProperty("java.home")  + "/bin/java";
                StreamlineCloud.log("Changing the default Java path to " + javaPath);
                Cache.i().getConfig().setDefaultJavaPath(javaPath);
                Cache.i().getConfig().getNetwork().setLoadBalancers(new LoadBalancer[]{new LoadBalancer("MainLoadBalancer", "proxy", 25565)});

                //TODO: ADD EULA ADVICE (Streamline automatically accepts the Minecraft EULA for each server you create (Please visit https://www.minecraft.net/en-us/eula) - Press enter to continue)

                new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "Do you want to enable the whitelist", output1 -> {
                    if (output1.equals("yes")) {
                        Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                        StreamlineCloud.log("Whitelist enabled. You can use the whitelist command to add / remove players");
                    }
                    MainConfig.saveConfig();
                    StreamlineCloud.log("Config generated");

                    new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.generateGroups", output2 -> {

                        if (output2.equals("yes")) {

                            CloudGroup lobby = new CloudGroup(
                                    "lobby",
                                    1,
                                    List.of(),
                                    ServerRuntime.SERVER,
                                    "paper");
                            CloudGroup proxy = new CloudGroup(
                                    "proxy",
                                    1,
                                    List.of(),
                                    ServerRuntime.PROXY,
                                    "velocity");

                            try {
                                lobby.save();
                                proxy.save();
                            } catch (IOException e) {
                                StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                                return;
                            }

                            new File(Cache.i().homeFile + "/templates/default/proxy").mkdirs();
                            new File(Cache.i().homeFile + "/templates/default/server").mkdirs();

                            StreamlineCloud.log("sl.setup.groupsGenerated");
                            StreamlineCloud.log("sl.setup.downloading");

                            SoftwareManager.getInstance().add("paper", ServerRuntime.SERVER, "paper-1.21.8");
                            SoftwareManager.getInstance().add("velocity", ServerRuntime.PROXY, "velocity-3.4.0");

                            boolean downloadServer = StreamlineCloud.download("https://fill-data.papermc.io/v1/objects/7023e1fe3d8a6d9112fde1618d2b4154890b92a91a25a2b05ba7d09864f4360f/paper-1.21.8-17.jar", Cache.i().getHomeFile() + "/data/software/paper-1.21.8", success -> {});
                            boolean downloadProxy = StreamlineCloud.download("https://fill-data.papermc.io/v1/objects/f82780ce33035ebe3d6ea7981f0e6e8a3e41a64f2080ef5c0f1266fada03cbee/velocity-3.4.0-SNAPSHOT-522.jar", Cache.i().getHomeFile() + "/data/software/velocity-3.4.0", success -> {});

                            try {
                                Files.copy(Objects.requireNonNull(Utils.getResourceFile("velocity.toml", "")).toPath(), new File(Cache.i().homeFile + "/templates/default/proxy/velocity.toml").toPath());
                                Files.writeString(Path.of(Cache.i().homeFile + "/templates/default/proxy/forwarding.secret"), new Random().nextInt(999999999) + "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            finishSetup();

                            if (!downloadServer) {

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
