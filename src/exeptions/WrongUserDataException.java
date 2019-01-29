package exeptions;

/**
 * Created by bozhidar on 08.11.17.
 */
public class WrongUserDataException extends Exception {
    public WrongUserDataException(String msg) {
        super(msg);
    }
}
