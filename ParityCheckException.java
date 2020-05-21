package project;
import java.util.IllegalFormatFlagsException;
public class ParityCheckException extends IllegalFormatFlagsException {
	private static final long serialVersionUID = 7434815269190400129L;
	/**
	 * Preferred constructor that sets the inherited message field
	 * of the exception object
	 * @param arg0 message passed by the exception that was thrown
	 */
	public ParityCheckException(String arg0) {
		super(arg0);
	}
}

