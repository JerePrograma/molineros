package ar.com.uoma.proveedores.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.Proveedor;

public class ProveedoresServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(ProveedoresServiceUtil.class);

	public static List<Proveedor> getProveedores(String cuit, String  sucursal, String razonSocial, Integer id) {
		return ProveedoresServiceImpl.getInstance().getProveedores(cuit, sucursal, razonSocial, id) ;
	}
	
	
	public static Integer insertaProveedor(Proveedor proveedor, String screenName) throws Exception {
		Integer idProveedor = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idProveedor= ProveedoresServiceImpl.getInstance().insertaProveedor(proveedor, screenName,connection);
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return idProveedor;
	}
	
	public static Integer updateProveedor(Proveedor proveedor, String screenName) throws Exception {
		Integer idProveedor = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idProveedor= ProveedoresServiceImpl.getInstance().updateProveedor(proveedor, screenName,connection);
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return idProveedor;
	}
	
	
}
