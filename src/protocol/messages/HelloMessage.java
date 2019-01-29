package protocol.messages;

/**
 * Created by bozhidar on 08.11.17.
 */
public class HelloMessage extends SuccessMessage {
    public HelloMessage(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "HelloMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
