package ar.com.ospim.crm.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.BusquedaDocumLegalFiltro;
import ar.com.ospim.crm.beans.DocumentoLegalCRMTotal;
import ar.com.ospim.crm.services.CrmServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarCorrespondenciaInboxAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de reclamos con filtro
 * 
 * @author SVA
 * 
 */
public class BusquedaDocumLegalCRMAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BusquedaDocumLegalCRMAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
//		User user = PortalUtil.getUser(renderRequest);
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		try {
			int motivo = ParamUtil.getInteger(renderRequest, "motivo",0);
			int tipoReclamo = ParamUtil.getInteger(renderRequest, "tipoReclamo",0);
			String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular",null);
			String inte = ParamUtil.getString(renderRequest, "inte",null);
			int incluirA = ParamUtil.getInteger(renderRequest, "incluirA", 0);
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
			int idDocumLegal = ParamUtil.getInteger(renderRequest, "nro_doc_legal",0);
			int idPlan = ParamUtil.getInteger(renderRequest, "plan",0);
			int idPlanOmint = ParamUtil.getInteger(renderRequest, "planOmint",0);
			boolean tieneAntec = ParamUtil.getBoolean(renderRequest, "antecedente");
			boolean concluido = ParamUtil.getBoolean(renderRequest, "concluido");
			boolean noconcluido = ParamUtil.getBoolean(renderRequest, "noconcluido");
			
			BusquedaDocumLegalFiltro filtro = (BusquedaDocumLegalFiltro) 
											session.getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL);
			if(filtro == null){
				filtro = new BusquedaDocumLegalFiltro();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

			int registrosTotalBusqueda = 0;
//			String llamada = ParamUtil.getString(renderRequest, "viene_de", "");
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
		
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setPagina(pagina_sel);
			filtro.setCuil_titular(cuilTitular);
			filtro.setInte(inte);
			filtro.setIncluirA(incluirA);
			filtro.setMotivo(motivo);
			filtro.setTipoReclamo(tipoReclamo);
			filtro.setIdDocumLegal(idDocumLegal);
			filtro.setIdPlan(idPlan);
			filtro.setIdPlanOmint(idPlanOmint);
			filtro.setAntecedente(tieneAntec);
			filtro.setConcluido(concluido);
			filtro.setNoConcluido(noconcluido);
			
			List<DocumentoLegalCRMTotal> busqueda = CrmServiceUtil.busquedaReclamosCRM(filtro, pagina_sel);
			
			session.removeAttribute(WebKeysCrm.BUSQUEDA_DOC_LEGAL_RESULT);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_TOTAL_REGISTROS);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_OFFSET_REG);
			
			if(busqueda != null && busqueda.size() > 0){ 
				registrosTotalBusqueda = busqueda.get(0).getTotalRegistros();
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_TOTAL_REGISTROS, registrosTotalBusqueda);
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_OFFSET_REG , pagina_sel);
			}else{
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_TOTAL_REGISTROS, 0);
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL_OFFSET_REG , 0);
			}
			session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_DOC_LEGAL, filtro);
			session.setAttribute(WebKeysCrm.BUSQUEDA_DOC_LEGAL_RESULT, busqueda);
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.crm.reclamos.result.search");

	}

}