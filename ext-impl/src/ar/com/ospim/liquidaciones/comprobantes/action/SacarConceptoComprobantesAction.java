package ar.com.ospim.liquidaciones.comprobantes.action;

import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class SacarConceptoComprobantesAction extends PortletAction {
	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}		

		List<Concepto> conceptoComprobantes = TraeListasServiceUtil
				.getConceptoEgresos(renderRequest, new Date(), entidad);

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		List<ComprobanteConcepto> lista = (List<ComprobanteConcepto>) session
				.getAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);
		
		

		int id = ParamUtil.getInteger(renderRequest, "id_concepto");
		int id_seccional = ParamUtil.getInteger(renderRequest, "id_seccional");
		int id_centro = ParamUtil.getInteger(renderRequest, "id_centro");
		Concepto concepto=new Concepto(id);
		
		CentroCosto centroCosto=new CentroCosto();
		centroCosto.setId(id_centro);
		
		if(entidad==WebKeysGlobal.UOMA){
			concepto.setIdSeccional(id_seccional);	
		}
		
		int index = conceptoComprobantes.indexOf(concepto);

		Concepto cc = conceptoComprobantes.get(index);
		int indexCompConceptoComp = lista.indexOf(new ComprobanteConcepto(cc,centroCosto));
//		int indexCompConceptoComp = lista.indexOf(new ComprobanteConcepto(cc));

		ComprobanteConcepto ccc = lista.get(indexCompConceptoComp);
		if (ccc.isNuevo()) {
			lista.remove(indexCompConceptoComp);
		} else {
			ccc.setBorradoLogicamente(true);
		}

		renderRequest.setAttribute("esEdicion", "true");
		session.setAttribute(
				WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS, lista);
		if (entidad!=WebKeysGlobal.AMTIMA) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.conceptos.search.result");
		} else {
			return mapping
					.findForward("portlet.farmacia.comprobantes.conceptos.search.result");

		}
	}

}
