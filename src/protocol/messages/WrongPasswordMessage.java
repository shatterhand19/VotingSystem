package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class WrongPasswordMessage extends ErrorMessage {
    public WrongPasswordMessage() {
        super("Wrong password!");
    }

    @Override
    public String toString() {
        return "WrongPasswordMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
