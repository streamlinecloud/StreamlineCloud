package net.streamlinecloud.main.setup.question;

import net.streamlinecloud.main.StreamlineCloud;
import net.streamlinecloud.main.setup.SetupQuestion;

public class EulaQuestion extends SetupQuestion {

    public EulaQuestion() {
        super(InputType.BOOLEAN, "sc.setup.eula", output -> {
            if (output.equals("yes")) {
                StreamlineCloud.log("sc.setup.eulaAccepted");
                return true;
            } else {
                StreamlineCloud.shutDown();
                return false;
            }
        });
    }
}
