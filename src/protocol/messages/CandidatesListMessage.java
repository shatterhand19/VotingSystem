package protocol.messages;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by bozhidar on 09.11.17.
 */
public class CandidatesListMessage implements Serializable {
    private String[] names;
    private long nonce;

    public CandidatesListMessage(String[] names, long nonce) {
        this.names = names;
        this.nonce = nonce;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandidatesListMessage)) return false;

        CandidatesListMessage that = (CandidatesListMessage) o;

        if (getNonce() != that.getNonce()) return false;

        return Arrays.equals(getNames(), that.getNames());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getNames());
        result = 31 * result + (int) (getNonce() ^ (getNonce() >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "CandidatesListMessage{" +
                "names=" + Arrays.toString(names) +
                ", nonce=" + nonce +
                '}';
    }
}
