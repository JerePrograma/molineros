package ar.com.ospim.afiliados.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.util.CuilUtils;

import com.liferay.portal.struts.JSONAction;

public class ValidaCuilAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int result=0;
		int existe=0;
		int cuitEnTramite = 0;
		
		String cuil = req.getParameter("cuil");
		int inte = Integer.parseInt(req.getParameter("inte"));
		
		String vigenteFechaDia = req.getParameter("vigenteFechaDia");
		String vigenteFechaMes = req.getParameter("vigenteFechaMes");
		String vigenteFechaAnio = req.getParameter("vigenteFechaAnio");
		
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");
		Date vigenFecha = null;
		try {
			vigenFecha = formatoDeFechaV.parse(vigenteFechaDia + "/"
						+ (Integer.parseInt(vigenteFechaMes) ) + "/"
						+ vigenteFechaAnio);
		} catch (Exception e) {
			vigenFecha = null;
		}
		

		
		
		//si es recien naciodo
		
		String nacimientoFechaDia = req.getParameter("diaNac");
		String nacimientoFechaMes = req.getParameter("mesNac");
		String nacimientoFechaAnio = req.getParameter("anioNac");
		
		Date nacimientoFecha = null;
		
		int dias = 0;
		
		try {
			nacimientoFecha = formatoDeFechaV.parse(nacimientoFechaDia + "/"
						+ (Integer.parseInt(nacimientoFechaMes) ) + "/"
						+ nacimientoFechaAnio);
			
			dias=(int) ((new Date().getTime() - nacimientoFecha.getTime())/86400000);
			
		} catch (Exception e) {
			nacimientoFecha = null;
		}
		
		int parentesco = 0;
		
		try{
			parentesco=	Integer.parseInt(req.getParameter("parentesco"));
		}catch(Exception e){}  

//		esta excepción es para evitar validar el cuil para lso bebes, xq en anses no daba dni por la cuarentena a los recién nacidos
		if (inte > 0 && (parentesco == 3 || parentesco == 5) && dias < 365 ){
			return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
		}

		
		
		/* CUIT 'en trámite' */
		cuitEnTramite = CuilUtils.validarCUITEnTramite(cuil); 
		switch (cuitEnTramite) {
		case 1: // 1 si el cuit empieza con 000, y valida correctamente, 
			return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
		case 2: // 2 si empieza con 0 pero no valida o no es valido x otra razon (long <> 11 o no numerico)
			result = 1;
			return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
		default:  // 0 no correspondia validar CUIT en trámite
			break;
		}
		
		/* CUIT 'comunes' */
		boolean cuilValido=CuilUtils.validarNum(cuil);		
		if(!cuilValido){
			result=1;
		}else{
			if(inte==0){
				existe=EditarAfiliadoServiceUtil.existeAfiliadoTitular(cuil,vigenFecha);
			}else{
				existe=EditarAfiliadoServiceUtil.existeAfiliado(cuil, vigenFecha);
			}
		}
		if(existe==1){
			result=2;
		}
		
		return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
	}
}