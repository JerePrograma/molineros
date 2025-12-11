package ar.com.ospim.webservice.actualizaCredencialPrevencion.service;


/**
 * <a href="ActuCredenPrevencionServiceUtil.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionService</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * @author Brian Wing Shun Chan
 *
 * @see ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionService
 *
 */
public class ActuCredenPrevencionServiceUtil {
    private static ActuCredenPrevencionService _service;

    public static ar.com.ospim.webservice.beans.ResultadoActualizacionCredencial actualizarCredencialBeneficiario(
        ar.com.ospim.webservice.beans.MensajeActualizacionCredencial mensaje) {
        return getService().actualizarCredencialBeneficiario(mensaje);
    }

    public static ActuCredenPrevencionService getService() {
        if (_service == null) {
            throw new RuntimeException("ActuCredenPrevencionService is not set");
        }

        return _service;
    }

    public void setService(ActuCredenPrevencionService service) {
        _service = service;
    }
}
