package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.PlanPrestacion;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PlanPrestacionServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(PlanPrestacionServiceImpl.class);

	public List<PlanPrestacion> traePlanPrestaciones(int prestacionId,
			String prestacion, int planId) {
		Connection con = null;
		CallableStatement stmt = null;
		List<PlanPrestacion> lista = null;
		try {
			String sql = "{call buscar_plan_prestacion(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (prestacionId == 0){
				stmt.setNull(1, Type.INT);
			} else {
				stmt.setInt(1, prestacionId);
			}
			stmt.setString(2, prestacion);
			stmt.setInt(3, planId);

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanPrestacion>();
			while (rs.next()) {
				PlanPrestacion planPrestacion = PlanPrestacion.getMapping(rs, "PPREST__");
				Prestacion prest = Prestacion.getMapping(rs, "PREST__");
				planPrestacion.setNomenclador(prest);
				lista.add(planPrestacion);
			}
		} catch (Exception e) {
			_log.error("Error al traer plan prestaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<PlanPrestacion> traePlanPrestaciones(String codigo,
			String prestacion, int planId) {
		Connection con = null;
		CallableStatement stmt = null;
		List<PlanPrestacion> lista = null;
		try {
			String sql = "{call buscar_plan_prestacion_cod_prest(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (codigo == null){
				stmt.setNull(1, Type.CHAR);
			} else {
				stmt.setString(1, codigo);
			}
			stmt.setString(2, prestacion);
			stmt.setInt(3, planId);

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanPrestacion>();
			while (rs.next()) {
				PlanPrestacion planPrestacion = PlanPrestacion.getMapping(rs, "PPREST__");
				Prestacion prest = Prestacion.getMapping(rs, "PREST__");
				planPrestacion.setNomenclador(prest);
				lista.add(planPrestacion);
			}
		} catch (Exception e) {
			_log.error("Error al traer plan prestaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public List<PlanPrestacion> traePlanPrestaciones(String codigo,
			String prestacion, int planId, String protesis) {
		Connection con = null;
		CallableStatement stmt = null;
		List<PlanPrestacion> lista = null;
		try {
			String sql = "{call buscar_plan_prestacion_cod_prest(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (codigo == null){
				stmt.setNull(1, Type.CHAR);
			} else {
				stmt.setString(1, codigo);
			}
			stmt.setString(2, prestacion);
			stmt.setInt(3, planId);
			stmt.setString(4, protesis);

			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanPrestacion>();
			while (rs.next()) {
				PlanPrestacion planPrestacion = PlanPrestacion.getMapping(rs, "PPREST__");
				Prestacion prest = Prestacion.getMapping(rs, "PREST__");
				planPrestacion.setNomenclador(prest);
				lista.add(planPrestacion);
			}
		} catch (Exception e) {
			_log.error("Error al traer plan prestaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public List<Prestacion> traeTipoNomencladorPrestaciones(int idTipoNomenclador, String codigoPrestacion, String descripcionPrestacion) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Prestacion> lista = null;
		try {
			String sql = "{call convenio_prest.buscar_tipo_nomenclador_prestacion(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());			
			stmt.setInt(1, idTipoNomenclador);
			stmt.setString(2, codigoPrestacion);
			stmt.setString(3, descripcionPrestacion);
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<Prestacion>();
			while (rs.next()) {
				Prestacion prest = Prestacion.getMappingSimple(rs, "PREST_");
				prest.setCodigo(rs.getString("PREST_" + "codigo"));
				lista.add(prest);
			}
		} catch (Exception e) {
			_log.error("Error al traer prestaciones por tipo nomnclador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
}
