package exeptions;

/**
 * Created by bozhidar on 08.11.17.
 */
public class UserNotRegisteredException extends Exception {
    public UserNotRegisteredException(String msg) {
        super(msg);
    }
}
