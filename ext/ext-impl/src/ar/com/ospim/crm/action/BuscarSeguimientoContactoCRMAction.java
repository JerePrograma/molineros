package ar.com.ospim.crm.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.DerivacionSeguimiento;
import ar.com.ospim.crm.services.CrmServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * @author SVA
 * 
 */
public class BuscarSeguimientoContactoCRMAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
// redirige al render
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

	// preferi no hacer nada x el processAction...
//			System.out.println("pasando x el processAction");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		Integer idContacto = ParamUtil.getInteger(renderRequest, "id_contacto");

		_log.debug("buscando seguimientos para contacto crm nro: " + idContacto);
		
		List<DerivacionSeguimiento> seguimientosDerivacion = null;

		seguimientosDerivacion = CrmServiceUtil.buscarSeguimientoContactoCRMbyIdContacto(idContacto) ;
		
		renderRequest.setAttribute(WebKeysCrm.CRM_DERIVACIONES_CONTACTO, seguimientosDerivacion);		
		
		return mapping.findForward(getForward(renderRequest,"portlet.seguimiento.crm.contacto.result.search"));
	}

	
}
