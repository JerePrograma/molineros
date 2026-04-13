package ar.com.ospim.tesoreria.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.MovimientoBancoCheque;
import ar.com.ospim.tesoreria.beans.MovimientoBancoReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.reportes.ReporteAcreditacionesAFIPExcel.ResumenExtractoBancario;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="MovimientoBancarioServiceUtil.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.tesoreria.services.MovimientoBancarioServiceImpl</code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 * 
 * @author Federico Brachi
 * 
 * @see ar.com.ospim.global.services.MovimientoBancarioServiceUtil
 * 
 */
public class MovimientoBancarioServiceUtil {

	private static Log _log = LogFactoryUtil
			.getLog(MovimientoBancarioServiceUtil.class);

	private static MovimientoBancarioServiceImpl instance = null;

	public static MovimientoBancarioServiceImpl getInstance() {
		if (null == instance) {
			instance = new MovimientoBancarioServiceImpl();
		}
		return instance;
	}

	public static MovimientoBancario grabaMovimientoBancario(
			MovimientoBancario mov, User user, Connection connectionParameter,
			int entidad) throws Exception {
		_log.debug("creando conexion");
		Connection con = null;

		if (connectionParameter == null) {
			con = ConnectionHelper.getConnectionForTransaction();
		} else {
			con = connectionParameter;
		}
		try {
			int id = getInstance().grabaMovimientoBancario(mov,
					user.getScreenName(), con, entidad);
			mov.setId_movimiento(id);

			if (mov.getChequesDepositados() != null
					&& mov.getChequesDepositados().size() > 0) {
				for (MovimientoBancoCheque mbch : mov.getChequesDepositados()) {
					Cheque ch = mbch.getCheque();
					if (ch.getEstado().getId() == Cheque.Estado.RECHAZADO
							&& !mbch.isBorradoLogico()) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						getInstance().saveCheque(mov, ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								ch.getEstado(), user.getScreenName(), con,
								entidad);
					}
				}
			}

			if (mov.getChequesRecibidos() != null
					&& mov.getChequesRecibidos().size() > 0) {
				for (MovimientoBancoCheque mbch : mov.getChequesRecibidos()) {
					Cheque ch = mbch.getCheque();
					if (ch.getEstado().getId() == Cheque.Estado.RECIBIDO
							&& !mbch.isBorradoLogico()) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								user.getScreenName(), con, entidad);
						getInstance().saveCheque(mov, ch, ch.getEstado(),
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								user.getScreenName(), con, entidad);
					}
				}
			}
			// EN QUE CASOS PASA DE DEPOSITADO A RECIBIDO!?!?

			/*
			 * if (mov.getChequesRecibidos() != null &&
			 * mov.getChequesRecibidos().size() > 0) { for
			 * (MovimientoBancoCheque mbch : mov.getChequesRecibidos()) { Cheque
			 * ch = mbch.getCheque(); if (ch.getEstado().getId() ==
			 * Cheque.Estado.RECIBIDO) {
			 * ChequeServiceUtil.cambiarEstadoCheque(ch, new Estado(
			 * Cheque.Estado.DEPOSITADO), user.getScreenName(), con, entidad);
			 * getInstance().saveCheque(mov, ch, new
			 * Cheque.Estado(Cheque.Estado.DEPOSITADO), ch.getEstado(),
			 * user.getScreenName(), con, entidad); } } }
			 */

			if (mov.getChequesCanjeados() != null
					&& mov.getChequesCanjeados().size() > 0) {
				for (MovimientoBancoCheque mbch : mov.getChequesCanjeados()) {
					if (!mbch.isBorradoLogico()) {
						Cheque ch = mbch.getCheque();
						Cheque.Estado estado = new Cheque.Estado(
								Cheque.Estado.CHEQUE_PROPIO_CANJEADO);
						ChequeServiceUtil.cambiarEstadoCheque(ch, estado,
								user.getScreenName(), con, entidad);
						getInstance().saveCheque(mov, ch,
								new Cheque.Estado(Cheque.Estado.EMITIDO),
								estado, user.getScreenName(), con, entidad);
					}
				}
			}

			if (mov.getEfectivoRecibido() != null
					&& mov.getEfectivoRecibido().size() > 0) {
				for (MovimientoBancoReciboIngreso mbri : mov
						.getEfectivoRecibido()) {
					ReciboIngreso ri = mbri.getReciboIngreso();
					if (((Efectivo) ri.getIngreso()).getEstado().getId() == Efectivo.Estado.DEPOSITADO && !mbri.isBorradoLogico()) {
						ReciboServiceUtil.cambiarEstadoReciboIngreso(ri,
								((Efectivo) ri.getIngreso()).getEstado(),
								user.getScreenName(), con, entidad);
						getInstance().save(mov, ri, user.getScreenName(), con,
								entidad);
					}
				}
			}
			if (connectionParameter == null) {
				con.commit();
			}
		} catch (Exception e) {
			_log.error("Error al guardar mov", e);
			if (connectionParameter == null) {
				ConnectionHelper.rollback(con);
			}
			throw e;
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(con);
			}
		}

		return mov;
	}

	public static MovimientoBancario editaMovimientoBancario(
			MovimientoBancario mov, User user, int entidad) throws Exception {
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (mov.getFecha_movimiento().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		_log.debug("creando conexion");
		Connection con = ConnectionHelper.getConnection();
		con.setAutoCommit(false);

		try {
			
			List<MovimientoBancoCheque> lbcDelete = new ArrayList <MovimientoBancoCheque>();
			if (mov.getChequesDepositados() != null
					&& mov.getChequesDepositados().size() > 0) {
				
				
				for (MovimientoBancoCheque mbch : mov.getChequesDepositados()) {
					Cheque ch = mbch.getCheque();
					if (mbch.getId() > 0
							&& (mbch.isBorradoLogico() || ch.getEstado()
									.getId() == Cheque.Estado.DEPOSITADO)) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						getInstance().borraMovimientoBancoItem(mov, mbch, con,
								entidad);
						lbcDelete.add(mbch);
						
					} else if (mbch.getId() < 0
							&& ch.getEstado().getId() == Cheque.Estado.RECHAZADO) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						getInstance().saveCheque(mov, ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								ch.getEstado(), user.getScreenName(), con,
								entidad);
					}
				}
				
				for(MovimientoBancoCheque mbch : lbcDelete) {
					mov.getChequesDepositados().remove(mbch);
				}
			}

			lbcDelete.clear();
			if (mov.getChequesRecibidos() != null
					&& mov.getChequesRecibidos().size() > 0) {
				for (MovimientoBancoCheque mbch : mov.getChequesRecibidos()) {
					Cheque ch = mbch.getCheque();
					if (mbch.getId() > 0 && mbch.isBorradoLogico()) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						getInstance().borraMovimientoBancoItem(mov, mbch, con,
								entidad);
						lbcDelete.add(mbch);
					} else if (mbch.getId() > 0
							&& ch.getEstado().getId() == Cheque.Estado.RECIBIDO) {
/*						
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								user.getScreenName(), con, entidad);
						getInstance().saveCheque(mov, ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								ch.getEstado(), user.getScreenName(), con,
								entidad);
*/
						
						
// Revierte estado DEPOSITADO y saca del movimiento 					
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								new Cheque.Estado(Cheque.Estado.RECIBIDO),
								user.getScreenName(), con, entidad);
						
						getInstance().borraMovimientoBancoItem(mov, mbch, con,
								entidad);
						lbcDelete.add(mbch);
// Fin  Reversión						
					  	
//NUEVO DEPOSITA UN CHEQUE con un movimiento ya generado						
					}else if (mbch.getId() > 0 
							&& ch.getEstado().getId() == Cheque.Estado.DEPOSITADO) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								new Cheque.Estado(Cheque.Estado.DEPOSITADO),
								user.getScreenName(), con, entidad);
//Fin nuevo						
					}else if (mbch.getId() < 0
							&& ch.getEstado().getId() == Cheque.Estado.DEPOSITADO) {
						ChequeServiceUtil.cambiarEstadoCheque(ch,
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						getInstance().saveCheque(mov, ch,
								new Cheque.Estado(Cheque.Estado.RECIBIDO),
								ch.getEstado(), user.getScreenName(), con,
								entidad);
						
					}
				}
				
				for(MovimientoBancoCheque mbch : lbcDelete) {
					mov.getChequesRecibidos().remove(mbch);
				}
			}

			List<MovimientoBancoReciboIngreso> lBIDelete = new ArrayList <MovimientoBancoReciboIngreso>();
			if (mov.getEfectivoRecibido() != null
					&& mov.getEfectivoRecibido().size() > 0) {
				for (MovimientoBancoReciboIngreso mbri : mov
						.getEfectivoRecibido()) {
					ReciboIngreso ri = mbri.getReciboIngreso();
					Efectivo efectivo = (Efectivo) ri.getIngreso();
					if (mbri.getId() > 0
							&& (mbri.isBorradoLogico() || efectivo.getEstado()
									.getId() == Efectivo.Estado.RECIBIDO)) {
						ReciboServiceUtil.cambiarEstadoReciboIngreso(ri,
								efectivo.getEstado(), user.getScreenName(),
								con, entidad);
						getInstance().borraMovimientoBancoItem(mov, mbri, con,
								entidad);
						lBIDelete.add(mbri);
					} else if (mbri.getId() < 0
							&& efectivo.getEstado().getId() == Efectivo.Estado.DEPOSITADO) {
						ReciboServiceUtil.cambiarEstadoReciboIngreso(ri,
								efectivo.getEstado(), user.getScreenName(),
								con, entidad);
						getInstance().save(mov, ri, user.getScreenName(), con,
								entidad);
					}
				}
				
				for(MovimientoBancoReciboIngreso mbch : lBIDelete) {
					mov.getEfectivoRecibido().remove(mbch);
				}
			}
			
			getInstance().editaMovimientoBancario(mov, user.getScreenName(),
					con, entidad);
			
			con.commit();
		} catch (Exception e) {
			_log.error("Error al guardar mov", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			con.close();
		}

		return mov;
	}

	public static List<MovimientoBancario> buscaMovimientoBcrio(
			Date fecha_desde, Date fecha_hasta, int id_cta_bcria,
			String descripcion, int tipoMov, int entidad) throws Exception {
		return getInstance().buscaMovimientoBcrio(fecha_desde, fecha_hasta,
				id_cta_bcria, descripcion, tipoMov, entidad);
	}

	public static MovimientoBancario get(int id_movimiento, int entidad)
			throws Exception {
		MovimientoBancario movimientoBancario = getInstance().get(
				id_movimiento, entidad);

		List<MovimientoBancoCheque> chequesReicibidos = getInstance()
				.getChequesEstadoOriginal(
						movimientoBancario.getId_movimiento(),
						new Estado(Cheque.Estado.RECIBIDO), entidad);
		if (chequesReicibidos != null) {
			movimientoBancario.setChequesRecibidos(chequesReicibidos);
		}

		List<MovimientoBancoCheque> chequesDepositados = getInstance()
				.getChequesEstadoOriginal(
						movimientoBancario.getId_movimiento(),
						new Estado(Cheque.Estado.DEPOSITADO), entidad);
		if (chequesDepositados != null) {
			movimientoBancario.setChequesDepositados(chequesDepositados);
		}

		List<MovimientoBancoCheque> chequesCanjeados = getInstance()
				.getChequesEstadoOriginal(
						movimientoBancario.getId_movimiento(),
						new Estado(Cheque.Estado.EMITIDO), entidad);
		if (chequesCanjeados != null) {
			movimientoBancario.setChequesCanjeados(chequesCanjeados);
		}

		List<MovimientoBancoReciboIngreso> ef = getInstance().getEfectivo(
				movimientoBancario.getId_movimiento(), entidad);
		if (ef != null) {
			movimientoBancario.setEfectivoRecibido(ef);
		}

		return movimientoBancario;
	}

	public static int borraMovimientoBcrio(int id_movimiento, String user,
			Connection connectionParameter, int entidad) throws Exception {
		return getInstance().borraMovimientoBcrio(id_movimiento, user,
				connectionParameter, entidad);
	}

	public static int borraMovimientoBcrio(int id_movimiento, String user,
			int entidad) throws Exception {
		return getInstance().borraMovimientoBcrio(id_movimiento, user, null,
				entidad);
	}

	public static Map<Date, List<ResumenExtractoBancario>> getResumenExtractoBancario(
			Date fechaIni, Date fechaFin) throws SQLException {
		return getInstance().getResumenExtractoBancario(fechaIni, fechaFin);
	}

	public static List<? extends ItemSubdiarioEgreso> reporteParaSubdiario(
			Date fechaInicio, Date fechaFin, int entidad) throws Exception {
		return getInstance().reporteParaSubdiario(fechaInicio, fechaFin,
				entidad);
	}

	public static MovimientoBancario grabaMovimientoBancario(
			MovimientoBancario mov, User user, int entidad) throws Exception {
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (mov.getFecha_movimiento().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}
		return grabaMovimientoBancario(mov, user, null, entidad);
	}
}
