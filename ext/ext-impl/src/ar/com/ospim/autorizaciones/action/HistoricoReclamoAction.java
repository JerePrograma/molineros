package ar.com.ospim.autorizaciones.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.autorizaciones.beans.MovimientoReclamoHistorico;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class HistoricoReclamoAction extends PortletAction {

    @Override
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
    		RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
    	
        int idReclamo = ParamUtil.getInteger(renderRequest, "idReclamo");

        try {
            List<MovimientoReclamoHistorico> historico = null;

            if (idReclamo > 0) {
                historico = ReclamosPrestacionesServiceUtil.buscarHistoricoReclamo(idReclamo);
            }
            renderRequest.setAttribute(WebKeysAutorizaciones.HISTORICO_RECLAMO, historico);

        } catch (Exception e) {
            setForward(renderRequest, "portlet.autorizaciones.error");
        }

        return mapping.findForward(
            getForward(renderRequest, "portlet.autorizaciones.historico.reclamo.result.search"));
    }
}