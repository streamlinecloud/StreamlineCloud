package net.streamlinecloud.main;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.main.command.*;
import net.streamlinecloud.main.core.backend.remoteLogic.WSClient;
import net.streamlinecloud.main.lang.LangManager;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.CloudLanguage;
import net.streamlinecloud.main.core.backend.BackEndMain;
import net.streamlinecloud.main.terminal.CloudTerminal;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.core.task.ServerStarterTask;
import lombok.Getter;
import lombok.SneakyThrows;
import net.streamlinecloud.main.utils.BuildSettings;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.StreamlineConfig;
import net.streamlinecloud.main.utils.StreamlineSetup;
import org.apache.commons.io.FileUtils;
import org.slf4j.simple.SimpleLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Getter
public class CloudMain {

    //TODO: Wenn ein Server normal gestoppt wird auch im Main Modul erkennen

    @Getter
    private static CloudMain instance;
    private static Cache cache;
    private final CloudTerminal terminal;
    List<CloudCommand> commandMap = new ArrayList<>();

    @SneakyThrows
    public CloudMain(String[] args) throws InterruptedException {

        cache = new Cache();
        cache.setStartuptime(Calendar.getInstance().getTimeInMillis());

        for (String arg : args) {
            if (Cache.i().getArguments().contains(arg)) continue;
            Cache.i().getArguments().add(arg);
        }

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

        if (cache.isFirstLaunch()) {
            new StreamlineSetup();
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
        registerCommand(new WhitelistCommand());

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

}