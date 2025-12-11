package ar.com.ospim.tesoreria;

public class CuentaDuplicadaException extends Exception {

	private static final long serialVersionUID = -5658745513295734019L;

	public CuentaDuplicadaException() {
		super();
	}

	public CuentaDuplicadaException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CuentaDuplicadaException(String arg0) {
		super(arg0);
	}

	public CuentaDuplicadaException(Throwable arg0) {
		super(arg0);
	}

}
