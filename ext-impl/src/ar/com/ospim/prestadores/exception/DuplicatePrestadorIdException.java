package ar.com.ospim.prestadores.exception;

public class DuplicatePrestadorIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicatePrestadorIdException() {
		super();
	}

	public DuplicatePrestadorIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicatePrestadorIdException(String message) {
		super(message);
	}

	public DuplicatePrestadorIdException(Throwable cause) {
		super(cause);
	}

}
