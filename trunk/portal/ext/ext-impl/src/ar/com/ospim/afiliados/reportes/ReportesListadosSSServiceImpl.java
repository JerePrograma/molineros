package ar.com.ospim.afiliados.reportes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesListadosSSServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(ReportesListadosSSServiceImpl.class);
	private static int ALTAS = 1;
	private static int BAJAS = 2;

	public List<String> getReporteListadoSSAlta(Date fecha_desde, 
			boolean registrar) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  listado_super_altas_bajas(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_desde.getTime()));
			stmt.setInt(2, ALTAS);
			stmt.setBoolean(3, registrar);
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("obra_social"));
				linea.append("|");
				linea.append(rs.getString("cuit"));
				linea.append("|");
				linea.append(rs.getString("cuil_titular"));
				linea.append("|");
				linea.append(rs.getString("parentesco"));
				linea.append("|");
				linea.append(rs.getString("cuil"));
				linea.append("|");
				linea.append(rs.getString("documento_tipo"));
				linea.append("|");
				linea.append(rs.getString("docu_numero"));
				linea.append("|");
				linea.append(rs.getString("ape_nombre"));
				linea.append("|");
				linea.append(rs.getString("sexo"));
				linea.append("|");
				linea.append(rs.getString("estado_civil"));
				linea.append("|");
				linea.append(rs.getString("naci_fecha"));
				linea.append("|");
				linea.append(rs.getString("nacionalidad"));
				linea.append("|");
				linea.append(rs.getString("calle"));
				linea.append("|");
				linea.append(rs.getString("numero"));
				linea.append("|");
				linea.append(rs.getString("piso"));
				linea.append("|");
				linea.append(rs.getString("depto"));
				linea.append("|");
				linea.append(rs.getString("localidad"));
				linea.append("|");
				linea.append(rs.getString("postal_codi"));
				linea.append("|");
				linea.append(rs.getString("provincia"));
				linea.append("|");
				linea.append(rs.getString("tipo_domi"));
				linea.append("|");
				linea.append(rs.getString("telefono"));
				linea.append("|");
				linea.append(rs.getString("situ_revista"));
				linea.append("|");
				linea.append(rs.getString("discapacitado"));
				linea.append("|");
				linea.append(rs.getString("tipo_beneficiario"));
				linea.append("|");
				linea.append(rs.getString("fecha_alta_os"));
				linea.append("|");
				linea.append(rs.getString("fecha_cierre"));				
				result.add(linea.toString());
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;
	}

	public List<String> getReporteListadoSSBaja(Date fecha_desde,
			boolean registrar) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  listado_super_altas_bajas(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_desde.getTime()));
			stmt.setInt(2, BAJAS);
			stmt.setBoolean(3, registrar);
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("obra_social"));
				linea.append("|");
				linea.append(rs.getString("cuit"));
				linea.append("|");
				linea.append(rs.getString("cuil_titular"));
				linea.append("|");
				linea.append(rs.getString("parentesco"));
				linea.append("|");
				linea.append(rs.getString("cuil"));
				linea.append("|");
				linea.append(rs.getString("documento_tipo"));
				linea.append("|");
				linea.append(rs.getString("docu_numero"));
				linea.append("|");
				linea.append(rs.getString("ape_nombre"));
				linea.append("|");
				linea.append(rs.getString("sexo"));
				linea.append("|");
				linea.append(rs.getString("estado_civil"));
				linea.append("|");
				linea.append(rs.getString("naci_fecha"));
				linea.append("|");
				linea.append(rs.getString("nacionalidad"));
				linea.append("|");
				linea.append(rs.getString("calle"));
				linea.append("|");
				linea.append(rs.getString("numero"));
				linea.append("|");
				linea.append(rs.getString("piso"));
				linea.append("|");
				linea.append(rs.getString("depto"));
				linea.append("|");
				linea.append(rs.getString("localidad"));
				linea.append("|");
				linea.append(rs.getString("postal_codi"));
				linea.append("|");
				linea.append(rs.getString("provincia"));
				linea.append("|");
				linea.append(rs.getString("tipo_domi"));
				linea.append("|");
				linea.append(rs.getString("telefono"));
				linea.append("|");
				linea.append(rs.getString("situ_revista"));
				linea.append("|");
				linea.append(rs.getString("discapacitado"));
				linea.append("|");
				linea.append(rs.getString("tipo_beneficiario"));
				linea.append("|");
				linea.append(rs.getString("fecha_alta_os"));
				linea.append("|");
				linea.append(rs.getString("fecha_cierre"));
				result.add(linea.toString());
			}
		}catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;
	}

	public List<String> getReporteListadoSSModificaciones(Date fecha_desde,
			Date fecha_hasta, boolean registrar) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call  listado_super_modificaciones(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_desde.getTime()));
			stmt.setDate(2, new java.sql.Date(fecha_hasta.getTime()));
			stmt.setBoolean(3, registrar);
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("obra_social"));
				linea.append("|");
				linea.append(rs.getString("cuit"));
				linea.append("|");
				linea.append(rs.getString("cuil_titular"));
				linea.append("|");
				linea.append(rs.getString("parentesco"));
				linea.append("|");
				linea.append(rs.getString("cuil"));
				linea.append("|");
				linea.append(rs.getString("documento_tipo"));
				linea.append("|");
				linea.append(rs.getString("docu_numero"));
				linea.append("|");
				linea.append(rs.getString("ape_nombre"));
				linea.append("|");
				linea.append(rs.getString("sexo"));
				linea.append("|");
				linea.append(rs.getString("estado_civil"));
				linea.append("|");
				linea.append(rs.getString("naci_fecha"));
				linea.append("|");
				linea.append(rs.getString("nacionalidad"));
				linea.append("|");
				linea.append(rs.getString("calle"));
				linea.append("|");
				linea.append(rs.getString("numero"));
				linea.append("|");
				linea.append(rs.getString("piso"));
				linea.append("|");
				linea.append(rs.getString("depto"));
				linea.append("|");
				linea.append(rs.getString("localidad"));
				linea.append("|");
				linea.append(rs.getString("postal_codi"));
				linea.append("|");
				linea.append(rs.getString("provincia"));
				linea.append("|");
				linea.append(rs.getString("tipo_domi"));
				linea.append("|");
				linea.append(rs.getString("telefono"));
				linea.append("|");
				linea.append(rs.getString("situ_revista"));
				linea.append("|");
				linea.append(rs.getString("discapacitado"));
				linea.append("|");
				linea.append(rs.getString("tipo_beneficiario"));
				linea.append("|");
				linea.append(rs.getString("fecha_alta_os"));
				linea.append("|");
				linea.append(rs.getString("fecha_cierre"));
				result.add(linea.toString());
			}
		}catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;
	}
	
	public List<String> getReporteSistemaViejoUoma(Date fechaArchivo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<String> result = null;
		try {
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  salida_para_viejo_sist_uoma(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaArchivo.getTime()));
			ResultSet rs = stmt.executeQuery();
			result = new ArrayList<String>();
			while (rs.next()) {
				StringBuffer linea = new StringBuffer();
				linea.append(rs.getString("banco"));
				linea.append(rs.getString("sucu_banco"));
				linea.append(rs.getString("cuit"));
				linea.append(ar.com.ospim.util.StringUtils.replaceAcutesAndEnies(rs.getString("razon_soc")));
				linea.append(rs.getString("periodo_barras"));
				linea.append(rs.getString("nro_boleta"));
				linea.append(rs.getString("fecha_recauda"));
				linea.append(rs.getString("fecha_rendicion"));
				linea.append(rs.getString("importe"));
				linea.append(rs.getString("bco_cheque"));
				linea.append(rs.getString("sucursal_cheque"));
				linea.append(rs.getString("nro_cheque"));
				linea.append(rs.getString("concepto"));
				linea.append(rs.getString("cant_afiliados"));
				linea.append(rs.getString("total_remu"));
				linea.append(rs.getString("acta_acuerdo"));
				linea.append(ar.com.ospim.util.StringUtils.replaceAcutesAndEnies(rs.getString("observaciones")));
				result.add(linea.toString());
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);

		}
		return result;
	}
}
