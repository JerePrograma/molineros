package ar.com.ospim.global.actions;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.beans.RetencionIVA;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.comprobantes.action.EditarComprobantesAction;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarComprobanteEmbebidoAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarComprobanteEmbebidoAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a reder");
		int entidad=WebKeysGlobal.OSPIM;		
		BigDecimal importesCompro=BigDecimal.ZERO;
		if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;			
		}else if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}
		
		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

		Comprobante comp = EditarComprobantesAction
				.getComprobanteFromRequest(renderRequest);

		List<Comprobante> comprobantes = (ArrayList<Comprobante>) session
				.getAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION);
		if (comprobantes == null) {
			comprobantes = new ArrayList<Comprobante>();
		} else {
			comprobantes.clear();
		}
				
		List<Comprobante> busqueda = ComprobanteServiceUtil
				.getComprobantesImpagosConConceptos(comp, entidad);
		if (busqueda != null && busqueda.size()>0) {
			comprobantes.addAll(busqueda);
			
			if(entidad==WebKeysGlobal.UOMA) {
			   importesCompro=sumaImportesOrdenUOMA(comprobantes);	
			}else {
			   importesCompro=sumaImportesOrden(comprobantes);
			}   
//			Empresa proveedor = TraeListasServiceUtil.getEmpleadores(busqueda.get(0).getCuit(),null,null);
			Empresa emp = busqueda.get(0).getAcreedorEmpresa();
			Empresa proveedor = TraeListasServiceUtil.getEmpleadores(emp.getCuit(),null, emp.getSucursal()).get(0);
			if(proveedor!=null&&proveedor.getImpGanancias()!=null && proveedor.getImpGanancias().equals("AC")){
				//1ro BUSCO TODOS LOS COMPROBANTES DEL MES EN CURSO				
				Calendar periodoCalendar=Calendar.getInstance();
				periodoCalendar.set(Calendar.DAY_OF_MONTH, 1);	
				if(entidad == WebKeysGlobal.OSPIM) {
					Integer fechaAltaOPMes = ParamUtil.getInteger(renderRequest,
							"fechaAltaOPMes");
//					Integer fechaAltaOPDia = ParamUtil.getInteger(renderRequest,
//							"fechaAltaOPDia");
					Integer fechaAltaOPAnio = ParamUtil.getInteger(renderRequest,
							"fechaAltaOPAnio");
//					SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
					try {
						periodoCalendar.set(java.util.Calendar.DATE, 1); //fechaAltaOPDia
						periodoCalendar.set(java.util.Calendar.MONTH, fechaAltaOPMes);
						periodoCalendar.set(java.util.Calendar.YEAR, fechaAltaOPAnio);
//						Date fechaEmisionC = formatoDeFecha.parse(fechaAltaOPDia
//								+ "/" + (Integer.parseInt(fechaAltaOPMes) + 1)
//								+ "/" + fechaAltaOPAnio);
					} catch (Exception e) {
//						fechaEmisionC = null;
						//dejamos el Calendar del dia en curso.
					}
				}
				
				BigDecimal retencion=AfipServiceUtil.getRetencionGanancias(proveedor.getCuit(),importesCompro, periodoCalendar.getTime(), entidad);
				if(retencion.compareTo(BigDecimal.ZERO)>0){
					OrdenPago ordenPago = (OrdenPago) session
							.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
					List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					if (list == null) {
						list = new ArrayList<OrdenPago.FormaPago>();
						ordenPago.setFormaPago(list);
					}
					RetencionGanancias ret = new RetencionGanancias();
					//CUENTAS BCRIAS POR DEFECTO SEGUN ENTIDAD
					int nroCta=entidad==WebKeysGlobal.OSPIM?2:entidad==WebKeysGlobal.UOMA?8:5;
					CuentaBancaria cta = new CuentaBancaria(nroCta);					
					ret.setImporte(retencion);
					ret.setCuentaBancaria(cta);
					if (!list.contains(new OrdenPago.FormaPago(ret))) {
						list.add(new OrdenPago.FormaPago(ret));
					}
					ordenPago.setPagos(list);
					session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
							ordenPago);
				}else if(retencion.compareTo(new BigDecimal(-1))==0){
					// Importe de retenciones = -1 es porque no está configurado el codigo de régimen
					SessionErrors.add(renderRequest, "regimenError");
					renderRequest.setAttribute("msgError2", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-regimen"));
				}else if(retencion.compareTo(new BigDecimal(-2))==0){
					// Importe de retenciones = -2 es porque no está importada la tabla de excenciones
					SessionErrors.add(renderRequest, "exencionError");
					renderRequest.setAttribute("msgError3", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-exencion"));
					SessionErrors.add(renderRequest, "exencionUrlError");
					renderRequest.setAttribute("msgError4", LanguageUtil.get(defaultLocale, "exception-ret-ganancia-exencion-url"));
				}
			}
			
			if(entidad==WebKeysGlobal.UOMA) { //Agregado para calcular retención de IIBB
				
				
				List<RetencionIIBB> retIIBB = OrdenPago.getRetencionIIBB(proveedor, busqueda);
				if(retIIBB!=null && !retIIBB.isEmpty() ) {
					if(retIIBB.get(0).getError()!= null) {
					  SessionErrors.add(renderRequest, "regimenError");
					  renderRequest.setAttribute("msgError2", retIIBB.get(0).getError());
					}else{
									
					  OrdenPago ordenPago = (OrdenPago) session
						  	.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
					  List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					  if (list == null) {
						list = new ArrayList<OrdenPago.FormaPago>();
						ordenPago.setFormaPago(list);
					  }else {
						List<OrdenPago.FormaPago> listAux = new ArrayList<OrdenPago.FormaPago>();
						for(OrdenPago.FormaPago f:list) {
							if(!(f.getPago() instanceof RetencionIIBB)) {
							  listAux.add(f); 	
							}
						}
						list=listAux;
					  }
					
					  for(RetencionIIBB ret:retIIBB) {
					   if (!list.contains(new OrdenPago.FormaPago(ret))) {
						if(ret.getImporte().compareTo(BigDecimal.ZERO)==1) {   
						   list.add(new OrdenPago.FormaPago(ret));
						}   
					   }
					  }   
					  ordenPago.setPagos(list);
					  session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
							ordenPago);
					}
				}
				
				
				
			}
			
            if(entidad==WebKeysGlobal.UOMA) { //Agregado para calcular retención de IVA especial
            	Boolean cRetIva = ParamUtil.getBoolean(renderRequest,"retivaesp");
            	if(cRetIva) {
				
				  RetencionIVA retIVA = OrdenPago.getRetencionIVA(proveedor, busqueda);
				  if(retIVA!=null  ) {
					  
					OrdenPago ordenPago = (OrdenPago) session
							.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
					List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					if (list == null) {
						list = new ArrayList<OrdenPago.FormaPago>();
						ordenPago.setFormaPago(list);
					}else {
						List<OrdenPago.FormaPago> listAux = new ArrayList<OrdenPago.FormaPago>();
						for(OrdenPago.FormaPago f:list) {
							if(!(f.getPago() instanceof RetencionIVA)) {
							  listAux.add(f); 	
							}
						}
						list=listAux;
					}
				
				    if (!list.contains(new OrdenPago.FormaPago(retIVA))) {
						list.add(new OrdenPago.FormaPago(retIVA));
					
					}   
					ordenPago.setPagos(list);
					session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
							ordenPago);
				}
				  
			  }
            }
            
            
            if(entidad==WebKeysGlobal.UOMA) { //Agregado para calcular retención de Ganancias especial
            	Boolean cRetGan = ParamUtil.getBoolean(renderRequest,"retganesp");
            	if(cRetGan) {
				
				  RetencionGanancias retGan = OrdenPago.getRetencionGananciasEspecial(proveedor, busqueda);
				  if(retGan!=null  ) {
					  
					OrdenPago ordenPago = (OrdenPago) session
							.getAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION);
					List<OrdenPago.FormaPago> list = ordenPago.getFormaPago();
					if (list == null) {
						list = new ArrayList<OrdenPago.FormaPago>();
						ordenPago.setFormaPago(list);
					}else {
						List<OrdenPago.FormaPago> listAux = new ArrayList<OrdenPago.FormaPago>();
						for(OrdenPago.FormaPago f:list) {
							if(!(f.getPago() instanceof RetencionGanancias)) {
							  listAux.add(f); 	
							}
						}
						list=listAux;
					}
				
				    if (!list.contains(new OrdenPago.FormaPago(retGan))) {
						list.add(new OrdenPago.FormaPago(retGan));
					
					}   
					ordenPago.setPagos(list);
					session.setAttribute(WebKeysLiquidaciones.ORDEN_PAGO_EN_EDICION,
							ordenPago);
				}
				  
			  }
            }
			
		}
		
		

		session.setAttribute(WebKeysGlobal.COMPROBANTES_EN_SESSION,
				comprobantes);
		session.setAttribute(WebKeysGlobal.SUMA_COMPROBANTES_EN_SESSION,
				importesCompro);

		renderRequest.setAttribute("esEditable", "true");
		_log.debug("Saliendo de render");
		return mapping.findForward("portlet.utils.comprobante.embebido.result.search");
	}

	public static BigDecimal sumaImportesOrden(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		BigDecimal signo = new BigDecimal(1);
		
		for (Comprobante comprobante : comprobantes) {
			signo=BigDecimal.ONE;
			if("NCR".equalsIgnoreCase(comprobante.getTipoComprobante())) {
				signo=BigDecimal.ONE.negate();
			}
			suma = suma.add(comprobante.getImporteComprobante().multiply(signo));
		}
		return suma;
	}
	
	public static BigDecimal sumaImportesOrdenUOMA(List<Comprobante> comprobantes) {
		BigDecimal suma = new BigDecimal(0);
		BigDecimal signo = new BigDecimal(1);
		
		for (Comprobante comprobante : comprobantes) {
			signo=BigDecimal.ONE;
			if("NCR".equalsIgnoreCase(comprobante.getTipoComprobante())) {
				signo=BigDecimal.ONE.negate();
			}
			
			for(ComprobanteConcepto c:comprobante.getConceptos()) {
			 BigDecimal base =new BigDecimal(0);
			 base=c.getGravadoIVA().add(c.getExento());
			 suma = suma.add(base.multiply(signo));
			}
		}
		return suma;
	}
}
