package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="NomencladorServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class NomencladorServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(NomencladorServiceImpl.class);
	
	public int getIncrementarNomenclador(Date vigAumento,BigDecimal porc_aumento, String resolucion ,
			boolean ttos , int nomenclador, String usuario_modi, int cod_desde , int cod_hasta) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int aumentoNomenclador = 0;
		try {
			String sql = "{call incrementar_nomenclador(?,?,?,?,?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, nomenclador);
			stmt.setInt(2, cod_desde);
			stmt.setInt(3, cod_hasta);
			
			if(null!=vigAumento){
				stmt.setDate(4, new java.sql.Date(vigAumento.getTime()));
			}else{
				stmt.setNull(4, Types.DATE);
			}

			stmt.setBigDecimal(5, porc_aumento);
			stmt.setBoolean(6, ttos);
			stmt.setString(7, usuario_modi);
			stmt.setString(8, resolucion);
							
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				aumentoNomenclador = rs.getInt(1);							
			}		
 		} catch (Exception e) {
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return aumentoNomenclador;
	}
	
}
