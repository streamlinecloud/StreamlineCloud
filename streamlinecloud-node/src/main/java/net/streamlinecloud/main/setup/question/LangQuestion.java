package net.streamlinecloud.main.setup.question;

import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.backend.LoadBalancer;
import net.streamlinecloud.main.setup.SetupQuestion;
import net.streamlinecloud.main.utils.Cache;

import java.util.List;

public class LangQuestion extends SetupQuestion {

    public LangQuestion() {
        setInputType(InputType.STRING);
        setQuestion("Set up language / Gebe eine Sprache ein [en/de]");
        setValidator(output -> {
            if (output.equalsIgnoreCase("en") || output.equalsIgnoreCase("de")) {

                if (output.equalsIgnoreCase("de")) {
                    StreamlineCloud.log("§DARK_REDACHTUNG: Bitte beachte, dass die deutsche Version von StreamlineCloud noch nicht vollständig ist. An meheren Stellen können englische Texte auftauchen. Wir arbeiten weiterhin an einer vollständigen deutschen Übersetzung. Danke für deine Geduld!");
                }

                Cache.i().getConfig().setLanguage(output + ".json");
                CloudMain.getInstance().initLang();

                String javaPath = System.getProperty("java.home") + "/bin/java";
                Cache.i().getConfig().setDefaultJavaPath(javaPath);
                Cache.i().getConfig().getNetwork().setLoadBalancers(List.of(new LoadBalancer("MainLoadBalancer", "proxy", 25565)));

                return true;
            } else {
                return false;
            }
        });
    }

}
