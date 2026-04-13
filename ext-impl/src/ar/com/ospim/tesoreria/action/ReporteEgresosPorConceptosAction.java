package ar.com.ospim.tesoreria.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.struts.PortletAction;

public class ReporteEgresosPorConceptosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		int entidad=WebKeysGlobal.OSPIM;
		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		
		List<Concepto> conceptosEgresoValidosDentroDe = TraeListasServiceUtil
				.getConceptosEgresoValidosDentroDe(DateUtils
						.getDesdeEjercicioActual().getTime(), DateUtils
						.getHastaEjercicioActual().getTime(), entidad);
		renderRequest.setAttribute("ConceptosEgresoTotales",
				conceptosEgresoValidosDentroDe);
		return mapping
				.findForward("portlet.tesoreria.reporte.comprobantes.conceptos");
	}
}
