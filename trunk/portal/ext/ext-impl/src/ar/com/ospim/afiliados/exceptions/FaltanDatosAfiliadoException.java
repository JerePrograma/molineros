package ar.com.ospim.afiliados.exceptions;

public class FaltanDatosAfiliadoException extends Exception {

	private static final long serialVersionUID = -3076026131227231083L;

	public FaltanDatosAfiliadoException() {
		super();
	}

	public FaltanDatosAfiliadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public FaltanDatosAfiliadoException(String message) {
		super(message);
	}

	public FaltanDatosAfiliadoException(Throwable cause) {
		super(cause);
	}

}
