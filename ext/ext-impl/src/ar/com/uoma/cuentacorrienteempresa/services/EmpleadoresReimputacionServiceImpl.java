package ar.com.uoma.cuentacorrienteempresa.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hsqldb.Types;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.CuentaCorrienteEmpresa;

public class EmpleadoresReimputacionServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(EmpleadoresReimputacionServiceImpl.class);

	private static EmpleadoresReimputacionServiceImpl instance = null;

	public static EmpleadoresReimputacionServiceImpl getInstance() {
		if (null == instance) {
			instance = new EmpleadoresReimputacionServiceImpl();
		}
		return instance;
	}

	public Boolean updatePago(FichaBoletaPortal origen, FichaBoletaPortal destino, 
			FichaBoletaPortal ajuste,User user) throws Exception {
		
		Connection conEmp = null;
		Connection con = null;
		CallableStatement stmt = null;
		Boolean ret=true;
		Integer reimputacionId = null;
        
		try {
			String sql = "";
			sql = "{call uoma.reimputacion_pagos_add(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
//			conEmp = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			con=ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
						
			if (origen.getCuit() != null) {
				stmt.setString(1, origen.getCuit());
			}else {
				stmt.setNull(1,  Types.VARCHAR);
			}
			
			stmt.setInt(2, origen.getNro_boleta_portal_emple() );
			stmt.setInt(3, origen.getTipoBoleta());
			stmt.setString(4,origen.getTipoBoletaStr());
			stmt.setDate(5,  new java.sql.Date(origen.getPeriodo_cod_barras().getTime()));
			stmt.setDouble(6, origen.getImporte().doubleValue());
			
			stmt.setInt(7, destino.getNro_boleta_portal_emple());
			stmt.setInt(8, destino.getTipoBoleta());
			stmt.setString(9,destino.getTipoBoletaStr());
			stmt.setDate(10,  new java.sql.Date(destino.getPeriodo_cod_barras().getTime()));
			stmt.setDouble(11, destino.getCapital().add(destino.getInteres()).doubleValue());
			
			if(ajuste.getTipoBoleta()!=null && ajuste.getTipoBoleta()!=0) {
				stmt.setInt(12, ajuste.getTipoBoleta());
				stmt.setString(13, ajuste.getTipoBoletaStr());
				stmt.setDate(14,  new java.sql.Date(ajuste.getFecha_recauda().getTime()));
				stmt.setDouble(15,ajuste.getImporte().setScale(2).doubleValue());
			}else {
				stmt.setNull(12, Types.INTEGER);
				stmt.setNull(13,  Types.VARCHAR);
				stmt.setNull(14, Types.DATE);
				stmt.setNull(15,Types.DOUBLE);
			}
			
			stmt.setString(16,user.getScreenName());
		 	
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				reimputacionId = rs.getInt(1);
			}
			
			//Impacta en tabla de pagos en Molineros y Empleadores
			
			sql="{call uoma.reimputacion_pagos_update(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, origen.getCuit());
			stmt.setInt(2, origen.getNro_boleta_portal_emple() );
			stmt.setInt(3, origen.getTipoBoleta());
			stmt.setDate(4,  new java.sql.Date(origen.getPeriodo_cod_barras().getTime()));
			stmt.setDate(5,  new java.sql.Date(origen.getFecha_recauda().getTime()));
			stmt.setDate(6,  new java.sql.Date(origen.getFecha_rendicion().getTime()));
			stmt.setInt(7, destino.getNro_boleta_portal_emple() );
			stmt.setInt(8, destino.getTipoBoleta());
			stmt.setDate(9,  new java.sql.Date(destino.getPeriodo_cod_barras().getTime()));
			
			if(ajuste.getTipoBoleta()!=null && ajuste.getTipoBoleta()!=0) {
				stmt.setInt(10, ajuste.getTipoBoleta());
				stmt.setDate(11,  new java.sql.Date(ajuste.getFecha_recauda().getTime()));
				stmt.setDouble(12,ajuste.getImporte().setScale(2).doubleValue());
			}else {
				stmt.setNull(10, Types.INTEGER);
				stmt.setNull(11, Types.DATE);
				stmt.setNull(12,Types.DOUBLE);
			}
			stmt.setString(13,origen.getNroMovimiento());
			
			rs = stmt.executeQuery();
			while (rs.next()) {
				reimputacionId = rs.getInt(1);
			}
			
			
		} catch (Exception e) {
			_log.error("Error al traer Reimputar pagos empleadores ", e);
			throw new Exception(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
//			ConnectionHelper.cerrar(stmt, conEmp);
		}
		_log.debug("saliendo de buscar Reimputar pagos empleadores");
		return ret;
	}
	
	
	
}
