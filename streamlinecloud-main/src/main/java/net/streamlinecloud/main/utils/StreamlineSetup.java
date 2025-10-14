package net.streamlinecloud.main.utils;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.software.StreamlineSoftware;
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
import java.util.*;

public class StreamlineSetup {

    List<ConsoleQuestion> questions = new ArrayList<>();
    int current = 0;

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

        addQuestions();
        next();

    }

    public void addQuestions() {
        /*
        SETUP LANG
        */
        questions.add(new ConsoleQuestion(ConsoleQuestion.InputType.STRING, "Set up language / Gebe eine Sprache ein [en/de]", output -> {

            if (output.contains("en") || output.contains("de")) {

                Cache.i().getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();
                StreamlineCloud.log("lang.welcome");

                String javaPath = System.getProperty("java.home") + "/bin/java";
                StreamlineCloud.log("sl.setup.changingPath", new ReplacePaket[]{new ReplacePaket("%0", javaPath)});
                Cache.i().getConfig().setDefaultJavaPath(javaPath);
                Cache.i().getConfig().getNetwork().setLoadBalancers(List.of(new LoadBalancer("MainLoadBalancer", "proxy", 25565)));
                next();

            } else {
                new StreamlineSetup();
            }
        }));

        /*
        EULA ADVICE
        */
        questions.add(new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.eula", output -> {
            if (output.equals("yes")) {
                StreamlineCloud.log("sl.setup.eulaAccepted");
                next();
            } else if (output.equals("no")) {
                StreamlineCloud.shutDown();
            }
        }));

        /*
        WHITELIST
        */
        questions.add(new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.enableWhitelist", output1 -> {
            if (output1.equals("yes")) {
                Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                StreamlineCloud.log("sl.setup.whitelistEnabled");
            }
            MainConfig.saveConfig();
            StreamlineCloud.log("sl.setup.configGenerated");
            next();
        }));

        /*
        DEFAULT SETUP
        */
        questions.add(new ConsoleQuestion(ConsoleQuestion.InputType.BOOLEAN, "sl.setup.generateGroups", output2 -> {

            if (output2.equals("yes")) {

                StreamlineSoftware software = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("paper"));
                StreamlineSoftware proxySoftware = SoftwareManager.getInstance().add(SoftwareManager.getInstance().getLatestSoftware("velocity"));

                Cache.i().getConfig().setDefaultSoftwareName(software.getName());
                StreamlineCloud.logSingle("");
                StreamlineCloud.log("sl.setup.downloaded", new ReplacePaket[]{new ReplacePaket("%0", software.getName()), new ReplacePaket("%1", proxySoftware.getName())});

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
                    StreamlineCloud.log("sl.command.groups.create.cantSave", new ReplacePaket[]{new ReplacePaket("%1", e.getMessage())});
                    return;
                }

                Utils.runMkdir(new File(Cache.i().homeFile + "/templates/default/proxy").mkdirs());
                Utils.runMkdir(new File(Cache.i().homeFile + "/templates/default/server").mkdirs());

                StreamlineCloud.log("sl.setup.groupsGenerated");

                try {
                    Files.copy(Objects.requireNonNull(Utils.getResourceFile("velocity.toml", "")).toPath(), new File(Cache.i().homeFile + "/templates/default/proxy/velocity.toml").toPath());
                    Files.writeString(Path.of(Cache.i().homeFile + "/templates/default/proxy/forwarding.secret"), new Random().nextInt(999999999) + "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                finishSetup();

            } else {
                finishSetup();
            }
        }));
    }

    public void next() {
        questions.get(current).start();
        current++;
    }

    private void finishSetup() {
        MainConfig.saveConfig();
        StreamlineCloud.log("sl.setup.finished");
        StreamlineCloud.shutDown();
    }

}
