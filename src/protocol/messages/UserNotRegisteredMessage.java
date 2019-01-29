package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class UserNotRegisteredMessage extends ErrorMessage {
    public UserNotRegisteredMessage() {
        super("User not registered!");
    }

    @Override
    public String toString() {
        return "UserNotRegisteredMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
