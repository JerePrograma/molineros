package ar.com.ospim.liquidaciones.comprobantes.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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

public class AgregarConceptoComprobantesAction extends PortletAction {
	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		int entidad = WebKeysGlobal.OSPIM;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		} else if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		}

		List<Concepto> conceptoComprobantes = null;

		conceptoComprobantes = TraeListasServiceUtil.getConceptoEgresos(
				renderRequest, new Date(), entidad);

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		List<ComprobanteConcepto> lista = (List<ComprobanteConcepto>) session
				.getAttribute(WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS);

		if (lista == null) {
			lista = new ArrayList<ComprobanteConcepto>();
		}
		int id = 0;
		int sucursal = 0;
		
		Integer idCentro=0;
		String descCentro="";
		CentroCosto centro = new CentroCosto();
		idCentro = ParamUtil.getInteger(renderRequest,"id_centro");
		if(idCentro!=0){
		    descCentro = ParamUtil.getString(renderRequest,"descripcion_centro");
		}    
		centro.setId(idCentro);
		centro.setDescripcion(descCentro);
		
		if (entidad == WebKeysGlobal.UOMA) {
			String idSeccional = ParamUtil.getString(renderRequest,
					"id_concepto");
			StringTokenizer str = new StringTokenizer(idSeccional, "|");
			try {
				id = Integer.parseInt(str.nextToken());
			} catch (NumberFormatException e) {
				id = 0;
				
			}
			try {
				sucursal = Integer.parseInt(str.nextToken());
			} catch (NumberFormatException e) {				
				sucursal = 0;
			}
			
			
		} else {
			id = ParamUtil.getInteger(renderRequest, "id_concepto");
			sucursal = ParamUtil.getInteger(renderRequest, "sucursal");
		}

		String totalStr = ParamUtil
				.getString(renderRequest, "importe_concepto");
		BigDecimal importe = new BigDecimal(totalStr);
		int index = -1;
		/*
		 * if (null != cuit && cuit.equals(WebKeysGlobal.CUIT_UOMA) && sucursal
		 * > 0) {
		 */
		for (int i = 0; i < conceptoComprobantes.size(); i++) {
			Concepto c = conceptoComprobantes.get(i);
			try {
				if (c.getId() == id && c.getIdSeccional() == sucursal) {
					index = i;
					break;
				}
			} catch (Exception e) {

			}
		}
		/*
		 * } else { index = conceptoComprobantes.indexOf(new Concepto(id)); }
		 */
		if (index < 0) {
			index = conceptoComprobantes.indexOf(new Concepto(id));
		}

		Concepto cc = conceptoComprobantes.get(index);

//		int indexOfCCC = lista.indexOf(new ComprobanteConcepto(cc));
		
		int indexOfCCC = lista.indexOf(new ComprobanteConcepto(cc,centro));
		if (indexOfCCC == -1) {
			lista.add(new ComprobanteConcepto(cc, importe,centro));
			
//			lista.add(new ComprobanteConcepto(cc, importe));
		} else {
			ComprobanteConcepto comprobanteConceptoComprobante = lista
					.get(indexOfCCC);
			
			BigDecimal xAux = comprobanteConceptoComprobante.isBorradoLogicamente()?importe:comprobanteConceptoComprobante.getImporte().add(importe);
			comprobanteConceptoComprobante.setImporte(xAux);
//			comprobanteConceptoComprobante.setImporte(comprobanteConceptoComprobante.getImporte().add(importe));
			comprobanteConceptoComprobante.setBorradoLogicamente(false);
			comprobanteConceptoComprobante.setCentroCosto(centro);
/*			
			comprobanteConceptoComprobante.setImporte(importe);
			comprobanteConceptoComprobante.setBorradoLogicamente(false);
			lista.add(new ComprobanteConcepto(cc, importe));
*/			
		}
		renderRequest.setAttribute("esEdicion", "true");
		session.setAttribute(
				WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS, lista);

		if (entidad != WebKeysGlobal.AMTIMA) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.conceptos.search.result");
		} else {
			return mapping
					.findForward("portlet.farmacia.comprobantes.conceptos.search.result");
		}
	}
}
