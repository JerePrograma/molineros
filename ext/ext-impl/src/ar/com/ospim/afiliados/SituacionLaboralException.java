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
public class SituacionLaboralException extends PortalException {

	private static final long serialVersionUID = 7620499263266851873L;

	public SituacionLaboralException() {
		super();
	}

	public SituacionLaboralException(String msg) {
		super(msg);
	}

	public SituacionLaboralException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SituacionLaboralException(Throwable cause) {
		super(cause);
	}

}