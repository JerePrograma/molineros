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
		String nombreRol = normalizarNombreRol(role);
		for (Role rol : roles) {
			if (rol != null) {
				String nombre = rol.getName();
				if (nombre != null
						&& nombreRol.equals(normalizarNombreRol(nombre))) {
					return true;
				}
			}
		}
		return false;
	}
	public static boolean rolesEquivalentes(String role, String otroRole) {
		if (role == null || role.length() == 0
				|| otroRole == null || otroRole.length() == 0) {
			return false;
		}
		return normalizarNombreRol(role).equals(normalizarNombreRol(otroRole));
	}

	private static String normalizarNombreRol(String role) {
		String nombre = role.toUpperCase(Locale.ENGLISH);
		if (nombre.startsWith("COMPRAS_")) {
			String tipo = nombre.substring("COMPRAS_".length());
			if ("VIEW".equals(tipo) || "ABM".equals(tipo)
					|| "ANULAR".equals(tipo) || "COTIZAR".equals(tipo)
					|| "AUTORIZAR".equals(tipo) || "GESTION".equals(tipo)) {
				return tipo + "_COMPRAS";
			}
		}
		return nombre;
	}

}
