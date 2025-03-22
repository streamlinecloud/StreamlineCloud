package net.streamlinecloud.main.core.backend.RestController.get;

import net.streamlinecloud.main.utils.Cache;

import java.util.Calendar;

import static net.streamlinecloud.main.core.backend.BackEndMain.mainPath;

public class UptimeRestController {

    public UptimeRestController() {
        Cache.i().getBackend().get(mainPath + "get/uptime", ctx -> {

            long dif_intime = Calendar.getInstance().getTimeInMillis() - Cache.i().getStartuptime();
            long dif_inmin = (dif_intime / (1000 * 60)) % 60;
            long dif_inhour = (dif_intime / (1000 * 60 * 60)) % 24;

            String hour = dif_inhour + "";
            String min = dif_inmin + "";

            if (dif_inhour <= 9)  hour = "0" + hour;
            if (dif_inmin <= 9)  min = "0" + min;

            ctx.result(hour + ":" + min);
            ctx.status(200);

        });
    }

}
