package ar.com.ospim.webservice.test.service.base;

import ar.com.ospim.webservice.test.service.TestHolaService;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.annotation.BeanReference;
import com.liferay.portal.service.base.PrincipalBean;
import com.liferay.portal.util.PortalUtil;


public abstract class TestHolaServiceBaseImpl extends PrincipalBean
    implements TestHolaService {
    @BeanReference(name = "ar.com.ospim.webservice.test.service.TestHolaService.impl")
    protected TestHolaService testHolaService;

    public TestHolaService getTestHolaService() {
        return testHolaService;
    }

    public void setTestHolaService(TestHolaService testHolaService) {
        this.testHolaService = testHolaService;
    }

    protected void runSQL(String sql) throws SystemException {
        try {
            PortalUtil.runSQL(sql);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
}
