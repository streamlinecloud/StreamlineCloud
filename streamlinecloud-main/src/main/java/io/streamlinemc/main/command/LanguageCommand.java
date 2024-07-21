package io.streamlinemc.main.command;

import io.streamlinemc.main.StreamlineCloud;
import io.streamlinemc.main.lang.CloudLanguage;
import io.streamlinemc.main.terminal.api.CloudCommand;
import io.streamlinemc.main.utils.Cache;

public class LanguageCommand extends CloudCommand {

    public LanguageCommand() {
        setName("language");
        setAliases(new String[]{"lang", "l"});
        setDescription("Set language");
    }

    @Override
    public void execute(String[] args) {

        String sub = args[1];

        if (sub != null) {

            if (sub.equals("list")) {
                StreamlineCloud.log("Langs:");
                for (CloudLanguage langs : Cache.i().getLanguages()) StreamlineCloud.log(langs.getMessages().get("lang.name") + " (" + langs.getName() + ")");

            } else if (sub.equals("help")) {
                StreamlineCloud.log("- langs list");
                StreamlineCloud.log("- langs set <lang>");
            }

        } else {
            StreamlineCloud.log("Unknown subcommand lang help");
        }
    }
}
