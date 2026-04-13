package ar.com.ospim.liquidaciones;

public class ComprobanteSinImporteException extends Exception {

	private static final long serialVersionUID = 6145127348870697741L;

	public ComprobanteSinImporteException() {
		super();
	}

	public ComprobanteSinImporteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ComprobanteSinImporteException(String arg0) {
		super(arg0);
	}

	public ComprobanteSinImporteException(Throwable arg0) {
		super(arg0);
	}

}
