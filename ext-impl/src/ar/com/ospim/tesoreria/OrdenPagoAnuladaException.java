package ar.com.ospim.tesoreria;

public class OrdenPagoAnuladaException extends Exception {

	private static final long serialVersionUID = -4036916012829269747L;

	public OrdenPagoAnuladaException() {
		super();
	}

	public OrdenPagoAnuladaException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrdenPagoAnuladaException(String message) {
		super(message);
	}

	public OrdenPagoAnuladaException(Throwable cause) {
		super(cause);
	}

}
