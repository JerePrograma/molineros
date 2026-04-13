/**
 */

package ar.com.ospim.afiliados;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchProductEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class NoSuchAfiliadoEntryException extends PortalException {
	
	private static final long serialVersionUID = 1L;

	public NoSuchAfiliadoEntryException() {
		super();
	}

	public NoSuchAfiliadoEntryException(String msg) {
		super(msg);
	}

	public NoSuchAfiliadoEntryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchAfiliadoEntryException(Throwable cause) {
		super(cause);
	}

}