package io.streamlinemc.main.terminal;

import io.streamlinemc.main.utils.Cache;
import org.fusesource.jansi.Ansi;

public enum Color {

    BLACK(0, 0, 0),              //0
    RED(255, 107, 104),            //1
    DARK_GREEN(0, 170, 0),       //2
    AQUA(85, 255, 255),          //3
    DARK_RED(170, 0, 0),         //4
    LIGHT_PURPLE(255, 85, 255),  //5
    GOLD(255, 170, 0),           //6
    GRAY(170, 170, 170),         //7
    DARK_GRAY(85, 85, 85),       //8
    YELLOW(255, 255, 85),        //9
    GREEN(137, 202, 120),          //10


    IMPORTANT(255, 48, 48);      //11

    private static final Color[] VALUES = values();
    private final String ansi;

    Color(int r, int g, int b) {
        this.ansi = Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(r, g, b).toString();
    }

    public static String translate(String output) {

        if (/*StaticCache.getConfig() == null ||*/ Cache.i().getArguments().contains("-runWithoutColor") || Cache.i().isDisabledColors()) {
            for (Color color : VALUES) {
                output = output.replace("§" + color.ordinal(),"");
                output = output.replace("§" + color.name(), "");
            }
            return output;
        }

        for (Color color : VALUES) {
            output = output.replace("§" + color.ordinal(), color.ansi);
            output = output.replace("§" + color.name(), color.ansi);
        }
        return output;
    }

    public static String remove(String output) {
        for (Color color : VALUES) {
            output = output.replace("§" + color.ordinal(),"");
            output = output.replace("§" + color.name(), "");
        }
        return output;
    }

}
