package net.streamlinecloud.main.setup;

import lombok.Getter;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.terminal.input.ConsoleInputContinue;
import net.streamlinecloud.main.utils.Cache;

@Getter
public class SetupQuestion {

    InputType inputType;
    ConsoleInputContinue next;
    String question;

    public SetupQuestion(InputType inputType, String question, ConsoleInputContinue next) {
        this.inputType = inputType;
        this.next = next;
        this.question = question;
    }

    public void start() {
        StreamlineCloud.log(question);
        //Cache.i().getConsoleInputs().add(this);
    }

    public void execute(String input)  {

        if (getInputType().equals(InputType.INT)) {

            try {

                int i = Integer.parseInt(input);

            } catch (NumberFormatException ex) {

                StreamlineCloud.log("Please enter a valid number!");
                return;
            }
        } else if (getInputType().equals(InputType.BOOLEAN)) {

            if (!input.equals("yes") && !input.equals("no")) {
                StreamlineCloud.log("Type yes or no");
                return;
            }
        }

        try {
            getNext().execute(input);
            //Cache.i().getConsoleInputs().remove(this);
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
