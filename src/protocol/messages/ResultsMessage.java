package protocol.messages;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bozhidar on 10.11.17.
 */
public class ResultsMessage implements Serializable {
    private ArrayList<String> names;
    private ArrayList<Integer> votes;

    public ResultsMessage(ArrayList<String> names, ArrayList<Integer> votes) {
        this.names = names;
        this.votes = votes;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<Integer> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Integer> votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultsMessage)) return false;

        ResultsMessage that = (ResultsMessage) o;

        if (!getNames().equals(that.getNames())) return false;
        return getVotes().equals(that.getVotes());
    }

    @Override
    public int hashCode() {
        int result = getNames().hashCode();
        result = 31 * result + getVotes().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ResultsMessage{" +
                "names=" + names +
                ", votes=" + votes +
                '}';
    }
}
