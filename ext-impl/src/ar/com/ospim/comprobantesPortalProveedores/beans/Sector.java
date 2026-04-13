package ar.com.ospim.comprobantesPortalProveedores.beans;

import java.io.Serializable;
import java.util.List;

import com.liferay.portal.model.User;

import ar.com.ospim.global.beans.ClaseBase;

public class Sector extends ClaseBase implements Serializable{
   /**
	 * 
	 */
	private static final long serialVersionUID = 6839853014253997508L;
private List<User> usuariosHabilitados;

public List<User> getUsuariosHabilitados() {
	return usuariosHabilitados;
}

public void setUsuariosHabilitados(List<User> usuariosHabilitados) {
	this.usuariosHabilitados = usuariosHabilitados;
}			
   
   
}
