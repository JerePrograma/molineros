package ar.com.uoma.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.EmpresaSituacionFinanciera;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ControlIngresosEgresosAction extends PortletAction {
	
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
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		String tipo = ParamUtil.getString(renderRequest, "tipo", null);
		Integer entidad = ParamUtil.getInteger(renderRequest, "entidad",1);
		

		Date fechaIni = getDesde(httpServletRequest);
		
		String msg = "";
		if (!StringUtils.checkEmpty(cmd)) {
			if(cmd.equals("nivel_1") ){ 
				
				if("I".equalsIgnoreCase(tipo)){
					procesaIngresos(session);
				}
				if("E".equalsIgnoreCase(tipo)){
					procesaEgresos(session,entidad,fechaIni);
				}
				return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.control_ingresos_egresos_explosion"));
			}
			
			if(cmd.equals("nivel_2") ){
				String cuenta = ParamUtil.getString(renderRequest, "cuenta", null);
				String leyenda = ParamUtil.getString(renderRequest, "leyenda", null);
				if("INGRESOS".equalsIgnoreCase(leyenda)){
					procesaIngresosCpte(session,cuenta);
				}
				if("EGRESOS".equalsIgnoreCase(leyenda)){
					procesaEgresosCpte(session,entidad,fechaIni,cuenta);
				}
				return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.control_ingresos_egresos_explosion_cpte"));
			}
			
			if(cmd.equals("declarados_nivel_0") ){
				
				
				procesaDeclaradosEmpresas(session,renderRequest);
								
				return mapping.findForward(getForward(renderRequest,
						"portlet.uoma.control_ingresos_egresos_explosion_declarado_result"));
			}
			
            if(cmd.equals("declarados_nivel_1") ){
				
				
				procesaDeclaradosEmpresasN1(session,renderRequest);
				return mapping.findForward(getForward(renderRequest,"portlet.tesoreria.reporte.cuentas_corrientes_actas_conv"));			
//				return mapping.findForward(getForward(renderRequest,
//						"portlet.uoma.control_ingresos_egresos_explosion_declarado_ctacte"));
			}
				
		}
		
		return mapping.findForward("portlet.uoma.control_ingresos_egresos_explosion");
		
	}
	
	
	private void procesaIngresos(HttpSession session){
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION");
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO");
		List<ItemSubdiarioIngreso> ingresos= (List<ItemSubdiarioIngreso>) session.getAttribute("CONTROL_INGRESOS");
		
		
		Map<String, ItemSubdiarioIngreso> cuentas = new TreeMap<String,ItemSubdiarioIngreso>();
		
		for(ItemSubdiarioIngreso i:ingresos){
		  if(i.getRazonSocial()==null || !"ANULADAMISMODIA".equalsIgnoreCase(i.getRazonSocial().trim())){	
			ItemSubdiarioIngreso it = new ItemSubdiarioIngreso();
			if(cuentas.get(i.getNumeroCuenta()) != null){
		    	it=cuentas.get(i.getNumeroCuenta());
		    	it.setImporte(it.getImporte().add(i.getImporte()));
		    	
		    }else{
		    	it.setNumeroCuenta(i.getNumeroCuenta());
		    	it.setCuenta(i.getCuenta());
		    	it.setImporte(i.getImporte());
		    }
		    
		    cuentas.put(i.getNumeroCuenta(), it);
		  }	
	   	     
		}
		
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION", cuentas);
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO","INGRESOS");
	}
	
	private void procesaEgresos(HttpSession session,Integer entidad,Date fechaIni){
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION");
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO");
		List<ItemSubdiarioEgreso> egresos= (List<ItemSubdiarioEgreso>) session.getAttribute("CONTROL_EGRESOS");
		List<PlanCuentas> planCuentas = TraeListasServiceUtil.getPlanCuentas(fechaIni, entidad);   
		
		Map<String, ItemSubdiarioIngreso> cuentas = new TreeMap<String,ItemSubdiarioIngreso>();
		for(ItemSubdiarioEgreso it:egresos){
			if(it.getObservaciones()==null || !"ANULADAMISMODIA".equalsIgnoreCase(it.getObservaciones().trim())){
			   if(it.getHacia()!=null){
				for(SubdiarioEgresoColumna fp:it.getHacia()){
					 String cuenta = fp.getCuenta(entidad);
					 ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();	
					 item.setNumeroCuenta(cuenta);
					 int indexOf = planCuentas.indexOf(new PlanCuentas(cuenta, ""));
					 if (indexOf != -1) {
						item.setCuenta(planCuentas.get(indexOf).getCuenta());
					 }
					 if(cuentas.get(cuenta) != null){
					     item.setImporte(cuentas.get(cuenta).getImporte().add(fp.getImporte()));
				     }else{
				    	 item.setImporte(fp.getImporte());
				     }
					 cuentas.put(cuenta, item);  
				}
					
			   }
		   } 
		}
		
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION", cuentas);
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO","EGRESOS");
	}
	
	
	private void procesaIngresosCpte(HttpSession session,String cuenta){
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE");
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE");
		List<ItemSubdiarioIngreso> ingresos= (List<ItemSubdiarioIngreso>) session.getAttribute("CONTROL_INGRESOS");
		
		
		List<ItemSubdiarioIngreso> comprobantes = new ArrayList<ItemSubdiarioIngreso>();
		
		for(ItemSubdiarioIngreso i:ingresos){
		  if(i.getRazonSocial()==null || !"ANULADAMISMODIA".equalsIgnoreCase(i.getRazonSocial().trim())){	
             if(cuenta.equalsIgnoreCase(i.getNumeroCuenta())){
            	comprobantes.add(i); 
             }
		  }	
		}
		
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE", comprobantes);
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE",cuenta);
	}
	
	
	
	private void procesaEgresosCpte(HttpSession session,Integer entidad,Date fechaIni,String cuenta){
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE");
		session.removeAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE");
		List<ItemSubdiarioEgreso> egresos= (List<ItemSubdiarioEgreso>) session.getAttribute("CONTROL_EGRESOS");
		List<ItemSubdiarioIngreso> comprobantes = new ArrayList<ItemSubdiarioIngreso>();
		
		for(ItemSubdiarioEgreso it:egresos){
			if(it.getObservaciones()==null || !"ANULADAMISMODIA".equalsIgnoreCase(it.getObservaciones().trim())){
			   if(it.getHacia()!=null){
				   
				 for(SubdiarioEgresoColumna fp:it.getHacia()){
				   String cuentaCpte = fp.getCuenta(entidad);	
				   if(cuenta.equalsIgnoreCase(cuentaCpte)){
					 ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();
					 item.setImporte(fp.getImporte());
					 item.setCuit(it.getCuit());
					 item.setRazonSocial(it.getRazonSocial());
					 item.setComprobante(fp.getDescripcionPAraSubdiario());
					 item.setFecha(it.getFecha());
					 for(SubdiarioComprobante sc:it.getComprobantesSubdiario()){
						 if(sc.getConceptos()!=null){
							 for(ComprobanteConcepto c:sc.getConceptos()){
								 if(c.getCuenta().equalsIgnoreCase(cuentaCpte)){
									 item.setComprobante(sc.getTipoComprobante()+" "+sc.getNroComprobante());	 
								 }
							 }
						 }
					 }
					 comprobantes.add(item);
				   }	 
				 }
				 
				 
				 /* Original
				  for(SubdiarioEgresoColumna fp:it.getHacia()){
				   String cuentaCpte = fp.getCuenta(entidad);	
				   if(cuenta.equalsIgnoreCase(cuentaCpte)){
					 ItemSubdiarioIngreso item = new ItemSubdiarioIngreso();
					 item.setImporte(fp.getImporte());
					 item.setCuit(it.getCuit());
					 item.setRazonSocial(it.getRazonSocial());
					 item.setComprobante(fp.getDescripcionPAraSubdiario());
					 item.setFecha(it.getFecha());
					 comprobantes.add(item);
				   }	 
				 }
				 */
				 
			   }
		   } 
		}
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_CPTE", comprobantes);
		session.setAttribute("CONTROL_INGRESOS_EGRESOS_EXPLOSION_TIPO_CPTE",cuenta);
	}
	
	protected static Date getDesde(HttpServletRequest req) {
		return DateUtils.getFechaDesde(req);
	}
	
	private void procesaDeclaradosEmpresas(HttpSession session,RenderRequest renderRequest){
		String[] meses = {"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO","JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};
		session.removeAttribute("CONTROL_DECLARADOS");
		session.removeAttribute("CONTROL_DECLARADOS_TIPO");
		session.removeAttribute("CONTROL_DECLARADOS_PERIODO");
		
		SimpleDateFormat formatD = new SimpleDateFormat("dd-MM-yyyy");
		String fechaDesdeDia = ParamUtil.getString(renderRequest, "fechaDesdeDia");
		String fechaDesdeMes = ParamUtil.getString(renderRequest, "fechaDesdeMes");
		fechaDesdeMes = String.valueOf(Integer.valueOf(fechaDesdeMes) + 1);
		String fechaDesdeAnio = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		String cuit = ParamUtil.getString(renderRequest, "cuit",null);
		int tipoBoleta = ParamUtil.getInteger(renderRequest, "tipoBoleta");
		Map<String, EmpresaSituacionFinanciera> empresas = new TreeMap<String,EmpresaSituacionFinanciera>();
		Date fechaIniD;
		try {
			fechaIniD = formatD.parse(fechaDesdeDia + "-" + fechaDesdeMes
					+ "-" + fechaDesdeAnio);
		
		
		    Date fechaHtaD=DateUtils.getLastDateOfMonth(fechaIniD,false);
	        //DECLARADO	
		    List<FichaBoletaPortal>l= PortalEmpleadoresServiceUtil.getReporteBoletaPortalTodasEmpresas(fechaIniD, fechaHtaD, cuit, 0, true);
		    for(FichaBoletaPortal i:l){
		    	EmpresaSituacionFinanciera it = new EmpresaSituacionFinanciera();
		    	BigDecimal importe=BigDecimal.ZERO;
		    	
		    	if(tipoBoleta==0 || tipoBoleta==2){
		    	   importe=importe.add(i.getCuotasocialuoma());	
		    	}
		    	
		    	if(tipoBoleta==0 || tipoBoleta==3){
			    	   importe=importe.add(i.getCuotausufructo());	
			    }
		    	
		    	if(tipoBoleta==0 || tipoBoleta==4){
			    	   importe=importe.add(i.getArticulo46());	
			    }
		    	
		    	if(tipoBoleta==0 || tipoBoleta==5){
			    	   importe=importe.add(i.getAportesocialuoma());	
			    }
		    	
		    	if(!"11111111111".equalsIgnoreCase(i.getEmpresa_cuit())){
		    	   if(empresas.get(i.getEmpresa_cuit()) != null){
			    	   it=empresas.get(i.getEmpresa_cuit());
			    	   it.setTotal(it.getTotal().add(importe));
			       }else{
			    	   it.setCuit(i.getEmpresa_cuit());
			    	   it.setRazonSoc(i.getRazon_soc());
			    	   it.setTotal(importe);
			       }
			       empresas.put(i.getEmpresa_cuit(), it);
		    	}
		      	
		    }
		    
		    
		    
		    //PAGADO
		    List<FichaBoletaPortal>l1= PortalEmpleadoresServiceUtil.getReporteBoletaPortal(fechaIniD, fechaHtaD, null,tipoBoleta==0?null:Integer.toString(tipoBoleta), 
		    		                                                   null, null, null, null, 0, 0, null, cuit, null, null, 0);
		    for(FichaBoletaPortal i:l1){
		    	EmpresaSituacionFinanciera it = new EmpresaSituacionFinanciera();
		    	BigDecimal importe=BigDecimal.ZERO;
		    	if(tipoBoleta==0 || (tipoBoleta==2 && "Cuota Social UOMA".equalsIgnoreCase(i.getDescripcion()))
		    			|| (tipoBoleta==3 && "Cuota Usufructo".equalsIgnoreCase(i.getDescripcion()))
		    			|| (tipoBoleta==4 && "Art. 46".equalsIgnoreCase(i.getDescripcion()))
		    			|| (tipoBoleta==5 && "Aporte Solidario UOMA".equalsIgnoreCase(i.getDescripcion()))
		    			){
			    	   importe=importe.add(i.getImporte());	
			    	}
			    	
			    	
			    	if(!"11111111111".equalsIgnoreCase(i.getEmpresa_cuit())){
			    	   if(empresas.get(i.getCuit()) != null){
				    	   it=empresas.get(i.getCuit());
				    	   it.setTotalPagado(it.getTotalPagado().add(importe));
				       }else{
				    	   it.setCuit(i.getCuit());
				    	   it.setRazonSoc(i.getRazon_soc());
				    	   it.setTotalPagado(importe);
				       }
				       empresas.put(i.getCuit(), it);
			    	}
		    }
		    
		    
		  //ESTIMADO
		    for(int xi=1;xi<=3;xi++){
		    	 
		    	
		    	Calendar c1 = Calendar.getInstance();
		    	c1.setTime(fechaIniD);
	            c1.add(Calendar.MONTH,-xi);
	            Date fechaEstDde=c1.getTime();
		    	
		    	Date fechaEstHta=DateUtils.getLastDateOfMonth(fechaEstDde,false);
		    
	        	
		        List<FichaBoletaPortal>le= PortalEmpleadoresServiceUtil.getReporteBoletaPortalTodasEmpresas(fechaEstDde, fechaEstHta, cuit, 0, true);
		        for(FichaBoletaPortal i:le){
		    	    EmpresaSituacionFinanciera it = new EmpresaSituacionFinanciera();
		    	    BigDecimal importe=BigDecimal.ZERO;
		    	
		    	    if(tipoBoleta==0 || tipoBoleta==2){
		    	       importe=importe.add(i.getCuotasocialuoma());	
		    	    }
		    	
  		    	    if(tipoBoleta==0 || tipoBoleta==3){
			    	   importe=importe.add(i.getCuotausufructo());	
			        }
		    	
		    	    if(tipoBoleta==0 || tipoBoleta==4){
			    	   importe=importe.add(i.getArticulo46());	
			        }
		    	
		    	    if(tipoBoleta==0 || tipoBoleta==5){
			    	   importe=importe.add(i.getAportesocialuoma());	
			        }
		    	
		    	    if(!"11111111111".equalsIgnoreCase(i.getEmpresa_cuit())){
		    	       if(empresas.get(i.getEmpresa_cuit()) == null){
			    	       it.setCuit(i.getEmpresa_cuit());
			    	       it.setRazonSoc(i.getRazon_soc());
			    	       it.setEstimado(importe);
			    	       empresas.put(i.getEmpresa_cuit(), it);
			           }
			           
		    	    }
		      	
		        }
		    
		    }
		    
		  session.setAttribute("CONTROL_DECLARADOS_PERIODO", fechaIniD);    
		} catch (ParseException e) {
		}
		
		session.setAttribute("CONTROL_DECLARADOS", empresas);
		session.setAttribute("CONTROL_DECLARADOS_TIPO", meses[ParamUtil.getInteger(renderRequest, "fechaDesdeMes")]+" "+fechaDesdeAnio);
		
	}
	
	
	private void procesaDeclaradosEmpresasN1(HttpSession session,RenderRequest renderRequest){
		session.removeAttribute("CONTROL_DECLARADOS_N1_SALDO_INICIAL");
		session.removeAttribute("CONTROL_DECLARADOS_N1_MOVIMIENTOS");
		session.removeAttribute("CONTROL_DECLARADOS_N1_LEYENDA");
		session.removeAttribute("CONTROL_DECLARADOS_N1_FECHA_INICIAL");
		session.removeAttribute("CONTROL_DECLARADOS_N1_FECHA_FINAL");
		session.removeAttribute("CONTROL_DECLARADOS_N1_CUIT");
		
		SimpleDateFormat formatD = new SimpleDateFormat("yyyyMMdd");
		String cuit = ParamUtil.getString(renderRequest, "cuit",null);
		String fecha = ParamUtil.getString(renderRequest, "fechaini");
		List<EstadoInicialCuentaCorriente> saldoIni = null;
		List<CuentaCorriente> ctas = null;
		String leyenda ="";
		try {
			Date fechaIniD= formatD.parse(fecha);
			Date fechaHtaD=DateUtils.getLastDateOfMonth(fechaIniD,false);
			leyenda = "CUIT "+ cuit +" - Cuentas Corrientes - Desde: "  + DateUtils.format(fechaIniD,"dd/MM/yyyy")+ " Hasta: " + 
			DateUtils.format(fechaHtaD,"dd/MM/yyyy");

/*			
			saldoIni = ContabilidadServiceUtil
					.saldoInicialCorrienteActasYConvenios(cuit, null,
							null, fechaIniD, WebKeysGlobal.UOMA);
			
			ctas = ContabilidadServiceUtil.cuentaCorrienteActasYConvenios(
					fechaIniD, fechaHtaD, cuit, null, null, 0,
					null, WebKeysGlobal.UOMA);
*/			
			session.setAttribute("CONTROL_DECLARADOS_N1_FECHA_INICIAL", fechaIniD);
			session.setAttribute("CONTROL_DECLARADOS_N1_FECHA_FINAL", fechaHtaD);
			
		} catch (Exception e) {}
		
		session.setAttribute("CONTROL_DECLARADOS_N1_CUIT", cuit);
		
//		session.setAttribute("CONTROL_DECLARADOS_N1_SALDO_INICIAL", saldoIni);
//		session.setAttribute("CONTROL_DECLARADOS_N1_MOVIMIENTOS", ctas);
//		session.setAttribute("CONTROL_DECLARADOS_N1_LEYENDA", leyenda);
		
	}
	
}
