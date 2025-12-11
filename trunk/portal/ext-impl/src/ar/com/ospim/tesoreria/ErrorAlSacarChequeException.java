package ar.com.ospim.tesoreria;

public class ErrorAlSacarChequeException extends Exception {

	private static final long serialVersionUID = 7412331258721328639L;

	public ErrorAlSacarChequeException() {
		super();
	}

	public ErrorAlSacarChequeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ErrorAlSacarChequeException(String arg0) {
		super(arg0);
	}

	public ErrorAlSacarChequeException(Throwable arg0) {
		super(arg0);
	}

}
