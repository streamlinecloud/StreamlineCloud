package net.streamlinecloud.main.terminal.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StreamlineSubcommand {

    @NonNull
    String name;

    @NonNull
    SubcommandExecute execute;

    StreamlineCommandTree commandTree;

}
