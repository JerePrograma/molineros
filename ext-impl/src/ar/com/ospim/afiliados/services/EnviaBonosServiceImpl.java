package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.EnvioBonos;
import ar.com.ospim.afiliados.exceptions.BonoNoCargadoException;
import ar.com.ospim.afiliados.exceptions.DuplicateEnvioBonosException;
import ar.com.ospim.afiliados.exceptions.EnvioBonosNoExisteEnSeccionalException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="EnviaBonosServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class EnviaBonosServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(EnviaBonosServiceImpl.class);

	public int grabaBonosRetornaLista(int tipoBono, Date fechaEnvio,
			int bono_desde, int bono_hasta, User user)
			throws DuplicateEnvioBonosException, Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int result;
		try {
			
			String sql = "{? = call inserta_bonos(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, tipoBono);
			stmt.setInt(3, bono_desde);
			stmt.setInt(4, bono_hasta);
			stmt.setString(5, user.getScreenName());
			stmt.executeUpdate();
			
			result = stmt.getInt(1);
			
		} catch (SQLException e) {
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEnvioBonosException(e);
			} else {
				throw new SystemException(e);
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public List<EnvioBonos> grabaEnvioBonosRetornaLista(int tipoBono,
			int seccional, Date fechaEnvio, int bono_desde, int bono_hasta,
			User user) throws DuplicateEnvioBonosException, Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<EnvioBonos> envioBonos = null;
		int id_envio;
		try {

////		Vamos a hacer un tratamiento especial para los bonos tipo formularios de opcion (100),
////		si los vamos a enviar y no estan cargados, lo haremos automaticamente...
			if(tipoBono==100 && existenBonos(tipoBono, bono_desde, bono_hasta)==0 ){
				int bonosCargados = grabaBonosRetornaLista(tipoBono, new Date(), bono_desde, bono_hasta, user);
				if(bonosCargados == bono_hasta-bono_desde+1){
					// esta todo bien...
				}else{
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
//				List<EnvioBonos> bonosEnviados = grabaEnvioBonosRetornaLista(tipoBono, seccional, new Date(), bono_desde, bono_hasta, user);
//				if(bonosEnviados.size() > 0){
//					// esta todo bien...
//				}else{
//					throw new EnvioBonosNoExisteEnSeccionalException();
//				}
			}
			List<EnvioBonos> existenYaEnviados = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, false, true, false,false);
			if (this.existenBonos(tipoBono,bono_desde, bono_hasta) == bono_hasta-bono_desde+1 && 
				existenYaEnviados.size() == 0	 ) {  
				String sql = "{? = call inserta_bonos_seccional(?,?,?,?,?,?)}";

				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, tipoBono);
				stmt.setInt(3, seccional);
				stmt.setDate(4, new java.sql.Date(fechaEnvio.getTime()));
				stmt.setInt(5, bono_desde);
				stmt.setInt(6, bono_hasta);
				stmt.setString(7, user.getScreenName());
				stmt.executeUpdate();
				id_envio = stmt.getInt(1);

				// Busco las situaciones laborales.
				String sqlList = "{call trae_bonos_seccional(?)}";
				stmt = con.prepareCall(sqlList.toString());
				stmt.setInt(1, id_envio);
				ResultSet rs = stmt.executeQuery();

				envioBonos = new ArrayList<EnvioBonos>();
				while (rs.next()) {
					EnvioBonos bp = EnvioBonos.getMapping(rs);
					envioBonos.add(bp);
				}
			} else {
				throw new BonoNoCargadoException();
			}
		} catch (SQLException e) {
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateEnvioBonosException(e);
			} else {
				throw new SystemException(e);
			}

		} catch (BonoNoCargadoException e) {
			throw new BonoNoCargadoException(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return envioBonos;
	}

	public int rindeEnvioBonosRetornaLista(int tipoBono, int seccional,
			Date fechaRendicion, int bono_desde, int bono_hasta, User user)
			throws EnvioBonosNoExisteEnSeccionalException, Exception {
		
		Connection con = null;
		int result = -1;
		CallableStatement stmt = null;
		List<EnvioBonos> envioBonosChequear = null;
		try {
			
			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, false, true, false,false);
// busca los bonos no rendidos para verificar que pertenezcan a la seccional y que no esten rendidos 
			for (EnvioBonos eb : envioBonosChequear) {
				if (eb.getId_seccional() != seccional) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
				if (eb.getFecha_rendido() != null) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
			}
	
// CHEQUEO LISTA:
//         asi solo validaba contra los enviados			
//			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
//					null, null, bono_desde, bono_hasta, false, true, false);
			
//		   asi valida enviados y rendidos.			
// valida sobre los bonos rendidos 			
			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, true, true, false,false);
			for (EnvioBonos eb : envioBonosChequear) {
				if (eb.getId_seccional() != seccional) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
				if (eb.getFecha_rendido() != null) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
			}

			// ALL BIEN SIGO...
			if (envioBonosChequear != null && envioBonosChequear.size() > 0) {
				String sql = "{call rinde_bonos_seccional(?,?,?,?,?,?)}";
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, tipoBono);
				stmt.setInt(2, seccional);
				stmt.setDate(3, new java.sql.Date(fechaRendicion.getTime()));
				stmt.setInt(4, bono_desde);
				stmt.setInt(5, bono_hasta);
				stmt.setString(6, user.getScreenName());
				result = stmt.executeUpdate();
			} else {
				throw new EnvioBonosNoExisteEnSeccionalException();
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public int anulaEnvioBonosRetornaLista(int tipoBono, int seccional,
			Date fechaAnulacion, int bono_desde, int bono_hasta, User user)
			throws EnvioBonosNoExisteEnSeccionalException, Exception {
		
		Connection con = null;
		int result = -1;
		CallableStatement stmt = null;
		List<EnvioBonos> envioBonosChequear = null;
		try {
		
			// CONTROL 0 TRAE RENDIDOS Y ANULADOS HAY DUDA EN LOS PARAMETROS 
			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, true, true, false,true);		
			
			for (EnvioBonos eb : envioBonosChequear) {
				if (eb.getFecha_anulacion() != (null) ) {// porque no trae exclusivamente a los anulados 
					throw new EnvioBonosNoExisteEnSeccionalException();			
				}	
			}			
// CONTROL 1  VIENE DE rindeEnvioBonosRetornaLista 
			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, false, true, false,false); 
			for (EnvioBonos eb : envioBonosChequear) {
				if (eb.getId_seccional() != seccional) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
				if (eb.getFecha_rendido() != null) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
			}      		
// CONTROL 2  VIENE DE rindeEnvioBonosRetornaLista  
			envioBonosChequear = buscaBonosRetornaLista(tipoBono, seccional,
					null, null, bono_desde, bono_hasta, true, true, false,false);
			for (EnvioBonos eb : envioBonosChequear) {
				if (eb.getId_seccional() != seccional) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}				
				if (eb.getFecha_rendido() != null) {
					throw new EnvioBonosNoExisteEnSeccionalException();
				}
			} 	
										 				
			if (envioBonosChequear != null && envioBonosChequear.size() > 0) {
				String sql = "{call anula_bonos_seccional(?,?,?,?,?,?)}";
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, tipoBono);
				stmt.setInt(2, seccional);
				stmt.setDate(3, new java.sql.Date(fechaAnulacion.getTime()));
				stmt.setInt(4, bono_desde);
				stmt.setInt(5, bono_hasta);
				stmt.setString(6, user.getScreenName());
				result = stmt.executeUpdate();
			} else {
				throw new EnvioBonosNoExisteEnSeccionalException();
			}

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public List<EnvioBonos> buscaBonosRetornaLista(int tipoBono, int seccional,
			Date fechaDesde, Date fechaHasta, int bono_desde, int bono_hasta,
			boolean rendidos, boolean sin_rendir, boolean sin_enviar,boolean anulados )
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<EnvioBonos> envioBonos = null;
		try {
			con = ConnectionHelper.getConnection();

			// Busco las situaciones laborales.
			String sqlList = "{call busqueda_bonos_seccional(?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sqlList.toString());
			if (tipoBono == 0) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, tipoBono);
			}
			if (seccional == 0) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, seccional);
			}
			if (fechaDesde != null) {
				stmt.setDate(3, new java.sql.Date(fechaDesde.getTime()));
			} else {
				stmt.setNull(3, Types.DATE);
			}

			if (fechaHasta != null) {
				stmt.setDate(4, new java.sql.Date(fechaHasta.getTime()));
			} else {
				stmt.setNull(4, Types.DATE);
			}

			if (bono_desde == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, bono_desde);
			}
			if (bono_hasta == 0) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, bono_hasta);
			}

			if (rendidos == true && sin_rendir == true) {
				stmt.setInt(7, 3);
			} else if (rendidos == false && sin_rendir == false) {
				stmt.setInt(7, 0);
			} else if (rendidos == true && sin_rendir == false) {
				stmt.setInt(7, 2);
			} else if (rendidos == false && sin_rendir == true) {
				stmt.setInt(7, 1);
			}

			if (sin_enviar == false) {
				stmt.setNull(8, Types.BOOLEAN);
			} else {
				stmt.setBoolean(8, sin_enviar);
			}
			
			if (anulados == false) {
				stmt.setNull(9, Types.BOOLEAN);
			} else {
				stmt.setBoolean(9, anulados);
			}
		

			ResultSet rs = stmt.executeQuery();

			envioBonos = new ArrayList<EnvioBonos>();
			while (rs.next()) {
				EnvioBonos bp = EnvioBonos.getMapping(rs);
				envioBonos.add(bp);
			}
		}catch(Exception e){
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return envioBonos;
	}

	public int liberaEnvioBonosRetornaLista(int id_envio, User user)
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			
			con = ConnectionHelper.getConnection();
			// Busco las situaciones laborales.
			String sqlList = "{call borra_bonos_seccional(?, ?)}";
			stmt = con.prepareCall(sqlList.toString());

			stmt.setInt(1, id_envio);
			stmt.setString(2, user.getScreenName());

			result = stmt.executeUpdate();

		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	private int existenBonos(int tipoBono, int bono_desde, int bono_hasta) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null;
		int result = 999;
		try {

			String sqlExiste = "{call existen_bonos(?,?,?)}";
//			String sqlExiste = "{? = call existen_bonos(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sqlExiste.toString());
//			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(1, tipoBono);
			stmt.setInt(2, bono_desde);
			stmt.setInt(3, bono_hasta);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				
				result = rs.getInt(1);
			}
			
			
		} catch (SQLException e) {
			_log.error(e.getMessage());
			result = 999;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}
}
