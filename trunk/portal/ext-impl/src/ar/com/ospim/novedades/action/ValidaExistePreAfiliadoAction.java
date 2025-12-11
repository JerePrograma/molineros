package ar.com.ospim.novedades.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.novedades.service.PreAfiliadoServiceImpl;
import ar.com.ospim.novedades.service.PreAfiliadoServiceUtil;
import ar.com.ospim.util.CuilUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.JSONAction;

public class ValidaExistePreAfiliadoAction extends JSONAction {

	private static Log logger = LogFactoryUtil.getLog(ValidaExistePreAfiliadoAction.class);

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		int result=0;
		int cuitEnTramite = 0;
				
		String cuil = req.getParameter("cuil");
		
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
			result = PreAfiliadoServiceUtil.existePreAfiliado(cuil); 
			// 1 es Error o Integrante que ya existe, 2 Titular en Padron, 3 Titular en pre_afiliado
		}
		Afiliado afiTitular=null;
		PreAfiliado preAfiTitular = null;
		if(result == 2){
			afiTitular = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, 0);
			req.setAttribute("DatosAfiliadoTitular", afiTitular.getApellido().trim()+", "+afiTitular.getNombre().trim());
			
			return "{ \"validado\" : \"" + String.valueOf(result) + "\",\"apellido\" : \""
             + afiTitular.getApellido().trim()  + "\",\"nombre\" : \""
                     + afiTitular.getNombre().trim()+ "\" }";
		}
		if(result == 3){
			preAfiTitular = PreAfiliadoServiceUtil.getInstance().buscarPreAfiliado(cuil, 0, 0);
			String r = "{ \"validado\" : \"" + String.valueOf(result) + "\",\"apellido\" : \""
            + preAfiTitular.getApellido().trim()  + "\",\"nombre\" : \""
                    + preAfiTitular.getNombre().trim()+ "\" }";
			logger.debug(r);
			return r;
		}
		
		return "{ \"validado\" : \"" + String.valueOf(result) + "\"}";
	}
}