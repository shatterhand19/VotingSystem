package protocol.messages;

import java.io.Serializable;

/**
 * Created by bozhidar on 08.11.17.
 */
public class SuccessMessage implements Serializable {
    protected String message;

    public SuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuccessMessage)) return false;

        SuccessMessage that = (SuccessMessage) o;

        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }

    @Override
    public String toString() {
        return "SuccessMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
