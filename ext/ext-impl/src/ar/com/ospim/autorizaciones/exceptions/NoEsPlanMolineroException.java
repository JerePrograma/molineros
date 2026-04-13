package ar.com.ospim.autorizaciones.exceptions;

public class NoEsPlanMolineroException extends Exception {

	private static final long serialVersionUID = -389674947296963669L;

	public NoEsPlanMolineroException() {
		super();
	}

	public NoEsPlanMolineroException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoEsPlanMolineroException(String message) {
		super(message);
	}

	public NoEsPlanMolineroException(Throwable cause) {
		super(cause);
	}

}
