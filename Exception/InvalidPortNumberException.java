/**
 * Description
 *
 * @author YUHAO SONG
 * Student_id  981738
 * Date        2019-09-05
 * @version 1.5
 */
public class InvalidPortNumberException extends Exception {

    public InvalidPortNumberException() {
        super("Port should be number in the range from 1025 to 65536." + "\n" + "Please try again !" + '\n');
    }

    public InvalidPortNumberException(String message) {
        super(message);
    }

}
