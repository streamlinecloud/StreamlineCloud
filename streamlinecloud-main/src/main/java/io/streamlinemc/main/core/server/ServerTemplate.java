package io.streamlinemc.main.core.server;

import lombok.Getter;

import java.io.File;

@Getter
public class ServerTemplate {

    File serverJar;
    File path;

    public ServerTemplate(File serverJar, File path) {
        this.serverJar = serverJar;
        this.path = path;
    }
}
