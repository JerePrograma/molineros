package ar.com.uoma.recibos.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.DuplicateNumeroReciboException;
import ar.com.ospim.tesoreria.ReciboDerivadoException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboCheque;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel.ReporteAnticipos;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ReciboNoOSServiceUtil {
	private static Log logger = LogFactoryUtil
			.getLog(ReciboNoOSServiceUtil.class);

	public static void save(Recibo recibo, boolean debaja, User user,
			String entidad, int entidad_i) throws SystemException,
			DuplicateNumeroReciboException, DuplicateNumeroChequeException,
			FechaMenorACierreContableException, NumberFormatException,
			ParseException {
		// CUANDO TENGAMOS CONTABILIDAD PARA QUE NO SE PUEDAN REALIZAR
		// MOVIMIENTOS FUERA DE LA FECHA DEL CIERRE CONTABLE
		/*
		 * Date fecha_cierre_periodo = ContabilidadServiceUtil
		 * .getFechaUltimoPeriodoContable(); if
		 * (recibo.getFecha().compareTo(fecha_cierre_periodo) <= 0) { throw new
		 * FechaMenorACierreContableException(); }
		 */

		logger.debug("Guardando recibo");
		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			ReciboNoOSServiceImpl instance = ReciboNoOSServiceImpl
					.getInstance();

			if (null != recibo && !recibo.getNumero().equals("")) {
				// busco duplicados
				List<Recibo> recibos = instance.get(recibo.getNumero(), null,
						null, entidad, con);

				if (recibos != null && recibos.size() > 0) {
					for (Recibo r : recibos) {
						if (r.getBaja_fecha() == null) {
							throw new DuplicateNumeroReciboException();
						}
					}
				}
			}

			// salvo el recibo
			String userName = user.getScreenName();
			int id = instance.save(recibo, debaja, userName, entidad, con);
			recibo.setId(id);

			// salvo ingresos
			if (recibo.getId() != 0 && recibo.getIngresos() != null) {
				for (ReciboIngreso ing : recibo.getIngresos()) {
					Ingreso ingreso = ing.getIngreso();
					ingreso.setCuit(recibo.getEmpresa().getCuit());
					int idReciboIng = ingreso.saveIngreso(instance, recibo,
							userName, con, entidad_i);
					ing.setId(idReciboIng);
				}
			}

			// salvo conceptos
			List<ReciboActa> actas = recibo.getActas();
			if (recibo.getId() != 0 && actas != null) {
				for (ReciboActa rActa : actas) {
					instance.save(recibo, rActa, userName, con, entidad_i, entidad);
				}
			}

			List<ReciboConvenio> convenios = recibo.getConvenios();
			if (recibo.getId() != 0 && convenios != null) {
				for (ReciboConvenio rConv : convenios) {
					instance.save(recibo, rConv, userName, con, entidad_i, entidad);
				}
			}

			if (recibo.getChequesNoDepositados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesNoDepositados()) {
					instance.saveChequeASustituir(rCheque, recibo, userName,
							con, entidad_i);
				}
			}

			if (recibo.getChequesRechazados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesRechazados()) {
					instance.saveChequeRechazadoASustituir(rCheque, recibo,
							userName, con, entidad_i);
				}
			}

			if (recibo.getOtrosConceptos() != null) {
				for (ReciboOtroConcepto oc : recibo.getOtrosConceptos()) {
					instance.save(oc, recibo, userName, con, entidad_i);
				}
			}

			logger.debug("Commiteando recibo");
			con.commit();
			logger.debug("Recibo guardado");
		} catch (SystemException e) {
			manejarExcepcion(recibo, con, e);
			throw e;
		} catch (SQLException e) {
			manejarExcepcion(recibo, con, e);
			throw new SystemException(e);
		} catch (DuplicateNumeroChequeException e) {
			manejarExcepcion(recibo, con, e);
			throw e;
		} catch (DuplicateNumeroReciboException e) {
			manejarExcepcion(recibo, con, e);
			throw e;
		} catch (Exception e) {
			manejarExcepcion(recibo, con, e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
		}

	}

	private static void manejarExcepcion(Recibo recibo, Connection con,
			Exception e) {
		// saco el id que se asigno al recibo, porque se hizo rollback
		recibo.setId(0);
		logger.debug("Error al guardar recibo", e);
		ConnectionHelper.rollback(con);
	}

	public static List<Recibo> get(String actaNroStr, String cuit,
			String empresa) throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().get(actaNroStr, cuit,
				empresa, null, null);
	}

	public static List<Recibo> get(String actaNroStr, String cuit,
			String empresa, String entidad) throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().get(actaNroStr, cuit,
				empresa, entidad);
	}

	public static List<Recibo> get(String actaNroStr, String cuit,
			String empresa, String entidad, String sacarRecibos,
			Date fechaDesde, Date fechaHasta) throws SystemException {
		List<Recibo> recibos = ReciboNoOSServiceImpl.getInstance().get(
				actaNroStr, cuit, empresa, entidad, sacarRecibos, fechaDesde,
				fechaHasta);
		List<Recibo> recibosAux = new ArrayList<Recibo>();
		if (sacarRecibos == null || sacarRecibos.trim().equals("") || sacarRecibos.trim().equals("undefined")) {
			sacarRecibos = "0";
		}		
		boolean sacar=false;
		for (Recibo recibo : recibos) {
			for (String id : sacarRecibos.split("\\|")) {
				if (!id.equals("")) {
					if (recibo.getId() == Integer.parseInt(id)) {
						sacar=true;
					}
				}
			}
			if(!sacar){
				recibosAux.add(recibo);				
			}
			sacar=false;
		}

		return recibosAux;
	}

	public static Recibo get(int id, int entidad) throws SystemException, Exception {
		Recibo recibo = ReciboNoOSServiceImpl.getInstance().get(id, entidad);

		if (recibo != null) {
			List<ReciboConcepto> conceptos = ReciboNoOSServiceImpl
					.getInstance().getConceptos(id, entidad);

			asignarConceptos(recibo, conceptos, entidad);
			recibo.setIngresos(getIngresos(id, entidad));
		}

		return recibo;
	}

	private static void asignarConceptos(Recibo recibo,
			List<ReciboConcepto> conceptos, int entidad) throws SystemException, Exception {
		List<ReciboActa> actas = new ArrayList<ReciboActa>();
		List<ReciboConvenio> convenios = new ArrayList<ReciboConvenio>();
		List<ReciboCheque> chequesNoDepositados = new ArrayList<ReciboCheque>();
		List<ReciboCheque> chequesRechazados = new ArrayList<ReciboCheque>();
		List<ReciboOtroConcepto> otrosConceptos = new ArrayList<ReciboOtroConcepto>();

		if (conceptos != null) {
			for (ReciboConcepto rc : conceptos) {
				if (rc instanceof ReciboActa) {
					ReciboActa reciboActa = (ReciboActa) rc;
					Acta acta =null;
					if(rc.getEntidad().equals("O.S.P.I.M.")){
						acta = ActaServiceUtil.getActa(reciboActa
								.getActa().getId(), recibo.getId());
					}else{
						acta = ActaNoOSServiceUtil.getActa(reciboActa
								.getActa().getId(), recibo.getId());
					}
					acta.setTotalActaPagosChequeNoIngresados(reciboActa
							.getImportePorCheques());
					reciboActa.setActa(acta);
					actas.add(reciboActa);
				} else if (rc instanceof ReciboConvenio) {
					ReciboConvenio reciboConvenio = (ReciboConvenio) rc;
					Convenio conv = null;
					if(rc.getEntidad().equals("O.S.P.I.M.")){
						conv = ConvenioServiceUtil
								.getConvenio(reciboConvenio.getConvenio().getId(), recibo.getId());
					}else{
						conv = ConvenioNoOSServiceUtil
							.getConvenio(reciboConvenio.getConvenio().getId(), recibo.getId(), entidad);
					}	
					reciboConvenio.setConvenio(conv);
					conv.setTotalConvenioPagosChequeNoIngresados(reciboConvenio
							.getImportePorCheques());
					convenios.add(reciboConvenio);
				} else if (rc instanceof ReciboCheque) {
					ReciboCheque reciboCheque = (ReciboCheque) rc;
					Cheque chequeIngreso = reciboCheque.getChequeASustituir();
					Cheque ch = ChequeServiceUtil.getCheques(chequeIngreso, entidad)
							.get(0);
					reciboCheque.setChequeASustituir(ch);
					if (reciboCheque.getTipo().equals(
							ReciboCheque.Tipo.RECHAZADO)) {
						chequesRechazados.add(reciboCheque);
					} else {
						chequesNoDepositados.add(reciboCheque);
					}
				} else if (rc instanceof ReciboOtroConcepto) {
					otrosConceptos.add((ReciboOtroConcepto) rc);
				}
			}
		}

		recibo.setActas(actas);
		recibo.setConvenios(convenios);
		recibo.setChequesNoDepositados(chequesNoDepositados);
		recibo.setChequesRechazados(chequesRechazados);
		recibo.setOtrosConceptos(otrosConceptos);
	}

	private static List<ReciboIngreso> getIngresos(int reciboId, int entidad)
			throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().getIngresos(reciboId, entidad);
	}

	public static void anularRecibo(int reciboId, User user, int entidad)
			throws SystemException, ReciboDerivadoException {
		if (ReciboNoOSServiceImpl.getInstance().verificarReciboDerivado(
				reciboId, entidad)) {
			throw new ReciboDerivadoException();
		}
		ReciboNoOSServiceImpl.getInstance().anular(reciboId,
				user.getScreenName(), new Date(), entidad);
	}

	public static List<ReciboIngreso> getEfectivosRecibidos(int entidad)
			throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().getEfectivosRecibidos(
				Efectivo.Estado.RECIBIDO, entidad);
	}

	public static void cambiarEstadoReciboIngreso(ReciboIngreso ri,
			Estado estado, String screenName, Connection con)
			throws SystemException {
		ReciboNoOSServiceImpl.getInstance().cambiarEstadoReciboEfectivo(ri,
				estado, screenName, con);
	}

	public static List<Recibo> getReporteRecibos(Date fechaIni, Date fechaFin,
			Empresa empresa, int entidad) throws SystemException, Exception {
		List<Recibo> recibos = ReciboNoOSServiceImpl.getInstance().get(
				fechaIni, fechaFin, empresa, entidad);
		List<Recibo> recibosAnulados = new ArrayList<Recibo>();
		Map<Recibo, List<ReciboConcepto>> conceptos = ReciboNoOSServiceImpl
				.getInstance().getConceptos(fechaIni, fechaFin, entidad);
		List<Recibo> ingresos = ReciboNoOSServiceImpl.getInstance()
				.getIngresos(fechaIni, fechaFin, entidad);

		for (Recibo rec : recibos) {
			List<ReciboConcepto> list = conceptos.get(rec);
			asignarConceptos(rec, list, entidad);

			int indexOf = ingresos.indexOf(rec);
			if (indexOf != -1) {
				Recibo recibo = ingresos.get(indexOf);
				rec.setIngresos(recibo.getIngresos());
			}

			if (rec.getBaja_fecha() != null) {
				Recibo reciboAnulado = new Recibo(rec);
				reciboAnulado.setFecha(rec.getBaja_fecha());
				rec.setBaja_fecha(null);
				recibosAnulados.add(reciboAnulado);
			}
		}
		recibos.addAll(recibosAnulados);
		return recibos;
	}

	public static List<ReciboIngreso> getAnticiposParaAplicar(Empresa empresa,
			String entidad) throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().getAnticiposParaAplicar(
				empresa, entidad);
	}

	public static List<ReporteAnticipos> getReporteAnticipos(Date fechaIni,
			Date fechaFin, Empresa empresa) throws SystemException {
		return ReciboNoOSServiceImpl.getInstance().getReporteAnticipos(
				fechaIni, fechaFin, empresa);
	}
}
