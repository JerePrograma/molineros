package ar.com.ospim.tesoreria.recibos.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;

public class ABMReciboOtrosConceptosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboOtrosConceptosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

// Busqueda de boleta en portal empleadores		
		String empleadores = renderRequest.getParameter("empleadores");
		if (empleadores != null && empleadores.equals("empleadores")) {
			buscarEmpleadores(renderRequest,session);
			return mapping
					.findForward("portlet.tesoreria.recibos.empleadores_aportes.result.search");
		}

		
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
		}

		if (recibo.getOtrosConceptos() == null) {
			recibo.setOtrosConceptos(new ArrayList<ReciboOtroConcepto>());
		}

		String borrar = renderRequest.getParameter("borrar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarConcepto(renderRequest, recibo);
		} else {
			agregarConcepto(renderRequest, recibo);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		return mapping
				.findForward("portlet.tesoreria.recibos.otros_conceptos.result.search");
	}

	private void agregarConcepto(RenderRequest renderRequest, Recibo recibo) {

		Date fechaPeriodo = null;
		int conceptoId = 0;
		String comproNro = "";
		
		comproNro = ParamUtil.getString(renderRequest, "concepto_id");
		try{
			conceptoId = Integer.parseInt(comproNro);
		}catch(NumberFormatException e){
			conceptoId=Integer.parseInt(comproNro.substring(0, comproNro.indexOf("_")));
			comproNro=comproNro.substring(comproNro.indexOf("_")+1,comproNro.length()-1);
		}
		
		String nroBoleta = renderRequest.getParameter("nroBoleta");
		String nroSecuenciaDDJJ = renderRequest.getParameter("nroSecuenciaDDJJ");
		
		String importe = renderRequest.getParameter("importe");
		String remunTotal = renderRequest.getParameter("impoRemunTotal");
		String cantEmpleados = renderRequest.getParameter("cantEmpleados");

		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(renderRequest, "periodo",
				null);
		Concepto concepto = null;
		if (conceptoId == Concepto.DEVOLUCION_ANTICIPO) {
			PortletSession portletSession = renderRequest.getPortletSession();
			@SuppressWarnings("unchecked")
			List<Concepto> conceptos = (List<Concepto>) portletSession
					.getAttribute(WebKeysLiquidaciones.CONCEPTOS_INGRESO,
							PortletSession.APPLICATION_SCOPE);
			for (Concepto conc : conceptos) {
				if (conc.getId() == conceptoId) {
					if (conceptoId == Concepto.DEVOLUCION_ANTICIPO) {
						if(conc.getAnticipoComproNro().equals(comproNro)){
							concepto=conc;
						}
					} else {
						concepto = conc;
					}
				}
			}
		} else {
			concepto = new Concepto(conceptoId);
		}

		try {
			String[] periodoDesdeSplit = null;
			if (periodoMesAnio.length() > 0) {
				periodoDesdeSplit = periodoMesAnio.split("_");
			}
			fechaPeriodo = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeSplit[0])
					+ 1
					+ "/"
					+ periodoDesdeSplit[1]);
		} catch (Exception e) {
			fechaPeriodo = null;
		}

		// ReciboOtroConcepto oc = new ReciboOtroConcepto(new Concepto(Integer
		// .parseInt(conceptoId)), new BigDecimal(importe));
		
		Double ipte=Double.parseDouble(importe); 
		
//Desdobla Capital e Interes Boletas de Aportes		
		
		if(nroBoleta!=null && !"".equals(nroBoleta)) {   
			Integer tipoBoleta=null;
			tipoBoleta=WebKeysTesoreria.PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS.get(conceptoId);
			
			String capitalStr = renderRequest.getParameter("boleta_capital");
			String interesStr = renderRequest.getParameter("boleta_interes");
			String ajusteStr = renderRequest.getParameter("boleta_ajuste");
			
			Double capital=Double.parseDouble(capitalStr);
			Double interes=Double.parseDouble(interesStr);
			Double ajuste=Double.parseDouble(ajusteStr);
			
			Integer conceptoInteresAportes =-1;
			
			//List<FichaBoletaPortal>list =PortalEmpleadoresServiceUtil.getBoletasPorSecuencia(recibo.getEmpresa().getCuit(), recibo.getEmpresa().getSucursal(), tipoBoleta, Integer.parseInt(nroBoleta));
			//FichaBoletaPortal fb = list.get(0);
			if(tipoBoleta==WebKeysGlobal.TIPO_BOLETA_AMTIMA) {
				conceptoInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_AMTIMA"));
			}else {
				conceptoInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_UOMA"));
			}
			Concepto conceptoInteres = new Concepto(conceptoInteresAportes);
			ReciboOtroConcepto ocInteres = new ReciboOtroConcepto(conceptoInteres,BigDecimal.ZERO);
			Boolean encontro=false;

//			if(Math.round(ipte*100)/100>=Math.round((capital+ajuste)*100)/100) {
				
			if(ipte-(capital+ajuste)>0.01D) {	
			   interes=ipte-(capital+ajuste);
			   ocInteres.setImporte(new BigDecimal(interes));
			   ipte=capital+ajuste;
			   
			   for(ReciboOtroConcepto r:recibo.getOtrosConceptos()) {
					  if(r.getConcepto().getId()==conceptoInteres.getId()){
						  encontro=true;
						  r.setImporte( r.getImporte().add(new BigDecimal(interes))) ;
					  }
			   }
			   
			   if( !encontro) {
				   int idAux = getMenorId(recibo.getOtrosConceptos());
				   ocInteres.setId(--idAux);
				   recibo.getOtrosConceptos().add(ocInteres);
			   }
			}
		}
//Fin Desdoble
		
		ReciboOtroConcepto oc = new ReciboOtroConcepto(concepto,
				new BigDecimal(ipte), new BigDecimal(remunTotal),
				new Integer(cantEmpleados), fechaPeriodo,new Integer("".equalsIgnoreCase(nroBoleta)?"0":nroBoleta),
				new Integer("".equalsIgnoreCase(nroSecuenciaDDJJ)?"0":nroSecuenciaDDJJ),new BigDecimal(importe));
		
		

		int id = getMenorId(recibo.getOtrosConceptos());
		oc.setId(--id);
		recibo.getOtrosConceptos().add(oc);
	}

	private int getMenorId(List<ReciboOtroConcepto> otrosConceptos) {
		int id = -1;
		for (ReciboOtroConcepto oc : otrosConceptos) {
			if (oc.getId() < id) {
				id = oc.getId();
			}
		}
		return id;
	}

	private void borrarConcepto(RenderRequest renderRequest, Recibo recibo) {
		String id = renderRequest.getParameter("oc_id");
		BigDecimal interes=BigDecimal.ZERO;
		Integer tipoBoleta=null;
		Boolean esAporte=false;
		Boolean esInteresAportes=false;
		Boolean procedeEliminar=true;
		
//Desdoble Capital Interes Boletas Aportes	
		
		for(ReciboOtroConcepto r:recibo.getOtrosConceptos()){
			if(r.getId()==Integer.parseInt(id) && (r.getBoletaNro()!=null && r.getBoletaNro()>0)) {
				interes=r.getTotalBoleta().subtract(r.getImporte());
				tipoBoleta=WebKeysTesoreria.PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS.get(r.getConcepto().getId());
				esAporte=true;
				break;
			}else if(r.getId()==Integer.parseInt(id) && 
					 (Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_AMTIMA"))==r.getConcepto().getId() ||
					  Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_UOMA"))==r.getConcepto().getId()
					 ) 
			){
				esInteresAportes=true;
				break;
			}
		}
		if(esAporte) {
			Integer conceptoInteresAportes=-1;
			if(tipoBoleta==WebKeysGlobal.TIPO_BOLETA_AMTIMA) {
				conceptoInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_AMTIMA"));
			}else {
				conceptoInteresAportes=Integer.parseInt(TraeListasServiceUtil.getSystemConfig("CONCEPTO_INTERES_UOMA"));
			}
			Integer rIdInteres=0;
			for(ReciboOtroConcepto r:recibo.getOtrosConceptos()){
				if(r.getConcepto().getId()==conceptoInteresAportes) {
					 r.setImporte( r.getImporte().subtract(interes));
					 if(Math.round(r.getImporte().doubleValue()*100)/100 ==0D){
						 rIdInteres=r.getId(); 
					 }
					 break;
				}
			}
			
			if(rIdInteres!=0) {
			   recibo.getOtrosConceptos().remove(
					new ReciboOtroConcepto(rIdInteres));
			}   
		}
		
		if(esInteresAportes) {
			for(ReciboOtroConcepto r:recibo.getOtrosConceptos()){
				if(r.getBoletaNro()!=null && r.getBoletaNro()>0) {
				   procedeEliminar=false;
				   break;
				}
			}
		}
// Fin Desdoble
		
		if(procedeEliminar) {
		   recibo.getOtrosConceptos().remove(
				new ReciboOtroConcepto(Integer.parseInt(id)));
		}   

	}
	
	
	private void buscarEmpleadores(RenderRequest renderRequest,HttpSession session) {

		Date fechaPeriodo = null;
		int conceptoId = 0;
		String comproNro = "";
		String cuit = ParamUtil.getString(renderRequest, "cuit",
				null);
		
		String sucursal = ParamUtil.getString(renderRequest, "sucursal",
				null);
		comproNro = ParamUtil.getString(renderRequest, "concepto_id");
		try{
			conceptoId = Integer.parseInt(comproNro);
		}catch(NumberFormatException e){
			conceptoId=Integer.parseInt(comproNro.substring(0, comproNro.indexOf("_")));
			comproNro=comproNro.substring(comproNro.indexOf("_")+1,comproNro.length()-1);
		}
		
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String periodoMesAnio = ParamUtil.getString(renderRequest, "periodo",
				null);
		Concepto concepto = null;
		concepto = new Concepto(conceptoId);
		

		try {
			String[] periodoDesdeSplit = null;
			if (periodoMesAnio.length() > 0) {
				periodoDesdeSplit = periodoMesAnio.split("_");
			}
			fechaPeriodo = formatoDePeriodos.parse(Integer
					.parseInt(periodoDesdeSplit[0])
					+ 1
					+ "/"
					+ periodoDesdeSplit[1]);
		} catch (Exception e) {
			fechaPeriodo = null;
		}
		Integer nroBoleta = ParamUtil.getInteger(renderRequest, "nroBoleta");
		Integer tipoBoleta=null;
		tipoBoleta=WebKeysTesoreria.PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS.get(conceptoId);
		
		session.removeAttribute("BOLETA_EMPLEADORES_NRO");
	    session.removeAttribute("BOLETA_EMPLEADORES_IMPAGAS");
	    
	    List<FichaBoletaPortal>list =PortalEmpleadoresServiceUtil.getBoletasPorSecuencia(cuit, sucursal, tipoBoleta, nroBoleta);;
	    if(!list.isEmpty()){
	       session.setAttribute("BOLETA_EMPLEADORES_NRO", list.get(0));
	    } 
	    
	    List<FichaBoletaPortal>listI =PortalEmpleadoresServiceUtil.getBoletasImpagas(cuit, sucursal, null, 10);;
	    if(!listI.isEmpty()){
	       session.setAttribute("BOLETA_EMPLEADORES_IMPAGAS", listI);
	    } 

	}


}
