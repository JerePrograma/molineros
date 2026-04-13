package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosHospitales;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosLiquidacionesPendientes;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaPrestadores;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaReintegros;
import ar.com.ospim.liquidaciones.reportes.bean.DebitosaTotal;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;

/**
 * <a href="BusquedaDebitoTercerizadorasServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Pablo Conde
 * 
 */
public class BusquedaDebitoTercerizadorasServiceImpl {
	static 	int entidad = WebKeysGlobal.OSPIM;


	private static Log _log = LogFactoryUtil.getLog(BusquedaDebitoTercerizadorasServiceImpl.class);

	public List<DebitosLiquidacionesPendientes> getBusquedaDebitosaLiquidacionesPendientes(Date fechaDesde, Date fechaHasta , DebitosaTotal debitosaTotal , String idTercerizadoras)
			throws SystemException, NumberFormatException, ParseException {


		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<DebitosLiquidacionesPendientes> debitosAutogestion = null;
		debitosAutogestion = new ArrayList<DebitosLiquidacionesPendientes>();
		BigDecimal montoTotal = new BigDecimal(0);
		BigDecimal montoPrestador = new BigDecimal(0);
	

		try {
			String sql = "{call public.reporte_debito_liq_pendientes(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
			stmt.setString(3, idTercerizadoras);
			
			String ordenPagoAnterior = null;

			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DebitosLiquidacionesPendientes debito = new DebitosLiquidacionesPendientes();
				
				debito.setNumero(rs.getBigDecimal("v_id_liquidacion"));
				debito.setHospitalesAutogestion(rs.getString("v_nombre_prestador"));
				debito.setFactura(rs.getString("v_numero_factura"));
				debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
				debito.setCargoPrestadoraReclamo(rs.getBigDecimal("v_cargo_reclamo") !=  null ? rs.getBigDecimal("v_cargo_reclamo") : new BigDecimal("0"));
				

				//acumulador
				montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0") ) ;
			
				debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
				montoTotal = montoTotal.add(rs.getBigDecimal("v_monto_prestador"));
		
			    debitosAutogestion.add(debito);

				
			}
			debitosaTotal.setMontoLiquidacionPendiente(montoPrestador);
			debitosaTotal.setMontoLiquidacionPendienteDebito(montoPrestador);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return debitosAutogestion;
	}
	
	
	public List<?> getBusquedaDebitosaGrabados(String tipo, Date fechaHasta ,  String idTercerizadoras)
			throws SystemException, NumberFormatException, ParseException {


		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<DebitosLiquidacionesPendientes> debitosAutogestion = null;
		List<DebitosHospitales> debitosHospitales = null;
		ArrayList<DebitosaReintegros> listaReintegros = null;
		listaReintegros = new ArrayList<DebitosaReintegros>();
		ArrayList<DebitosaPrestadores> listaPrestadores= null;
		listaPrestadores = new ArrayList<DebitosaPrestadores>();
		debitosAutogestion = new ArrayList<DebitosLiquidacionesPendientes>();
		BigDecimal montoTotal = new BigDecimal(0);
		BigDecimal montoPrestador = new BigDecimal(0);
	

		try {
			String sql = "{call public.reporte_debito_grabado_detalle(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipo);
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
			stmt.setString(3, idTercerizadoras);
			
			ResultSet rs = stmt.executeQuery();

			
			if (WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES.equals(tipo) ){
				
				while (rs.next()) {
					DebitosLiquidacionesPendientes debito = new DebitosLiquidacionesPendientes();
					
					debito.setNumero(rs.getBigDecimal("numero"));
					debito.setHospitalesAutogestion(rs.getString("descripcion"));
					debito.setFactura(rs.getString("numero_factura"));
					debito.setMonto(rs.getBigDecimal("monto_debitar"));
					debito.setCargoPrestadoraReclamo(rs.getBigDecimal("monto_debitar_reclamo") !=  null ? rs.getBigDecimal("monto_debitar_reclamo") : new BigDecimal("0"));
					
					
					//acumulador
					montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_debitar") != null ? rs.getBigDecimal("monto_debitar") : new BigDecimal("0") ) ;
					
					debito.setMonto(rs.getBigDecimal("monto_debitar"));
					montoTotal = montoTotal.add(rs.getBigDecimal("monto_debitar"));
					
					debitosAutogestion.add(debito);
					
					
				}
				//debitosaTotal.setMontoLiquidacionPendiente(montoPrestador);
				//debitosaTotal.setMontoLiquidacionPendienteDebito(montoPrestador);
				return debitosAutogestion;

			}else if (WebKeysLiquidaciones.DEBITOS_HOSPITALES.equals(tipo)){
				debitosHospitales = new ArrayList<DebitosHospitales>();
				while (rs.next()) {
					DebitosHospitales debito = new DebitosHospitales();
					
					debito.setNumero(rs.getBigDecimal("numero_op"));

					debito.setHospital(rs.getString("descripcion"));
					debito.setFactura(rs.getString("numero_factura"));
					debito.setMonto(rs.getBigDecimal("monto_debitar"));
					debito.setOrdenPago(rs.getString("numero_op"));
					
					debito.setIdLiquidacion(rs.getInt("numero"));
				
					
					if (!this.existeElemento(debitosHospitales, debito) ) {
						debitosHospitales.add(debito);	
						montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_debitar") != null ? rs.getBigDecimal("monto_debitar") : new BigDecimal("0") ) ;
					}
					
				}
				//debitosaTotal.setMontoHospitales(montoPrestador);
				//debitosaTotal.setMontoHospitaleDebito(montoPrestador);
				return debitosHospitales;
			}else if (WebKeysLiquidaciones.DEBITOS_REINTEGROS.equals(tipo)){
				String ordenPagoAnterior = null;


				while (rs.next()) {
					DebitosaReintegros reintegro = new DebitosaReintegros();						
					
					reintegro.setDescripcion(rs.getString("descripcion"));
					reintegro.setDocumento(rs.getString("numero_documento"));
					reintegro.setFechaOP(rs.getDate("fecha_op"));
					reintegro.setSeccional(rs.getString("desc_seccional"));
					reintegro.setNumeroOP(rs.getString("numero_op"));
					
					
					reintegro.setApellido(rs.getString("apellido"));
					reintegro.setNombre(rs.getString("nombre"));
					reintegro.setNumReintegro(rs.getInt("numero"));
					montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_debitar") != null ? rs.getBigDecimal("monto_debitar") : new BigDecimal("0") ) ;
					
					montoTotal = montoTotal.add(rs.getBigDecimal("monto_debitar"));
					reintegro.setImporteTotal(rs.getBigDecimal("monto_debitar"));
					reintegro.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));

					ordenPagoAnterior = reintegro.getNumeroOP();
					
					

					listaReintegros.add(reintegro);
					
				}
				return listaReintegros;
			}else if (WebKeysLiquidaciones.DEBITOS_PRESTADORES.equals(tipo)){
				while (rs.next()) {
					DebitosaPrestadores prestador= new DebitosaPrestadores();
				
				
					prestador.setNumero(rs.getBigDecimal("numero"));

					
					prestador.setPrestador(rs.getString("descripcion"));
					prestador.setFactura(rs.getString("numero_factura"));
					prestador.setOrdenPago(rs.getString("numero_op"));
					prestador.setCargoPrestadora(rs.getBigDecimal("monto_debitar"));
					prestador.setMonto(rs.getBigDecimal("monto_debitar"));
					montoTotal = montoTotal.add(rs.getBigDecimal("monto_debitar"));
					montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_debitar") != null ? rs.getBigDecimal("monto_debitar") : new BigDecimal("0") ) ;

					prestador.setIdLiquidacion(rs.getInt("numero"));
					prestador.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
					prestador.setReclamosPrestacionales(rs.getString("reclamos"));
						
					if (!this.existeElemento(listaPrestadores, prestador) ) {
							listaPrestadores.add(prestador);	
					}										
				}		
				//debitosaTotal.setMontoPrestadores(montoTotal);
				//debitosaTotal.setMontoPrestadoreDebito(montoPrestador);
				return listaPrestadores;

			}
			//debitosaTotal.setMontoReintegros(montoTotal);
			//debitosaTotal.setMontoReintegroDebito(montoPrestador);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	

	public List<DebitosHospitales> getBusquedaDebitosHospitales(
			Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal , String idTercerizadoras) {
		Connection con = null;
		CallableStatement stmt = null;
		List<DebitosHospitales> debitosHospitales = null;
		BigDecimal montoTotal = new BigDecimal(0);
		BigDecimal montoPrestador = new BigDecimal(0);
		BigDecimal aux = new BigDecimal(0);
		Boolean existe=false;
		
		try {
			String sql = "{call public.reporte_debito_hospitales(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
			stmt.setString(3, idTercerizadoras);
			
			ResultSet rs = stmt.executeQuery();
			debitosHospitales = new ArrayList<DebitosHospitales>();
			while (rs.next()) {
				DebitosHospitales debito = new DebitosHospitales();
				
				debito.setNumero(rs.getBigDecimal("v_id_orden_pago"));

				debito.setHospital(rs.getString("v_nombre_prestador"));
				debito.setFactura(rs.getString("v_numero_factura"));
				debito.setMonto(rs.getBigDecimal("v_monto_prestador"));
				debito.setOrdenPago(rs.getString("v_id_orden_pago"));
				
				debito.setIdLiquidacion(rs.getInt("v_id_liquidacion"));
			
				
//				if (!this.existeElemento(debitosHospitales, debito) ) {
//					debitosHospitales.add(debito);	
//					montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0") ) ;
//				}
					
//DS AGregad0 2022-09-21 Prueba porque traia 2 prestaciones para una liquidacion y no sumaba		
				montoPrestador = montoPrestador.add(rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0") ) ;
				aux=rs.getBigDecimal("v_monto_prestador") != null ? rs.getBigDecimal("v_monto_prestador") : new BigDecimal("0");
				existe=false;
				for(DebitosHospitales deb : debitosHospitales){
				      if(deb.getIdLiquidacion() == debito.getIdLiquidacion()){
				    	  deb.setMonto(deb.getMonto().add(aux));
				    	  existe=true;
				      }
			    }
				if(!existe) debitosHospitales.add(debito);
//Fin Agregado				
			}
			debitosaTotal.setMontoHospitales(montoPrestador);
			debitosaTotal.setMontoHospitaleDebito(montoPrestador);
			
			
		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return debitosHospitales;
	}

	

	public List<DebitosaReintegros> getBusquedaDebitosReintegros(Date fechaDesde, Date fechaHasta, DebitosaTotal debitosaTotal  , String idTercerizadoras) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<DebitosaReintegros> listaReintegros = null;
		listaReintegros = new ArrayList<DebitosaReintegros>();
		BigDecimal montoTotal = new BigDecimal(0);
		BigDecimal montoPrestador = new BigDecimal(0);
		try {
					
			String sql = "{call public.reporte_debito_reintegros(?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, fechaDesde == null ? null : new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, fechaHasta == null ? null : new java.sql.Date(fechaHasta.getTime()));
			stmt.setString(3, idTercerizadoras);
			
			String ordenPagoAnterior = null;

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DebitosaReintegros reintegro = new DebitosaReintegros();						
				
				reintegro.setDescripcion(rs.getString("descripcion_r"));
				reintegro.setDocumento(rs.getString("docu_numero"));
				reintegro.setFechaOP(rs.getDate("alta_fecha_op"));
				//reintegro.setImporteTotal(rs.getBigDecimal("importe"));
				reintegro.setSeccional(rs.getString("seccional"));
				reintegro.setNumeroOP(rs.getString("id_orden_pago"));
				//reintegro.setFechaOP(rs.getDate("alta_fecha_op"));
				//reintegro.setCargoPrestadora(rs.getBigDecimal("monto_prestador"));
				
				//acumulador
				
				reintegro.setApellido(rs.getString("apellido"));
				reintegro.setNombre(rs.getString("nombre"));
				reintegro.setNumReintegro(rs.getInt("id_reintegro"));
				montoPrestador = montoPrestador.add(rs.getBigDecimal("monto_prestador") != null ? rs.getBigDecimal("monto_prestador") : new BigDecimal("0") ) ;
				
				montoTotal = montoTotal.add(rs.getBigDecimal("monto_prestador"));
				reintegro.setImporteTotal(rs.getBigDecimal("monto_prestador"));
				
				ordenPagoAnterior = reintegro.getNumeroOP();
				Integer reclamo=0;
				try {
					reclamo=rs.getInt("id_reclamo_prestacional");
				}catch(Exception e) {}
				
                reintegro.setReclamoPrestacional(reclamo);  
				listaReintegros.add(reintegro);

			}
		
			debitosaTotal.setMontoReintegros(montoPrestador);
			debitosaTotal.setMontoReintegroDebito(montoPrestador);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return listaReintegros;
		
	}
	
	public List<DebitosaPrestadores> getBusquedaDebitosPrestadores(Date periodoDesde,Date periodoHasta, DebitosaTotal debitosaTotal , String idTercerizadoras) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<DebitosaPrestadores> listaPrestadores= null;
		listaPrestadores = new ArrayList<DebitosaPrestadores>();
		BigDecimal montoTotal = new BigDecimal(0);
		BigDecimal montoPrestador = new BigDecimal(0);
		try {
					
			String sql = "{call public.reporte_debito_prestadores(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(null!=periodoDesde){
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			if(null!=periodoHasta){
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));				
			}else{
				stmt.setNull(2, Types.DATE);				
			}
			stmt.setString(3, idTercerizadoras);
			
			String ordenPagoAnterior = null;
			int idLiquidacion = 0;		
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DebitosaPrestadores prestador= new DebitosaPrestadores();
			
			
				
			
				prestador.setNumero(rs.getBigDecimal("reintegro"));

				
				prestador.setPrestador(rs.getString("prestador"));
				prestador.setFactura(rs.getString("num_comprobante"));
					//prestadore.setMonto(rs.getBigDecimal("monto"));
				prestador.setOrdenPago(rs.getString("id_orden_pago"));
				prestador.setCargoPrestadora(rs.getBigDecimal("cargo_prestadora"));
				prestador.setMonto(rs.getBigDecimal("cargo_prestadora"));
				montoTotal = montoTotal.add(rs.getBigDecimal("cargo_prestadora"));

				prestador.setIdLiquidacion(rs.getInt("reintegro"));
				
				try {
					prestador.setReclamoPrestacional(rs.getInt("id_reclamo_prestacional"));
				}catch(Exception e) {
					
				}
					
				if (!this.existeElemento(listaPrestadores, prestador) ) {
						listaPrestadores.add(prestador);	
						montoPrestador = montoPrestador.add(rs.getBigDecimal("cargo_prestadora") != null ? rs.getBigDecimal("cargo_prestadora") : new BigDecimal("0") ) ;
				}
					
				
				
				
				
				//ordenPagoAnterior = prestadore.getOrdenPago();
									
			}		
			debitosaTotal.setMontoPrestadores(montoPrestador);
			debitosaTotal.setMontoPrestadoreDebito(montoPrestador);
 		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaPrestadores;
	}

	 
	private boolean existeElemento(ArrayList<DebitosaPrestadores> listaPrestadores, DebitosaPrestadores prestador){
		boolean found = false;
		String rp="";
		if(prestador.getReclamosPrestacionales()!=null && prestador.getReclamosPrestacionales().length()>0 ) {
			  rp= prestador.getReclamosPrestacionales();
		}else {
			  rp=prestador.getReclamoPrestacional()==null ||prestador.getReclamoPrestacional()==0?"":prestador.getReclamoPrestacional().toString();
		}
		for(DebitosaPrestadores pres : listaPrestadores){
		      if(pres.getIdLiquidacion() == prestador.getIdLiquidacion()){
		           found = true;
		           if(pres.getReclamosPrestacionales()==null || !pres.getReclamosPrestacionales().contains(rp)) {
		        	   pres.setReclamosPrestacionales(pres.getReclamosPrestacionales()+";" +rp);
		           }
		           //also do something  
		           break;
		       }
		}
		if(!found)  prestador.setReclamosPrestacionales(rp);
		return found;
	}

	private boolean existeElemento(List<DebitosHospitales> lista, DebitosHospitales debito){
		boolean found = false;
		for(DebitosHospitales deb : lista){
		      if(deb.getIdLiquidacion() == debito.getIdLiquidacion()){
		           found = true;
		           //also do something
		           break;
		       }
		  }
		  return found;
		}
	   
	
	public DebitosaTotal getBuscarTotalesDebitos(Date fecha , String idTercerizadora) {

		Connection con = null;
		CallableStatement stmt = null;
		DebitosaTotal deb= null;
	
		try {
			String sql = "{call buscar_totales_debitos(?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(null!=fecha){
				stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			stmt.setString(2, idTercerizadora);

			
						
			ResultSet rs = stmt.executeQuery();
			deb = new DebitosaTotal();
			deb.setExisteDebito(false);
			while (rs.next()) {
				
				deb.setExisteDebito(true);
				deb.setMontoHospitales(rs.getBigDecimal("v_monto_hospital"));
				deb.setMontoHospitaleDebito(rs.getBigDecimal("v_monto_hospital_debito"));
				
				deb.setMontoLiquidacionPendiente(rs.getBigDecimal("v_monto_autogestion"));
				deb.setMontoLiquidacionPendienteDebito(rs.getBigDecimal("v_monto_autogestion_devito"));
				
				deb.setMontoPrestadores(rs.getBigDecimal("v_monto_prestador"));
				deb.setMontoPrestadoreDebito(rs.getBigDecimal("v_monto_prestador_debito"));
				
				deb.setMontoReintegros(rs.getBigDecimal("v_monto_reintegro"));
				deb.setMontoReintegroDebito(rs.getBigDecimal("v_monto_reintegro_debito"));
				

				
			}		
 		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return deb;
	}
	
	
	public int grabarTotalesDebitos(DebitosaTotal deb, String user , Date fecha , String idTercerizadoras) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call insertar_totales_debitos(?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setBigDecimal(1, deb.getMontoHospitales());
			stmt.setBigDecimal(2, deb.getMontoHospitaleDebito());
			stmt.setBigDecimal(3, deb.getMontoPrestadores());			
			stmt.setBigDecimal(4, deb.getMontoPrestadoreDebito());
			stmt.setBigDecimal(5, deb.getMontoLiquidacionPendiente());
			stmt.setBigDecimal(6, deb.getMontoLiquidacionPendienteDebito());
			stmt.setBigDecimal(7, deb.getMontoReintegros());
			stmt.setBigDecimal(8, deb.getMontoReintegroDebito());
			stmt.setString(9, user);
			stmt.setDate(10, new java.sql.Date(fecha.getTime()));
			stmt.setString(11, idTercerizadoras);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public int grabarLiquidacionesPendientesDebitos(DebitosLiquidacionesPendientes deb, String user , Date fecha , String idTercerizadoras) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null; 
		try {
			String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setString(2, WebKeysLiquidaciones.DEBITOS_LIQ_PENDIENTES);
			stmt.setString(3, idTercerizadoras);
			stmt.setBigDecimal(4, deb.getNumero());
			stmt.setString(5, deb.getHospitalesAutogestion());
			stmt.setString(6,  deb.getFactura());
			stmt.setBigDecimal(7, deb.getMonto());
			stmt.setBigDecimal(8, deb.getCargoPrestadoraReclamo());
			stmt.setNull(9, Types.NUMERIC);
			stmt.setNull(10,  Types.VARCHAR);
			stmt.setNull(11,  Types.VARCHAR);
			stmt.setNull(12,  Types.VARCHAR);
			stmt.setNull(13,  Types.VARCHAR);
			stmt.setNull(14, Types.DATE);
			stmt.setString(15, user);
			stmt.setNull(16, Types.INTEGER);
			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
			
		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	
	
	public int grabarHospitalesDebitos(DebitosHospitales deb, String user , Date fecha , String idTercerizadoras) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setString(2, WebKeysLiquidaciones.DEBITOS_HOSPITALES);
			stmt.setString(3, idTercerizadoras);
			stmt.setBigDecimal(4, deb.getNumero());
			stmt.setString(5, deb.getHospital());
			stmt.setString(6,  deb.getFactura());
			stmt.setBigDecimal(7, deb.getMonto());
			stmt.setNull(8, Types.NUMERIC);
			stmt.setBigDecimal(9, new BigDecimal(deb.getOrdenPago()));
			stmt.setNull(10,  Types.VARCHAR);
			stmt.setNull(11,  Types.VARCHAR);
			stmt.setNull(12,  Types.VARCHAR);
			stmt.setNull(13,  Types.VARCHAR);
			stmt.setNull(14, Types.DATE);
			stmt.setString(15, user);
			stmt.setNull(16, Types.INTEGER);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	public int grabarReintegrosDebitos(DebitosaReintegros deb, String user , Date fecha , String idTercerizadoras) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setString(2, WebKeysLiquidaciones.DEBITOS_REINTEGROS);
			stmt.setString(3, idTercerizadoras);
			stmt.setBigDecimal(4, new BigDecimal(deb.getNumReintegro()));
			stmt.setString(5, deb.getDescripcion());
			stmt.setNull(6,  Types.VARCHAR);
			stmt.setBigDecimal(7, deb.getImporteTotal());
			stmt.setNull(8, Types.NUMERIC);
			stmt.setBigDecimal(9, new BigDecimal(deb.getNumeroOP()));
			stmt.setString(10,  deb.getApellido());
			stmt.setString(11,  deb.getNombre());
			stmt.setString(12,  deb.getDocumento());
			stmt.setString(13, deb.getSeccional());
			stmt.setDate(14, new java.sql.Date(deb.getFechaOP().getTime()));
			stmt.setString(15, user);
			stmt.setInt(16, deb.getReclamoPrestacional());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	

	public int grabarPrestadoresDebitos(DebitosaPrestadores deb, String user , Date fecha , String idTercerizadoras) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call public.insertar_detalles_reporte_debitos_tercerizadoras(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();			
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setString(2, WebKeysLiquidaciones.DEBITOS_PRESTADORES);
			stmt.setString(3, idTercerizadoras);
			stmt.setBigDecimal(4, deb.getNumero());
			stmt.setString(5, deb.getPrestador());
			stmt.setString(6,  deb.getFactura());
			stmt.setBigDecimal(7, deb.getMonto());
			stmt.setNull(8, Types.NUMERIC);
			stmt.setBigDecimal(9, new BigDecimal(deb.getOrdenPago()));
			stmt.setNull(10,  Types.VARCHAR);
			stmt.setNull(11,  Types.VARCHAR);
			stmt.setNull(12,  Types.VARCHAR);
			stmt.setNull(13,  Types.VARCHAR);
			stmt.setNull(14, Types.DATE);
			stmt.setString(15, user);
			if(deb.getReclamoPrestacional()!=null) {
			  stmt.setInt(16, deb.getReclamoPrestacional());
			}else {
			  stmt.setNull(16, Types.INTEGER);
			}
			if(deb.getReclamosPrestacionales() !=null) {
				  stmt.setString(17, deb.getReclamosPrestacionales());
			}else {
				  stmt.setNull(17, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar el item de catastro", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
	
	public boolean existeReporteDebitoTercerizadoras(Date periodoDesde,Date periodoHasta, String idTercerizadora) {

		Connection con = null;
		CallableStatement stmt = null;
		 
		boolean result = true;
		
		try {
			String sql = "{call existe_reporte_debito_tercerizadoras(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(null!=periodoDesde){
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			if(null!=periodoHasta){
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));				
			}else{
				stmt.setNull(2, Types.DATE);				
			}
			stmt.setString(3, idTercerizadora);
						
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				if (rs.getInt(1) == 0){
					return false;
				}
			}


				

				
 		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}
	
	
	
	
	public boolean existeReporteGrabadoDebitoTercerizadoras(Date periodoHasta, String idTercerizadora) {

		Connection con = null;
		CallableStatement stmt = null;
		 
		boolean result = true;
		
		try {
			String sql = "{call existe_reporte_grabado_debito_tercerizadoras(?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(null!=periodoHasta){
				stmt.setDate(1, new java.sql.Date(periodoHasta.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			
			stmt.setString(2, idTercerizadora);
						
			ResultSet rs = stmt.executeQuery();
		
			while (rs.next()) {
				
		
			if (rs.getBigDecimal("total").compareTo(BigDecimal.ZERO) == 0){
				result = false;
			}
				

				
			}		
 		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}
	
	
	
	
	private  Comprobante getComprobante(String idTercerizadoras  ,BigDecimal totalDebitoPrestadoras,  Date fecha, Date periodo) throws SystemException {

		Date fechaActual  = new Date();
		
		int ptoVenta = 2;
		String tipoC = "NDB";
		String nro = ComprobanteServiceUtil.getUltimoNroDebito(entidad);
		int suma = Integer.valueOf(nro) + 1;
		
		
		String nroC = String.valueOf(suma);
		String letra = "";
		int cantCuotas = 0;

		Date fechaEmisionC = fechaActual;
		Date fechaRecepcionC = fechaActual;
		int sucu = 0;
		Date fechaVencimientoC = DateUtils.anyadeMeses(fechaActual, 1);
		
		String cuit = WebKeysGlobal.CUIT_OSPIM;

		Comprobante comprobante = new Comprobante(ptoVenta, tipoC, nroC, cuit,
				fechaEmisionC, fechaRecepcionC,
				totalDebitoPrestadoras, letra, sucu, fechaVencimientoC, null, periodo);
		
		
		
		String cuitAcreedor = null;
		if ("MPS".equalsIgnoreCase(idTercerizadoras)){
			cuitAcreedor = WebKeysGlobal.PREVENCION_CUIT;
		}else if ("OMI".equalsIgnoreCase(idTercerizadoras)){
			cuitAcreedor = WebKeysGlobal.OMINT_CUIT;
		}else if ("MEN".equalsIgnoreCase(idTercerizadoras)){
			cuitAcreedor = WebKeysGlobal.ENSALUD_CUIT;
			
		}
		
		Empresa empresa = null;
		empresa = new Empresa(cuitAcreedor, "000", null);
		empresa.setId_seccional(0);
		
		
		comprobante.setAcreedorEmpresa(empresa);
		comprobante.setObservaciones("Debito Tercerizadora");
		comprobante.setCantCuotas(cantCuotas);
		comprobante.setNroAnticipo(0);
		
		comprobante.setAlta_fecha(new Date());
		
		
		List<ComprobanteConcepto> conceptos = new ArrayList<ComprobanteConcepto>();
		ComprobanteConcepto concepto =  new ComprobanteConcepto();
		
		//concepto.set
		Concepto conceptoCompro = new Concepto();
		
		conceptoCompro.setId(89);
		concepto.setImporte(totalDebitoPrestadoras);
		concepto.setConceptoComprobante(conceptoCompro);
		
		conceptos.add(concepto);
		
		comprobante.setConceptos(conceptos);
		
		return comprobante;

	}
	

	public int grabarTotalesDebitos(BigDecimal totalDebitoPrestadoras , User user , Date fecha , Date periodo, String idTercerizadoras ) throws SystemException {
		Comprobante comp = null;

		comp = getComprobante(idTercerizadoras,totalDebitoPrestadoras,fecha,periodo);
			
		try {
			ComprobanteServiceUtil.save(comp, user, entidad);
		} catch (Exception e) {
			_log.error(e);
		}	

		
		return 0;
	}
	
	
	

	
	


	public List<DebitosaTotal> getArchivosDebitos ()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<DebitosaTotal> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_periodo_debitos_debitos_tercerizadoras()}";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<DebitosaTotal>();
			while (rs.next()) {
				DebitosaTotal archivo = DebitosaTotal.getMapping(rs, "deb_");
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al trae_ultimos_periodo_debitos_debitos_tercerizadoras()", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}
	
	
	
	
	
	
	
	
	
	
}
