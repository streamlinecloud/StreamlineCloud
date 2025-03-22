package net.streamlinecloud.main.terminal.api;

import lombok.Setter;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public abstract class CloudCommand implements CloudCommandImpl {

    @Setter
    String name;

    @Setter
    String description;

    @Setter
    String[] aliases;

    @Setter
    List<CloudCommandArgument> arguments;

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String[] aliases() {
        return aliases;
    }

    @Override
    public List<CloudCommandArgument> arguments() {
        return arguments;
    }

    @Override
    public void execute(String[] args) throws MalformedURLException {
    }

    public void addArgument(CloudCommandArgument argument) {
        if (arguments == null) {
            arguments = new ArrayList<>();
        }

        arguments.add(argument);
    }

}
