package ar.com.ospim.tesoreria.service;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.tesoreria.beans.AjustePlanSuperador;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.facturacion.Producto;

public class LiquidacionPlanesSuperadoresServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(LiquidacionPlanesSuperadoresServiceImpl.class);

	private static LiquidacionPlanesSuperadoresServiceImpl instance = null;

	public static LiquidacionPlanesSuperadoresServiceImpl getInstance() {
		if (null == instance) {
			instance = new LiquidacionPlanesSuperadoresServiceImpl();
		}
		return instance;
	}

	public long addPrecioPlanSuperador(PrecioPlanSuperador precio, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_precio = 0;
		try {
			
			String sql ="";
			
			sql = "{call facturacion.precio_plan_superador_add(?,?,?,?,?,?)}";	
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,precio.getDescripcion());
			
			if(precio.getFechaDesde() ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (precio.getFechaDesde().getTime()));
			}
			
			if(precio.getFechaHasta() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (precio.getFechaHasta().getTime()));
			}
		
			if(precio.getEdadDesde()==null) {
			  stmt.setNull(4, Types.INTEGER );	
			}else {
			  stmt.setInt(4,precio.getEdadDesde());
			} 
			
			if(precio.getEdadHasta()==null) {
			  stmt.setNull(5, Types.INTEGER );	
		    }else {
			  stmt.setInt(5,precio.getEdadHasta());
			}
			stmt.setString(6,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar precio plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}


	public long updatePrecioPlanSuperador(PrecioPlanSuperador precio, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_precio = 0;
		try {
			
			String sql ="";
			
			sql = "{call facturacion.precio_plan_superador_update(?,?,?,?,?,?,?)}";	
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precio.getId());
			
			stmt.setString(2,precio.getDescripcion());
			
			if(precio.getFechaDesde() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (precio.getFechaDesde().getTime()));
			}
			
			if(precio.getFechaHasta() ==null){
				  stmt.setNull(4, Types.DATE );	
			}else{
				  stmt.setDate(4, new java.sql.Date (precio.getFechaHasta().getTime()));
			}
		
			if(precio.getEdadDesde()==null) {
			  stmt.setNull(5, Types.INTEGER );	
			}else {
			  stmt.setInt(5,precio.getEdadDesde());
			} 
			
			if(precio.getEdadHasta()==null) {
			  stmt.setNull(6, Types.INTEGER );	
		    }else {
			  stmt.setInt(6,precio.getEdadHasta());
			}
			stmt.setString(7,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al update precio plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	
	
	
	public Integer addPrecioPlanSuperadorPlanes(Integer precioId,Plan plan,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_add_plan(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,plan.getId());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar precio plan superador -plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public Integer addPrecioPlanSuperadorParenstescos(Integer precioId,Parentesco parentesco,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_add_parentesco(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,parentesco.getCodigo());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar precio plan superador - parentesco ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public Integer addPrecioPlanSuperadorProvincias (Integer precioId,Provincia provincia,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_add_provincia(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,provincia.getId());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar precio plan superador - provincia ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	
	public Integer addPrecioPlanSuperadorValores(Integer precioId,Producto producto,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_add_valor(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,producto.getId());
			stmt.setDouble(3, producto.getPrecioUnitario().doubleValue());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar precio plan superador -valor ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	
	
	
	public List<PrecioPlanSuperador> searchPlanSuperador(PrecioPlanSuperador filtro,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<PrecioPlanSuperador> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.precio_planes_superadores_list(?,?,?,?,?,?)}";
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getId()!=null && filtro.getId()>0) {
				stmt.setInt(1,filtro.getId());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (null != filtro.getDescripcion() && filtro.getDescripcion().trim().length() > 0) {
				stmt.setString(2, filtro.getDescripcion());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if(filtro.getFechaDesde()!=null) {
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}else {
				stmt.setNull(3, Types.DATE);	
			}
			
			
			if(filtro.getPlanes()!=null && !filtro.getPlanes().isEmpty()) {
				stmt.setInt(4, filtro.getPlanes().get(0).getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			
			if(filtro.getParentescos()!=null && !filtro.getParentescos().isEmpty()) {
				stmt.setInt(5, filtro.getParentescos().get(0).getCodigo());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			
			if(filtro.getProvincias()!=null && !filtro.getProvincias().isEmpty()) {
				stmt.setInt(6, filtro.getProvincias().get(0).getId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<PrecioPlanSuperador>();
			while (rs.next()) {
				PrecioPlanSuperador archivo = PrecioPlanSuperador.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al lista de Precios de Planes superadores", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
			
		}
		return list;
	}
	
	public List<Plan> searchPlanSuperadorPlanes(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Plan> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.precio_plan_superador_planes(?)}";
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Plan>();
			while (rs.next()) {
				Plan archivo = Plan.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al planes del precio del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
			
		}
		return list;
	}
	
	
	public List<Parentesco> searchPlanSuperadorParentescos(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Parentesco> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.precio_plan_superador_parentescos(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Parentesco>();
			while (rs.next()) {
				Parentesco archivo = Parentesco.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al parentescos del precio del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	public List<Provincia> searchPlanSuperadorProvincias(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Provincia> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.precio_plan_superador_provincias(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Provincia>();
			while (rs.next()) {
				Provincia archivo = new Provincia();
				archivo.setId(rs.getInt("id_provincia"));
				archivo.setDescripcion(rs.getString("descripcion"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al provincias del precio del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	public List<Producto> searchPlanSuperadorValores(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Producto> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.precio_plan_superador_valores(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Producto>();
			while (rs.next()) {
				Producto archivo = new Producto();
				archivo.setId(rs.getInt("orden"));
				archivo.setPrecioUnitario(rs.getBigDecimal("importe"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al importes del precio del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	
	public Integer deleteChildsPrecioPlanSuperador(Integer precioId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_delete_childs(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete childs precio plan superador  ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public Integer deletePrecioPlanSuperador(Integer precioId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.precio_plan_superador_delete(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete precio plan superador  ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	// Ajustes//
	
	public long addAjustePlanSuperador(AjustePlanSuperador precio, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_precio = 0;
		try {
			
			String sql ="";
			
			sql = "{call facturacion.ajuste_plan_superador_add(?,?,?,?,?,?,?,?,?)}";	
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,precio.getDescripcion());
			
			if(precio.getFechaDesde() ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (precio.getFechaDesde().getTime()));
			}
			
			if(precio.getFechaHasta() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (precio.getFechaHasta().getTime()));
			}
		
			if(precio.getEdadDesde()==null) {
			  stmt.setNull(4, Types.INTEGER );	
			}else {
			  stmt.setInt(4,precio.getEdadDesde());
			} 
			
			if(precio.getEdadHasta()==null) {
			  stmt.setNull(5, Types.INTEGER );	
		    }else {
			  stmt.setInt(5,precio.getEdadHasta());
			}
			
			if(precio.getPorcentaje()==null) {
				  stmt.setNull(6, Types.DOUBLE);	
			}else {
				  stmt.setDouble(6,precio.getPorcentaje());
			}
			
			if(precio.getImporte()==null) {
				  stmt.setNull(7, Types.NUMERIC);	
			}else {
				  stmt.setBigDecimal(7,precio.getImporte());
			}
			
			if(precio.getSoloUsoPersonalizado()==null) {
				  stmt.setNull(8, Types.BOOLEAN);	
			}else {
				  stmt.setBoolean(8,precio.getSoloUsoPersonalizado());
			}
			
			stmt.setString(9,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar ajuste plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public long updateAjustePlanSuperador(AjustePlanSuperador precio, String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_precio = 0;
		try {
			
			String sql ="";
			
			sql = "{call facturacion.ajuste_plan_superador_update(?,?,?,?,?,?,?,?,?,?)}";	
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precio.getId());
			
			stmt.setString(2,precio.getDescripcion());
			
			if(precio.getFechaDesde() ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (precio.getFechaDesde().getTime()));
			}
			
			if(precio.getFechaHasta() ==null){
				  stmt.setNull(4, Types.DATE );	
			}else{
				  stmt.setDate(4, new java.sql.Date (precio.getFechaHasta().getTime()));
			}
		
			if(precio.getEdadDesde()==null) {
			  stmt.setNull(5, Types.INTEGER );	
			}else {
			  stmt.setInt(5,precio.getEdadDesde());
			} 
			
			if(precio.getEdadHasta()==null) {
			  stmt.setNull(6, Types.INTEGER );	
		    }else {
			  stmt.setInt(6,precio.getEdadHasta());
			}
			
			if(precio.getPorcentaje()==null) {
				  stmt.setNull(7, Types.DOUBLE);	
			}else {
				  stmt.setDouble(7,precio.getPorcentaje());
			}
			
			if(precio.getImporte()==null) {
				  stmt.setNull(8, Types.NUMERIC);	
			}else {
				  stmt.setBigDecimal(8,precio.getImporte());
			}
			
			if(precio.getSoloUsoPersonalizado()==null) {
				  stmt.setNull(9, Types.BOOLEAN);	
			}else {
				  stmt.setBoolean(9,precio.getSoloUsoPersonalizado());
			}
			
			stmt.setString(10,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al update ajuste plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	
	public Integer addAjustePlanSuperadorPlanes(Integer precioId,Plan plan,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_add_plan(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,plan.getId());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar ajuste plan superador -plan ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public Integer addAjustePlanSuperadorParenstescos(Integer precioId,Parentesco parentesco,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_add_parentesco(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,parentesco.getCodigo());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar ajuste plan superador - parentesco ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public Integer addAjustePlanSuperadorProvincias (Integer precioId,Provincia provincia,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_add_provincia(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setInt(2,provincia.getId());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar ajuste plan superador - provincia ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	
	public Integer addAjustePlanSuperadorCuiles(Integer precioId,Afiliado afiliado,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_add_cuil(?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			stmt.setString(2,afiliado.getCuil_titular());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar ajuste plan superador -valor ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	
	public List<AjustePlanSuperador> searchPlanSuperadorAjuste(AjustePlanSuperador filtro,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<AjustePlanSuperador> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.ajuste_planes_superadores_list(?,?,?,?,?,?,?)}";
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			if (filtro.getId()!=null && filtro.getId()>0) {
				stmt.setInt(1,filtro.getId());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (null != filtro.getDescripcion() && filtro.getDescripcion().trim().length() > 0) {
				stmt.setString(2, filtro.getDescripcion());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if(filtro.getFechaDesde()!=null) {
				stmt.setDate(3, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}else {
				stmt.setNull(3, Types.DATE);	
			}
			
			
			if(filtro.getPlanes()!=null && !filtro.getPlanes().isEmpty()) {
				stmt.setInt(4, filtro.getPlanes().get(0).getId());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			
			if(filtro.getParentescos()!=null && !filtro.getParentescos().isEmpty()) {
				stmt.setInt(5, filtro.getParentescos().get(0).getCodigo());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			
			if(filtro.getProvincias()!=null && !filtro.getProvincias().isEmpty()) {
				stmt.setInt(6, filtro.getProvincias().get(0).getId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			if(filtro.getAfiliados()!=null && !filtro.getAfiliados().isEmpty()) {
				stmt.setString(7, filtro.getAfiliados().get(0).getCuil_titular());
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<AjustePlanSuperador>();
			while (rs.next()) {
				AjustePlanSuperador archivo = AjustePlanSuperador.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al lista de Ajustes de Planes superadores", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
			
		}
		return list;
	}
	
	
	public List<Plan> searchAjustePlanes(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Plan> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.ajuste_plan_superador_planes(?)}";
			
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Plan>();
			while (rs.next()) {
				Plan archivo = Plan.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al planes del ajuste del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
			
		}
		return list;
	}
		
	
	public List<Parentesco> searchAjusteParentescos(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Parentesco> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.ajuste_plan_superador_parentescos(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Parentesco>();
			while (rs.next()) {
				Parentesco archivo = Parentesco.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al parentescos del ajuste del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	public List<Provincia> searchAjusteProvincias(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Provincia> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.ajuste_plan_superador_provincias(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Provincia>();
			while (rs.next()) {
				Provincia archivo = new Provincia();
				archivo.setId(rs.getInt("id_provincia"));
				archivo.setDescripcion(rs.getString("descripcion"));
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al provincias del ajuste del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	public List<Afiliado> searchAjusteAfiliados(Integer id,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> list = null;
		try {
			String sql = "";
			sql = "{call facturacion.ajuste_plan_superador_cuiles(?)}";
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,id);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado archivo = new Afiliado();
				archivo.setCuil_titular(rs.getString("cuil_titular"));
				archivo.setApellido(rs.getString("apellido_nombre"));
				archivo.setNombre("");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error cuiless del ajuste del plan superador", e);
			throw new SystemException(e);
		} finally {
			
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	
	public Integer deleteChildsAjustePlanSuperador(Integer precioId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_delete_childs(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete childs ajuste plan superador  ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}
	
	public Integer deleteAjustePlanSuperador(Integer precioId,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int id_precio = 0;
		try {
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
			} else {
				con = connectionParameter;
			}

			String sql = "{call facturacion.ajuste_plan_superador_delete(?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,precioId);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				id_precio = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al delete ajuste plan superador  ", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_precio;
	}

	public List<Afiliado> getBusquedaGrupoFliar(String cuil_titular) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call facturacion.buscar_grupo_fliar(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil_titular"),
						rs.getInt("inte"), 
						rs.getInt("id_parentesco_sss"), 
						rs.getString("parentesco"),
						rs.getString("nombre"), 
						rs.getString("apellido"),
						rs.getString("tdoc"), 
						rs.getString("documento"),
						rs.getString("seccional"), 
						rs.getDate("ingreso"),
						rs.getDate("baja_fecha"));
				 
				bp.setVigen_fecha(rs.getDate("vigen_fecha"));
				bp.setDiscapacitado(rs.getString("discapacitado"));
                bp.setEdad(rs.getInt("edad"));
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	public List<PrecioPlanSuperador> cotizar(Integer plan_id,Integer provincia_id,Date fecha,String[]grupoFliar,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		PreparedStatement stmt = null;
		List<PrecioPlanSuperador> list = null;
		try {
			String sql = "";
			sql = "SELECT * from facturacion.planes_superadores_cotizar_v01(?, ?, ?, ?::text[])";
	           
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareStatement(sql);
			
			if (plan_id!=null && plan_id>0) {
				stmt.setInt(1,plan_id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (provincia_id!=null && provincia_id>0) {
				stmt.setInt(2,provincia_id);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			
			if(fecha!=null) {
				stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			}else {
				stmt.setNull(3, Types.DATE);	
			}
			
			StringBuilder sb = new StringBuilder();
	        sb.append("{");
	        for (int i = 0; i < grupoFliar.length; i++) {
	            sb.append("\"").append(grupoFliar[i].replace("\"", "\\\"")).append("\"");
	            if (i < grupoFliar.length - 1) {
	                sb.append(",");
	            }
	        }
	        sb.append("}");
	        stmt.setString(4, sb.toString());

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<PrecioPlanSuperador>();
			while (rs.next()) {
				PrecioPlanSuperador archivo = PrecioPlanSuperador.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al Cotizar plan", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}
	
	public Map<String,Integer> getPlanesEquivalencias() {
		Connection con = null;
		CallableStatement stmt = null;
		Map<String,Integer> ret = new HashMap<String,Integer>();
		try {
			String sql = "{call comercial.buscar_planes_equivalencias()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ret.put(rs.getString("plan_solicitud"),  rs.getInt("plan_facturacion"));
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public List<AjustePlanSuperador> getAjustesPersonalizables(Integer plan_id,Integer provincia_id,Date fecha,String[]grupoFliar,Connection connectionParameter)
			throws SystemException {
		Connection con = null;
		PreparedStatement stmt = null;
		List<AjustePlanSuperador> list = null;
		try {
			String sql = "";
			sql = "SELECT * from facturacion.planes_superadores_ajustes_personalizables_v01(?, ?, ?, ?::text[])";
	           
			
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();			
			} else {
				con = connectionParameter;
			}
			
			stmt = con.prepareStatement(sql);
			
			if (plan_id!=null && plan_id>0) {
				stmt.setInt(1,plan_id);
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (provincia_id!=null && provincia_id>0) {
				stmt.setInt(2,provincia_id);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			
			if(fecha!=null) {
				stmt.setDate(3, new java.sql.Date(fecha.getTime()));
			}else {
				stmt.setNull(3, Types.DATE);	
			}
			
			StringBuilder sb = new StringBuilder();
	        sb.append("{");
	        for (int i = 0; i < grupoFliar.length; i++) {
	            sb.append("\"").append(grupoFliar[i].replace("\"", "\\\"")).append("\"");
	            if (i < grupoFliar.length - 1) {
	                sb.append(",");
	            }
	        }
	        sb.append("}");
	        stmt.setString(4, sb.toString());

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<AjustePlanSuperador>();
			while (rs.next()) {
				AjustePlanSuperador archivo = AjustePlanSuperador.getMapping(rs,"");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al taer ajustes personalizables", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return list;
	}

	
}
