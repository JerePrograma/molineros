package ar.com.uoma.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
//import org.compass.core.util.backport.java.util.Collections;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;

public class TraerControlIngresosEgresos extends JSONAction {
	
	private static Log _log = LogFactoryUtil.getLog(TraerControlIngresosEgresos.class);
	
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
		 req.getSession().removeAttribute("CONTROL_INGRESOS");
		 req.getSession().removeAttribute("CONTROL_EGRESOS");
		 req.getSession().removeAttribute("CONTROL_DECLARADO");
		 
		 String resultado = "{}";
		 
		int entidad = ParamUtil.getInteger(req, "entidad");
		try {
			Date fechaIni = getDesde(req);
			Date fechaFin = getHasta(req);
			Empresa empresa = new Empresa("","", "");
			
			String excluye = TraeListasServiceUtil.getSystemConfig("TABLERO_INGRESOS_EGRESOS_EXCLUYE");
			String[] vexcluye = excluye.split(";");
			
			List<ItemSubdiarioIngreso> ingresos1 = ContabilidadServiceUtil
					.subdiarioIngresos(fechaIni, fechaFin, empresa,
							true, true, true, false, entidad);
			
			
			List<ItemSubdiarioIngreso> ingresos= new ArrayList<ItemSubdiarioIngreso>();
			for(ItemSubdiarioIngreso i:ingresos1){
			   boolean subir=true;	
			   for(int xi=0;xi<vexcluye.length;xi++){
				   if(i.getNumeroCuenta().equalsIgnoreCase(vexcluye[xi])){
					 subir=false;
					 break;
				   }
			   }
			   
			   if(subir){
				   ingresos.add(i);
			   }
			}
			
			Collections.sort(ingresos, new Comparator<ItemSubdiarioIngreso>() {
				public int compare(ItemSubdiarioIngreso arg0,
						ItemSubdiarioIngreso arg1) {
					int compareTo = arg0.getFecha().compareTo(arg1.getFecha());
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
					return compareTo;
				}

			});
			
			req.getSession().setAttribute("CONTROL_INGRESOS", ingresos);
			
			
			List<ItemSubdiarioEgreso> egresos1 = new ArrayList<ItemSubdiarioEgreso>();
			List<? extends ItemSubdiarioEgreso> reporte = null;
			
			reporte = OrdenPagoServiceUtil
						.reporteOrdenPagoCompletoParaSubdiario(fechaIni,
								fechaFin, true, false, false, false,
								entidad, 0);

			egresos1.addAll(reporte);
			List<? extends ItemSubdiarioEgreso> reporteParaSubdiario = MovimientoBancarioServiceUtil
						.reporteParaSubdiario(fechaIni, fechaFin, entidad);
				egresos1.addAll(reporteParaSubdiario);
				
				
				
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

			req.getSession().setAttribute("CONTROL_EGRESOS", egresos);
			
			BigDecimal totalIngresos = BigDecimal.ZERO;
			BigDecimal totalEgresos = BigDecimal.ZERO;
			BigDecimal totalDeclarado = BigDecimal.ZERO;
			BigDecimal totalAnticipos = BigDecimal.ZERO;
			
			for (ItemSubdiarioIngreso i: ingresos){
				if(i.getRazonSocial()==null || !"ANULADAMISMODIA".equalsIgnoreCase(i.getRazonSocial().trim())){
				  totalIngresos=totalIngresos.add(i.getImporte());
				}  
			}
			
			for (ItemSubdiarioEgreso i: egresos){
			
				if(i.getObservaciones()==null || !"ANULADAMISMODIA".equalsIgnoreCase(i.getObservaciones().trim())){
				  if(i.getHacia()!=null){
				    for(SubdiarioEgresoColumna fp:i.getHacia()){
					   if (fp.getImporte() != null ) {
						  totalEgresos=totalEgresos.add(fp.getImporte());
					  }		
				    }
				  }
				}  
				 
			}
			
			/*
			List<FichaBoletaPortal> boletas = PortalEmpleadoresServiceUtil.getBoletaCapitalInteresPortal(fechaIni, fechaFin); 
			for (FichaBoletaPortal i: boletas){
				 totalDeclarado=totalDeclarado.add(i.getImporte());
					 
			}
			req.getSession().setAttribute("CONTROL_DECLARADO", boletas);
			*/
			resultado = "{ \"ingresos\" : \"" 
				    + totalIngresos.setScale(2, BigDecimal.ROUND_HALF_UP) 
			        + "\",\"egresos\" : \""
			        + totalEgresos.setScale(2, BigDecimal.ROUND_HALF_UP)
			        + "\",\"declarado\" : \""
			        + totalDeclarado
			        + "\" }";
		
			
		}catch(Exception e){
			_log.error("Error al generar control ingresos-egresos", e);
		    return null;
		}	
		
		return resultado;
	}
	
	protected static Date getDesde(HttpServletRequest req) {
		return DateUtils.getFechaDesde(req);
	}

	protected static Date getHasta(HttpServletRequest req) {
		return DateUtils.getFechaHasta(req);
	}
	
}