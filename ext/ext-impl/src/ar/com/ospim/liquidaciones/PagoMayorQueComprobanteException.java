package ar.com.ospim.liquidaciones;

public class PagoMayorQueComprobanteException extends Exception {

	private static final long serialVersionUID = -4280807660550250495L;

	public PagoMayorQueComprobanteException() {
		super();
	}

	public PagoMayorQueComprobanteException(String message, Throwable cause) {
		super(message, cause);
	}

	public PagoMayorQueComprobanteException(String message) {
		super(message);
	}

	public PagoMayorQueComprobanteException(Throwable cause) {
		super(cause);
	}

}
