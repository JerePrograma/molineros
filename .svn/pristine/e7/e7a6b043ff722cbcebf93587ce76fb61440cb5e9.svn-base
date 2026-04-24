package ar.com.ospim.correspondencia.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class EmpresaSectorUsuarioServiceUtil {
	
	
	public static List<EmpresaLiferay> getEmpresasSectoresUsuarios(){
		
		List<EmpresaLiferay> empresasSectoresUsuarios = new ArrayList<EmpresaLiferay>();
		EmpresaLiferay empresa = null;
		SectorLiferay sector = null;
		ArrayList<SectorLiferay> sectoresUsuarios ;
		
		try {
			List<Organization> organizaciones = OrganizationLocalServiceUtil.getOrganizations(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			
			for (Iterator<Organization> iterator = organizaciones.iterator(); iterator.hasNext();) {
				Organization org = iterator.next();
				
				empresa = new EmpresaLiferay();
				empresa.setEmpresa(org);
				sectoresUsuarios = new ArrayList<SectorLiferay>();
					
				
				List<User> usersG = UserLocalServiceUtil.getOrganizationUsers(org.getOrganizationId());
	
				ArrayList<UserGroup> ugrp = new ArrayList<UserGroup>(); 

				for (Iterator<User> iterator2 = usersG.iterator(); iterator2.hasNext();) {
					
					User user = iterator2.next();

//					List<User> users = UserLocalServiceUtil.getUserGroupUsers(user.getUser);
					
					if(user.getUserGroups() != null && user.getUserGroups().size() >0 ){
						
						for (Iterator<UserGroup> iterator3 = user.getUserGroups().iterator(); iterator3.hasNext();) {
							UserGroup ugAux = iterator3.next();
							if(!ugrp.contains(ugAux)){
								ugrp.add(ugAux);

								List<User> users = UserLocalServiceUtil.getUserGroupUsers(ugAux.getUserGroupId());
								sector = new SectorLiferay();
								sector.setSector(ugAux);
								sector.setUsuarios(users);
								
								sectoresUsuarios.add(sector);
								
							}
							
						}// fin for UserGroups
						
					}// fin if
					
				}// fin for	Users
				empresa.setSectores(sectoresUsuarios);
				
//				if(sector.getUsuarios().size() >0){
					if(empresa.getSectores().size() > 0 ){
						empresasSectoresUsuarios.add(empresa);
					}
//				}
			}// fin for	Organizations
			
			
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
	 return empresasSectoresUsuarios;	
	}

}
