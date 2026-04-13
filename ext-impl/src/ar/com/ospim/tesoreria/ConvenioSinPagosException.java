package ar.com.ospim.tesoreria;

public class ConvenioSinPagosException extends Exception {

	private static final long serialVersionUID = 3150770682575450727L;

	public ConvenioSinPagosException() {
		super();
	}

	public ConvenioSinPagosException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConvenioSinPagosException(String message) {
		super(message);
	}

	public ConvenioSinPagosException(Throwable cause) {
		super(cause);
	}

}
