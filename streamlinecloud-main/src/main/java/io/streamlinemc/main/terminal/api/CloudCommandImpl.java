package io.streamlinemc.main.terminal.api;

import java.net.MalformedURLException;

public interface CloudCommandImpl {

    String name();

    String description();

    String[] aliases();

    void execute(String[] args) throws MalformedURLException;
}
