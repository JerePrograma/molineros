package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.DesempleoSS;
import ar.com.ospim.tesoreria.beans.EgresoLiquidacion;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteDesempleoSServiceImpl {

	
	private static Log _log = LogFactoryUtil.getLog(ReporteDesempleoSServiceImpl.class);

	private static ReporteDesempleoSServiceImpl instance = null;

	public static ReporteDesempleoSServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReporteDesempleoSServiceImpl();
		}
		return instance;
	}


	public List<DesempleoSS> getReporteDesempleoSS(String  id_terc, Date fechaDesde, boolean guardar) {
		Connection con = null;
		CallableStatement stmt = null;
		List<DesempleoSS> listaEgresos = null;
		try {
			String sql = "{call reporte_desempleo(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id_terc);
			stmt.setDate(2, new java.sql.Date(fechaDesde.getTime()));
			ResultSet rs = stmt.executeQuery();
			listaEgresos = new ArrayList<DesempleoSS>();
			while (rs.next()) {
				DesempleoSS ins = DesempleoSS.getMapping(rs);
				listaEgresos.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte desempleo SS", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEgresos;
	}
		
}
