package ar.com.ospim.autorizaciones.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.afiliados.beans.AfiObservacion;
import ar.com.ospim.afiliados.services.AfiObservacionServiceUtil;

public class AfiliadoEvaluaObservacionesInternasAction extends JSONAction {
	
	private Logger _log = Logger.getLogger(this.getClass());

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		String resultado = "{}";
		boolean res= false;

		try {
			String cuilTitular = req.getParameter("cuil_titular");
			String inte = req.getParameter("inte");

			List<AfiObservacion> obsInternasGrupoFliar =  AfiObservacionServiceUtil.getObservaciones(cuilTitular, Integer.parseInt(inte));
			
			if(obsInternasGrupoFliar != null && obsInternasGrupoFliar.size() > 0) {
				res = true;
			}
			
		} catch (Exception e) {
			_log.debug(e.getMessage());
		}
	
 		resultado = "{ \"tieneObsInternas\" : \"" + res + "\" }";
		
		return resultado;
		
		
	}
}