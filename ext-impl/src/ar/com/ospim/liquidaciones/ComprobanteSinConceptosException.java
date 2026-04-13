package ar.com.ospim.liquidaciones;

public class ComprobanteSinConceptosException extends Exception {

	private static final long serialVersionUID = 6408835672157034079L;

	public ComprobanteSinConceptosException() {
		super();
	}

	public ComprobanteSinConceptosException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComprobanteSinConceptosException(String message) {
		super(message);
	}

	public ComprobanteSinConceptosException(Throwable cause) {
		super(cause);
	}

}
