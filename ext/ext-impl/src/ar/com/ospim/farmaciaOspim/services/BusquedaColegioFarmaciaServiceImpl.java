package ar.com.ospim.farmaciaOspim.services;
import java.math.BigDecimal; 
import java.sql.CallableStatement; 
import java.sql.Connection; 
import java.sql.ResultSet; 
import java.sql.Types; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List; 
import ar.com.ospim.global.beans.ColegioFarmacia;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log; 
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.sun.star.sdbc.SQLException; 


public class BusquedaColegioFarmaciaServiceImpl { 

	private static Log _log = LogFactoryUtil.getLog(BusquedaColegioFarmaciaServiceImpl.class); 

	public List<ColegioFarmacia> getBusquedaColegio(String codigoColegio, String detalleColegio) { 
		Connection con = null; 
		CallableStatement stmt = null; 
		List<ColegioFarmacia> listaColegio = null; 
		try { 
			String sql = "{call trae_listado_colegio_farmacia(?,?)}"; 
			con = ConnectionHelper.getConnection(); 
			stmt = con.prepareCall(sql.toString()); 
			 
			if (null != codigoColegio) { 
				stmt.setString(1, codigoColegio); 
			} else { 
				stmt.setNull(1, Types.VARCHAR ); 
			}			 
			 
			if (null != detalleColegio) { 
				stmt.setString(2, detalleColegio); 
			} else { 
				stmt.setNull(2, Types.VARCHAR ); 
			} 
			 
			ResultSet rs = stmt.executeQuery(); 
			listaColegio  = new ArrayList<ColegioFarmacia>(); 
			while (rs.next()) { 
				ColegioFarmacia bp = new ColegioFarmacia(rs.getString("cole_codigo"), rs.getString("cole_descripcion"));
				listaColegio.add(bp); 
			} 

		} catch (Exception e) { 
			_log.error(e); 
			_log.debug(e.getMessage()); 
		} finally { 
			ConnectionHelper.cerrar(stmt, con); 
		} 
		return listaColegio; 
	} 


	
	public boolean existeColegioFarmacia(String descripcion)
			throws SystemException {

		boolean result = false;
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			
			String sql = "{call trae_listado_colegio_farmacia (?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setNull(1, Types.VARCHAR );
			if (null != descripcion) { 
				stmt.setString(2, descripcion); 
			} else { 
				stmt.setNull(2, Types.VARCHAR ); 
			} 
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				result = true;
			}
			
		} catch (Exception e) {
			_log.error("Error al verificar duplicado del Colegio Farmacia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return result;
	}



public long insertaColegioFarmacia(String descripcion,String screenName,Connection connectionParameter) throws SystemException, SQLException, java.sql.SQLException {
		Connection con = null;
		CallableStatement stmt = null;
			
		int id_seguimiento = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
		
			String sql = "{call inserta_colegio_farmacia(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,descripcion);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_seguimiento;
	}


	
	
} 

 
