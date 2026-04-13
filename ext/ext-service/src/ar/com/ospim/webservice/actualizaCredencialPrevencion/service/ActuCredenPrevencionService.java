package ar.com.ospim.webservice.actualizaCredencialPrevencion.service;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.annotation.Isolation;
import com.liferay.portal.kernel.annotation.Transactional;


/**
 * <a href="ActuCredenPrevencionService.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface defines the service. The default implementation is
 * <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.service.impl.ActuCredenPrevencionServiceImpl</code>.
 * Modify methods in that class and rerun ServiceBuilder to populate this class
 * and all other generated classes.
 * </p>
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have security checks based on the propagated JAAS credentials because this service can be accessed remotely.
 * </p>
 *
 * @author Brian Wing Shun Chan
 *
 * @see ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionServiceUtil
 *
 */
@Transactional(isolation = Isolation.PORTAL, rollbackFor =  {
    PortalException.class, SystemException.class}
)
public interface ActuCredenPrevencionService {
    public ar.com.ospim.webservice.beans.ResultadoActualizacionCredencial actualizarCredencialBeneficiario(
        ar.com.ospim.webservice.beans.MensajeActualizacionCredencial mensaje);
}
