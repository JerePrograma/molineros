package ar.com.ospim.autorizaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.autorizaciones.beans.Cartilla;
import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador;
import ar.com.ospim.autorizaciones.beans.TratamientoDiscapacidadSeguimiento;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.liquidaciones.services.EditarLiquidacionServiceUtil;
import ar.com.ospim.liquidaciones.services.TratamientoDiscapacidadServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.beans.Comprobante;

public class CartillaServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(CartillaServiceImpl.class);

	public List<Cartilla> getListaCartillas(String tipo,String prestador,String plan,String localidad,String provincia, String especialidad, String trabajaen,Boolean incluyeBajas) {
		Connection con = null;
		List<Cartilla> lista = new ArrayList<Cartilla>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_lista(?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,tipo);
			stmt.setString(2,prestador);
			stmt.setString(3, plan);
			stmt.setString(4, localidad);
			stmt.setString(5, provincia);
			stmt.setString(6, especialidad);
			stmt.setString(7, trabajaen);
			stmt.setBoolean(8, incluyeBajas);
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<Cartilla>();
			while (rs.next()) {
			    Cartilla c = Cartilla.getMapping(rs);
				lista.add(c);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return lista;
	}
    
    public Cartilla getCartillaById(Integer id) {
		Connection con = null;
		CallableStatement stmt = null;
		Cartilla c = new Cartilla();
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
			    c = Cartilla.getMapping(rs);
			}
			
		} catch (Exception e) {
			_log.debug("error al traer cartillas", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return c;
	}
    
    public long eliminaCartilla(int idCartilla,String screenName,Date fechaBaja,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;

		int id_cartilla = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call autorizaciones.elimina_cartilla_prevencion(?,?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1,idCartilla);
			stmt.setString(2, screenName);
			stmt.setDate(3, new java.sql.Date(fechaBaja.getTime()));
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_cartilla= rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al eliminar cartilla", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cartilla;
	}

	public long recuperaCartilla(int idCartilla,String screenName,Connection connectionParameter) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_cartilla = 0;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}
				
			String sql = "{call autorizaciones.recupera_cartilla_prevencion(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idCartilla);
			stmt.setString(2,screenName);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_cartilla = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al recuperar cartilla", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cartilla;
	}

	
}
