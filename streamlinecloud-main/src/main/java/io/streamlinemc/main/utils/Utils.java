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

    public static String parseLowerCase(String s) {
        s.replace("A", "a");
        s.replace("B", "b");
        s.replace("C", "c");
        s.replace("D", "d");
        s.replace("E", "e");
        s.replace("F", "f");
        s.replace("G", "g");
        s.replace("H", "h");
        s.replace("I", "i");
        s.replace("J", "j");
        s.replace("K", "k");
        s.replace("L", "l");
        s.replace("M", "m");
        s.replace("N", "n");
        s.replace("O", "o");
        s.replace("P", "p");
        s.replace("Q", "q");
        s.replace("R", "r");
        s.replace("S", "s");
        s.replace("T", "t");
        s.replace("U", "u");
        s.replace("V", "v");
        s.replace("W", "w");
        s.replace("X", "x");
        s.replace("Y", "y");
        s.replace("Z", "z");
        return s;
    }

}
