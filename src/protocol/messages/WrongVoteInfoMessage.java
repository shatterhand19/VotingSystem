package protocol.messages;

/**
 * Created by bozhidar on 09.11.17.
 */
public class WrongVoteInfoMessage extends ErrorMessage {
    public WrongVoteInfoMessage() {
        super("The vote info is wrong!");
    }

    public WrongVoteInfoMessage(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "WrongVoteInfoMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
