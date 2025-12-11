package ar.com.ospim.global;

public class PrestacionComprobanteExistenteException extends Exception {

	private static final long serialVersionUID = 7081656943439538251L;
	
	public PrestacionComprobanteExistenteException() {
		super();
	}

	public PrestacionComprobanteExistenteException(String message, Throwable cause) {
		super(message, cause);
	}

	public PrestacionComprobanteExistenteException(String message) {
		super(message);
	}

	public PrestacionComprobanteExistenteException(Throwable cause) {
		super(cause);
	}

}
