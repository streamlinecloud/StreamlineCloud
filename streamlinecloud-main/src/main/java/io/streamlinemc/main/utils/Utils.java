package io.streamlinemc.main.utils;

public class Utils {

    public static void downloadFile(String fileUrl, String localFilePath) {

    }

    public static boolean isProcessRunning(Process process) {
        try {
            // Versuchen Sie, den Exit-Wert abzurufen. Wenn der Prozess noch läuft, wirft dies eine Exception.
            process.exitValue();
            return false; // Der Prozess wurde beendet
        } catch (IllegalThreadStateException e) {
            return true; // Der Prozess läuft noch
        }
    }

}
