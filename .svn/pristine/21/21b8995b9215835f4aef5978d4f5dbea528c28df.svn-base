package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.SubidaFTPPadronIGS;
import ar.com.ospim.afiliados.beans.TotalesPadronIGS;
import ar.com.ospim.util.ConnectionHelper;

/**
 * 
 * 
 * @author Conde Pablo
 * 
 */
public class SubidaPadronIGSServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(SubidaPadronIGSServiceImpl.class);



	public void grabarReporte(Date fechaDesde, Date fechaHasta) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		int out = 0;
		
		try {
	
			con = ConnectionHelper.getConnection();
		
			
			String sql = "{call public.alta_subida_FTP_padron_igs(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				out = rs.getInt(1);
			}
			
			if (out != 1){
				throw new SQLException();
			}
			
		} catch (Exception e) {
			_log.debug("Error al grabar subida FTP padron IGS ", e);
			throw e;
		} finally {
				ConnectionHelper.cerrar(stmt,con);
			
		}
		
	}


	public List<SubidaFTPPadronIGS> generarArchivo(Date fechaDesde) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		List<SubidaFTPPadronIGS> subidas = new ArrayList<SubidaFTPPadronIGS>();
		
		 try {

			con = ConnectionHelper.getConnection();
			
			String sql = "{call public.subida_FTP_padron_igs(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
		
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				SubidaFTPPadronIGS subida = new SubidaFTPPadronIGS();  
				subida = SubidaFTPPadronIGS.getMapping(rs);
				subidas.add(subida);
			}
			
		} catch (Exception e) {
			_log.debug("Error al generar subida FTP padron IGS ", e);
			throw e;
		} finally {
				ConnectionHelper.cerrar(stmt,con);
		}
		return subidas;
		
	}

	
	public List<TotalesPadronIGS> generarArchivoTotales(Date fechaDesde) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		List<TotalesPadronIGS> subidas = new ArrayList<TotalesPadronIGS>();
		
		
		try {
			
				con = ConnectionHelper.getConnection();
		
			String sql = "{call public.reporte_subida_FTP_padron_igs(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
		
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				TotalesPadronIGS totales = new TotalesPadronIGS();  
				totales = TotalesPadronIGS.getMapping(rs);
				subidas.add(totales);
			}
			
		} catch (Exception e) {
			_log.debug("Error al generar reporte totales IGS ", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
			
		}
		return subidas;
		
	}
	
	
	
}
