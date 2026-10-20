package ar.com.ospim.util;

import java.util.List;
import java.util.Locale;

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
		if (user == null || role == null || role.length() == 0) {
			return false;
		}
		List<Role> roles = user.getRoles();
		if (roles == null) {
			return false;
		}
		String nombreRol = role.toUpperCase(Locale.ENGLISH);
		for (Role rol : roles) {
			if (rol != null) {
				String nombre = rol.getName();
				if (nombre != null
						&& nombreRol.equals(nombre.toUpperCase(Locale.ENGLISH))) {
					return true;
				}
			}
		}
		return false;
	}
}
