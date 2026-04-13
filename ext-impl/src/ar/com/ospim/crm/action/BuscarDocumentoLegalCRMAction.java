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
import ar.com.ospim.crm.beans.DocumentoLegalCRM;
import ar.com.ospim.crm.services.CrmServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * @author SVA
 * 
 */
public class BuscarDocumentoLegalCRMAction extends PortletAction {
	
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

		_log.debug("buscando reclamos p/ " + cuilTitular + "/" + inte + " entre " + fechaDesdeFinal + " y " + fechaHastaFinal);
		
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
		
		List<DocumentoLegalCRM> ultimosReclamos = null;

		ultimosReclamos = CrmServiceUtil.buscarUltimosReclamosCRM(cuilTitular, inte, fechaDesde, fechaHasta);
		
		renderRequest.setAttribute(WebKeysCrm.CRM_ULTIMOS_DOCUM_LEGAL, ultimosReclamos);		
		
		return mapping.findForward(getForward(renderRequest,"portlet.crm.reclamo.afi.result.search"));
	}

	
}
