package ar.com.ospim.webservice.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SimuladorSalarialServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(SimuladorSalarialServiceImpl.class);

	private static SimuladorSalarialServiceImpl instance = null;

	public static SimuladorSalarialServiceImpl getInstance() {
		if (null == instance) {
			instance = new SimuladorSalarialServiceImpl();
		}
		return instance;
	}
	
	
	
    public static void registraUbicacionConsulta(Integer provincia, Integer localidad){
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
//			con = ConnectionHelper.getConnectionFromJavaApplication() ;
			con = ConnectionHelper.getConnection() ;
			String sql = "{call informes.registra_ubicacion_simulador_salarial_ws(?, ?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, provincia);
			stmt.setInt(2, localidad);

			stmt.executeUpdate();

		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

	}

	
	
	
	
	
	
	
		
	
}
