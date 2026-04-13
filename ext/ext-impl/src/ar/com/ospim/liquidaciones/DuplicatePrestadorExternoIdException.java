package ar.com.ospim.liquidaciones;

public class DuplicatePrestadorExternoIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicatePrestadorExternoIdException() {
		super();
	}

	public DuplicatePrestadorExternoIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicatePrestadorExternoIdException(String message) {
		super(message);
	}

	public DuplicatePrestadorExternoIdException(Throwable cause) {
		super(cause);
	}
}
