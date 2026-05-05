package ar.com.ospim.farmacia.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;
import ar.com.ospim.farmaciaOspim.beans.MedicacionOspimExcel;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarMedicamentoOspimException;
import ar.com.ospim.farmaciaOspim.reportes.beans.BusquedaReporteMedicamentosFiltro;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;


/**
 * <a href="BusquedaMedicamentoServiceImpl .java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class BusquedaMedicamentoServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(BusquedaMedicamentoServiceImpl .class);

	public List<Medicamento> getBusquedaMedicamentos(int troquel, int registro,
			String nombre, String presentacion, String laboratorio, String cod_barras) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call buscar_medicamentos(?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(registro!=0){
				stmt.setInt(2, registro);	
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			stmt.setString(4, (null!=presentacion&&presentacion.trim().equals(""))?null:presentacion);
			stmt.setString(5, (null!=laboratorio&&laboratorio.trim().equals(""))?null:laboratorio);
			stmt.setString(6, (null!=cod_barras&&cod_barras.trim().equals(""))?null:cod_barras);

			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = new Medicamento(rs.getInt("id_medicamento"),rs.getInt("troquel"), rs.getInt("nro_registro"), rs.getString("nombre"),rs.getString("presentacion"), 
								 rs.getString("laboratorio"), rs.getString("accion"), rs.getString("droga"), rs.getBigDecimal("precio_unitario"),BigDecimal.valueOf(0),
								 BigDecimal.valueOf(0),rs.getBigDecimal("porc_sssalud"),rs.getBigDecimal("pmoe"), rs.getString("cod_barras"));
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}
	

	
	

	public List<Medicamento> getBusquedaMedicamentosxRegistrooxTroquel (int registro , int troquel) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call farmacia.buscar_medicamentos_por_troquel_o_nro_registro(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel==0  ){
				stmt.setInt(1, registro  );
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(registro ==0  ){
				stmt.setInt(2, troquel);
			}else{
				stmt.setNull(2, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = new Medicamento(rs.getInt("id_medicamento"),rs.getInt("troquel"), rs.getInt("nro_registro"), rs.getString("nombre"),rs.getString("presentacion"), 
								 rs.getString("laboratorio"), rs.getString("accion"), rs.getString("droga"), rs.getBigDecimal("precio_unitario"),BigDecimal.valueOf(0),
								 BigDecimal.valueOf(0),rs.getBigDecimal("porc_sssalud"),rs.getBigDecimal("pmoe"), rs.getString("cod_barras"));
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}
	
	
	public List<MedicacionOspimExcel> getReporteMedicamentosOspimFiltro (BusquedaReporteMedicamentosFiltro filtro) {
		Connection con = null;
		CallableStatement stmt=null;
		List<MedicacionOspimExcel> listaMedicamentos= null;
		try {
			String sql = "{call farmacia.reporte_medicamentos_ospim(?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getTroquel() !=0){
				stmt.setInt(1, filtro.getTroquel());
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(filtro.getRegistro()  !=0){	
				stmt.setInt(2, filtro.getRegistro());	
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			stmt.setString(3, (null!=filtro.getNombre()&&filtro.getNombre().trim().equals(""))?null:filtro.getNombre());
			stmt.setString(4, (null!=filtro.getPresentacion()&&filtro.getPresentacion().trim().equals(""))?null:filtro.getPresentacion());
			stmt.setString(5, (null!=filtro.getLaboratorio()&&filtro.getLaboratorio().trim().equals(""))?null:filtro.getLaboratorio());
			stmt.setString(6, (null!=filtro.getCod_barra()&&filtro.getCod_barra().trim().equals(""))?null:filtro.getCod_barra());
			stmt.setDate(7, filtro.getPeriodo()== null ? null : new java.sql.Date(	filtro.getPeriodo().getTime()));
			stmt.setString(8, (null!=filtro.getDroga()&&filtro.getDroga().trim().equals(""))?null:filtro.getDroga());
			if(filtro.isManualDat()){	
				stmt.setBoolean(9, filtro.isManualDat());	
			}else{
				stmt.setNull(9, Types.BOOLEAN );	
			}			
			stmt.setBoolean(10, filtro.isIncluyeBajas()  );
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<MedicacionOspimExcel>();
			while (rs.next()) {
				MedicacionOspimExcel bp = MedicacionOspimExcel.getMappingReporte(rs);
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}	


	
	
	public List<ItemMedicacionTotal> getBusquedaMedicamentosOspimTotal(int troquel, int registro,
			String nombre, String presentacion, String laboratorio, String cod_barras , Date periodoFecha ,String drogaMedicacion 
			, boolean manualDat , int pagina , boolean incluyeBajas) {
		Connection con = null;
		CallableStatement stmt=null;
		List<ItemMedicacionTotal> listaMedicamentos= null;
		try {
			String sql = "{call farmacia.buscar_medicamentos_ospim(?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(registro!=0){
				stmt.setInt(2, registro);	
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			stmt.setString(3, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			stmt.setString(4, (null!=presentacion&&presentacion.trim().equals(""))?null:presentacion);
			stmt.setString(5, (null!=laboratorio&&laboratorio.trim().equals(""))?null:laboratorio);
			stmt.setString(6, (null!=cod_barras&&cod_barras.trim().equals(""))?null:cod_barras);
			if(periodoFecha != null) {
				stmt.setDate(7, new java.sql.Date(periodoFecha.getTime()));
			}else {
				stmt.setNull(7, Types.DATE);
			}
			stmt.setString(8, (null!=drogaMedicacion&&drogaMedicacion.trim().equals(""))?null:drogaMedicacion);
			if(!manualDat){
				stmt.setNull(9, Types.BOOLEAN );
			}else{
				stmt.setBoolean(9, true);	
			}
			stmt.setInt(10, pagina );
			if(!incluyeBajas) {
				stmt.setNull(11, Types.BOOLEAN );
			}else{
				stmt.setBoolean(11, incluyeBajas);
			}
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<ItemMedicacionTotal>();
			while (rs.next()) {
				ItemMedicacionTotal bp = Medicamento.getMappingOspimTotal(rs, "medospim_");
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}	

	
	public List<Medicamento> getBusquedaMedicamentosOspim (int troquel, int registro,
			String nombre, String presentacion, String laboratorio, String cod_barras , Date periodoFecha ,String drogaMedicacion 
			, boolean manualDat) {
		Connection con = null;
		CallableStatement stmt=null;
		List<Medicamento> listaMedicamentos= null;
		try {
			String sql = "{call farmacia.buscar_medicamentos_ospim(?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(troquel!=0){
				stmt.setInt(1, troquel);
			}else{
				stmt.setNull(1, Types.INTEGER);
			}
			if(registro!=0){
				stmt.setInt(2, registro);	
			}else{
				stmt.setNull(2, Types.INTEGER);	
			}
			stmt.setString(3, (null!=nombre&&nombre.trim().equals(""))?null:nombre);
			stmt.setString(4, (null!=presentacion&&presentacion.trim().equals(""))?null:presentacion);
			stmt.setString(5, (null!=laboratorio&&laboratorio.trim().equals(""))?null:laboratorio);
			stmt.setString(6, (null!=cod_barras&&cod_barras.trim().equals(""))?null:cod_barras);
			if(periodoFecha != null) {
				stmt.setDate(7, new java.sql.Date(periodoFecha.getTime()));
			}else {
				stmt.setNull(7, Types.DATE);
			}
			stmt.setString(8, (null!=drogaMedicacion&&drogaMedicacion.trim().equals(""))?null:drogaMedicacion);
			if(!manualDat){
				stmt.setNull(9, Types.BOOLEAN );
			}else{
				stmt.setBoolean(9, true);	
			}
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<Medicamento>();
			while (rs.next()) {
				Medicamento bp = Medicamento.getMappingOspim(rs, "medospim_");
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}	


	public Medicamento getMedicamento (int id ) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Medicamento  medicamento = null;
		
		try {
			String sql = "{call farmacia.buscar_medicacion_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				medicamento = Medicamento.getMappingOspimEdita(rs, "medospim_") ;
			}
		} catch (Exception e) {
			_log.error("Error al buscar medicacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
//		medicamento.setId_medicamento(id);		
		return medicamento ;
	}

	
	public int insertar(Medicamento medicacion, User user ) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null ;
		String screenName = user.getScreenName();
		int idMedicacion =0;
		String sql  = "{call farmacia.insertar_medicacion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		try {			
			
			con = ConnectionHelper.getConnectionForTransaction();		
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			if (null != medicacion.getPeriodo() ) {
				stmt.setDate(1, new java.sql.Date(medicacion.getPeriodo().getTime()));						
			} else {					
				stmt.setNull(1, Types.DATE);
			}
			if (null != medicacion.getFecha()) {
				stmt.setDate(2, new java.sql.Date(medicacion.getFecha().getTime()));						
			} else {					
				stmt.setNull(2, Types.DATE);
			}			
			stmt.setInt (3, medicacion.getRegistro());
			stmt.setString(4, screenName);
			stmt.setString (5, medicacion.getBaja());
			stmt.setInt (6, medicacion.getTroquel());
			stmt.setString (7, medicacion.getNombre());
			stmt.setString (8, medicacion.getPresentacion());
			stmt.setString (9, medicacion.getLaboratorio());
			stmt.setBigDecimal(10, medicacion.getPrecio());
			stmt.setString (11, medicacion.getCod_barra());
			stmt.setString (12, medicacion.getAccion());
			stmt.setString (13, medicacion.getDroga());
			stmt.setString (14, medicacion.getTipoVenta());
			stmt.setString (15, medicacion.getIva());
			stmt.setBoolean(16 , medicacion.getManualDat());
			idMedicacion = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){				
				idMedicacion =stmt.getInt(1);
			}
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar medicacion", e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return idMedicacion ;
	}

	public boolean actualizar(Medicamento medicacion, User user ) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null ;
		boolean resp=false ;
		
		try {
			
			String screenName = user.getScreenName();
			String sql1  = "{call farmacia.update_medicacion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnectionForTransaction();
			stmt = con.prepareCall(sql1.toString());
			
			if (null != medicacion.getPeriodo() ) {
				stmt.setDate(1, new java.sql.Date(medicacion.getPeriodo().getTime()));						
			} else {					
				stmt.setNull(1, Types.DATE);
			}
			if (null != medicacion.getFecha()) {
				stmt.setDate(2, new java.sql.Date(medicacion.getFecha().getTime()));						
			} else {					
				stmt.setNull(2, Types.DATE);
			}			
			stmt.setInt (3, medicacion.getRegistro());
			stmt.setString(4, screenName);
			stmt.setString (5, medicacion.getBaja());
			stmt.setInt (6, medicacion.getTroquel());
			stmt.setString (7, medicacion.getNombre());
			stmt.setString (8, medicacion.getPresentacion());
			stmt.setString (9, medicacion.getLaboratorio());
			stmt.setBigDecimal(10, medicacion.getPrecio());
			stmt.setString (11, medicacion.getCod_barra());
			stmt.setString (12, medicacion.getAccion());
			stmt.setString (13, medicacion.getDroga());
			stmt.setString (14, medicacion.getTipoVenta());
			stmt.setString (15, medicacion.getIva());
			stmt.setBoolean(16 , medicacion.getManualDat());
			stmt.setInt(17 , medicacion.getId_medicamento());
			
			stmt.executeUpdate();
					
			con.commit();	 
			resp=true;
		} catch (SQLException e) {
			_log.error("Error al actualizar medicacion", e);
			ConnectionHelper.rollback(con);		
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return resp ;
	}
	
	public void borrar(int id, String screenName) throws SQLException, ImposibleBorrarMedicamentoOspimException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call farmacia.borra_medicacion(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarMedicamentoOspimException ();
				}
			}
		} catch (ImposibleBorrarMedicamentoOspimException  e) {
			_log.error("Error al borrar el medicamento", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}


}
