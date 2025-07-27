package net.streamlinecloud.api.software;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.server.ServerRuntime;

@Getter @Setter
@AllArgsConstructor
public class StreamlineSoftware {

    String name;
    ServerRuntime type;
    String folder;
    boolean cached;

}
