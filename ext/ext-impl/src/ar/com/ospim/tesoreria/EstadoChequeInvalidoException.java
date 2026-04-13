package ar.com.ospim.tesoreria;

public class EstadoChequeInvalidoException extends Exception {

	private static final long serialVersionUID = -4786499124777678978L;

	public EstadoChequeInvalidoException() {
		super();
	}

	public EstadoChequeInvalidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public EstadoChequeInvalidoException(String message) {
		super(message);
	}

	public EstadoChequeInvalidoException(Throwable cause) {
		super(cause);
	}

}
