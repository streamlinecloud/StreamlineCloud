package net.streamlinecloud.main.setup;

import lombok.Getter;
import lombok.Setter;
import net.streamlinecloud.api.extension.event.EventListener;
import net.streamlinecloud.api.extension.event.StreamlineEvent;
import net.streamlinecloud.api.extension.event.console.ConsoleInputEvent;
import net.streamlinecloud.main.CloudMain;
import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.extension.ExtensionManager;

@Getter @Setter
public class SetupQuestion implements EventListener {

    InputType inputType;
    Validator validator;
    Continue continueAction;
    String question;

    private static SetupQuestion current = null;

    public SetupQuestion(InputType inputType, String question, Validator validator) {
        this.inputType = inputType;
        this.validator = validator;
        this.question = question;

        ExtensionManager.eventManager.registerListener(this);
    }

    public SetupQuestion() {
        ExtensionManager.eventManager.registerListener(this);
    }

    public void start(Continue continueAction) {
        if (current != null) {
            StreamlineCloud.log("Please wait for the current question to be answered!");
            return;
        }

        current = this;
        this.continueAction = continueAction;

        //CloudMain.getInstance().getTerminal().getRunner().setRestricted(true);
        StreamlineCloud.log(question);
    }

    public void start() {
        start(result -> {});
    }

    @StreamlineEvent
    public void onConsoleInput(ConsoleInputEvent event) {
        execute(event.getInput());
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
            if (validator.execute(input)) {
                continueAction.execute(input);
                current = null;
                CloudMain.getInstance().getTerminal().getRunner().setRestricted(false);
            } else {
                StreamlineCloud.log("Invalid input!");
                StreamlineCloud.log(question);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public enum InputType {
        STRING,
        INT,
        BOOLEAN
    }

    public interface Continue {
        void execute(String result);
    }

    public interface Validator {
        boolean execute(String output) throws InterruptedException;
    }
}
