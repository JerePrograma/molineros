package ar.com.uoma.paritarias.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hsqldb.Types;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.EscalaSueldosBasicos;
import ar.com.uoma.beans.Paritaria;

public class ParitariasServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(ParitariasServiceImpl.class);

	private static ParitariasServiceImpl instance = null;

	public static ParitariasServiceImpl getInstance() {
		if (null == instance) {
			instance = new ParitariasServiceImpl();
		}
		return instance;
	}

	public List<Paritaria> getParitarias(String nombreCamara, Date periodo) {
		_log.debug("buscando getParitarias");

		Connection con = null;
		CallableStatement stmt = null;
		List<Paritaria> paritarias = null;
		Paritaria par = null;
		try {
			String sql = "{call busca_paritarias(?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			if (periodo != null) {
				stmt.setDate(1, new java.sql.Date(periodo.getTime()));
			}else {
				stmt.setNull(1, Types.DATE);
			}
			if ("0".equalsIgnoreCase(nombreCamara)) {
				stmt.setNull(2, Types.VARCHAR);
			}else {
				stmt.setString(2, nombreCamara);
			}
					
			ResultSet rs = stmt.executeQuery();
			paritarias = new ArrayList<Paritaria>();
			while (rs.next()) {
				par = Paritaria.getMapping(rs, "");
				paritarias.add(par);
			}
		} catch (Exception e) {
			_log.error("Error al traer paritarias ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar paritarias");
		return paritarias;
	}
	
	public int agregarParitarias(Paritaria paritaria, boolean simular) {
		_log.debug(" agregarParitarias ");

		Connection con = null;
		CallableStatement stmt = null;
		int ok = 0;
		try {
			String sql = "{call inserta_paritaria(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(paritaria.getFechaAltaParitaria().getTime()));
			stmt.setString(2, paritaria.getCamara());
			stmt.setBigDecimal(3, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatA())));
			stmt.setBigDecimal(4, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatB())));
			stmt.setBigDecimal(5, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatC())));
			stmt.setBigDecimal(6, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatD())));
			stmt.setBigDecimal(7, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatE())));
			stmt.setBigDecimal(8, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatJornalesA())));
			stmt.setBigDecimal(9, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatJornalesB())));
			stmt.setBigDecimal(10, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatJornalesC())));
			stmt.setBigDecimal(11, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatJornalesD())));
			stmt.setBigDecimal(12, BigDecimal.valueOf(Double.parseDouble(paritaria.getCatJornalesE())));
			stmt.setBoolean(13, simular);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ok = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al grabar paritarias ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de grabar paritarias");
		return ok;
	}


	public List<EscalaSueldosBasicos> getTraerParitarias(String nombreCamara, Date periodo, boolean simulado) {
		_log.debug("buscando getTraerParitarias");

		Connection con = null;
		CallableStatement stmt = null;
	    List<EscalaSueldosBasicos> sueldos = null;
		
	    EscalaSueldosBasicos sueldo = null;
		try {
			String sql = "{call traer_sueldos_paritarias(?,?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(periodo.getTime()));
			stmt.setString(2, nombreCamara);
			stmt.setBoolean(3, simulado);
					
			ResultSet rs = stmt.executeQuery();
			sueldos = new ArrayList<EscalaSueldosBasicos>();
			while (rs.next()) {
				sueldo = Paritaria.getMappingSuedos(rs, "");
				sueldos.add(sueldo);
			}
		} catch (Exception e) {
			_log.error("Error al traer paritarias ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar paritarias");
		return sueldos;
	}
	
	public List<EscalaSueldosBasicos> getTraerParitariasJornales(String nombreCamara, Date periodo, boolean simulado) {
		_log.debug("buscando getTraerParitarias");

		Connection con = null;
		CallableStatement stmt = null;
	    List<EscalaSueldosBasicos> sueldos = null;
		
	    EscalaSueldosBasicos sueldo = null;
		try {
			String sql = "{call traer_sueldos_jornales_paritarias(?,?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(periodo.getTime()));
			stmt.setString(2, nombreCamara);
			stmt.setBoolean(3, simulado);
					
			ResultSet rs = stmt.executeQuery();
			sueldos = new ArrayList<EscalaSueldosBasicos>();
			while (rs.next()) {
				sueldo = Paritaria.getMappingSuedos(rs, "");
				sueldos.add(sueldo);
			}
		} catch (Exception e) {
			_log.error("Error al traer paritarias ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de buscar paritarias");
		return sueldos;
	}
	
	
	public int validarParitariaExistente(Paritaria paritaria) {
		_log.debug(" validarParitariaExistente ");

		Connection con = null;
		CallableStatement stmt = null;
		int ok = 0;
		try {
			String sql = "{call validar_paritaria_mayor_igual(?,?)}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(paritaria.getFechaAltaParitaria().getTime()));
			stmt.setString(2, paritaria.getCamara());
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ok = rs.getInt(1);
			}
		} catch (Exception e) {
			_log.error("Error al grabar paritarias ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("saliendo de grabar paritarias");
		return ok;
	}
}
