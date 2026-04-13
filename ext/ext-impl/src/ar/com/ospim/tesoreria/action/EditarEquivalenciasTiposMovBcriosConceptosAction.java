package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarEquivalenciasTiposMovBcriosConceptosAction extends
		PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest req,
			ActionResponse actionResponse) throws Exception {
		try {
			
			PortletSession portletSession = req.getPortletSession();
			
			int entidad=WebKeysGlobal.OSPIM;
			
			if(actionResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(actionResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}
			
			String ddOriginal = req.getParameter("ejercicio_desde_original");
			String dd = req.getParameter("ejercicio_desde");
			String hta = req.getParameter("ejercicio_hasta");
			if (StringUtils.isBlank(dd) || StringUtils.isBlank(hta)
					&& !StringUtils.isBlank(req.getParameter("ejercicio"))) {
				String ejercicio = req.getParameter("ejercicio");
				portletSession.removeAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
				portletSession.setAttribute("ejercicio_seleccionado", ejercicio, PortletSession.PORTLET_SCOPE);
				if(entidad==WebKeysGlobal.AMTIMA){
					dd = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
					hta = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);					
				}else{
					dd = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
					hta = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);	
				}
				
				ddOriginal = dd;
			}
			req.setAttribute("ejercicio_desde", dd);
			req.setAttribute("ejercicio_hasta", hta);
			req.setAttribute("ejercicio_desde_original", ddOriginal);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Date desdeOriginal = format.parse(ddOriginal);
			TipoMovBcrio tipo = new TipoMovBcrio();

			tipo.setValidoDesde(format.parse(dd));
			if(entidad==WebKeysGlobal.AMTIMA){
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActualAmtima().getTime()) == 0) {
					tipo.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					tipo.setValidoHasta(format.parse(hta));
				}
				
			}else if(entidad==WebKeysGlobal.UOMA){
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActualUOMA().getTime()) == 0) {
					tipo.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					tipo.setValidoHasta(format.parse(hta));
				}				
			}else if(entidad==WebKeysGlobal.OSPIM){
				if (DateUtils.compararFechasTruncarEnDia(format.parse(dd),
						DateUtils.getDesdeEjercicioActual().getTime()) == 0) {
					tipo.setValidoHasta(DateUtils.getInfinito().getTime());
				} else {
					tipo.setValidoHasta(format.parse(hta));
				}				
			}
			
			tipo.setId_tipo_mov(Integer.parseInt(req.getParameter("id")));
			tipo.setDescripcion(req.getParameter("concepto"));
			if (!req.getParameter("id_concepto").trim().equals("-1")) {
				tipo.setConcepto(new Concepto(Integer.parseInt(req
						.getParameter("id_concepto"))));
			}

			User user = PortalUtil.getUser(req);
			if (tipo.getId_tipo_mov() != 0) {
				ConceptoServiceUtil.update(tipo, desdeOriginal, user, entidad);
			} else {
				ConceptoServiceUtil.guardar(tipo, user, entidad);
			}
			req.setAttribute("ejercicio_desde_original", dd);
			req.getPortletSession().removeAttribute(
					WebKeysLiquidaciones.CONCEPTOS_EGRESOS,
					PortletSession.APPLICATION_SCOPE);
			req.getPortletSession().removeAttribute(
					WebKeysLiquidaciones.CONCEPTOS_INGRESO,
					PortletSession.APPLICATION_SCOPE);
			req.setAttribute("id", tipo.getId_tipo_mov());
		} catch (Exception e) {
			SessionErrors.add(req, e.getClass().getName());
		}

		if (SessionErrors.isEmpty(req)) {
			String successMessage = ParamUtil.getString(req, "successMessage");
			SessionMessages.add(req, "request_processed", successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}

		String ddOriginal = renderRequest
				.getParameter("ejercicio_desde_original");
		String dd = renderRequest.getParameter("ejercicio_desde");
		String hta = renderRequest.getParameter("ejercicio_hasta");
		if (renderRequest.getAttribute("ejercicio_desde_original") != null) {
			ddOriginal = (String) renderRequest
					.getAttribute("ejercicio_desde_original");
		}
		if (renderRequest.getAttribute("ejercicio_desde") != null) {
			dd = (String) renderRequest.getAttribute("ejercicio_desde");
		}
		if (renderRequest.getAttribute("ejercicio_hasta") != null) {
			hta = (String) renderRequest.getAttribute("ejercicio_hasta");
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if(entidad==WebKeysGlobal.AMTIMA){
			if (StringUtils.isBlank(dd)) {
				dd = format.format(DateUtils.getDesdeEjercicioActualAmtima().getTime());
			}
			if (StringUtils.isBlank(hta)) {
				hta = format.format(DateUtils.getHastaEjercicioActualAmtima().getTime());
			}			
		}else{
			if (StringUtils.isBlank(dd)) {
				dd = format.format(DateUtils.getDesdeEjercicioActual().getTime());
			}
			if (StringUtils.isBlank(hta)) {
				hta = format.format(DateUtils.getHastaEjercicioActual().getTime());
			}			
		}
		
		Date desde = format.parse(dd);
		Date hasta = format.parse(hta);
		renderRequest.setAttribute("ejercicio_desde", dd);
		renderRequest.setAttribute("ejercicio_hasta", hta);
		renderRequest.setAttribute("ejercicio_desde_original", ddOriginal);

		List<Concepto> conceptos = TraeListasServiceUtil.getConceptos(desde, entidad);
		renderRequest.setAttribute("conceptos", conceptos);

		String id = renderRequest.getParameter("id");
		if (renderRequest.getAttribute("id") != null) {
			id = ((Integer) renderRequest.getAttribute("id")).toString();
		}
		if (id != null) {
			List<TipoMovBcrio> tiposMov = TraeListasServiceUtil
					.getTipoMovBcrio(format.parse(ddOriginal), hasta, entidad);
			TipoMovBcrio tipo = tiposMov.get(tiposMov.indexOf(new TipoMovBcrio(
					Integer.parseInt(id))));
			renderRequest.setAttribute("tipoMovBcrio", tipo);
		} else {
			renderRequest.setAttribute("tipoMovBcrio", new TipoMovBcrio());
		}

		return mapping
				.findForward(getForward(renderRequest,
						"portlet.tesoreria.equivalencia.editar_equivalencias_mov_bcrios"));

	}

}
