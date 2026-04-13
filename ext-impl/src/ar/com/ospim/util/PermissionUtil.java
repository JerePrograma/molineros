package ar.com.ospim.util;

import java.util.List;

import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

/**
 * <a href="PermissionUtil.java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * Esta clase provee métodos para la ayuda en la manipulación de los roles de un usuario
 * </p>
 *
 * @author Federico Brachi
 *
 * @see ar.com.ospim.afiliados.PermissionUtil.PermissionUtil
 *
 */
public class PermissionUtil {
	
	public static boolean userContainsRole(User user,String role) {
		if(null!=user){
			List<Role> roles=user.getRoles();
			for (Role rol : roles){
				if (rol.getName().equals(role)){
					return true;
				}
			}
		}
		return false;
				
	}
}
