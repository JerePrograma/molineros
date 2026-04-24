package ar.com.ospim.tesoreria.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class TraerBoletaPortalEmpleadoresJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		SimpleDateFormat fdp = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String cuit = req.getParameter("cuit");
		
		String sucursal = req.getParameter("sucursal");
		
		String comproNro =req.getParameter("concepto_id");
		
		Integer conceptoId=null;
		try{
			conceptoId = Integer.parseInt(comproNro);
		}catch(NumberFormatException e){
			conceptoId=Integer.parseInt(comproNro.substring(0, comproNro.indexOf("_")));
			comproNro=comproNro.substring(comproNro.indexOf("_")+1,comproNro.length()-1);
		}
		Integer nroBoleta=Integer.parseInt(req.getParameter("nroBoleta"));
		
		
		String periodoMesAnio =req.getParameter("periodo");
		Date fechaPeriodo=null;
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
		
		
		
		Integer tipoBoleta=null;
		tipoBoleta=WebKeysTesoreria.PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS.get(conceptoId);
		List<FichaBoletaPortal>list =PortalEmpleadoresServiceUtil.getBoletasPorSecuencia(cuit, sucursal, tipoBoleta, nroBoleta);
		
		String periodo ="";
		Double capital =0D;
		Double interes =0D;
		Double ajustes=0D;
		Integer cantidadEmpleados=0;
		Double remuneracion=0D;
		Double totalboleta=0D;
		
		Boolean inexistente=true;
		Boolean pagado=false;
		Boolean periodoCorrecto=false;
		Integer secuenciaddjj=-1;
		
		
		if(!list.isEmpty()) {
			inexistente=false;
			FichaBoletaPortal bp = list.get(0);
			periodo=fdp.format(bp.getPeriodo_cod_barras());
			capital=bp.getCapital()!=null?bp.getCapital().doubleValue()*100:0D;
			interes=bp.getInteres()!=null?bp.getInteres().doubleValue()*100:0D;
			ajustes=bp.getAjusteCapital()!=null?bp.getAjusteCapital().doubleValue()*100:0D;
			cantidadEmpleados=bp.getCantidad();
			remuneracion=bp.getRemuneracion()!=null?bp.getRemuneracion().doubleValue()*100:0D;
			totalboleta=capital+interes+ajustes;
			
			if(bp.getFecha_recauda()!=null) pagado=true;
			secuenciaddjj=bp.getNro_secuendia_ddjj_portal_emple();
			
			if(fechaPeriodo!=null && formatoDePeriodos.format(fechaPeriodo).equalsIgnoreCase(list.get(0).getPeriodoAsString())){
				periodoCorrecto=true;
			}
		}
		
		String resultado = "{ \"inexistente\" :\""  
				    + inexistente 
				    + "\",\"secuenciaddjj\" : \"" 
				    + secuenciaddjj 
				    + "\",\"periodocorrecto\" : \"" 
				    + periodoCorrecto 
				    + "\",\"pagado\" : \"" 
				    + pagado
				    + "\",\"periodo\" : \"" 
				    + periodo 
				    + "\",\"capital\" : \""
			        + capital
			        + "\",\"interes\" : \""
			        + interes
			        + "\",\"ajustes\" : \""
			        + ajustes
			        + "\",\"empleados\" : \""
			        + cantidadEmpleados
			        + "\",\"remuneracion\" : \""
			        + remuneracion
			        + "\",\"totalboleta\" : \""
			        + totalboleta+ "\" }";
		
		return resultado;
	}

}