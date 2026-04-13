package ar.com.ospim.afiliados;

import com.liferay.portal.PortalException;

public class HijoNoPuedeSerCasadoException extends PortalException {
	
	private static final long serialVersionUID = 1L;

	public HijoNoPuedeSerCasadoException() {
		super();
	}

	public HijoNoPuedeSerCasadoException(String msg) {
		super(msg);
	}

	public HijoNoPuedeSerCasadoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public HijoNoPuedeSerCasadoException(Throwable cause) {
		super(cause);
	}
}