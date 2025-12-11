package ar.com.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReportesSIAPTxtImpl {
	private static Log logger = LogFactoryUtil
			.getLog(ReportesSIAPTxtImpl.class);
	
	
	public List<String> getReporteRetencionGanancias(Date fecha_liq, int entidad)
		throws Exception {
			Connection con = null;
			CallableStatement stmt = null;

			List<String> result = null;
			try {
				logger.debug("obteniendo conexion");
				con = ConnectionHelper.getConnection();
				String sql = "{call  reporte_retencion_ganancias(?)}";
				if(entidad==WebKeysGlobal.UOMA){
					sql = "{call  uoma.reporte_retencion_ganancias_uoma(?)}";
				}
				if(entidad==WebKeysGlobal.AMTIMA){
					sql = "{call  reporte_retencion_ganancias_amtima(?)}";
				}
				stmt = con.prepareCall(sql.toString());
				stmt.setDate(1, new java.sql.Date(fecha_liq.getTime()));
				
				ResultSet rs = stmt.executeQuery();
				result = new ArrayList<String>();
				String delimiter="";
				while (rs.next()) {
					StringBuffer linea = new StringBuffer();
					linea.append(rs.getString(1));
					linea.append(delimiter);					
					result.add(linea.toString());
				}
			} finally {
				ConnectionHelper.cerrar(stmt, con);

			}
			return result;
		
	}
	
}
