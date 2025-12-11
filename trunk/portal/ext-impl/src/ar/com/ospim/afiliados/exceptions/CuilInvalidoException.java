/**
 */

package ar.com.ospim.afiliados.exceptions;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchProductEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class CuilInvalidoException extends PortalException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5450643472076535083L;

	public CuilInvalidoException() {
		super();
	}

	public CuilInvalidoException(String msg) {
		super(msg);
	}

	public CuilInvalidoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CuilInvalidoException(Throwable cause) {
		super(cause);
	}

}