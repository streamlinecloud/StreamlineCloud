package net.streamlinecloud.main.terminal;

import lombok.SneakyThrows;
import net.streamlinecloud.api.StreamlineAPI;
import net.streamlinecloud.api.extension.event.console.ConsoleMessageEvent;
import net.streamlinecloud.api.terminal.ReplacePaket;
import net.streamlinecloud.api.terminal.StreamlineLogger;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.lang.LangManager;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.MainBuildConfig;
import net.streamlinecloud.main.utils.Settings;
import net.streamlinecloud.main.utils.Utils;
import org.jline.reader.LineReader;
import org.jline.reader.PrintAboveWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static net.streamlinecloud.main.extension.ExtensionManager.eventManager;

public class NodeLogger implements StreamlineLogger {

    private static final List<String> printedErrors = new ArrayList<>();

    @Override
    public String getName() {
        return "Node";
    }

    @Override
    public void info(String message) {
        logIntern(message, new ReplacePaket[]{});
    }

    @Override
    public void info(String message, ReplacePaket[] pakets) {
        logIntern(message, pakets);
    }

    @Override
    public void warning(String message) {
        logIntern("§BOLD" + message, "§YELLOWWARNING", new ReplacePaket[]{});
    }

    @Override
    public void warning(String message, ReplacePaket[] pakets) {
        logIntern("§BOLD" + message, "§YELLOWWARNING", pakets);
    }

    @Override
    public void error(String message) {
        logIntern("§BOLD" + message, "§DARK_REDERROR", new ReplacePaket[]{});
    }

    @Override
    public void error(String message, ReplacePaket[] pakets) {
        logIntern("§BLOD" + message, "DARK_REDERROR", pakets);
    }

    @Override
    public void debug(String message) {
        if (Cache.i().isDebugMode()) {
            logIntern("DEBUG -> " + message, new ReplacePaket[]{});
        }
    }

    public static void logImportant(String msg) {

        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
        logIntern(Settings.name + msg, new ReplacePaket[]{});
        logIntern(Settings.name + "§DARK_RED || IMPORTANT ||", new ReplacePaket[]{});
    }

    private static void logIntern(String msg, ReplacePaket[] pakets) {
        logIntern(msg, "§AQUAINFO", pakets);

    }
    private static void logIntern(String msg, String prefix, ReplacePaket[] pakets) {

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

        s = "§8| §RED" + formattedDate + " " + prefix + " §8-> §RED" + msg + "§RED";

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
        CloudMain.getInstance().getTerminal().log("§WHITE" + msg);

    }

    public static void serverLog(String server, String msg) {

        LineReader lineReader = CloudMain.getInstance().getTerminal().getLineReader();
        PrintWriter writer = new PrintWriter(new PrintAboveWriter(lineReader));

        writer.println(Color.translate(server + " -> " + msg));
        writer.flush();
    }

}
