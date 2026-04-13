package ar.com.ospim.novedades.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoAltas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoBajas;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidadoInconsistencia;
import ar.com.ospim.util.ConnectionHelper;

public class ReporteNovedadPadronConsolidadoServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(ReporteNovedadPadronConsolidadoServiceImpl.class);

	public List<NovedadPadronConsolidadoBajas> getNovedadPadronConsolidadoBajas(Date fechaDesde) throws SystemException{
		
		List<NovedadPadronConsolidadoBajas> bajas = new ArrayList<NovedadPadronConsolidadoBajas>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.reporte_afiliado_regla_automatica(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (fechaDesde == null){
				stmt.setNull(1, Types.DATE);
			}else{
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()) );
			}
			
			stmt.setString(2, "B"); 

			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NovedadPadronConsolidadoBajas an = NovedadPadronConsolidadoBajas.getMapping(rs);
				bajas.add(an);
			}

		} catch (Exception e) {
			logger.error("error Novedad Padron Consolidado Bajas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return bajas;
	}
	
	
public List<NovedadPadronConsolidadoAltas> getNovedadPadronConsolidadoAltas(Date fechaDesde) throws SystemException{
		
		List<NovedadPadronConsolidadoAltas> altas = new ArrayList<NovedadPadronConsolidadoAltas>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.reporte_afiliado_regla_automatica(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (fechaDesde == null){
				stmt.setNull(1, Types.DATE);
			}else{
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()) );
			}
			
			stmt.setString(2, "A"); 

			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NovedadPadronConsolidadoAltas an = NovedadPadronConsolidadoAltas.getMapping(rs);
				altas.add(an);
			}

		} catch (Exception e) {
			logger.error("error Padron Consolidado Altas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return altas;
	}
	

	
	public List<NovedadPadronConsolidadoInconsistencia> getNovedadPadronConsolidadoInconsistentes(Date fechaDesde) throws SystemException{
		
		List<NovedadPadronConsolidadoInconsistencia> inconsistencias = new ArrayList<NovedadPadronConsolidadoInconsistencia>();
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.reporte_afiliado_regla_automatica(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (fechaDesde == null){
				stmt.setNull(1, Types.DATE);
			}else{
				stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()) );
			}
			
			stmt.setString(2, "I"); 
	
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NovedadPadronConsolidadoInconsistencia an = NovedadPadronConsolidadoInconsistencia.getMapping(rs);
				inconsistencias.add(an);
			}
	
		} catch (Exception e) {
			logger.error("error Padron Consolidado Inconsistentes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return inconsistencias;
	}

}
