package net.streamlinecloud.api.terminal;

import lombok.Getter;

/**
 * A paket used to insert dynamic variables into translated messages.
 * <h3>Example</h1>
 * Translated string: 'server %0 started' & ReplacePacket: target=%0 value=lobby-1 -> Final message: 'server lobby-1 started'
 */
@Getter
public class ReplacePaket {

    String target;
    String value;

    public ReplacePaket(String target, String value) {
        this.target = target;
        this.value = value;
    }
}
