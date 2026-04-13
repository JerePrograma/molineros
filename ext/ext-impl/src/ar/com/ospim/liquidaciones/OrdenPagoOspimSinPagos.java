package ar.com.ospim.liquidaciones;

public class OrdenPagoOspimSinPagos extends Exception {

	private static final long serialVersionUID = 3701498309002756845L;

	public OrdenPagoOspimSinPagos() {
		super();
	}

	public OrdenPagoOspimSinPagos(String message, Throwable cause) {
		super(message, cause);
	}

	public OrdenPagoOspimSinPagos(String message) {
		super(message);
	}

	public OrdenPagoOspimSinPagos(Throwable cause) {
		super(cause);
	}

}
