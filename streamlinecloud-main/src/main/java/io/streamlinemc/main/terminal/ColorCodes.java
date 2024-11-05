package io.streamlinemc.main.terminal;

import lombok.Getter;
import org.jline.jansi.Ansi;

@Getter
public enum ColorCodes {

    BLACK("black", 0, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(0, 0, 0).toString()),           // 0
    DARK_BLUE("dark_blue", 1, Ansi.ansi().reset().fg(Ansi.Color.BLUE).toString()),     // 1
    GREEN("green", 2, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(137, 202, 120).toString()),            // 2
    CYAN("cyan", 3, Ansi.ansi().reset().fg(Ansi.Color.CYAN).toString()),               // 3
    DARK_RED("dark_red", 4, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(170, 0, 0).toString()),        // 4
    PURPLE("purple", 5, Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).toString()),        // 5
    ORANGE("orange", 6, Ansi.ansi().reset().fg(Ansi.Color.YELLOW).toString()),         // 6
    GRAY("gray", 7, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(170, 170, 170).toString()),              // 7
    DARK_GRAY("dark_gray", 8, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(85, 85, 85).toString()), // 8
    BLUE("blue", 9, Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString()),        // 9
    LIGHT_GREEN("light_green", 10, Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString()), // 10
    AQUA("aqua", 11, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(85, 255, 255).toString()),       // 11
    RED("red", 12, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(255, 107, 104).toString()),          // 12
    PINK("pink", 13, Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString()),    // 13
    YELLOW("yellow", 14, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(255, 255, 85).toString()), // 14
    WHITE("white", 15, Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString()),    // 15
    OBFUSCATED("obfuscated", 16, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()), // 16
    BOLD("bold", 17, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),       // 17
    STRIKETHROUGH("strikethrough", 18, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()), // 18
    UNDERLINE("underline", 19, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),    // 19
    ITALIC("italic", 20, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()),             // 20
    DEFAULT("default", 21, Ansi.ansi().reset().toString()),
    IMPORTANT("important", 22, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(255, 48, 48).toString()),
    DARK_GREEN("green", 2, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(0, 170, 0).toString()),
    LIGHT_PURPLE("green", 2, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(255, 85, 255).toString()),
    GOLD("green", 2, Ansi.ansi().a(Ansi.Attribute.RESET).fgRgb(255, 170, 0).toString());


    private static final ColorCodes[] VALUES = values();
    private final String name;
    private final String ansi;
    private final int index;

    ColorCodes(String name, int index, String ansi) {
        this.name = name;
        this.index = index;
        this.ansi = ansi;
    }

    public String ansiCode() {
        return this.ansi;
    }

}
