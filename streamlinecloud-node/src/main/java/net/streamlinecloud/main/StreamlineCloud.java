package net.streamlinecloud.main;

import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.terminal.ReplacePaket;
import net.streamlinecloud.api.terminal.StreamlineLogger;
import net.streamlinecloud.api.util.StreamlineState;
import net.streamlinecloud.client.core.StreamlineApiClient;
import net.streamlinecloud.client.core.StreamlineHttpClient;
import net.streamlinecloud.client.manager.GroupManager;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.terminal.NodeLogger;
import net.streamlinecloud.main.terminal.command.CommandManager;
import net.streamlinecloud.main.utils.*;
import net.streamlinecloud.main.backend.BackEndMain;
import net.streamlinecloud.main.core.server.RunningServer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URL;
import java.util.*;

/**
 * A utility class that provides convenient api functions
 */
@Getter
public class StreamlineCloud {

    private static final List<Integer> generatedPorts = new ArrayList<>();
    private static final Random random = new Random();

    private static boolean clientInjected = false;

    @Getter
    private static GroupManager groupManager;

    @Getter
    private static StreamlineHttpClient httpClient;

    @Getter
    private static CommandManager commandManager = new CommandManager();

    @Getter
    private static StreamlineLogger logger = new NodeLogger();

    public static void injectClient(StreamlineApiClient client) {
        if (clientInjected) return;
        groupManager = client.getGroupManager();
        httpClient = client.getHttpClient();

        clientInjected = true;
    }

    public static int generateUniquePort() {

        int port;

        do {
            port = 1024 + random.nextInt(49151 - 1024 + 1);

        } while (generatedPorts.contains(port));

        generatedPorts.add(port);
        if (Cache.i().getConfig().getAdvanced().isEnableRconSupport()) generatedPorts.add(port + 1);

        return port;
    }

    /**
     * Checks if a port is available.
     * @param port the port to check in the range of 1-65535
     * @return true if the port is available, false otherwise.
     */
    public static boolean isPortAvailable(int port) {
        if (port < 1 ||port > 65535) {
            throw new IllegalArgumentException("Port must be in the range of 1-65535");
        }
        try (ServerSocket tcp = new ServerSocket(port); DatagramSocket udp = new DatagramSocket(port)) {
            tcp.setReuseAddress(true);
            udp.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }


    }

    public static void log(String message) {
        getLogger().info(message);
    }

    public static void log(String message, ReplacePaket[] paket) {
        getLogger().info(message, paket);
    }

    private static void redisplay() {

        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();

        if (lineReader.isReading()) {
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }
    }

    @SneakyThrows
    public static void shutDown() {
        log("sc.shutdown.shuttingDown");

        if (RunningServerManager.getInstance() != null) {

            List<RunningServer> servers = new ArrayList<>(RunningServerManager.getInstance().getRunningServers());
            Cache.i().setStreamlineState(StreamlineState.STOPPING);

            for (RunningServer server : servers) {
                if (server.getRuntime().equals(ServerRuntime.PROXY)) {
                    server.stop();
                } else {
                    if (server.getServerState().equals(ServerState.STARTING)) server.kill();
                    else server.stop();
                }
            }

            while (!RunningServerManager.getInstance().getRunningServers().isEmpty()) {
                Thread.sleep(100);
            }

        }

        Cache.i().getPluginManager().executeStop();

        BackEndMain.stop();

        if (Cache.i().isFirstLaunch()) {
            NodeLogger.logSingle("");
            NodeLogger.logSingle(readyBanner());
            NodeLogger.logSingle("");
            NodeLogger.logSingle("Please start StreamlineCloud again.");
            NodeLogger.logSingle("");
        } else {
            log("sc.thanksForUsing");
        }

        CloudMain.getInstance().getTerminal().close();

        System.exit(0);
    }

    public static String generateApiKey() {

        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        int length = 120;

        Random random = new Random();

        for (int i = 0; i < length; i++) {

            int randomIndex = random.nextInt(letters.length());
            char randomCharacter = letters.charAt(randomIndex);
            builder.append(randomCharacter);
        }

        return builder.toString();
    }

    public static boolean download(String url, String file, CloudGroup.DownloadResponse response) {
        if (file.startsWith("/")) Utils.runMkdir(new File(file).mkdirs());
        File template_dir = new File(System.getProperty("user.dir") + "/templates/" + file);
        Downloader downloader = new Downloader();
        try {
            downloader.download(new URL(url), new File((file.startsWith("/") ? file + "/server.jar" : template_dir.getAbsolutePath() + "/server.jar")), s1 -> {

                StreamlineCloud.log("Server für " + file +"  wurde erfolgreich heruntergeladen!");
                response.execute(true);
            });
        } catch (Exception e) {
            StreamlineCloud.log("sc.group.downloadFailed");
            response.execute(false);
            return false;
        }
        return true;
    }

    public static String readyBanner() {
        return """
                 /$$$$$$$  /$$$$$$$$  /$$$$$$  /$$$$$$$  /$$     /$$
                | $$__  $$| $$_____/ /$$__  $$| $$__  $$|  $$   /$$/
                | $$  \\ $$| $$      | $$  \\ $$| $$  \\ $$ \\  $$ /$$/\s
                | $$$$$$$/| $$$$$   | $$$$$$$$| $$  | $$  \\  $$$$/ \s
                | $$__  $$| $$__/   | $$__  $$| $$  | $$   \\  $$/  \s
                | $$  \\ $$| $$      | $$  | $$| $$  | $$    | $$   \s
                | $$  | $$| $$$$$$$$| $$  | $$| $$$$$$$/    | $$   \s
                |__/  |__/|________/|__/  |__/|_______/     |__/   \s""";
    }

    public static String streamlineBanner() {

        return """
                
                
                  /$$$$$$   /$$                                                 /$$       /$$                         \s
                 /$$__  $$ | $$                                                | $$      |__/                         \s
                | $$  \\__//$$$$$$    /$$$$$$   /$$$$$$   /$$$$$$  /$$$$$$/$$$$ | $$       /$$ /$$$$$$$   /$$$$$$      \s
                |  $$$$$$|_  $$_/   /$$__  $$ /$$__  $$ |____  $$| $$_  $$_  $$| $$      | $$| $$__  $$ /$$__  $$     \s
                 \\____  $$ | $$    | $$  \\__/| $$$$$$$$  /$$$$$$$| $$ \\ $$ \\ $$| $$      | $$| $$  \\ $$| $$$$$$$$     \s
                 /$$  \\ $$ | $$ /$$| $$      | $$_____/ /$$__  $$| $$ | $$ | $$| $$      | $$| $$  | $$| $$_____/     \s
                |  $$$$$$/ |  $$$$/| $$      |  $$$$$$$|  $$$$$$$| $$ | $$ | $$| $$$$$$$$| $$| $$  | $$|  $$$$$$$     \s
                 \\______/   \\___/  |__/       \\_______/ \\_______/|__/ |__/ |__/|________/|__/|__/  |__/ \\_______/     \s
                                                                                                                      \s
                  /$$$$$$  /$$                           /$$                                                          \s
                 /$$__  $$| $$                          | $$                                                          \s
                | $$  \\__/| $$  /$$$$$$  /$$   /$$  /$$$$$$$                                                          \s
                | $$      | $$ /$$__  $$| $$  | $$ /$$__  $$                                                          \s
                | $$      | $$| $$  \\ $$| $$  | $$| $$  | $$                                                          \s
                | $$    $$| $$| $$  | $$| $$  | $$| $$  | $$                                                          \s
                |  $$$$$$/| $$|  $$$$$$/|  $$$$$$/|  $$$$$$$                                                          \s
                 \\______/ |__/ \\______/  \\______/  \\_______/   \s""";
    }
}