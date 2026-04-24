package ar.com.ospim.tesoreria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.interes.Interes;
import ar.com.ospim.util.ConnectionHelper;


import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class InteresServiceUtil {
	
	private static Log _log = LogFactoryUtil
			.getLog(InteresServiceUtil.class);

	private static InteresServiceImpl instance = null;

	public static InteresServiceImpl getInstance() {
		if (null == instance) {
			instance = new InteresServiceImpl();
		}
		return instance;
	}
	
	public static Interes get(String fechaIni, String fechaFin, double interesDia, int entidad)
			throws SystemException, SQLException {
		
		Connection connection = null;
		Interes _interes= new Interes();
		try {
		  connection = ConnectionHelper.getConnection();
		  List<Interes> list = getInstance().list(fechaIni, fechaFin, interesDia, entidad, connection);
		  _interes =list.get(0);
		
			connection.close();
		} catch (SQLException e) {
			
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return _interes;
    }
	
	public static List<Interes> list(
			String fechaIni, 
			String fechaFin, 
			double interesDia, int entidad)
				throws Exception{
		
		Connection connection = null;
		connection = ConnectionHelper.getConnection();
		List<Interes>_interes=new ArrayList<Interes>();
		try{
			_interes=getInstance().list(fechaIni, fechaFin, interesDia, entidad, connection);
		}catch(Exception e){
			throw e;
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return _interes;
	}
	
	
	public static boolean add(Interes interes, int entidad) throws Exception {
		boolean retVal = false;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    retVal = getInstance().add(interes,entidad,connection);
			
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
	  return retVal;
	}

	public static boolean delete(Interes interes, int entidad) throws Exception {
		boolean retVal = false;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    retVal = getInstance().delete(interes,entidad,connection);
			
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
	  return retVal;
	}

	public static boolean update(Interes interes, 
			String origFechaDesde, String origFechaHasta, 
			int entidad) throws Exception {
		boolean retVal = false;
		Connection connection = null;
		//Boolean esBaja=false;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    retVal = getInstance().update(interes, origFechaDesde, origFechaHasta, entidad, connection);
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
	  return retVal;
	}
}
