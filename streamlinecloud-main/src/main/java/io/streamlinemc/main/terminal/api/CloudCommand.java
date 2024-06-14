package io.streamlinemc.main.terminal.api;

import lombok.Setter;

import java.net.MalformedURLException;

public abstract class CloudCommand implements CloudCommandImpl {

    @Setter
    String name;

    @Setter
    String description;

    @Setter
    String[] aliases;

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
    public void execute(String[] args) throws MalformedURLException {
    }
}
