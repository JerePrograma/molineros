package ar.com.ospim.tesoreria.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoAmtima;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.services.ComprobanteServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.tesoreria.OpConChequesCanjeadosException;
import ar.com.ospim.tesoreria.beans.Chequera;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancoCheque;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio;
import ar.com.ospim.tesoreria.beans.canje.CanjeChequePropio.ChequeACanjear;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CanjeChequePropioServiceUtil {
	private static Log logger = LogFactoryUtil
			.getLog(CanjeChequePropioServiceUtil.class);

	private static CanjeChequePropioServiceImpl instance = null;

	public static CanjeChequePropioServiceImpl getInstance() {
		if (null == instance) {
			instance = new CanjeChequePropioServiceImpl();
		}
		return instance;
	}

	public static void save(CanjeChequePropio cpp, User user, int entidad)
			throws Exception {
		logger.debug("salvando canjeChequePropio");
		Connection connection = null;
		try {
			OrdenPago ordenPago = null;
			if (entidad==WebKeysGlobal.OSPIM) {
				ordenPago= (OrdenPagoOspim) OrdenPagoServiceUtil
						.getOrdenPagoOspim(cpp.getOrdenPago().getId());
			} else {
				ordenPago= OrdenPagoServiceUtil
						.getOrdenPago(cpp.getOrdenPago().getId(), entidad);
			} 
			String numero = ComprobanteServiceUtil.getUltimoNumeroComprobante(
					WebKeysGlobal.COMPROBANTE_VARIOS, ordenPago
							.getAcreedor().getCuit(), ordenPago
							.getAcreedor().getSucursal(), entidad);
			
			int nroComprobante = Integer.valueOf(numero) + 1;

			cpp.setOrdenPago(ordenPago);

			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			// nueva OP
			OrdenPago opo =null;
			
			opo= obtenerOp(cpp, nroComprobante, entidad);
			
			OrdenPagoServiceUtil.save(opo, user, connection, entidad);
			cpp.setOrdenPagoNueva(opo);

			MovimientoBancario mov = obtenerMovimientoBancario(cpp);
			mov = MovimientoBancarioServiceUtil.grabaMovimientoBancario(mov,
					user, connection, entidad);
			cpp.setIdMovimientoBancario(mov.getId_movimiento());

			int id = getInstance().save(cpp, user, connection, entidad);
			cpp.setId(id);
			List<ChequeACanjear> chequesViejos = cpp.getChequesViejos();
			for (ChequeACanjear chequeACanjear : chequesViejos) {
				if (chequeACanjear.isCanjeado()) {
					Cheque cheque = chequeACanjear.getCheque();
					getInstance().guardarAsociacionCheque(cheque, false, cpp,
							connection, entidad);
				}
			}

			List<Cheque> chequesNuevos = cpp.getChequesNuevos();
			for (Cheque cheque : chequesNuevos) {
				// ChequeServiceUtil.save(cheque, user, true); //YA GUARDO EL
				// CHEQUE NUEVO CUENDO ACTUALIZO LA OP...
				getInstance().guardarAsociacionCheque(cheque, true, cpp,
						connection, entidad);
			}

			connection.commit();
			logger.debug("canjeChequePropio salvado");
		} catch (Exception e) {
			logger.error("Error al guardar canje cheque", e);
			cpp.setId(0);
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	private static MovimientoBancario obtenerMovimientoBancario(
			CanjeChequePropio cpp) {

		MovimientoBancario movimientoBancario = new MovimientoBancario();
		List<MovimientoBancoCheque> listaChequesCanjeados = new ArrayList<MovimientoBancoCheque>();
		BigDecimal importe = BigDecimal.ZERO;
		CuentaBancaria cta = null;
		for (ChequeACanjear chequeACanjear : cpp.getChequesViejos()) {
			if (chequeACanjear.isCanjeado()) {
				MovimientoBancoCheque movimiento = new MovimientoBancoCheque();
				movimiento.setCheque(chequeACanjear.getCheque());
				movimiento.setEstadoViejo(chequeACanjear.getCheque()
						.getEstado());
				movimiento.setEstadoNuevo(new Cheque.Estado(
						Cheque.Estado.CHEQUE_PROPIO_CANJEADO));
				importe = importe.add(chequeACanjear.getCheque().getImporte());
				cta = chequeACanjear.getCheque().getCuentaBancaria();
				listaChequesCanjeados.add(movimiento);
			}
		}
		movimientoBancario.setChequesCanjeados(listaChequesCanjeados);
		movimientoBancario.setFecha_movimiento(new Date());
		movimientoBancario.setDeb_cred(false);
		movimientoBancario.setImporte(importe);
		movimientoBancario.setTipo_mov(new TipoMovBcrio(
				TipoMovBcrio.CANJE_CHEQUE));
		movimientoBancario.setChequera(new Chequera());
		movimientoBancario.setFecha_comprobante(new Date());
		movimientoBancario.setCta_bcria(cta);
		movimientoBancario.setDescripcion("");
		return movimientoBancario;
	}

	private static OrdenPago obtenerOp(CanjeChequePropio cpp,
			int nroComprobante, int entidad) {
		OrdenPago opo = null;
		if(entidad==WebKeysGlobal.AMTIMA){
			opo = new OrdenPagoAmtima();
		}else if(entidad==WebKeysGlobal.UOMA){
			opo = new OrdenPagoUoma();
		}else{	
			opo = new OrdenPagoOspim();
		}

		// acreedor
		opo.setAcreedor(cpp.getOrdenPago().getAcreedor());

		// comprobante
		List<Comprobante> comprobantes = new ArrayList<Comprobante>();
		Comprobante comprobante = new Comprobante();
		comprobante.setAcreedorEmpresa(cpp.getOrdenPago().getAcreedor());
		comprobante.setPtoVenta(1);
		comprobante.setTipoComprobante(WebKeysGlobal.COMPROBANTE_VARIOS);
		comprobante.setNroComprobante(cpp.getOrdenPago().getAcreedor()
				.getCuit()
				+ "-"
				+ cpp.getOrdenPago().getAcreedor().getSucursal()
				+ "/" + nroComprobante);
		comprobante.setCuitEmisor(WebKeysGlobal.CUIT_OSPIM);
		comprobante.setFechaEmision(new Date());
		comprobante.setFechaRecepcion(new Date());
		comprobante.setImporteComprobante(cpp.getImporteCanjeado());
		comprobante.setLetraComprobante(" ");
		comprobantes.add(comprobante);

		List<ComprobanteConcepto> conceptos = new ArrayList<Comprobante.ComprobanteConcepto>();
		ComprobanteConcepto concepto = new ComprobanteConcepto();
		concepto.setImporte(cpp.getImporteCanjeado());
		concepto.setConceptoComprobante(new Concepto(ConceptoServiceUtil
				.getIdCanjeCheque(new Date(), entidad)));
		conceptos.add(concepto);
		comprobante.setConceptos(conceptos);
		opo.setComprobantes(comprobantes);

		// /forma pago
		List<OrdenPago.FormaPago> pagos = new ArrayList<OrdenPago.FormaPago>();
		for (Cheque c : cpp.getChequesNuevos()) {
			c.setAlta_fecha(null);
			OrdenPago.FormaPago pago = new OrdenPago.FormaPago();
			pago.setNuevo(false);
			pago.setPago(c);
			pagos.add(pago);
		}
		opo.setFormaPago(pagos);
		opo.setImporte(cpp.getImporteCanjeado());
		return opo;
	}

	public static CanjeChequePropio get(int id, int entidad)
			throws Exception {
		CanjeChequePropio canjeChequePropio = getInstance().get(id, entidad);
		if (canjeChequePropio != null && canjeChequePropio.getId() != 0) {
			List<Cheque> chequesNuevos = getInstance().getChequesNuevos(id,
					entidad);
			List<Cheque> chequesViejos = getInstance().getChequesViejos(id,
					entidad);
			canjeChequePropio.setChequesNuevos(chequesNuevos);

			List<CanjeChequePropio.ChequeACanjear> listaChequesViejos = new ArrayList<CanjeChequePropio.ChequeACanjear>();

			OrdenPago ordenPago = null;
			
			if (entidad==WebKeysGlobal.OSPIM) { 
				ordenPago = OrdenPagoServiceUtil
						.getOrdenPagoOspim(canjeChequePropio
								.getOrdenPago().getId());
			} else{
				ordenPago = OrdenPagoServiceUtil
						.getOrdenPago(canjeChequePropio
								.getOrdenPago().getId(), entidad);
			}
			for (Cheque cheque : ordenPago.getSoloCheques()) {
				CanjeChequePropio.ChequeACanjear chequeACanjear = new CanjeChequePropio.ChequeACanjear(
						cheque);
				if (chequesViejos.contains(cheque)) {
					chequeACanjear.setCanjeado(true);
				}
				listaChequesViejos.add(chequeACanjear);
			}
			canjeChequePropio.setChequesViejos(listaChequesViejos);
			return canjeChequePropio;
		}
		return null;
	}

	public static List<CanjeChequePropio> buscar(Date fechaIni, Date fechaFin,
			BigDecimal chequeNuevo, BigDecimal chequeCanjeado,
			Integer op_generada, int entidad) throws SystemException {
		return getInstance().buscar(fechaIni, fechaFin, chequeNuevo,
				chequeCanjeado, op_generada, entidad);
	}

	public static void anular(Integer id, User user, int entidad)
			throws Exception {
		CanjeChequePropio canjeChequePropio = get(id, entidad);

		Connection connection = ConnectionHelper.getConnectionForTransaction();
		try {
			List<Cheque> canjeados = OrdenPagoServiceUtil
					.verificarOpConChequesCanjeados(canjeChequePropio
							.getOrdenPagoNueva().getId(), connection,
							entidad);
			if (canjeados != null && canjeados.size() > 0) {
				throw new OpConChequesCanjeadosException();
			}
			if (entidad!=WebKeysGlobal.OSPIM) {
				OrdenPagoServiceUtil.anularOrdenPago(canjeChequePropio
						.getOrdenPagoNueva().getId(), user, new Date(),
						canjeChequePropio.getChequesNuevos(), entidad);
			} else {
				OrdenPagoServiceUtil.anularOrdenPagoOspim(canjeChequePropio
						.getOrdenPagoNueva().getId(), user, new Date(),
						canjeChequePropio.getChequesNuevos(), connection);
			}

			getInstance().anular(id, user.getScreenName(), connection, entidad);
			MovimientoBancarioServiceUtil.borraMovimientoBcrio(
					canjeChequePropio.getIdMovimientoBancario(),
					user.getScreenName(), connection, entidad);
			connection.commit();
		} catch (Exception e) {
			logger.error("no se pudo anular canje id: " + id, e);
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}

	}
}
