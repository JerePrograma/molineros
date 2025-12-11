package ar.com.ospim.liquidaciones;

public class NoSuchPrestadorEntryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchPrestadorEntryException() {
		super();
	}

	public NoSuchPrestadorEntryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchPrestadorEntryException(String message) {
		super(message);
	}

	public NoSuchPrestadorEntryException(Throwable cause) {
		super(cause);
	}

}
