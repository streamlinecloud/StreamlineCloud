package net.streamlinecloud.main.command;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.config.MainConfig;
import net.streamlinecloud.main.lang.CloudLanguage;
import net.streamlinecloud.main.lang.LangManager;
import net.streamlinecloud.main.terminal.api.CloudCommand;
import net.streamlinecloud.main.utils.Cache;

public class LanguageCommand extends CloudCommand {

    public LanguageCommand() {
        setName("language");
        setAliases(new String[]{"lang"});
        setDescription("Set language");
    }

    @Override
    public void execute(String[] args) {

        String sub = args[1];

        if (sub != null) {

            switch (sub) {
                case "list" -> {
                    StreamlineCloud.log("Langs:");
                    for (CloudLanguage langs : LangManager.getInstance().getLanguages())
                        StreamlineCloud.log(langs.getMessages().get("lang.name") + " (" + langs.getName() + ")");
                }
                case "set" -> {

                    if (LangManager.getInstance().getLanguages().stream().noneMatch(l -> l.getName().equals(args[2]))) {
                        StreamlineCloud.log("Language " + args[2] + " invalid");
                        return;
                    }

                    CloudLanguage language = LangManager.getInstance().getLanguages().stream().filter(l -> l.getName().equals(args[2])).findFirst().get();
                    LangManager.getInstance().setCurrentLanguage(language);
                    Cache.i().getConfig().setLanguage(language.getName());
                    MainConfig.saveConfig();

                    StreamlineCloud.log("Language set to " + language.getName());
                }
            }

        } else {
            StreamlineCloud.log("Unknown subcommand lang help");
        }
    }
}
