package ar.com.ospim.autorizaciones.exceptions;

public class ExcedeCantAutoException extends Exception {
	
	private static final long serialVersionUID = 1446251442885376578L;

	public ExcedeCantAutoException() {
		super();
	}

	public ExcedeCantAutoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcedeCantAutoException(String message) {
		super(message);
	}

	public ExcedeCantAutoException(Throwable cause) {
		super(cause);
	}

}
