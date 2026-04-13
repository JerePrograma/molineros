package ar.com.ospim.webservice.test.service;


/**
 * <a href="TestHolaServiceUtil.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.webservice.test.service.TestHolaService</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * @author Brian Wing Shun Chan
 *
 * @see ar.com.ospim.webservice.test.service.TestHolaService
 *
 */
public class TestHolaServiceUtil {
    private static TestHolaService _service;

    public static java.lang.String getSaludo() {
        return getService().getSaludo();
    }

    public static TestHolaService getService() {
        if (_service == null) {
            throw new RuntimeException("TestHolaService is not set");
        }

        return _service;
    }

    public void setService(TestHolaService service) {
        _service = service;
    }
}
