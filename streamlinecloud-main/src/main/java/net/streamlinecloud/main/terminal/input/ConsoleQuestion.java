package net.streamlinecloud.main.terminal.input;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.utils.Cache;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ConsoleQuestion {

    InputType inputType;
    ConsoleInputContinue next;
    String question;

    public ConsoleQuestion(InputType inputType, String question, ConsoleInputContinue next) {
        this.inputType = inputType;
        this.next = next;
        this.question = question;
    }

    public void start() {
        StreamlineCloud.log(question);
        Cache.i().getConsoleInputs().add(this);
    }

    public void execute(String input)  {

        if (getInputType().equals(ConsoleQuestion.InputType.INT)) {

            try {

                int i = Integer.parseInt(input);

            } catch (NumberFormatException ex) {

                StreamlineCloud.log("Please enter a valid number!");
                return;
            }
        } else if (getInputType().equals(ConsoleQuestion.InputType.BOOLEAN)) {

            if (!input.equals("yes") && !input.equals("no")) {
                StreamlineCloud.log("Type yes or no");
                return;
            }
        }

        try {
            getNext().execute(input);
            Cache.i().getConsoleInputs().remove(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public enum InputType {
        STRING,
        INT,
        BOOLEAN
    }

}
