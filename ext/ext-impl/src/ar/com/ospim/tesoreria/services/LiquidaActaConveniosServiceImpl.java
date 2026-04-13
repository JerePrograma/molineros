package ar.com.ospim.tesoreria.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.LiquidarActaConvenioException;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.tesoreria.beans.LiquidacionActaConvenio;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LiquidaActaConveniosServiceImpl {
	private static Log logger = LogFactoryUtil
			.getLog(LiquidaActaConveniosServiceImpl.class);
	
	
	public int liqActaConvenio()
			throws LiquidarActaConvenioException  {
		Connection con = null;
		CallableStatement stmt = null;
		List<LiquidacionActaConvenio> consolidado = null;
		try {
			String sql = "{call derivar_actas_convenios_portal()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());						
			ResultSet rs = stmt.executeQuery();			
		} catch (Exception e) {
			logger.error("error al liquidar actas y convenios", e);
			throw new LiquidarActaConvenioException (e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}		
		return 0;
	}
	
	public List<LiquidacionActaConvenio> getLiqActaConvenioFechaLiq(Date fechaLiq)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<LiquidacionActaConvenio> consolidado = null;
		try {
			String sql = "{call buscar_liq_actas_conv(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (fechaLiq != null) {				
				stmt.setDate(1, new java.sql.Date(fechaLiq.getTime()));
			}			
			ResultSet rs = stmt.executeQuery();
			consolidado = new ArrayList<LiquidacionActaConvenio>();
			while (rs.next()) {
				consolidado.add(LiquidacionActaConvenio.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("error al buscar consolidado liquidaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidado;
	}
	
	public List<ConsolidadoLiquidaciones> getConsolidadoLiquidaciones(Date fechaIni)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ConsolidadoLiquidaciones> consolidado = null;
		try {
			String sql = "{call buscar_consolidado_liq_actas_conv(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (fechaIni != null) {				
				stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			}			
			ResultSet rs = stmt.executeQuery();
			consolidado = new ArrayList<ConsolidadoLiquidaciones>();
			while (rs.next()) {
				consolidado.add(ConsolidadoLiquidaciones.getMapping(rs));
			}
		} catch (Exception e) {
			logger.error("error al buscar consolidado liquidaciones", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consolidado;
	}
	
}
