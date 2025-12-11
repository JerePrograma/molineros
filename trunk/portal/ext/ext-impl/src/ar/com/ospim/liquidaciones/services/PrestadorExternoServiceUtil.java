package ar.com.ospim.liquidaciones.services;

import java.util.List;

import ar.com.ospim.liquidaciones.DuplicatePrestadorExternoIdException;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.beans.PrestadorExterno;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

public class PrestadorExternoServiceUtil {

	private static PrestadorExternoServiceImpl instance = null;

	public static PrestadorExternoServiceImpl getInstance() {
		if (null == instance) {
			instance = new PrestadorExternoServiceImpl();
		}
		return instance;
	}

	public static List<PrestadorExterno> getPrestadores(int id, String tipo_matricula, String numero_matricula, String descripcion, String cuit)
			throws Exception {
		return getInstance().getPrestadores(id, tipo_matricula, numero_matricula, descripcion, cuit);
	}
	
	public static PrestadorExterno getPrestadorExterno(int id) {
		return getInstance().getPrestadorExterno(id);
	}

	public static int save(String cuit, String desc, int iva, String matriculaTipo, 
			int matriculaNro, int matriculaProvincia, String matriculaCategoria, User user)
			throws DuplicatePrestadorIdException, SystemException, DuplicatePrestadorExternoIdException {
		return getInstance().save(cuit, desc, user.getScreenName(), iva,
				matriculaTipo, matriculaNro, matriculaProvincia, matriculaCategoria);
	}

	public static void update(int id_prestador_ext, String cuit, String desc,
			int iva, String matriculaTipo, int matriculaNro, int matriculaProvincia, String matriculaCategoria,
			User user)
			throws SystemException {
		getInstance().update(id_prestador_ext, cuit, desc, user.getScreenName(), iva, matriculaTipo,
				matriculaNro, matriculaProvincia, matriculaCategoria);
	}
//	public static void borrar(int id, User user)
//			throws ImposibleBorrarPrestadorException, SQLException {
//		getInstance().borrar(id, user.getScreenName());
//
//	}

}
