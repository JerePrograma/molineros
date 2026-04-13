package ar.com.ospim.webservice.actualizaCredencialPrevencion.service.base;

import ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionService;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.annotation.BeanReference;
import com.liferay.portal.service.base.PrincipalBean;
import com.liferay.portal.util.PortalUtil;


public abstract class ActuCredenPrevencionServiceBaseImpl extends PrincipalBean
    implements ActuCredenPrevencionService {
    @BeanReference(name = "ar.com.ospim.webservice.actualizaCredencialPrevencion.service.ActuCredenPrevencionService.impl")
    protected ActuCredenPrevencionService actuCredenPrevencionService;

    public ActuCredenPrevencionService getActuCredenPrevencionService() {
        return actuCredenPrevencionService;
    }

    public void setActuCredenPrevencionService(
        ActuCredenPrevencionService actuCredenPrevencionService) {
        this.actuCredenPrevencionService = actuCredenPrevencionService;
    }

    protected void runSQL(String sql) throws SystemException {
        try {
            PortalUtil.runSQL(sql);
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }
}
