/**
 */

package ar.com.ospim.prestadores;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchContratoEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Carlos Rivas
 *
 */
public class NoSuchConvenioPrestacionalEntryException extends PortalException {

	private static final long serialVersionUID = -3659852192136705663L;

	public NoSuchConvenioPrestacionalEntryException() {
		super();
	}

	public NoSuchConvenioPrestacionalEntryException(String msg) {
		super(msg);
	}

	public NoSuchConvenioPrestacionalEntryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchConvenioPrestacionalEntryException(Throwable cause) {
		super(cause);
	}
}