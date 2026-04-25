package net.streamlinecloud.main.setup;

import net.streamlinecloud.api.util.StreamlineState;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.terminal.NodeLogger;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Utils;

import java.io.File;
import java.util.*;

public class StreamlineSetup {

    List<SetupQuestion> questions;
    int current = 0;

    public StreamlineSetup(SetupQuestion[] questions) {
        Cache.i().setStreamlineState(StreamlineState.SETUP);

        NodeLogger.logSingle("");
        NodeLogger.logSingle("StreamlineCloud");
        NodeLogger.logSingle("""
                 $$$$$$\\  $$$$$$$$\\ $$$$$$$$\\ $$\\   $$\\ $$$$$$$\\       \s
                $$  __$$\\ $$  _____|\\__$$  __|$$ |  $$ |$$  __$$\\      \s
                $$ /  \\__|$$ |         $$ |   $$ |  $$ |$$ |  $$ |     \s
                \\$$$$$$\\  $$$$$\\       $$ |   $$ |  $$ |$$$$$$$  |     \s
                 \\____$$\\ $$  __|      $$ |   $$ |  $$ |$$  ____/      \s
                $$\\   $$ |$$ |         $$ |   $$ |  $$ |$$ |           \s
                \\$$$$$$  |$$$$$$$$\\    $$ |   \\$$$$$$  |$$ |           \s
                 \\______/ \\________|   \\__|    \\______/ \\__|          \s""");
        NodeLogger.logSingle("");

        Cache.i().setConfig(new MainConfig("", "lobby"));
        Utils.runMkdir(new File(Cache.i().homeFile + "/templates/default/server").mkdirs());

        this.questions = Arrays.asList(questions);
        next();
    }

    public void next() {
        if (current >= questions.size()) {
            finishSetup();
            return;
        }

        questions.get(current).start(result -> {
            current++;
            next();
        });
    }

    private void finishSetup() {
        MainConfig.saveConfig();
        StreamlineCloud.log("sc.setup.finished");
        StreamlineCloud.shutDown();
    }

}