package net.streamlinecloud.main;

import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.api.extension.event.console.ConsoleMessageEvent;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.core.backend.BackEndMain;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.Color;
import net.streamlinecloud.main.utils.MainBuildConfig;
import net.streamlinecloud.main.utils.Settings;
import net.streamlinecloud.main.utils.Downloader;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.PrintAboveWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

@Getter
public class StreamlineCloud {

    private static List<String> savedLogs = new ArrayList<>();
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

    public static void log(String msg) { logIntern(msg, new ReplacePaket[]{}); }
    public static void log(String msg, ReplacePaket[] packets) { logIntern(msg, packets); }
    public static void logError(String msg) { logIntern(Settings.name + "§DARK_RED" + msg, new ReplacePaket[]{}); }


    public static void logImportant(String msg) {

        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
        logIntern(Settings.name + msg, new ReplacePaket[]{});
        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
    }

    private static void logIntern(String msg, ReplacePaket[] pakets) {

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM-HH:mm:ss");
        String formattedDate= format.format(now);
        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();

        if (Cache.i().getCurrentLanguage() != null) {

            String replace = Cache.i().getCurrentLanguage().getMessages().get(msg);

            if (replace != null) {

                msg = replace;

            } else {

                String replaceEn = Cache.i().getLanguages().get(1).getMessages().get(msg);
                if (replaceEn != null) msg = replaceEn;
            }
        } else {

            msg = msg + " (preinit)";
        }

        String s = msg + "§RED";

        s = "§RED" + formattedDate + " §8| " + Settings.name + msg + "§RED";

        for (ReplacePaket p : pakets) {
            s = s.replace(p.getTarget(), p.getValue());
        }

        ConsoleMessageEvent consoleMessageEvent = eventManager.callEvent(new ConsoleMessageEvent(Color.remove(s)));

        if (consoleMessageEvent.isCancelled()) return;


        if (Cache.i().getWebSocketClient() != null && Cache.i().getWebSocketClient().getClient().isOpen()) {
            Cache.i().getWebSocketClient().getClient().send("MESSAGE streamline/output " + Color.translate(s));
        }


        PrintWriter writer = new PrintWriter(new PrintAboveWriter(lineReader));
        writer.println(Color.translate(s));
        writer.flush();


    }

    @SneakyThrows
    public static void printError(String error, Exception e) {

        if (printedErrors.contains(error)) {
            StreamlineCloud.log("sl.error.again", new ReplacePaket[]{new ReplacePaket("%0", error)});
            return;
        }

        StreamlineCloud.log("sl.error.title", new ReplacePaket[]{new ReplacePaket("%0", error)});

        if (e != null) {

            long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartuptime();
            long dif_insec = dif_intime / 1000 % 60;
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;
            long dif_inday = (dif_intime / (1000 * 60 * 60 * 24)) % 365;

            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM-HH:mm:ss");
            String formattedDate= format.format(now);
            String fileName = error + "-" + formattedDate;
            File file = new File(System.getProperty("user.dir") + "/data/error/" + fileName + ".txt");
            new File(System.getProperty("user.dir") + "/data/error").mkdirs();

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
            bufferedWriter.write("Need help? https://streamline.jdev.shop");

            bufferedWriter.close();
            fileWriter.close();

            StreamlineCloud.log("sl.error.details", new ReplacePaket[]{new ReplacePaket("%0", fileName + ".txt")});
        }

        printedErrors.add(error);
    }

    public static void releaseSavedLogs() {

        for (String log : savedLogs) logIntern(log, new ReplacePaket[]{});
        savedLogs = new ArrayList<>();
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
        log("sl.shutdown.shuttingDown");

        Cache.i().getPluginManager().executeStop();

        if (Cache.i().getWebSocketClient() != null) Cache.i().getWebSocketClient().getClient().close();

        BackEndMain.stop();

        List<CloudServer> servers = new ArrayList<>(Cache.i().getRunningServers());

        for (CloudServer server : servers) {
            if (server.getThread() != null) {
                if (server.isStaticServer()) server.stop();
                else server.kill();
            }
        }

        if (Cache.i().isFirstLaunch()) {
            logSingle("");
            logSingle(readyBanner());
            logSingle("");
            logSingle("Please start StreamlineCloud again.");
            logSingle("");
        } else {
            log("sl.thanksForUsing");
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

            int zufallsIndex = random.nextInt(letters.length());
            char zufallsZeichen = letters.charAt(zufallsIndex);
            builder.append(zufallsZeichen);
        }

        return builder.toString();
    }

    public static boolean download(String url, String file, CloudGroup.DownloadResponse response) {
        File template_dir = new File(System.getProperty("user.dir") + "/templates/" + file);
        Downloader downloader = new Downloader();
        try {
            downloader.download(new URL(url), new File(template_dir.getAbsolutePath() + "/server.jar"), s1 -> {

                StreamlineCloud.log("Server für " + file +"  wurde erfolgreich heruntergeladen!");
                response.execute(true);
            });
        } catch (Exception e) {
            StreamlineCloud.log("sl.group.downloadFailed");
            response.execute(false);
            return false;
        }
        return true;
    }

    public static String readyBanner() {
        return " /$$$$$$$  /$$$$$$$$  /$$$$$$  /$$$$$$$  /$$     /$$\n" +
                "| $$__  $$| $$_____/ /$$__  $$| $$__  $$|  $$   /$$/\n" +
                "| $$  \\ $$| $$      | $$  \\ $$| $$  \\ $$ \\  $$ /$$/ \n" +
                "| $$$$$$$/| $$$$$   | $$$$$$$$| $$  | $$  \\  $$$$/  \n" +
                "| $$__  $$| $$__/   | $$__  $$| $$  | $$   \\  $$/   \n" +
                "| $$  \\ $$| $$      | $$  | $$| $$  | $$    | $$    \n" +
                "| $$  | $$| $$$$$$$$| $$  | $$| $$$$$$$/    | $$    \n" +
                "|__/  |__/|________/|__/  |__/|_______/     |__/    ";
    }

    public static String streamlineBanner() {

        return "\n\n" +
                "  /$$$$$$   /$$                                                 /$$       /$$                          \n" +
                " /$$__  $$ | $$                                                | $$      |__/                          \n" +
                "| $$  \\__//$$$$$$    /$$$$$$   /$$$$$$   /$$$$$$  /$$$$$$/$$$$ | $$       /$$ /$$$$$$$   /$$$$$$       \n" +
                "|  $$$$$$|_  $$_/   /$$__  $$ /$$__  $$ |____  $$| $$_  $$_  $$| $$      | $$| $$__  $$ /$$__  $$      \n" +
                " \\____  $$ | $$    | $$  \\__/| $$$$$$$$  /$$$$$$$| $$ \\ $$ \\ $$| $$      | $$| $$  \\ $$| $$$$$$$$      \n" +
                " /$$  \\ $$ | $$ /$$| $$      | $$_____/ /$$__  $$| $$ | $$ | $$| $$      | $$| $$  | $$| $$_____/      \n" +
                "|  $$$$$$/ |  $$$$/| $$      |  $$$$$$$|  $$$$$$$| $$ | $$ | $$| $$$$$$$$| $$| $$  | $$|  $$$$$$$      \n" +
                " \\______/   \\___/  |__/       \\_______/ \\_______/|__/ |__/ |__/|________/|__/|__/  |__/ \\_______/      \n" +
                "                                                                                                       \n" +
                "  /$$$$$$  /$$                           /$$                                                           \n" +
                " /$$__  $$| $$                          | $$                                                           \n" +
                "| $$  \\__/| $$  /$$$$$$  /$$   /$$  /$$$$$$$                                                           \n" +
                "| $$      | $$ /$$__  $$| $$  | $$ /$$__  $$                                                           \n" +
                "| $$      | $$| $$  \\ $$| $$  | $$| $$  | $$                                                           \n" +
                "| $$    $$| $$| $$  | $$| $$  | $$| $$  | $$                                                           \n" +
                "|  $$$$$$/| $$|  $$$$$$/|  $$$$$$/|  $$$$$$$                                                           \n" +
                " \\______/ |__/ \\______/  \\______/  \\_______/    ";
    }
}