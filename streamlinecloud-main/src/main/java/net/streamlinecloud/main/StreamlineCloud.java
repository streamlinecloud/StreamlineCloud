package net.streamlinecloud.main;

import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.api.plugin.event.predefined.ConsoleMessageEvent;
import net.streamlinecloud.main.core.group.CloudGroup;
import net.streamlinecloud.main.lang.ReplacePaket;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.core.backend.BackEndMain;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.Color;
import net.streamlinecloud.main.utils.BuildSettings;
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

import static net.streamlinecloud.main.plugin.PluginManager.eventManager;

@Getter
public class StreamlineCloud {

    private static List<String> savedLogs = new ArrayList<>();
    private static List<Integer> generatedPorts = new ArrayList<>();
    private static Random random = new Random();

    private static List<String> printedErrors = new ArrayList<>();

    public static int generateUniquePort() {

        int port;

        do {
            port = 1024 + random.nextInt(49151 - 1024 + 1);

        } while (generatedPorts.contains(port));

        generatedPorts.add(port);
        if (Cache.i().getConfig().isEnableRconSupport()) generatedPorts.add(port + 1);

        return port;
    }

    public static void log(String msg) { logIntern(msg, new ReplacePaket[]{}, true); }
    public static void log(String msg, ReplacePaket[] packets) { logIntern(msg, packets, true); }
    public static void logError(String msg) { logIntern(BuildSettings.name + "§DARK_RED" + msg, new ReplacePaket[]{}, true); }


    public static void logImportant(String msg) {

        logIntern(BuildSettings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{}, true);
        logIntern(BuildSettings.name + msg, new ReplacePaket[]{}, true);
        logIntern(BuildSettings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{}, true);
    }

    private static void logIntern(String msg, ReplacePaket[] pakets, boolean prefix) {

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

        if (prefix) {
            s = "§RED" + formattedDate + " §8| " + BuildSettings.name + msg + "§RED";
        }

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

            //Create error log

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
            bufferedWriter.write("Streamline-Version: " + BuildSettings.version + " (API: " + StreamlineAPI.getApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineMC-Version: " + Cache.i().getPluginVersion() + " (API: " + Cache.i().getPluginApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("Uptime: " + dif_inday + "d " + dif_inhour + "h " + dif_inmin + "m " + dif_insec + "s");

            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineCloud by " + BuildSettings.authors);
            bufferedWriter.newLine();
            bufferedWriter.write("Need help? https://streamline.jdev.shop");

            bufferedWriter.close();
            fileWriter.close();

            StreamlineCloud.log("sl.error.details", new ReplacePaket[]{new ReplacePaket("%0", fileName + ".txt")});
        }

        printedErrors.add(error);
    }

    public static void releaseSavedLogs() {

        for (String log : savedLogs) logIntern(log, new ReplacePaket[]{}, true);
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

    public static CloudServer getServerByName(String name) {

        for (CloudServer ser : Cache.i().getRunningServers()) {

            if (ser.getName().equals(name)) {

                return ser;
            }
        }
        return null;
    }

    public static CloudServer getServerByUuid(String uuid) {

        for (CloudServer ser : Cache.i().getRunningServers()) {

            if (ser.getUuid().equals(uuid)) {

                return ser;
            }
        }
        return null;
    }

    public static CloudGroup getGroupByName(String name) {

        for (CloudGroup group : Cache.i().getActiveGroups()) {

            if (group.getName().equals(name)) {

                return group;
            }
        }
        return null;
    }

    public static void checkGroups() {

        for (CloudGroup g : Cache.i().getActiveGroups()) {

            List<CloudServer> servers = getGroupOnlineServers(g);
            startServersIfNeeded(g, servers);
        }
    }

    private static void startServersIfNeeded(CloudGroup g, List<CloudServer> servers) {

        List<CloudServer> alLServers = new ArrayList<>(getGroupOnlineServers(g));
        for (CloudServer s : Cache.i().getServersWaitingForStart()) if (s.getGroupDirect().equals(g)) alLServers.add(s);

        if (alLServers.size() < g.getMinOnlineCount()) {

            startServerByGroup(g);
        }
    }

    public static String startServerByGroup(CloudGroup cloudGroup) {
        return startServerByGroup(cloudGroup, new ArrayList<>());
    }
    public static String startServerByGroup(CloudGroup cloudGroup, List<String> templates) {
        List<CloudServer> allServers = new ArrayList<>(getGroupOnlineServers(cloudGroup));
        for (CloudServer s : Cache.i().getServersWaitingForStart()) if (s.getGroupDirect().equals(cloudGroup)) allServers.add(s);

        CloudServer server = new CloudServer(cloudGroup.getName() + "-" + calculateServerNumber(cloudGroup), cloudGroup.getRuntime());
        server.setGroup(cloudGroup.getName());
        server.setCustomTemplates(templates);
        Cache.i().getServersWaitingForStart().add(server);
        return server.getUuid();
    }

    public static int calculateServerNumber(CloudGroup g) {

        ArrayList<Integer> usedNumbers = new ArrayList<>();
        for (CloudServer server : getGroupOnlineServers(g)) {
            usedNumbers.add(Integer.valueOf(server.getName().split("-")[1]));
        }

        for (int i = 1; true ; i++) {
            if (!usedNumbers.contains(i)) return i;
        }

    }

    public static List<CloudServer> getGroupOnlineServers(CloudGroup g) {
        List<CloudServer> onlineServers = new ArrayList<>();
        for (CloudServer s : Cache.i().getRunningServers()) {
            if (s.getGroup().equals(g.getName())) {
                onlineServers.add(s);
            }
        }
        return onlineServers;
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
                server.kill();
            }
        }

        log("sl.thanksForUsing");

        CloudMain.getInstance().getTerminal().close();

        System.exit(0);
    }

    public static String generateApiKey() {

        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.:/*+-~^";
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

    public static void download(String url, String file, CloudGroup.DownloadResponse response) {
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
        }
    }

    public static String generatePassword() {

        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?*+-";
        StringBuilder builder = new StringBuilder();
        int length = 64;

        Random random = new Random();

        for (int i = 0; i < length; i++) {

            int zufallsIndex = random.nextInt(letters.length());
            char zufallsZeichen = letters.charAt(zufallsIndex);
            builder.append(zufallsZeichen);
        }

        return builder.toString();
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

        /*StreamlineCloud.log("  ____ _____ ____  _____    _    __  __ _     ___ _   _ _____ ");
        StreamlineCloud.log("/ ___|_   _|  _ \\| ____|  / \\  |  \\/  | |   |_ _| \\ | | ____|");
        StreamlineCloud.log("\\___ \\ | | | |_) |  _|   / _ \\ | |\\/| | |    | ||  \\| |  _|  ");
        StreamlineCloud.log(" ___) || | |  _ <| |___ / ___ \\| |  | | |___ | || |\\  | |___ ");
        StreamlineCloud.log("|____/ |_| |_| \\_\\_____/_/   \\_\\_|  |_|_____|___|_| \\_|_____|");
        StreamlineCloud.log("");
        StreamlineCloud.log(" / ___| |   / _ \\| | | |  _ \\ ");
        StreamlineCloud.log("| |   | |  | | | | | | | | | |");
        StreamlineCloud.log("| |___| |__| |_| | |_| | |_| | ");
        StreamlineCloud.log("| |___| |__| |_| | |_| | |_| | ");
        StreamlineCloud.log(" \\____|_____\\___/ \\___/|____/ ");*/

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