package ar.com.ospim.afip.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.ospim.afip.beans.ArchivoSubidoAfip;
import ar.com.ospim.afip.beans.ArchivoSubidoBco;
import ar.com.ospim.afip.beans.ReporteAporteContribucionesEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaCab;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaConsolidado;
import ar.com.ospim.afip.beans.ReporteDeudaEmpresaDet;
import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.TipoMovExtractoBancario;
import ar.com.ospim.tesoreria.beans.InteresAfip;
import ar.com.ospim.tesoreria.reportes.ReporteAcreditacionesAFIPExcel.ResumenExtractoBancario;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class AfipServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(AfipServiceImpl.class);

	private static AfipServiceImpl instance = null;

	public static AfipServiceImpl getInstance() {
		if (null == instance) {
			instance = new AfipServiceImpl();
		}
		return instance;
	}
	
	public BigDecimal getRetencionGanancias(String cuit, BigDecimal importe, Date periodo, int entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;		
		BigDecimal importeRetencion=BigDecimal.ZERO;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  calcula_retencion_ganancias(?,?,?)}";
			if(entidad==WebKeysGlobal.UOMA){
				sql = "{call  uoma.calcula_retencion_ganancias_uoma(?,?,?)}";
			}else if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call  calcula_retencion_ganancias_amtima(?,?,?)}";
			}
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setBigDecimal(2, importe);
			stmt.setDate(3, new java.sql.Date(periodo.getTime()));
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				importeRetencion=rs.getBigDecimal(1);				
			}
		} catch (Exception e) {
			logger.error("Error al calcular retenciones de ganancias", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return importeRetencion;
	}

	public List<ArchivoSubidoAfip> getArchivosSubidosAfip()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoSubidoAfip> list = null;

		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  trae_ultimos_archivos_afip ()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoSubidoAfip>();
			while (rs.next()) {
				ArchivoSubidoAfip archivo = ArchivoSubidoAfip.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar archivo AFIP", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	public List<ArchivoSubidoBco> getArchivosSubidosBcoAMTIMA(Date fechaArchivo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoSubidoBco> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_archivos_bco_amtima(?)}";
			stmt = con.prepareCall(sql.toString());
			if (null != fechaArchivo) {
				stmt.setDate(1, new java.sql.Date(fechaArchivo.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoSubidoBco>();
			while (rs.next()) {
				ArchivoSubidoBco archivo = ArchivoSubidoBco.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar ultimo archivo BCO en AMTIMA", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	public List<ArchivoSubidoBco> getArchivosSubidosBcoUOMA(Date fechaArchivo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoSubidoBco> list = null;
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  trae_ultimos_archivos_bco_uoma(?)}";
			stmt = con.prepareCall(sql.toString());
			if (null != fechaArchivo) {
				stmt.setDate(1, new java.sql.Date(fechaArchivo.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoSubidoBco>();
			while (rs.next()) {
				ArchivoSubidoBco archivo = ArchivoSubidoBco.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar ultimo archivo BCO en UOMA", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	public List<ReporteDeudaEmpresa> getReporteDeudaEmpresaPeriodo(Date desde,
			Date hasta, boolean sin_deuda, int ramo_desde, int ramo_hasta)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaEmpresa> list = null;
		ReporteDeudaEmpresa deuda = null;

		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_deuda_empresas_periodo (?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desde.getTime()));
			stmt.setDate(2, new java.sql.Date(hasta.getTime()));
			stmt.setBoolean(3, sin_deuda);
			if (ramo_desde > 0) {
				stmt.setInt(4, ramo_desde);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			if (ramo_desde > 0) {
				stmt.setInt(5, ramo_hasta);
			} else {
				stmt.setNull(5, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaEmpresa>();
			while (rs.next()) {

				deuda = ReporteDeudaEmpresa.getMapping2(rs);

				list.add(deuda);
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda empresa", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public ReporteDeudaEmpresaCab getReporteDeudaEmpresaPeriodoCabecera(
			int idRepoAgendado) throws SystemException {

		Connection con = null;
		PreparedStatement stmt = null;
		ReporteDeudaEmpresaCab cab = null;

		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "select * from informes.reporte_deuda_empresas_periodo_cab where id = ?";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idRepoAgendado);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				cab = ReporteDeudaEmpresaCab.getMapping(rs);
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda empresa cabecera", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cab;
	}

	public List<ReporteDeudaEmpresaDet> getReporteDeudaEmpresaPeriodoDetalle(
			int idRepoAgendado) throws SystemException {
		Connection con = null;
		PreparedStatement stmt = null;
		List<ReporteDeudaEmpresaDet> list = null;
		ReporteDeudaEmpresaDet deuda = null;

		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "select * from informes.reporte_deuda_empresas_periodo_detalle(?)";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idRepoAgendado);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaEmpresaDet>();

			while (rs.next()) {

				deuda = ReporteDeudaEmpresaDet.getMapping3(rs);
				list.add(deuda);
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda empresa detalle", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<ReporteDeudaEmpresaConsolidado> getReporteDeudaEmpresaPeriodoConsolidado(
			int idRepoAgendado) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaEmpresaConsolidado> list = null;
		ReporteDeudaEmpresaConsolidado cons = null;

		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call informes.reporte_deuda_empresas_periodo_consolidado(?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idRepoAgendado);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaEmpresaConsolidado>();

			while (rs.next()) {

				cons = ReporteDeudaEmpresaConsolidado.getMapping(rs);
				list.add(cons);
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda empresa consolidado", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<ReporteDeudaEmpresa> getDeudaEmpresa(String cuit, Date desde,
			Date hasta) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaEmpresa> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  reporte_deuda_empresa_detalle (?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaEmpresa>();
			while (rs.next()) {
				ReporteDeudaEmpresa deuda = ReporteDeudaEmpresa.getMapping(rs);
				deuda.setCuit(cuit);
				list.add(deuda);
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda empresa", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	public List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresa(
			String cuit, String cuil, Date desde, Date hasta, Date fechaAcreDesde, Date fechaAcreHasta)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAporteContribucionesEmpresa> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_aportes_contrib_empresa_periodo_s (?,?,?,?,?,?)}";
			int cont=1;
			stmt = con.prepareCall(sql.toString());
			stmt.setString(cont++, cuit);
			stmt.setDate(cont++, new java.sql.Date(desde.getTime()));
			stmt.setDate(cont++, new java.sql.Date(hasta.getTime()));
			
			if(null!=fechaAcreDesde){
				stmt.setDate(cont++, new java.sql.Date(fechaAcreDesde.getTime()));
			}else{
				stmt.setNull(cont++, Types.DATE);
			}
			
			if(null!=fechaAcreHasta){
				stmt.setDate(cont++, new java.sql.Date(fechaAcreHasta.getTime()));
			}else{
				stmt.setNull(cont++, Types.DATE);
			}
			
			
			if (StringUtils.checkNotEmpty(cuil)) {
				stmt.setString(cont++, cuil);
			} else {
				stmt.setNull(cont++, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteAporteContribucionesEmpresa>();
			while (rs.next()) {
				list.add(ReporteAporteContribucionesEmpresa.getMapping(rs,
						false));
			}
		} catch (Exception e) {
			logger.error(
					"Error al buscar reporte aportes contribucion de empresa ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de obtencionreporte aportes contribucion de empresa "
				+ cuit);
		return list;
	}

	public List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresas(
			Date desde, Date hasta, int ramo, int ramo_hasta, String cuit) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAporteContribucionesEmpresa> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call reporte_aportes_contrib_empresa_periodo_ramo (?, ?, ?, ?, ?)}";

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desde.getTime()));
			if(null==hasta){
				stmt.setDate(2, new java.sql.Date(desde.getTime()));	
			}else{
				stmt.setDate(2, new java.sql.Date(hasta.getTime()));
			}
			
			stmt.setInt(3, ramo);
			stmt.setInt(4, ramo_hasta);
			stmt.setString(5, cuit);

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteAporteContribucionesEmpresa>();
			while (rs.next()) {
				list.add(ReporteAporteContribucionesEmpresa.getMapping(rs,
						false));
			}
		} catch (Exception e) {
			logger.error(
					"Error al buscar reporte aportes contribucion de empresa ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de obtencionreporte aportes contribucion de empresa Ramo"
				+ ramo);
		return list;
	}

	public List<ReporteAporteContribucionesEmpresa> getReporteAportesMonotributistas(
			Date desde, Date hasta, String cuil) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAporteContribucionesEmpresa> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call reporte_aportes_monotributistas(?,?,?)}";

			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(desde.getTime()));
			stmt.setDate(2, new java.sql.Date(hasta.getTime()));
			if (cuil == null || cuil.isEmpty()) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, cuil);
			}

			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteAporteContribucionesEmpresa>();
			while (rs.next()) {
				list.add(ReporteAporteContribucionesEmpresa
						.getMappingMonotrib(rs));
			}
		} catch (Exception e) {
			logger.error(
					"Error al buscar reporte aportes contribucion de monotri ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de obtencionreporte aportes contribucion de monotr");
		return list;
	}

	public List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresaActaConv(
			String cuit, String cuil, Date desde, Date hasta)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteAporteContribucionesEmpresa> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_aportes_contrib_empresa_periodo_con_ac_conv (?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			if (null != cuil) {
				stmt.setString(4, cuil);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteAporteContribucionesEmpresa>();
			while (rs.next()) {
				list.add(ReporteAporteContribucionesEmpresa
						.getMapping(rs, true));
			}
		} catch (Exception e) {
			logger.error(
					"Error al buscar reporte aportes contribucion de empresa ",
					e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de obtencionreporte aportes contribucion de empresa "
				+ cuit);
		return list;
	}

	public List<ReporteAporteContribucionesEmpresa> getReporteAportesContribucionEmpresa(
			String cuit, String cuil, Date desde, Date hasta, Connection con,
			CallableStatement stmt) throws SystemException {

		List<ReporteAporteContribucionesEmpresa> list = null;
		try {
			String sql = "{call  reporte_aportes_contrib_empresa_periodo (?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			if (null != cuil) {
				stmt.setString(4, cuil);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteAporteContribucionesEmpresa>();
			while (rs.next()) {
				list.add(ReporteAporteContribucionesEmpresa.getMapping(rs,
						false));
			}
		} catch (Exception e) {
			logger.error(
					"Error al buscar reporte aportes contribucion de empresa ",
					e);
			throw new SystemException(e);
		}

		return list;
	}

	public List<ReporteDeudaNominaEmpresa> getDeudaNominaEmpresa(String cuit,
			Date desde, Date hasta) throws SystemException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		logger.debug("Obteniendo deuda de empresa " + cuit);
		logger.debug("Obteniendo deuda de empresa desde" + desde!=null?sdf.format(desde):"");
		logger.debug("Obteniendo deuda de empresa hasta" + hasta!=null?sdf.format(hasta):"");
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaNominaEmpresa> list = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			String sql = "{call  reporte_deuda_nomina_empresa (?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaNominaEmpresa>();
			while (rs.next()) {
				ReporteDeudaNominaEmpresa deuda = ReporteDeudaNominaEmpresa
						.getMapping(rs);
				int indexOf = list.indexOf(deuda);
				if (indexOf == -1) {
					list.add(deuda);
				} else {
					list.get(indexOf).getPagos().addAll(deuda.getPagos());
				}
			}
		} catch (Exception e) {
			logger.error("Error al buscar deuda nomina empresa", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de obtencion de deuda nomina empresa " + cuit);
		return list;
	}

	public List<InteresAfip> getIntereses() throws SystemException {
		logger.debug("Obteniendo intereses afip");
		Connection con = null;
		CallableStatement stmt = null;
		List<InteresAfip> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call buscar_intereses_afip ()}";
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<InteresAfip>();
			while (rs.next()) {
				InteresAfip deuda = InteresAfip.getMapping(rs);
				list.add(deuda);
			}
		} catch (Exception e) {
			logger.error("Error al buscar intereses afip", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		logger.debug("Saliendo de Obteniendo intereses afip");
		return list;
	}

	public Map<Date, List<ResumenExtractoBancario>> getResumenOSAportes(
			Date fechaIni, Date fechaFin) throws SQLException {
		return getResumenExtracto(fechaIni, fechaFin,
				"buscar_resumen_os_aportes_detalle");
	}

	private Map<Date, List<ResumenExtractoBancario>> getResumenExtracto(
			Date fechaIni, Date fechaFin, String sp) throws SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		Map<Date, List<ResumenExtractoBancario>> resultados = null;
		try {
			String sql = "{call " + sp + "(?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaIni.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			resultados = new HashMap<Date, List<ResumenExtractoBancario>>();
			while (rs.next()) {

				ResumenExtractoBancario resumen = new ResumenExtractoBancario();
				resumen.setFecha(rs.getDate("fecha"));
				resumen.setTotal(rs.getBigDecimal("total"));
				TipoMovExtractoBancario tipo = new TipoMovExtractoBancario();
				tipo.setCodigoMovimiento(rs.getInt("orden"));
				tipo.setDescripcionMovimiento(rs.getString("descripcion"));
				resumen.setTipo(tipo);
				if (resultados.get(resumen.getFecha()) == null) {
					List<ResumenExtractoBancario> lista = new ArrayList<ResumenExtractoBancario>();
					lista.add(resumen);
					resultados.put(resumen.getFecha(), lista);
				} else {
					resultados.get(resumen.getFecha()).add(resumen);
				}
			}
		} catch (SQLException e) {
			logger.debug("Error al buscar resumen os aportes detalle", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return resultados;
	}

	public Map<Date, List<ResumenExtractoBancario>> getResumenSubsidioDesempleo(
			Date fechaIni, Date fechaFin) throws SQLException {
		return getResumenExtracto(fechaIni, fechaFin,
				"buscar_resumen_subsidio_desempleo");
	}
}
