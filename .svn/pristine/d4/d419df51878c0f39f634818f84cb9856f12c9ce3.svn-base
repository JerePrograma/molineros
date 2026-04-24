package ar.com.ospim.liquidaciones.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;

/**
 * Este Servicio consulta si a exedido el tope de reintegro por pieza dental
 * @author Pablo
 *
 */

public class ValidarTopesProtesisPorFecha extends JSONAction {
	private static Log _log = LogFactoryUtil.getLog(ValidarTopesProtesisPorFecha.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
	    String resultado = "{}";
				
		String cuilTitular=ParamUtil.getString(req, "cuil_titular");
		int inte =  ParamUtil.getInteger(req, "inte");
		String pieza= ParamUtil.getString(req, "pieza");
		String cara=ParamUtil.getString(req, "cara");
		String diaPer=ParamUtil.getString(req, "diaPer");
		String mesPer=ParamUtil.getString(req, "mesPer");
		String anioPer=ParamUtil.getString(req, "anioPer");
		String codigo=ParamUtil.getString(req, "codigo");
		String editProtesis=ParamUtil.getString(req, "editProtesis");

		
				
		Boolean existe=false;
		Object[] topeExcedido = null;
	
		_log.debug("Ingresa a ValidarTopesProtesisPorFecha   cuil titular:"+   cuilTitular );
			
		  try {
			    topeExcedido = ReintegroServiceUtil.evaluaTopesReintegroPorFecha(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS,cuilTitular,inte,pieza,cara,diaPer,mesPer,anioPer,codigo);
			   
		  
		  }catch (Exception e) {
			_log.error(e);
		  }	
		  

		  
		  if (topeExcedido[0] != null && (Boolean)topeExcedido[0]){
			  existe = true;
			  if("1".equalsIgnoreCase(editProtesis)) {//Si estoy editando y existe y es igual no valido
				  existe = false;
			  }
			  resultado = "{ \"existeTope\" : \"" 
					    + existe 
				        + "\" }";
			
		  }else if(topeExcedido[1] != null && (Boolean) topeExcedido[1]){
			  existe = true;
			  if("1".equalsIgnoreCase(editProtesis)) {//Si estoy editando y existe y es igual no valido
				  existe = false;
			  } 
			  resultado = "{ \"existeTopeSinCodigo\" : \"" 
					    + existe 
				        + "\" }";
		  }
		
		  
		
		return resultado;
	}
	
		
}