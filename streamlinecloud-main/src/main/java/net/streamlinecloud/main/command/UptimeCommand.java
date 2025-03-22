package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.core.server.CloudServer;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

import java.net.MalformedURLException;
import java.util.Calendar;

public class UptimeCommand extends CloudCommand {

    public UptimeCommand() {
        setName("uptime");
        setDescription("Shows the Uptime from this Cloud or Servers");
    }

    @Override
    public void execute(String[] args) throws MalformedURLException {

        if (args.length == 1){
            long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartuptime();
            long dif_insec = dif_intime / 1000 % 60;
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;
            long dif_inday = (dif_intime / (1000 * 60 * 60 * 24)) % 365;
            StreamlineCloud.log("Uptime: " + dif_inday + "d " + dif_inhour + "h " + dif_inmin + "m " + dif_insec + "s");
            return;
        }


            if (StreamlineCloud.getServerByName(args[1]) == null) {
                StreamlineCloud.log("Server not found");
                return;
            }

            CloudServer server = StreamlineCloud.getServerByName(args[1]);

            long dif_intime = Calendar.getInstance().getTimeInMillis() - server.getStartupTime();
            long dif_insec = dif_intime / 1000 % 60;
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;
            long dif_inday = (dif_intime / (1000 * 60 * 60 * 24)) % 365;
            StreamlineCloud.log("Uptime from Server " + args[1] + ": " + dif_inday + "d " + dif_inhour + "h " + dif_inmin + "m " + dif_insec + "s");
    }
}
