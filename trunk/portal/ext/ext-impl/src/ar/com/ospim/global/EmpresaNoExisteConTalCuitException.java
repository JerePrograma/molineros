package ar.com.ospim.global;

public class EmpresaNoExisteConTalCuitException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmpresaNoExisteConTalCuitException() {
		super();
	}

	public EmpresaNoExisteConTalCuitException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmpresaNoExisteConTalCuitException(String message) {
		super(message);
	}

	public EmpresaNoExisteConTalCuitException(Throwable cause) {
		super(cause);
	}

}
