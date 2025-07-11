package net.streamlinecloud.main.command;

import lombok.SneakyThrows;
import net.streamlinecloud.api.server.TemplateEnums;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;
import net.streamlinecloud.main.utils.Downloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TemplatesCommand extends CloudCommand {

    public TemplatesCommand() {
        setName("templates");
        setAliases(new String[]{"t", "vorlagen"});
        setDescription("Manage templates");
    }

    @SneakyThrows
    @Override
    public void execute(String[] args) {
        //


        switch (args[1]) {
            case "create" -> {
                if (args.length < 4) {
                    StreamlineCloud.log("Syntax Error, please use template help");
                    return;
                }

                String name = args[2];
                String software = args[3];
                String url = args[4];

                File template_dir = new File(System.getProperty("user.dir") + "/templates/" + name);

                if (template_dir.exists()) {
                    StreamlineCloud.log("Error: Templates with name " + name + " already exists!");
                    return;
                }

                if (!TemplateEnums.SOFTWARE.SERVER.equals(TemplateEnums.SOFTWARE.valueOf(software)) && !TemplateEnums.SOFTWARE.PROXY.equals(TemplateEnums.SOFTWARE.valueOf(software))) {
                    StreamlineCloud.log("Error: Unkown Software, enter SERVER | PROXY");
                    return;
                }

                template_dir.mkdirs();

                Downloader downoader = new Downloader();

                if (software.equals("SERVER")) {
                    downoader.download(new URL(url), new File(template_dir.getAbsolutePath() + "/server.jar"));
                } else if (software.equals("PROXY")) {
                    downoader.download(new URL(url), new File(template_dir.getAbsolutePath() + "/proxy.jar"));
                }

                ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                scheduledExecutorService.schedule(() -> {
                    StreamlineCloud.log("Download Complete!");
                    StreamlineCloud.log("§GREEN Template with name " + name + " created!§RED");
                    scheduledExecutorService.shutdownNow();
                }, 3, TimeUnit.SECONDS);
            }
            case "help" -> StreamlineCloud.log("- template create <templatename> <SERVER | PROXY> <SOFTWARE-URL>");
            case "delete" -> {
                if (!Files.exists(Paths.get(Cache.i().homeFile + "/template/" + args[1]))) {
                    File template = new File(Cache.i().homeFile + "/templates/" + args[1]);
                    Files.delete(template.toPath());
                    StreamlineCloud.log("tmp.delete.success");
                    return;
                }
                StreamlineCloud.log("tmp.delete.notfound");
            }
            default -> sendHelp();
        }
    }

    public void sendHelp() {
        StreamlineCloud.log("Unknown Subcommand or Invalid Arguments!");
        StreamlineCloud.log("-> templates help");
    }
}
