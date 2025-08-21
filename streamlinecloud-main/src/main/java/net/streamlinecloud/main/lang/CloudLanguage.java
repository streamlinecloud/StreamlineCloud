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

    public String get(String key) {
        for (String s : messages.keySet()) {
            if (key.equals(s)) {
                return messages.get(s);
            }
        }
        return "unknown";
    }
}
