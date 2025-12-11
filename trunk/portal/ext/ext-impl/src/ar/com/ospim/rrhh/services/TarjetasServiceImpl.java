package ar.com.ospim.rrhh.services;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import ar.com.ospim.rrhh.beans.BusquedaTarjetasFiltro;
import ar.com.ospim.rrhh.beans.ItemTarjetasTotal;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.rrhh.exceptions.ImposibleBorrarTarjetaAcessoException;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
	
public class TarjetasServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(TarjetasServiceImpl.class);

	
	public List<ItemTarjetasTotal> buscarTarjetasTotales(BusquedaTarjetasFiltro filtro) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<ItemTarjetasTotal> listaTarjetas = null;
		listaTarjetas  = new ArrayList<ItemTarjetasTotal>();
		
		try {
			String sql="" ;
			sql = "{call buscar_tarjetas_acceso(?,?,?,?,?,?,?)}";
			
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			
			stmt = con.prepareCall(sql.toString());										
			
			if (filtro.getNombre()!="") {
				stmt.setString(1, filtro.getNombre());
			} else {				
				stmt.setNull(1, Types.VARCHAR );
			}			
			if (filtro.getApellido() !="") {
				stmt.setString(2, filtro.getApellido());
			} else {				
				stmt.setNull(2, Types.VARCHAR );
			}			
			if (filtro.getNroTarjeta()  == 0) {
				stmt.setNull(3, Types.INTEGER);
			} else {
				stmt.setInt(3, filtro.getNroTarjeta()); 
			}
			if (filtro.getEntidad() !="") {
				stmt.setString(4, filtro.getEntidad() );
			} else {				
				stmt.setNull(4, Types.VARCHAR );
			}
			
			if (filtro.getSector()   !="") {
				stmt.setString(5, filtro.getSector());
			} else {				
				stmt.setNull(5, Types.VARCHAR );
			}
			
			if (filtro.getLegajo()==0) {
				stmt.setNull(6, Types.INTEGER );
			} else {				
				stmt.setInt(6, filtro.getLegajo() );
			}
			
			stmt.setInt(7, filtro.getPagina() );			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemTarjetasTotal tarjeta = TarjetaAcceso.getMappingBuscadorTotal(rs, "tarje_");
				listaTarjetas.add(tarjeta  );
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTarjetas  ;
	}
	
	
public int insertar(TarjetaAcceso   tarjeta , String screenName , Connection  connectionParametro) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;
		int idRegTarjetaAcceso =0;
		String sql  = "{call inserta_tarjeta_acceso(?,?,?,?,?,?,?,?,?)}";
		
		try {			
			if (connectionParametro == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParametro ;
			}
			
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(1, tarjeta.getNombre()   );
			stmt.setString(2, tarjeta.getApellido()    );
			stmt.setInt(3, tarjeta.getId_tarjeta_acceso()  );
			stmt.setString(4, tarjeta.getEntidad()    );
			stmt.setString(5, tarjeta.getSector()     );
			stmt.setInt(6, tarjeta.getLegajo()  );
			stmt.setDouble(7, tarjeta.getHoras_jornada()     );
			stmt.setString(8, tarjeta.getPiso() );
			stmt.setString(9, screenName);
			idRegTarjetaAcceso  = stmt.executeUpdate();
			
			if(stmt.getInt(1) > 0){				 
				idRegTarjetaAcceso  =stmt.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar tarjeta acceso", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			if (connectionParametro == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return idRegTarjetaAcceso ;
	}



	public TarjetaAcceso getTarjetaxNroTarjetaoLegajo(int nroTarjeta , int legajo, int idTarjeta ) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		TarjetaAcceso tarjeta = null;
		
		try {
			String sql = "{call buscar_tarjetas_acceso_activa_by_nrotarjeta (?,?,? )}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idTarjeta);
			stmt.setInt(2, nroTarjeta);
			stmt.setInt(3, legajo );
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				tarjeta = TarjetaAcceso.getMapping(rs, "tarje_");		  }
		} catch (Exception e) {
			_log.error("Error al validar tarjeta acceso", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tarjeta;	
	}

	public TarjetaAcceso getTarjetaAcceso(int idTarjetaAcceso) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		TarjetaAcceso tarjeta = null;
		
		
		try {
			String sql = "{call buscar_tarjetas_acceso_by_Id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idTarjetaAcceso);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tarjeta = TarjetaAcceso.getMapping(rs, "tarje_");		  
			}
		} catch (Exception e) {
			_log.error("Error al buscar tarjeta acceso", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tarjeta;	
	}

	public int actualizar(TarjetaAcceso tarjeta, String screenName ) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null ;
			
		con = ConnectionHelper.getConnection();
		try {
			String sql1 = "{call update_tarjeta_acceso(?,?,?,?,?,?,?,?,?,?)}";		
		    stmt = con.prepareCall(sql1.toString()); 
		    stmt.setString(1 , tarjeta.getNombre());
		    stmt.setString(2 , tarjeta.getApellido());
		    stmt.setInt(3 , tarjeta.getId_tarjeta_acceso());
		    stmt.setString(4 , tarjeta.getEntidad());
		    stmt.setString(5 , tarjeta.getSector());
		    stmt.setInt (6 , tarjeta.getLegajo());
		    stmt.setDouble(7 , tarjeta.getHoras_jornada());
		    stmt.setString(8 , tarjeta.getPiso());
		    stmt.setString(9 , screenName);
		    stmt.setInt(10, tarjeta.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar tarjeta de acceso", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con );
		}
		return tarjeta.getId() ;
	}


	public void borrar(int id, String screenName, Connection connectionParametro ) throws ImposibleBorrarTarjetaAcessoException, SystemException {
	
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			if (connectionParametro == null) {
				con = ConnectionHelper.getConnection() ;
			} else {
				con = connectionParametro ;
			}
			String sql = "{call borra_tarjeta_acceso(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);	
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarTarjetaAcessoException ();
				}
			}
			
		}catch(SQLException e) {
			_log.error(e);
			throw new SystemException(e);
		} finally {
			if (connectionParametro == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}				
		}
	}

	public List<TarjetaAcceso> getHistoricoTarjetaEmpleado(int empleadoLegajo ) {
		
		List<TarjetaAcceso> tarjetasEmpleado = new ArrayList<TarjetaAcceso>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_tarjetas_acceso_by_Legajo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, empleadoLegajo );
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TarjetaAcceso tarjeta = TarjetaAcceso.getMapping(rs,"tarje_");						
				tarjetasEmpleado.add(tarjeta );
			}
		} catch (Exception e) {
			_log.error("Error al buscar tarjetas historicas de la persona", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tarjetasEmpleado  ;
	}



}



