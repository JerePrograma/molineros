package ar.com.ospim.correspondencia.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.JSONAction;

public class GroupUserAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		long idUG = 0;
		String idUserGrp = req.getParameter("usergroupId");
		boolean esBusqueda = ParamUtil.getBoolean(req, "esBusqueda");
		boolean esEntrada = ParamUtil.getBoolean(req, "esEntrada");
		
		if(idUserGrp != null && idUserGrp != ""){
			idUG = Long.valueOf(idUserGrp);
		}
		List<User> users = UserLocalServiceUtil.getUserGroupUsers(idUG);

		Collections.sort(users, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				User o11 = (User) o1;
				User o22 = (User) o2;
				
				return o11.getFullName().compareTo(o22.getFullName()) ;
			}
		});
		String json = "{\"listaFiltrada\": ["; 
		json += "\""+""+"|"+"Seleccione un usuario"+"\"" + ",";
		if(esEntrada || esBusqueda){
			json += "\""+"TODOS"+"|"+"A todos los usuarios"+"\"" + ",";
		}
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User u = iterator.next();
			if(esBusqueda || (u.isActive() && u.getActive())){
			json += "\""+u.getScreenName()+"|"+u.getFullName()+"\"" + ",";
//			json += "\""+u.getUserId()+"|"+u.getFullName()+"\"" + ",";
			}
		}

		int count = json.length();
		String result = json.substring(0, count/*-1*/);
		result += "]}";
		return result;
	}
	
}