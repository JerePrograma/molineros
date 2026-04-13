package ar.com.cgt.ddhh.services;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import ar.com.cgt.ddhh.beans.NormaDdHh;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.model.User;

public class NormaDDHHServiceUtil {
	
	public static void save(NormaDdHh normaDH, User user) throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_normaddhh = NormaDDHHServiceImpl.getInstance().save(normaDH,
					user.getScreenName(), connection);
			
			normaDH.setId(id_normaddhh);
			
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static void update(NormaDdHh normaDH, User user) throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnectionCGT();
			connection.setAutoCommit(false);

			int id_normaddhh = NormaDDHHServiceImpl.getInstance().update(normaDH,
					user.getScreenName(), connection);
			
//			normaDH.setId(id_normaddhh); ya esta porque estoy actualizando
			
			connection.commit();
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	public static List<NormaDdHh> getNormasDhHh(Date fechaDesde, Date fechaHasta, String sistema, String numero, 
												int id_tema_normadh, int id_tipo_normadh, String autor, String lugar ) throws Exception{
		
		return NormaDDHHServiceImpl.getInstance().getNormasDhHh(fechaDesde, fechaHasta, sistema, numero, id_tema_normadh, 
																id_tipo_normadh, autor, lugar);
	}
	
	public static NormaDdHh getNormaDDHH(int id_norma) throws Exception{
		return NormaDDHHServiceImpl.getInstance().getNormaDDHH(id_norma);
	
	}
	
	public static void borrarNormaDDHH(int id_normaddhh, User user) throws Exception{
		
		NormaDDHHServiceImpl.getInstance().borrarNormaDDHH(id_normaddhh, user.getScreenName());
		
	}
}
