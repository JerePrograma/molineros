package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.services.AfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.util.StringUtils;

public class AfiliadoEvaluaPermanenciaAction extends JSONAction {
	private Logger _log = Logger.getLogger(this.getClass());

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		String resultado = "{}";
		String res="false";
 		String aviso="";
 		String color="";
 		Integer difDia=0;

		
		try {
			String cuil = req.getParameter("cuil");
			String inte = req.getParameter("inte");
			String amarillo ="yellow";
			String rojo="red";
			String naranja="orange";
			Date hoy= new Date();
			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
			String fechaOspimDia = ParamUtil.getString(req,"fechaOspimDia");
			String fechaOspimMes = ParamUtil.getString(req,"fechaOspimMes");
			String fechaOspimAnio = ParamUtil.getString(req,"fechaOspimAnio");
			Date fechaOspim = null;
			try {
				fechaOspim = formatoDeFechas.parse(fechaOspimDia + "/"
						+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
						+ fechaOspimAnio);
			} catch (Exception e) {
				fechaOspim = null;
			}
			
		
	 		
	 		 difDia=AfiliadoServiceUtil.permanenciaDesdeUltimoLaboral(cuil, 0, "8,10,12",fechaOspim);
	 		 if(difDia>0 && difDia<181) {
	 		 	   res="true";
				   color=amarillo;
				   aviso="Afiliado tiene permanencia menor a 6 meses";
				   if(difDia<91) {
					   color=rojo;
					   aviso="Afiliado tiene permanencia menor a 3 meses";
				   }
	 		 }else if(difDia<0) {
	 			res="true";
				color=amarillo;
				aviso="Afiliado sin Situación Laboral vigente";
	 		 }
			   
	 	
	 		List<AfiSuspencionCobertura> suspCoberMedica = null;
			suspCoberMedica = PlanServiceUtil.getSuspencionesCobMedicaBeneficiario(cuil, Integer.parseInt(inte));
				
			if(suspCoberMedica!=null && suspCoberMedica.size()>0) {
			   AfiSuspencionCobertura ascm = suspCoberMedica.get(0);
					if(ascm.getVigenDesde().before(fechaOspim) 
							&& (ascm.getVigenHasta() == null 
							|| ascm.getVigenHasta().after(fechaOspim) ) ) {
						
						res="true";
						color=naranja;
						aviso="El Afiliado tiene suspendida la cobertura médica";
					}
			}
				
			 
	 		 
	 		 
			
		} catch (Exception e) {
			_log.debug(e.getMessage());
		}
	
 		resultado = "{ \"mostrarAviso\" : \"" 
				    + res 
				    + "\",\"aviso\" : \""
				    + aviso
				    + "\",\"color\" : \""
				    + color
			        + "\" }";
		
		return resultado;
		
		
	}
}