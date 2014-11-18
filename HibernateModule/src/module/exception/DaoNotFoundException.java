package module.exception;

public class DaoNotFoundException extends Exception {

	public DaoNotFoundException() {
		super();
	}

	public DaoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoNotFoundException(String message) {
		super(message);
	}

	public DaoNotFoundException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3562955783028600006L;

}
