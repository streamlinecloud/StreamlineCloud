package net.streamlinecloud.main.lang;

import lombok.Getter;

@Getter
public class ReplacePaket {

    String target;
    String value;

    public ReplacePaket(String target, String value) {
        this.target = target;
        this.value = value;
    }
}
