package ar.com.ospim.afiliados.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.SubidaFTPPadronPF;
import ar.com.ospim.util.ConnectionHelper;

/**
 * 
 * 
 * @author Conde Pablo
 * 
 */
public class SubidaPadronPFServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(SubidaPadronPFServiceImpl.class);



	public void generarPadronPagoFacil() throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		int out = 0;
		
		try {
	
			con = ConnectionHelper.getReportesOspimConnection();
		
			
			String sql = "{call public.reporte_padron_cobros_pf_coseguro()}";
			stmt = con.prepareCall(sql.toString());
			

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				out = rs.getInt(1);
			}
			
			if (out != 1){
				throw new SQLException();
			}
			
		} catch (Exception e) {
			_log.debug("Error al grabar la subida FTP padron PF ", e);
			throw e;
		} finally {
				ConnectionHelper.cerrar(stmt,con);
			
		}
		
	}


	public List<SubidaFTPPadronPF> generarArchivo() throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		List<SubidaFTPPadronPF> subidas = new ArrayList<SubidaFTPPadronPF>();
		
		 try {

			//con = ConnectionHelper.getConnectionFromJavaApplication();
			 con = ConnectionHelper.getConnection();
			 
			String sql = "{call public.trae_padron_pago_facil()}";
			stmt = con.prepareCall(sql.toString());
			
		
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				SubidaFTPPadronPF subida = new SubidaFTPPadronPF();  
				subida = SubidaFTPPadronPF.getMapping(rs);
				subidas.add(subida);
			}
			
		} catch (Exception e) {
			_log.debug("Error al generar subida FTP padron Pago Facil ", e);
			throw e;
		} finally {
				ConnectionHelper.cerrar(stmt,con);
		}
		return subidas;
		
	}

	
	public String generarReporte() throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		
		String total = null;
		
		try {
			
			//con = ConnectionHelper.getConnectionFromJavaApplication();
			con = ConnectionHelper.getConnection();
			
			String sql = "{call public.trae_total_afiliados_enviados_pago_facil()}";
			stmt = con.prepareCall(sql.toString());
		
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				total = rs.getString(1);
				
			}
			
		} catch (Exception e) {
			_log.debug("Error al generar reporte total Pago Facil ", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
			
		}
		return total;
		
	}
	
	
	
	
	
}
