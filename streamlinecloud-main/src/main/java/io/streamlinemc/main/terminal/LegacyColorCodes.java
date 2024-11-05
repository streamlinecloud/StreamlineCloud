package io.streamlinemc.main.terminal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jline.jansi.Ansi;

@Getter
public enum LegacyColorCodes {

    BLACK("black", 0, Ansi.ansi().reset().fg(Ansi.Color.BLACK).toString()),           // 0
    DARK_BLUE("dark_blue", 1, Ansi.ansi().reset().fg(Ansi.Color.BLUE).toString()),     // 1
    GREEN("green", 2, Ansi.ansi().reset().fg(Ansi.Color.GREEN).toString()),            // 2
    CYAN("cyan", 3, Ansi.ansi().reset().fg(Ansi.Color.CYAN).toString()),               // 3
    DARK_RED("dark_red", 4, Ansi.ansi().reset().fg(Ansi.Color.RED).toString()),        // 4
    PURPLE("purple", 5, Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).toString()),        // 5
    ORANGE("orange", 6, Ansi.ansi().reset().fg(Ansi.Color.YELLOW).toString()),         // 6
    GRAY("gray", 7, Ansi.ansi().reset().fg(Ansi.Color.WHITE).toString()),              // 7
    DARK_GRAY("dark_gray", 8, Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString()), // 8
    BLUE("blue", 9, Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString()),        // 9
    LIGHT_GREEN("light_green", 10, Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString()), // 10
    AQUA("aqua", 11, Ansi.ansi().reset().fg(Ansi.Color.CYAN).bold().toString()),       // 11
    RED("red", 12, Ansi.ansi().reset().fg(Ansi.Color.RED).bold().toString()),          // 12
    PINK("pink", 13, Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString()),    // 13
    YELLOW("yellow", 14, Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString()), // 14
    WHITE("white", 15, Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString()),    // 15
    OBFUSCATED("obfuscated", 16, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()), // 16
    BOLD("bold", 17, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),       // 17
    STRIKETHROUGH("strikethrough", 18, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()), // 18
    UNDERLINE("underline", 19, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),    // 19
    ITALIC("italic", 20, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()),             // 20
    DEFAULT("default", 21, Ansi.ansi().reset().toString()),
    IMPORTANT("important", 22, RED.ansi + BOLD.ansi);           // 2

    private static final LegacyColorCodes[] VALUES = values();
    private final String name;
    private final String ansi;
    private final int index;

    LegacyColorCodes(String name, int index, String ansi) {
        this.name = name;
        this.index = index;
        this.ansi = ansi;
    }

    public String ansiCode() {
        return this.ansi;
    }
}
