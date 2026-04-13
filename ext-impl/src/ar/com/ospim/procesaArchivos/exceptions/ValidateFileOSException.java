package ar.com.ospim.procesaArchivos.exceptions;

public class ValidateFileOSException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ValidateFileOSException () {
		super();
	}

	public ValidateFileOSException (String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ValidateFileOSException (String arg0) {
		super(arg0);
	}

	public ValidateFileOSException (Throwable arg0) {
		super(arg0);
	}

}
