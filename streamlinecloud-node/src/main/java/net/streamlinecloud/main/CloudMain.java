package net.streamlinecloud.main;

import kotlin.Unit;
import net.streamlinecloud.api.util.StreamlineState;
import net.streamlinecloud.client.core.StreamlineApiClient;
import net.streamlinecloud.main.command.*;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.config.StreamlineConfig;
import net.streamlinecloud.main.backend.LoadBalancer;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.core.software.SoftwareConfig;
import net.streamlinecloud.main.core.software.SoftwareManager;
import net.streamlinecloud.main.lang.LangManager;
import net.streamlinecloud.main.lang.CloudLanguage;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.setup.StreamlineSetup;
import net.streamlinecloud.main.setup.question.DefaultSetupQuestion;
import net.streamlinecloud.main.setup.question.EulaQuestion;
import net.streamlinecloud.main.setup.question.LangQuestion;
import net.streamlinecloud.main.setup.question.WhitelistQuestion;
import net.streamlinecloud.main.terminal.CloudTerminal;
import net.streamlinecloud.main.terminal.NodeLogger;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import lombok.Getter;
import lombok.SneakyThrows;
import net.streamlinecloud.main.utils.*;
import net.streamlinecloud.main.utils.MainBuildConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.simple.SimpleLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Getter
public class CloudMain {

    @Getter
    private static CloudMain instance;
    private final CloudTerminal terminal;
    private final StreamlineApiClient apiClient;
    List<CloudCommand> commandMap = new ArrayList<>();

    @SneakyThrows
    public CloudMain(String[] args) {

        Cache cache = new Cache();
        cache.setStartUptime(Calendar.getInstance().getTimeInMillis());
        cache.setStreamlineState(StreamlineState.INITIALIZING);

        if (Arrays.asList(args).contains("-debug")) Cache.i().setDebugMode(true);

        for (String arg : args) {
            if (Cache.i().getArguments().contains(arg)) continue;
            Cache.i().getArguments().add(arg);
        }

        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");

        Utils.runMkdir(new File(cache.homeFile + "/data/software").mkdirs());
        Utils.runMkdir(new File(cache.homeFile + "/extensions").mkdir());
        Utils.runMkdir(new File(cache.homeFile + "/static").mkdir());
        Utils.runMkdir(new File(cache.homeFile + "/temp").mkdir());
        Utils.runMkdir(new File(cache.homeFile + "/templates").mkdir());

        new LangManager();

        instance = this;
        terminal = new CloudTerminal();

        NodeLogger.logSingle("§RED" + StreamlineCloud.streamlineBanner());
        NodeLogger.logSingle("");
        NodeLogger.logSingle("§DARK_GRAY-> §REDA streamlined Minecraft network");
        NodeLogger.logSingle("");
        NodeLogger.logSingle("§DARK_GRAY-> §REDVersion: §AQUA" + MainBuildConfig.VERSION);
        NodeLogger.logSingle("§DARK_GRAY-> §REDDeveloped by: §AQUA" + Settings.authors);
        NodeLogger.logSingle("§DARK_GRAY-> §REDWebsite: §AQUA" + Settings.website);
        NodeLogger.logSingle("");

        Settings.name = "§REDStreamlineCloud §8-> §RED";
        StreamlineCloud.log("Starting StreamlineCloud");

        MainConfig.init();

        if (Cache.i().getConfig() != null) initLang();
        StreamlineCloud.log("lang.welcome");

        new SoftwareManager();
        SoftwareManager.getInstance().setConfig(new StreamlineConfig(new SoftwareConfig(), cache.homeFile + "/data/software/software.json"));
        SoftwareManager.getInstance().getConfig().init();

        /*try {
            BackEndMain.startBE();
        } catch (JavalinBindException ignored) {
            StreamlineCloud.log("sc.error.portAlreadyInUse", new ReplacePaket[]{new ReplacePaket("%0", Cache.i().getConfig().getNetwork().getBackendPort() + "")});
            StreamlineCloud.shutDown();
            return;
        }*/

        new RunningServerManager();

        if (new File(cache.homeFile + "/temp").exists()) FileUtils.forceDelete(new File(cache.homeFile + "/temp"));

        Cache.i().getPluginManager().loadExtensions();
        Cache.i().getPluginManager().executeStartup();

        if (cache.isFirstLaunch()) {
            new StreamlineSetup(new SetupQuestion[]{
                    new LangQuestion(),
                    new EulaQuestion(),
                    new WhitelistQuestion(),
                    new DefaultSetupQuestion()
            });
            apiClient = null;
            return;
        }

        apiClient = new StreamlineApiClient(
                Cache.i().getConfig().getNetwork().getBackendUrl(),
                Cache.i().getConfig().getNetwork().getBackendSocketUrl(),
                Cache.i().getConfig().getNetwork().getBackendAccessKey(),
                StreamlineCloud.getLogger(),
                error -> {
                    StreamlineCloud.getLogger().error("Backend connection error: " + error + "- make sure your backend is online, reachable and that your credentials are correct. Check out the NetworkConfig part in your data/config.json!");
                    StreamlineCloud.shutDown();
                    return Unit.INSTANCE;
                }
        );
        new Thread(apiClient::connect).start();

        StreamlineCloud.injectClient(apiClient);

        String playerSpreading = Cache.i().getConfig().getFallback().getFallbackPlayerSpreading();
        if (!(playerSpreading.equals("SPLIT") || playerSpreading.equals("BUNDLE") || playerSpreading.equals("RANDOM"))) {
            StreamlineCloud.getLogger().error("The config option '" + playerSpreading + "' for fallbackPlayerSpreading is invalid. Set it to 'SPLIT' or 'BUNDLE'");
            StreamlineCloud.log("Changed config value fallbackPlayerSpreading to 'RANDOM'");
            Cache.i().getConfig().getFallback().setFallbackPlayerSpreading("RANDOM");
        }

        for (LoadBalancer loadBalancer : Cache.i().getConfig().getNetwork().getLoadBalancers()) loadBalancer.start();

        RunningServerManager.getInstance().startServersIfNeeded();

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
        registerCommand(new SoftwareCommand());
        registerCommand(new ScreenCommand());
        registerCommand(new LoadBalancerCommand());
        registerCommand(new SendToScreenCommand());
        registerCommand(new ExitCommand());

        StreamlineCloud.getCommandManager().getCommandMap().add(new NodesCommand());

        Cache.i().setStreamlineState(StreamlineState.RUNNING);

    }

    public void initLang() {
        for (CloudLanguage lang : LangManager.getInstance().getLanguages()) {
            if (lang.getName().equals(Cache.i().getConfig().getLanguage())) LangManager.getInstance().setCurrentLanguage(lang);
        }

        if (LangManager.getInstance().getCurrentLanguage() == null) {
            StreamlineCloud.log("Lang " + Cache.i().getConfig().getLanguage() + " is invalid. Loading en.json");
            LangManager.getInstance().setCurrentLanguage(LangManager.getInstance().getLanguages().getFirst());
        }
    }

    public void registerCommand(CloudCommand command) {
        commandMap.add(command);
    }

}