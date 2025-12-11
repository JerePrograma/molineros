package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.List;

import ar.com.ospim.afiliados.beans.SituacionLaboral;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="GrabaSituLaboralServiceUtil.java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.GrabaSituLaboralServiceUtil</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceImpl
 * 
 */
public class SituLaboralServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(SituLaboralServiceUtil.class);
	private static SituLaboralServiceImpl instance = null;

	public static SituLaboralServiceImpl getInstance() {
		if (null == instance) {
			instance = new SituLaboralServiceImpl();
		}
		return instance;
	}

	public static void grabaSituLaboral(SituacionLaboral situLaboralAdd,
			String user, Connection con) throws Exception {
		getInstance().grabaSituLaboral(situLaboralAdd, user, con);
	}

	public static void editaSituLaboral(SituacionLaboral situLaboralUpdate,
			String user, Connection con) throws Exception {
		getInstance().editaSituLaboral(situLaboralUpdate, user, con);
	}

	public static void borraSituLaboral(SituacionLaboral situLaboralDelete,
			User user, Connection con) throws Exception {
		getInstance().borraSituLaboral(situLaboralDelete, user, con);
	}

	public static List<SituacionLaboral> buscaSituLaboral(String cuil,
			int inte, Connection con) throws Exception {
		return getInstance().buscaSituLaboral(cuil, inte, con);
	}

	public static List<SituacionLaboral> buscaSituLaboral(String cuil, int inte)
			throws Exception {
		Connection con = null;
		List<SituacionLaboral> lista = null;
		try {
			con = ConnectionHelper.getConnection();
			lista = buscaSituLaboral(cuil, inte, con);
		} catch (Exception e) {
			_log.debug("Error al buscar situ laboral!");
		} finally {
			ConnectionHelper.cerrar(con);
		}
		return lista;
	}

	public static void editarSituLaboral(List<SituacionLaboral> situLaborales,
			String user, Connection connectionParameter) throws Exception {
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			if (situLaborales.size() != 0) {
				SituacionLaboral situLaboral = null;
				for (int i = 0; i < situLaborales.size(); i++) {
					situLaboral = situLaborales.get(i);
					if (situLaboral.getEstado() != null) {
						if (situLaboral.getEstado().equals("add")) {
							SituLaboralServiceUtil.grabaSituLaboral(
									situLaboral, user, con);
						} else if (situLaboral.getEstado().equals("update")) {
							SituLaboralServiceUtil.editaSituLaboral(
									situLaboral, user, con);
						}
					}
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.debug("Error al grabar situ laboral!", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			} else {
				throw e;
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}
}
