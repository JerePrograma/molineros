package ar.com.ospim.liquidaciones;

public class AnticiposNoPagadosException extends Exception {

	private static final long serialVersionUID = 5453458435856914793L;

	public AnticiposNoPagadosException() {
		super();
	}

	public AnticiposNoPagadosException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnticiposNoPagadosException(String message) {
		super(message);
	}

	public AnticiposNoPagadosException(Throwable cause) {
		super(cause);
	}

}
