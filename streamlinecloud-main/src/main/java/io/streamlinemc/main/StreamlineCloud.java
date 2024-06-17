package io.streamlinemc.main;

import io.streamlinemc.api.StreamlineAPI;
import io.streamlinemc.main.core.group.CloudGroup;
import io.streamlinemc.main.lang.ReplacePaket;
import io.streamlinemc.main.utils.StaticCache;
import io.streamlinemc.main.core.backend.BackEndMain;
import io.streamlinemc.main.core.server.CloudServer;
import io.streamlinemc.main.terminal.Color;
import io.streamlinemc.main.utils.InternalSettings;
import io.streamlinemc.main.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.PrintAboveWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

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
        return port;
    }

    public static void log(String msg) { logIntern(msg, new ReplacePaket[]{}, true); }
    public static void log(String msg, ReplacePaket[] packets) { logIntern(msg, packets, true); }
    public static void logError(String msg) { logIntern(InternalSettings.name + "§DARK_RED" + msg, new ReplacePaket[]{}, true); }


    public static void logImportant(String msg) {

        logIntern(InternalSettings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{}, true);
        logIntern(InternalSettings.name + msg, new ReplacePaket[]{}, true);
        logIntern(InternalSettings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{}, true);
    }

    private static void logIntern(String msg, ReplacePaket[] pakets, boolean prefix) {

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM-HH:mm:ss");
        String formattedDate= format.format(now);
        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();

        if (StaticCache.getCurrentLanguage() != null) {

            String replace = StaticCache.getCurrentLanguage().getMessages().get(msg);

            if (replace != null) {

                msg = replace;

            } else {

                String replaceEn = StaticCache.getLanguages().get(1).getMessages().get(msg);
                if (replaceEn != null) msg = replaceEn;
            }
        } else {

            msg = msg + " (preinit)";
        }

        String s = msg + "§RED";

        if (prefix) {
            s = "§RED" + formattedDate + " §8| " + InternalSettings.name + msg + "§RED";
        }

        for (ReplacePaket p : pakets) {
            s = s.replace(p.getTarget(), p.getValue());
        }


        if (StaticCache.getWebSocketClient() != null && StaticCache.getWebSocketClient().getClient().isOpen()) {
            StaticCache.getWebSocketClient().getClient().send("MESSAGE streamline/output " + Color.translate(s));
        }

        PrintWriter writer = new PrintWriter(new PrintAboveWriter(lineReader));
        writer.println(Color.translate(s));
        writer.flush();


    }

    @SneakyThrows
    public static void printError(String error, String[] fixes, Exception e) {

        if (printedErrors.contains(error)) {
            StreamlineCloud.log("sl.error.again", new ReplacePaket[]{new ReplacePaket("%0", error)});
            return;
        }

        StreamlineCloud.log("sl.error.title", new ReplacePaket[]{new ReplacePaket("%0", error)});
        StreamlineCloud.log("sl.error.fixes");

        if (fixes == null) {

            StreamlineCloud.log("sl.error.noFixes");

        } else {

            for (String fix : fixes) {

                StreamlineCloud.log("sl.error.fix." + fix);

            }
        }

        if (e != null) {

            //Create error log

            long dif_intime = Calendar.getInstance().getTimeInMillis() - StaticCache.getStartuptime();
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
            bufferedWriter.write("Streamline-Version: " + InternalSettings.version + " (API: " + StreamlineAPI.getApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineMC-Version: " + StaticCache.getPluginVersion() + " (API: " + StaticCache.getPluginApiVersion() + ")");
            bufferedWriter.newLine();
            bufferedWriter.write("Uptime: " + dif_inday + "d " + dif_inhour + "h " + dif_inmin + "m " + dif_insec + "s");

            bufferedWriter.newLine();
            bufferedWriter.newLine();
            bufferedWriter.write("StreamlineCloud by " + InternalSettings.authors);
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

        for (CloudServer ser : StaticCache.getRunningServers()) {

            if (ser.getName().equals(name)) {

                return ser;
            }
        }
        return null;
    }

    public static CloudServer getServerByUuid(String uuid) {

        for (CloudServer ser : StaticCache.getRunningServers()) {

            if (ser.getUuid().equals(uuid)) {

                return ser;
            }
        }
        return null;
    }

    public static CloudGroup getGroupByName(String name) {

        for (CloudGroup group : StaticCache.getActiveGroups()) {

            if (group.getName().equals(name)) {

                return group;
            }
        }
        return null;
    }

    public static void checkGroups() {

        for (CloudGroup g : StaticCache.getActiveGroups()) {

            List<CloudServer> servers = getGroupOnlineServers(g);
            startServersIfNeeded(g, servers);
        }
    }

    private static void startServersIfNeeded(CloudGroup g, List<CloudServer> servers) {

        List<CloudServer> alLServers = new ArrayList<>(getGroupOnlineServers(g));
        for (CloudServer s : StaticCache.getServersWaitingForStart()) if (s.getGroupDirect().equals(g)) alLServers.add(s);

        if (alLServers.size() < g.getMinOnlineCount()) {

            startServerByGroup(g);
        }
    }

    public static void startServerByGroup(CloudGroup cloudGroup) {
        List<CloudServer> alLServers = new ArrayList<>(getGroupOnlineServers(cloudGroup));
        for (CloudServer s : StaticCache.getServersWaitingForStart()) if (s.getGroupDirect().equals(cloudGroup)) alLServers.add(s);

        CloudServer server = new CloudServer(cloudGroup.getName() + "-" + (alLServers.size() + 1), cloudGroup.getRuntime());
        server.setGroup(cloudGroup.getName());
        StaticCache.getServersWaitingForStart().add(server);
    }

    public static List<CloudServer> getGroupOnlineServers(CloudGroup g) {
        List<CloudServer> onlineServers = new ArrayList<>();
        for (CloudServer s : StaticCache.getLinkedServers().keySet()) {
            if (s.getGroupDirect().equals(g)) {
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

        StaticCache.getPluginManager().executeStop();

        if (StaticCache.getWebSocketClient() != null) StaticCache.getWebSocketClient().getClient().close();

        BackEndMain.stop();

        List<CloudServer> servers = new ArrayList<>(StaticCache.getRunningServers());

        for (CloudServer server : servers) {
            if (server.getThread() != null) {
                StreamlineCloud.log("sl.server.killing", new ReplacePaket[]{new ReplacePaket("%1", server.getName())});
                server.kill();
            }
        }

        servers = null;

        log("sl.thanksForUsing");

        if (StaticCache.isFirstLaunch()) {
            logSingle("");
            logSingle(readyBanner());
            logSingle("");
            logIntern("sl.ready", new ReplacePaket[]{}, false);
            logSingle("");

            if (StaticCache.getDataCache().contains("streamlinecloud-mc-copy-failed")) {
                StreamlineCloud.log("sl.streamline-mc-copy.failed");
            }
        }

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