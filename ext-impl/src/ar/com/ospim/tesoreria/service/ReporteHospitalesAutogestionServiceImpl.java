package ar.com.ospim.tesoreria.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.tesoreria.beans.EgresoLiquidacion;
import ar.com.ospim.tesoreria.beans.HospitalAutogestion;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ReporteHospitalesAutogestionServiceImpl {

	
	private static Log _log = LogFactoryUtil.getLog(ReporteHospitalesAutogestionServiceImpl.class);

	private static ReporteHospitalesAutogestionServiceImpl instance = null;

	public static ReporteHospitalesAutogestionServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReporteHospitalesAutogestionServiceImpl();
		}
		return instance;
	}


	public static List<HospitalAutogestion> getListaHospitalesAutogestion(Date fechaDesde) {
		Connection con = null;
		CallableStatement stmt = null;
		List<HospitalAutogestion> lista = null;
		try {
			String sql = "{call reporte_hospitales_autogestion(?)}";
			con = ConnectionHelper.getReportesOspimConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<HospitalAutogestion>();
			while (rs.next()) {
				HospitalAutogestion ins = HospitalAutogestion.getMapping(rs);
				lista.add(ins);
			}
		} catch (Exception e) {
			_log.error("Error al traer reporte Hospitales-Autogestion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	

}
