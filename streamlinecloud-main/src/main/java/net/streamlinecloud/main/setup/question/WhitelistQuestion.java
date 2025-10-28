package net.streamlinecloud.main.setup.question;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.utils.Cache;

public class WhitelistQuestion extends SetupQuestion {

    public WhitelistQuestion() {
        super(InputType.STRING, "sc.setup.enableWhitelist", output -> {
            if (output.equals("yes")) {
                Cache.i().getConfig().getWhitelist().setWhitelistEnabled(true);
                StreamlineCloud.log("sc.setup.whitelistEnabled");
            }
            MainConfig.saveConfig();
            StreamlineCloud.log("sc.setup.configGenerated");
            return true;
        });
    }

}
