package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;
//import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

/**
 * <a href="CredencialesServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 *
 */
public class CredencialesServiceImpl {
	
	private static Log _log = LogFactoryUtil.getLog(CredencialesServiceImpl.class);
	
		
	public int generaLoteAImprimir(Map<String,Afiliado>credenciales, User user) throws Exception{
		Date hoy = new Date();
		Connection con = null;		
		CallableStatement stmt=null, stmt2=null;		
		int id_lote=0;
		try {			
			con = ConnectionHelper.getConnection();			
			//Busco documentacion
			String sqlList = "{call traer_lote_afi_creden(?)}";			
			stmt = con.prepareCall(sqlList.toString());			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.executeUpdate();
			id_lote=stmt.getInt(1);
			sqlList="{call inserta_afi_creden_lote(?,?,?,?)}";

//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";
			
			stmt=con.prepareCall(sqlList);
			stmt2 = con.prepareCall(sqlInsert.toString());
			
			ArrayList<Afiliado> afiliados=new ArrayList<Afiliado>(credenciales.values());
			for(Afiliado afi : afiliados) {
				stmt.setInt(1,id_lote);
				stmt.setString(2,afi.getCuil_titular());
				stmt.setInt(3,afi.getInte());
				stmt.setString(4, user.getScreenName());
				stmt.executeUpdate();
				
				//actualizacion de legajo del afiliado
				stmt2.setString(1, afi.getCuil_titular());
				stmt2.setInt(2, afi.getInte());
				stmt2.setInt(3, afi.getIdCorrespondencia());
				stmt2.setTimestamp(4, new java.sql.Timestamp(hoy.getTime())); // fecha de ahora
				stmt2.setNull(5, Types.INTEGER);
				stmt2.setNull(6, Types.TIMESTAMP);
				stmt2.setString(7, "impresion_credencial");
				stmt2.setString(8, user.getScreenName());
				
				stmt2.executeUpdate();
			}

		} catch (SQLException e) {
			_log.debug("error al imprimir credenciales del afiliado", e);
			throw new Exception(e);
		} finally {
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt,con);
		}
		return id_lote;
	}
	
	public int generaLoteAImprimir(List<Afiliado> credenciales, User user) throws Exception{
		Date hoy = new Date();
		Connection con = null;		
		CallableStatement stmt=null, stmt2=null;		
		int id_lote=0;
		try {			
			con = ConnectionHelper.getConnection();			
			//Busco documentacion
			String sqlList = "{call traer_lote_afi_creden(?)}";			
			stmt = con.prepareCall(sqlList.toString());			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.executeUpdate();
			id_lote=stmt.getInt(1);
			sqlList="{call inserta_afi_creden_lote(?,?,?,?)}";

//			Inserta ingreso de legajo del afiliado
			String sqlInsert = "{call actualiza_afi_legajo(?, ?, ?, ?, ?, ?, ?, ?) }";
			
			stmt=con.prepareCall(sqlList);
			stmt2 = con.prepareCall(sqlInsert.toString());
			
			for(Afiliado afi : credenciales) {
				stmt.setInt(1,id_lote);
				stmt.setString(2,afi.getCuil_titular());
				stmt.setInt(3,afi.getInte());
				stmt.setString(4, user.getScreenName());
				stmt.executeUpdate();
				
				//actualizacion de legajo del afiliado
				stmt2.setString(1, afi.getCuil_titular());
				stmt2.setInt(2, afi.getInte());
				stmt2.setInt(3, afi.getIdCorrespondencia());
				stmt2.setTimestamp(4, new java.sql.Timestamp(hoy.getTime())); // fecha de ahora
				stmt2.setNull(5, Types.INTEGER);
				stmt2.setNull(6, Types.TIMESTAMP);
				stmt2.setString(7, "impresion_credencial");
				stmt2.setString(8, user.getScreenName());
				
				stmt2.executeUpdate();
			}

		} catch (SQLException e) {
			_log.debug("error al imprimir credenciales del afiliado", e);
			throw new Exception(e);
		} finally {
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt,con);
		}
		return id_lote;
	}
	
	public boolean validarAfiliadoCredencialPrevencion(AfiliacionPrevencionDTO credencial) throws NoSuchAfiliadoEntryException{
		
		boolean resultado = false;
		Connection con = null;		
		CallableStatement stmt=null;		

		try {			
			con = ConnectionHelper.getConnection();	
//			con = ConnectionHelper.getConnectionFromJavaApplication();
			
			//Busco documentacion
			String sqlList = "{? = call validar_afiliado_credencial_prevencion(?,?,?)}";			
			stmt = con.prepareCall(sqlList.toString());			
			stmt.registerOutParameter(1, Types.BOOLEAN);
			stmt.setString(2, credencial.getCuilTitular());
			stmt.setString(3, credencial.getCuil());
			stmt.setString(4, credencial.getNroDocumento());

			stmt.execute();
			
			resultado=stmt.getBoolean(1);
			
			if(!resultado){
				throw new NoSuchAfiliadoEntryException();
			}
		
		} catch (SQLException e) {
			_log.debug("error al validar afiliado de la credencial prevención", e);
			throw new NoSuchAfiliadoEntryException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		
		return resultado;
	}
	
	public int actualizarCredencialPrevencion(AfiliacionPrevencionDTO credencial, String user) throws Exception{

		Connection con = null;		
		CallableStatement stmt=null;		
		int idTransaction=0;
		
		try {			
//			con = ConnectionHelper.getConnectionFromJavaApplication();			
			con = ConnectionHelper.getConnection();	

			String sqlList = "{call actualizar_credencial_prevencion(?,?,?,?,?,?,?)}";			
			stmt = con.prepareCall(sqlList.toString());		
			stmt.setInt(1, credencial.getNroSocio());
			stmt.setString(2, credencial.getNroDocumento());
			stmt.setString(3, credencial.getCuil());
			stmt.setBigDecimal(4, credencial.getNroCredencial());
			stmt.setString(5, credencial.getCuilTitular());
			stmt.setInt(6, credencial.getIntePrevencion());
			stmt.setString(7, user);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idTransaction=rs.getInt(1);
			}	

		} catch (SQLException e) {
			_log.debug("error al actualizar credenciales de prevencion del afiliado", e);
			throw new Exception(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return idTransaction;
	}
	
	
	
	public int insertarCredencial(String cuilTitular , int inte, String user) throws Exception{

		Connection con = null;		
		CallableStatement stmt=null;		
		int idTransaction=0;
		
		try {			
//			con = ConnectionHelper.getConnectionFromJavaApplication();			
			con = ConnectionHelper.getConnection();	

			String sqlList = "{call inserta_afi_creden_lote(?,?,?,?)}";			
			stmt = con.prepareCall(sqlList.toString());		
			stmt.setInt(1, 0);// genera el id_lote de forma automatica
			stmt.setString(2, cuilTitular);
			stmt.setInt(3,inte);
			stmt.setString(4, user);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idTransaction=rs.getInt(1);
			}	

		} catch (SQLException e) {
			_log.debug("error al crear credenciales del afiliado exento de copago", e);
			throw new Exception(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return idTransaction;
	}
	
	
	

	public int validarExisteExentoCopago(String cuil_titular , Integer inte){
		
		int resultado = 0;
		Connection con = null;		
		CallableStatement stmt=null;		

		try {			
			con = ConnectionHelper.getConnection();	

			String sqlList = "{? = call valida_credencial_exepcion_copago(?,?)}";			
			stmt = con.prepareCall(sqlList.toString());			
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, cuil_titular);
			stmt.setInt(3, inte);

			stmt.execute();
			
			resultado=stmt.getInt(1);
			
			
		} catch (SQLException e) {
			_log.debug("error al validar afiliado de la credencial exento copago", e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		
		return resultado;
	}
	
	
}
