package ar.com.uoma.cuentacorrienteempresa.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.SaldoInicial;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class SaldoInicialServiceUtil {
	
	private static Log _log = LogFactoryUtil
			.getLog(SaldoInicialServiceUtil.class);

	private static SaldoInicialServiceImpl instance = null;

	public static SaldoInicialServiceImpl getInstance() {
		if (null == instance) {
			instance = new SaldoInicialServiceImpl();
		}
		return instance;
	}
	
	public static SaldoInicial get(Integer Id, String cuit, String suc)
			throws SystemException, SQLException {
		
		Connection connection = null;
		SaldoInicial _saldo= new SaldoInicial();
		try {
		  connection = ConnectionHelper.getConnection();
		  List<SaldoInicial> list = getInstance().list(Id, cuit, suc, connection);
		  _saldo =list.get(0);
		
			connection.close();
		} catch (SQLException e) {
			
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return _saldo;
    }
	
	public static List<SaldoInicial> list(
			Integer id,
			String cuit, 
			String suc)
				throws Exception{
		
		Connection connection = null;
		List<SaldoInicial>_saldo=new ArrayList<SaldoInicial>();
		try{
			_saldo=getInstance().list(id, cuit, suc, connection);
		}catch(Exception e){
			throw e;
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return _saldo;
	}
	
	public static boolean add(SaldoInicial saldo) throws Exception {
		boolean retVal = false;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    retVal = getInstance().add(saldo,connection);
			
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

	public static boolean delete(int id) throws Exception {
		boolean retVal = false;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    retVal = getInstance().delete(id,connection);
			
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
	
	public static boolean update(SaldoInicial saldo) throws Exception {
		boolean retVal = false;
		Connection connection = ConnectionHelper.getConnectionPortalEmpleadoresV01();
		//Boolean esBaja=false;
		try {			
			connection.setAutoCommit(false);
		    retVal = getInstance().update(saldo, connection);
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
