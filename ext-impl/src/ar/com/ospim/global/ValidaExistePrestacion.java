package ar.com.ospim.global;

public class ValidaExistePrestacion extends Exception {

	private static final long serialVersionUID = 7081656943439538251L;
	
	public ValidaExistePrestacion() {
		super();
	}

	public ValidaExistePrestacion(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidaExistePrestacion(String message) {
		super(message);
	}

	public ValidaExistePrestacion(Throwable cause) {
		super(cause);
	}

}
