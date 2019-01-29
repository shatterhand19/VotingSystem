package protocol.messages;

/**
 * Created by bozhidar on 11.11.17.
 */
public class ElectionCompleteMessage extends SuccessMessage {
    public ElectionCompleteMessage() {
        super("Election complete");
    }

    @Override
    public String toString() {
        return "ElectionCompleteMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
