package io.streamlinemc.main.terminal.input;

import io.streamlinemc.main.utils.Cache;
import lombok.Getter;

@Getter
public class ConsoleInput {

    InputType inputType;
    ConsoleInputContinue next;

    public ConsoleInput(InputType inputType, ConsoleInputContinue next) {
        this.inputType = inputType;
        this.next = next;

        Cache.i().getConsoleInputs().add(this);
    }

    public enum InputType {
        STRING,
        INT,
        BOOLEAN
    }

}
