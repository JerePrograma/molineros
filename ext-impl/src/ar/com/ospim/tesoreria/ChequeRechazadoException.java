package ar.com.ospim.tesoreria;

public class ChequeRechazadoException extends Exception {

	private static final long serialVersionUID = -1630117907773047167L;

	public ChequeRechazadoException() {
		super();
	}

	public ChequeRechazadoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ChequeRechazadoException(String arg0) {
		super(arg0);
	}

	public ChequeRechazadoException(Throwable arg0) {
		super(arg0);
	}

}
