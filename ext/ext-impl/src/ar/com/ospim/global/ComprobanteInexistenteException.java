package ar.com.ospim.global;

public class ComprobanteInexistenteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComprobanteInexistenteException() {
		super();
	}

	public ComprobanteInexistenteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComprobanteInexistenteException(String message) {
		super(message);
	}

	public ComprobanteInexistenteException(Throwable cause) {
		super(cause);
	}

}
