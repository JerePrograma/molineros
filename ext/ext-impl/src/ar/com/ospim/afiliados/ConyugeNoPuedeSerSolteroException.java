package ar.com.ospim.afiliados;

import com.liferay.portal.PortalException;

public class ConyugeNoPuedeSerSolteroException extends PortalException {
	
	private static final long serialVersionUID = 1L;

	public ConyugeNoPuedeSerSolteroException() {
		super();
	}

	public ConyugeNoPuedeSerSolteroException(String msg) {
		super(msg);
	}

	public ConyugeNoPuedeSerSolteroException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ConyugeNoPuedeSerSolteroException(Throwable cause) {
		super(cause);
	}
}