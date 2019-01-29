package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class WrongMessage extends ErrorMessage {
    public WrongMessage() {
        super("You have sent wrong message, breaking the control flow. Nasty hacker!");
    }

    public WrongMessage(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "WrongMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
