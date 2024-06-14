package io.streamlinemc.main;

import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.main.core.backend.remoteLogic.WSClient;
import io.streamlinemc.main.utils.*;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.command.*;
import io.streamlinemc.main.terminal.input.ConsoleInput;
import io.streamlinemc.main.core.backend.BackEndMain;
import io.streamlinemc.main.terminal.CloudTerminal;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.core.task.ServerStarterTask;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.simple.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CloudMain {

    //TODO: Wenn ein Server normal geestoppt wird auch im Main Modul erkännen

    @Getter
    private static CloudMain instance;

    @Getter
    List<CloudCommand> commandMap = new ArrayList<>();

    @Getter
    private final CloudTerminal terminal;

    @SneakyThrows
    public CloudMain(String[] args) throws InterruptedException {

        StaticCache.setStartuptime(Calendar.getInstance().getTimeInMillis());

        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

        instance = this;
        terminal = new CloudTerminal();

        StreamlineCloud.logSingle("§RED" + StreamlineCloud.streamlineBanner());
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDA powerful Minecraft network");
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDVersion: §AQUA" + InternalSettings.version);
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDDeveloped by: §AQUA" + InternalSettings.authors);
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDWebsite: §AQUAhttps://streamlinemc.cloud");
        StreamlineCloud.logSingle("");


        InternalSettings.name = "§REDStreamlineCloud §8-> §RED";
        StreamlineCloud.log("starting StreamlineCloud");

        FileSystem.init();


        if (StaticCache.isFirstLaunch()) {
            StreamlineCloud.log("Launching setup...");
            Thread.sleep(1000);
            launchSetup();
            return;
        }

        if (StaticCache.getConfig() != null) initLang();

        StreamlineCloud.log("lang.welcome");

        if (StaticCache.getConfig().isUseMultiRoot() || StaticCache.getConfig().isUseWebSocket()) {
            StaticCache.setWebSocketClient(new WSClient());
        }

        BackEndMain.startBE();

        StaticCache.getPluginManager().loadPlugins();
        StaticCache.getPluginManager().executeStartup();


        StreamlineCloud.log("sl.startup.startingServers");
        StreamlineCloud.checkGroups();

        new ServerStarterTask();

        registerCommand(new TestCommand());
        registerCommand(new HelpCommand());
        registerCommand(new ShutDownCommand());
        registerCommand(new GroupsCommand());
        registerCommand(new ServersCommand());
        registerCommand(new TemplatesCommand());
        registerCommand(new VersionCommand());
        registerCommand(new ShortcutsCommand());
        registerCommand(new LanguageCommand());
        registerCommand(new UptimeCommand());
        registerCommand(new MultiRootCommand());

        if (StaticCache.getConfig() != null) StaticCache.setDefaultGroup(new CloudGroup("WITHOUT", StaticCache.getConfig().getDefaultJavaPath(), 0, new ArrayList<>(), ServerRuntime.SERVER));
        StaticCache.getActiveGroups().add(StaticCache.getDefaultGroup());

    }

    public void initLang() {
        for (CloudLanguage lang : StaticCache.getLanguages()) {
            if (lang.getName().equals(StaticCache.getConfig().getLanguage())) StaticCache.setCurrentLanguage(lang);
        }

        if (StaticCache.getCurrentLanguage() == null) {
            StreamlineCloud.log("Lang " + StaticCache.getConfig().getLanguage() + " is invaild. Setting en.json");
            StaticCache.setCurrentLanguage(StaticCache.getLanguages().get(0));
        }
    }

    public void registerCommand(CloudCommand command) {
        commandMap.add(command);
    }


    /*
    * SETUP
    */


    private void launchSetup() {
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

        StaticCache.setConfig(new StreamlineConfig("", 19132, 5378, "lobby"));

        StreamlineCloud.log("Set up language / Gebe eine Sprache ein");
        StreamlineCloud.log("[en/de]");
        new ConsoleInput(ConsoleInput.InputType.STRING, output -> {

            if (output.contains("en") || output.contains("de")) {

                StaticCache.getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();
                StreamlineCloud.log("lang.welcome");
                StreamlineCloud.log("sl.setup.enterJavaPath");

                new ConsoleInput(ConsoleInput.InputType.STRING, output1 -> {
                    StaticCache.getConfig().setDefaultJavaPath(output1);
                    FileSystem.saveConfig();

                    StreamlineCloud.log("sl.setup.generateGroups");
                    new ConsoleInput(ConsoleInput.InputType.BOOLEAN, output2 -> {
                        boolean generate = output2.equals("true");

                        if (generate) {

                            CloudGroup lobby = new CloudGroup(
                                    "lobby",
                                    StaticCache.getConfig().getDefaultJavaPath(),
                                    1,
                                    Arrays.asList(new String[]{}), ServerRuntime.SERVER);
                            CloudGroup proxy = new CloudGroup(
                                    "proxy",
                                    StaticCache.getConfig().getDefaultJavaPath(),
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

                            try {
                                File template_dir = new File(System.getProperty("user.dir") + "/templates/default");
                                Downloader downloader = new Downloader();
                                downloader.download(new URL("https://api.papermc.io/v2/projects/paper/versions/1.20.2/builds/318/downloads/paper-1.20.2-318.jar"), new File(template_dir.getAbsolutePath() + "/server/server.jar"), s -> {

                                    try {
                                        downloader.download(new URL("https://api.papermc.io/v2/projects/waterfall/versions/1.20/builds/560/downloads/waterfall-1.20-560.jar"), new File(template_dir.getAbsolutePath() + "/proxy/server.jar"), s1 -> {

                                            copyStreamlineMc();

                                            StreamlineCloud.log("sl.setup.finished");
                                            StreamlineCloud.shutDown();
                                        });
                                    } catch (MalformedURLException e) {
                                        StreamlineCloud.printError("SetupDownloadFailed", new String[]{"3", "4"}, e);
                                        StreamlineCloud.log("sl.setup.downloadFailed");

                                        copyStreamlineMc();

                                        StreamlineCloud.log("sl.setup.finished");
                                        Thread.sleep(2000);
                                        StreamlineCloud.shutDown();

                                    }

                                });
                            } catch (MalformedURLException e) {
                                StreamlineCloud.printError("SetupDownloadFailed", new String[]{"3", "4"}, e);
                                StreamlineCloud.log("sl.setup.downloadFailed");

                                copyStreamlineMc();

                                StreamlineCloud.log("sl.setup.finished");
                                Thread.sleep(2000);
                                StreamlineCloud.shutDown();
                            }
                        }
                    });
                });
            } else {
                launchSetup();
            }
        });
    }

    private void copyStreamlineMc() {

        try {

            new File("templates/default/server/plugins").mkdirs();
            new File("templates/default/proxy/plugins").mkdirs();

            Files.copy(new File(FileSystem.homeFile + "/streamlinecloud-mc.jar").toPath(),
                    new File(FileSystem.homeFile + "/templates/default/server/plugins/streamlinecloud-mc.jar").toPath());
            Files.copy(new File(FileSystem.homeFile + "/streamlinecloud-mc.jar").toPath(),
                    new File(FileSystem.homeFile + "/templates/default/proxy/plugins/streamlinecloud-mc.jar").toPath());
        } catch (Exception e) {
            StaticCache.getDataCache().add("streamlinecloud-mc-copy-failed");
            e.printStackTrace();
        }

    }
}