/**
 */

package ar.com.ospim.autorizaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AgregaComprobanteLiquidadoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(AgregaComprobanteLiquidadoAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.autorizaciones.comprobantesliquidados.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			Formatter fmt = new Formatter();
			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
			Integer ptoVta = ParamUtil.getInteger(renderRequest,"ptoventa");
			String tipo = ParamUtil
					.getString(renderRequest, "tipocomprobante", null);
			Integer nro = ParamUtil
					.getInteger(renderRequest, "nrocomprobante", 0);
			String nroS= fmt.format("%08d",nro).toString() ;
			if("00000000".equalsIgnoreCase(nroS)) nroS=null;
			
			
			String cuit = ParamUtil
					.getString(renderRequest, "cuit", null);
			Integer idPrestador=ParamUtil.getInteger(renderRequest, "idprestador");
			
			Date fechaEmision =null;
			try{		
				int mesEmision=ParamUtil.getInteger(renderRequest, "fechaEmisionComprobanteMes", 0)+1;
				fechaEmision=formatoDeFechas.parse( ParamUtil.getString(renderRequest, "fechaEmisionComprobanteDia", null) +"/"+
						                             mesEmision+"/"+
						                            ParamUtil.getString(renderRequest, "fechaEmisionComprobanteAnio", null) +"/");
			}catch(Exception e){}		
			
			
			Date fechaRecibido =null;
			try{		
				int mesRecepcion=ParamUtil.getInteger(renderRequest, "fechaRecepcionComprobanteMes", 0)+1;
				fechaRecibido=formatoDeFechas.parse( ParamUtil.getString(renderRequest, "fechaRecepcionComprobanteDia", null) +"/"+
						                            mesRecepcion +"/"+
						                            ParamUtil.getString(renderRequest, "fechaRecepcionComprobanteAnio", null) +"/");
			}catch(Exception e){}
					
			Date fechaVencimiento =null;
			try{	
				int mesVencimiento=ParamUtil.getInteger(renderRequest, "fechaVencimientoComprobanteMes", 0)+1;
				fechaVencimiento=formatoDeFechas.parse( ParamUtil.getString(renderRequest, "fechaVencimientoComprobanteDia", null) +"/"+
						                           mesVencimiento+"/"+
						                            ParamUtil.getString(renderRequest, "fechaVencimientoComprobanteAnio", null) +"/");
			}catch(Exception e){}
			
			String razonSocial = ParamUtil
					.getString(renderRequest, "razonsocial", null);
			String letra = ParamUtil
					.getString(renderRequest, "letracomprobante", null);
			
			Double importe = ParamUtil.getDouble(renderRequest, "importe");

			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			
			SeguimientoSur seguimiento = (SeguimientoSur) session.getAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION);
			SeguimientoSurComprobante comprobante= new SeguimientoSurComprobante();
			
			comprobante.setCuit(cuit);
			Empresa empresa = new Empresa(cuit,idPrestador.toString(),razonSocial);
			comprobante.setAcreedorEmpresa(empresa);
			comprobante.setTipoComprobante(tipo);
			comprobante.setLetraComprobante(letra);
			comprobante.setPtoVenta(ptoVta);
			comprobante.setSucuComprobante(ptoVta);
			comprobante.setNroComprobante(nroS);
			comprobante.setFechaEmision(fechaEmision);
			comprobante.setFechaRecepcion(fechaRecibido);
			comprobante.setFechaVencimiento(fechaVencimiento);
			comprobante.setImporteComprobante(BigDecimal.valueOf(importe)); 
			
			
			Boolean existe=false;
			for(SeguimientoSurComprobante c:seguimiento.getComprobantes()){
				
					if(comprobante.getCuit().equalsIgnoreCase(c.getCuit()) &&
					   comprobante.getLetraComprobante().equalsIgnoreCase(c.getLetraComprobante()) &&
					   comprobante.getTipoComprobante().equalsIgnoreCase(c.getTipoComprobante()) &&
					   comprobante.getPtoVenta() == c.getPtoVenta() &&
					   comprobante.getSucuComprobante() == c.getSucuComprobante() &&
					   comprobante.getNroComprobante().equalsIgnoreCase(c.getNroComprobante())
					  ){
					   existe=true;
					   break;
			        }
			}
			if(!existe){
				seguimiento.getComprobantes().add(comprobante) ;
			}
			session.setAttribute(WebKeysAutorizaciones.SEGUIMIENTO_EN_EDICION, seguimiento);
						
		} catch (Exception e) {
			_log.error(e);
		}
		
		return mapping
				.findForward("portlet.autorizaciones.comprobantesliquidados.result.search");
		
	}
}