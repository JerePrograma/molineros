package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionDebitosEntryException;
import ar.com.ospim.liquidaciones.beans.DetalleCuota;
import ar.com.ospim.liquidaciones.beans.Liquidacion;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTercero;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleLiq;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReint;
import ar.com.ospim.liquidaciones.beans.LiquidacionDebitoTerceroDetalleReintOrtod;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionOdoOrtopediaOrtodoncia;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="BusquedaLiquidacionServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class LiquidacionDebitoTerceroServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(LiquidacionDebitoTerceroServiceImpl.class);

	public List<LiquidacionDebitoTercero> getLiquidacionesDebitosTerceros(
			Date periodoDesde, Date periodoHasta) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<LiquidacionDebitoTercero> listaLiquidaciones = null;
		listaLiquidaciones = new ArrayList<LiquidacionDebitoTercero>();
		try {
			String sql = "{call buscar_liquidaciones_debitos(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, periodoDesde == null ? null : new java.sql.Date(
					periodoDesde.getTime()));
			stmt.setDate(2, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LiquidacionDebitoTercero liquidacion = LiquidacionDebitoTercero
						.getMapping(rs, "ldt_");
				listaLiquidaciones.add(liquidacion);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidaciones;
	}

	public LiquidacionDebitoTercero getLiquidacionDebitosEntry(
			int id_liquidacion) throws SystemException,
			NoSuchLiquidacionDebitosEntryException {
		Connection con = null;
		CallableStatement stmt = null;
		LiquidacionDebitoTercero liquidacion = null;
		try {
			String sql = "{call busca_liquidacion_debitos_header_por_id(?)}";
			_log.debug("creando conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_liquidacion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				liquidacion = LiquidacionDebitoTercero.getMapping(rs, "ldt_");
			}
		} catch (Exception e) {
			_log.debug(e.getMessage());
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return liquidacion;
	}

	public int save(LiquidacionDebitoTercero ldt, String screenName)
			throws SystemException, SQLException,
			DuplicateLiquidacionIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call inserta_liquidacion_debitos_terceros (?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, ldt.getPeriodoHasta() == null ? null
					: new java.sql.Date(ldt.getPeriodoHasta().getTime()));
			stmt.setString(2, ldt.getObservaciones());
			stmt.setString(3, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar liquidacion de débitos a tercero", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateLiquidacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar liquidacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return 0;
	}

	public void update(LiquidacionDebitoTercero ldt, String screenName)
			throws SystemException, SQLException,
			DuplicateLiquidacionIdException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call actualiza_liquidacion_debitos_terceros (?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, ldt.getPeriodoHasta() == null ? null
					: new java.sql.Date(ldt.getPeriodoHasta().getTime()));
			stmt.setString(2, ldt.getObservaciones());
			stmt.setString(3, screenName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			_log.error("Error al actualizar liquidacion debitos a terceros", e);
			if (e.getSQLState().equalsIgnoreCase(
					WebKeysGlobal.SQL_STATE_DUPLICATE_KEY)) {
				throw new DuplicateLiquidacionIdException(e);
			} else {
				throw new SystemException(e);
			}
		} catch (Exception e) {
			_log.error("Error al insertar liquidacion deb terc.", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<LiquidacionDebitoTerceroDetalleReint> getDetalleReintegrosPagosPeriodo(
			Date periodoHasta) {
		ArrayList<LiquidacionDebitoTerceroDetalleReint> listaLiquidacionesDetalleReintegro = null;
		listaLiquidacionesDetalleReintegro = new ArrayList<LiquidacionDebitoTerceroDetalleReint>();
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call buscar_detalle_reintegros_pagos_periodo(?)}";

			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LiquidacionDebitoTerceroDetalleReint detalle = new LiquidacionDebitoTerceroDetalleReint();
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional.getMappingSeccionalParaReintegros(rs, "s_");				
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegro.setSeccional(seccional);
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "op_");
				ReintegroPrestacionNormal rp = ReintegroPrestacionNormal
						.getMapping(rs, "rp_");
				rp.setReintegro(reintegro);
				reintegro.setAfiliado(afiliado);
				afiliado.setSeccional(seccional);
				detalle.setReintegroPrestacion(rp);
				detalle.setOp(op);
				listaLiquidacionesDetalleReintegro.add(detalle);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidacionesDetalleReintegro;
	}

	public List<LiquidacionDebitoTerceroDetalleReintOrtod> getDetalleReintegrosOrtPagosPeriodo(
			Date periodoHasta) {
		ArrayList<LiquidacionDebitoTerceroDetalleReintOrtod> listaLiquidacionesDetalleReintegroOrtods = null;
		listaLiquidacionesDetalleReintegroOrtods = new ArrayList<LiquidacionDebitoTerceroDetalleReintOrtod>();
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call buscar_detalle_reintegros_ortod_pagos_periodo(?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LiquidacionDebitoTerceroDetalleReintOrtod detalle = new LiquidacionDebitoTerceroDetalleReintOrtod();
				Afiliado afiliado = Afiliado.getMapping(rs, "a_");
				Seccional seccional = Seccional.getMappingSeccionalParaReintegros(rs, "s_");				
				Reintegro reintegro = Reintegro.getMapping(rs, "r_");
				reintegro.setSeccional(seccional);
				DetalleCuota detalleCuota = DetalleCuota.getMapping(rs, "dc_");
				List<DetalleCuota> cuotas = new ArrayList<DetalleCuota>();
				cuotas.add(detalleCuota);
				reintegro.setDetalleCuota(cuotas);							
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "op_");
				ReintegroPrestacionOdoOrtopediaOrtodoncia rp = ReintegroPrestacionOdoOrtopediaOrtodoncia
						.getMapping(rs, "rp_");
				rp.setReintegro(reintegro);
				reintegro.setAfiliado(afiliado);
				afiliado.setSeccional(seccional);
				detalle.setReintegroPrestacion(rp);
				detalle.setOp(op);
				listaLiquidacionesDetalleReintegroOrtods.add(detalle);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidacionesDetalleReintegroOrtods;
	}

	public List<LiquidacionDebitoTerceroDetalleLiq> getDetalleLiquidacionesPagasPeriodo(
			Date periodoHasta) {
		ArrayList<LiquidacionDebitoTerceroDetalleLiq> listaLiquidacionesDetalleReintegroLiqs = null;
		listaLiquidacionesDetalleReintegroLiqs = new ArrayList<LiquidacionDebitoTerceroDetalleLiq>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_detalle_liquidaciones_pagas_periodo(?)}";

			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setDate(1, periodoHasta == null ? null : new java.sql.Date(
					periodoHasta.getTime()));

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LiquidacionDebitoTerceroDetalleLiq detalle = new LiquidacionDebitoTerceroDetalleLiq();
				Liquidacion liquidacion = Liquidacion.getMapping(rs, "l_");
				PrestadorLugarAtencion pla = PrestadorLugarAtencion.getMappingSimple(rs, "pla_");
				Prestador prestador = Prestador.getMapping(rs, "pd_");
				prestador.setId_prestador(liquidacion.getId_prestador());
				pla.setPrestador(prestador);
				liquidacion.setPrestador_lugar_atencion(pla);
				liquidacion.setId_domicilio(pla.getId_domicilio());				
				OrdenPagoOspim op = OrdenPagoOspim.getMapping(rs, "op_");				
				ComprobanteConcepto cc = ComprobanteConcepto.getMapping(rs, "cc_");								
				Comprobante c = new Comprobante(rs.getInt("c_id_punto_venta"),
						rs.getString("c_compro_tipo"), rs
								.getString("c_compro_nro"), rs
								.getString("c_compro_letra"), rs
								.getInt("c_compro_sucu"), rs
								.getString("c_cuit"));								
				detalle.setOp(op);
				detalle.setLiquidacion(liquidacion);
				detalle.setComprobante(c);
				detalle.setComprobanteConcepto(cc);
				listaLiquidacionesDetalleReintegroLiqs.add(detalle);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLiquidacionesDetalleReintegroLiqs;
	}
	
	public Date getUltimoPeriodoDebitosTerceros() throws SystemException, NumberFormatException, ParseException {
		Connection con = null;
		CallableStatement stmt = null;		
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = "01";
		String fechaMes = "01";
		String fechaAnio = "1800";
		Date fecha = null;
		
		fecha = formatoDeFecha.parse(fechaDia + "/"
				+ Integer.parseInt(fechaMes) + "/" + fechaAnio);
		
		try {
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_ultimo_per_liq_debitos_terceros()}";			

			stmt = con.prepareCall(sql.toString());			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				fecha = rs.getDate("periodo");
			}
			if (fecha == null) {
				fecha = formatoDeFecha.parse(fechaDia + "/"
						+ Integer.parseInt(fechaMes) + "/" + fechaAnio);				
			}
		} catch (SQLException e) {
			_log.error("Error al buscar el ultimo periodo de liquidacion", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return fecha;
	}
}