package ar.com.ospim.afiliados.services;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="TercerizadoraServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class TercerizadoraServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(TercerizadoraServiceImpl.class);

	public void grabaTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaInicioPres, Date fechaFinPres,
			User user, Connection con) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call inserta_tercerizadora(?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setString(3, id_tercerizadora);
			stmt.setDate(4, new java.sql.Date(fechaInicioPres.getTime()));
			stmt.setDate(5, null != fechaFinPres ? (new java.sql.Date(
					fechaFinPres.getTime())) : null);
			stmt.setString(6, user.getScreenName());
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error al grabar tercerizadora", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void editaTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaInicioPres, Date fechaFinPres,
			User user, Connection con, Date fechaIngresoOriginal)
			throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call edita_tercerizadora(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setString(3, id_tercerizadora);
			stmt.setDate(4, new java.sql.Date(fechaInicioPres.getTime()));
			stmt.setDate(5, null != fechaFinPres ? (new java.sql.Date(
					fechaFinPres.getTime())) : null);
			stmt.setString(6, user.getScreenName());
			stmt.setDate(7, null != fechaIngresoOriginal ? (new java.sql.Date(
					fechaIngresoOriginal.getTime())) : new java.sql.Date(fechaInicioPres.getTime()));
//			stmt.setDate(7, null != fechaIngresoOriginal ? (new java.sql.Date(
//					fechaIngresoOriginal.getTime())) : null);
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error!", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void borraTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaInicioPres, User user,
			Connection connectionParameter) throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_tercerizadora(?,?,?,?,?)}";
			stmt = connectionParameter.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setString(3, id_tercerizadora);
			stmt.setDate(4, new java.sql.Date(fechaInicioPres.getTime()));
			stmt.setString(5, user.getScreenName());
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error!", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}

	public void borraTercerizadora(String cuil, int inte,
			String id_tercerizadora, Date fechaInicioPres, User user)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call borra_tercerizadora(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			stmt.setString(3, id_tercerizadora);
			stmt.setDate(4, new java.sql.Date(fechaInicioPres.getTime()));
			stmt.setString(5, user.getScreenName());
			stmt.executeUpdate();
			con.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
			_log.error("Error!", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<AfiTercerizadoraServicio> buscaTercerizadoras(String cuil,
			int inte, Connection con) throws Exception {
		CallableStatement stmt = null;
		List<AfiTercerizadoraServicio> tercerizadoras = null;
		try {
			// Busco tercerizadoras
			String sqlList = "{call trae_tercerizadoras_afi(?,?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			stmt.setInt(2, inte);
			ResultSet rs = stmt.executeQuery();
			tercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
			while (rs.next()) {
				AfiTercerizadoraServicio bp = new AfiTercerizadoraServicio(
						rs.getString("id_tercerizadora"),
						rs.getString("descripcion"),
						rs.getDate("fecha_ingreso"), rs.getDate("fecha_egreso"));
				tercerizadoras.add(bp);
			}
		} catch (Exception e) {
			_log.error("Error! buscaTercerizadoras", e);		
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return tercerizadoras;
	}
	
	public List<TercerizadoraServicio> getTercerizadoraPlan(int id_plan) throws Exception {
		CallableStatement stmt = null;
		Connection con = null;
		List<TercerizadoraServicio> tercerizadoras = new ArrayList<TercerizadoraServicio>();
		TercerizadoraServicio ts = null;
		try {
			con = ConnectionHelper.getConnection();
			// Busco tercerizadoras x plan
			String sqlList = "{call trae_tercerizadoras_plan(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setInt(1, id_plan);			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ts = new TercerizadoraServicio(rs.getString("id_tercerizadora"), rs.getString("descripcion"),
									rs.getDate("fecha_inicio"), rs.getDate("fecha_fin")); 
				tercerizadoras.add(ts);
			}
		} catch (Exception e) {
			_log.error("Error! getTercerizadoraPlan", e);	
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return tercerizadoras;
	}
	
	/**
	 * Sirve tanto par adar baja ultima tercerizadora, como para levantar la fecha fin de pres
	 * @param con
	 * @param cuil
	 * @param fechaEgreso
	 * @param user
	 * @throws Exception
	 */
	public void actualizaBajaUltimaTercerizadora(Connection con, String cuil, BigInteger idTercerizadora, Date fechaEgreso, String user )
			throws Exception {
		CallableStatement stmt = null;
		try {
			String sql = "{call actualiza_baja_ultima_tercerizadora(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setDouble(2, idTercerizadora.doubleValue());
			stmt.setDate(3, null != fechaEgreso ? (new java.sql.Date(fechaEgreso.getTime())) : null);
			stmt.setString(4, user);
			
			stmt.executeUpdate();
		} catch (Exception e) {
			_log.error("Error! actualizaBajaUltimaTercerizadora", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
	}
	
	public AfiTercerizadoraServicio buscarUltimaTercerizadoraDelAfiliado(Connection connectionParameter, String cuil) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;
		AfiTercerizadoraServicio ats = null;
		
		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}
			// Busco tercerizadora del afiliado, este vigente o la ultima de baja 
			String sqlList = "{call trae_ultima_tercerizadora_afi(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ats = new AfiTercerizadoraServicio(
						rs.getString("id_tercerizadora"),
						rs.getString("descripcion"),
						rs.getDate("fecha_ingreso"), 
						rs.getDate("fecha_egreso"));
				try{
					ats.setId(BigInteger.valueOf(rs.getLong("id")));
				}catch (Exception e) {
//					nada
				}
			}
		} catch (Exception e) {
			_log.error("Error! busca Tercerizadora del afi", e);		
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return ats;
	}
	
	public List<AfiTercerizadoraServicio> buscarUltimasTercerizadorasContinuidadDelAfiliado(Connection connectionParameter, String cuil) throws Exception {
		
		CallableStatement stmt = null;
		Connection con=null;
		AfiTercerizadoraServicio ats = null;
		List<AfiTercerizadoraServicio> afiTercerizadorasContinuidad = new ArrayList<AfiTercerizadoraServicio>();
		try {
			if(connectionParameter == null){
				con = ConnectionHelper.getConnection();
			}else{
				con=connectionParameter;
			}
			// Busco tercerizadora del afiliado, este vigente o la ultima de baja y si la anterior tambien es continuidad de la ultima
			String sqlList = "{call trae_ultimas_tercerizadora_afi(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ats = new AfiTercerizadoraServicio(
						rs.getString("id_tercerizadora"),
						rs.getString("descripcion"),
						rs.getDate("fecha_inicio_pres"), 
						rs.getDate("fecha_fin_pres"));
				
				ats.getTercerizadora().setFechaInicio(rs.getDate("fecha_inicio"));
				ats.getTercerizadora().setFechaInicio(rs.getDate("fecha_fin"));
				
				afiTercerizadorasContinuidad.add(ats);
			}
		} catch (Exception e) {
			_log.error("Error! busca Tercerizadora del afi", e);		
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return afiTercerizadorasContinuidad;
	}
	
	public List<AfiTercerizadoraServicio> historicoTercerizadoraDelAfiliado(String cuil_titular) throws Exception {
		
		CallableStatement stmt = null;
		AfiTercerizadoraServicio ats = null;
		List<AfiTercerizadoraServicio> afiTercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
		Connection con = null;
		
		try {
			con = ConnectionHelper.getConnection();
			
			String sqlList = "{call buscar_historico_tercerizadoras(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil_titular);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ats = AfiTercerizadoraServicio.getMapping("", rs);
				afiTercerizadoras.add(ats);
			}
		} catch (Exception e) {
			_log.error("Error! busca Tercerizadora del afi", e);		
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiTercerizadoras;
	}
	
	public List<AfiTercerizadoraServicio> traeHistoricoTercerizadoras(String cuil_titular) throws Exception {
		
		CallableStatement stmt = null;
		AfiTercerizadoraServicio ats = null;
		List<AfiTercerizadoraServicio> afiTercerizadoras = new ArrayList<AfiTercerizadoraServicio>();
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sqlList = "{call trae_historico_tercerizadoras_afi(?)}";
			stmt = con.prepareCall(sqlList.toString());
			stmt.setString(1, cuil_titular);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ats = AfiTercerizadoraServicio.getMapping("", rs);
				afiTercerizadoras.add(ats);
			}
		} catch (Exception e) {
			_log.error("Error! busca Tercerizadora del afi", e);		
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiTercerizadoras;
	}
	
	public void actualizaTercerizadora(AfiTercerizadoraServicio ats, User user, Connection connectionParameter) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectionParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectionParameter;
			}

			String sql = "{call actualiza_afi_tercerizadora(?,?,?,?,?,?,?)}"; 

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ats.getAfiliado().getCuil_titular());  
			stmt.setInt(2, ats.getAfiliado().getInte());
			stmt.setInt(3, ats.getId().intValue());
			stmt.setString(4, ats.getTercerizadora().getId_tercerizadora());
			stmt.setDate(5, new java.sql.Date(ats.getFechaInicioPres().getTime()));
			if(ats.getFechaFinPres() == null){
				stmt.setNull(6, Types.DATE);
			}else{
				stmt.setDate(6, new java.sql.Date(ats.getFechaFinPres().getTime()));
			}
			stmt.setString(7, user.getScreenName());
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			_log.error("Error!", e);
			throw e;
		} finally {
			if(connectionParameter == null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
}