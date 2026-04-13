package ar.com.ospim.seccional.action ;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.compass.core.util.backport.java.util.Collections;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.seccional.beans.GestionSeccional;
import ar.com.ospim.seccional.beans.WebKeysSeccionales;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.CentroCosto;

public class InicializarGestionSeccionalAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(InicializarGestionSeccionalAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		_log.debug("entro processAction");
		
		setForward(actionRequest,"portlet.gestion_seccional.view");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		_log.debug("entro render");
		
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		Integer idSeccional = ParamUtil.getInteger(renderRequest, "id_seccional", 0);
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		Seccional seccional = null;
		
		if(cmd != null && cmd.equalsIgnoreCase(Constants.SEARCH) ) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}
			
			_log.debug("buscando gastos - subdiario egreso - div");
			generarSubdiarioEgresos(session, idSeccional, fechaDesde);	
			
			
			return mapping.findForward("portlet.gestion_seccional.explosionIngEgr");
		}
		if(cmd != null && cmd.equalsIgnoreCase(Constants.MANAGE)) {
		
			_log.debug("manage");
			
			_log.debug("buscando seccional");
			
//			buscar seccional completa
//			List<Seccional> seccionales = SeccionalServiceUtil.buscarSeccionales(idSeccional, null, null);
//			seccional = seccionales.get(0);
			seccional = SeccionalServiceUtil.buscarSeccionalById(idSeccional);
			
			renderRequest.setAttribute(WebKeysSeccionales.SECCIONAL_VIEW, seccional);
			renderRequest.setAttribute(WebKeysSeccionales.UOMA_URL, getUOMAurl(idSeccional));
			
		/**	
			1) cargar galeria fotos x carpeta de proyectos
			2) autorizades, 2da solapa cargos de los contactos de seccional
			3) padron, total x uoma, amtima,ospim
			4) empresas de la seccion
			5) observaciones (gestiones)
			6) gastos, sacar del panel de control - Ingresos y egresos
			7) Inversiones - Centro de costo de la seccional
		*/
//			8) caja chica??
			
			

		    
// 2) Autoridades			
			List<Contacto>contactos = new ArrayList<Contacto>();
			
			renderRequest.removeAttribute(WebKeysSeccionales.CONTACTOS_SECCIONALES);
			
			_log.debug("buscando autoridades");
			
		    contactos=SeccionalServiceUtil.buscarAutoridadesSeccionalByID(idSeccional);
		    
		    renderRequest.setAttribute(WebKeysSeccionales.CONTACTOS_SECCIONALES,contactos);
		    
		    
// 3) padron, total x uoma, amtima,ospim		
		    _log.debug("buscando padron");
		    
//		    Map<String,Integer> desgloseSeccional = new HashMap<String,Integer>();
			Map<String,Integer> desgloseSeccional = SeccionalServiceUtil.desgloseSeccional(idSeccional);
			renderRequest.setAttribute(WebKeysSeccionales.DESGLOSE_PADRON, desgloseSeccional);

// 4) Empresas	
			_log.debug("buscando empresas");
			
		    List<Empresa>empresas = new ArrayList<Empresa>();
		    renderRequest.removeAttribute(WebKeysSeccionales.EMPRESAS_SECCIONALES);
		    empresas=SeccionalServiceUtil.buscarEmpresasSeccionalByID(idSeccional);
		    renderRequest.setAttribute(WebKeysSeccionales.EMPRESAS_SECCIONALES,empresas);
		    
		    TraeListasServiceUtil.getBancos(renderRequest);
			TraeListasServiceUtil.getRamosEmpresa(renderRequest);
			TraeListasServiceUtil.getProvincias(renderRequest);
			TraeListasServiceUtil.getLocalidades(renderRequest);
			session.setAttribute(WebKeysEstudioIsidro.TIPOS_LOTE_EMPRESA_EN_SESSION, EmpresaServiceUtil.getTiposLoteEmpresa());
			session.setAttribute(WebKeysEstudioIsidro.ESTADOS_GESTION, EmpresaServiceUtil.getEstadosEmpresa());
			
// 5) observaciones (gestiones)
			_log.debug("buscando gestiones");
			
			List<GestionSeccional> gestiones = SeccionalServiceUtil.buscarGestionesxSeccional(idSeccional);
			renderRequest.setAttribute(WebKeysSeccionales.GESTIONES, gestiones);

// 6 Gastos
			_log.debug("buscando gastos - subdiario egreso");
			generarSubdiarioEgresos(session, idSeccional, null);	
			
// 7) Inversiones - Centros de Costos	
			
			_log.debug("buscando centros de costos");
			
			List<CentroCosto>centros = SeccionalServiceUtil.buscarCentroCostoSeccionalByID(idSeccional);
			renderRequest.setAttribute(WebKeysSeccionales.CENTROSCOSTO,centros);
			
			
			return mapping.findForward("portlet.gestion_seccional.tablero");
		}
		
		cargarListas(renderRequest);
		
		return mapping.findForward("portlet.gestion_seccional.view");
		                      		                      
	}

	private void cargarListas(RenderRequest renderRequest) {
		
		try {
			
			PortletSession portletSession = renderRequest.getPortletSession();
			
			List<Provincia> provincias = (ArrayList<Provincia>) portletSession.getAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION, PortletSession.APPLICATION_SCOPE);

			if (provincias == null) {
				provincias = TraeListasServiceUtil.getProvincias();
				portletSession.setAttribute(WebKeysAfiliados.PROVINCIAS_EN_SESSION,
				provincias,PortletSession.APPLICATION_SCOPE);	
			}
			
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	private void generarSubdiarioEgresos(HttpSession session, int idSeccional, Date fecha) throws NumberFormatException, SystemException {
		
		Calendar fechaDesde = DateUtils.getCalendarGMTMenos3();
		if(fecha!=null) {
			fechaDesde.setTime(fecha);
		}else {
			fechaDesde.add(Calendar.DATE, -31);
		}	
		
		Calendar fechaHasta = DateUtils.getCalendarGMTMenos3();
		
		int entidad = WebKeysGlobal.UOMA;
			
		String excluye = TraeListasServiceUtil.getSystemConfig("TABLERO_INGRESOS_EGRESOS_EXCLUYE");
		String[] vexcluye = excluye.split(";");

		List<ItemSubdiarioEgreso> egresos1 = new ArrayList<ItemSubdiarioEgreso>();
		List<? extends ItemSubdiarioEgreso> reporte = null;
		
		try {
			reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoCompletoParaSubdiario(fechaDesde.getTime(),
								fechaHasta.getTime(), true, false, false, false,
								entidad, idSeccional);

			egresos1.addAll(reporte);
			List<? extends ItemSubdiarioEgreso> reporteParaSubdiario = MovimientoBancarioServiceUtil
						.reporteParaSubdiario(fechaDesde.getTime(), fechaHasta.getTime(), entidad);
				egresos1.addAll(reporteParaSubdiario);
				
		}catch (Exception e) {
			_log.error(e);
		}	
			
		List<ItemSubdiarioEgreso> egresos= new ArrayList<ItemSubdiarioEgreso>();
		for(ItemSubdiarioEgreso i:egresos1){
		   boolean subir=true;	
		   for(int xi=0;xi<vexcluye.length;xi++){
			   for(SubdiarioEgresoColumna fp:i.getHacia()){
			      if(fp.getCuenta().equalsIgnoreCase(vexcluye[xi])){
				     subir=false;
				     break;
			      }
			   }
			   if(!subir) break;
		   }
			   
		   if(subir){
			   egresos.add(i);
		   }
		}

		Collections.sort(egresos, new Comparator<ItemSubdiarioEgreso>() {
			public int compare(ItemSubdiarioEgreso arg0,
					ItemSubdiarioEgreso arg1) {
				// Primero por fecha OP
				int compareTo = arg0.getFecha().compareTo(arg1.getFecha());
				if (compareTo == 0) {
					compareTo = arg0.getNumeroOP().compareTo(
							arg1.getNumeroOP());
					if (compareTo == 0) {
						if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() != null) {
							compareTo = arg0.getBaja_fecha().compareTo(
									arg1.getBaja_fecha());
						} else if (arg0.getBaja_fecha() != null
								&& arg1.getBaja_fecha() == null) {
							compareTo = 1;
						} else if (arg0.getBaja_fecha() == null
								&& arg1.getBaja_fecha() != null) {
							compareTo = -1;
						}
					}
				}
				return compareTo;
			}

		});

		String[] cuentaContableSecc = SeccionalServiceUtil.buscarCuentaContablexSeccional(idSeccional);
		String cuenta = cuentaContableSecc!=null && cuentaContableSecc.length>0? cuentaContableSecc[0]:"";
		
		
		List<ItemSubdiarioIngreso> comprobantes = new ArrayList<ItemSubdiarioIngreso>();
		
		for(ItemSubdiarioEgreso it:egresos){
			if(it.getObservaciones()==null || !"ANULADAMISMODIA".equalsIgnoreCase(it.getObservaciones().trim())){
			   if(it.getHacia()!=null){
				   
				 for(SubdiarioEgresoColumna fp:it.getHacia()){
				   String cuentaCpte = fp.getCuenta(entidad);	
				   if(cuenta.equalsIgnoreCase(cuentaCpte)){
					   
					 
					 List<ComprobanteCajaChica> lista =CajaChicaServiceUtil.comprobantesPorOP(WebKeysGlobal.UOMA , Integer.parseInt(it.getNumeroOP()));
					 
					  
					   for(SubdiarioComprobante sc:it.getComprobantesSubdiario()){
						 if(sc.getConceptos()!=null){
							 for(ComprobanteConcepto c:sc.getConceptos()){
								 if(c.getCuenta().equalsIgnoreCase(cuentaCpte)){
									 
									if(lista.size()==0) { 
									 ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();
									 item.setImporte(sc.getImporte());
									 item.setCuit(it.getCuit());
									 item.setRazonSocial(it.getRazonSocial());
									 item.setComprobante(fp.getDescripcionPAraSubdiario());
									 item.setFecha(it.getFecha());	
									 item.setComprobante(sc.getTipoComprobante()+" "+sc.getNroComprobante());
									 comprobantes.add(item);
									}else { //Viene de Caja Chica
										for(ComprobanteCajaChica cch:lista) {
											
											if(cch.getConceptos().get(0).getConceptoComprobante().getId()==c.getConceptoComprobante().getId()) {
												
												
											  ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();
											  item.setImporte(cch.getImporte());
											  item.setCuit(cch.getAcreedorEmpresa().getCuit());
											  item.setRazonSocial(cch.getAcreedorEmpresa().getDescripcion());
											  item.setComprobante(cch.getTipoComprobante() +" "+cch.getNroComprobante()+" (CCH)");
											  item.setFecha(cch.getFechaEmision());
											  boolean existe=false;
											  for(ItemSubdiarioIngreso i:comprobantes) {
												  if(i.getComprobante().equalsIgnoreCase(item.getComprobante())) {
													  existe=true;
													  break;
												  }
											  }
											  
											  if(!existe){
											     comprobantes.add(item);
											  }
											  
											}
											
										}
									}
								 }
							 }
						 }
					   } 
				   }	 
				 }
				 
			   }
		   } 
		}
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE", comprobantes);
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE",cuenta);
		
	}
	
	private String getUOMAurl(Integer idSeccional) {
		
		Map<Integer, String> urlsUOMAdotorgdotar = new HashMap<Integer,String>();
		
		urlsUOMAdotorgdotar.put(103, "http://www.uoma.org.ar/img/seccionales/BahiaBlanca.jpg"); // Bahía Blanca
		urlsUOMAdotorgdotar.put(105, "http://www.uoma.org.ar/img/seccionales/Cañuelas.jpg"); // Cañuelas
		urlsUOMAdotorgdotar.put(2102, "http://www.uoma.org.ar/img/seccionales/Carcaraña.JPG"); // Carcaraña
		urlsUOMAdotorgdotar.put(2104, "http://www.uoma.org.ar/img/seccionales/Chabás.png"); // Chabás
		urlsUOMAdotorgdotar.put(110, "http://www.uoma.org.ar/img/seccionales/Chacabuco.jpg"); // Chacabuco
		urlsUOMAdotorgdotar.put(111, "http://www.uoma.org.ar/img/seccionales/Chivilcoy.jpg"); // Chivilcoy
		urlsUOMAdotorgdotar.put(803, "http://www.uoma.org.ar/img/seccionales/ConcepciondelUruguay.jpg"); // Concep.del Uruguay
		urlsUOMAdotorgdotar.put(402, "http://www.uoma.org.ar/img/seccionales/Córdoba.jpg"); // Córdoba
		urlsUOMAdotorgdotar.put(804, "http://www.uoma.org.ar/img/seccionales/Crespo.jpg"); // Crespo
		urlsUOMAdotorgdotar.put(403, "http://www.uoma.org.ar/img/seccionales/GeneralDeheza.jpg"); // General Deheza
		urlsUOMAdotorgdotar.put(806, "http://www.uoma.org.ar/img/seccionales/GeneralRamÍrez.jpg"); // General Ramirez
		urlsUOMAdotorgdotar.put(146, "http://www.uoma.org.ar/img/seccionales/GeneralRodriguez.jpg"); // Gral Rodriguez
		urlsUOMAdotorgdotar.put(114, "http://www.uoma.org.ar/img/seccionales/junÍn.jpg"); // Junín
		urlsUOMAdotorgdotar.put(404, "http://www.uoma.org.ar/img/seccionales/Laborde.jpg"); // Laborde
		urlsUOMAdotorgdotar.put(406, "http://www.uoma.org.ar/img/seccionales/LaCarlota.jpg"); // La Corlota
		urlsUOMAdotorgdotar.put(115, "http://www.uoma.org.ar/img/seccionales/Laplata.jpg"); // La Plata
		urlsUOMAdotorgdotar.put(405, "http://www.uoma.org.ar/img/seccionales/Laboulaye.jpg"); // Laboulaye
		urlsUOMAdotorgdotar.put(2106, "http://www.uoma.org.ar/img/seccionales/Maciel.jpg"); // Maciel
		urlsUOMAdotorgdotar.put(2107, "http://www.uoma.org.ar/img/seccionales/MarÍaJuana.jpg"); // María Juana
		urlsUOMAdotorgdotar.put(140, "http://www.uoma.org.ar/img/seccionales/monte.jpg"); // Monte
		urlsUOMAdotorgdotar.put(118, "http://www.uoma.org.ar/img/seccionales/Navarro.jpg"); // Navarro
		urlsUOMAdotorgdotar.put(136, "http://www.uoma.org.ar/img/seccionales/NuevedeJulio.jpg"); // 9 de Julio
		urlsUOMAdotorgdotar.put(122, "http://www.uoma.org.ar/img/seccionales/Pilar.jpg"); // Pilar
		urlsUOMAdotorgdotar.put(1103, "http://www.uoma.org.ar/img/seccionales/Realico.jpg"); // Realicó
		urlsUOMAdotorgdotar.put(601, "http://www.uoma.org.ar/img/seccionales/Resistencia.jpg"); // Resistencia
		urlsUOMAdotorgdotar.put(409, "http://www.uoma.org.ar/img/seccionales/RioCuarto.jpg"); // RioCuarto
		urlsUOMAdotorgdotar.put(124, "http://www.uoma.org.ar/img/seccionales/Rojas.jpg"); // Rojas
		urlsUOMAdotorgdotar.put(2110, "http://www.uoma.org.ar/img/seccionales/Rosario.jpg"); // Rosario
		urlsUOMAdotorgdotar.put(810, "http://www.uoma.org.ar/img/seccionales/RosariodelTala.jpg"); // Rosario del Tala
		urlsUOMAdotorgdotar.put(125, "http://www.uoma.org.ar/img/seccionales/Saladillo.jpg"); // Saladillo
		urlsUOMAdotorgdotar.put(126, "http://www.uoma.org.ar/img/seccionales/Salto.jpg"); // Salto
		urlsUOMAdotorgdotar.put(127, "http://www.uoma.org.ar/img/seccionales/SanCayetano.jpg"); // San Cayetano
		urlsUOMAdotorgdotar.put(129, "http://www.uoma.org.ar/img/seccionales/SanNicolas.jpg"); // San Nicolás
		urlsUOMAdotorgdotar.put(411, "http://www.uoma.org.ar/img/seccionales/SanFrancisco.jpg"); // San Francisco
		urlsUOMAdotorgdotar.put(2112, "http://www.uoma.org.ar/img/seccionales/SanJorge.jpg"); // San Jorge
		urlsUOMAdotorgdotar.put(2115, "http://www.uoma.org.ar/img/seccionales/SantaFe.jpg"); // Santa Fé
		urlsUOMAdotorgdotar.put(130, "http://www.uoma.org.ar/img/seccionales/Tandil.jpg"); // Tandil
		urlsUOMAdotorgdotar.put(131, "http://www.uoma.org.ar/img/seccionales/TresArroyos.jpg"); // Tres Arroyos
		urlsUOMAdotorgdotar.put(811, "http://www.uoma.org.ar/img/seccionales/Viale.jpg"); // Viale
		urlsUOMAdotorgdotar.put(412, "http://www.uoma.org.ar/img/seccionales/VilladelRosario.jpg"); // Villa del Rosario
		urlsUOMAdotorgdotar.put(413, "http://www.uoma.org.ar/img/seccionales/VillaMarÍa.jpg"); // Villa María

		return urlsUOMAdotorgdotar.get(idSeccional);
	}
	
	
}




