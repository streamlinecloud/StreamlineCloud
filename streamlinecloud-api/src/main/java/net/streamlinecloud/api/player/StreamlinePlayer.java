package net.streamlinecloud.api.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class StreamlinePlayer implements StreamlinePlayerImpl {

    final UUID uuid;
    final String name;
    String currentServerId;

}
