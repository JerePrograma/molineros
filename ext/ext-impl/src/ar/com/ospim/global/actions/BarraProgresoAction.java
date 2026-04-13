package ar.com.ospim.global.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BarraProgresoAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {		
		Integer aleatorio=ParamUtil.getInteger(req, "rnd");
		Integer progreso=(Integer)req.getSession().getAttribute("progreso"+aleatorio);
		Integer totalProgreso=(Integer)req.getSession().getAttribute("totalProgreso"+aleatorio);
		System.out.println("PROGRESO: "+progreso+" ALEATORIO:"+aleatorio);
		if(null!=progreso){
			return "{ \"progreso"+aleatorio+"\" : \"" + progreso.intValue() + "\",\"totalProgreso"+aleatorio+"\" : \"" +totalProgreso.intValue()+"\"}";			
		}else{
			return "{ \"progreso"+aleatorio+"\" : \"" + 0 + "\"}";
		}
	}

}
