/**
 */

package ar.com.ospim.correspondencia;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchProductEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class NoSuchItemCorrespondenciaEntryException extends PortalException {
	
	private static final long serialVersionUID = 1L;

	public NoSuchItemCorrespondenciaEntryException() {
		super();
	}

	public NoSuchItemCorrespondenciaEntryException(String msg) {
		super(msg);
	}

	public NoSuchItemCorrespondenciaEntryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchItemCorrespondenciaEntryException(Throwable cause) {
		super(cause);
	}

}