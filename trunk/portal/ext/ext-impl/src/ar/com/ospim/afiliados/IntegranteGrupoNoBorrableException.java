package ar.com.ospim.afiliados;

import com.liferay.portal.PortalException;

public class IntegranteGrupoNoBorrableException extends PortalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IntegranteGrupoNoBorrableException() {
		super();
	}

	public IntegranteGrupoNoBorrableException(String msg) {
		super(msg);
	}

	public IntegranteGrupoNoBorrableException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public IntegranteGrupoNoBorrableException(Throwable cause) {
		super(cause);
	}
}
