package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ConceptoSueldos;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente.SaldoInicial;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion;
import ar.com.ospim.tesoreria.beans.FechaCierre;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.LibroBanco;
import ar.com.ospim.tesoreria.beans.LibroCaja;
import ar.com.ospim.tesoreria.reportes.ReporteEstadoComprobantesExcel.EstadoComprobante;
import ar.com.ospim.tesoreria.reportes.ReporteLibroBancoExcel.EstadoInicialLibroBanco;
import ar.com.ospim.tesoreria.reportes.ReporteLibroCajaExcel.EstadoInicialLibroCaja;
import ar.com.ospim.tesoreria.reportes.ReporteListadoValoresExcel.ReporteListadoValores;
import ar.com.ospim.tesoreria.reportes.ReporteListadodDeDeudasExcel.ItemListadoDeuda;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ContabilidadServiceImpl {
	private static Log _log = LogFactoryUtil
			.getLog(ContabilidadServiceImpl.class);

	private static ContabilidadServiceImpl instance = null;

	public static ContabilidadServiceImpl getInstance() {
		if (null == instance) {
			instance = new ContabilidadServiceImpl();
		}
		return instance;
	}

	public List<LibroBanco> libroBanco(Date fechaInicio, Date fechaFin,
			Integer ctaBcria, int entidad) throws SystemException {
		_log.debug("Buscando libro banco");
		Connection con = null;
		CallableStatement stmt = null;
		List<LibroBanco> libro = new ArrayList<LibroBanco>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call libro_banco(?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call libro_banco_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.libro_banco_uoma(?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setInt(3, ctaBcria);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LibroBanco lb = LibroBanco.getMapping(rs);
				libro.add(lb);
			}
		} catch (Exception e) {
			_log.error("Error al cambiar buscar libro banco", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return libro;
	}

	public EstadoInicialLibroBanco getSaldoInicialBanco(int idCtaBcria,
			Date fechaIni) throws SystemException {
		_log.debug("Buscando libro banco");
		Connection con = null;
		CallableStatement stmt = null;
		EstadoInicialLibroBanco ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_saldo_inicial(?, ?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCtaBcria);
			stmt.setDate(2, new java.sql.Date(fechaIni.getTime()));
			ResultSet rs = stmt.executeQuery();
			ret = new EstadoInicialLibroBanco();
			while (rs.next()) {
				ret.setImporte(rs.getBigDecimal("saldo"));
				ret.setFecha(rs.getDate("fecha_inicio_ejercicio"));

				CuentaBancaria ctaBcria = new CuentaBancaria();
				ctaBcria.setId_cuenta_bcria(rs.getInt("id_cuenta_bcria"));
				ctaBcria.setDescripcion(rs.getString("desripcion"));
				ctaBcria.setNro_cuenta(rs.getInt("nro_cuenta"));
				ctaBcria.setSucursal(rs.getInt("sucursal"));

				ret.setCtaBcria(ctaBcria);
			}
		} catch (Exception e) {
			_log.error("Error al buscar saldo inicial de cta " + idCtaBcria, e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<LibroCaja> libroCaja(Date fechaInicio, Date fechaFin,
			int entidad) throws SystemException {
		_log.debug("Buscando libro banco");
		Connection con = null;
		CallableStatement stmt = null;
		List<LibroCaja> libro = new ArrayList<LibroCaja>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call libro_caja_efectivo(?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call libro_caja_efectivo_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.libro_caja_efectivo_uoma(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LibroCaja lb = LibroCaja.getMapping(rs);
				libro.add(lb);
			}
		} catch (Exception e) {
			_log.error("Error al cambiar buscar libro caja", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return libro;
	}

	public EstadoInicialLibroCaja getSaldoInicialCaja(Date fechaIni, int entidad)
			throws SystemException {
		_log.debug("Buscando saldo libro caja");
		Connection con = null;
		CallableStatement stmt = null;
		EstadoInicialLibroCaja ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_saldo_inicial_caja(?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call buscar_saldo_inicial_caja_amtima(?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.buscar_saldo_inicial_caja_uoma(?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			ResultSet rs = stmt.executeQuery();
			ret = new EstadoInicialLibroCaja();
			while (rs.next()) {
				ret.setImporte(rs.getBigDecimal("saldo"));
				ret.setFecha(rs.getDate("fecha_inicio_ejercicio"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar saldo inicial de caja ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<CuentaCorriente> cuentaCorrienteAcreedores(String cuit,
			String sucu, Integer seccional, Date fechaIni, Date fechaFin,
			Date fechaPagoHasta, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros,
			boolean incluirLiquidacionesFarmacia,
			boolean incluirReintegrosFarmacia, int entidad)
			throws SystemException {
		_log.debug("Buscando cta cte");
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaCorriente> ctas = new ArrayList<CuentaCorriente>();
		int colCont = 1;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call cuenta_corriente_acreedores_nuevo(?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call cuenta_corriente_acreedores_amtima_nuevo(?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.cuenta_corriente_acreedores_uoma(?, ?, ? ,?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(colCont++, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(colCont++, new java.sql.Date(fechaFin.getTime()));
			if (fechaPagoHasta != null) {
				stmt.setDate(colCont++,
						new java.sql.Date(fechaPagoHasta.getTime()));
			} else {
				stmt.setDate(colCont++, null);
			}
			stmt.setBoolean(colCont++, incluirProveedores);
			if (entidad != WebKeysGlobal.UOMA) {
				stmt.setBoolean(colCont++, incluirLiquidaciones);
				stmt.setBoolean(colCont++, incluirReintegros);
			}
			stmt.setString(colCont++, StringUtils.checkEmpty(cuit) ? null
					: cuit);
			stmt.setString(colCont++, StringUtils.checkEmpty(sucu) ? null
					: sucu);
			if (seccional != null && seccional.intValue() != 0) {
				stmt.setInt(colCont++, seccional);
			} else {
				stmt.setNull(colCont++, Types.INTEGER);
			}
			if (entidad != WebKeysGlobal.UOMA) {
				stmt.setBoolean(colCont++, incluirLiquidacionesFarmacia);
				stmt.setBoolean(colCont++, incluirReintegrosFarmacia);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CuentaCorriente cta = CuentaCorriente.getMapping(rs, true,
						true, entidad);

				int indexof = ctas.indexOf(cta);
				if (indexof != -1) {
					ctas.get(indexof).getInfo().addAll(cta.getInfo());
				} else {
					ctas.add(cta);
				}
			}
		} catch (Exception e) {
			_log.error("Error al cambiar buscar cta cte", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctas;
	}

	public List<EstadoComprobante> listadoEstadoComprobantes(Date fechaIni,
			Date fechaFin, Date fechaPagoFin, String cuit, String sucu,
			Integer seccional, boolean soloConSaldo,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros, Date fechaEmiIni, Date fechaEmiFin,
			int entidad) throws SystemException {
		_log.debug("Buscando listado estado comp");
		Connection con = null;
		CallableStatement stmt = null;
		List<EstadoComprobante> ctas = new ArrayList<EstadoComprobante>();
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call listado_estado_comprobantes(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.listado_estado_comprobantes_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call listado_estado_comprobantes_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setDate(3, new java.sql.Date(fechaPagoFin.getTime()));
			stmt.setString(4, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(5, StringUtils.checkEmpty(sucu) ? null : sucu);
			if (seccional != null && seccional.intValue() != 0) {
				stmt.setInt(6, seccional);
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setBoolean(7, soloConSaldo);

			if (entidad != WebKeysGlobal.OSPIM) {
				if (null != fechaEmiIni) {
					stmt.setDate(8, new java.sql.Date(fechaEmiIni.getTime()));
				} else {
					stmt.setNull(8, Types.DATE);
				}
				if (null != fechaEmiFin) {
					stmt.setDate(9, new java.sql.Date(fechaEmiFin.getTime()));
				} else {
					stmt.setNull(9, Types.DATE);
				}
			}

			if (entidad == WebKeysGlobal.OSPIM) {
				stmt.setBoolean(8, incluirProveedores);
				stmt.setBoolean(9, incluirLiquidaciones);
				stmt.setBoolean(10, incluirReintegros);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				EstadoComprobante est = EstadoComprobante.getMapping(rs,
						entidad);
				ctas.add(est);
			}
		} catch (Exception e) {
			_log.error("Error al  listado estado comp", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctas;
	}

	public List<CuentaCorriente> cuentaCorrienteActasYConvenios(Date fechaIni,
			Date fechaFin, String cuit, String sucu, Integer seccional, int id,
			String tipo, int entidad) throws SystemException {
		_log.debug("Buscando cta cte actas y convenios");
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaCorriente> ctas = new ArrayList<CuentaCorriente>();
		String sql = null;
		if(id==0){
			sql = "{call cuenta_corriente_actas_convenios(?, ?, ? ,?)}";
		}else{
			sql = "{call cuenta_corriente_actas_convenios(?, ?, ? ,?, ?, ?)}";
		}
		
		try {
//			con = ConnectionHelper.getConnection();
			con = ConnectionHelper.getReportesOspimConnection();
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call cuenta_corriente_actas_convenios_amtima(?, ?, ? ,?)}";
			}
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.cuenta_corriente_actas_convenios_uoma(?, ?, ? ,?)}";
			}
			int cont=1;
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(cont++, StringUtils.checkEmpty(sucu) ? null : sucu);
			stmt.setDate(cont++, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(cont++, new java.sql.Date(fechaFin.getTime()));
			if(id>0){
				stmt.setInt(cont++, id);
				stmt.setString(cont++, tipo);				
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CuentaCorriente cta = CuentaCorriente.getMapping(rs, false,
						false, entidad);

				int indexof = ctas.indexOf(cta);
				if (indexof != -1) {
					ctas.get(indexof).getInfo().addAll(cta.getInfo());
				} else {
					ctas.add(cta);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar cta cte actas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctas;
	}

	public List<CuentaCorriente> cuentaCorrienteActasYConveniosConApoContrib(
			Date fechaIni, Date fechaFin, String cuit, String sucu,
			Integer seccional) throws SystemException {
		_log.debug("Buscando cta cte actas y convenios");
		Connection con = null;
		CallableStatement stmt = null;
		List<CuentaCorriente> ctas = new ArrayList<CuentaCorriente>();
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call cuenta_corriente_actas_convenios_apo_cont(?, ?, ? ,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(2, StringUtils.checkEmpty(sucu) ? null : sucu);
			stmt.setDate(3, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				CuentaCorriente cta = CuentaCorriente.getMapping(rs, false,
						false, WebKeysGlobal.OSPIM);

				int indexof = ctas.indexOf(cta);
				if (indexof != -1) {
					ctas.get(indexof).getInfo().addAll(cta.getInfo());
				} else {
					ctas.add(cta);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar cta cte actas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctas;
	}

	public List<ItemSubdiarioIngreso> subdiarioIngresos(Date fechaIni,
			Date fechaFin, Empresa empresa, boolean incluirBcrios,
			boolean incluirRecibos, boolean incluirAfip, boolean contabilidad, int entidad)
			throws SystemException {
		_log.debug("Buscando subdiario ing");
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemSubdiarioIngreso> ret = null;
		ItemSubdiarioIngreso est =null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call subdiario_ingresos(?, ?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call subdiario_ingresos_amtima(?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.subdiario_ingresos_uoma(?, ?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			stmt.setString(3, StringUtils.checkEmpty(empresa.getCuit()) ? null
					: empresa.getCuit());
			stmt.setString(4,
					StringUtils.checkEmpty(empresa.getSucursal()) ? null
							: empresa.getSucursal());
			stmt.setBoolean(5, incluirBcrios);
			stmt.setBoolean(6, incluirRecibos);
			stmt.setBoolean(7, incluirAfip);
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ItemSubdiarioIngreso>();
			
			while (rs.next()) {
				est = ItemSubdiarioIngreso.getMapping(rs);
				if (DateUtils.compararFechasTruncarEnDia(est.getFecha(),
						fechaIni) >= 0
						&& DateUtils.compararFechasTruncarEnDia(est.getFecha(),
								fechaFin) <= 0) {
					if((contabilidad && !est.getRazonSocial().equals("ANULADAMISMODIA")) || !contabilidad){
						ret.add(est);	
					}
					
				}
				if (est.getBaja_fecha() != null
						&& DateUtils.compararFechasTruncarEnDia(
								est.getBaja_fecha(), fechaIni) >= 0
						&& DateUtils.compararFechasTruncarEnDia(
								est.getBaja_fecha(), fechaFin) <= 0 && DateUtils.compararFechasTruncarEnDia(est.getBaja_fecha(),est.getFecha())!=0) {
					ItemSubdiarioIngreso estBaja = new ItemSubdiarioIngreso(est);
					estBaja.setFecha(est.getBaja_fecha());
					est.setBaja_fecha(null);
					if((contabilidad && !est.getRazonSocial().equals("ANULADAMISMODIA")) || !contabilidad){
//						ret.add(est);
						ret.add(estBaja); //Agregado porque no estaba tomando las bajas DS 21-01-2021
					}
				} else if (est.getBaja_fecha() != null) {
					est.setBaja_fecha(null);
				}

			}
		} catch (Exception e) {
			_log.error("Error al buscar subdiario ingresos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<ItemSubdiarioIngreso> subdiarioIngresosBoleta(
			Date fechaIni, Date fechaFin, int entidad) throws SystemException {
		_log.debug("Buscando subdiario ing");
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemSubdiarioIngreso> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;

			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call subdiario_ingresos_boleta_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.subdiario_ingresos_boleta_uoma(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ItemSubdiarioIngreso>();
			while (rs.next()) {
				ItemSubdiarioIngreso est = ItemSubdiarioIngreso.getMapping(rs);
				if (DateUtils.compararFechasTruncarEnDia(est.getFecha(),
						fechaIni) >= 0
						&& DateUtils.compararFechasTruncarEnDia(est.getFecha(),
								fechaFin) <= 0) {
					ret.add(est);
				}
				if (est.getBaja_fecha() != null
						&& DateUtils.compararFechasTruncarEnDia(
								est.getBaja_fecha(), fechaIni) >= 0
						&& DateUtils.compararFechasTruncarEnDia(
								est.getBaja_fecha(), fechaFin) <= 0) {
					ItemSubdiarioIngreso estBaja = new ItemSubdiarioIngreso(est);
					estBaja.setFecha(est.getBaja_fecha());
					est.setBaja_fecha(null);
					ret.add(estBaja);
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar subdiario ingresos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<ReporteListadoValores> listadoValores(Date fechaVtoInicio,
			Date fechaVtoFin, Date fechaDptoInicio, Date fechaDptoFin,
			Date fechaRechInicio, Date fechaRechFin, Date fechaReemInicio,
			Date fechaReemFin, String cuit, Integer idBanco,
			Integer depositados, Integer reemplazados, Integer rechazados,
			Integer cta_bcria, int entidad, int nro_cheque, Date fechaReciIni, Date fechaReciFin)
			throws SystemException {
		_log.debug("Buscando listado valores");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteListadoValores> libro = new ArrayList<ReporteListadoValores>();
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call listado_valores(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call listado_valores_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.listado_valores_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)}";
			}

			stmt = con.prepareCall(sql.toString());
			if (fechaVtoInicio == null) {
				stmt.setNull(1, Types.DATE);
			} else {
				stmt.setDate(1, new java.sql.Date(fechaVtoInicio.getTime()));
			}
			if (fechaVtoInicio == null) {
				stmt.setNull(2, Types.DATE);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaVtoFin.getTime()));
			}

			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuit);
			}

			if (idBanco == null || idBanco.intValue() == -1) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, idBanco);
			}

			if (depositados == null || depositados.intValue() == -1) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, depositados);
			}

			if (reemplazados == null || reemplazados.intValue() == -1) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, reemplazados);
			}

			if (rechazados == null || rechazados.intValue() == -1) {
				stmt.setNull(7, Types.INTEGER);
			} else {
				stmt.setInt(7, rechazados);
			}
			if (depositados >= 0) {
				stmt.setDate(8, new java.sql.Date(fechaDptoInicio.getTime()));
				stmt.setDate(9, new java.sql.Date(fechaDptoFin.getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
				stmt.setNull(9, Types.DATE);
			}
			if (reemplazados >= 0) {
				stmt.setDate(10, new java.sql.Date(fechaReemInicio.getTime()));
				stmt.setDate(11, new java.sql.Date(fechaReemFin.getTime()));
			} else {
				stmt.setNull(10, Types.DATE);
				stmt.setNull(11, Types.DATE);
			}
			if (rechazados >= 0) {
				stmt.setDate(12, new java.sql.Date(fechaRechInicio.getTime()));
				stmt.setDate(13, new java.sql.Date(fechaRechFin.getTime()));
			} else {
				stmt.setNull(12, Types.DATE);
				stmt.setNull(13, Types.DATE);
			}

			if (cta_bcria == null || cta_bcria.intValue() <= 0) {
				stmt.setNull(14, Types.INTEGER);
			} else {
				stmt.setInt(14, cta_bcria.intValue());
			}
			if (nro_cheque == 0) {
				stmt.setNull(15, Types.INTEGER);
			} else {
				stmt.setInt(15, nro_cheque);
			}
			
			if (null!=fechaReciIni) {
				stmt.setDate(16, new java.sql.Date(fechaReciIni.getTime()));				
			} else {				
				stmt.setNull(16, Types.DATE);
			}
			
			if (null!=fechaReciFin) {
				stmt.setDate(17, new java.sql.Date(fechaReciFin.getTime()));				
			} else {				
				stmt.setNull(17, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReporteListadoValores lb = ReporteListadoValores.getMapping(rs);
				libro.add(lb);
			}
		} catch (Exception e) {
			_log.error("Error al buscar listado valores", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return libro;
	}
	

	public List<ReporteListadoValores> listadoValores(Date fechaVtoInicio,
			Date fechaVtoFin, Date fechaDptoInicio, Date fechaDptoFin,
			Date fechaRechInicio, Date fechaRechFin, Date fechaReemInicio,
			Date fechaReemFin, String cuit, Integer idBanco,
			Integer depositados, Integer reemplazados, Integer rechazados,
			Integer cta_bcria, int entidad, int nro_cheque, Date fechaReciIni, Date fechaReciFin,
			Integer judicializados,Date fechaJudiInicio,Date fechaJudiFin)
			throws SystemException {
		_log.debug("Buscando listado valores");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteListadoValores> libro = new ArrayList<ReporteListadoValores>();
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call listado_valores(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call listado_valores_amtima(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.listado_valores_uoma(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)}";
			}

			stmt = con.prepareCall(sql.toString());
			if (fechaVtoInicio == null) {
				stmt.setNull(1, Types.DATE);
			} else {
				stmt.setDate(1, new java.sql.Date(fechaVtoInicio.getTime()));
			}
			if (fechaVtoInicio == null) {
				stmt.setNull(2, Types.DATE);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaVtoFin.getTime()));
			}

			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuit);
			}

			if (idBanco == null || idBanco.intValue() == -1) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, idBanco);
			}

			if (depositados == null || depositados.intValue() == -1) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, depositados);
			}

			if (reemplazados == null || reemplazados.intValue() == -1) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, reemplazados);
			}

			if (rechazados == null || rechazados.intValue() == -1) {
				stmt.setNull(7, Types.INTEGER);
			} else {
				stmt.setInt(7, rechazados);
			}
			if (depositados >= 0) {
				stmt.setDate(8, new java.sql.Date(fechaDptoInicio.getTime()));
				stmt.setDate(9, new java.sql.Date(fechaDptoFin.getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
				stmt.setNull(9, Types.DATE);
			}
			if (reemplazados >= 0) {
				stmt.setDate(10, new java.sql.Date(fechaReemInicio.getTime()));
				stmt.setDate(11, new java.sql.Date(fechaReemFin.getTime()));
			} else {
				stmt.setNull(10, Types.DATE);
				stmt.setNull(11, Types.DATE);
			}
			if (rechazados >= 0) {
				stmt.setDate(12, new java.sql.Date(fechaRechInicio.getTime()));
				stmt.setDate(13, new java.sql.Date(fechaRechFin.getTime()));
			} else {
				stmt.setNull(12, Types.DATE);
				stmt.setNull(13, Types.DATE);
			}

			if (cta_bcria == null || cta_bcria.intValue() <= 0) {
				stmt.setNull(14, Types.INTEGER);
			} else {
				stmt.setInt(14, cta_bcria.intValue());
			}
			if (nro_cheque == 0) {
				stmt.setNull(15, Types.INTEGER);
			} else {
				stmt.setInt(15, nro_cheque);
			}
			
			if (null!=fechaReciIni) {
				stmt.setDate(16, new java.sql.Date(fechaReciIni.getTime()));				
			} else {				
				stmt.setNull(16, Types.DATE);
			}
			
			if (null!=fechaReciFin) {
				stmt.setDate(17, new java.sql.Date(fechaReciFin.getTime()));				
			} else {				
				stmt.setNull(17, Types.DATE);
			}
			
			
			if (judicializados == null || judicializados.intValue() == -1) {
				stmt.setNull(18, Types.INTEGER);
			} else {
				stmt.setInt(18, judicializados);
			}
			if (judicializados >= 0) {
				stmt.setDate(19, new java.sql.Date(fechaJudiInicio.getTime()));
				stmt.setDate(20, new java.sql.Date(fechaJudiFin.getTime()));
			} else {
				stmt.setNull(19, Types.DATE);
				stmt.setNull(20, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReporteListadoValores lb = ReporteListadoValores.getMapping(rs);
				libro.add(lb);
			}
		} catch (Exception e) {
			_log.error("Error al buscar listado valores con Judicial Incluido", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return libro;
	}

	
	
	
	
	
	public List<ReporteListadoValores> listadoValoresSeguimiento(Date fechaVtoInicio,
			Date fechaVtoFin, Date fechaDptoInicio, Date fechaDptoFin,
			Date fechaRechInicio, Date fechaRechFin, Date fechaReemInicio,
			Date fechaReemFin, String cuit, Integer idBanco,
			Integer depositados, Integer reemplazados, Integer rechazados,
			Integer cta_bcria, int nro_cheque, Date fechaReciIni, Date fechaReciFin)
			throws SystemException {
		_log.debug("Buscando listado valores");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteListadoValores> libro = new ArrayList<ReporteListadoValores>();
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call listado_valores_seguimiento(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)}";			

			stmt = con.prepareCall(sql.toString());
			if (fechaVtoInicio == null) {
				stmt.setNull(1, Types.DATE);
			} else {
				stmt.setDate(1, new java.sql.Date(fechaVtoInicio.getTime()));
			}
			if (fechaVtoInicio == null) {
				stmt.setNull(2, Types.DATE);
			} else {
				stmt.setDate(2, new java.sql.Date(fechaVtoFin.getTime()));
			}

			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuit);
			}

			if (idBanco == null || idBanco.intValue() == -1) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, idBanco);
			}

			if (depositados == null || depositados.intValue() == -1) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, depositados);
			}

			if (reemplazados == null || reemplazados.intValue() == -1) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, reemplazados);
			}

			if (rechazados == null || rechazados.intValue() == -1) {
				stmt.setNull(7, Types.INTEGER);
			} else {
				stmt.setInt(7, rechazados);
			}
			if (null!=depositados && depositados >= 0) {
				stmt.setDate(8, new java.sql.Date(fechaDptoInicio.getTime()));
				stmt.setDate(9, new java.sql.Date(fechaDptoFin.getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
				stmt.setNull(9, Types.DATE);
			}
			if (null!= reemplazados && reemplazados >= 0) {
				stmt.setDate(10, new java.sql.Date(fechaReemInicio.getTime()));
				stmt.setDate(11, new java.sql.Date(fechaReemFin.getTime()));
			} else {
				stmt.setNull(10, Types.DATE);
				stmt.setNull(11, Types.DATE);
			}
			if (null!=rechazados && rechazados >= 0) {
				stmt.setDate(12, new java.sql.Date(fechaRechInicio.getTime()));
				stmt.setDate(13, new java.sql.Date(fechaRechFin.getTime()));
			} else {
				stmt.setNull(12, Types.DATE);
				stmt.setNull(13, Types.DATE);
			}

			if (cta_bcria == null || cta_bcria.intValue() <= 0) {
				stmt.setNull(14, Types.INTEGER);
			} else {
				stmt.setInt(14, cta_bcria.intValue());
			}
			if (nro_cheque == 0) {
				stmt.setNull(15, Types.INTEGER);
			} else {
				stmt.setInt(15, nro_cheque);
			}
			
			if (null!=fechaReciIni) {
				stmt.setDate(16, new java.sql.Date(fechaReciIni.getTime()));				
			} else {				
				stmt.setNull(16, Types.DATE);
			}
			
			if (null!=fechaReciFin) {
				stmt.setDate(17, new java.sql.Date(fechaReciFin.getTime()));				
			} else {				
				stmt.setNull(17, Types.DATE);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ReporteListadoValores lb = ReporteListadoValores.getMapping(rs);
				lb.setEntidad(rs.getInt("entidad"));
				libro.add(lb);
			}
		} catch (Exception e) {
			_log.error("Error al buscar listado valores seguimiento", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return libro;
	}

	public List<ItemListadoDeuda> listadoDeDeudas(Date fechaIni, Date fechaFin,
			String cuit, String sucu, Integer seccional, Date fechaPagoHasta,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros, int entidad) throws SystemException {
		_log.debug("Buscando listado de deudas");
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemListadoDeuda> ctas = new ArrayList<ItemListadoDeuda>();
		// LAS NDB DEBEN ESTAR AL FINAL
		Map<Integer, ItemListadoDeuda> compsLiquidacion = new HashMap<Integer, ItemListadoDeuda>();
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call listado_de_deudas(?, ?, ? ,?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call listado_de_deudas_amtima(?, ?, ? ,?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.listado_de_deudas_uoma(?, ?, ? ,?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(2, StringUtils.checkEmpty(sucu) ? null : sucu);
			if (seccional != null && seccional.intValue() != 0) {
				stmt.setInt(3, seccional);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			stmt.setDate(4, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(5, new java.sql.Date(fechaFin.getTime()));
			stmt.setDate(6, new java.sql.Date(fechaPagoHasta.getTime()));
			stmt.setBoolean(7, incluirProveedores);
			stmt.setBoolean(8, incluirLiquidaciones);
			stmt.setBoolean(9, incluirReintegros);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemListadoDeuda item = ItemListadoDeuda.getMapping(rs);
				String ctaNro = rs.getString("numero");
				if (!(item.getComprobante().getTipoComprobante().equals("NDB") && StringUtils
						.checkEmpty(ctaNro))) {

					int indexOf = ctas.indexOf(item);
					if (indexOf == -1) {
						ctas.add(item);
					} else {
						ctas.get(indexOf).getDesde().addAll(item.getDesde());
						ctas.get(indexOf).getHasta().addAll(item.getHasta());
					}
				}

				int liquidacion = rs.getInt("id_liquidaciones");
				if (liquidacion != 0) {
					if (!(item.getComprobante().getTipoComprobante()
							.equals("NDB") && StringUtils.checkEmpty(ctaNro))) {
						if (!compsLiquidacion.containsKey(liquidacion)) {
							compsLiquidacion.put(liquidacion, item);
						}
					} else {
						if (compsLiquidacion.containsKey(liquidacion)) {
							// deberia existir, lo dejo dentro del if por las
							// dudas para q no explote
							compsLiquidacion.get(liquidacion).addNotaDebito(
									item.getComprobante());
						}
					}
				}
			}
		} catch (Exception e) {
			_log.error("Error al Buscando listado de deudas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctas;
	}

	public List<EstadoInicialCuentaCorriente> getSaldoInicialCtasCtes(
			String cuit, String sucu, Integer seccionalParam, Date fechaIni,
			boolean incluirProveedores, boolean incluirLiquidaciones,
			boolean incluirReintegros, boolean incluirLiquidaciones_farmacia,
			boolean incluirReintegros_farmacia, int entidad)
			throws SystemException {

		return getCalculoSaldoInicialCtasCtes(cuit, sucu, seccionalParam,
				fechaIni, fechaIni, incluirProveedores, incluirLiquidaciones,
				incluirReintegros, incluirLiquidaciones_farmacia,
				incluirReintegros_farmacia, entidad);

	}

	public List<EstadoInicialCuentaCorriente> saldoInicialCorrienteActasYConvenios(
			String cuit, String sucu, Integer seccionalParam, Date fechaIni,
			int entidad) throws SystemException {
		return getSaldoInicialCtasCtes(cuit, sucu, seccionalParam, fechaIni,
				"cuit", "sucu", "seccional",
				"buscar_saldo_inicial_actas_convenios", entidad);
	}

	public List<EstadoInicialCuentaCorriente> getCalculoSaldoInicialCtasCtes(
			String cuit, String sucu, Integer seccional, Date fechaIni,
			Date fechaPagoHasta, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros,
			boolean incluirLiquidaciones_farmacia,
			boolean incluirReintegros_farmacia, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;

		EstadoInicialCuentaCorriente ctaFinal = new EstadoInicialCuentaCorriente();
		List<EstadoInicialCuentaCorriente> ctaFinalList = new ArrayList<EstadoInicialCuentaCorriente>();
		int colCont = 1;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call cuenta_corriente_acreedores_nuevo(?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call cuenta_corriente_acreedores_amtima_nuevo(?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.cuenta_corriente_acreedores_uoma(?, ?, ? ,?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());

			stmt.setNull(colCont++, Types.DATE);
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaIni);
			cal.add(Calendar.DATE, -1);

			stmt.setDate(colCont++, new java.sql.Date(cal.getTime().getTime()));

			stmt.setDate(colCont++, new java.sql.Date(cal.getTime().getTime()));

			stmt.setBoolean(colCont++, incluirProveedores);
			if (entidad != WebKeysGlobal.UOMA) {
				stmt.setBoolean(colCont++, incluirLiquidaciones);
				stmt.setBoolean(colCont++, incluirReintegros);
			}
			stmt.setString(colCont++, StringUtils.checkEmpty(cuit) ? null
					: cuit);
			if (seccional == 0) {
				stmt.setString(colCont++, StringUtils.checkEmpty(sucu) ? null
						: sucu);
			} else {
				stmt.setString(colCont++, "000");
			}
			if (seccional != null && seccional.intValue() != 0) {
				stmt.setInt(colCont++, seccional);
			} else {
				stmt.setNull(colCont++, Types.INTEGER);
			}
			if (entidad != WebKeysGlobal.UOMA) {
				stmt.setBoolean(colCont++, incluirLiquidaciones_farmacia);
				stmt.setBoolean(colCont++, incluirReintegros_farmacia);
			}

			ResultSet rs = stmt.executeQuery();
			SaldoInicial info = new SaldoInicial();
			List<SaldoInicial> saldos = new ArrayList<SaldoInicial>();
			info.setImporte(BigDecimal.ZERO);
			info.setFecha(cal.getTime());
			while (rs.next()) {
				if (rs.getString("debito_credito").trim().equals("C")) {
					info.setImporte(info.getImporte().add(
							rs.getBigDecimal("importe")));
				} else if (rs.getString("debito_credito").trim().equals("D")) {
					info.setImporte(info.getImporte().subtract(
							rs.getBigDecimal("importe")));
				} else if (rs.getString("debito_credito").trim().equals("I")) {
					info.setImporte(rs.getBigDecimal("importe"));
				}

			}
			Empresa empresa = new Empresa();
			empresa.setCuit(cuit);
			empresa.setSucursal(sucu);
			empresa.setId_seccional(seccional);

			saldos.add(info);
			ctaFinal.setEmpresa(empresa);
			ctaFinal.setSaldosIniciales(saldos);
			ctaFinalList.add(ctaFinal);
		} catch (Exception e) {
			_log.error("Error al cambiar buscar cta cte", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ctaFinalList;
	}

	private List<EstadoInicialCuentaCorriente> getSaldoInicialCtasCtes(
			String cuit, String sucu, Integer seccionalParam, Date fechaIni,
			String paramCuit, String paramSucu, String paramSeccional,
			String sp, int entidad) throws SystemException {
		_log.debug("Buscando saldo inicial");
		Connection con = null;
		CallableStatement stmt = null;
		List<EstadoInicialCuentaCorriente> ret = null;
		String sql = "{call " + sp + "(?, ?, ?, ?)}";
		try {
			con = ConnectionHelper.getConnection();
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call " + sp + "_amtima" + "(?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call " + "uoma." + sp + "_uoma" + "(?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(2, StringUtils.checkEmpty(sucu) ? null : sucu);
			if (seccionalParam != null && seccionalParam.intValue() != 0) {
				stmt.setInt(3, seccionalParam);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			stmt.setDate(4, new java.sql.Date(fechaIni.getTime()));
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<EstadoInicialCuentaCorriente>();
			while (rs.next()) {
				String sucursal = rs.getString(paramSucu);
				int id_seccional = rs.getInt(paramSeccional);
				if (id_seccional != 0) {
					sucursal = String.valueOf(id_seccional);
				}
				EstadoInicialCuentaCorriente est = new EstadoInicialCuentaCorriente(
						rs.getDate("fecha_inicio_ejercicio"),
						rs.getBigDecimal("saldo"), new Empresa(
								rs.getString(paramCuit), sucursal, null));

				int indexOf = ret.indexOf(est);
				if (indexOf == -1) {
					ret.add(est);
				} else {
					ret.get(indexOf).getSaldosIniciales()
							.addAll(est.getSaldosIniciales());
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar saldo inicial de cta " + cuit, e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public boolean isAsientosOrdenados(Date ejercicioIni, Date ejercicioFin,
			int entidad) throws SystemException {
		_log.debug("Buscando isAsientosOrdenados");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call verificar_asientos_ordenados_amtima(?, ?)}";
			}else if(entidad == WebKeysGlobal.UOMA){ 
				sql = "{call uoma.verificar_asientos_ordenados_uoma(?, ?)}";
			}else {
				sql = "{call verificar_asientos_ordenados(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ejercicioIni.getTime()));
			stmt.setDate(2, new java.sql.Date(ejercicioFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getBoolean(1);
			}
		} catch (Exception e) {
			_log.error("Error al isAsientosOrdenados ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return false;
	}

	public void ordenarAsientos(Date ejercicioIni, Date ejercicioFin, int entidad)
			throws SystemException {
		_log.debug("reacomodar_numeros_asiento");
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call reacomodar_numeros_asiento(?, ?)}";
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call reacomodar_numeros_asiento_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.reacomodar_numeros_asiento_uoma(?, ?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(ejercicioIni.getTime()));
			stmt.setDate(2, new java.sql.Date(ejercicioFin.getTime()));
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al reacomodar_numeros_asiento ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<FechaCierre> getFechasCierreContable(int entidad)
			throws SystemException {
		List<FechaCierre> fechas = new ArrayList<FechaCierre>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_fechas_cierre_contable_gestion()}";
			con = ConnectionHelper.getConnection();
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call trae_fechas_cierre_contable_gestion_amtima()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.trae_fechas_cierre_contable_gestion_uoma()}";
			}

			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fechas.add(FechaCierre.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al trae_fechas_cierre_contable_gestion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return fechas;
	}

	public List<FechaCierre> getFechasCierreAsientos(int entidad)
			throws SystemException {
		List<FechaCierre> fechas = new ArrayList<FechaCierre>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_fechas_cierre_contable_asientos()}";
			con = ConnectionHelper.getConnection();
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call trae_fechas_cierre_contable_asientos_amtima()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.trae_fechas_cierre_contable_asientos_uoma()}";
			}

			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fechas.add(FechaCierre.getMapping(rs));
			}
		} catch (Exception e) {
			_log.error("Error al trae_fechas_cierre_contable_asientos ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return fechas;
	}

	public void guardarFechaCierreContableGestion(FechaCierre fechacierre,
			String username, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call insertar_fecha_cierre_contable_gestion(?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_fecha_cierre_contable_gestion_amtima(?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_fecha_cierre_contable_gestion_uoma(?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechacierre.getFecha().getTime()));
			stmt.setString(2, fechacierre.getObservacion());
			stmt.setString(3, username);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al insertar_fecha_cierre_contable_gestion ", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void guardarFechaCierreContableAsientos(FechaCierre fechacierre,
			String username, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call insertar_fecha_cierre_contable_asientos(?, ?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call insertar_fecha_cierre_contable_asientos_amtima(?, ?, ?)}";
			}
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.insertar_fecha_cierre_contable_asientos_uoma(?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechacierre.getFecha().getTime()));
			stmt.setString(2, fechacierre.getObservacion());
			stmt.setString(3, username);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al insertar_fecha_cierre_contable_asientos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void eliminarFechaCierreContableGestion(FechaCierre fechacierre,
			String username, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call eliminar_fecha_cierre_contable_gestion(?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_fecha_cierre_contable_gestion_amtima(?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_fecha_cierre_contable_gestion_uoma(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechacierre.getFecha().getTime()));
			stmt.setString(2, username);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al eliminar_fecha_cierre_contable_gestion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public void eliminarFechaCierreContableAsientos(FechaCierre fechacierre,
			String username, int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call eliminar_fecha_cierre_contable_asientos(?, ?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call eliminar_fecha_cierre_contable_asientos_amtima(?, ?)}";
			}
			if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.eliminar_fecha_cierre_contable_asientos_uoma(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechacierre.getFecha().getTime()));
			stmt.setString(2, username);
			stmt.execute();
		} catch (Exception e) {
			_log.error("Error al eliminar_fecha_cierre_contable_asientos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

	}

	public Date getFechaUltimoPeriodoContable(int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;

		Date fecha = null;
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call trae_fecha_ultimo_per_contable_amtima()}";
			} else if (entidad == WebKeysGlobal.OSPIM) {
				sql = "{call trae_fecha_ultimo_per_contable()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.trae_fecha_ultimo_per_contable_uoma()}";
			}

			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fecha = rs.getDate("fecha_cierre");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar la ultima fecha del periodo contable",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		if (fecha == null) {
			fecha = DateUtils.getDesdeInfinito().getTime();
		}
		return fecha;
	}

	public Date getFechaCierreAsientos(int entidad) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;

		Date fecha = null;

		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = null;
			if (entidad == WebKeysGlobal.AMTIMA) {
				sql = "{call trae_fecha_cierre_asientos_amtima()}";
			} else if (entidad == WebKeysGlobal.UOMA) {
				sql = "{call uoma.trae_fecha_cierre_asientos_uoma()}";
			}  else {
				sql = "{call trae_fecha_cierre_asientos()}";
			}

			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fecha = rs.getDate("fecha_cierre");
			}
		} catch (SQLException e) {
			_log.error("Error al buscar el cierre de asientos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		if (fecha == null) {
			return DateUtils.getDesdeInfinito().getTime();
		}
		return fecha;
	}
	
	
	public PlanCuentasSSS getEquivalenciaPlanCuentaSSS(String cta, int entidad,String tipo)
			throws SystemException {
//		_log.debug("Buscando Equivalencia Cuenta " + cta);
		Connection con = null;
		CallableStatement stmt = null;
		PlanCuentasSSS ret = new PlanCuentasSSS();
		
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call equivalencia_cuentas_sss(?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
//				sql = "{call subdiario_ingresos_amtima(?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
//				sql = "{call uoma.subdiario_ingresos_uoma(?, ?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,cta);
			
			if (tipo != null && tipo.length() != 0) {
				stmt.setString(2, tipo);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ret=PlanCuentasSSS.getMapping(rs);
			}
		} catch (Exception e) {
			_log.error("Error al buscar equivalencia cuenta SSS " + cta, e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<PlanCuentas> getCuentasAsociadasSSS(String cta, int entidad,String tipo)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List <PlanCuentas> ret = new ArrayList<PlanCuentas>();
		
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call trae_equivalencia_cuentas_sss_ospim(?,?)}";
			if (entidad == WebKeysGlobal.AMTIMA) {
//				sql = "{call subdiario_ingresos_amtima(?, ?, ?, ?, ?, ?, ?)}";
			} else if (entidad == WebKeysGlobal.UOMA) {
//				sql = "{call uoma.subdiario_ingresos_uoma(?, ?, ?, ?, ?, ?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1,cta);
			
			if (tipo != null && tipo.length() != 0) {
				stmt.setString(2, tipo);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				PlanCuentas pc = new PlanCuentas();
				pc.setCuenta(rs.getString("descripcion"));
				pc.setNumero(rs.getString("numero"));
				ret.add(pc);
			}
		} catch (Exception e) {
			_log.error("Error al buscar equivalencia cuenta SSS ospimp" + cta, e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public long addCuentaSSS(PlanCuentasSSS cuenta, int entidad,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call inserta_plan_cuentas_sss_ospim(?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
//				sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,cuenta.getNumero());
			stmt.setString(2, cuenta.getCuenta());
			stmt.setString(3, cuenta.getTipo());
			stmt.setString(4, cuenta.getAcumulaSobre());
			stmt.setInt(5, cuenta.getSigno());
			stmt.setString(6,screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_cuenta = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Plan Cuentas SSS", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cuenta;
	}
	
	
	
	public long addCuentaSSSAsociacion(String cuenta, int entidad,PlanCuentas d,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		long id_cuenta_asociada = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call inserta_plan_cuentas_sss_ospim_asociadas(?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call cajachica.inserta_caja_chica_uoma_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
//				sql = "{call cajachica.inserta_caja_chica_amtima_usuario(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuenta);
			stmt.setString(2, d.getNumero());
//			stmt.setString(3,screenName);

			
			ResultSet rs = stmt.executeQuery();
			
//			while (rs.next()) {
//				id_cuenta_asociada = rs.getString(1);
//			}
		} catch (SQLException e) {
			_log.error("Error al insertar Cuenta SSS Asociada", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_cuenta_asociada;

	}
	
	public long updateCuentaSSS(PlanCuentasSSS cuenta, int entidad,String screenName,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call update_plan_cuentas_sss_ospim(?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
//				sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1,cuenta.getNumero());
			stmt.setString(2, cuenta.getCuenta());
			stmt.setString(3, cuenta.getTipo());
			stmt.setString(4, cuenta.getAcumulaSobre());
			stmt.setInt(5, cuenta.getSigno());
			stmt.setString(6,screenName);
			stmt.setInt(7,cuenta.getId());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_cuenta = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al modificar Plan Cuentas SSS", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cuenta;
	}
	
	public long deleteCuentaSSSAsociacion(String cuenta, int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta_asociada = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call delete_plan_cuentas_sss_ospim_asociadas(?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call cajachica.inserta_caja_chica_uoma_usuario(?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
//				sql = "{call cajachica.inserta_caja_chica_amtima_usuario(?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuenta);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				id_cuenta_asociada = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar Cuenta SSS Asociada", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);	
			}else{
				ConnectionHelper.cerrar(stmt);
			}
			
		}
		return id_cuenta_asociada;

	}

	
	public long deleteCuentaSSS(PlanCuentasSSS cuenta, int entidad,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta = 0;
		try {
			
			String sql ="";
			if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call delete_plan_cuentas_sss_ospim(?,?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
//				sql = "{call cajachica.inserta_caja_chica_uoma(?,?,?,?,?,?,?,?,?,?)}";	
			}else if(entidad==WebKeysGlobal.AMTIMA){
//				sql = "{call cajachica.inserta_caja_chica_amtima(?,?,?,?,?,?,?)}";	
			}
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,cuenta.getId());
			stmt.setString(2,cuenta.getNumero());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_cuenta = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al modificar Plan Cuentas SSS", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cuenta;
	}
	
	public static Map<Integer,BigDecimal> getCoeficientesAjusteInflacion(Integer entidad,Integer periodoDde ,Integer periodoHta){
		Connection con = null;
		CallableStatement stmt = null;
		Map<Integer,BigDecimal>map = new HashMap<Integer,BigDecimal>();
		try {
			String sql = null;
			sql = "{call trae_coeficientes_ajuste_por_inflacion(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, entidad);
			stmt.setInt(2, periodoDde);
			stmt.setInt(3, periodoHta);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				map.put(rs.getInt("periodo"), rs.getBigDecimal("coeficiente"));
			}
		} catch (SQLException e) {
			_log.error("Error al traer coeficientes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return map;
	}
	
	
	public long updateCoeficienteAjusteInflacion(CoeficienteAjusteInflacion c,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta = 0;
		try {
			
			String sql ="{call coeficiente_ajuste_inflacion_update(?,?,?)}";
		
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,c.getEntidad());
			stmt.setInt(2, c.getPeriodo());
			stmt.setBigDecimal(3,c.getCoeficiente());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_cuenta = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al grabar coeficiente ajuste inflacion", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cuenta;
	}
	
	public long deleteCoeficienteAjusteInflacion(CoeficienteAjusteInflacion c,Connection connectionParameter) throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		
		if (connectionParameter == null) {
			con = ConnectionHelper.getConnection();			
		} else {
			con = connectionParameter;
		}
		
		Integer id_cuenta = 0;
		try {
			
			String sql ="{call coeficiente_ajuste_inflacion_delete(?,?)}";
		
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1,c.getEntidad());
			stmt.setInt(2, c.getPeriodo());
			ResultSet rs = stmt.executeQuery();
			
		} catch (SQLException e) {
			_log.error("Error al borrar coeficiente ajuste inflacion", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return id_cuenta;
	}
	
	public List<FichaBoletaPortal> devengadoBoleta(
			Date fechaIni, Date fechaFin, int entidad) throws SystemException {
		_log.debug("Buscando devengado Boletas Portal Empleadores");
		Connection con = null;
		CallableStatement stmt = null;
		List<FichaBoletaPortal> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;

			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call genera_asiento_boletas_empleadores_por_periodo_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.genera_asiento_boletas_empleadores_por_periodo(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<FichaBoletaPortal>();
			while (rs.next()) {
				FichaBoletaPortal est = new FichaBoletaPortal();
				est.setPeriodo_cod_barras(DateUtils.getLastDateOfMonth(rs.getDate("periodo"),true));
				est.setTipoBoleta(rs.getInt("tipo_boleta_nro"));
				est.setCapital(rs.getBigDecimal("capital"));
				est.setInteres(rs.getBigDecimal("interes"));
				est.setAjusteCapital(rs.getBigDecimal("ajustes"));
				PlanCuentas cuenta = new PlanCuentas();
				cuenta.setId(rs.getInt("id_plan_cuenta"));
				cuenta.setCuenta(rs.getString("plan_cuenta_descripcion"));
				cuenta.setNumero(rs.getString("plan_cuenta_numero"));
			    est.setCuenta(cuenta);
			    
			    PlanCuentas cuentaDevengado = new PlanCuentas();
				cuentaDevengado.setId(rs.getInt("id_plan_cuenta_devengado"));
				cuentaDevengado.setCuenta(rs.getString("plan_cuenta_descripcion_devengado"));
				cuentaDevengado.setNumero(rs.getString("plan_cuenta_numero_devengado"));
			    est.setCuentaDevengado(cuentaDevengado);
				ret.add(est);
				
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar Devengado Boletas Portal Empleadores", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public List<Comprobante> devengadoComprobantes(
			Date fechaIni, Date fechaFin, int entidad) throws SystemException {
		_log.debug("Buscando devengado Comprobantes");
		Connection con = null;
		CallableStatement stmt = null;
		List<Comprobante> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;

			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call buscar_concepto_comprobante_por_fecha_asiento_automatico_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.buscar_concepto_comprobante_por_fecha_asiento_automatico_uoma(?, ?)}";
			}

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));

			ResultSet rs = stmt.executeQuery();
			
			ret = new ArrayList<Comprobante>();
			
			while (rs.next()) {
				ComprobanteConcepto ccc = ComprobanteConcepto.getMapping(rs,
						"CCC__");
				ccc.setConceptoComprobante(Concepto.getMapping(rs, "CC__"));
				ccc.getConceptoComprobante().getPlanCuentas()
						.setId(rs.getInt("cc__cuenta_id"));
				PlanCuentas planCuentasPasivo = new PlanCuentas(
						rs.getString("cc_numero_pasivo"),
						rs.getString("cc_cuenta_pasivo"));
				planCuentasPasivo.setId(rs.getInt("cc_cuenta_pasivo_id"));
				ccc.getConceptoComprobante().setPlanCuentasPasivo(
						planCuentasPasivo);
				
				ccc.setImporte(rs.getBigDecimal("ccc__importe"));
				
				Comprobante c = new Comprobante(rs.getInt("C__id_punto_venta"),
						rs.getString("C__compro_tipo"),
						rs.getString("C__compro_nro"),
						rs.getString("C__compro_letra"),
						rs.getInt("C__compro_sucu"), rs.getString("C__cuit"));
				c.setNroAnticipo(rs.getInt("nro_cuota"));
				c.setConceptos(new ArrayList<ComprobanteConcepto>());
				
				c.getConceptos().add(ccc);
//Ver si el comprobante es ABA poner fecha baja para invertir columnnas de asiento
				
				ret.add(c);
				
			}

			
			
		} catch (Exception e) {
			_log.error("Error al buscar Devengado Comprobantes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
	
	
	public List<ConceptoSueldos> equivalenciasSueldos(String entidad,Integer sector,Integer codigo) throws SystemException {
		_log.debug("Equivalencias Sueldos - cuentas contables");
		Connection con = null;
		CallableStatement stmt = null;
		List<ConceptoSueldos> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			sql = "{call contabilidad.equivalencias_sueldos(?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			if (entidad != null && entidad.length() != 0) {
				stmt.setString(1, entidad);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			
			if (sector != null && sector != 0) {
				stmt.setInt(2,sector);
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			
			
			if (codigo != null && codigo != 0) {
				stmt.setInt(3,codigo);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			
			ResultSet rs = stmt.executeQuery();
			ret = new ArrayList<ConceptoSueldos>();
			while (rs.next()) {
				ConceptoSueldos est = new ConceptoSueldos();
				
				est.setId(rs.getInt("id"));
				est.setSectorLiquidado(rs.getInt("sector"));
				est.setCodigo(rs.getInt("codigo"));
				
				PlanCuentas cuenta = new PlanCuentas();
				cuenta.setId(rs.getInt("cuentacontable_id"));
				cuenta.setCuenta(rs.getString("cuentacontable_descripcion"));
				cuenta.setNumero(rs.getString("cuentacontable_nro"));
			    est.setCuentaContable(cuenta);
			    
			    CentroCosto ccosto=new CentroCosto();
			    ccosto.setId(rs.getInt("centrocosto_id"));
			    est.setCentroCosto(ccosto);
			    
			    est.setDebeHaber(rs.getString("debeHaber"));
			    est.setEntidad(rs.getString("entidad"));
				ret.add(est);
				
				
			}
		} catch (Exception e) {
			_log.error("Error al buscar Equivalencias - Cuentas Sueldos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}


	public Integer updateEquivalenciasSueldos(ConceptoSueldos concepto) throws SystemException {
		_log.debug("Update Equivalencias Sueldos - cuentas contables");
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret = 0;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			sql = "{call contabilidad.equivalencias_sueldos_update(?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			if(concepto.getId()!=null) {
				stmt.setInt(1,concepto.getId());
			}else {
				stmt.setNull(1, Types.INTEGER);
			}
			
			if (concepto.getEntidad() != null && concepto.getEntidad().length() != 0) {
				stmt.setString(2, concepto.getEntidad());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if (concepto.getSectorLiquidado() != null && concepto.getSectorLiquidado() != 0) {
				stmt.setInt(3,concepto.getSectorLiquidado());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			
			
			if (concepto.getCodigo() != null && concepto.getCodigo() != 0) {
				stmt.setInt(4,concepto.getCodigo());
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			
			if (concepto.getCuentaContable().getId()  != 0) {
				stmt.setInt(5,concepto.getCuentaContable().getId());
			} else {
				stmt.setNull(5, Types.INTEGER);
			}
			
			if (concepto.getCentroCosto()!=null && concepto.getCentroCosto().getId()  != 0) {
				stmt.setInt(6,concepto.getCentroCosto().getId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			
			if (concepto.getDebeHaber() != null) {
				stmt.setString(7, concepto.getDebeHaber());
			} else {
				stmt.setNull(7, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = rs.getInt(1);
			}
			
		} catch (Exception e) {
			_log.error("Error al Update Equivalencias - Cuentas Sueldos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}

	public Integer deleteEquivalenciasSueldos(Integer concepto) throws SystemException {
		_log.debug("Delete Equivalencias Sueldos - cuentas contables");
		Connection con = null;
		CallableStatement stmt = null;
		List<ConceptoSueldos> ret = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = null;
			sql = "{call contabilidad.equivalencias_sueldos_delete(?)}";
			
			stmt = con.prepareCall(sql.toString());
				stmt.setInt(1,concepto);
			
			ResultSet rs = stmt.executeQuery();
			
		} catch (Exception e) {
			_log.error("Error al delete Equivalencias - Cuentas Sueldos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}
	
}
