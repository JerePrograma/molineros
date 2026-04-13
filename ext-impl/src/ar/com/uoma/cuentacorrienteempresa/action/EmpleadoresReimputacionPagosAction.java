package ar.com.uoma.cuentacorrienteempresa.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.uoma.cuentacorrienteempresa.services.EmpleadoresReimputacionServiceUtil;

public class EmpleadoresReimputacionPagosAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String portlet_name = null;
		if (renderResponse.getNamespace().equals("_UOM_1_")) {
			portlet_name = "uoma";
		}
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		
		String msg = "";
		String cmd=ParamUtil.getString(renderRequest,"cmd", "");		
		String cuit=ParamUtil.getString(renderRequest,"cuit_entidad",null);
		String sucursal=ParamUtil.getString(renderRequest,"suc_entidad",null);
		String fechaDesdeMes=ParamUtil.getString(renderRequest,"desde_mes",null);
		String fechaDesdeAnio=ParamUtil.getString(renderRequest,"desde_anio",null);
		String fechaHastaMes=ParamUtil.getString(renderRequest,"hasta_mes",null);
		String fechaHastaAnio=ParamUtil.getString(renderRequest,"hasta_anio",null);
		String periodoDDJJ=ParamUtil.getString(renderRequest,"periodo",null);
		Integer nroBoleta=ParamUtil.getInteger(renderRequest,"nro_boleta");
		String nroMovimiento=ParamUtil.getString(renderRequest,"nro_movimiento",null);
		
		Date fechaDesde= null;
		try {
			fechaDesde = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaDesdeMes) ) + 1)  + "/" + fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}

		Date fechaHasta= null;
		try {
			fechaHasta = formatoDeFecha.parse("01" + "/" + ((Integer.parseInt(fechaHastaMes) ) + 1)  + "/" + fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		Date periodo= null;
		try {
			String[] periodoDesdeSplit = null;
			if (periodoDDJJ.length() > 0) {
				periodoDesdeSplit = periodoDDJJ.split("_");
			}
			periodo = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeSplit[0])
					+ 1 + "/" + periodoDesdeSplit[1]);
		} catch (Exception e) {
			periodo = null;
		}
		
		
		
		if(cmd!=null && !"".equals(cmd)) {
			if("filter".equals(cmd)) {
				List<FichaBoletaPortal> fichas = new ArrayList<FichaBoletaPortal>();
				List<FichaBoletaPortal> fichasAux = new ArrayList<FichaBoletaPortal>();
				fichasAux = PortalEmpleadoresServiceUtil.getReporteBoletaPortal(
						periodo, periodo, null, null,
						null, fechaDesde, fechaHasta, null,
						0, 0, null, cuit,
						null, null, 0);
				for(FichaBoletaPortal f:fichasAux) {
					if((f.getNro_cheque()!=null && "LIBERADO".equals(f.getEstado_cheque())) ||
							(f.getNro_cheque()==null || f.getNro_cheque().equals(BigDecimal.ZERO))) {
						fichas.add(f);
					}
				}
				
				if(nroBoleta!=null && nroBoleta>0 && fichas.size()>0) {
					FichaBoletaPortal ficha = new FichaBoletaPortal();
					for(FichaBoletaPortal f:fichas) {
						if(f.getNro_boleta_portal_emple()==nroBoleta) {
						   	ficha=f;
						   	break;
						}
					}
					fichas.clear();
					if(ficha.getNro_boleta_portal_emple()>0) {
					   fichas.add(ficha);
					}
				}
				Empresa empleador = EmpresaServiceUtil.getEmpleadorCompleto(cuit,"000");
				session.setAttribute("BOLETAS_EMPLEADOR", fichas);
				session.setAttribute("BOLETA_EMPLEADORES_EMPRESA", empleador);
				return mapping.findForward(getForward(renderRequest, 
			     		   "portlet.uoma.empleadores_reimputacion_pagos_search_result"));
			}else if("edit".equals(cmd)) {
				Integer tipo=ParamUtil.getInteger(renderRequest,"tipo_boleta");
				cuit=ParamUtil.getString(renderRequest,"cuit",null);
				FichaBoletaPortal ficha = PortalEmpleadoresServiceUtil.getBoletaCobranzaByCuitNroBoleta(cuit, nroBoleta,nroMovimiento);
				session.setAttribute("BOLETA_EMPLEADORES_REIMPUTAR", ficha);
				session.setAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA", new FichaBoletaPortal());
				session.setAttribute("BOLETA_EMPLEADORES_IMPAGAS", new ArrayList<FichaBoletaPortal>());
				return mapping.findForward(getForward(renderRequest, 
			     		   "portlet.uoma.empleadores_reimputacion_pagos_edit"));
			}else if("impagas".equals(cmd)) {
				 session.removeAttribute("BOLETA_EMPLEADORES_IMPAGAS");
				 cuit=ParamUtil.getString(renderRequest,"cuit",null);
				 String accion=ParamUtil.getString(renderRequest,"accion",null);
				 if("V".equals(accion)) {
				     List<FichaBoletaPortal>listI =PortalEmpleadoresServiceUtil.getBoletasImpagas(cuit, sucursal, null, 20);;
				     if(!listI.isEmpty()){
				        session.setAttribute("BOLETA_EMPLEADORES_IMPAGAS", listI);
				     } 
				 }
				 return mapping.findForward(getForward(renderRequest, 
			     		   "portlet.uoma.empleadores_reimputacion_pagos_impagas"));
			}else if("traerImpaga".equals(cmd)) {
				 session.removeAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA");
				 session.removeAttribute("Errores");
				 cuit=ParamUtil.getString(renderRequest,"cuit",null);
				 FichaBoletaPortal ficha = PortalEmpleadoresServiceUtil.getBoletaCobranzaByCuitNroBoleta(cuit, nroBoleta);
				 List<String> errores = new ArrayList<String>();
				 if(ficha.getImporte()!=null && !ficha.getImporte().equals(BigDecimal.ZERO)) {
					 errores.add("La boleta seleccionada se encuentra pagada");
					 session.setAttribute("Errores", errores);
					 
				 }else {
				 
				    session.setAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA", ficha);
				 }   
				 return mapping.findForward(getForward(renderRequest, 
			     		   "portlet.uoma.empleadores_reimputacion_pagos_edit_destino"));
			}else if("add".equals(cmd)) {
				boolean tieneAjuste=ParamUtil.getBoolean(renderRequest, "ajusteChk");
				String fechaAjusteDia = ParamUtil.getString(renderRequest,"fechaAjusteDia");
				String fechaAjusteMes = ParamUtil.getString(renderRequest,"fechaAjusteMes");
				String fechaAjusteAnio = ParamUtil.getString(renderRequest,"fechaAjusteAnio");
				Integer tipoAjuste = ParamUtil.getInteger(renderRequest,"tipoAporteAjuste",0);
				String importeAjuste = ParamUtil.getString(renderRequest, "importe_ajuste");
				Date fechaAjuste = null;
				try {
					fechaAjuste= formatoDeFechas.parse(fechaAjusteDia + "/"
							+ (Integer.parseInt(fechaAjusteMes) + 1) + "/"
							+ fechaAjusteAnio);
				} catch (Exception e) {
					fechaAjuste = null;
				}
				FichaBoletaPortal ajuste = new FichaBoletaPortal();
				if(tieneAjuste) {
					importeAjuste =importeAjuste.replace(",",".");
					if(Double.parseDouble(importeAjuste)!=0D) {
					   ajuste.setFecha_recauda(fechaAjuste);
					   ajuste.setTipoBoleta(tipoAjuste);
					   ajuste.setImporte(new BigDecimal(importeAjuste).setScale(2, RoundingMode.HALF_UP));
					}   
				}
				FichaBoletaPortal pagada = (FichaBoletaPortal) session.getAttribute("BOLETA_EMPLEADORES_REIMPUTAR");
				FichaBoletaPortal impaga = (FichaBoletaPortal) session.getAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA");
				EmpleadoresReimputacionServiceUtil.updatePago(pagada.getCuit(), pagada,impaga,ajuste,user);
				msg = "Se efectuó reimputacion del pago";
				SessionMessages.add(renderRequest, "insertCabOk");
				renderRequest.setAttribute("msgCabOk", msg);
				session.setAttribute("BOLETAS_EMPLEADOR", new ArrayList<FichaBoletaPortal>());
				session.setAttribute("BOLETA_EMPLEADORES_REIMPUTAR",new FichaBoletaPortal());
				session.setAttribute("BOLETA_EMPLEADORES_BOLETA_IMPAGA",new FichaBoletaPortal());
				return mapping.findForward(getForward(renderRequest, 
			     		   "portlet.uoma.empleadores_reimputacion_pagos_edit"));
			}
		}
		
		return mapping.findForward(getForward(renderRequest, 
     		   "portlet.uoma.empleadores_reimputacion_pagos_search_result"));
	}	
	
}