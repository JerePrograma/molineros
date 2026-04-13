package ar.com.ospim.afiliados.exceptions;

public class FaltanPlanesException extends Exception {

	private static final long serialVersionUID = -8372164458468476665L;

	public FaltanPlanesException() {
		super();
	}

	public FaltanPlanesException(String message, Throwable cause) {
		super(message, cause);
	}

	public FaltanPlanesException(String message) {
		super(message);
	}

	public FaltanPlanesException(Throwable cause) {
		super(cause);
	}

}
