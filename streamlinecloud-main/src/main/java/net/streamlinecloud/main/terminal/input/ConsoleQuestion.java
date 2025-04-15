package net.streamlinecloud.main.terminal.input;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ConsoleQuestion {

    InputType inputType;
    ConsoleInputContinue next;

    public ConsoleQuestion(InputType inputType, String question, ConsoleInputContinue next) {
        this.inputType = inputType;
        this.next = next;

        StreamlineCloud.log(question);

        Cache.i().getConsoleInputs().add(this);
    }

    public void execute(String input)  {

        if (getInputType().equals(ConsoleQuestion.InputType.INT)) {

            try {

                int i = Integer.parseInt(input);

            } catch (NumberFormatException ex) {

                StreamlineCloud.log("Bitte gebe eine gültige Zahl ein!");
                return;
            }
        } else if (getInputType().equals(ConsoleQuestion.InputType.BOOLEAN)) {

            StreamlineCloud.log("Bitte gebe true (yes) oder false (no) ein");

            if (!input.equals("yes") && !input.equals("no")) {
                return;
            }
        }

        try {
            getNext().execute(input);
            Cache.i().getConsoleInputs().remove(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public enum InputType {
        STRING,
        INT,
        BOOLEAN
    }

}
