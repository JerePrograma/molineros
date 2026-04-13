package ar.com.ospim.afiliados.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.services.AfiOpcionSSUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class VolverAtrasExportacionSSSAction extends JSONAction {

	private static Log logger = LogFactoryUtil.getLog(VolverAtrasExportacionSSSAction.class);

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String resultadoFinal = "";
		int result=0;
		User user = PortalUtil.getUser(req);
		
		String paraCalcular = ParamUtil.getString(req, "calcular");
		String paraProcesar = ParamUtil.getString(req, "procesar");
		
		if(paraCalcular != null && paraCalcular.equalsIgnoreCase("true")){
			
			logger.debug("Consultando para volver atrás la exportación de la SSS");
			
			
			
			result = AfiOpcionSSUtil.verificaCantidadFormulariosOpcionExportadosSSS();
			
			resultadoFinal = "{ \"cantidad\" : \"" + String.valueOf(result) + "\"}";
		}
		
		if(paraProcesar != null && paraProcesar.equalsIgnoreCase("true")){
			
			logger.debug("Procesando volver atrás la exportación de la SSS");
			logger.debug("Usuario: " + user.getScreenName());
			
			result = AfiOpcionSSUtil.volverAtrasFormulariosOpcionExportadosSSS();
			
			resultadoFinal = "{ \"resultado \" : \"" + String.valueOf(result) + "\"}";
			
		}

		return resultadoFinal;
	}
}