package ar.com.ospim.tesoreria;

public class OrdenPagoInexistenteException extends Exception {

	private static final long serialVersionUID = -1387299585943880765L;

	public OrdenPagoInexistenteException() {
		super();
	}

	public OrdenPagoInexistenteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public OrdenPagoInexistenteException(String arg0) {
		super(arg0);
	}

	public OrdenPagoInexistenteException(Throwable arg0) {
		super(arg0);
	}

}
