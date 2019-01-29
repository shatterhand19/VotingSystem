package protocol.messages;

/**
 * Created by bozhidar on 10.11.17.
 */
public class LogOutSuccessfulMessage extends SuccessMessage {
    public LogOutSuccessfulMessage() {
        super("You were successfully logged out!");
    }

    @Override
    public String toString() {
        return "LogOutSuccessfulMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
