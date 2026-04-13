/**
 */

package ar.com.ospim.liquidaciones;

import com.liferay.portal.PortalException;

/**
 * <a href="NoSuchLiquidacionEntryException.java.html"><b><i>View Source</i></b></a>
 *
 * @author Carlos Rivas
 *
 */
public class  NoSuchLiquidacionEntryException extends PortalException {

	private static final long serialVersionUID = 3907899183739854736L;

	public NoSuchLiquidacionEntryException() {
		super();
	}

	public NoSuchLiquidacionEntryException(String msg) {
		super(msg);
	}

	public NoSuchLiquidacionEntryException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchLiquidacionEntryException(Throwable cause) {
		super(cause);
	}

}