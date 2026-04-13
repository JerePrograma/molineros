package ar.com.ospim.administracion.exception;

public class ParametroInvalidoException extends Exception {

	public ParametroInvalidoException() {
		super();
	}

	public ParametroInvalidoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ParametroInvalidoException(String arg0) {
		super(arg0);
	}

	public ParametroInvalidoException(Throwable arg0) {
		super(arg0);
	}

	private static final long serialVersionUID = -1745123171137573071L;

}
