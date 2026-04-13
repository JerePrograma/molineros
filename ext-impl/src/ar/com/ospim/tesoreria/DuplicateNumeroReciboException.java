package ar.com.ospim.tesoreria;

public class DuplicateNumeroReciboException extends Exception {

	private static final long serialVersionUID = -6361057798453832262L;

	public DuplicateNumeroReciboException() {
		super();
	}

	public DuplicateNumeroReciboException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DuplicateNumeroReciboException(String arg0) {
		super(arg0);
	}

	public DuplicateNumeroReciboException(Throwable arg0) {
		super(arg0);
	}

}
