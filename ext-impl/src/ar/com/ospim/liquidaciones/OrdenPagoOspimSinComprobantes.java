package ar.com.ospim.liquidaciones;

public class OrdenPagoOspimSinComprobantes extends Exception {

	private static final long serialVersionUID = -4280807660550250495L;

	public OrdenPagoOspimSinComprobantes() {
		super();
	}

	public OrdenPagoOspimSinComprobantes(String message, Throwable cause) {
		super(message, cause);
	}

	public OrdenPagoOspimSinComprobantes(String message) {
		super(message);
	}

	public OrdenPagoOspimSinComprobantes(Throwable cause) {
		super(cause);
	}

}
