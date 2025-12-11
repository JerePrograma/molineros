package ar.com.ospim.correspondencia.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.JSONAction;

public class OrganizationGroupAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		long idOrg =0;
		String idOrganiz = req.getParameter("organizatioId");
		if(idOrganiz !=null && idOrganiz != ""){
			idOrg = Long.valueOf(idOrganiz);
		}
					
		List<User> usersG = UserLocalServiceUtil.getOrganizationUsers(idOrg);
//		List<UserGroup> grupos = UserGroupLocalServiceUtil.getUserGroups(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		
		ArrayList<UserGroup> ugrp = new ArrayList<UserGroup>(); 

		for (Iterator<User> iterator = usersG.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			if(user.getUserGroups() != null && user.getUserGroups().size() >0 ){
				for (Iterator<UserGroup> iterator2 = user.getUserGroups().iterator(); iterator2.hasNext();) {
					UserGroup ugAux = iterator2.next();
					if(!ugrp.contains(ugAux)){
						ugrp.add(ugAux);
					}
				}
			}
		}	

//		Collections.sort(usersG, new Comparator() {
//			@Override
//			public int compare(Object o1, Object o2) {
//				User o11 = (User) o1;
//				User o22 = (User) o2;
//				
//				return o11.getFullName().compareTo(o22.getFullName()) ;
//			}
//		});
		Collections.sort(ugrp, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				UserGroup o11 = (UserGroup) o1;
				UserGroup o22 = (UserGroup) o2;
				
				return o11.getName().compareTo(o22.getName()) ;
			}
		});
		
		String json = "{\"listaFiltrada\": ["; 
		json += "\""+""+"|"+"Seleccione un sector"+"\"" + ",";
		for (Iterator<UserGroup> iterator = ugrp.iterator(); iterator.hasNext();) {
			UserGroup g = iterator.next();
			json += "\""+g.getUserGroupId()+"|"+g.getName()+"\"" + ",";
		}

		int count = json.length();
		String result = json.substring(0, count/*-1*/);
		result += "]}";
		return result;
	}
	
}