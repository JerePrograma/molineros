package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afiliados.beans.HistoricoMovimientoAfiliado;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;

public class HistoricoMovimientoServiceImpl {

	public HistoricoMovimientoServiceImpl() {
	}

	public List<HistoricoMovimientoAfiliado> buscaHistoricoMovimientoAfiliado(
			String cuil_titular, java.util.Date fecha_desde,
			java.util.Date fecha_hasta) throws SQLException, SystemException {
		Connection con;
		List<HistoricoMovimientoAfiliado> historico = new ArrayList<HistoricoMovimientoAfiliado>();
		CallableStatement stmt;
		con = null;
		stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sqlList = "{call reporte_modificaciones_periodo(?,?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil_titular);
			if (fecha_desde != null) {
				stmt.setDate(2, new java.sql.Date(fecha_desde.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			if (fecha_hasta != null) {
				stmt.setDate(3, new java.sql.Date(fecha_hasta.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			HistoricoMovimientoAfiliado histoMovListUltimo = null;
			for (; rs.next(); historico.add(histoMovListUltimo)) {
				histoMovListUltimo = new HistoricoMovimientoAfiliado();
				histoMovListUltimo.setCuil_titular(rs.getString("cuil_titular"));
				histoMovListUltimo.setInte(rs.getInt("inte"));
				histoMovListUltimo.setParentesco(rs.getString("parentesco"));
				histoMovListUltimo
						.setNro_documento(rs.getString("docu_numero"));
				histoMovListUltimo.setApellido(rs.getString("apellido"));
				histoMovListUltimo.setNombre(rs.getString("nombre"));
				histoMovListUltimo.setModificacion(rs.getString("cambio"));
				histoMovListUltimo.setValor_anterior(rs.getString("anterior"));
				histoMovListUltimo.setValor_actual(rs.getString("ahora"));
				histoMovListUltimo.setUsuario(rs.getString("usuario"));
				histoMovListUltimo.setFecha_modificacion(rs
						.getTimestamp("fecha"));
				try {
					histoMovListUltimo.setDiscapacitado(rs.getString("discapacitado"));
				}catch(Exception e) {
					
				}
			}
		} catch (Exception e) {
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		Collections.sort(historico,
				new Comparator<HistoricoMovimientoAfiliado>() {
					public int compare(HistoricoMovimientoAfiliado o1,
							HistoricoMovimientoAfiliado o2) {
						return o1.getFecha_modificacion().compareTo(
								o2.getFecha_modificacion());
					}

				});
		return historico;
	}

}
