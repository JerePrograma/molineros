package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.farmacia.beans.ReintegroFarmaciaList;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPago.ItemOrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Pago;
import ar.com.ospim.global.beans.RetencionGanancias;
import ar.com.ospim.global.beans.RetencionIIBB;
import ar.com.ospim.global.beans.RetencionIVA;
import ar.com.ospim.liquidaciones.AnticiposNoPagadosException;
import ar.com.ospim.liquidaciones.AnticiposReUtilizadosException;
import ar.com.ospim.liquidaciones.AnticiposUtilizadosException;
import ar.com.ospim.liquidaciones.ComprobantesAnuladosException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.DuplicateNumeroComprobanteException;
import ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroList;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.reportes.action.ReporteOrdenesPagoAction.ReporteOrdenPagoOspim;
import ar.com.ospim.tesoreria.OpConChequesCanjeadosException;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

/**
 * @author Martin Moreyra
 * 
 */
public class OrdenPagoServiceUtil {

	public static int A_NOMBRE_DE_POS = 0;
	public static int DESTINO_POS = 1;
	public static int CBU_POS = 2;
	public static int EMAIL_POS = 3;
	public static int RAZON_SOC_POS = 4;

	private static Log _log = LogFactoryUtil.getLog(OrdenPagoServiceImpl.class);
	private static OrdenPagoServiceImpl instance = null;

	public static OrdenPagoServiceImpl getInstance() {
		if (null == instance) {
			instance = new OrdenPagoServiceImpl();
		}
		return instance;
	}

	public static List<OrdenPago> getOrdenesPago(BigDecimal numeroChequeInt,
			Integer numeroInt, String cuit, String sucursal, Date fechaDesde,
			Date fechaHasta, int idSeccional, int entidad) throws Exception {
		return getInstance().getOrdenesPago(numeroChequeInt, numeroInt, cuit,
				sucursal, fechaDesde, fechaHasta, idSeccional, entidad);
	}

	public static int getLoteOrdenPago() throws Exception {
		return getInstance().getLastLoteOrdenPago();
	}

	public static int setFechaFirmaLoteOrdenPago(Date fecha_firma, String user)
			throws Exception {
		return getInstance().setFechaFirmaLoteOrdenPago(fecha_firma, user);
	}

	public static OrdenPago getOrdenPago(Integer numeroInt, int entidad)
			throws Exception {
		OrdenPago ordenPago = getInstance().getOrdenPago(numeroInt, null, null,
				null, null, entidad);

		getInstance().getPagos(ordenPago, entidad);
		getInstance().getListasReintegros(ordenPago, entidad);
		if (ordenPago.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : ordenPago.getFormaPago()) {
				if (fp.getPago() instanceof Anticipo) {
					Anticipo ant = (Anticipo) fp.getPago();
					Comprobante comprobanteAnt = null;
					comprobanteAnt = ComprobanteServiceUtil
							.getComprobanteAnticipo(ant.getAnticipo(), entidad,
									numeroInt);
					ant.setAnticipo(comprobanteAnt);
				} else if (fp.getPago() instanceof RetencionGanancias) {
					ordenPago.setTieneRetencion(true);
				} else if (fp.getPago() instanceof RetencionIIBB) {
					ordenPago.setTieneRetencionIIBB(true);
				} else if (fp.getPago() instanceof RetencionIVA) {
					ordenPago.setTieneRetencionIVA(true);
				}
			}
		}

		ComprobanteServiceUtil.getComprobantesConConceptos(ordenPago, entidad);
		return ordenPago;
	}

	public static void save(OrdenPago op, User user, int entidad)
			throws Exception {
		save(op, user, null, entidad);
	}

	public static void save(OrdenPago op, User user,
			Connection connectionParameter, int entidad) throws Exception {

		int idOriginal = op.getId();
		_log.debug("obteniendo conexion");
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			int id = getInstance().save(op, user.getScreenName(), con, entidad);

			op.setId(id);

			_log.debug("Por grabar comprobantes");
			if (op.getComprobantes() != null) {
				for (Comprobante comp : op.getComprobantes()) {
					String tipo = comp.getTipoComprobante();
					if ((tipo.equals("NDF") || tipo.equals("LIQ")
							|| tipo.equals("ANT") || tipo.equals("REI") || tipo
								.equals(WebKeysGlobal.COMPROBANTE_VARIOS))
							&& comp.getAlta_fecha() == null) {
						
						if(entidad == WebKeysGlobal.UOMA) {
						  ComprobanteServiceUtil.saveExtendido(comp, user, con, entidad);
						}else {
						  ComprobanteServiceUtil.save(comp, user, con, entidad);
						}  
					} else if (comp.getImporteComprobante().compareTo(
							comp.getImporteComprobanteOriginal()) < 0) { // PAGO
																			// PARCIAL
						ComprobanteServiceUtil.save(comp, user, con, entidad);
					}
					ComprobanteServiceUtil.saveAsociacionOp(op.getId(), comp,
							user.getScreenName(), con, entidad);
				}
			}

			if (op.getReintegrosList() != null
					&& !op.getReintegrosList().isEmpty()) {
				getInstance().saveOPReintegrosList(op, user.getScreenName(),
						con, entidad);
			}

			for (OrdenPago.FormaPago pago : op.getFormaPago()) {
				pago.getPago().savePago(op, user.getScreenName(), con, entidad);
			}

			if (op.getItems() != null) {
				for (ItemOrdenPago iop : op.getItems()) {
					OrdenPagoServiceUtil.saveItem(iop, op, user, con);
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SystemException e) {
			_log.error("Error:", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			op.setId(idOriginal);
			throw e;
		} catch (DuplicateNumeroComprobanteException e) {
			_log.error("Error:", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			op.setId(idOriginal);
			throw e;
		} catch (Exception e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			_log.error("Error:", e);
			op.setId(idOriginal);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}

	public static void saveFromAlta(OrdenPago opo, User user, int entidad)
			throws SystemException, DuplicateNumeroChequeException,
			DuplicateNumeroComprobanteException {
		_log.debug("obteniendo conexion");
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			save(opo, user, con, entidad);
			con.commit();
		} catch (SystemException e) {
			_log.error("Error:", e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (DuplicateNumeroComprobanteException e) {
			_log.error("Error:", e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (Exception e) {
			_log.error("Error:", e);
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	public static void save(OrdenPagoOspim opo, User user)
			throws SystemException, DuplicateNumeroChequeException,
			DuplicateNumeroComprobanteException {
		_log.debug("obteniendo conexion");
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			save(opo, user, con);
			con.commit();
		} catch (SystemException e) {
			_log.error("Error:", e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (DuplicateNumeroComprobanteException e) {
			_log.error("Error:", e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (Exception e) {
			_log.error("Error:", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	public static void save(OrdenPagoOspim op, User user, Connection con)
			throws Exception {

		_log.debug("Por grabar OP");
		int id = getInstance().save(op, user.getScreenName(), con);
		op.setId(id);

		_log.debug("Por grabar comprobantes");
		if (op.getComprobantes() != null) {
			for (Comprobante comp : op.getComprobantes()) {
				if(op.getAlta_fecha()!=null) {
					comp.setAlta_fecha(op.getAlta_fecha());
				}
				if ((comp.getTipoComprobante().equals("ANT")
						|| comp.getTipoComprobante().equals("REI") || comp
						.getTipoComprobante().equals(
								WebKeysGlobal.COMPROBANTE_VARIOS))
						&& comp.getAlta_usr() == null) {
					ComprobanteServiceUtil.save(comp, user, con,
							WebKeysGlobal.OSPIM);
				} else if (comp.getImporteComprobante().compareTo(
						comp.getImporteComprobanteOriginal()) < 0) { // PAGO
																		// PARCIAL
					ComprobanteServiceUtil.save(comp, user, con,
							WebKeysGlobal.OSPIM);
				}
				ComprobanteServiceUtil.saveAsociacionOp(op.getId(), comp,
						user.getScreenName(), con, WebKeysGlobal.OSPIM);
			}

		}
		if (op.getReintegrosList() != null && !op.getReintegrosList().isEmpty()) {
			getInstance().saveOPReintegrosList(op, user.getScreenName(), con);
		}
		if (op.getLiquidacionesList() != null) {
			getInstance()
					.saveOPLiquidacionesList(op, user.getScreenName(), con);
		}

		for (OrdenPago.FormaPago pago : op.getFormaPago()) {
			if (null != pago.getCuentaBancaria()
					&& pago.getCuentaBancaria().getId_cuenta_bcria() == 99
					&& pago.getTipo().contains("Cheque")) {
				ChequeServiceUtil.cambiarEstadoCheque((Cheque) pago.getPago(),
						new Estado(Estado.ENTREGADO_A_TERCEROS),
						user.getScreenName(), con, WebKeysGlobal.OSPIM);
			}
			pago.getPago().savePago(op, user.getScreenName(), con,WebKeysGlobal.OSPIM);
		}

	}

	public static void savePago(Pago pago, OrdenPago op, String user,
			Connection con, int entidad) throws Exception {
		getInstance().save(pago, op, con, entidad);
	}
	
	public static void savePago(RetencionIIBB pago, OrdenPago op, String user,
			Connection con, int entidad) throws Exception {
		getInstance().saveRetencion(pago, op, con, entidad);
	}
	
	public static void savePago(RetencionIVA pago, OrdenPago op, String user,
			Connection con, int entidad) throws Exception {
		getInstance().saveRetencion(pago, op, con, entidad);
	}
	
	public static void savePago(Cheque cheque, OrdenPago op, String user,
			Connection con, int entidad) throws Exception {
		if(cheque.getAlta_fecha()==null) {
			cheque.setAlta_fecha(op.getAlta_fecha());
		}
//		if (cheque.isNew()) {
		if (cheque.getAlta_usr()==null) {
			Cheque ch = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cheque, entidad);
			if (ch == null){
			    ChequeServiceUtil.save(cheque, user, con, entidad);
			}else {
				ChequeServiceUtil.updateDatos(cheque, user, con, entidad);
			}
		}
		if (null != cheque.getCuentaBancaria()
				&& cheque.getCuentaBancaria().getId_cuenta_bcria() == 99) {
			ChequeServiceUtil.cambiarEstadoCheque(cheque, new Estado(
					Estado.ENTREGADO_A_TERCEROS), user, con, entidad);
		}
		getInstance().save(cheque, op, con, entidad);
	}

	public static void borrar(Integer id, User user) throws SQLException {
		getInstance().borrar(id, user.getScreenName());
	}

	public static void update(OrdenPagoAmtima op, User user)
			throws SystemException {

	}

	public static void saveItem(ItemOrdenPago iop, OrdenPago op, User user,
			Connection connectionParameter) throws SystemException {
		getInstance().saveItem(
				iop.getFecha(),
				iop.getPeriodo(),
				op,
				iop.getNroLiquidacion(),
				iop.getCodigoPrestador(),
				iop.getPrestador(),
				iop.getNroFarmacia(),
				iop.getFarmacia(),
				(iop.getAfiliado().getId_ospim() == 0 ? null : iop
						.getAfiliado().getId_ospim()),
				(iop.getAfiliado().getId_amtima() == 0 ? null : iop
						.getAfiliado().getId_amtima()),
				(iop.getAfiliado().getId_uoma() == 0 ? null : iop.getAfiliado()
						.getId_uoma()), iop.getAfiliado().getInte(),
				iop.getAfiliado().getNombre(), iop.getNroRecetario(),
				iop.getTroquel(), iop.getMedicamento(), iop.getCantidad(),
				iop.getPvp(), iop.getTotalOspim(), iop.getTotalAmtima(),
				iop.getDebito(), iop.getDifOspim(), iop.getDifAmtima(),
				iop.getPorcentajeOSPIM(), iop.getPorcentajeAmtima(),
				iop.getPmi(), user.getScreenName(), iop.getCajaFarmacia(),
				iop.getArchivo(), connectionParameter);
	}

	public static void update(OrdenPagoOspim op, User user)
			throws SystemException {
		getInstance().udpate(op, user.getScreenName());

	}

	public static List<OrdenPagoOspim> getOrdenesPagoOspim(
			BigDecimal numeroChequeInt, Integer numeroInt, String cuit,
			String sucursal, Date fechaDesde, Date fechaHasta, int idSeccional, String cbu) {
		return getInstance().getOrdenesPagoOspim(numeroChequeInt, numeroInt,
				cuit, sucursal, fechaDesde, fechaHasta, idSeccional, cbu);
	}

	public static OrdenPagoOspim getOrdenPagoOspim(Integer numeroInt)
			throws Exception {
		OrdenPagoOspim ordenPagoOspim = getInstance().getOrdenPagoOspim(
				numeroInt, WebKeysGlobal.OSPIM);
		if (ordenPagoOspim == null) {
			return null;
		}
		getInstance().getPagos(ordenPagoOspim, WebKeysGlobal.OSPIM);
		/*
		 * if (ordenPagoOspim.getFormaPago() != null) { for (OrdenPago.FormaPago
		 * fp : ordenPagoOspim.getFormaPago()) { if (fp.getPago() instanceof
		 * Anticipo) { Anticipo ant = (Anticipo) fp.getPago(); Comprobante
		 * comprobanteAnt = ComprobanteServiceUtil
		 * .getComprobante(ant.getAnticipo(), WebKeysGlobal.OSPIM);
		 * ant.setAnticipo(comprobanteAnt); }else if (fp.getPago() instanceof
		 * RetencionGanancias) { ordenPagoOspim.setTieneRetencion(true); } } }
		 */

		if (ordenPagoOspim.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : ordenPagoOspim.getFormaPago()) {
				if (fp.getPago() instanceof Anticipo) {
					Anticipo ant = (Anticipo) fp.getPago();
					Comprobante comprobanteAnt = null;

					comprobanteAnt = ComprobanteServiceUtil
							.getComprobanteAnticipo(ant.getAnticipo(),
									WebKeysGlobal.OSPIM, numeroInt);
					ant.setAnticipo(comprobanteAnt);
				} else if (fp.getPago() instanceof RetencionGanancias) {
					ordenPagoOspim.setTieneRetencion(true);
				}
			}
		}

		ComprobanteServiceUtil.getComprobantesConConceptos(ordenPagoOspim,
				WebKeysGlobal.OSPIM);
		return ordenPagoOspim;
	}

	private static void anularOrdenPago(Integer nro, User user, Date fechaBaja,
			List<Cheque> chequesAnular, int entidad,
			Connection connectionParameter) throws SystemException,
			AnticiposUtilizadosException, FechaBajaMenorQueAltaException,
			OpConChequesCanjeadosException, NumberFormatException,
			ParseException, FechaMenorACierreContableException {

		Connection connection = null;
		if (connectionParameter == null) {
			connection = ConnectionHelper.getConnectionForTransaction();
		} else {
			connection = connectionParameter;
		}
		try {
			OrdenPago ordenPago = null;
			if (entidad == WebKeysGlobal.OSPIM) {
				ordenPago = getInstance().getOrdenPagoOspim(nro,
						WebKeysGlobal.OSPIM);
			} else {
				ordenPago = getInstance().getOrdenPago(nro, null, null, null,
						null, entidad);
			}

			if (ordenPago.getAlta_fecha().compareTo(fechaBaja) > 0) {
				throw new FechaBajaMenorQueAltaException();
			}

			boolean anticiposUtilizados = getInstance()
					.verificarAnticiposUtilizados(nro, connection, entidad);

			if (anticiposUtilizados) {
				throw new AnticiposUtilizadosException();
			}

			List<Cheque> chequesCanjeados = verificarOpConChequesCanjeados(nro,
					connection, entidad);

			if (chequesCanjeados != null && chequesCanjeados.size() > 0) {
				throw new OpConChequesCanjeadosException(chequesCanjeados);
			}

			Date fecha_cierre_periodo = ContabilidadServiceUtil
					.getFechaUltimoPeriodoContable(entidad);
			if (fechaBaja.compareTo(fecha_cierre_periodo) <= 0
					|| (ordenPago.getBaja_fecha() != null && ordenPago
							.getBaja_fecha().compareTo(fecha_cierre_periodo) <= 0)) {
				throw new FechaMenorACierreContableException();
			}

			getInstance().sacarAnulacionChequesReactivados(nro,
					user.getScreenName(), connection, entidad);

			if (chequesAnular != null) {
				for (Cheque ch : chequesAnular) {
					ChequeServiceUtil.anularcheque(ch, fechaBaja, user, connection, entidad);
				}
			}

			getInstance().anularOrdenPago(nro, fechaBaja, user.getScreenName(),
					connection, entidad);

			if (connectionParameter == null) {
				connection.commit();
			}
		} catch (AnticiposUtilizadosException e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(connection);
			}
			_log.error("Error al anular op", e);
			throw e;
		} catch (SQLException e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(connection);
			}
			_log.error("Error al anular op", e);
			throw new SystemException();
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(connection);
			}
		}
	}

	public static List<ReporteOrdenPagoReintegros> getReintegrosFromListaId(
			int id) throws SystemException, NoSuchReintegroEntryException {
		return getInstance().getReintegros(id);
	}

	public static List<ReporteOrdenPagoReintegros> getReintegrosFromListas(
			String ids) throws SystemException, NoSuchReintegroEntryException {
		return getInstance().getReintegros(ids);
	}

	public static List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFarmaciaFromListaId(
			int id) throws SystemException, NoSuchReintegroEntryException {
		return getInstance().getReintegrosFarmacia(id);
	}

	public static List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFarmaciaFromListas(
			String ids) throws SystemException, NoSuchReintegroEntryException {
		return getInstance().getReintegrosFarmacia(ids);
	}

	public static int saveReintegroListParaPago(ReintegroList reintegrosList,
			User user) throws SystemException {
		_log.debug("Insertando lista de reintegros");
		int id = 0;
		id = getInstance().saveReintegroListParaPago(reintegrosList,
				user.getScreenName());
		getInstance().saveReintegroListParaPagoDetalle(reintegrosList);
		return id;
	}

	public static int saveReintegroListParaReporte(
			ReintegroList reintegrosList, User user) throws SystemException {
		_log.debug("Insertando lista de reintegros");
		int id = 0;
		id = getInstance().saveReintegroListParaReporte(reintegrosList,
				user.getScreenName());
		getInstance().saveReintegroListParaReporteDetalle(reintegrosList);
		return id;
	}

	public static int saveReintegroFarmaciaListParaPago(
			ReintegroFarmaciaList reintegrosList, User user)
			throws SystemException {
		_log.debug("Insertando lista de reintegros");
		int id = 0;
		id = getInstance().saveReintegroFarmaciaListParaPago(reintegrosList,
				user.getScreenName());
		getInstance().saveReintegroFarmaciaListParaPagoDetalle(reintegrosList);
		return id;
	}

	public static int saveReintegroFarmaciaListParaReporte(
			ReintegroFarmaciaList reintegrosList, User user)
			throws SystemException {
		_log.debug("Insertando lista de reintegros");
		int id = 0;
		id = getInstance().saveReintegroFarmaciaListParaReporte(reintegrosList,
				user.getScreenName());
		getInstance().saveReintegroFarmaciaListParaReporteDetalle(
				reintegrosList);
		return id;
	}

	public static boolean existeOPAmtima(Date periodo, String codigoPrestador)
			throws SystemException {
		return getInstance().existeOPAmtima(periodo, codigoPrestador);
	}

	public static boolean existeOPFarmacia(Date periodo, String codigoPrestador)
			throws SystemException {
		return getInstance().existeOPFarmacia(periodo, codigoPrestador);
	}

	public static List<Reintegro> getReintegrosFromList(int nroList)
			throws SystemException {
		return getInstance().getReintegrosFromList(nroList);
	}

	public static List<ReintegroList> getReintegrosLists(Integer idSeccional,
			Date fechaIni, Date fechaFin) throws SystemException {
		return getInstance().getReintegrosLists(idSeccional, fechaIni, fechaFin);
	}
	
	public static List<ReintegroList> getReintegroAgrupadoSumadoList(int idSeccional,
			Date fechaIni, Date fechaFin, String in) throws SystemException {
		return getInstance().getReintegroAgrupadoSumadoList(idSeccional, fechaIni, fechaFin,  in);
	}
	
	
	

	public static List<ReporteOrdenPagoOspim> reporteOrdenPagoOspim(
			Date fechaInicio, Date fechaFin) throws SystemException {
		return getInstance().reporteOrdenPagoOspim(fechaInicio, fechaFin);
	}

	public static List<OrdenPago> reporteOrdenPagoOspimCompleto(
			Date fechaInicio, Date fechaFin, int id_prestador, String cuit,
			String sucur, String compro_tipo, String compro_nro,
			int compro_sucur, String compro_letra, int nro_lote)
			throws Exception {
		Connection connection = ConnectionHelper.getReportesOspimConnection();
		List<OrdenPago> ops = null;
		List<OrdenPagoOspim> opsComprobantes = null;
		List<OrdenPagoOspim> opsPagos = null;
		try {
			ops = getInstance().getOrdenesPagoOspim(fechaInicio, fechaFin,
					id_prestador, cuit, sucur, compro_tipo, compro_nro,
					compro_sucur, compro_letra, nro_lote, connection);

			opsComprobantes = ComprobanteServiceUtil.getComprobantesOP(
					fechaInicio, fechaFin, id_prestador, cuit, sucur,
					compro_tipo, compro_nro, compro_sucur, compro_letra,
					connection);

			opsPagos = getInstance().getPagosOP(fechaInicio, fechaFin,
					connection);
		} finally {
			ConnectionHelper.cerrar(connection);
		}

		for (OrdenPago op : ops) {
			int indexPago = opsPagos.indexOf(op);
			if (opsPagos != null && indexPago != -1) {
				op.setFormaPago(opsPagos.get(indexPago).getFormaPago());
			}
			// YA NO ES NECESARIO PORQUE USAMOS EL ANTICIPO (A RENDIR) COMO UN
			// COMPROBANTE MAS PERO NEGATIVO
			// if (op.getFormaPago() != null) {
			// for (FormaPago fp : op.getFormaPago()) {
			// if (fp.getPago() instanceof Anticipo) {
			// Anticipo ant = (Anticipo) fp.getPago();
			// Comprobante comprobanteAnt = ComprobanteServiceUtil
			// .getComprobante(ant.getAnticipo());
			// fp.setPago(new Anticipo(comprobanteAnt));
			// }
			// }
			// }
			int indexof = opsComprobantes.indexOf(op);
			if (opsComprobantes != null && indexof != -1) {
				op.setComprobantes(opsComprobantes.get(indexof)
						.getComprobantes());
			}
		}

		return ops;
	}

	public static List<OrdenPago> reporteOrdenPagoCompleto(Date fechaInicio,
			Date fechaFin, int id_prestador, String cuit, String sucur,
			String compro_tipo, String compro_nro, int compro_sucur,
			String compro_letra, int entidad) throws Exception {
		List<OrdenPago> opsPagos = null;
		List<OrdenPago> ops = null;
		List<OrdenPago> opsComprobantes = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			ops = getInstance().getOrdenesPago(fechaInicio, fechaFin,
					id_prestador, cuit, sucur, compro_tipo, compro_nro,
					compro_sucur, compro_letra, entidad, con);

			opsComprobantes = ComprobanteServiceUtil.getComprobantesOP(
					fechaInicio, fechaFin, id_prestador, cuit, sucur,
					compro_tipo, compro_nro, compro_sucur, compro_letra,
					entidad, con);

			opsPagos = getInstance().getPagosOP(fechaInicio, fechaFin, entidad,
					con);
		} finally {
			ConnectionHelper.cerrar(con);
		}

		for (OrdenPago op : ops) {
			int indexPago = opsPagos.indexOf(op);
			if (opsPagos != null && indexPago != -1) {
				op.setFormaPago(opsPagos.get(indexPago).getFormaPago());
			}

			int indexof = opsComprobantes.indexOf(op);
			if (opsComprobantes != null && indexof != -1) {
				op.setComprobantes(opsComprobantes.get(indexof)
						.getComprobantes());
			}

			// 1. La busqueda de comprobantes trae tambien los anticipos que
			// fueron utilizados como forma de pago.
			// 2. La busqueda de formas de pago trae los anticipos pero sin el
			// detalle necesario.
			// 3. Por lo tanto cruzo comprobantes y formas de pago para asignar
			// los anticipos y no hacer nueveamente la query
			// ESTO ES (VALGA LA REDUNDANCIA) PORQUE LOS ANTICIPOS COMPROBANTES
			// QUE LUEGO SON FORMAS DE PAGO
			// asignarAnticiposQuitarComprobante(op);
			asignarAnticiposQuitarComprobante(op, entidad);
		}

		return ops;
	}

	public static BigDecimal getUltimoNumeroChequeOP(int idCtaBcria, int entidad) {
		return getInstance().getUltimoNumeroChequeOP(idCtaBcria, entidad);
	}

	public static String[] getUltimaRazonSocialChequeYDestinoOP(String cuit,
			String sucu, Integer seccional, int entidad) {
		return getInstance().getUltimaRazonSocialChequeYDestinoOP(cuit, sucu,
				seccional, entidad);
	}

	/**
	 * Obtiene el reporte de ordenes de pago, pero con la finromacion necesaria
	 * para el subdiario de egresos. Existen 2 diferencias con el reporte de OP:
	 * 1. El subdiario desglosa el concepto "PRESTAICONES MEDICAS" fijandose las
	 * prestaciones reales en las liquidaciones y/o reintegros necesarios
	 * 
	 * 2. El manejo de anticipos es diferente. Al momento de utilizar los
	 * anticipos para pagar comprobantes, se muestran dichos anticipos restando
	 * en la columna comprobantes (al igual que en el reporte de OP), PERO se
	 * muestra/n su/s conceptos en las columnas de forma de pago y NO en las
	 * columnas dodne irian los conceptos de comprobantes
	 * 
	 * @param fechaInicio
	 * @param fechaFin
	 * @return
	 * @throws Exception
	 */
	public static List<? extends ItemSubdiarioEgreso> reporteOrdenPagoOspimCompletoParaSubdiario(
			Date fechaInicio, Date fechaFin, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros)
			throws Exception {
		Connection con = null;
		List<OrdenPagoOspim> ops = null;
		List<OrdenPagoOspim> opsPagos = null;
		List<OrdenPagoOspim> opsComprobantes = null;
		try {
			con = ConnectionHelper.getReportesOspimConnection();
			// todas ops por fecha
			ops = getInstance().getOrdenesPagoOspim(fechaInicio, fechaFin,
					incluirProveedores, incluirLiquidaciones,
					incluirReintegros, con);
			// todos los comprobantes por fecha de las ops
			opsComprobantes = ComprobanteServiceUtil
					.getComprobantesOPSubdiario(fechaInicio, fechaFin, con);
			// todos las formas de pago de las ops
			opsPagos = getInstance().getPagosOP(fechaInicio, fechaFin, con);
		} finally {
			ConnectionHelper.cerrar(con);
		}
		// Uno las ops con los pagos y los comprobantes
		for (OrdenPagoOspim op : ops) {
			_log.debug("OP: " + op.getId());
			int indexPago = opsPagos.indexOf(op);
			if (opsPagos != null && indexPago != -1) {
				op.setFormaPago(opsPagos.get(indexPago).getFormaPago());
			}
			int indexof = opsComprobantes.indexOf(op);
			if (opsComprobantes != null && indexof != -1) {
				op.setComprobantes(opsComprobantes.get(indexof)
						.getComprobantes());
			}

			// 1. La busqueda de comprobantes trae tambien los anticipos que
			// fueron utilizados como forma de pago.
			// 2. La busqueda de formas de pago trae los anticipos pero sin el
			// detalle necesario.
			// 3. Por lo tanto cruzo comprobantes y formas de pago para asignar
			// los anticipos y no hacer nueveamente la query
			// ESTO ES (VALGA LA REDUNDANCIA) PORQUE LOS ANTICIPOS COMPROBANTES
			// QUE LUEGO SON FORMAS DE PAGO
			// asignarAnticipos(op);
			asignarAnticiposQuitarComprobante(op, WebKeysGlobal.OSPIM);
		}
		return ops;
	}

	public static List<? extends ItemSubdiarioEgreso> reporteOrdenPagoCompletoParaSubdiario(
			Date fechaInicio, Date fechaFin, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros,
			boolean contabilidad, int entidad, int idSeccional) throws Exception {
		
		return reporteOrdenPagoCompletoParaSubdiario(fechaInicio, fechaFin,
				incluirProveedores, incluirLiquidaciones, incluirReintegros,
				null, null, idSeccional, contabilidad, entidad);
	}

	public static List<? extends ItemSubdiarioEgreso> reporteOrdenPagoCompletoParaSubdiario(
			Date fechaInicio, Date fechaFin, boolean incluirProveedores,
			boolean incluirLiquidaciones, boolean incluirReintegros,
			String cuit, String sucursal, int idSeccional,
			boolean contabilidad, int entidad) throws Exception {
		Connection con =null;
		List<OrdenPago> ops =null;
		List<OrdenPago> opsComprobantes =null;
		List<OrdenPago> opsPagos = null;
				try{
					con=ConnectionHelper.getReportesOspimConnection();
		// todas ops por fecha
		ops = getInstance().getOrdenesPago(fechaInicio,
				fechaFin, incluirProveedores, incluirLiquidaciones,
				incluirReintegros, cuit, sucursal, idSeccional, contabilidad,
				entidad, con);
		
		// todos los comprobantes por fecha de las ops
		opsComprobantes = ComprobanteServiceUtil
				.getComprobantesOPSubdiario(fechaInicio, fechaFin, entidad, con);
		// todos las formas de pago de las ops
		opsPagos = getInstance().getPagosOP(fechaInicio,
				fechaFin, entidad,con);
				}finally{
					ConnectionHelper.cerrar(con);
				}
		int[] idCambiar = new int[ops.size()];
		int indice = 0;
		// Uno las ops con los pagos y los comprobantes
		for (OrdenPago op : ops) {
			int indexPago = opsPagos.indexOf(op);
			if (indexPago != -1) {
				if (op.getBaja_fecha() != null
						&& op.getBaja_fecha().equals(op.getFecha())) {
					idCambiar[indice++] = op.getId().intValue();
					op.setMostrarComprobantesEnSubdiario(false);
				}
				if (opsPagos != null) {
					op.setFormaPago(opsPagos.get(indexPago).getFormaPago());
				}
			}
			int indexof = opsComprobantes.indexOf(op);
			if (opsComprobantes != null && indexof != -1) {
				op.setComprobantes(opsComprobantes.get(indexof)
						.getComprobantes());
			}

			// 1. La busqueda de comprobantes trae tambien los anticipos que
			// fueron utilizados como forma de pago.
			// 2. La busqueda de formas de pago trae los anticipos pero sin el
			// detalle necesario.
			// 3. Por lo tanto cruzo comprobantes y formas de pago para asignar
			// los anticipos y no hacer nueveamente la query
			// ESTO ES (VALGA LA REDUNDANCIA) PORQUE LOS ANTICIPOS COMPROBANTES
			// QUE LUEGO SON FORMAS DE PAGO
			asignarAnticiposQuitarComprobante(op, entidad);
		}
		
	    return verificarOpsMarcadas(ops, idCambiar);
	}

	private static List<OrdenPago> verificarOpsMarcadas(List<OrdenPago> ops,
			int[] idCambiar) {
		for (int i = 0; i < idCambiar.length; i++) {
			for (OrdenPago op : ops) {
				if (op.getId().intValue() == idCambiar[i]) {
					op.setMostrarComprobantesEnSubdiario(false);
				}
			}
		}
		return ops;
	}

	@SuppressWarnings("unused")
	private static void asignarAnticipos(OrdenPago op) {
		if (op.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : op.getFormaPago()) {
				if (fp.getTipo().equals(Anticipo.class.getSimpleName())) {
					Anticipo ant = (Anticipo) fp.getPago();
					int indeOf = op.getComprobantes()
							.indexOf(ant.getAnticipo());
					ant.setAnticipo(op.getComprobantes().get(indeOf));
				}
			}
		}
	}

	private static void asignarAnticiposQuitarComprobante(OrdenPago op,
			int entidad) {
		if (op.getFormaPago() != null) {
			for (OrdenPago.FormaPago fp : op.getFormaPago()) {
				if (fp.getTipo().equals(Anticipo.class.getSimpleName())) {
					Anticipo ant = (Anticipo) fp.getPago();
					if (op.getBaja_fecha() != null) {
						ant.setAnticipo(ant.getAnticipo());
					} else {
						_log.debug("ant: " + ant.getNumeroStr() + " OP: "
								+ ant.getOpOrigen());
						int indeOf = op.getComprobantes().indexOf(
								ant.getAnticipo());
						
						if(indeOf==-1) {
					      int pos=0;   		
						  for(Comprobante c:op.getComprobantes()) {
							 if(c.getTipoComprobante().equalsIgnoreCase(ant.getAnticipo().getTipoComprobante()) && c.getNroComprobante().equalsIgnoreCase(ant.getNumeroStr())) {
								indeOf=pos;
								break;
							 }
							 pos++;
						  }
						  
						}
												
						ant.setAnticipo(op.getComprobantes().get(indeOf));
						   
						if (entidad == WebKeysGlobal.UOMA
								|| ant.getImporte().compareTo(BigDecimal.ZERO) == 0) {
							op.getComprobantes().remove(indeOf);
						}
					}
				}
			}
		}
	}

	public static void reactivar(Integer nro, int entidad) throws Exception {
		Connection connectionParameter = ConnectionHelper.getConnection();
		try {
			OrdenPago ordenPago = null;
			if (entidad == WebKeysGlobal.OSPIM) {
				ordenPago = getInstance().getOrdenPagoOspim(nro,
						WebKeysGlobal.OSPIM);
			} else {
				ordenPago = getInstance().getOrdenPago(nro, null, null, null,
						null, entidad);
			}

			// verificar Cheques
			boolean chequesReutilizados = getInstance()
					.verificarChequesReutilizados(nro, connectionParameter,
							entidad);
			if (chequesReutilizados) {
				throw new ChequesReutilizadosException();
			}

			// verificar comprobantes
			boolean comprobantesYaPagados = getInstance()
					.verificarComprobantesYaPagados(nro, connectionParameter,
							entidad);
			if (comprobantesYaPagados) {
				throw new ComprobantesYaPagadosException();
			}

			// verificar comprobantes
			boolean comprobantesAnulados = getInstance()
					.verificarComprobantesAnulados(nro, connectionParameter,
							entidad);
			if (comprobantesAnulados) {
				throw new ComprobantesAnuladosException();
			}

			// verificar anticipos no pagados
			boolean anticiposNoPagados = getInstance()
					.verificarAnticiposNoPagados(nro, connectionParameter,
							entidad);
			if (anticiposNoPagados) {
				throw new AnticiposNoPagadosException();
			}

			boolean anticiposReutilizados = getInstance()
					.verificarAnticiposReUtilizados(nro, connectionParameter,
							entidad);
			if (anticiposReutilizados) {
				throw new AnticiposReUtilizadosException();
			}

			if (entidad == WebKeysGlobal.OSPIM) {
				Date fecha_cierre_periodo = ContabilidadServiceUtil
						.getFechaUltimoPeriodoContable(entidad);
				if (ordenPago.getBaja_fecha().compareTo(fecha_cierre_periodo) <= 0) {
					throw new FechaMenorACierreContableException();
				}
			}

			getInstance().reactivar(nro, connectionParameter, entidad);
			connectionParameter.commit();
		} catch (Exception e) {
			connectionParameter.rollback();
			throw e;
		} finally {
			ConnectionHelper.cerrar(connectionParameter);
		}
	}

	public static void anularOrdenPagoOspim(Integer nro, User user,
			Date fechaBaja, List<Cheque> chequesAnular,
			Connection connectionParameter) throws SystemException,
			AnticiposUtilizadosException, FechaBajaMenorQueAltaException,
			OpConChequesCanjeadosException, NumberFormatException,
			ParseException, FechaMenorACierreContableException {
		anularOrdenPago(nro, user, fechaBaja, chequesAnular,
				WebKeysGlobal.OSPIM, connectionParameter);
	}

	public static void anularOrdenPagoOspim(Integer nro, User user,
			Date fechaBaja, List<Cheque> chequesAnular) throws SystemException,
			AnticiposUtilizadosException, FechaBajaMenorQueAltaException,
			OpConChequesCanjeadosException, NumberFormatException,
			ParseException, FechaMenorACierreContableException {
		anularOrdenPago(nro, user, fechaBaja, chequesAnular,
				WebKeysGlobal.OSPIM, null);
	}

	public static void anularOrdenPago(Integer nro, User user, Date fechaBaja,
			List<Cheque> chequesAnular, int entidad) throws SystemException,
			AnticiposUtilizadosException, FechaBajaMenorQueAltaException,
			OpConChequesCanjeadosException, NumberFormatException,
			ParseException, FechaMenorACierreContableException {
		anularOrdenPago(nro, user, fechaBaja, chequesAnular, entidad, null);
	}

	public static List<ReintegroList> getReintegrosFarmaciaLists(
			Integer idSeccional, Date fechaIni, Date fechaFin)
			throws SystemException {
		return getInstance().getReintegrosFarmaciasLists(idSeccional, fechaIni,
				fechaFin);
	}

	public static void save(List<OrdenPago> ordenes, User user, int entidad)
			throws Exception {
		Connection connection = ConnectionHelper.getConnection();
		try {
			connection.setAutoCommit(false);
			for (int i = 0; i < ordenes.size(); i++) {
				guardarOP(ordenes, i, user, connection, entidad);
			}
			connection.commit();
		} catch (Exception e) {
			_log.debug("error al guardar op amtima", e);
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	private static void guardarOP(List<OrdenPago> ordenes, int i, User user,
			Connection connection, int entidad) throws Exception {
		OrdenPago op = (OrdenPago) ordenes.get(i);
		if (op.getId() > 0) {
			return;
		}
		OrdenPagoServiceUtil.save(op, user, connection, entidad);
	}

	public static boolean verificarOPCreadaEnCanje(Integer nro, int entidad)
			throws Exception {
		Connection connection = ConnectionHelper.getConnection();
		try {
			return getInstance().verificarOPCreadaEnCanje(nro, connection,
					entidad);
		} catch (Exception e) {
			_log.debug("error al guardar op", e);
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	public static List<Cheque> verificarOpConChequesCanjeados(Integer id,
			Connection connectionParameter, int entidad) throws SystemException {
		return getInstance().verificarOpConChequesCanjeados(id,
				connectionParameter, entidad);
	}
	
	public static int obtenerProximoIdOrdenPago() throws SystemException {
		return getInstance().obtenerProximoIdOrdenPago();
	}
	
	public static List<OrdenPagoOspim> reporteOrdenPagoInterbanking(
			Date fechaInicio, Date fechaFin,Integer tipoPago,Integer ctaBcria,
			int entidad) throws Exception {
		Connection con =null;
		List<OrdenPagoOspim> ops =null;
		
		return getInstance().reporteOrdenPagoInterbanking(fechaInicio,
				fechaFin, tipoPago,ctaBcria ,entidad, con);
	}
	
	public static void updateFormaPago(OrdenPago op, User user,
			Connection connectionParameter, int entidad)
			throws Exception {
		
		int idOriginal = op.getId();
		_log.debug("obteniendo conexion");
		Connection con = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}

			getInstance().eliminarPagos(op.getId(), con, entidad);

			for (OrdenPago.FormaPago pago : op.getFormaPago()) {
				pago.getPago().savePago(op, user.getScreenName(), con, entidad);
			}

			if (connectionParameter == null) {
				con.commit();
			}
		} catch (SystemException e) {
			_log.error("Error:", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			op.setId(idOriginal);
			throw e;
		} catch (DuplicateNumeroComprobanteException e) {
			_log.error("Error:", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			op.setId(idOriginal);
			throw e;
		} catch (Exception e) {
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			_log.error("Error:", e);
			op.setId(idOriginal);
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}
	}
	
	
	public static Double getAlicuotaARBA(String cuit, Date fecha, String tipo) throws Exception {
		return getInstance().getAlicuotaARBA(cuit,fecha,tipo);
	}
	
	public static void saveOPsFromLiquidaciones(String lista,String ctaBcria) throws Exception {
		getInstance().saveOPsFromLiquidaciones(lista,ctaBcria,null);
	}
}
