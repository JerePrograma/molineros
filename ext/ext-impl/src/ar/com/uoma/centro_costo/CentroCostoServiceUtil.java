package ar.com.uoma.centro_costo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class CentroCostoServiceUtil {
	
	private static Log _log = LogFactoryUtil
			.getLog(CentroCostoServiceUtil.class);

	private static CentroCostoServiceImpl instance = null;

	public static CentroCostoServiceImpl getInstance() {
		if (null == instance) {
			instance = new CentroCostoServiceImpl();
		}
		return instance;
	}
	
	
	public static CentroCosto get(Integer id,int entidad)
			throws SystemException, SQLException {
		
		Connection connection = null;
		CentroCosto centroCosto= new CentroCosto();
		try {
		  connection = ConnectionHelper.getConnection();
		  
		  List<CentroCosto> list = getInstance().list(null,entidad,id,connection);
		  centroCosto =list.get(0);
		 
			connection.close();
		} catch (SQLException e) {
			
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return centroCosto;
    }
	
	

	public static List<CentroCosto> list(String descripcion,int entidad,Integer id)
			throws Exception{
	
	   Connection connection = null;
	   connection = ConnectionHelper.getConnection();
	   List<CentroCosto>centros=new ArrayList<CentroCosto>();
	   try{
	      centros =getInstance().list(descripcion,entidad,id,connection);
	   }catch(Exception e){
		throw e;
	   }finally{
		if (connection != null) {
			 connection.close();
		 }
	   }
	    return centros;
    }
	
	public static Integer add(CentroCosto centroCosto, String screenName,int entidad) throws Exception {

		Integer idCentroCosto = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCentroCosto=getInstance().add(centroCosto, screenName,entidad,connection);
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
	  return idCentroCosto;
	}
	
	
	public static Integer update(CentroCosto centroCosto, String screenName,int entidad) throws Exception {
		Integer idCentroCosto = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCentroCosto=getInstance().update(centroCosto, screenName,entidad,connection);
		    
//		    CentroCosto cajaChicaDB = get(cajaChica.getId(),entidad);
		    
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
	  return idCentroCosto;
	}
	
	public static Integer delete(Integer idCentroCosto, String screenName,int entidad) throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCentroCosto=getInstance().delete(idCentroCosto, screenName,entidad,connection);
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
	  return idCentroCosto;
	}
	
	public static List<ComprobanteCajaChica> comprobantesPorCentroCosto(int id,int entidad,Connection connectionParameter) throws SystemException{
		return getInstance().comprobantesPorCentroCosto(id, entidad, connectionParameter);
	}
	
	public static List<ComprobanteCajaChica> comprobantesPorCentroCostoOffset(int id,int entidad,int offset,Connection connectionParameter) throws SystemException{
		return getInstance().comprobantesPorCentroCostoOffset(id, entidad,offset,connectionParameter);
	}
	
	public static Double ejecucionPorCentroDeCosto(int id,int entidad,Connection connectionParameter){
		return getInstance().ejecucionPorCentroCosto(id, entidad, connectionParameter);
	}
	
}
