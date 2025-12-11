package ar.com.ospim.novedades.service;

import java.util.List;

import ar.com.ospim.afiliados.DuplicateAfiliadoIdException;
import ar.com.ospim.novedades.beans.BusquedaPreAfiliadosFiltro;
import ar.com.ospim.novedades.beans.PreAfiliado;
import ar.com.ospim.novedades.beans.PreAfiliadoTotal;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class PreAfiliadoServiceUtil {

	private static Log logger = LogFactoryUtil.getLog(PreAfiliadoServiceUtil.class);

	private static PreAfiliadoServiceImpl instance = null;

	public static PreAfiliadoServiceImpl getInstance() {
		if (null == instance) {
			instance = new PreAfiliadoServiceImpl();
		}
		return instance;
	}
	
	public static int insertaPreAfiliadoEntry(PreAfiliado preAfi, User user)
			throws SystemException, DuplicateAfiliadoIdException {
		
		String organizacionId = user.getOrganizations().size()>0?String.valueOf(user.getOrganizations().get(0).getOrganizationId()):"11337"; 
		int idEmpresa = Integer.parseInt(organizacionId);
				
		return getInstance().insertaPreAfiliadoEntry(preAfi, user.getScreenName(), idEmpresa);
			
	}
	
	public static void actualizaPreAfiliadoEntry(PreAfiliado preAfi, User user)
			throws SystemException {
		
		getInstance().actualizaPreAfiliado(preAfi, user.getScreenName());
			
	}
	
	public static List<PreAfiliadoTotal> getBusquedaPreAfiliados(BusquedaPreAfiliadosFiltro filtro) throws SystemException {
		
		return getInstance().buscarPreCargaAfiliados(filtro);
		
	}
	
	public static int existePreAfiliado(String cuil) throws SystemException {
		
		return getInstance().existePreAfiliado(cuil);
		
	}
	
	public static void borrarPreAfiliado(String cuil, int inte, int id, boolean esCascada, User user) throws SystemException {
		
		getInstance().borrarPreAfiliado(cuil, inte, id, esCascada, user.getScreenName());
		
	}

	public static PreAfiliadoTotal buscarPreAfiliado(String cuil_titular, int inte, Integer idPreAfi) throws SystemException {
		
		return getInstance().buscarPreAfiliado(cuil_titular, inte, idPreAfi);
		
	}
		
}
