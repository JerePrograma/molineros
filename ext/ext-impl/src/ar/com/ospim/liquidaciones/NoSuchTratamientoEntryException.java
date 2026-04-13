/**
 */

package ar.com.ospim.liquidaciones;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchTratamientoEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Carlos Rivas
 *
 */
public class NoSuchTratamientoEntryException extends PortalException {

	private static final long serialVersionUID = -4397446068937578643L;

	public NoSuchTratamientoEntryException() {
		super();
	}

	public NoSuchTratamientoEntryException(String msg) {
		super(msg);
	}

	public NoSuchTratamientoEntryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchTratamientoEntryException(Throwable cause) {
		super(cause);
	}

}