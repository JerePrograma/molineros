package ar.com.ospim.afiliados.exceptions;

public class SituacionLaboralInvalidaException extends Exception {

	private static final long serialVersionUID = -8342199607244625918L;

	public SituacionLaboralInvalidaException() {
		super();
	}

	public SituacionLaboralInvalidaException(String message, Throwable cause) {
		super(message, cause);
	}

	public SituacionLaboralInvalidaException(String message) {
		super(message);
	}

	public SituacionLaboralInvalidaException(Throwable cause) {
		super(cause);
	}

}
