package protocol.messages;

import java.io.Serializable;

/**
 * Created by bozhidar on 08.11.17.
 */
public class VoteMessage implements Serializable {
    private byte[] name;
    private long cnonce;
    private byte[] hash;

    public VoteMessage(byte[] name, long cnonce, byte[] hash) {
        this.name = name;
        this.cnonce = cnonce;
        this.hash = hash;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public long getCnonce() {
        return cnonce;
    }

    public void setCnonce(long cnonce) {
        this.cnonce = cnonce;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteMessage)) return false;

        VoteMessage that = (VoteMessage) o;

        if (getCnonce() != that.getCnonce()) return false;
        if (!getName().equals(that.getName())) return false;
        return getHash().equals(that.getHash());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (int) (getCnonce() ^ (getCnonce() >>> 32));
        result = 31 * result + getHash().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VoteMessage{" +
                "name='" + name + '\'' +
                ", cnonce=" + cnonce +
                ", hash='" + hash + '\'' +
                '}';
    }
}
