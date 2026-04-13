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
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregarConceptoComprobantesExtendidoAction extends PortletAction {
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
				
			}catch (Exception e) {
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
		
		String gravadoStr=ParamUtil.getString(renderRequest, "gravado");
		BigDecimal gravado = new BigDecimal("".equalsIgnoreCase(gravadoStr)?"0":gravadoStr);
		Double tasaIva=ParamUtil.getDouble(renderRequest,"tasa_iva");
		String ivaStr=ParamUtil.getString(renderRequest, "importe_iva");
		BigDecimal iva=new BigDecimal("".equalsIgnoreCase(ivaStr)?"0":ivaStr);
		BigDecimal exento=BigDecimal.ZERO;
		
		if(gravado.compareTo(BigDecimal.ZERO)>0 && tasaIva==0) {
			exento=gravado;
			gravado=BigDecimal.ZERO;
		}
		
		String percepIvaStr=ParamUtil.getString(renderRequest, "importe_percep_iva");
		BigDecimal percepIva=BigDecimal.ZERO;
		if(percepIvaStr !=null && !"".equalsIgnoreCase(percepIvaStr)) {
			percepIva=new BigDecimal(percepIvaStr);
		}   
		
		String percepIIBBStr=ParamUtil.getString(renderRequest, "importe_percep_iibb");
		BigDecimal percepIIBB=BigDecimal.ZERO;
		if(percepIIBBStr !=null && !"".equalsIgnoreCase(percepIIBBStr)) {
		   percepIIBB=new BigDecimal(percepIIBBStr);
		}   
		
		Integer jurisd=ParamUtil.getInteger(renderRequest,"jurisdiccion_iibb");
		String otrosTributosStr=ParamUtil.getString(renderRequest,"importe_otros_tributos");
		BigDecimal otrosTributos=BigDecimal.ZERO;
		if(otrosTributosStr !=null && !"".equalsIgnoreCase(otrosTributosStr)) {
		   otrosTributos=new BigDecimal(otrosTributosStr);
		}
		
		BigDecimal retenciones=BigDecimal.ZERO;
		int index = -1;
		
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
		
		
		if (index < 0) {
			index = conceptoComprobantes.indexOf(new Concepto(id));
		}

		Concepto cc = conceptoComprobantes.get(index);

		
		int indexOfCCC = lista.indexOf(new ComprobanteConcepto(cc,centro,tasaIva));
		if (indexOfCCC == -1) {
			lista.add(new ComprobanteConcepto(cc, importe,  centro,
					gravado, iva, tasaIva,  exento,  percepIva,
					percepIIBB, retenciones, otrosTributos, jurisd) );
		} else {
           lista.remove(indexOfCCC);
           lista.add(new ComprobanteConcepto(cc, importe,  centro,
					gravado, iva, tasaIva,  exento,  percepIva,
					percepIIBB, retenciones, otrosTributos, jurisd) );
			
			
//			ComprobanteConcepto comprobanteConceptoComprobante = lista.get(indexOfCCC);
//			BigDecimal xAux = comprobanteConceptoComprobante.isBorradoLogicamente()?importe:comprobanteConceptoComprobante.getImporte().add(importe);
//			comprobanteConceptoComprobante.setImporte(xAux);
//			comprobanteConceptoComprobante.setBorradoLogicamente(false);
//			comprobanteConceptoComprobante.setCentroCosto(centro);
			
		}
		renderRequest.setAttribute("esEdicion", "true");
		session.setAttribute(
				WebKeysLiquidaciones.COMPROBANTE_CONCEPTOS_AGREGADOS, lista);
		
		

		if (entidad == WebKeysGlobal.OSPIM) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.conceptos.search.result");
		}else if(entidad == WebKeysGlobal.UOMA) { 
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.conceptos.extendido.search.result");
	    }else {
			return mapping
					.findForward("portlet.farmacia.comprobantes.conceptos.search.result");
		}
	}
}
