package ar.com.ospim.correspondencia.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;

public class SectorLiferay {

	private Logger _log = Logger.getLogger(this.getClass());
	
	UserGroup sector ;
	List<User> usuarios ;
	
	public UserGroup getSector() {
		return sector;
	}
	public void setSector(UserGroup sector) {
		this.sector = sector;
	}
	public List<User> getUsuarios() {
		
		try{
			Collections.sort(usuarios, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					User o11 = (User) o1;
					User o22 = (User) o2;
					
					return o11.getFullName().compareTo(o22.getFullName()) ;
				}
			});
		}catch (java.lang.UnsupportedOperationException e) {
//			_log.error(e);
		}
		return usuarios;
	}
	public void setUsuarios(List<User> usuarios) {
		this.usuarios = usuarios;
	}

}
