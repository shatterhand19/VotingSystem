package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class WrongUserDataMessage extends ErrorMessage {
    public WrongUserDataMessage() {
        super("Wrong user data!");
    }

    public WrongUserDataMessage(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "WrongUserDataMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
