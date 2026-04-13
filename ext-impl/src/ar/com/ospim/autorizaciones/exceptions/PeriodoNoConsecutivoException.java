package ar.com.ospim.autorizaciones.exceptions;

public class PeriodoNoConsecutivoException extends Exception {

	private static final long serialVersionUID = 3312237675331667483L;

	public PeriodoNoConsecutivoException() {
		super();
	}

	public PeriodoNoConsecutivoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PeriodoNoConsecutivoException(String message) {
		super(message);
	}

	public PeriodoNoConsecutivoException(Throwable cause) {
		super(cause);
	}

}
