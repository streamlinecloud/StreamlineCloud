package net.streamlinecloud.api.terminal;

/**
 * A blueprint of a basic logger class
 */
public interface StreamlineLogger {

    String getName();

    void info(String message);
    void info(String message, ReplacePaket[] pakets);

    void warning(String message);
    void warning(String message, ReplacePaket[] pakets);

    void error(String message);
    void error(String message, ReplacePaket[] pakets);

    /**
     * Should only be printed if debug mode is activated by the user
     */
    void debug(String message);

}
