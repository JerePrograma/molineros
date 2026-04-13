package ar.com.ospim.liquidaciones.comprobantes.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.global.WebKeysPortal;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.action.UploadArchivoIntegracionAction;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ComprobantesConsultaGeneralAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(ComprobantesConsultaGeneralAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		

		int entidad = WebKeysGlobal.OSPIM;
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		if (renderResponse.getNamespace().equals("_FAR_1_")) {
			entidad = WebKeysGlobal.AMTIMA;
		} else if (renderResponse.getNamespace().equals("_UOM_1_")) {
			entidad = WebKeysGlobal.UOMA;
		}

		Comprobante comp = getComprobanteFromRequest(renderRequest);
		int pagina =ParamUtil.getInteger(renderRequest, "pagina",1);
		
		List<Comprobante>busqueda=ComprobanteServiceUtil.getComprobantesGlobales(comp, entidad,pagina);
		renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_COMPROBANTES_GLOBALES,busqueda);
		
		
		//Seteos Paginadaor
		Integer tRegistros=0;
		if(busqueda!=null && !busqueda.isEmpty()) {
		 tRegistros=busqueda.get(0).getTotalRegistros();
		}	
			
		session
		.removeAttribute(WebKeysLiquidaciones.CONSULTA_COMPROBANTES_GLOBAL_TOTAL_REGISTROS);
        session.removeAttribute(
		  WebKeysLiquidaciones.CONSULTA_COMPROBANTES_GLOBAL_OFFSET_REG);
		session.setAttribute(WebKeysLiquidaciones.CONSULTA_COMPROBANTES_GLOBAL_TOTAL_REGISTROS, tRegistros );
		session.setAttribute(WebKeysLiquidaciones.CONSULTA_COMPROBANTES_GLOBAL_OFFSET_REG, pagina);

		if (entidad != WebKeysGlobal.AMTIMA) {
			return mapping
					.findForward("portlet.liquidaciones.comprobantes.general.search.result");
		} else {
			return mapping
					.findForward("portlet.farmacia.comprobantes.general.search.result");
		}

	}
	
	
	public static Comprobante getComprobanteFromRequest(
			PortletRequest renderRequest) {

		int pto_venta = ParamUtil.getInteger(renderRequest, "pto_venta");
		String tipoC = ParamUtil.getString(renderRequest, "tipo_comprobante");
		String nroC = ParamUtil.getString(renderRequest, "nro_comprobante");
		String letra = ParamUtil.getString(renderRequest, "letra", " ");
		
//		String cuit = ParamUtil.getString(renderRequest, "cuit_compr_emisor");
//		int sucu = ParamUtil.getInteger(renderRequest, "sucursal", pto_venta);
		
		Comprobante comprobante = new Comprobante();
		comprobante.setPtoVenta(pto_venta);
		comprobante.setTipoComprobante(tipoC);
		comprobante.setNroComprobante(nroC);
		comprobante.setLetraComprobante(letra);

		String cuitAcreedor = renderRequest.getParameter("cuit_entidad");
		String sucuAcreedor = renderRequest.getParameter("sucursal_entidad");
//      String idSeccional = renderRequest.getParameter("id_seccional");
		
		

//		if ((StringUtils.checkNotEmpty(idSeccional) && !idSeccional.equals("0"))   
//				|| (null!=cuitAcreedor && (cuitAcreedor.equals(WebKeysGlobal.UOMA) || cuitAcreedor.equals(WebKeysGlobal.AMTIMA) || cuitAcreedor.equals(WebKeysGlobal.OSPIM)))) {
//			comprobante.setSeccional(new Seccional(Integer
//					.parseInt(idSeccional), null, cuitAcreedor));
//			sucuAcreedor = "000";
//		}

		Empresa empresa = null;
		if (StringUtils.checkNotEmpty(cuitAcreedor)) {
			empresa = new Empresa(cuitAcreedor, sucuAcreedor, null);
//			empresa.setId_seccional(Integer.parseInt(idSeccional));
		}
		
		comprobante.setAcreedorEmpresa(empresa);
		
		return comprobante;
	}

}
