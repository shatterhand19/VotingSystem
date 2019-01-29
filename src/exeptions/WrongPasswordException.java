package exeptions;

/**
 * Created by bozhidar on 08.11.17.
 */
public class WrongPasswordException extends Exception {
    public WrongPasswordException(String msg) {
        super(msg);
    }
}
