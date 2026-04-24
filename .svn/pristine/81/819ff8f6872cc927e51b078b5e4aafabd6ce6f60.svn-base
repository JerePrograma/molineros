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
import ar.com.ospim.crm.services.CrmServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * @author SVA
 * 
 */
public class BuscarContactoCRMAction extends PortletAction {
	
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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	

		String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular");
		Integer inte = ParamUtil.getInteger(renderRequest, "inte");
		String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
		String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
		Integer contactoseccional = ParamUtil.getInteger(renderRequest, "contactoseccional");

		_log.debug("buscando contactos p/ " + cuilTitular + "/" + inte + " entre " + fechaDesdeFinal + " y " + fechaHastaFinal);
		
		Date fechaDesde = null;
		try {
			fechaDesde = sdf.parse(fechaDesdeFinal);
		} catch (Exception e) {
			fechaDesde = null;
		}		
		Date fechaHasta = null;
		try {
			fechaHasta = sdf.parse(fechaHastaFinal);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		List<ContactoCRM> ultimosContactos = null;

		if(cuilTitular!=null && !"".equalsIgnoreCase(cuilTitular) &&  !"undefined".equalsIgnoreCase(cuilTitular) && (contactoseccional==null || contactoseccional==0)){
		   ultimosContactos = CrmServiceUtil.buscarUltimosContactosCRM(cuilTitular, inte, fechaDesde, fechaHasta);
		} else if(contactoseccional!=null && contactoseccional!=0){
		   ultimosContactos = CrmServiceUtil.buscarUltimosContactosCRMSeccional(contactoseccional, fechaDesde, fechaHasta);	
		}
		
		renderRequest.setAttribute(WebKeysCrm.CRM_ULTIMOS_CONTACTOS, ultimosContactos);		
		
		return mapping.findForward(getForward(renderRequest,"portlet.crm.contacto.afi.result.search"));
	}

	
}
