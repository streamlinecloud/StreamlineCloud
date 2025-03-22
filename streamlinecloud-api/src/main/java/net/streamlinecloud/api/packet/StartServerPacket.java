package net.streamlinecloud.api.packet;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StartServerPacket {

    String group = "without";
    String[] templates = new String[]{};
}
