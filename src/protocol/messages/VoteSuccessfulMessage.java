package protocol.messages;

/**
 * Created by bozhidar on 09.11.17.
 */
public class VoteSuccessfulMessage extends SuccessMessage {
    public VoteSuccessfulMessage() {
        super("There are still no results!\nYou will be logged out, log back in later for results!");
    }

    @Override
    public String toString() {
        return "VoteSuccessfulMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
