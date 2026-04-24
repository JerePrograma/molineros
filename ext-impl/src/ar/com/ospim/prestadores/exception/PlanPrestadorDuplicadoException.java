package ar.com.ospim.prestadores.exception;

public class PlanPrestadorDuplicadoException extends Exception {
	
	private static final long serialVersionUID = -4088245519958247763L;

	public PlanPrestadorDuplicadoException() {
		super();
	}

	public PlanPrestadorDuplicadoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlanPrestadorDuplicadoException(String message) {
		super(message);
	}

	public PlanPrestadorDuplicadoException(Throwable cause) {
		super(cause);
	}

}
