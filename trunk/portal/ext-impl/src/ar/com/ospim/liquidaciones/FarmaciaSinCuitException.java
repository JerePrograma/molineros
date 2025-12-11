package ar.com.ospim.liquidaciones;

public class FarmaciaSinCuitException extends Exception {
	private static final long serialVersionUID = 1L;

	public FarmaciaSinCuitException() {
		super();
	}

	public FarmaciaSinCuitException(String message, Throwable cause) {
		super(message, cause);
	}

	public FarmaciaSinCuitException(String message) {
		super(message);
	}

	public FarmaciaSinCuitException(Throwable cause) {
		super(cause);
	}

}
