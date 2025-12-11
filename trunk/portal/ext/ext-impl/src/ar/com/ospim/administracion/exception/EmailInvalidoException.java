package ar.com.ospim.administracion.exception;

public class EmailInvalidoException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailInvalidoException() {
		super();
	}

	public EmailInvalidoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public EmailInvalidoException(String arg0) {
		super(arg0);
	}

	public EmailInvalidoException(Throwable arg0) {
		super(arg0);
	}

}
