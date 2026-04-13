package ar.com.ospim.webservice.actualizaCredencialPrevencion.service.http;

import ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.rmi.RemoteException;


/**
 * <a href="ActuCredenPrevencionServiceSoap.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This class provides a SOAP utility for the
 * <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionServiceUtil</code> service
 * utility. The static methods of this class calls the same methods of the
 * service utility. However, the signatures are different because it is
 * difficult for SOAP to support certain types.
 * </p>
 *
 * <p>
 * ServiceBuilder follows certain rules in translating the methods. For example,
 * if the method in the service utility returns a <code>java.util.List</code>,
 * that is translated to an array of
 * <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.model.ActuCredenPrevencionSoap</code>. If the method in the
 * service utility returns a <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.model.ActuCredenPrevencion</code>,
 * that is translated to a <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.model.ActuCredenPrevencionSoap</code>.
 * Methods that SOAP cannot safely wire are skipped.
 * </p>
 *
 * <p>
 * The benefits of using the SOAP utility is that it is cross platform
 * compatible. SOAP allows different languages like Java, .NET, C++, PHP, and
 * even Perl, to call the generated services. One drawback of SOAP is that it is
 * slow because it needs to serialize all calls into a text format (XML).
 * </p>
 *
 * <p>
 * You can see a list of services at
 * http://localhost:8080/tunnel-web/secure/axis. Set the property
 * <code>tunnel.servlet.hosts.allowed</code> in portal.properties to configure
 * security.
 * </p>
 *
 * <p>
 * The SOAP utility is only generated for remote services.
 * </p>
 *
 * @author Brian Wing Shun Chan
 *
 * @see ar.com.ospim.webservice.actualizaCredencialPrevencion.model.ActuCredenPrevencionSoap
 * @see ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionServiceUtil
 * @see ar.com.ospim.webservice.actualizaCredencialPrevencion.service.http.ActuCredenPrevencionServiceHttp
 *
 */
public class ActuCredenPrevencionServiceSoap {
    private static Log _log = LogFactoryUtil.getLog(ActuCredenPrevencionServiceSoap.class);

    public static ar.com.ospim.webservice.beans.ResultadoActualizacionCredencial actualizarCredencialBeneficiario(
        ar.com.ospim.webservice.beans.MensajeActualizacionCredencial mensaje)
        throws RemoteException {
        try {
            ar.com.ospim.webservice.beans.ResultadoActualizacionCredencial returnValue =
                ActuCredenPrevencionServiceUtil.actualizarCredencialBeneficiario(mensaje);

            return returnValue;
        } catch (Exception e) {
            _log.error(e, e);

            throw new RemoteException(e.getMessage());
        }
    }
}
