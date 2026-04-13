package ar.com.ospim.autorizaciones.services;

import com.liferay.portal.PortalException;

public class UserSinOrganizacionException extends PortalException {
	private static final long serialVersionUID = -3748608701156248494L;

	public UserSinOrganizacionException() {
		super();
	}

	public UserSinOrganizacionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserSinOrganizacionException(String message) {
		super(message);
	}

	public UserSinOrganizacionException(Throwable cause) {
		super(cause);
	}

}
