package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.EmailHomologacionPS;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.webservice.service.AfiliadoOpe;

/**
 * 
 * @author pconde
 *
 */

public class PrevencionServiceImpl {
	
	private static Log logger = LogFactoryUtil.getLog(PrevencionServiceImpl.class);


	
	public List<AfiliadoOpe> buscarHistoricoPrevencionAfi(String cuilTitular,  Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		AfiliadoOpe prevenRespuesta = null;
		List<AfiliadoOpe> listaPrevenRespuesta = new ArrayList<AfiliadoOpe>();
		
		try {
			String sql = "{call informes.buscar_novedades_ps_por_cuil_fecha(?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular);
			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaHasta.getTime()));
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String mensaje = null;
				prevenRespuesta = AfiliadoOpe.getMapping3(rs);
				mensaje = prevenRespuesta.getMensajeDesc();
				if (mensaje !=  null && !mensaje.isEmpty()) {
					prevenRespuesta.setInfoDatoHomologacionPS(esErrorReportarPrevencion(mensaje));
				}
				listaPrevenRespuesta.add(prevenRespuesta);					
			}
		
		} catch (Exception e) {
			logger.error("Error al buscar prentencion novedades historicas ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaPrevenRespuesta;
	}
	
	
	

	public int procesar(int operacion, Integer  idTransaccion, boolean accion,
			Connection connectionParameter) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		int result = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
			String sql = "{? = call informes.procesados_novedades_ws(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, operacion);
			stmt.setInt(3, idTransaccion);
			stmt.setBoolean(4, accion);

			stmt.executeUpdate();
			
			result = stmt.getInt(1);
			
		} catch (SQLException e) {
			logger.debug(e.getMessage());
			throw new SystemException(e);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return result;
	}
	
	
	
	public EmailHomologacionPS obtenerDatosEmailPrevencion(String cuilTitular,  int inte) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		EmailHomologacionPS eh = null;
		
		try {
			String sql = "{call informes.obtener_datos_email_prevencion_ps(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuilTitular.replace("-",""));
			stmt.setInt(2, inte);
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				eh = EmailHomologacionPS.getMapping(rs);	
			}
		
		} catch (Exception e) {
			logger.error("Error al obtener datos email prevencion ps ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return eh;
	}
	
	
	private static boolean esErrorReportarPrevencion(String mensaje) {
		boolean respuesta = false;
		String[] caracteres = TraeListasServiceUtil.getSystemConfig("HOMOLOGACION_PS_ERROR").split(",");
		for (String val : caracteres) {
			try {
				respuesta = mensaje.toUpperCase().contains(val.toUpperCase());
				if (respuesta) {
					return respuesta;
				}
			}catch (Exception e) {
				logger.debug(e);
			}
		}
		return respuesta;
	}
}
