package net.streamlinecloud.main.lang;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class CloudLanguage {

    String name;
    HashMap<String, String> messages;

    public CloudLanguage(String name, HashMap<String, String> messages) {
        this.name = name;
        this.messages = messages;
    }
}
