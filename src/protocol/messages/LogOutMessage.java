package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class LogOutMessage extends SuccessMessage {
    public LogOutMessage() {
        super("Log out.");
    }

    @Override
    public String toString() {
        return "LogOutMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
