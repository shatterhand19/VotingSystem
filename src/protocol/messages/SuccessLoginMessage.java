package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class SuccessLoginMessage extends SuccessMessage {
    public SuccessLoginMessage() {
        super("Log in successful!");
    }

    @Override
    public String toString() {
        return "SuccessLoginMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
