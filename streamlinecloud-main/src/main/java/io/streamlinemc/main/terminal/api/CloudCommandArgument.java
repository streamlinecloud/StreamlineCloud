package io.streamlinemc.main.terminal.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CloudCommandArgument {

    private final String argument;
    private final List<CloudCommandArgument> subarguments;

    public CloudCommandArgument(String argument) {
        this.argument = argument;
        subarguments = new ArrayList<>();
    }


    public CloudCommandArgument addSubargument(CloudCommandArgument subargument) {
        this.subarguments.add(subargument);
        return this;
    }
}
