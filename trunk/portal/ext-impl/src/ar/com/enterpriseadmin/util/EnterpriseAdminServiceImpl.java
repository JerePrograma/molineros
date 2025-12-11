package ar.com.enterpriseadmin.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;
import ar.com.ospim.global.beans.PrestacionConcepto;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceImpl;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.login.coordenadas.services.CoordenadasServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;


public class EnterpriseAdminServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(EnterpriseAdminServiceImpl.class);

	public long insertaDerivacionNotificacion(User user,String derivacionEMail,User responsable,
			String usuarioResponsableEmail, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
		} else {
			con = connectionParameter;
		}
		int id_seguimiento = 0;
		try {
			String sql = "{call crm.inserta_derivacion_notificacion(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, user.getScreenName());
			stmt.setLong(2, user.getGroupIds()[0]);
			stmt.setLong(3,user.getOrganizationIds()[0]);
			stmt.setString(4, derivacionEMail );
			stmt.setString(5, responsable.getScreenName());
			stmt.setString(6,usuarioResponsableEmail);
			stmt.setString(7,"Ud. tiene un contacto derivado a su bandeja de entrada");	
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				id_seguimiento = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimientoSUR", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_seguimiento;
	}
	
	public long updateDerivacionNotificacion(User user,String derivacionEMail,User responsable,
			String usuarioResponsableEmail, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
		} else {
			con = connectionParameter;
		}
		int id_seguimiento = 0;
		try {
			String sql = "{call crm.update_derivacion_notificacion(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			Long sector = user.getUserGroupIds()[0];
			Long edificio= user.getOrganizationIds()[0];
			stmt.setString(1, user.getScreenName());
			stmt.setInt(2, sector.intValue() );
			stmt.setInt(3,edificio.intValue());
			stmt.setString(4, derivacionEMail );
			stmt.setString(5, responsable.getScreenName());
			stmt.setString(6,usuarioResponsableEmail);
			stmt.setString(7,"Ud. tiene un contacto derivado a su bandeja de entrada");	
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				id_seguimiento = rs.getInt(1);
				
				if(id_seguimiento==0){
					sql = "{call crm.inserta_derivacion_notificacion(?,?,?,?,?,?,?)}";
					stmt = con.prepareCall(sql.toString());
					stmt.setString(1, user.getScreenName());
					stmt.setInt(2, sector.intValue() );
					stmt.setInt(3,edificio.intValue());
					stmt.setString(4, derivacionEMail );
					stmt.setString(5, responsable.getScreenName());
					stmt.setString(6,usuarioResponsableEmail);
					stmt.setString(7,"Ud. tiene un contacto derivado a su bandeja de entrada");	
					
					ResultSet rsn = stmt.executeQuery();
				}
				
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SQLException e) {
			_log.error("Error al insertar seguimientoSUR", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_seguimiento;
	}
	
	public static long insertaTarjetaCoordenadasUser(User user,String ipSeguras) throws Exception {
		long idSeguimiento = 0; 
		Connection connection = null;
		CallableStatement stmt = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			String query = "{call ingreso_externo.inserta_tarjetacoordenadas(?,?,?)}";
			stmt = connection.prepareCall(query.toString());
			String coordenadasFinal = generarCoordenadas();
			stmt.setString(1, coordenadasFinal);
			stmt.setLong(2, user.getUserId() );
			stmt.setString(3, ipSeguras);
			stmt.executeQuery() ;
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
	  return idSeguimiento;
	}
	
	public static long updateTarjetaCoordenadasUser(User user,String ipSeguras,Boolean generaCoordenadas) throws Exception {
		long idSeguimiento = 0; 
		Connection connection = null;
		CallableStatement stmt = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			String query = "{call ingreso_externo.update_tarjetacoordenadas(?,?,?)}";
			stmt = connection.prepareCall(query.toString());
			String coordenadasFinal="";
			if(generaCoordenadas){
				 coordenadasFinal = generarCoordenadas();
			}
/*			
			String query = "update ingreso_externo.tarjetacoordenadas set ";
			if(generaCoordenadas){
				String coordenadasFinal = generarCoordenadas();
			    query += "coordenadas='" + coordenadasFinal +"',"; 
			}
			if(ipSeguras !=null && !"".equalsIgnoreCase(ipSeguras)){
			    query += "ip_sin_coordenadas='"+ ipSeguras +"',";
			}
            query +=  "modi_usr='admin'," +
			          "modi_fecha=current_timestamp "+
			          "where user_id=?";
*/					
			stmt.setString(1, coordenadasFinal);
			stmt.setLong(2,user.getUserId());
			stmt.setString(3,ipSeguras);

			stmt.executeQuery();
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
	  return idSeguimiento;
	}
	
	public static String generarCoordenadas() {
		return CoordenadasServiceUtil.generarCoordenadas();
	}
}
