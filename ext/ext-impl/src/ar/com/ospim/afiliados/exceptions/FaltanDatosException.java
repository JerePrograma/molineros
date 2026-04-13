package ar.com.ospim.afiliados.exceptions;

public class FaltanDatosException extends Exception {

	private static final long serialVersionUID = -1188304668078881735L;

	public FaltanDatosException() {
		super();
	}

	public FaltanDatosException(String message, Throwable cause) {
		super(message, cause);
	}

	public FaltanDatosException(String message) {
		super(message);
	}

	public FaltanDatosException(Throwable cause) {
		super(cause);
	}

}
