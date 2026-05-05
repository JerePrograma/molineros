package ar.com.ospim.hoteles.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import ar.com.ospim.farmaciaOspim.beans.ArchivoMedEspecial;
import ar.com.ospim.procesaArchivos.beans.ArchivoVademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemFarmaciaTotal;
import ar.com.ospim.farmaciaOspim.beans.TiposDeVentas;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarFarmaciaOspimException;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleCerrarVademecumFarmaciaOspimException;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.Retencion;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.hoteles.beans.Consumo;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.beans.Personal;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.beans.PrestamoCuota;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.beans.ProductoConfiteria;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.beans.Reserva;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleDesglose;
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;


	public class HotelesServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(HotelesServiceImpl.class);
	
	private static HotelesServiceImpl instance = null;

	public static HotelesServiceImpl getInstance() {
		if (null == instance) {
			instance = new HotelesServiceImpl();
		}
		return instance;
	}
	
	public List<Habitacion> getHabitaciones(String codHotel  , String  grupo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Habitacion> habitaciones = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_habitaciones(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(grupo == null || grupo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, grupo);   
			}	
		
			ResultSet rs = stmt.executeQuery();
			habitaciones = new ArrayList<Habitacion>();
			
			while (rs.next()) {
				Habitacion archivo = Habitacion.getMapping(rs,"");
				habitaciones.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Habitaciones de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return habitaciones;		
	}
	
	public List<ProductoCategoria> getProductosCategorias(String codHotel)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ProductoCategoria> categorias = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_productos_categorias(?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}
			
			ResultSet rs = stmt.executeQuery();
			categorias = new ArrayList<ProductoCategoria>();
			
			while (rs.next()) {
				ProductoCategoria archivo = ProductoCategoria.getMapping(rs,"");
				categorias.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Categorias de Productos del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return categorias;		
	}
	
	
	public List<ProductoConfiteria> getProductos(String codHotel  , String  categoria, String producto)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ProductoConfiteria> productos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_productos(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(categoria == null || categoria  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, categoria);   
			}
			
			if(producto == null || producto  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, producto);   
			}
		
			ResultSet rs = stmt.executeQuery();
			productos = new ArrayList<ProductoConfiteria>();
			
			while (rs.next()) {
				ProductoConfiteria archivo = ProductoConfiteria.getMapping(rs,"");
				productos.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Productos de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return productos;		
	}

	public Integer actualizarConsumos(String codHotel,String tipo,String producto,Integer cantidad,String unidadId,Integer personalId,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.actualizar_consumos(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(tipo == null || tipo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);   
			}
			
			if(producto == null || producto  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, producto);   
			}
			
			if(cantidad==null) {
				stmt.setNull(4, Types.INTEGER);
			}else {
				stmt.setInt(4,cantidad);
			}
		
			if(unidadId == null || unidadId  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, unidadId);   
			}
			
			if(personalId==null) {
				stmt.setNull(6, Types.INTEGER);
			}else {
				stmt.setInt(6,personalId);
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(7, Types.VARCHAR );
			}else{
				stmt.setString(7, usr);   
			}
			
			stmt.executeQuery();
			
			
		} catch (Exception e) {
			logger.error("Error al actualizar consumo Productos de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}
	
	
	public Integer eliminarConsumos(String codHotel,String tipo,String producto,String unidadId,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.eliminar_consumos(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(tipo == null || tipo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);   
			}
			
			if(producto == null || producto  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, producto);   
			}
			
			if(unidadId == null || unidadId  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, unidadId);   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, usr);   
			}
			
			stmt.executeQuery();
			
			
		} catch (Exception e) {
			logger.error("Error al eliminar consumo Productos de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer cambiarEstado(String codHotel,String tipo,String producto,String unidadId,String usr,String estado)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.cambiar_estado(?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(tipo == null || tipo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);   
			}
			
			if(producto == null || producto  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, producto);   
			}
			
			if(unidadId == null || unidadId  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, unidadId);   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, usr);   
			}
			
			if(estado == null || estado  =="") {
				stmt.setNull(6, Types.VARCHAR );
			}else{
				stmt.setString(6, estado);   
			}
			
			stmt.executeQuery();
			
			
		} catch (Exception e) {
			logger.error("Error al cambiar estado consumo Productos de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	
	
	public List<Consumo> getConsumos(String codHotel  , String  tipo, String unidadId)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Consumo> consumos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_consumos(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(tipo == null || tipo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);   
			}
			
			if(unidadId == null || unidadId  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, unidadId);   
			}
		
			ResultSet rs = stmt.executeQuery();
			consumos = new ArrayList<Consumo>();
			
			while (rs.next()) {
				Consumo archivo = Consumo.getMapping(rs,"",tipo);
				consumos.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Consumos Hotel Unidades", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return consumos;		
	}

	public List<Mesa> getMesas(String codHotel  , String  grupo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Mesa> mesas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_Mesas(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(grupo == null || grupo  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, grupo);   
			}	
		
			ResultSet rs = stmt.executeQuery();
			mesas = new ArrayList<Mesa>();
			
			while (rs.next()) {
				Mesa archivo = Mesa.getMapping(rs,"");
				mesas.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Mesas de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return mesas;		
	}
	
	public List<Mesa> getPersonalByMesas(String codHotel  , Integer  mesa)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Mesa> mesas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_personal_por_mesas(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(mesa == null || mesa  ==0) {
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, mesa);   
			}	
		
			ResultSet rs = stmt.executeQuery();
			mesas = new ArrayList<Mesa>();
			
			while (rs.next()) {
				Mesa archivo = Mesa.getMapping(rs,"");
				mesas.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Personal por Mesas de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return mesas;		
	}


	public List<Mesa> getMesasByPersonal(String codHotel  , Integer  personal)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Mesa> mesas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_mesas_por_personal(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(personal == null) {
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, personal);   
			}	
		
			ResultSet rs = stmt.executeQuery();
			mesas = new ArrayList<Mesa>();
			
			while (rs.next()) {
				Mesa archivo = Mesa.getMapping(rs,"");
				mesas.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Mesas por Personal de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return mesas;		
	}

	
	
	public Integer updateMesa(Mesa mesa,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.update_mesa(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (mesa.getHotel() == null || mesa.getHotel()  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, mesa.getHotel());  
				
			}	
			if(mesa.getNumero() ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, mesa.getNumero());   
			}
			
			if(mesa.getDescripcion() == null || mesa.getDescripcion()  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, mesa.getDescripcion());   
			}
			
			
			if(mesa.getGrupo() == null || mesa.getGrupo()  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, mesa.getGrupo());   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, usr);   
			}
			
			stmt.executeQuery();
			ret=mesa.getNumero();
			
		} catch (Exception e) {
			logger.error("Error al actualizar mesas del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer deleteMesa(Mesa mesa,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_mesa(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (mesa.getHotel() == null || mesa.getHotel()  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, mesa.getHotel());  
				
			}	
			if(mesa.getNumero() ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, mesa.getNumero());   
			}
			
			stmt.executeQuery();
			ret=mesa.getNumero();
			
		} catch (Exception e) {
			logger.error("Error al eliminar mesas del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer updateHabitacion(Habitacion habitacion,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.update_habitacion(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (habitacion.getHotel() == null || habitacion.getHotel()  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, habitacion.getHotel());  
				
			}	
			if(habitacion.getNumero() ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, habitacion.getNumero());   
			}
			
			if(habitacion.getDescripcion() == null || habitacion.getDescripcion()  =="") {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, habitacion.getDescripcion());   
			}
			
			
			if(habitacion.getGrupo() == null || habitacion.getGrupo()  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, habitacion.getGrupo());   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, usr);   
			}
			
			stmt.executeQuery();
			ret=habitacion.getNumero();
			
		} catch (Exception e) {
			logger.error("Error al actualizar habitaciones del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer deleteHabitacion(Habitacion habitacion,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_habitacion(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (habitacion.getHotel() == null || habitacion.getHotel()  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, habitacion.getHotel());  
				
			}	
			if(habitacion.getNumero() ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, habitacion.getNumero());   
			}
			
			stmt.executeQuery();
			ret=habitacion.getNumero();
			
		} catch (Exception e) {
			logger.error("Error al eliminar habitaciones del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}


	public String updateCategoria(ProductoCategoria categoria,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		String ret="";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.update_categoria(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (categoria.getHotel() == null || categoria.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, categoria.getHotel());  
			}	
			if(categoria.getCodigo() == null || categoria.getCodigo().equalsIgnoreCase("")) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, categoria.getCodigo());   
			}
			
			if(categoria.getDescripcion() == null || categoria.getDescripcion().equalsIgnoreCase("")) {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, categoria.getDescripcion());   
			}
			
			
			if(categoria.getAplicaA()  == null || categoria.getAplicaA().equalsIgnoreCase("")) {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, categoria.getAplicaA());   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, usr);   
			}
			
			stmt.executeQuery();
			ret=categoria.getCodigo();
			
		} catch (Exception e) {
			logger.error("Error al actualizar categorias del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public String deleteCategoria(ProductoCategoria categoria,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		String ret="";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_categoria(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (categoria.getHotel() == null || categoria.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, categoria.getHotel());  
				
			}	
			if(categoria.getCodigo()==null) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, categoria.getCodigo());   
			}
			
			stmt.executeQuery();
			ret=categoria.getCodigo();
			
		} catch (Exception e) {
			logger.error("Error al eliminar categorias del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	
	public String updateProducto(ProductoConfiteria producto,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		String ret="";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.update_producto(?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (producto.getHotel() == null || producto.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, producto.getHotel());  
			}	
			if(producto.getCodigo() == null || producto.getCodigo().equalsIgnoreCase("")) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, producto.getCodigo());   
			}
			
			if(producto.getDescripcion() == null || producto.getDescripcion().equalsIgnoreCase("")) {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, producto.getDescripcion());   
			}
			
			if(producto.getDescripcionCorta() == null || producto.getDescripcionCorta().equalsIgnoreCase("")) {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, producto.getDescripcionCorta());   
			}
			
			
			if(producto.getCategoria() == null || producto.getCategoria().getCodigo()==null ||producto.getCategoria().getCodigo().equalsIgnoreCase("")) {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, producto.getCategoria().getCodigo());   
			}
			
			
			if(producto.getPrecio()==null) {
				stmt.setNull(6, Types.DOUBLE );
			}else{
				stmt.setDouble(6, producto.getPrecio());   
			}
			
			stmt.setBoolean(7,producto.isHabilitadoHabitaciones());
			
			if(usr == null || usr  =="") {
				stmt.setNull(8, Types.VARCHAR );
			}else{
				stmt.setString(8, usr);   
			}
			
			stmt.executeQuery();
			ret=producto.getCodigo();
			
		} catch (Exception e) {
			logger.error("Error al actualizar productos del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}
	
	public String deleteProducto(ProductoConfiteria producto,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		String ret="";
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_producto(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (producto.getHotel() == null || producto.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, producto.getHotel());  
				
			}	
			if(producto.getCodigo()==null) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, producto.getCodigo());   
			}
			if(usr==null) {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, usr);   
			}
			stmt.executeQuery();
			ret=producto.getCodigo();
			
		} catch (Exception e) {
			logger.error("Error al eliminar producto del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public List<Personal> getPersonal(String codHotel  , String  categoria, Integer id)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Personal> personalList = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_personal(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(categoria == null || categoria  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, categoria);   
			}
			
			if(id== null || id == 0) {
				stmt.setNull(3, Types.INTEGER);
			}else{
				stmt.setInt(3, id);   
			}
		
			ResultSet rs = stmt.executeQuery();
			personalList = new ArrayList<Personal>();
			
			while (rs.next()) {
				Personal archivo = Personal.getMapping(rs,"");
				personalList.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Personal de Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return personalList;		
	}

	
	public Integer updatePersonal(Personal personal,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.update_personal(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (personal.getHotel() == null || personal.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, personal.getHotel());  
			}	
			if(personal.getId() == null ) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, personal.getId());   
			}
			
			if(personal.getApellido() == null || personal.getApellido().equalsIgnoreCase("")) {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, personal.getApellido());   
			}
			
			if(personal.getNombre() == null || personal.getNombre().equalsIgnoreCase("")) {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, personal.getNombre());   
			}
			
			
			if(personal.getCategoria() == null || personal.getCategoria().equalsIgnoreCase("")) {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, personal.getCategoria());   
			}
			
			
			if(personal.getPassword() == null || personal.getPassword().equalsIgnoreCase("")) {
				stmt.setNull(6, Types.VARCHAR );
			}else{
				stmt.setString(6, personal.getPassword());   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(7, Types.VARCHAR );
			}else{
				stmt.setString(7, usr);   
			}
			
			stmt.executeQuery();
			ret=personal.getId();
			
		} catch (Exception e) {
			logger.error("Error al actualizar personal del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}
	
	public Integer deletePersonal(Personal personal,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_personal(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (personal.getHotel() == null || personal.getHotel().equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, personal.getHotel());  
				
			}	
			if(personal.getId()==null) {
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, personal.getId());   
			}
			if(usr==null) {
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, usr);   
			}
			stmt.executeQuery();
			ret=personal.getId();
			
		} catch (Exception e) {
			logger.error("Error al eliminar personal del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer deleteMesasAsignadasPersonal(String hotel,Integer personalId)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call hoteles.delete_unidades_asignadas_personal(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (hotel == null || hotel.equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, hotel);  
				
			}	
			if(personalId==null) {
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, personalId);   
			}
			stmt.executeQuery();
			ret=personalId;
			
		} catch (Exception e) {
			logger.error("Error al eliminar asignacion mesas personal del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}
	
	
	
	public Integer insertMesasAsignadasPersonal(String hotel, Integer personal,Integer mesa,String usr)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.insert_unidades_asignadas_personal(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (hotel == null || hotel.equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, hotel);  
			}	
			if(personal == null ) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, personal);   
			}
			
			if(mesa == null ) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, mesa);   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, usr);   
			}
			
			stmt.executeQuery();
			ret=personal;
			
		} catch (Exception e) {
			logger.error("Error al actualizar asignacion mesas personal del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}
	
	
	public List<Reserva> getReservasActivas(Integer anio  ,Date fecha )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reserva> reservas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.buscar_reservas_activas(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (anio == null || anio  ==0) {
				stmt.setNull(1, Types.INTEGER );
			}else{
				stmt.setInt(1, anio);   
			}	
			
			
			if(fecha ==null){
				  stmt.setNull(2, Types.DATE );	
			}else{
				  stmt.setDate(2, new java.sql.Date (fecha.getTime()));
			}
			
			
			ResultSet rs = stmt.executeQuery();
			reservas = new ArrayList<Reserva>();
			
			while (rs.next()) {
				Reserva archivo = Reserva.getMapping(rs,"");
				reservas.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Reservas Activas Hotel ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return reservas;		
	}

	public Integer insertOrdenConsumo(String hotel,String tipo, Integer mesa, Integer personal,Integer reserva,String usr,String nroFactura)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.inserta_orden_consumo(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (hotel == null || hotel.equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, hotel);  
			}	
			
			if (tipo == null || tipo.equalsIgnoreCase("")) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);  
			}
				
			if(mesa == null) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, mesa);   
			}
			
			if(personal == null ) {
				stmt.setNull(4, Types.INTEGER );
			}else{
				stmt.setInt(4, personal);   
			}
			
			if(reserva == null ) {
				stmt.setNull(5, Types.INTEGER );
			}else{
				stmt.setInt(5, reserva);   
			}
			
			if(usr == null || usr  =="") {
				stmt.setNull(6, Types.VARCHAR );
			}else{
				stmt.setString(6, usr);   
			}
			
			if(nroFactura == null || nroFactura  =="") {
				stmt.setNull(7, Types.VARCHAR );
			}else{
				stmt.setString(7, nroFactura);   
			}
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ret = rs.getInt(1);
			}
			
		} catch (Exception e) {
			logger.error("Error al insertar orden consumo del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}

	public Integer deleteConsumosActivos(String hotel,String tipo, Integer mesa)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		Integer ret=0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.delete_consumos_activos(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (hotel == null || hotel.equalsIgnoreCase("")) {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, hotel);  
			}	
			
			if (tipo == null || tipo.equalsIgnoreCase("")) {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, tipo);  
			}
				
			if(mesa == null) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, mesa);   
			}
						
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ret = rs.getInt(1);
			}
			
		} catch (Exception e) {
			logger.error("Error al eliminar consumos activos del Hotel", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	    return ret;
	}


	
	public Integer getTotalConsumosPorReserva(Integer idReserva   )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Integer total = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_total_consumos_por_reserva(?)}";
			stmt = con.prepareCall(sql.toString());
			
			
			
			
			if (idReserva == null || idReserva  ==0) {
				stmt.setNull(1, Types.INTEGER );
			}else{
				stmt.setInt(1, idReserva);   
			}	

			
			ResultSet rs = stmt.executeQuery();

			
			while (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Error al obtener total consumido por reserva ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return total;		
	}

	

	public Integer getTraeTotalReserva(Integer anio, Integer idReserva   )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Integer total = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_total_reserva(?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (anio == null || anio  ==0) {
				stmt.setNull(1, Types.INTEGER );
			}else{
				stmt.setInt(1, anio);   
			}	
			
			
			if (idReserva == null || idReserva  ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, idReserva);   
			}	

			
			ResultSet rs = stmt.executeQuery();

			
			while (rs.next()) {
				total = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("Error al obtener trae_total_reserva ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return total;		
	}
	
	public List<Reserva> getReservasByFechaFin(String codHotel,Integer anio,Integer reserva,String habitacion,Date fechaDde,Date fechaHta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Reserva> reservas = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.buscar_reservas_by_fecha_fin(?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			if (anio == null || anio  ==0) {
				stmt.setNull(1, Types.INTEGER );
			}else{
				stmt.setInt(1, anio);   
			}	
			
			if (reserva == null || reserva  ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, reserva);   
			}
			
			if (habitacion == null || "".equalsIgnoreCase(habitacion) ){
				stmt.setNull(3, Types.VARCHAR );
			}else{
				stmt.setString(3, habitacion);   
			}
			
			
			if(fechaDde ==null){
				  stmt.setNull(4, Types.DATE );	
			}else{
				  stmt.setDate(4, new java.sql.Date (fechaDde.getTime()));
			}
			
			
			if(fechaHta ==null){
				  stmt.setNull(5, Types.DATE );	
			}else{
				  stmt.setDate(5, new java.sql.Date (fechaHta.getTime()));
			}
			
			ResultSet rs = stmt.executeQuery();
			reservas = new ArrayList<Reserva>();
			
			while (rs.next()) {
				Reserva archivo = Reserva.getMapping(rs,"");
				archivo.setAnio(anio);
				reservas.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Reservas por fecha Fin Hotel ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return reservas;		
	}

	public List<Consumo> getUltimoConsumoAsignadoHabitacion(String codHotel  , String  anio, Integer mesaid, String habitacionid)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Consumo> consumos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.trae_ultimo_consumo_habitacion_pendiente_facturacion(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			
			if (codHotel == null || codHotel  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, codHotel);   
			}	
			if(anio == null || anio  =="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, anio);   
			}
			
			if(mesaid == null || mesaid  ==0) {
				stmt.setNull(3, Types.INTEGER);
			}else{
				stmt.setInt(3, mesaid);   
			}
			
			if(habitacionid == null || habitacionid  =="") {
				stmt.setNull(4, Types.VARCHAR );
			}else{
				stmt.setString(4, habitacionid);   
			}
		
			ResultSet rs = stmt.executeQuery();
			consumos = new ArrayList<Consumo>();
			
			while (rs.next()) {
				Consumo consumo = new Consumo();
				consumo.setCantidad(rs.getInt("cantidad"));
				consumo.setPrecio(rs.getDouble("precio"));
				if(rs.getInt("mesa_id")!=0) {
					Mesa mesa = new Mesa();
					mesa.setNumero(rs.getInt("mesa_id"));
					consumo.setMesa(mesa);
				}
				Habitacion habitacion = new Habitacion();
				habitacion.setDescripcion(rs.getString("habitacion_id"));
				habitacion.setNumero(rs.getInt("reserva_id"));
				consumo.setHabitacion(habitacion);
				ProductoConfiteria producto = new ProductoConfiteria();
				producto.setCodigo(rs.getString("producto_id"));
				producto.setDescripcion(rs.getString("descripcion"));
				
				Cliente cliente = new Cliente();
				cliente.setApellido(rs.getString("cliente"));
				consumo.setCliente(cliente);
				
				consumo.setProducto(producto);
				
				consumos.add(consumo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Ultimo Consumo Asignado Habitacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return consumos;		
	}

	public Reserva getReservaById(String codHotel,Integer anio,Integer reservaId)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Reserva reserva = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.buscar_reservas_por_id(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			if (anio == null || anio  ==0) {
				stmt.setNull(1, Types.INTEGER );
			}else{
				stmt.setInt(1, anio);   
			}	
			
			if (reservaId == null || reservaId  ==0) {
				stmt.setNull(2, Types.INTEGER );
			}else{
				stmt.setInt(2, reservaId);   
			}
			
			
			
			ResultSet rs = stmt.executeQuery();
			reserva = new Reserva();
			
			while (rs.next()) {
				reserva = Reserva.getMapping(rs,"");
				reserva.setAnio(anio);
				reserva.setFechaDesdeId(rs.getInt("fecha_desde_id"));
				reserva.setFechaHastaId(rs.getInt("fecha_hasta_id"));
				reserva.setPagado(rs.getDouble("pagado"));
				reserva.setTotalAPagar(rs.getDouble("total_a_pagar"));
				reserva.setTotalCochera(rs.getDouble("total_cochera"));
				reserva.setSenia(rs.getDouble("senia"));
				reserva.setIdCliente(rs.getInt("cliente_id"));
				
			}
		} catch (Exception e) {
			logger.error("Error al buscar Reservas por ID Fin Hotel ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return reserva;		
	}

	
public Long updateRecibo(Recibo recibo, String usuario) throws Exception{
		
	    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null, stmtCheque = null;
		
		Long idRecibo=0L;
		Boolean nuevo=false;
		try {
			
			con = ConnectionHelper.getConnectionForTransaction();
//			if(recibo.getNumero()==0L) {
				nuevo=true;
//				idRecibo =  getReciboProximoNro(recibo.getSucursal(),con);
				
				String sql = "{call hoteles.inserta_recibo(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";	
				stmt = con.prepareCall(sql.toString());
				stmt.setBigDecimal(1,new BigDecimal(recibo.getNumero()));
				stmt.setString(2,recibo.getSucursal());
				
				
				if(recibo.getDescripcion()!=null) {
				  stmt.setString(3,recibo.getDescripcion());
				}else {
				  stmt.setNull(3, Types.VARCHAR); 	
				}
				
				if(recibo.getFecha()!=null) {
					stmt.setDate(4, new java.sql.Date(recibo.getFecha().getTime()));
				}else {
					stmt.setNull(4,Types.DATE);
				}
				
				if(StringUtils.checkNotEmpty(recibo.getCliente().getRazonSocial())) {
					stmt.setString(5, recibo.getCliente().getRazonSocial());
				}else {
					stmt.setNull(5, Types.VARCHAR);
				}
				
				if(StringUtils.checkNotEmpty(recibo.getCliente().getCuit())) {
					stmt.setString(6, recibo.getCliente().getCuit());
				}else {
					stmt.setNull(6, Types.VARCHAR);
				}
				
				String tipoComprobante="";
				String sucursal="";
				String letra="";
				String numero="";
				Integer clienteId=0;
				Integer anio=0;
				
				if(recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null && recibo.getReserva().getIdReserva()>0) {
					tipoComprobante="RES";
					sucursal=recibo.getSucursal();
					numero=recibo.getReserva().getIdReserva().toString();
					clienteId=recibo.getReserva().getIdCliente();
					anio=recibo.getReserva().getAnio();
				}else {
					if(recibo.getFactura()!=null && recibo.getFactura().getNumero() !=null) {
						tipoComprobante=recibo.getFactura().getTipo();
						sucursal=recibo.getFactura().getSucursal();
						numero=recibo.getFactura().getNumero();
						//clienteId=recibo.getFactura().getCliente().getId();
						anio=0;
					}	
				}
				
				stmt.setString(7, tipoComprobante);
				stmt.setString(8, sucursal);
				stmt.setString(9, letra);
				stmt.setString(10, numero);
				stmt.setInt(11, clienteId);
				stmt.setInt(12, anio);
				stmt.setDouble(13, recibo.getTotal());
				stmt.setString(14, usuario);
				
				stmt.executeUpdate();
				
/*				
				
			}else {
				idRecibo=recibo.getNumero();
				
				String sql = "{call hoteles.update_recibo(?,?,?,?,?,?,?,?) }";	
				stmt = con.prepareCall(sql.toString());
				
				stmt.setBigDecimal(1,new BigDecimal(idRecibo));
				stmt.setString(2,recibo.getSucursal());
				stmt.setString(3,recibo.getDescripcion());
				if(StringUtils.checkNotEmpty(recibo.getCliente().getRazonSocial())) {
					stmt.setString(4, recibo.getCliente().getRazonSocial());
				}else {
					stmt.setNull(4, Types.VARCHAR);
				}
				
				if(StringUtils.checkNotEmpty(recibo.getCliente().getCuit())) {
					stmt.setString(5, recibo.getCliente().getCuit());
				}else {
					stmt.setNull(5, Types.VARCHAR);
				}
				Integer clienteId=0;
				if(recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null && recibo.getReserva().getIdReserva()>0) {
					clienteId=recibo.getReserva().getIdCliente();
				}	
				stmt.setInt(6, clienteId);
				stmt.setDouble(7, recibo.getTotal());
				stmt.setString(8, usuario);
				stmt.executeUpdate();
			}
*/			
			
			String sql1 = null;
			sql1 = "{call hoteles.delete_recibo_ingreso (?, ?)}";
			stmt1 = con.prepareCall(sql1.toString());
			stmt1.setString(1,recibo.getSucursal());
			stmt1.setLong(2,recibo.getNumero());
			stmt1.executeUpdate();
			
			
			String sql3 = null;
			sql3 = "{call hoteles.inserta_recibo_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
			stmt3 = con.prepareCall(sql3.toString());
			
			String sqlCheque = null;
			sqlCheque = "{call uoma.insertar_cheques_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			stmtCheque = con.prepareCall(sqlCheque.toString());
			
			for (Iterator<FacturaIngreso> iterator = recibo.getIngresos().iterator(); iterator.hasNext();) {
				FacturaIngreso fi =  iterator.next();
				Ingreso i =  fi.getIngreso();
				stmt3.setLong(1,recibo.getNumero());
			
				if (i.getTipo().equals("Cheque")) {
					stmt3.setBigDecimal(2, new BigDecimal( i.getNumeroStr()) ) ;
				} else {
					stmt3.setBigDecimal(2, null);
				}
	
				if (i.getTipo().equals("Cheque")) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta D�bito") ) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
					stmt3.setNull(3, java.sql.Types.INTEGER);
				} else {
					stmt3.setNull(3, java.sql.Types.INTEGER);
				}
	
				if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") || 
						i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ||
						i.getTipo().equalsIgnoreCase("Tarjeta D�bito")) {
					stmt3.setString(4, i.getNumeroStr());
				} else {
					stmt3.setNull(4, Types.VARCHAR);
				}
	
				BigDecimal importe = null;
				stmt3.setBigDecimal(5, i.getImporte());
				
				Date fecha = null;
				stmt3.setDate(6, new java.sql.Date(i.getFecha().getTime()));
				
				if (i.getTipo().equals("Cheque")) {
					stmt3.setInt(7, Cheque.Estado.RECIBIDO);
				}else {
					stmt3.setNull(7, java.sql.Types.INTEGER);
				}
				stmt3.setString(8, usuario);
	
				if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
					stmt3.setInt(9, i.getCuentaBancaria().getId_cuenta_bcria());
					stmt3.setInt(10, DepositoBancario.ID_TIPO_TRANSFERENCIA);
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, TarjetaDebitoCredito.ID_TIPO_CREDITO);
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta D�bito") ) {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setInt(10,  TarjetaDebitoCredito.ID_TIPO_DEBITO );
				}else if (i.getTipo().equalsIgnoreCase("Retenci�n No Identificada") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.GRAL);
				}else if (i.getTipo().equalsIgnoreCase("Retenci�n IVA") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.IVA);
				} else if (i.getTipo().equalsIgnoreCase("Retenci�n Ingresos Brutos") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.IIBB);
				}else if (i.getTipo().equalsIgnoreCase("Retenci�n Seguridad Social") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.SUSS);
				}else {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setNull(10, Types.INTEGER);
				}
	
				stmt3.setNull(11, Types.INTEGER);
				stmt3.setNull(12, Types.INTEGER);
				if(i.getTipo().equals("Cheque")){
					stmt3.setInt(13, i.getCuentaBancaria().getId_cuenta_bcria());
				}else{
					stmt3.setNull(13, Types.INTEGER);
				}
				
				if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") || i.getTipo().equalsIgnoreCase("Tarjeta D�bito")) {
					stmt3.setInt(14, i.getEmisor());
				 	stmt3.setInt(15, i.getCuotas());
				} else {
					stmt3.setNull(14, Types.INTEGER);
					stmt3.setNull(15, Types.INTEGER);
				}
				
				stmt3.setString(16, recibo.getSucursal());
				
				stmt3.executeUpdate();
				
				
				if (i.getTipo().equals("Cheque")) {
					stmtCheque.setBigDecimal(1, new BigDecimal( i.getNumeroStr()) ) ;
					if(((Cheque)i).getCuit()!=null){
						stmtCheque.setString(2, ((Cheque)i).getCuit());
					}else {
						stmtCheque.setNull(2,Types.VARCHAR);
					}
					stmtCheque.setNull(3,Types.VARCHAR); //A nombre de
					stmtCheque.setDate(4, new java.sql.Date( ((Cheque)i).getFecha().getTime()));
					stmtCheque.setBigDecimal(5,((Cheque)i).getImporte());
					stmtCheque.setString(6, usuario);
					stmtCheque.setBoolean(7, false); //prestador
					stmtCheque.setNull(8, Types.VARCHAR); //concepto
					stmtCheque.setInt(9, ((Cheque)i).getCuentaBancaria().getId_cuenta_bcria());
					stmtCheque.setString(10,"C");
					stmtCheque.setInt(11, ((Cheque)i).getBanco().getId_banco());
					stmtCheque.setInt(12,  ((Cheque)i).getEstado().getId());
					
					stmtCheque.executeUpdate();
				}
				
				

			}
			
// Actualiza Sistema Reservas Hotel	
/*			
			String sql2 = null;
			sql2 = "{call hoteles.update_recibo_reserva ( ?, ?, ?, ?, ?, ?, ? , ?,?,?,?)}";
			stmt2 = con.prepareCall(sql2.toString());
			if(recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null && recibo.getReserva().getIdReserva()>0) {
				
				String cad="Reserva " + recibo.getReserva().getIdReserva() + " desde " + sdf.format(recibo.getReserva().getFechaDesde()) +
						" hasta " +sdf.format(recibo.getReserva().getFechaDesde()) + " por " +recibo.getCliente().getClienteNombre();
				stmt2.setInt(1,recibo.getReserva().getAnio());
				stmt2.setInt(2,recibo.getReserva().getIdReserva());
				stmt2.setLong(3,idRecibo);
				if(recibo.getFecha()!=null) {
					stmt2.setDate(4, new java.sql.Date(recibo.getFecha().getTime()));
				}else {
					stmt2.setNull(4,Types.DATE);
				}
				
				stmt2.setInt(5,recibo.getReserva().getIdCliente());
				stmt2.setInt(6,recibo.getReserva().getFechaDesdeId());
				stmt2.setInt(7,recibo.getReserva().getFechaHastaId());
				stmt2.setDouble(8,recibo.getTotal());
				if(recibo.getTotalAnterior()!=null) {
					stmt2.setDouble(9,recibo.getTotalAnterior());	
				}else {
					stmt2.setDouble(9,0D);
				}
				
				stmt2.setBoolean(10, nuevo);
				stmt2.setString(11, cad);
				stmt2.executeUpdate();
			}
*/			

			con.commit();
			
			
		} catch (SQLException e) {
			logger.error("Error al insertar recibo Hoteles uoma", e);
			
			ConnectionHelper.rollback(con);
			
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al insertar recibo Hoteles uoma", e);
			throw new Exception(e);
			
		} finally {
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt, con);
		}

		return idRecibo;
	}


    public Long getReciboProximoNro(String sucursal,Connection connection)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	Long nro = null;
	try {
		
		if (connection == null) {
			con = ConnectionHelper.getConnection();
		} else {
			con = connection;
		}
		
		String sql = "{call  hoteles.recibo_proximo_numero(?)}";
		stmt = con.prepareCall(sql.toString());
		
		stmt.setString(1, sucursal);   
		
		ResultSet rs = stmt.executeQuery();

		
		while (rs.next()) {
			nro = rs.getLong(1);
		}
	} catch (Exception e) {
		logger.error("Error al obtener proximo nro Recibo Hoteles ", e);
		throw new SystemException(e);
	} finally {
		if (connection == null) {
			ConnectionHelper.cerrar(stmt, con);
		} else {
			ConnectionHelper.cerrar(stmt);
		}
	}

	return nro;		
  }

    
    public List<Recibo> getRecibos(String sucursal,Long nro,Date fechaDde,Date fechaHta,String clienteNombre,String clienteDoc,Integer estado)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Recibo> recibos = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  hoteles.buscar_recibos(?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,sucursal);
			
			if (nro == null || nro  ==0L) {
				stmt.setNull(2, Types.BIGINT );
			}else{
				stmt.setLong(2, nro);   
			}
			
			
			if(fechaDde ==null){
				  stmt.setNull(3, Types.DATE );	
			}else{
				  stmt.setDate(3, new java.sql.Date (fechaDde.getTime()));
			}
			
			
			if(fechaHta ==null){
				  stmt.setNull(4, Types.DATE );	
			}else{
				  stmt.setDate(4, new java.sql.Date (fechaHta.getTime()));
			}
			
			
			if (clienteNombre == null || "".equalsIgnoreCase(clienteNombre) ) {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, clienteNombre);   
			}
			
			if (clienteDoc == null || "".equalsIgnoreCase(clienteDoc) ) {
				stmt.setNull(6, Types.VARCHAR );
			}else{
				stmt.setString(6, clienteDoc);   
			}
			
			if (estado == null  ) {
				stmt.setNull(7, Types.INTEGER);
			}else{
				stmt.setInt(7, estado);   
			}
			
			ResultSet rs = stmt.executeQuery();
			recibos = new ArrayList<Recibo>();
			
			while (rs.next()) {
				Recibo archivo = Recibo.getMapping(rs,"");
				recibos.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar Recibos Hotel ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return recibos;		
	}
    
    
    public List<FacturaIngreso>  getReciboIngresos(String sucursal,Long idRecibo,Connection connection)
    		throws SystemException {
    	Connection con = null;
    	CallableStatement stmt = null;
    	Long nro = null;
    	ArrayList<FacturaIngreso> ingresos = new ArrayList<FacturaIngreso>();
    	try {
    		
    		if (connection == null) {
    			con = ConnectionHelper.getConnection();
    		} else {
    			con = connection;
    		}
    		
    		String sql = "{call  hoteles.buscar_recibo_ingresos(?,?)}";
    		stmt = con.prepareCall(sql.toString());
    		
    		stmt.setString(1, sucursal);
    		stmt.setBigDecimal(2, new BigDecimal(idRecibo));
    		
    		ResultSet rs = stmt.executeQuery();
    		
			while (rs.next()) {
				ingresos.add(FacturaIngreso.getMapping(rs,"fi__",1));
			}
    		
    		
    	} catch (Exception e) {
    		logger.error("Error al obtener ingresos Recibo Hoteles ", e);
    		throw new SystemException(e);
    	} finally {
    		if (connection == null) {
    			ConnectionHelper.cerrar(stmt, con);
    		} else {
    			ConnectionHelper.cerrar(stmt);
    		}
    	}

    	return ingresos;		
      }
    
    
public Long anulaRecibo(Recibo recibo, String usuario,boolean estoyEnCentral) throws SystemException{
		
	    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null;
		
		Long idRecibo=0L;
		Boolean nuevo=false;
		try {
			
			con = ConnectionHelper.getConnectionForTransaction();

			idRecibo=recibo.getNumero();
				
			String sql = "{call hoteles.anula_recibo(?,?,?,?) }";	
			stmt = con.prepareCall(sql.toString());
				
			stmt.setBigDecimal(1,new BigDecimal(idRecibo));
			stmt.setString(2,recibo.getSucursal());
			stmt.setDate(3, new java.sql.Date(recibo.getFechaBaja().getTime()));
			stmt.setString(4, usuario);
			stmt.executeUpdate();
						
// Actualiza Sistema Reservas Hotel	
			if(!estoyEnCentral) {
			  String sql2 = null;
			  sql2 = "{call hoteles.anula_recibo_reserva ( ?, ?, ?, ?, ?, ?, ? , ?,?,?)}";
			  stmt2 = con.prepareCall(sql2.toString());
			  if(recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null && recibo.getReserva().getIdReserva()>0) {
				
				String cad="Reserva " + recibo.getReserva().getIdReserva(); 
				if(recibo.getReserva()!=null && recibo.getReserva().getFechaDesde()!=null){
					cad+=" desde " + 
					         sdf.format(recibo.getReserva().getFechaDesde()) +
								" hasta " +sdf.format(recibo.getReserva().getFechaDesde());
				}
				if(recibo.getReserva()!=null && recibo.getCliente()!=null){
					cad+=" por " +recibo.getCliente().getClienteNombre();
				}
				stmt2.setInt(1,recibo.getReserva().getAnio());
				stmt2.setInt(2,recibo.getReserva().getIdReserva());
				stmt2.setLong(3,idRecibo);
				if(recibo.getFecha()!=null) {
					stmt2.setDate(4, new java.sql.Date(recibo.getFecha().getTime()));
				}else {
					stmt2.setNull(4,Types.DATE);
				}
				
				stmt2.setInt(5,recibo.getReserva().getIdCliente());
				stmt2.setInt(6,recibo.getReserva().getFechaDesdeId());
				stmt2.setInt(7,recibo.getReserva().getFechaHastaId());
				stmt2.setDouble(8,recibo.getTotal());
				if(recibo.getTotalAnterior()!=null) {
					stmt2.setDouble(9,recibo.getTotalAnterior());	
				}else {
					stmt2.setDouble(9,0D);
				}
				
				stmt2.setString(10, cad);
				stmt2.executeUpdate();
			  }
		   }

			con.commit();
			
			
		} catch (SQLException e) {
			logger.error("Error al anular recibo Hoteles uoma", e);
			
			ConnectionHelper.rollback(con);
			
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al anular recibo Hoteles uoma", e);
			
		} finally {
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt, con);
		}

		return idRecibo;
	}

/*
public Long sincronizaReciboCentral(Recibo recibo, String usuario) throws SystemException{
	
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

	Connection con = null;
	CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null;
	
	Long idRecibo=0L;
	Boolean nuevo=false;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();
		
		nuevo=true;
			
		String sql = "{call hoteles.inserta_recibo(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";	
		stmt = con.prepareCall(sql.toString());
		stmt.setBigDecimal(1,new BigDecimal(recibo.getNumero()));
		stmt.setString(2,recibo.getSucursal());
		stmt.setString(3,recibo.getDescripcion());
		if(recibo.getFecha()!=null) {
			stmt.setDate(4, new java.sql.Date(recibo.getFecha().getTime()));
		}else {
			stmt.setNull(4,Types.DATE);
		}
			
		if(StringUtils.checkNotEmpty(recibo.getCliente().getRazonSocial())) {
			stmt.setString(5, recibo.getCliente().getRazonSocial());
		}else {
			stmt.setNull(5, Types.VARCHAR);
		}
		
		if(StringUtils.checkNotEmpty(recibo.getCliente().getCuit())) {
			stmt.setString(6, recibo.getCliente().getCuit());
		}else {
			stmt.setNull(6, Types.VARCHAR);
		}
			
		String tipoComprobante="";
		String sucursal="";
		String letra="";
		String numero="";
		Integer clienteId=0;
		Integer anio=0;
			
		if(recibo.getReserva()!=null && recibo.getReserva().getIdReserva()!=null && recibo.getReserva().getIdReserva()>0) {
			tipoComprobante="RES";
			sucursal=recibo.getSucursal();
			numero=recibo.getReserva().getIdReserva().toString();
			clienteId=recibo.getReserva().getIdCliente();
			anio=recibo.getReserva().getAnio();
		}else {
			if(recibo.getFactura()!=null && recibo.getFactura().getNumero() !=null) {
				tipoComprobante=recibo.getFactura().getTipo();
				sucursal=recibo.getFactura().getSucursal();
				numero=recibo.getFactura().getNumero();
				anio=0;
			}	
		}
			
		stmt.setString(7, tipoComprobante);
		stmt.setString(8, sucursal);
		stmt.setString(9, letra);
		stmt.setString(10, numero);
		stmt.setInt(11, clienteId);
		stmt.setInt(12, anio);
		stmt.setDouble(13, recibo.getTotal());
		stmt.setString(14, usuario);
		
		stmt.executeUpdate();
			
		String sql1 = null;
		sql1 = "{call hoteles.delete_recibo_ingreso (?, ?)}";
		stmt1 = con.prepareCall(sql1.toString());
		stmt1.setString(1,recibo.getSucursal());
		stmt1.setLong(2,idRecibo);
		stmt1.executeUpdate();
		
		
		String sql3 = null;
		sql3 = "{call hoteles.inserta_recibo_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)}";
		stmt3 = con.prepareCall(sql3.toString());
		
		for (Iterator<FacturaIngreso> iterator = recibo.getIngresos().iterator(); iterator.hasNext();) {
			FacturaIngreso fi =  iterator.next();
			Ingreso i =  fi.getIngreso();
			stmt3.setLong(1, idRecibo);
		
			if (i.getTipo().equals("Cheque")) {
				stmt3.setBigDecimal(2, new BigDecimal( i.getNumeroStr()) ) ;
			} else {
				stmt3.setBigDecimal(2, null);
			}

			if (i.getTipo().equals("Cheque")) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta D�bito") ) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
				stmt3.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt3.setNull(3, java.sql.Types.INTEGER);
			}

			if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") || 
					i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ||
					i.getTipo().equalsIgnoreCase("Tarjeta D�bito")) {
				stmt3.setString(4, i.getNumeroStr());
			} else {
				stmt3.setNull(4, Types.VARCHAR);
			}

			BigDecimal importe = null;
			stmt3.setBigDecimal(5, i.getImporte());
			
			Date fecha = null;
			stmt3.setDate(6, new java.sql.Date(i.getFecha().getTime()));
			
			if (i.getTipo().equals("Cheque")) {
				stmt3.setInt(7, Cheque.Estado.RECIBIDO);
			}else {
				stmt3.setNull(7, java.sql.Types.INTEGER);
			}
			stmt3.setString(8, usuario);

			if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
				stmt3.setInt(9, i.getCuentaBancaria().getId_cuenta_bcria());
				stmt3.setInt(10, DepositoBancario.ID_TIPO_TRANSFERENCIA);
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") ) {
				stmt3.setNull(9, Types.INTEGER);
			 	stmt3.setInt(10, TarjetaDebitoCredito.ID_TIPO_CREDITO);
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta D�bito") ) {
				stmt3.setNull(9, Types.INTEGER);
				stmt3.setInt(10,  TarjetaDebitoCredito.ID_TIPO_DEBITO );		
			} else {
				stmt3.setNull(9, Types.INTEGER);
				stmt3.setNull(10, Types.INTEGER);
			}

			stmt3.setNull(11, Types.INTEGER);
			stmt3.setNull(12, Types.INTEGER);
			if(i.getTipo().equals("Cheque")){
				stmt3.setInt(13, i.getCuentaBancaria().getId_cuenta_bcria());
			}else{
				stmt3.setNull(13, Types.INTEGER);
			}
			
			if (i.getTipo().equalsIgnoreCase("Tarjeta Cr�dito") || i.getTipo().equalsIgnoreCase("Tarjeta D�bito")) {
				stmt3.setInt(14, i.getEmisor());
			 	stmt3.setInt(15, i.getCuotas());
			} else {
				stmt3.setNull(14, Types.INTEGER);
				stmt3.setNull(15, Types.INTEGER);
			}
			
			stmt3.setString(16, recibo.getSucursal());
			
			stmt3.executeUpdate();

		}
		
		con.commit();
		
		
	} catch (SQLException e) {
		logger.error("Error al insertar recibo Hoteles uoma WS", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SystemException(e);
	} catch (Exception e) {
		logger.error("Error al insertar recibo Hoteles uoma WS", e);
		
	} finally {
		ConnectionHelper.cerrar(stmt3);
		ConnectionHelper.cerrar(stmt2);
		ConnectionHelper.cerrar(stmt1);
		ConnectionHelper.cerrar(stmt, con);
	}

	return idRecibo;
}
*/



public List<Recibo> getRecibosPendientesSincronizar()
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<Recibo> recibos = null;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.buscar_recibos_pendientes_sincronizar()}";
		
		stmt = con.prepareCall(sql.toString());
		
		
		ResultSet rs = stmt.executeQuery();
		recibos = new ArrayList<Recibo>();
		
		while (rs.next()) {
			Recibo archivo = Recibo.getMapping(rs,"");
			recibos.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error al buscar Recibos Pendientes Sincronizar Hotel ", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return recibos;		
}





public Long registraProcesoTransferenciaCentralRecibo(Recibo recibo) throws SystemException{
	
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

	Connection con = null;
	CallableStatement stmt = null;
	
	Long idRecibo=0L;
	Boolean nuevo=false;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();

		idRecibo=recibo.getNumero();
			
		String sql = "{call hoteles.registra_proceso_recibo(?,?,?) }";	
		stmt = con.prepareCall(sql.toString());
			
		stmt.setBigDecimal(1,new BigDecimal(idRecibo));
		stmt.setString(2,recibo.getSucursal());
		stmt.setDate(3, new java.sql.Date(recibo.getFechaProceso().getTime() ));
		
		stmt.executeUpdate();
					
		

		con.commit();
		
		
	} catch (SQLException e) {
		logger.error("Error al registrar transferencia recibo Hoteles uoma", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SystemException(e);
	} catch (Exception e) {
		logger.error("Error al registrar transferencia recibo Hoteles uom", e);
		
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return idRecibo;
}


public Long aprobarRecibos(List<Recibo> recibos) throws SystemException{
	
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

	Connection con = null;
	CallableStatement stmt = null;
	
	Long idRecibo=0L;
	Boolean nuevo=false;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();
		
		for(Recibo recibo:recibos){

		    idRecibo=recibo.getNumero();
			
		    String sql = "{call hoteles.aprobar_recibo(?,?,?) }";	
		    stmt = con.prepareCall(sql.toString());
			
		    stmt.setBigDecimal(2,new BigDecimal(idRecibo));
		    stmt.setString(1,recibo.getSucursal());
		    if(recibo.getFechaProceso()!=null) {
		      stmt.setDate(3, new java.sql.Date(recibo.getFechaProceso().getTime() ));
		    }else {
		      stmt.setNull(3, Types.DATE);	
		    }
		
		    stmt.executeUpdate();
		}			
		

		con.commit();
		
		
	} catch (SQLException e) {
		logger.error("Error al aprobar recibos Hoteles uoma", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SystemException(e);
	} catch (Exception e) {
		logger.error("Error al aprobar recibos Hoteles uom", e);
		
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return idRecibo;
}

public List<Recibo> getRecibosByReserva(String sucursal,Integer nro,Integer anio)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<Recibo> recibos = null;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.buscar_recibos_by_reserva(?,?,?)}";
		
		stmt = con.prepareCall(sql.toString());
		
		stmt.setString(1,sucursal);
		
		if (nro == null || nro  ==0L) {
			stmt.setNull(2, Types.BIGINT );
		}else{
			stmt.setLong(2, nro);   
		}
		if (anio == null  ) {
			stmt.setNull(3, Types.INTEGER);
		}else{
			stmt.setInt(3, anio);   
		}
		
		ResultSet rs = stmt.executeQuery();
		recibos = new ArrayList<Recibo>();
		
		while (rs.next()) {
			Recibo archivo = Recibo.getMapping(rs,"");
			recibos.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error al buscar Recibos Hotel por reserva ", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return recibos;		
}


public Long updatePrestamo(Prestamo prestamo,String usr)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	CallableStatement stmt1 = null;
	CallableStatement stmt2 = null;
    Formatter fmt =new Formatter();
	Long ret=0L;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.update_prestamo(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		
		stmt.setLong(1,prestamo.getId());
		stmt.setString(2,prestamo.getAfiliado().getCuil_titular());
		stmt.setInt(3,prestamo.getAfiliado().getInte());
		
		if(prestamo.getHotel()==null) {
			stmt.setNull(4, Types.VARCHAR );
		}else{
			stmt.setString(4, prestamo.getHotel());   
		}
		
		
		if(prestamo.getEstadiaDesde() !=null) {
		      stmt.setDate(5, new java.sql.Date(prestamo.getEstadiaDesde().getTime() ));
		}else {
		      stmt.setNull(5, Types.DATE);	
		}
		
		if(prestamo.getEstadiaHasta() !=null) {
		      stmt.setDate(6, new java.sql.Date(prestamo.getEstadiaHasta().getTime() ));
		}else {
		      stmt.setNull(6, Types.DATE);	
		}
		
		if(prestamo.getObservaciones()==null) {
			stmt.setNull(7, Types.VARCHAR );
		}else{
			stmt.setString(7, prestamo.getObservaciones());   
		}
		
		if(prestamo.getFactura()==null || prestamo.getFactura().getTipo()==null) {
			stmt.setNull(8, Types.VARCHAR );
		}else{
			stmt.setString(8, prestamo.getFactura().getTipo());   
		}
		
		if(prestamo.getFactura()==null || prestamo.getFactura().getLetra()==null) {
			stmt.setNull(9, Types.VARCHAR );
		}else{
			stmt.setString(9, prestamo.getFactura().getLetra());
		}
		
		if(prestamo.getFactura()==null || prestamo.getFactura().getSucursal()==null) {
			stmt.setNull(10, Types.VARCHAR );
		}else{
			stmt.setString(10, prestamo.getFactura().getSucursal());   
		}
		
		if(prestamo.getFactura()==null || prestamo.getFactura().getNumero()==null || "".equalsIgnoreCase(prestamo.getFactura().getNumero()) ) {
			stmt.setNull(11, Types.VARCHAR );
		}else{
			String nroF=fmt.format("%08d", Integer.parseInt(prestamo.getFactura().getNumero())).toString();
			stmt.setString(11, nroF );   
		}
		
		if(prestamo.getFactura()==null || prestamo.getFactura().getTotalExento()==null) {
			stmt.setNull(12, Types.DOUBLE );
		}else{
			stmt.setDouble(12, prestamo.getFactura().getTotalExento().doubleValue());   
		}
		if(usr == null || usr  =="") {
			stmt.setNull(13, Types.VARCHAR );
		}else{
			stmt.setString(13, usr);   
		}
		
		if(prestamo.getAcuerdoFecha() !=null) {
		      stmt.setDate(14, new java.sql.Date(prestamo.getAcuerdoFecha().getTime() ));
		}else {
		      stmt.setNull(14, Types.DATE);	
		}
		
		if(prestamo.getMonto()==null ) {
			stmt.setNull(15, Types.DOUBLE );
		}else{
			stmt.setDouble(15, prestamo.getMonto());   
		}
		
		
		if(prestamo.getInteresPorcentaje()==null ) {
			stmt.setNull(16, Types.DOUBLE );
		}else{
			stmt.setDouble(16, prestamo.getInteresPorcentaje());   
		}
		
		if(prestamo.getInteresImporte()==null ) {
			stmt.setNull(17, Types.DOUBLE );
		}else{
			stmt.setDouble(17, prestamo.getInteresImporte());   
		}
		
		if(prestamo.getTotal()==null ) {
			stmt.setNull(18, Types.DOUBLE );
		}else{
			stmt.setDouble(18, prestamo.getTotal());   
		}
		
		if(prestamo.getCantidadCuotas()==null ) {
			stmt.setNull(19, Types.INTEGER );
		}else{
			stmt.setInt(19, prestamo.getCantidadCuotas());   
		}
		
		if(prestamo.getPrimeraCuota() !=null) {
		      stmt.setDate(20, new java.sql.Date(prestamo.getPrimeraCuota().getTime() ));
		}else {
		      stmt.setNull(20, Types.DATE);	
		}
		
		if(prestamo.getMovilidad()==null ) {
			stmt.setNull(21, Types.DOUBLE );
		}else{
			stmt.setDouble(21, prestamo.getMovilidad());   
		}
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ret = rs.getLong(1);
		}
		
		if(!prestamo.getCuotas().isEmpty()) {
			String sql1 = "{call  hoteles.delete_prestamo_cuotas(?)}";
			stmt1 = con.prepareCall(sql1.toString());
			stmt1.setLong(1,prestamo.getId());
			stmt1.executeQuery();
		}
		
		String sql2 = "{call  hoteles.update_prestamo_cuotas(?,?,?,?)}";
		for(PrestamoCuota c:prestamo.getCuotas()) {
			stmt2 = con.prepareCall(sql2.toString());
			stmt2.setLong(1,prestamo.getId());
			stmt2.setInt(2,c.getNumero());
			stmt2.setDouble(3, c.getImporte());
			stmt2.setDate(4, new java.sql.Date(c.getVencimiento().getTime() ));
			stmt2.executeQuery();	
		}
		
	} catch (Exception e) {
		logger.error("Error al actualizar prestamos hoteles", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

    return ret;
}

public List<Prestamo> getListaPrestamos(Prestamo filtro)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<Prestamo> prestamos = null;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.prestamos_busqueda(?,?,?,?,?,?,?,?,?,?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		if (filtro.getId() == null || filtro.getId()  ==0) {
			stmt.setNull(1, Types.BIGINT );
		}else{
			stmt.setLong(1, filtro.getId()); 
		}
		
		if(filtro.getAfiliado()==null ||  filtro.getAfiliado().getCuil_titular() == null || filtro.getAfiliado().getCuil_titular()  =="") {
			stmt.setNull(2, Types.VARCHAR );
		}else{
			stmt.setString(2, filtro.getAfiliado().getCuil_titular());   
		}
		
		if (filtro.getAfiliado()==null ||  filtro.getAfiliado().getInte() == 0) {
			stmt.setNull(3, Types.INTEGER );
		}else{
			stmt.setInt(3, filtro.getAfiliado().getInte()); 
		}
		
		
		if(filtro.getHotel() == null || filtro.getHotel()  =="") {
			stmt.setNull(4, Types.VARCHAR );
		}else{
			stmt.setString(4, filtro.getHotel());   
		}
		
		
		if(filtro.getFechaConvenioDesde() ==null){
			  stmt.setNull(5, Types.DATE );	
		}else{
			  stmt.setDate(5, new java.sql.Date (filtro.getFechaConvenioDesde().getTime()));
		}
		
		if(filtro.getFechaConvenioHasta() ==null){
			  stmt.setNull(6, Types.DATE );	
		}else{
			  stmt.setDate(6, new java.sql.Date (filtro.getFechaConvenioHasta().getTime()));
		}
		
		
		if(filtro.getFechaCuotaDesde() ==null){
			  stmt.setNull(7, Types.DATE );	
		}else{
			  stmt.setDate(7, new java.sql.Date (filtro.getFechaCuotaDesde().getTime()));
		}
		
		if(filtro.getFechaCuotaHasta() ==null){
			  stmt.setNull(8, Types.DATE );	
		}else{
			  stmt.setDate(8, new java.sql.Date (filtro.getFechaCuotaHasta().getTime()));
		}
		
		if(filtro.getAfiliado()==null ||  filtro.getAfiliado().getSeccional() == null || filtro.getAfiliado().getSeccional().getId()  ==0) {
			stmt.setNull(9, Types.INTEGER);
		}else{
			stmt.setInt(9, filtro.getAfiliado().getSeccional().getId());   
		}	
		
		if(filtro.getDeudaExigibleAl()==null){
			  stmt.setNull(10, Types.DATE );	
		}else{
			  stmt.setDate(10, new java.sql.Date (filtro.getDeudaExigibleAl().getTime()));
		}
		
		if(filtro.getCorteCuentaCorriente()==null){
			  stmt.setNull(11, Types.DATE );	
			  //stmt.setDate(11, new java.sql.Date ((new Date()).getTime()));
		}else{
			  stmt.setDate(11, new java.sql.Date (filtro.getCorteCuentaCorriente().getTime()));
		}
		
		ResultSet rs = stmt.executeQuery();
		prestamos = new ArrayList<Prestamo>();
		
		while (rs.next()) {
			Prestamo archivo = Prestamo.getMapping(rs,"");
			prestamos.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error al buscar Prestamos Turismo", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return prestamos;		
}


public Long updatePrestamoImagen(Prestamo prestamo,String tipo,String usr)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	
	Long ret=0L;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.update_prestamo_imagen(?,?,?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		
		stmt.setLong(1,prestamo.getId());
		if(prestamo==null || tipo==null) {
			stmt.setNull(2, Types.VARCHAR );
		}else {
			stmt.setString(2,tipo);
		}
		
		if("FCP".equalsIgnoreCase(tipo)) {
		
		   if(prestamo.getFactura()==null || prestamo.getImgFactura()==null) {
			stmt.setNull(3, Types.VARCHAR );
		   }else {
			stmt.setString(3,prestamo.getImgFactura());
	 	   }
		} else {
			if(prestamo.getFactura()==null || prestamo.getImgConvenio()==null) {
				stmt.setNull(3, Types.VARCHAR );
			}else {
				stmt.setString(3,prestamo.getImgConvenio());
		 	}
		}
		
		if(usr == null || usr  =="") {
			stmt.setNull(4, Types.VARCHAR );
		}else{
			stmt.setString(4, usr);   
		}
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ret = rs.getLong(1);
		}
		
		
	} catch (Exception e) {
		logger.error("Error al actualizar prestamos imagen hoteles", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

    return ret;
}


public Long deletePrestamoImagen(Prestamo prestamo,String tipo,String usr)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	
	Long ret=0L;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.delete_prestamo_imagen(?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		
		stmt.setLong(1,prestamo.getId());
		stmt.setString(2,tipo);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ret = rs.getLong(1);
		}
		
		
	} catch (Exception e) {
		logger.error("Error al eliminar prestamos imagen prestamos hoteles", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

    return ret;
}

public List<PrestamoCuota> getPrestamoCuotas(Prestamo filtro)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<PrestamoCuota> cuotas = null;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.prestamos_cuotas_by_prestamo(?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		if (filtro.getId() == null || filtro.getId()  ==0) {
			stmt.setNull(1, Types.BIGINT );
		}else{
			stmt.setLong(1, filtro.getId()); 
		}
		
		if(filtro.getCuotaNro()==null) {
			stmt.setNull(2, Types.INTEGER);
		}else{
			stmt.setInt(2, filtro.getCuotaNro());   
		}
		
		ResultSet rs = stmt.executeQuery();
		cuotas = new ArrayList<PrestamoCuota>();
		
		while (rs.next()) {
			PrestamoCuota archivo = PrestamoCuota.getMapping(rs,"");
			cuotas.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error al buscar Cuotas Prestamos Turismo", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return cuotas;		
}


public Integer deletePrestamo(Prestamo prestamo,String usr)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	
	Integer ret=0;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.delete_prestamo(?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		
		stmt.setLong(1,prestamo.getId());
		stmt.setString(2,usr);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ret = rs.getInt(1);
		}
		
		
	} catch (Exception e) {
		logger.error("Error al eliminar prestamos hoteles", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

    return ret;
}


public List<ar.com.ospim.tesoreria.beans.Recibo> getPrestamoPagos(Long idPrestamo,Integer entidad,Date fechaCCHasta)
		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<ar.com.ospim.tesoreria.beans.Recibo> recibos = null;
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call  hoteles.prestamos_recibo_conceptos_amtima(?,?)}";
		stmt = con.prepareCall(sql.toString());
		
		stmt.setLong(1, idPrestamo); 
		
		
		if(fechaCCHasta==null){
			  stmt.setNull(2, Types.DATE );	
		}else{
			  stmt.setDate(2, new java.sql.Date (fechaCCHasta.getTime()));
		}
		
		
		ResultSet rs = stmt.executeQuery();
		recibos = new ArrayList<ar.com.ospim.tesoreria.beans.Recibo>();
		
		while (rs.next()) {
			ar.com.ospim.tesoreria.beans.Recibo archivo = new ar.com.ospim.tesoreria.beans.Recibo();
			archivo.setReciboPrestamos(new ArrayList<ReciboPrestamo>());
			Prestamo prestamo = new Prestamo();
			prestamo.setId(rs.getLong("prestamo_id"));
			prestamo.setAcuerdoFecha(rs.getDate("prestamo_fecha"));
			prestamo.setMonto(rs.getDouble("prestamo_importe"));
			ReciboPrestamo rp = new ReciboPrestamo();
			rp.setPrestamo(prestamo);
			archivo.setNumero(rs.getString("numero"));
			archivo.getReciboPrestamos().add(rp);
			recibos.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error al buscar Pagos Prestamos Turismo", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return recibos;		
}


public Long updateReciboRetencion(Recibo recibo, String usuario) throws Exception{
	
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

	Connection con = null;
	CallableStatement stmt = null;
	
	Long idRecibo=0L;
	Boolean nuevo=false;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();
		
		String sql3 = null;
		sql3 = "{call hoteles.update_recibo_retencion (?, ?, ?, ?,?)}";
		stmt = con.prepareCall(sql3.toString());
		
		for (Iterator<FacturaIngreso> iterator = recibo.getIngresos().iterator(); iterator.hasNext();) {
			FacturaIngreso fi =  iterator.next();
			Ingreso i =  fi.getIngreso();
			stmt.setLong(1,recibo.getNumero());
			stmt.setString(2, recibo.getSucursal());
			stmt.setDate(3, new java.sql.Date(i.getFecha().getTime()));
			stmt.setBigDecimal(4, i.getImporte());
			
			if (i.getTipo().equalsIgnoreCase("Retenci�n No Identificada") ) {
			 	stmt.setInt(5, Retencion.GRAL);
			}else if (i.getTipo().equalsIgnoreCase("Retenci�n IVA") ) {
		 	    stmt.setInt(5, Retencion.IVA);
			}else if (i.getTipo().equalsIgnoreCase("Retenci�n Ingresos Brutos") ) {
			 	stmt.setInt(5, Retencion.IIBB);
			}else if (i.getTipo().equalsIgnoreCase("Retenci�n Seguridad Social") ) {
			 	stmt.setInt(5, Retencion.SUSS);
			}
			
			stmt.executeUpdate();

		}
		
		con.commit();
		
		
	} catch (SQLException e) {
		logger.error("Error al actualizar retenciones recibos Hoteles uoma", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SystemException(e);
	} catch (Exception e) {
		logger.error("Error al actualizar retenciones recibos Hoteles uoma", e);
		throw new Exception(e);
		
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return idRecibo;
}




}
