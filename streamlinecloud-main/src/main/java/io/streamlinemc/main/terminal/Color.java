package io.streamlinemc.main.terminal;

import io.streamlinemc.main.utils.Cache;


public class Color {

    public static String translate(String output) {
        if (Cache.i().getArguments().contains("--runWithoutColor") || Cache.i().isDisabledColors()) {
            return remove(output);
        }

        if (Cache.i().getArguments().contains("--useLegacyColor") || Cache.i().isUseLgecyColor()) {
            for (LegacyColorCodes value : LegacyColorCodes.values()) {
                output = output.replace("§" + value.getIndex(), value.getAnsi());
                output = output.replace("§" + value.name().toUpperCase(), value.getAnsi());
            }
        }

        for (ColorCodes value : ColorCodes.values()) {
            output = output.replace("§" + value.getIndex(), value.getAnsi());
            output = output.replace("§" + value.name().toUpperCase(), value.getAnsi());
        }
        return output;
    }

    public static String remove(String output) {
        for (ColorCodes value : ColorCodes.values()) {
            output = output.replace("§" + value.getIndex(), "");
            output = output.replace("§" + value.name().toUpperCase(), "");
        }
        return output;
    }



}
