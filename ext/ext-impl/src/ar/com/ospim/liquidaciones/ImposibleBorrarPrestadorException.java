package ar.com.ospim.liquidaciones;

public class ImposibleBorrarPrestadorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImposibleBorrarPrestadorException() {
		super();
	}

	public ImposibleBorrarPrestadorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImposibleBorrarPrestadorException(String message) {
		super(message);
	}

	public ImposibleBorrarPrestadorException(Throwable cause) {
		super(cause);
	}

}
