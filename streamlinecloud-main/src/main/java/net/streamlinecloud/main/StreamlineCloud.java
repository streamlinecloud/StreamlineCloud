package net.streamlinecloud.main;

import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.api.extension.event.console.ConsoleMessageEvent;
import net.streamlinecloud.api.server.ServerRuntime;
import net.streamlinecloud.api.server.ServerState;
import net.streamlinecloud.api.util.StreamlineState;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.core.server.RunningServerManager;
import net.streamlinecloud.main.lang.LangManager;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.*;
import net.streamlinecloud.main.backend.BackEndMain;
import net.streamlinecloud.main.core.server.RunningServer;
import net.streamlinecloud.main.terminal.Color;
import lombok.Getter;
import lombok.SneakyThrows;
import net.streamlinecloud.main.utils.MainBuildConfig;
import org.jline.reader.LineReader;
import org.jline.reader.PrintAboveWriter;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

@Getter
public class StreamlineCloud {

    private static final List<Integer> generatedPorts = new ArrayList<>();
    private static final Random random = new Random();

    private static final List<String> printedErrors = new ArrayList<>();

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

    public static void log(String msg) { logIntern(msg, new ReplacePaket[]{}); }
    public static void log(String msg, ReplacePaket[] packets) { logIntern(msg, packets); }
    public static void logError(String msg) { logIntern("§DARK_RED" + msg, new ReplacePaket[]{}); }


    public static void logImportant(String msg) {

        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
        logIntern(Settings.name + msg, new ReplacePaket[]{});
        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
    }

    public static void logDebug(String msg) {
        if (Cache.i().isDebugMode()) {
            log(msg);
        }
    }

    private static void logIntern(String msg, ReplacePaket[] pakets) {

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM-HH:mm:ss");
        String formattedDate= format.format(now);
        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();

        if (LangManager.getInstance().getCurrentLanguage() != null) {

            String replace = LangManager.getInstance().getCurrentLanguage().getMessages().get(msg);

            if (replace != null) {

                msg = replace;

            } else {

                String replaceEn = LangManager.getInstance().getLanguages().get(1).getMessages().get(msg);
                if (replaceEn != null) msg = replaceEn;
            }
        } else {

            msg = msg + " (preinit)";
        }

        String s;

        s = "§8| §RED" + formattedDate + " §8-> §RED" + msg + "§RED";

        for (ReplacePaket p : pakets) {
            s = s.replace(p.getTarget(), p.getValue());
        }

        ConsoleMessageEvent consoleMessageEvent = eventManager.callEvent(new ConsoleMessageEvent(Color.remove(s)));

        if (consoleMessageEvent.isCancelled()) return;


        if (Cache.i().getWebSocketClient() != null && Cache.i().getWebSocketClient().getClient().isOpen()) {
            Cache.i().getWebSocketClient().getClient().send("MESSAGE streamline/output " + Color.translate(s));
        }

        CloudMain.getInstance().getTerminal().log(s);


    }

    @SneakyThrows
    public static void printError(String error, Exception e) {

        if (printedErrors.contains(error)) {
            StreamlineCloud.log("sc.error.again", new ReplacePaket[]{new ReplacePaket("%0", error)});
            return;
        }

        StreamlineCloud.log("sc.error.title", new ReplacePaket[]{new ReplacePaket("%0", error)});

        if (e != null) {

            long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartUptime();
            long dif_insec = dif_intime / 1000 % 60;
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;
            long dif_inday = (dif_intime / (1000 * 60 * 60 * 24)) % 365;

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM-HH:mm:ss");
            String formattedDate= format.format(now);
            String fileName = error + "-" + formattedDate;
            File file = new File(System.getProperty("user.dir") + "/data/error/" + fileName + ".txt");
            Utils.runMkdir(new File(System.getProperty("user.dir") + "/data/error").mkdirs());

            FileWriter fileWriter = new FileWriter(file + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("StreamlineCloudErrorLog: " + fileName);
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write(e.getMessage());
            bufferedWriter.newLine();
            for (StackTraceElement stack : e.getStackTrace()) {
                bufferedWriter.write(stack.getClassName() + " -> " + stack.getMethodName() + " -> line:" + stack.getLineNumber());
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
            bufferedWriter.write("Streamline-Version: " + MainBuildConfig.VERSION + " (API: " + StreamlineAPI.getApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineMC-Version: " + Cache.i().getPluginVersion() + " (API: " + Cache.i().getPluginApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("Uptime: " + dif_inday + "d " + dif_inhour + "h " + dif_inmin + "m " + dif_insec + "s");

            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineCloud by " + Settings.authors);
            bufferedWriter.newLine();
            bufferedWriter.write("Need help? https://streamlinecloud.net/");

            bufferedWriter.close();
            fileWriter.close();

            StreamlineCloud.log("sc.error.details", new ReplacePaket[]{new ReplacePaket("%0", fileName + ".txt")});
        }

        printedErrors.add(error);
    }

    public static void logSingle(String msg) {

        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();
        PrintWriter writer = new PrintWriter(new PrintAboveWriter(lineReader));

        writer.println(Color.translate(msg));
        writer.flush();
    }

    public static void serverLog(String server, String msg) {

        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();
        PrintWriter writer = new PrintWriter(new PrintAboveWriter(lineReader));

        writer.println(Color.translate(server + " -> " + msg));
        writer.flush();
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

        if (Cache.i().getWebSocketClient() != null) Cache.i().getWebSocketClient().getClient().close();

        BackEndMain.stop();

        if (Cache.i().isFirstLaunch()) {
            logSingle("");
            logSingle(readyBanner());
            logSingle("");
            logSingle("Please start StreamlineCloud again.");
            logSingle("");
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