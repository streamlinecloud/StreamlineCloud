package net.streamlinecloud.main.core.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerIdGenerator {

    private static List<String> ids = new ArrayList<>();
    private static int index = 0;

    static {
        initIds();
    }

    private static void initIds() {
        ids.clear();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            for (int number = 1; number <= 99; number++) {
                ids.add(letter + String.valueOf(number));
            }
        }

        Collections.shuffle(ids);
        index = 0;
    }

    public static synchronized String generateId() {
        if (index >= ids.size()) {
            initIds();
        }
        return ids.get(index++);
    }

}
