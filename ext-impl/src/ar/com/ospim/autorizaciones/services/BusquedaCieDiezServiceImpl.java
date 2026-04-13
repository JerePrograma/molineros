package ar.com.ospim.autorizaciones.services; 

import java.math.BigDecimal; 
import java.sql.CallableStatement; 
import java.sql.Connection; 
import java.sql.ResultSet; 
import java.sql.Types; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List; 

 
import ar.com.ospim.afiliados.beans.CieDiez; 
import ar.com.ospim.util.ConnectionHelper; 
import com.liferay.portal.kernel.log.Log; 
import com.liferay.portal.kernel.log.LogFactoryUtil; 


public class BusquedaCieDiezServiceImpl { 

	private static Log _log = LogFactoryUtil.getLog(BusquedaCieDiezServiceImpl.class); 

	public List<CieDiez> getBusquedaCieDiez(String codigoCie, String detalleCie) { 
		Connection con = null; 
		CallableStatement stmt = null; 
		List<CieDiez> listaCieDiez = null; 
		try { 
			String sql = "{call trae_listado_ciediez(?,?)}"; 
			con = ConnectionHelper.getConnection(); 
			stmt = con.prepareCall(sql.toString()); 
			 
			if (null != codigoCie) { 
				stmt.setString(1, codigoCie); 
			} else { 
				stmt.setNull(1, Types.VARCHAR ); 
			}			 
			 
			if (null != detalleCie) { 
				stmt.setString(2, detalleCie); 
			} else { 
				stmt.setNull(2, Types.VARCHAR ); 
			} 
			 
			ResultSet rs = stmt.executeQuery(); 
			listaCieDiez = new ArrayList<CieDiez>(); 
			while (rs.next()) { 
				CieDiez bp = new CieDiez(rs.getString("cie_codigo"), rs.getString("cie_descripcion"));
				listaCieDiez.add(bp); 
			} 

		} catch (Exception e) { 
			_log.error(e); 
			_log.debug(e.getMessage()); 
		} finally { 
			ConnectionHelper.cerrar(stmt, con); 
		} 
		return listaCieDiez ; 
	} 
	 

} 