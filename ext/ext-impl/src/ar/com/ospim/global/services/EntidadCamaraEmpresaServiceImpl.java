package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.global.beans.EntidadCamaraEmpresa;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="EntidadCamaraEmpersaServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class EntidadCamaraEmpresaServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(EntidadCamaraEmpresaServiceImpl.class);

	private static EntidadCamaraEmpresaServiceImpl instance = null;

	public static EntidadCamaraEmpresaServiceImpl getInstance() {
		if (null == instance) {
			instance = new EntidadCamaraEmpresaServiceImpl();
		}
		return instance;
	}

	public List<EntidadCamaraEmpresa> getEntidadesCamaraEmpresa() throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<EntidadCamaraEmpresa> listaEntidades = null;
		try {
			String sql = "{call trae_entidad_camara_empresa()}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			listaEntidades = new ArrayList<EntidadCamaraEmpresa>();
			while (rs.next()) {
				EntidadCamaraEmpresa pos = EntidadCamaraEmpresa.getMapping(rs);
				listaEntidades.add(pos);
			}
		} catch (Exception e) {
			_log.error("Error al buscar entidades camara empresa", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEntidades;
	}
}
