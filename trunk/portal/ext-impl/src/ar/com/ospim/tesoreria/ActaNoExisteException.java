package ar.com.ospim.tesoreria;

public class ActaNoExisteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActaNoExisteException() {
		super();
	}

	public ActaNoExisteException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ActaNoExisteException(String arg0) {
		super(arg0);
	}

	public ActaNoExisteException(Throwable arg0) {
		super(arg0);
	}

}
