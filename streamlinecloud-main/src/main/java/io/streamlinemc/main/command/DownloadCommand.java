package io.streamlinemc.main.command;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.terminal.api.CloudCommand;

public class DownloadCommand extends CloudCommand {

    public DownloadCommand() {
        setName("download");
        setDescription("Download files");
    }

    @Override
    public void execute(String[] args) {

        if (args.length == 3) {

            StreamlineCloud.download(args[2], args[1], success -> {
                //
            });

        } else {
            StreamlineCloud.log("syntax: download <template> <url>");
            StreamlineCloud.log("example: download default/server https://streamlinemc.cloud/my-software.jar");
        }
    }

}
