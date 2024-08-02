package io.streamlinemc.main;

import io.streamlinemc.api.server.ServerRuntime;
import io.streamlinemc.main.core.backend.remoteLogic.WSClient;
import io.streamlinemc.main.lang.LangManager;
import io.streamlinemc.main.utils.*;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.command.*;
import io.streamlinemc.main.terminal.input.ConsoleQuestion;
import io.streamlinemc.main.core.backend.BackEndMain;
import io.streamlinemc.main.terminal.CloudTerminal;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.core.task.ServerStarterTask;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.simple.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CloudMain {

    //TODO: Wenn ein Server normal geestoppt wird auch im Main Modul erkännen
    //Test

    @Getter
    private static CloudMain instance;

    @Getter
    private static Cache cache;

    @Getter
    List<CloudCommand> commandMap = new ArrayList<>();

    @Getter
    private final CloudTerminal terminal;

    @SneakyThrows
    public CloudMain(String[] args) throws InterruptedException {

        cache = new Cache();
        cache.setStartuptime(Calendar.getInstance().getTimeInMillis());

        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

        instance = this;
        terminal = new CloudTerminal();

        StreamlineCloud.logSingle("§RED" + StreamlineCloud.streamlineBanner());
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDA powerful Minecraft network");
        StreamlineCloud.logSingle("");
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDVersion: §AQUA" + BuildSettings.version);
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDDeveloped by: §AQUA" + BuildSettings.authors);
        StreamlineCloud.logSingle("§DARK_GRAY-> §REDWebsite: §AQUAhttps://streamlinemc.cloud");
        StreamlineCloud.logSingle("");


        BuildSettings.name = "§REDStreamlineCloud §8-> §RED";
        StreamlineCloud.log("Starting StreamlineCloud");

        //init
        StreamlineConfig.init();
        new LangManager();

        if (new File(cache.homeFile + "/temp").exists()) FileUtils.forceDelete(new File(cache.homeFile + "/temp"));

        new File(cache.homeFile + "/groups").mkdir();
        new File(cache.homeFile + "/data").mkdir();
        new File(cache.homeFile + "/plugins").mkdir();
        new File(cache.homeFile + "/staticservers").mkdir();
        new File(cache.homeFile + "/temp").mkdir();
        new File(cache.homeFile + "/templates").mkdir();



        /////

        if (cache.isFirstLaunch()) {
            StreamlineCloud.log("Launching setup...");
            Thread.sleep(1000);
            launchSetup();
            return;
        }

        if (Cache.i().getConfig() != null) initLang();

        StreamlineCloud.log("lang.welcome");

        if (Cache.i().getConfig().isUseMultiRoot() || Cache.i().getConfig().isUseWebSocket()) {
            Cache.i().setWebSocketClient(new WSClient());
        }

        BackEndMain.startBE();

        Cache.i().getPluginManager().loadPlugins();
        Cache.i().getPluginManager().executeStartup();


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
        registerCommand(new DownloadCommand());

        if (Cache.i().getConfig() != null) Cache.i().setDefaultGroup(new CloudGroup("WITHOUT", Cache.i().getConfig().getDefaultJavaPath(), 0, new ArrayList<>(), ServerRuntime.SERVER));
        Cache.i().getActiveGroups().add(Cache.i().getDefaultGroup());

    }

    public void initLang() {
        for (CloudLanguage lang : Cache.i().getLanguages()) {
            if (lang.getName().equals(Cache.i().getConfig().getLanguage())) Cache.i().setCurrentLanguage(lang);
        }

        if (Cache.i().getCurrentLanguage() == null) {
            StreamlineCloud.log("Lang " + Cache.i().getConfig().getLanguage() + " is invaild. Setting en.json");
            Cache.i().setCurrentLanguage(Cache.i().getLanguages().get(0));
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
                launchSetup();
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