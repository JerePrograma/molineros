package ar.com.ospim.tesoreria;

public class DuplicateActaIdException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateActaIdException() {
		super();
	}

	public DuplicateActaIdException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DuplicateActaIdException(String arg0) {
		super(arg0);
	}

	public DuplicateActaIdException(Throwable arg0) {
		super(arg0);
	}

}
