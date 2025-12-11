package ar.com.ospim.tesoreria.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimiento;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.ActaConReciboException;
import ar.com.ospim.tesoreria.ActaRelacionadaException;
import ar.com.ospim.tesoreria.DuplicateActaIdException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.actas.action.InspectorWrapper;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Acta.ActaPagoIngresado;
import ar.com.ospim.tesoreria.beans.Acta.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.Acta.DetalleActaInspectores;
import ar.com.ospim.tesoreria.beans.ActaEstadoSeguimiento;
import ar.com.ospim.tesoreria.beans.ActaPago;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.CalculoDeudaMasivoCab;
import ar.com.ospim.tesoreria.beans.Inspector;
import ar.com.ospim.tesoreria.beans.ReporteActaBean;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaActaBean;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;

public class ActaServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(ActaServiceUtil.class);

	public static void save(Acta acta, User user,
			List<InspectorWrapper> inspectoresWrapper, boolean cerrarActa)
			throws Exception {
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
		if (acta.getCierre_fecha().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		Connection connection = null;
		boolean actaRepetida = false;
		try {
			if (acta.getNumero() != null && !acta.getNumero().equals("")) {
//				List<Acta> actas = ActaServiceImpl.getInstance().getActas(
//						acta.getNumero(), acta.getEmpresa().getCuit(), null);
				List<Acta> actas = ActaServiceImpl.getInstance().getActas(
						acta.getNumero(), acta.getEmpresa()!=null?acta.getEmpresa().getCuit():null, null,null);
				if (actas != null && !actas.isEmpty()) {
					actaRepetida = true;
					throw new DuplicateActaIdException();
				}
			}

			if (acta.getPagos() != null && acta.getPagos().size() > 0) {
				for (ActaPago ap : acta.getPagos()) {
					if (ap.getIngreso() != null
							&& (ap.getIngreso() instanceof Cheque)) {
//						List<Cheque> cheques = ChequeServiceUtil.getCheques(
//								(Cheque) ap.getIngreso(), WebKeysGlobal.OSPIM);
//						if (cheques != null && !cheques.isEmpty()) {
//							throw new DuplicateNumeroChequeException(
//									(Cheque) ap.getIngreso());
//						}
						Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro((Cheque) ap.getIngreso(), WebKeysGlobal.OSPIM);
						if (chequeDuplicado != null && ap.getId() == 0) {
							throw new DuplicateNumeroChequeException(chequeDuplicado);
						}
					}
				}
			}

			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);

			if (null != acta.getEmpresa()
					&& acta.getEmpresa().getCuentasBcrias() != null
					&& acta.getEmpresa().getCuentasBcrias().size() > 0) {
				EmpresaServiceUtil.saveCuentasBancaria(acta.getEmpresa(),
						user.getScreenName(), connection);

			}
			
			int id = ActaServiceImpl.getInstance().save(acta,
					user.getScreenName(), connection);
			acta.setId(id);
			if (inspectoresWrapper != null) {
				for (InspectorWrapper inspector : inspectoresWrapper) {
					ActaServiceImpl.getInstance().saveInspectorFirmante(
							acta.getId(), inspector.getId(), connection);
				}
			}

			List<DetalleActaInspectores> detalles = acta.getDetallesActas();
			if (detalles != null) {
				for (DetalleActaInspectores detalle : detalles) {
					ActaServiceImpl.getInstance().saveDetalleActa(acta,
							detalle, connection);
				}
			}

			List<ActaRelacionada> actas = acta.getActasRelacionadas();
			if (actas != null) {
				for (ActaRelacionada actaRel : actas) {
					ActaServiceImpl.getInstance().saveActaRelacionada(actaRel,
							user.getScreenName(), connection);
				}
			}

			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris != null) {
				for (ActaPeriodoDeudaEmpresa peri : peris) {
					ActaServiceImpl.getInstance().saveActaPeriodo(acta, peri,
							user.getScreenName(), connection);
				}
			}

			List<ActaPago> pagos = acta.getPagos();
			if (pagos != null) {
				for (ActaPago p : pagos) {
					if (p.getIngreso() != null
							&& (p.getIngreso() instanceof Cheque)) {
						Cheque cheque = new Cheque((Cheque) p.getIngreso());
						cheque.setCuit(acta.getEmpresa().getCuit()); //aunque se a de terceros en nuestro sistema se cargan a la empresa
						EmpresaServiceUtil.saveCuentaBancaria(cheque.getCuit(),"000", cheque.getCuentaBancaria(), user.getScreenName(), connection); 
						ChequeServiceUtil.save(cheque, user.getScreenName(),
								connection, WebKeysGlobal.OSPIM);
					}
					ActaServiceImpl.getInstance().saveActaPago(acta, p,
							user.getScreenName(), connection);
				}
			}

			if (cerrarActa) {
				ActaServiceImpl.getInstance().cerrarActa(acta,
						user.getScreenName(), connection);
			}

			connection.commit();
		} catch (Exception e) {
			if (null != connection) {
				ConnectionHelper.rollback(connection);
				throw e;
			}
		} finally {
			if (connection != null) {
				ConnectionHelper.cerrar(connection);
			}
			if(actaRepetida){
				throw new DuplicateActaIdException();
			}
		}
	}

	public static void update(Acta acta, User user,
			List<InspectorWrapper> inspectoresWrapper, boolean cerrarActa)
			throws Exception {

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
		if (acta.getCierre_fecha().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		if (acta.getPagos() != null && acta.getPagos().size() > 0) {
			for (ActaPago ap : acta.getPagos()) {
				if (ap.getIngreso() != null
						&& (ap.getIngreso() instanceof Cheque)) {
//					List<Cheque> cheques = ChequeServiceUtil.getCheques(
//							(Cheque) ap.getIngreso(), WebKeysGlobal.OSPIM);
//					if (ap.getId() == 0 && cheques != null
//							&& !cheques.isEmpty()) {
//						throw new DuplicateNumeroChequeException(
//								(Cheque) ap.getIngreso());
//					}
					Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro((Cheque) ap.getIngreso(), WebKeysGlobal.OSPIM);
					if (chequeDuplicado != null && ap.getId() == 0) {
						throw new DuplicateNumeroChequeException(chequeDuplicado);
					}
				}
			}
		}
		Connection connection = null;
		try {
			connection = ConnectionHelper.getConnectionForTransaction();
			ActaServiceImpl.getInstance().update(acta, user.getScreenName(),
					connection);

			if (inspectoresWrapper != null) {
				for (InspectorWrapper inspector : inspectoresWrapper) {
					if (inspector.isRecienAgregado()) {
						ActaServiceImpl.getInstance().saveInspectorFirmante(
								acta.getId(), inspector.getId(), connection);
					}
					if (!inspector.isRecienAgregado()
							&& inspector.isBorradoLogico()) {
						ActaServiceImpl.getInstance().borrarInspectorFirmante(
								acta.getId(), inspector.getId(), connection);
					}
				}
			}

			List<DetalleActaInspectores> detalles = acta.getDetallesActas();
			if (detalles != null) {
				for (DetalleActaInspectores detalle : detalles) {
					if (detalle.getId() <= 0) {
						ActaServiceImpl.getInstance().saveDetalleActa(acta,
								detalle, connection);
					} else {
						if (detalle.isBorradoLogico()) {
							ActaServiceImpl.getInstance().deleteDetalleActa(
									detalle, connection);
						}
					}
				}
			}

			List<ActaRelacionada> actas = acta.getActasRelacionadas();
			if (actas != null) {
				for (ActaRelacionada actaRel : actas) {
					if (actaRel.isBorradoLogico()) {
						ActaServiceImpl.getInstance().deleteActaRelacionada(
								actaRel, connection);
					} else {
						if (actaRel.getId() == 0) {
							ActaServiceImpl.getInstance().saveActaRelacionada(
									actaRel, user.getScreenName(), connection);
						} else {
							ActaServiceImpl.getInstance()
									.updateActaRelacionada(actaRel,
											user.getScreenName(), connection);
						}
					}
				}
			}

			List<ActaPeriodoDeudaEmpresa> peris = acta.getPeriodos();
			if (peris != null) {
				for (ActaPeriodoDeudaEmpresa peri : peris) {
					ActaServiceImpl.getInstance().deleteActaPeriodo(peri,
							connection);
					if (peri.getDetalle().get(0).getId() <= 0) {
						ActaServiceImpl.getInstance().saveActaPeriodo(acta,
								peri, user.getScreenName(), connection);
					} else {
						ActaServiceImpl.getInstance().updateActaPeriodo(acta,
								peri, user.getScreenName(), connection);
					}
				}
			}

			List<ActaPago> pagos = acta.getPagos();
			if (pagos != null) {
				for (ActaPago p : pagos) {
					if (p.isBorradoLogico()) {
						ActaServiceImpl.getInstance().deleteActaPago(p,
								user.getScreenName(), connection);
					} else {
						if (p.getId() == 0) {
							if (p.getIngreso() != null
									&& (p.getIngreso() instanceof Cheque)) {
								Cheque cheque = new Cheque(
										(Cheque) p.getIngreso());
								
								EmpresaServiceUtil.saveCuentaBancaria(cheque.getCuit(),"000", cheque.getCuentaBancaria(), user.getScreenName(), connection); 
								cheque.setCuit(acta.getEmpresa().getCuit()); //aunque se a de terceros en nuestro sistema se cargan a la empresa
								ChequeServiceUtil.save(cheque,
										user.getScreenName(), connection,
										WebKeysGlobal.OSPIM);
							}
							ActaServiceImpl.getInstance().saveActaPago(acta, p,
									user.getScreenName(), connection);
						} else {
							ActaServiceImpl.getInstance().updateActaPago(acta,
									p, user.getScreenName(), connection);
						}
					}
				}
			}

			if (cerrarActa) {
				ActaServiceImpl.getInstance().cerrarActa(acta,
						user.getScreenName(), connection);
			}
			connection.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
			throw e;
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	public static Acta getActa(int id, int reciboId) {
		Acta acta = ActaServiceImpl.getInstance().get(id);
		if (acta != null) {
			List<Inspector> inspectoresFirmantes = ActaServiceImpl
					.getInstance().getInspectoresFirmantes(id);
			if (inspectoresFirmantes != null) {
				acta.setInspectoresFirmantes(inspectoresFirmantes);
			}
			List<DetalleActaInspectores> detallesActas = ActaServiceImpl
					.getInstance().getDetallesActas(id);
			if (detallesActas != null && !detallesActas.isEmpty()) {
				acta.setDetallesActas(detallesActas);
				acta.setInspector(true);
			}
			List<ActaRelacionada> actasRelacionadas = ActaServiceImpl
					.getInstance().getActasRelacionadas(id);
			if (actasRelacionadas != null) {
				acta.setActasRelacionadas(actasRelacionadas);
			}
			List<ActaPeriodoDeudaEmpresa> periodos = ActaServiceImpl
					.getInstance().getPeriodosActas(id);
			if (periodos != null) {
				acta.setPeriodos(periodos);
			}
			List<ActaPago> pagos = ActaServiceImpl.getInstance().getPagosActas(
					id);
			if (pagos != null) {
				acta.setPagos(pagos);
			}
			List<ActaPagoIngresado> pagosIngresados = ActaServiceImpl
					.getInstance().getPagosIngresados(id, reciboId);
			if (pagosIngresados != null) {
				acta.setPagosIngresados(pagosIngresados);
			}
		}
		return acta;
	}

	public static List<Acta> getActas(String actaNro, String cuit,
			String empresa) {
		return ActaServiceImpl.getInstance().getActas(actaNro, cuit, empresa,
				null);
	}

	public static void getDeudasSeguimiento(String cuit,
			PortletRequest renderRequest) {
		Connection con = null;
		PortletSession portletSession = renderRequest.getPortletSession();
		try {
			con = ConnectionHelper.getConnection();
			List<Acta> lista = getDeudaSeguimiento(cuit, null, con);

			Collections.sort(lista, new Comparator<Acta>() {
				public int compare(Acta o1, Acta o2) {
					int compareTo = o2.getFechaInicio().compareTo(
							o1.getFechaInicio());
					if (compareTo == 0) {
						compareTo = o1.getEntidad().compareTo(o2.getEntidad());
					}
					return compareTo;
				}
			});
			portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS);
			portletSession
					.setAttribute(WebKeysTesoreria.BUSQUEDA_DEUDAS, lista);
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	public static List<Acta> getActasSeguimiento(String actaNro, String cuit,
			String empresa) {
		Connection con = null;
		List<Acta> lista = null;
		try {
			con = ConnectionHelper.getConnection();

			lista = ActaServiceImpl.getInstance().getActas(actaNro, cuit,
					empresa, con);
			lista.addAll(ActaNoOSServiceImpl.getInstance().getActas(null,
					actaNro, cuit, empresa, null, con));

			Collections.sort(lista, new Comparator<Acta>() {
				public int compare(Acta o1, Acta o2) {
					int compareTo = o2.getFechaInicio().compareTo(
							o1.getFechaInicio());
					if (compareTo == 0) {
						compareTo = o1.getEntidad().compareTo(o2.getEntidad());
					}
					return compareTo;
				}
			});
		} finally {
			ConnectionHelper.cerrar(con);
		}
		return lista;
	}

	public static List<Acta> getDeuda(String cuit, String empresa) {
		return ActaServiceImpl.getInstance().getDeuda(cuit, empresa, null);
	}

	public static List<Acta> getDeudaSeguimiento(String cuit, String empresa,
			Connection con) {
		_log.debug("Pasando por aca al solicitar Deuda");
		List<Acta> lista = ActaServiceImpl.getInstance().getDeuda(cuit,
				empresa, con);
		lista.addAll(ActaNoOSServiceImpl.getInstance().getDeuda(cuit, empresa,
				null, con));

		Collections.sort(lista, new Comparator<Acta>() {
			public int compare(Acta o1, Acta o2) {
				int compareTo = o2.getFechaInicio().compareTo(
						o1.getFechaInicio());
				if (compareTo == 0) {
					compareTo = o1.getEntidad().compareTo(o2.getEntidad());
				}
				return compareTo;
			}
		});

		return lista;
	}

	public static void borrar(Integer actaId, Date fechaBaja, User user)
			throws Exception {

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
		if (fechaBaja.compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		Connection con = null;
		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			Acta acta = getActa(actaId, 0);
			if (acta.isActaCerrada()) {
				if (isActaRelacionada(actaId, con)) {
					throw new ActaRelacionadaException();
				}
				if (isActaConRecibo(actaId, con)) {
					throw new ActaConReciboException();
				}

				ActaServiceImpl.getInstance().pasarACalculo(actaId, fechaBaja,
						user.getScreenName(), con);

			} else {
				ActaServiceImpl.getInstance().borrar(actaId, fechaBaja,
						user.getScreenName(), con);
			}
			con.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);
		}
	}

	private static boolean isActaConRecibo(Integer actaId, Connection con)
			throws Exception {
		return ActaServiceImpl.getInstance().isActaConRecibo(actaId, null);
	}

	public static boolean isActaRelacionada(int actaId) throws Exception {
		return ActaServiceImpl.getInstance().isActaRelacionada(actaId, null);
	}

	public static boolean isActaRelacionada(int actaId, Connection con)
			throws Exception {
		return ActaServiceImpl.getInstance().isActaRelacionada(actaId, con);
	}

	public static ActaRelacionada getActaARelacionar(Acta actaOriginal,
			String actaARelacionarNro) {
		ActaRelacionada ar = null;
		List<Acta> actas = ActaServiceImpl.getInstance().getActas(
				actaARelacionarNro, null, null, null);
		Acta acta = null;
		if (actas != null && actas.size() > 0) {
			acta = ActaServiceUtil.getActa(actas.get(0).getId(), 0);
		}
		if (acta != null) {
			ar = new ActaRelacionada();
			ar.setActaRelacionada(acta);
			ar.setActa(actaOriginal);
			ar.setImporte(acta.getTotal()
					.subtract(acta.getTotalPagadoIngresado())
					.subtract(acta.getTotalPagadoPorConvenioYActas()));
		}
		return ar;
	}

	public static List<Acta> getActasSinRecibo(String cuit) {
		List<Acta> ar = ActaServiceImpl.getInstance().getActasSinRecibo(cuit);
		return ar;
	}

	public static List<ReporteActaBean> reporteActas(Date fechaIni,
			Date fechaFin, int entidad) {
		return ActaServiceImpl.getInstance().reporteActas(fechaIni, fechaFin,
				entidad);
	}

	public static List<ReporteCobranzaActaBean> reporteCobranzaActas(
			Date fechaIni, Date fechaFin) {
		return ActaServiceImpl.getInstance().reporteCobranzaActas(fechaIni,
				fechaFin);
	}

	public static void buscaActaAcuerdoSeguimiento(String cuit,
			PortletRequest renderRequest) {
		int entidad = ParamUtil.getInteger(renderRequest, "entidad");
		String tipo = ParamUtil.getString(renderRequest, "tipo");
		List<ActaAcuerdoSeguimiento> lista = ActaServiceImpl.getInstance()
				.buscaActaAcuerdoSeguimiento(cuit);
		List<ActaAcuerdoSeguimiento> listaResult = new ArrayList<ActaAcuerdoSeguimiento>();
		if (entidad!=0) {
			for (ActaAcuerdoSeguimiento acta : lista) {
				if (acta.getTipo().equals(tipo)) {
					if (acta.getEntidad()==entidad) {
						listaResult.add(acta);
					}
				}
			}
		} else {
			listaResult=lista;
		}

		PortletSession portletSession = renderRequest.getPortletSession();
		portletSession
				.removeAttribute(WebKeysEstudioIsidro.ACTAS_ACUERDO_SEGUIMIENTO);
		portletSession.setAttribute(
				WebKeysEstudioIsidro.ACTAS_ACUERDO_SEGUIMIENTO, listaResult);

	}
	
	public static List<ActaEstadoSeguimiento> getEstadosSeguimientoActas(){
		
//		FIXME: hacer service que invoque una tabla con dichos valores
		
		List<ActaEstadoSeguimiento> lista = new ArrayList<ActaEstadoSeguimiento>();
		ActaEstadoSeguimiento aes1 = new ActaEstadoSeguimiento(1, "Intimación");
		ActaEstadoSeguimiento aes2 = new ActaEstadoSeguimiento(2, "Con Cert. Deuda");
		ActaEstadoSeguimiento aes3 = new ActaEstadoSeguimiento(3, "Concurso");
		ActaEstadoSeguimiento aes4 = new ActaEstadoSeguimiento(4, "Quiebra");
		ActaEstadoSeguimiento aes5 = new ActaEstadoSeguimiento(5, "Ejecución");
		ActaEstadoSeguimiento aes6 = new ActaEstadoSeguimiento(6, "Reemplazado");
		ActaEstadoSeguimiento aes7 = new ActaEstadoSeguimiento(7, "Impugnado");
		
		lista.add(aes1);
		lista.add(aes2);
		lista.add(aes3);
		lista.add(aes4);
		lista.add(aes5);
		lista.add(aes6);
		lista.add(aes7);
		
		return lista;
	}
	
	public static boolean actualizaEstadoSeguimientoActa(int actaId, int estadoSegId, String usr) throws Exception{
		
		return ActaServiceImpl.getInstance().actualizaEstadoSeguimientoActa(actaId, estadoSegId, usr);
		
	}
	
	public static boolean actualizaEstadoSeguimientoActaNoOS(int actaId, int estadoSegId, String usr) throws Exception{
		
		return ActaServiceImpl.getInstance().actualizaEstadoSeguimientoActaNoOS(actaId, estadoSegId, usr);
		
	}
	
	public static List<CalculoDeudaMasivoCab> getProcesosCalculoDeudaMasivo(){
		
		return ActaServiceImpl.getInstance().traerProcesosCalculoDeudaMasivo();
	}
	
}
