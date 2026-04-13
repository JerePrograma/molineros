package ar.com.uoma.facturacion.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.CuentaCorriente;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.FinanciacionTurismo;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.beans.Retencion;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.facturacion.BusquedaFacturasFiltro;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.LoginCmsResponse;
import ar.com.uoma.facturacion.Producto;

public class FacturacionServiceImpl {


	private static Log _log = LogFactoryUtil
			.getLog(FacturacionServiceImpl.class);

	public List<Producto> getProductos() throws SystemException {
		Connection con = null;
		List<Producto> prods = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.buscar_productos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			prods = new ArrayList<Producto>();
			while (rs.next()) {
				Producto prod = Producto.getMapping("",rs);
				prods.add(prod);
			}
		} catch (Exception e) {
			_log.debug("error al traer productos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return prods;
	}
	
	public List<Cliente> getClientes() throws SystemException {
		Connection con = null;
		List<Cliente> clientes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.buscar_clientes()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			clientes = new ArrayList<Cliente>();
			while (rs.next()) {
				Cliente cli = Cliente.getMapping("",rs);
				clientes.add(cli);
			}
		} catch (Exception e) {
			_log.debug("error al traer clientes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return clientes;
	}
	
	public List<Cliente> getClientes(String docuNro, String apellido) throws SystemException {
		Connection con = null;
		List<Cliente> clientes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.buscar_clientes(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(StringUtils.checkNotEmpty(docuNro)) {
				stmt.setString(1, docuNro);
			}else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(apellido)) {
				stmt.setString(2, apellido);
			}else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			clientes = new ArrayList<Cliente>();
			while (rs.next()) {
				Cliente cli = Cliente.getMapping("",rs);
				clientes.add(cli);
			}
		} catch (Exception e) {
			_log.debug("error al traer clientes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return clientes;
	}
	
	
	public List<Cliente> getClientesPorAnio(String docuNro, String apellido, String cuit) throws SystemException {
		Connection con = null;
		List<Cliente> clientes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.buscar_clientes_por_anno_calendario(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(StringUtils.checkNotEmpty(docuNro)) {
				stmt.setString(1, docuNro);
			}else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(apellido)) {
				stmt.setString(2, apellido);
			}else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(cuit)) {
				stmt.setString(3, cuit);
			}else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			clientes = new ArrayList<Cliente>();
			while (rs.next()) {
				Cliente cli = Cliente.getMapping2("",rs);
				clientes.add(cli);
			}
		} catch (Exception e) {
			_log.debug("error al traer clientes por anio", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return clientes;
	}
	
	
	public int saveFactura(Factura factura, String usuario) throws SystemException, SQLException{
		
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null, stmt4 = null;
		
		int idClienteNuevo = -1, idFacturaNueva = 0;
//		String numeroFacturaSucursalLetra = null;
		try {
			String sql = "{? = call uoma.inserta_cliente(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

//			***** CLIENTE ***** 
			con = ConnectionHelper.getConnectionForTransaction();
			stmt = con.prepareCall(sql.toString());
			
			Cliente cli = factura.getCliente();
			
			stmt.registerOutParameter(1, Types.INTEGER);
			
			if(StringUtils.checkNotEmpty(cli.getApellido())) {
				stmt.setString(2, cli.getApellido());
				stmt.setString(3, cli.getNombre());
			}else {
				stmt.setNull(2, Types.VARCHAR);
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if(StringUtils.checkNotEmpty(cli.getDocumentoNro())) {
				stmt.setString(4, cli.getCuil());
				stmt.setString(5, cli.getDocumentoTipo());
				stmt.setString(6, cli.getDocumentoNro());
			}else {
				stmt.setNull(4, Types.VARCHAR);
				stmt.setNull(5, Types.VARCHAR);
				stmt.setNull(6, Types.VARCHAR);
			}
			
			stmt.setString(7, cli.getObservaciones());
			stmt.setString(8, cli.getTipo().name());
			stmt.setString(9, cli.getCategoriaIVA());
			
			if(StringUtils.checkNotEmpty(cli.getCuilTitular())) {
				stmt.setString(10, cli.getCuilTitular());
				stmt.setInt(11, cli.getInte());
			}else {
				stmt.setNull(10, Types.VARCHAR);
				stmt.setNull(11, Types.INTEGER);
			}
			
			if(StringUtils.checkNotEmpty(cli.getCuit())) {
				stmt.setString(12, cli.getCuit());
				stmt.setString(13, cli.getSucursal());
				stmt.setString(14, cli.getRazonSocial());
			}else {
				stmt.setNull(12, Types.VARCHAR);
				stmt.setNull(13, Types.VARCHAR);
				stmt.setNull(14, Types.VARCHAR);
			}
			
			stmt.setString(15, usuario);
			
			stmt.executeUpdate();
			
			idClienteNuevo =  stmt.getInt(1);
			
			
//			***** FACTURA CABECERA *****
				
			String sql1 = "{? = call uoma.inserta_factura(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			stmt1 = con.prepareCall(sql1.toString());
	
			stmt1.registerOutParameter(1, Types.INTEGER);
			
			stmt1.setString(2, factura.getTipo());
			stmt1.setInt(3, Integer.parseInt(factura.getNumero()));
			stmt1.setString(4, factura.getLetra());
			stmt1.setString(5, factura.getSucursal());
			stmt1.setDate(6, new java.sql.Date(factura.getFecha().getTime()));
			if(factura.getFechaCae()!=null) {
				stmt1.setDate(7, new java.sql.Date(factura.getFechaCae().getTime()));
				stmt1.setString(8, factura.getCae());
			}else {
				stmt1.setNull(7,Types.DATE);
				stmt1.setNull(8, Types.VARCHAR);
			}
			stmt1.setInt(9, idClienteNuevo);
			stmt1.setBigDecimal(10, factura.getTotalExento());
			stmt1.setBigDecimal(11, factura.getImporteNeto());
			stmt1.setBigDecimal(12, factura.getIva());
			stmt1.setBigDecimal(13, factura.getImporteTotal());
			if (StringUtils.checkNotEmpty(factura.getObservaciones())){
				stmt1.setString(14, factura.getObservaciones());
			}else{
				stmt1.setNull(14, Types.VARCHAR);
			}
			stmt1.setString(15, usuario);
			stmt1.setBoolean(16, factura.isPresentaForm8001());
			stmt1.setBigDecimal(17, factura.getPercepcion());
			stmt1.setBigDecimal(18, factura.getIvaReintegro());
			
			stmt1.executeUpdate();
			
			idFacturaNueva =  stmt1.getInt(1);
			
//			***** FACTURA DETALLE *****
			
			String sql2 = "{call uoma.inserta_factura_detalle(?,?,?,?) }";
			
			stmt2 = con.prepareCall(sql2.toString());
			
			for (Iterator<FacturaDetalle> iterator = factura.getDetalles().iterator(); iterator.hasNext();) {
				FacturaDetalle fd =  iterator.next();
				
				stmt2.setInt(1, idFacturaNueva);
				stmt2.setInt(2, fd.getDetalle().getId());
				stmt2.setBigDecimal(3, fd.getPrecio());
				stmt2.setString(4, usuario);
				
				stmt2.executeUpdate();
			}
			
			String sql3 = null;
//			if (entidad == WebKeysGlobal.AMTIMA) {
//				sql3 = "{call inserta_recibo_ingreso_amtima (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
//			} else if (entidad == WebKeysGlobal.OSPIM) {
//				sql3 = "{call inserta_recibo_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
//			} else if (entidad == WebKeysGlobal.UOMA) {
				sql3 = "{call uoma.inserta_factura_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
//			}

			stmt3 = con.prepareCall(sql3.toString());
			
			for (Iterator<FacturaIngreso> iterator = factura.getIngresos().iterator(); iterator.hasNext();) {
				FacturaIngreso fi =  iterator.next();
				Ingreso i =  fi.getIngreso();
				stmt3.setInt(1, idFacturaNueva);
			
				if (i.getTipo().equals("Cheque")) {
					stmt3.setBigDecimal(2, new BigDecimal( i.getNumeroStr()) ) ;
				} else {
					stmt3.setBigDecimal(2, null);
				}
	
				if (i.getTipo().equals("Cheque")) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
					stmt3.setInt(3, i.getBanco().getId_banco());
				} else if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
					stmt3.setNull(3, java.sql.Types.INTEGER);
				} else {
					stmt3.setNull(3, java.sql.Types.INTEGER);
				}
	
				if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") || 
						i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ||
						i.getTipo().equalsIgnoreCase("Tarjeta Débito")) {
					stmt3.setString(4, i.getNumeroStr());
				} else {
					stmt3.setNull(4, Types.VARCHAR);
				}
	
				BigDecimal importe = null;
//				if (cheque != null) {
//					importe = cheque.getImporte();
//				} else if (depo != null) {
//					importe = depo.getImporte();
//				} else if (ef != null) {
//					importe = ef.getImporte();
//				} else {
//					importe = rAnticipo.getImporte();
//				}
//				stmt3.setBigDecimal(5, importe);
				stmt3.setBigDecimal(5, i.getImporte());
				
				Date fecha = null;
//				if (cheque != null) {
//					fecha = cheque.getFecha();
//				} else if (depo != null) {
//					fecha = depo.getFecha();
//				} else if (ef != null) {
//					fecha = ef.getFecha();
//				} else {
//					fecha = rAnticipo.getFecha();
//				}
//	
//				stmt3.setDate(6, new java.sql.Date(fecha.getTime()));
				stmt3.setDate(6, new java.sql.Date(i.getFecha().getTime()));
				
				if (i.getTipo().equals("Cheque")) {
					stmt3.setInt(7, Cheque.Estado.RECIBIDO);
				}/* else if (rAnticipo != null) {
					stmt3.setInt(7, rAnticipo.getEstado().getId());
				}*/ else {
					stmt3.setNull(7, java.sql.Types.INTEGER);
				}
				stmt3.setString(8, usuario);
	
				if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
					stmt3.setInt(9, i.getCuentaBancaria().getId_cuenta_bcria());
//					stmt3.setInt(10, depo.getTipoDeposito());
					stmt3.setInt(10, DepositoBancario.ID_TIPO_TRANSFERENCIA);
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, TarjetaDebitoCredito.ID_TIPO_CREDITO);
				} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setInt(10,  TarjetaDebitoCredito.ID_TIPO_DEBITO );		
				} else if (i.getTipo().equalsIgnoreCase("FinanciacionTurismo") ) {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setInt(10, FinanciacionTurismo.TURISMO);		
				} else if (i.getTipo().equalsIgnoreCase("CuentaCorriente") ) {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setInt(10, CuentaCorriente.CTACTE);		
				}else if (i.getTipo().equalsIgnoreCase("Retención No Identificada") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.GRAL);
				}else if (i.getTipo().equalsIgnoreCase("Retención IVA") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.IVA);
				} else if (i.getTipo().equalsIgnoreCase("Retención Ingresos Brutos") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.IIBB);
				}else if (i.getTipo().equalsIgnoreCase("Retención Seguridad Social") ) {
					stmt3.setNull(9, Types.INTEGER);
				 	stmt3.setInt(10, Retencion.SUSS);
				}else {
					stmt3.setNull(9, Types.INTEGER);
					stmt3.setNull(10, Types.INTEGER);
				}
	
//				if (rAnticipo != null) {
//					stmt3.setInt(11, rAnticipo.getAnticipo().getId());
//				} else {
					stmt3.setNull(11, Types.INTEGER);
//				}
	
//				if (entidad == WebKeysGlobal.OSPIM || entidad == WebKeysGlobal.AMTIMA) {
//					if(cheque!= null && cheque.getCuentaBancaria()!=null){
//						stmt3.setInt(12, cheque.getCuentaBancaria().getId_cuenta_bcria());
//					}else{
						stmt3.setNull(12, Types.INTEGER);
//					}
//				}else if (entidad == WebKeysGlobal.UOMA && depo != null) {
//					stmt3.setInt(12, depo.getSucuNacion());
//				}else{
//					stmt3.setNull(12, Types.INTEGER);
//				}
//						stmt3.setNull(12, Types.INTEGER);
//				if (entidad == WebKeysGlobal.UOMA) {
//					if(cheque!= null && cheque.getCuentaBancaria()!=null){
//						stmt3.setInt(13, cheque.getCuentaBancaria().getId_cuenta_bcria());
//					}else{
//						stmt3.setNull(13, Types.INTEGER);
//					}
//				} 
				if(i.getTipo().equals("Cheque")){
					stmt3.setInt(13, i.getCuentaBancaria().getId_cuenta_bcria());
				}else{
					stmt3.setNull(13, Types.INTEGER);
				}
				
				if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") || i.getTipo().equalsIgnoreCase("Tarjeta Débito")) {
					stmt3.setInt(14, i.getEmisor());
				 	stmt3.setInt(15, i.getCuotas());
				} else {
					stmt3.setNull(14, Types.INTEGER);
					stmt3.setNull(15, Types.INTEGER);
				}
				stmt3.executeUpdate();
				
				
				String sql4 = "{call uoma.inserta_factura_recibo(?,?,?,?,?) }";
				
				stmt4 = con.prepareCall(sql4.toString());
				if(factura.getRecibosAdelantos()!=null) {
				  for (Recibo r: factura.getRecibosAdelantos()) {
					stmt4.setInt(1, idFacturaNueva);
					stmt4.setString(2, r.getSucursal());
					stmt4.setBigDecimal(3, BigDecimal.valueOf(r.getNumero()));
					stmt4.setBigDecimal(4,BigDecimal.valueOf(r.getTotal()));
					stmt4.setString(5, usuario);
					stmt4.executeUpdate();
				  }
				}
			}
			

			con.commit();
			
			
		} catch (SQLException e) {
			_log.error("Error al insertar factura uoma-amtima", e);
			
			ConnectionHelper.rollback(con);
			
			throw new SQLException(e);
		} catch (Exception e) {
			_log.error("Error global al insertar factura uoma-amtima", e);
			throw new SystemException(e);
			
		} finally {
			ConnectionHelper.cerrar(stmt4);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt, con);
		}

//		return numeroFacturaSucursalLetra;
		return idFacturaNueva;
	}
	
	public Factura getFactura(int idFactura) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null;
		Factura fc = null;
		try {
			
			String sql = "{call uoma.buscar_factura(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idFactura);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				fc = Factura.getMapping("",rs);
			}

			String sql1 = "{call uoma.buscar_factura_detalle(?)}";
			stmt1 = con.prepareCall(sql1.toString());
			
			stmt1.setInt(1, idFactura);
			
			ResultSet rs1 = stmt1.executeQuery();

			ArrayList<FacturaDetalle> detalles = new ArrayList<FacturaDetalle>();
			while (rs1.next()) {
				detalles.add(FacturaDetalle.getMapping("",rs1));
			}
			fc.setDetalle(detalles);
			
			
			String sql2 = "{call uoma.buscar_factura_ingresos(?)}";
			stmt2 = con.prepareCall(sql2.toString());
			
			stmt2.setInt(1, idFactura);
			
			ResultSet rs2 = stmt2.executeQuery();

			ArrayList<FacturaIngreso> ingresos = new ArrayList<FacturaIngreso>();
			while (rs2.next()) {
				ingresos.add(FacturaIngreso.getMapping(rs2,"fi__",1));
			}
			fc.setIngresos(ingresos);
			
			
			String sql3 = "{call uoma.buscar_factura_recibos(?)}";
			stmt3 = con.prepareCall(sql3.toString());
			
			stmt3.setInt(1, idFactura);
			
			ResultSet rs3 = stmt3.executeQuery();

			ArrayList<Recibo> recibos = new ArrayList<Recibo>();
			while (rs3.next()) {
				Recibo recibo = new Recibo(rs3.getString("recibo_sucursal"),rs3.getLong("recibo_nro"));
				recibo.setTotal(rs3.getDouble("importe"));
				recibo.setFecha(rs3.getDate("recibo_fecha"));
				recibos.add(recibo);
			}
			fc.setRecibosAdelantos(recibos);
			
						
		} catch (Exception e) {
			_log.debug("error al buscar factura uoma/amtima", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt2, con);
		}
		return fc;
	}
	
	public List<Factura> getFacturas(BusquedaFacturasFiltro filtro) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<Factura> facturas = new ArrayList<Factura>();
		
		try {

			con = ConnectionHelper.getConnection();
			
			String sql = "{call uoma.buscar_facturas(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, filtro.getNumero());
			stmt.setString(2, filtro.getTipo());
			stmt.setString(3, filtro.getLetra());
			stmt.setString(4, filtro.getSucursal());
			
			if(filtro.getFechaDesde()!=null) {
				stmt.setDate(5, new java.sql.Date(filtro.getFechaDesde().getTime()));
			}else {
				stmt.setNull(5, Types.DATE);
			}
			if(filtro.getFechaHasta()!=null) {
				stmt.setDate(6, new java.sql.Date(filtro.getFechaHasta().getTime()));
			}else {
				stmt.setNull(6, Types.DATE);
			}
			
			if( filtro.getPagina()!=null) {
			  stmt.setInt(7, filtro.getPagina());
			}else {
			  stmt.setNull(7, Types.INTEGER);	
			}
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				facturas.add(Factura.getMapping("",rs));
			}
			
		} catch (Exception e) {
			_log.debug("error al buscar facturas uoma/amtima", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return facturas;
	}
	
	public List<Factura> getFacturasPeriodo(Date fechaDesde, Date fechaHasta) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<Factura> facturas = new ArrayList<Factura>();
		
		try {

			con = ConnectionHelper.getConnection();
			
			String sql = "{call uoma.buscar_facturas_periodo(?,?)}";

			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				facturas.add(Factura.getMapping("",rs));
			}
			
		} catch (Exception e) {
			_log.debug("error al buscar facturas periodo uoma/amtima", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return facturas;
	}

	public void insertarLoginCmsResponse(LoginCmsResponse resp, String usuario) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		
		try {

			con = ConnectionHelper.getConnection();
			
			String sql = "{call uoma.inserta_factura_login_ticket(?,?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, resp.getSource());
			stmt.setString(2, resp.getDestination());
			stmt.setString(3, resp.getUniqueId());
			stmt.setTimestamp(4, new java.sql.Timestamp(resp.getGenerationTime().getTime()));
			stmt.setTimestamp(5, new java.sql.Timestamp(resp.getExpirationTime().getTime()));
			stmt.setString(6, resp.getToken() );
			stmt.setString(7, resp.getSign());
			stmt.setString(8, usuario);
			
			stmt.executeUpdate();

		} catch (Exception e) {
			_log.debug("error al insertar LoginCmsResponse", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

	}
	
	public LoginCmsResponse buscarLoginCmsResponseVigente() throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		LoginCmsResponse resp = null;
		
		try {

			con = ConnectionHelper.getConnection();
			
			String sql = "{ call uoma.buscar_factura_login_ticket_vigente() }";

			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				
				resp = LoginCmsResponse.getMapping("l__", rs);
			}
			
		} catch (Exception e) {
			_log.debug("error al buscar LoginCmsResponse", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}

		return resp;
	}
	@Deprecated
	public int obtenerProximoNumeroFactura(String ptoVta, String letra) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int idProxFactura = 0;
		try {
			con = ConnectionHelper.getConnection();

			String sql = "{? = call uoma.propone_numero_factura(?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, ptoVta);
			stmt.setString(3, letra);
			stmt.executeUpdate();
			idProxFactura = stmt.getInt(1);
			
		} catch (Exception e) {
			_log.error("Error al proponer Id Factura ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return idProxFactura;
	}

	
public ClaseBase getPtoVtaJurisdiccion(String id) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		ClaseBase fc = new ClaseBase();
		try {
			
			String sql = "{call uoma.buscar_factura_cfg_jurisdiccion(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, id);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				fc.setId(rs.getString("id")); 
				fc.setDescripcion(rs.getString("descripcion"));
			}
		} catch (Exception e) {
			_log.debug("error al buscar jurisdiccion factura uoma/amtima", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return fc;
	}

	
public Cliente getConfiguracionPtoVta(String id) throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null;
	Cliente cl = new Cliente();
	try {
		
		String sql = "{call uoma.buscar_factura_cfg(?)}";
		
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		
		stmt.setString(1, id);
		
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			cl.setRazonSocial(rs.getString("nombre_fantasia"));
			
			Domicilio domicilio = new Domicilio();
			domicilio.setCalle(rs.getString("direccion"));
			domicilio.setTelefono(rs.getString("telefono"));
			cl.setDomicilio(domicilio);
			cl.setCategoriaIVA(rs.getString("condicion_iva"));
			cl.setDocumentoNro(rs.getString("ingresos_brutos"));
			cl.setDocumentoTipo(rs.getString("inicio_actividades"));
			
		}
	} catch (Exception e) {
		_log.debug("error al buscar cfg factura uoma", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt,con);
	}
	return cl;
}


public List<FacturaIngreso> getPagosFacturasPeriodo(Date dde,Date hta) throws SystemException {
	
	Connection con = null;
	CallableStatement stmt2 = null;
	ArrayList<FacturaIngreso> ingresos = new ArrayList<FacturaIngreso>();
	try {
		con = ConnectionHelper.getConnection();
		String sql2 = "{call uoma.buscar_factura_ingresos_by_fechas(?,?)}";
		stmt2 = con.prepareCall(sql2.toString());
		
		stmt2.setDate(1, new java.sql.Date(dde.getTime()));
		
		stmt2.setDate(2, new java.sql.Date(hta.getTime()));
		
		ResultSet rs2 = stmt2.executeQuery();
		
		while (rs2.next()) {
			FacturaIngreso fi =FacturaIngreso.getMapping(rs2,"fi__",1);
			Factura fa= new Factura();
			fa.setTipo(rs2.getString("fa__tipo"));
			fa.setSucursal(rs2.getString("fa__sucursal"));
			fi.setFactura(fa);
			ingresos.add(fi);
		}
					
	} catch (Exception e) {
		_log.debug("error al buscar ingresos factura uoma/amtima", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt2, con);
	}
	return ingresos;
}

public List<Factura> getFacturasPendientesSincronizar() throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null;
	ArrayList<Factura> facturas = new ArrayList<Factura>();
	
	try {

		con = ConnectionHelper.getConnection();
		
		String sql = "{call uoma.buscar_facturas_pendientes_sincronizar()}";

		stmt = con.prepareCall(sql.toString());
		
		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			facturas.add(Factura.getMapping("",rs));
		}
		
	} catch (Exception e) {
		_log.debug("error al buscar facturas uoma pendiente sincronizar", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt,con);
	}
	return facturas;
}


public Long registraProcesoTransferenciaCentralFactura(Long id, Long idCentral, Date fechaProceso) throws SystemException{
	
    SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");

	Connection con = null;
	CallableStatement stmt = null;
	
	Long idFactura=0L;
	Boolean nuevo=false;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();

	
			
		String sql = "{call uoma.registra_proceso_factura(?,?,?) }";	
		stmt = con.prepareCall(sql.toString());
			
		stmt.setBigDecimal(1,new BigDecimal(id));
		stmt.setBigDecimal(2,new BigDecimal(idCentral));
		stmt.setDate(3, new java.sql.Date(fechaProceso.getTime() ));
		
		stmt.executeUpdate();
					
		

		con.commit();
		
		
	} catch (SQLException e) {
		_log.error("Error al registrar transferencia recibo Hoteles uoma", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SystemException(e);
	} catch (Exception e) {
		_log.error("Error al registrar transferencia recibo Hoteles uom", e);
		
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}

	return id;
}


public int updateFactura(Factura factura, String usuario) throws SystemException, SQLException{
	
	Connection con = null;
	CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null, stmt4 = null;
	
	int idClienteNuevo = -1, idFacturaNueva = 0;
	try {
		
		con = ConnectionHelper.getConnectionForTransaction();
//		***** FACTURA CABECERA *****
		
		String sql1 = "{call uoma.update_factura(?,?,?,?,?,?,?,?,?,?,?) }";

		stmt1 = con.prepareCall(sql1.toString());

		stmt1.setInt(1, factura.getId());
		if(factura.getFechaCae()!=null) {
			stmt1.setDate(2, new java.sql.Date(factura.getFechaCae().getTime()));
			stmt1.setString(3, factura.getCae());
		}else {
			stmt1.setNull(2,Types.DATE);
			stmt1.setNull(3, Types.VARCHAR);
		}
		stmt1.setBigDecimal(4, factura.getTotalExento());
		stmt1.setBigDecimal(5, factura.getImporteNeto());
		stmt1.setBigDecimal(6, factura.getIva());
		stmt1.setBigDecimal(7, factura.getImporteTotal());
		if (StringUtils.checkNotEmpty(factura.getObservaciones())){
			stmt1.setString(8, factura.getObservaciones());
		}else{
			stmt1.setNull(8, Types.VARCHAR);
		}
		stmt1.setString(9, usuario);
		stmt1.setBigDecimal(10, factura.getPercepcion());
		stmt1.setBigDecimal(11, factura.getIvaReintegro());
		
		stmt1.executeUpdate(); 

		
		idFacturaNueva =  factura.getId();
		
		
//		***** FACTURA DETALLE *****
		

		
        String sql2 = "{call uoma.delete_factura_detalle(?) }";
		stmt2 = con.prepareCall(sql2.toString());
		stmt2.setInt(1, factura.getId());
		stmt2.executeUpdate();
		
		sql2 = "{call uoma.inserta_factura_detalle(?,?,?,?) }";
		
		stmt2 = con.prepareCall(sql2.toString());
	
		for (Iterator<FacturaDetalle> iterator = factura.getDetalles().iterator(); iterator.hasNext();) {
			FacturaDetalle fd =  iterator.next();
			
			stmt2.setInt(1, idFacturaNueva);
			stmt2.setInt(2, fd.getDetalle().getId());
			stmt2.setBigDecimal(3, fd.getPrecio());
			stmt2.setString(4, usuario);
			
			stmt2.executeUpdate();
		}
		
		sql2 = "{call uoma.delete_factura_ingresos(?) }";
		stmt2 = con.prepareCall(sql2.toString());
		stmt2.setInt(1, factura.getId());
		stmt2.executeUpdate();
		
		
		String sql3 = null;
			sql3 = "{call uoma.inserta_factura_ingreso (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

		stmt3 = con.prepareCall(sql3.toString());
		
		for (Iterator<FacturaIngreso> iterator = factura.getIngresos().iterator(); iterator.hasNext();) {
			FacturaIngreso fi =  iterator.next();
			Ingreso i =  fi.getIngreso();
			stmt3.setInt(1, idFacturaNueva);
		
			if (i.getTipo().equals("Cheque")) {
				stmt3.setBigDecimal(2, new BigDecimal( i.getNumeroStr()) ) ;
			} else {
				stmt3.setBigDecimal(2, null);
			}

			if (i.getTipo().equals("Cheque")) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
				stmt3.setInt(3, i.getBanco().getId_banco());
			} else if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
				stmt3.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt3.setNull(3, java.sql.Types.INTEGER);
			}

			if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") || 
					i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ||
					i.getTipo().equalsIgnoreCase("Tarjeta Débito")) {
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
			} else {
				stmt3.setNull(7, java.sql.Types.INTEGER);
			}
			stmt3.setString(8, usuario);

			if (i.getTipo().equalsIgnoreCase("Transferencia Bancaria") ) {
				stmt3.setInt(9, i.getCuentaBancaria().getId_cuenta_bcria());
				stmt3.setInt(10, DepositoBancario.ID_TIPO_TRANSFERENCIA);
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") ) {
				stmt3.setNull(9, Types.INTEGER);
			 	stmt3.setInt(10, TarjetaDebitoCredito.ID_TIPO_CREDITO);
			} else if (i.getTipo().equalsIgnoreCase("Tarjeta Débito") ) {
				stmt3.setNull(9, Types.INTEGER);
				stmt3.setInt(10,  TarjetaDebitoCredito.ID_TIPO_DEBITO );		
			} else if (i.getTipo().equalsIgnoreCase("FinanciacionTurismo") ) {
				stmt3.setNull(9, Types.INTEGER);
				stmt3.setInt(10, FinanciacionTurismo.TURISMO);		
			} else if (i.getTipo().equalsIgnoreCase("CuentaCorriente") ) {
				stmt3.setNull(9, Types.INTEGER);
				stmt3.setInt(10, CuentaCorriente.CTACTE);		
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
			
			if (i.getTipo().equalsIgnoreCase("Tarjeta Crédito") || i.getTipo().equalsIgnoreCase("Tarjeta Débito")) {
				stmt3.setInt(14, i.getEmisor());
			 	stmt3.setInt(15, i.getCuotas());
			} else {
				stmt3.setNull(14, Types.INTEGER);
				stmt3.setNull(15, Types.INTEGER);
			}
			stmt3.executeUpdate();
			
			
		}
		

		con.commit();
		
		
	} catch (SQLException e) {
		_log.error("Error al insertar factura uoma-amtima", e);
		
		ConnectionHelper.rollback(con);
		
		throw new SQLException(e);
	} catch (Exception e) {
		_log.error("Error global al insertar factura uoma-amtima", e);
		throw new SystemException(e);
		
	} finally {
		ConnectionHelper.cerrar(stmt4);
		ConnectionHelper.cerrar(stmt3);
		ConnectionHelper.cerrar(stmt2);
		ConnectionHelper.cerrar(stmt1);
		ConnectionHelper.cerrar(stmt, con);
	}
	return idFacturaNueva;
}


public Cliente getClienteById(Integer idCliente) throws SystemException {
	Connection con = null;
	Cliente cliente = null;
	CallableStatement stmt = null;
	try {
		String sql = "{call uoma.buscar_cliente_by_id(?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		if(StringUtils.checkNotEmpty(idCliente)) {
			stmt.setInt(1,idCliente);
		}else {
			stmt.setNull(1, Types.INTEGER);
		}
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			cliente = Cliente.getMapping("",rs);
		}
	} catch (Exception e) {
		_log.debug("error al traer clientes", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return cliente;
}
	
}

