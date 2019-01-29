package protocol.messages;

/**
 * Created by bozhidar on 10.11.17.
 */
public class VoteComplete extends SuccessMessage {
    public VoteComplete() {
        super("Vote process complete!");
    }

    @Override
    public String toString() {
        return "VoteComplete{" +
                "message='" + message + '\'' +
                '}';
    }
}
