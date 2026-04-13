package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.global.services.CalculaCapitalCuotaServiceUtil;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Ingreso;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.liquidaciones.FechaBajaMenorQueAltaException;
import ar.com.ospim.procesaArchivos.beans.nacion.ListadoRendicionNacion;
import ar.com.ospim.procesaArchivos.beans.nacion.RendicionNacion;
import ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosServiceImpl;
import ar.com.ospim.tesoreria.DuplicateNumeroReciboException;
import ar.com.ospim.tesoreria.ReciboDerivadoException;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboCheque;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.convenios.service.ConvenioServiceUtil;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel.ReporteAnticipos;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ReciboServiceUtil {
	private static Log logger = LogFactoryUtil.getLog(ReciboServiceUtil.class);

	public static void save(Recibo recibo, boolean debaja, User user,
			int entidad) throws SystemException,
			DuplicateNumeroReciboException, DuplicateNumeroChequeException,
			FechaMenorACierreContableException, NumberFormatException,
			ParseException {

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (!user.getScreenName().equals("dschejtman") && recibo.getFecha().compareTo(fecha_cierre_periodo) <= 0) { //DIEGO SE SALVA DEL CONTROL
			throw new FechaMenorACierreContableException();
		}

		logger.debug("Guardando recibo");
		Connection con = null;
		Connection con2=null;
		try {
			con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			con2.setAutoCommit(false);
			ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			ReciboServiceImpl instance = ReciboServiceImpl.getInstance();
			
			// busco duplicados
			List<Recibo> recibos = instance.get(recibo.getNumero(), null, null,
					con, null, null, entidad);

			if (recibos != null && recibos.size() > 0) {
				for (Recibo r : recibos) {
					if (r.getBaja_fecha() == null) {
						throw new DuplicateNumeroReciboException();
					}
				}
			}

			// salvo el recibo
			String userName = user.getScreenName();
			int id = instance.save(recibo, debaja, userName, con, entidad);
			recibo.setId(id);

			// salvo ingresos
			if (recibo.getId() != 0 && recibo.getIngresos() != null) {
				for (ReciboIngreso ing : recibo.getIngresos()) {
					Ingreso ingreso = ing.getIngreso();
					if (recibo.getEmpresa() != null) {
						ingreso.setCuit(recibo.getEmpresa().getCuit());
					}
					int idReciboIng = ingreso.saveIngreso(instance, recibo,
							userName, con, entidad);
					ing.setId(idReciboIng);
				}
			}

			// salvo conceptos
			List<ReciboActa> actas = recibo.getActas();
			if (recibo.getId() != 0 && actas != null) {
				for (ReciboActa rActa : actas) {
					instance.save(recibo, rActa, userName, con, entidad);
				}
			}

			List<ReciboConvenio> convenios = recibo.getConvenios();
			if (recibo.getId() != 0 && convenios != null) {
				for (ReciboConvenio rConv : convenios) {
					instance.save(recibo, rConv, userName, con, entidad);
				}
			}

			if (recibo.getChequesNoDepositados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesNoDepositados()) {
					instance.saveChequeASustituir(rCheque, recibo, userName,
							con, entidad);
				}
			}

			if (recibo.getChequesRechazados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesRechazados()) {
					instance.saveChequeRechazadoASustituir(rCheque, recibo,
							userName, con, entidad);
				}
			}

			if (recibo.getOtrosConceptos() != null) {
				for (ReciboOtroConcepto oc : recibo.getOtrosConceptos()) {
					instance.save(oc, recibo, userName, con, entidad);
					//Grabar pagos empleadores
					if(oc.getBoletaNro()!=null && oc.getBoletaNro()>0){
						actualizaEmpleadores(recibo,oc,con,con2,servicio); 
					} //Fin registra pago Empleadores
					
				}
			}
			
			
            if (recibo.getReciboPrestamos() != null) {
				for (ReciboPrestamo pr : recibo.getReciboPrestamos()) {
					instance.save(pr, recibo, userName, con, entidad);
				}
			}

			logger.debug("Commiteando recibo");
			con.commit();
			con2.commit();
			logger.debug("Recibo guardado");
		} catch (SystemException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (SQLException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw new SystemException(e);
		} catch (DuplicateNumeroChequeException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (DuplicateNumeroReciboException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (Exception e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(con2);
		}

	}

	public static void update(Recibo recibo, boolean debaja, User user,
			int entidad) throws SystemException,
			DuplicateNumeroReciboException, DuplicateNumeroChequeException,
			FechaMenorACierreContableException, NumberFormatException,
			ParseException {

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (!user.getScreenName().equals("dschejtman") && recibo.getFecha().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		logger.debug("Guardando recibo");
		Connection con = null;
		Connection con2 = null;
		try {
			
			con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			con2.setAutoCommit(false);
			ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			ReciboServiceImpl instance = ReciboServiceImpl.getInstance();

			// busco duplicados
			List<Recibo> recibos = instance.get(recibo.getNumero(), null, null,
					con, null, null, entidad);

			if (recibos != null && recibos.size() > 0) {
				for (Recibo r : recibos) {
					if (r.getBaja_fecha() == null
							&& r.getId() != recibo.getId()) {
						throw new DuplicateNumeroReciboException();
					}
				}
			}

			// actualizo el recibo
			String userName = user.getScreenName();
			instance.update(recibo, debaja, userName, con, entidad);

			// actualizo ingresos
			// PRIMERO LOS BORRO...
			int result = instance.borrarIngresos(recibo.getId(), con, entidad);
			// LOS VUELVO A INSERTAR SI NO EXISTIAN MOVS BCRIOS.
			if (result == 1) {
				if (recibo.getId() != 0 && recibo.getIngresos() != null) {
					for (ReciboIngreso ing : recibo.getIngresos()) {
						Ingreso ingreso = ing.getIngreso();
						if (recibo.getEmpresa() != null) {
							ingreso.setCuit(recibo.getEmpresa().getCuit());
						}
						if (ing.getMovBcrioId() == 0) {
							int idReciboIng = ingreso.saveIngreso(instance,
									recibo, userName, con, entidad);

							ing.setId(idReciboIng);
						}
					}
				}
			}

			// actualizo conceptos
			// PRIMERO LOS BORRO...
			instance.borrarConceptos(recibo.getId(), con, entidad);
			
			//Borra Aportes
			instance.borrarConceptosAportes(recibo.getNumero(), con, entidad);
			instance.borrarConceptosAportes(recibo.getNumero(), con2, entidad);

			// salvo conceptos
			List<ReciboActa> actas = recibo.getActas();
			if (recibo.getId() != 0 && actas != null) {
				for (ReciboActa rActa : actas) {
					instance.save(recibo, rActa, userName, con, entidad);
				}
			}

			List<ReciboConvenio> convenios = recibo.getConvenios();
			if (recibo.getId() != 0 && convenios != null) {
				for (ReciboConvenio rConv : convenios) {
					instance.save(recibo, rConv, userName, con, entidad);
				}
			}

			if (recibo.getChequesNoDepositados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesNoDepositados()) {
					instance.saveChequeASustituir(rCheque, recibo, userName,
							con, entidad);
				}
			}

			if (recibo.getChequesRechazados() != null) {
				for (ReciboCheque rCheque : recibo.getChequesRechazados()) {
					instance.saveChequeRechazadoASustituir(rCheque, recibo,
							userName, con, entidad);
				}
			}

			if (recibo.getOtrosConceptos() != null) {
				for (ReciboOtroConcepto oc : recibo.getOtrosConceptos()) {
					instance.save(oc, recibo, userName, con, entidad);
					if(oc.getBoletaNro()!=null && oc.getBoletaNro()>0){
						actualizaEmpleadores(recibo,oc,con,con2,servicio); 
					}	
				}
			}

			if (recibo.getReciboPrestamos() != null) {
				for (ReciboPrestamo oc : recibo.getReciboPrestamos()) {
					instance.save(oc, recibo, userName, con, entidad);
				}
			}
			
			logger.debug("Commiteando recibo");
			con.commit();
			con2.commit();
			logger.debug("Recibo guardado");
		} catch (SystemException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (SQLException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw new SystemException(e);
		} catch (DuplicateNumeroChequeException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (DuplicateNumeroReciboException e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw e;
		} catch (Exception e) {
			manejarExcepcion(recibo, con, e);
			ConnectionHelper.rollback(con2);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(con2);
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
			String empresa, String cuil_titular, Integer inte, int entidad)
			throws SystemException {
		return ReciboServiceImpl.getInstance().get(actaNroStr, cuit, empresa,
				cuil_titular, inte, entidad);
		// (actaNroStr, cuit, empresa, amtima);
		// return ReciboServiceImpl.getInstance().get(actaNroStr, cuit, empresa,
		// amtima);
	}
	
	public static List<Recibo> get(String actaNroStr, String cuit,
			String empresa, String cuil_titular, Integer inte, int entidad,Integer id_amtima)
			throws SystemException {
		return ReciboServiceImpl.getInstance().get(actaNroStr, cuit, empresa,
				cuil_titular, inte, entidad,id_amtima);
		// (actaNroStr, cuit, empresa, amtima);
		// return ReciboServiceImpl.getInstance().get(actaNroStr, cuit, empresa,
		// amtima);
	}

	public static void getRecibosSeguimiento(String actaNroStr,
			String cuit, String empresa, PortletRequest request) throws SystemException {
		Connection con = ConnectionHelper.getConnection();	
		List<Recibo> lista = ReciboServiceImpl.getInstance().get(actaNroStr,
				cuit, empresa, con, null,null, WebKeysGlobal.OSPIM);
		lista.addAll(ReciboServiceImpl.getInstance().get(actaNroStr,
				cuit, empresa, con, null,null, WebKeysGlobal.UOMA));
		lista.addAll(ReciboServiceImpl.getInstance().get(actaNroStr,
				cuit, empresa, con, null,null, WebKeysGlobal.AMTIMA));

		Collections.sort(lista, new Comparator<Recibo>() {
			public int compare(Recibo o1, Recibo o2) {
				int compareTo = o2.getFecha().compareTo(o1.getFecha());
				if (compareTo == 0) {
					compareTo = o1.getEntidad().compareTo(o2.getEntidad());
				}
				return compareTo;
			}
		});
		
		PortletSession portletSession=request.getPortletSession();
		portletSession.removeAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS);
		portletSession.setAttribute(WebKeysTesoreria.BUSQUEDA_RECIBOS, lista);		
	}

	public static Recibo get(int id, int entidad) throws SystemException,
			Exception {
		Recibo recibo = ReciboServiceImpl.getInstance().get(id, entidad);

		if (recibo != null) {
			List<ReciboConcepto> conceptos = ReciboServiceImpl.getInstance()
					.getConceptos(id, entidad);

			asignarConceptos(recibo, conceptos, entidad);
			recibo.setIngresos(getIngresos(id, entidad));
		}

		return recibo;
	}

	private static void asignarConceptos(Recibo recibo,
			List<ReciboConcepto> conceptos, int entidad)
			throws SystemException, Exception {
		List<ReciboActa> actas = new ArrayList<ReciboActa>();
		List<ReciboConvenio> convenios = new ArrayList<ReciboConvenio>();
		List<ReciboCheque> chequesNoDepositados = new ArrayList<ReciboCheque>();
		List<ReciboCheque> chequesRechazados = new ArrayList<ReciboCheque>();
		List<ReciboOtroConcepto> otrosConceptos = new ArrayList<ReciboOtroConcepto>();
		List<ReciboPrestamo> prestamos = new ArrayList<ReciboPrestamo>();

		if (conceptos != null) {
			for (ReciboConcepto rc : conceptos) {
				if (rc instanceof ReciboActa) {

					ReciboActa reciboActa = (ReciboActa) rc;
					Acta acta = null;
					if (entidad == WebKeysGlobal.OSPIM) {
						acta = ActaServiceUtil.getActa(reciboActa.getActa()
								.getId(), recibo.getId());
					} else {
						acta = ActaNoOSServiceUtil.getActa(reciboActa.getActa()
								.getId(), recibo.getId());
					}
					acta.setTotalActaPagosChequeNoIngresados(reciboActa
							.getImportePorCheques());
					reciboActa.setActa(acta);
					// if(recibo.getId()>0){//ESTO POR QUE? ROMPE EL REPORTE...
					// reciboActa.setImportePorCheques(acta.getTotalPagadoIngresado());
					// }
					acta.setTotalActaPagosChequeNoIngresados(reciboActa
							.getImportePorCheques());
					actas.add(reciboActa);
				} else if (rc instanceof ReciboConvenio) {
					Convenio conv = null;
					ReciboConvenio reciboConvenio = (ReciboConvenio) rc;
					if (entidad == WebKeysGlobal.OSPIM) {
						conv = ConvenioServiceUtil.getConvenio(reciboConvenio
								.getConvenio().getId(), recibo.getId());
					} else {
						conv = ConvenioNoOSServiceUtil.getConvenio(
								reciboConvenio.getConvenio().getId(),
								recibo.getId(), entidad);
					}
					reciboConvenio.setConvenio(conv);
					// if(recibo.getId()>0){//ESTO POR QUE? ROMPE EL REPORTE...
					// reciboConvenio.setImportePorCheques(conv.getTotalPagadoIngresado());
					// }
					conv.setTotalConvenioPagosChequeNoIngresados(reciboConvenio
							.getImportePorCheques());

					convenios.add(reciboConvenio);
				} else if (rc instanceof ReciboCheque) {
					ReciboCheque reciboCheque = (ReciboCheque) rc;
					Cheque chequeIngreso = reciboCheque.getChequeASustituir();
					Cheque ch = ChequeServiceUtil.getCheques(chequeIngreso,
							entidad).get(0);
					reciboCheque.setChequeASustituir(ch);
					if (reciboCheque.getTipo().equals(
							ReciboCheque.Tipo.RECHAZADO)) {
						chequesRechazados.add(reciboCheque);
					} else {
						chequesNoDepositados.add(reciboCheque);
					}
				} else if (rc instanceof ReciboOtroConcepto) {
					otrosConceptos.add((ReciboOtroConcepto) rc);
				}else if (rc instanceof ReciboPrestamo) {
					prestamos.add((ReciboPrestamo) rc);
				}
			}
		}

		recibo.setActas(actas);
		recibo.setConvenios(convenios);
		recibo.setChequesNoDepositados(chequesNoDepositados);
		recibo.setChequesRechazados(chequesRechazados);
		recibo.setOtrosConceptos(otrosConceptos);
		recibo.setReciboPrestamos(prestamos);
	}

	private static List<ReciboIngreso> getIngresos(int reciboId, int entidad)
			throws SystemException {
		return ReciboServiceImpl.getInstance().getIngresos(reciboId, entidad);
	}

	public static void anularRecibo(int reciboId, User user, Date fechaBaja,
			int entidad) throws SystemException, ReciboDerivadoException,
			FechaBajaMenorQueAltaException, FechaMenorACierreContableException {
		if (ReciboServiceImpl.getInstance().verificarReciboDerivado(reciboId,
				entidad)) {
			throw new ReciboDerivadoException();
		}
		Recibo recibo = ReciboServiceImpl.getInstance().get(reciboId, entidad);

		if (recibo.getFecha().compareTo(fechaBaja) > 0) {
			throw new FechaBajaMenorQueAltaException();
		}
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (!user.getScreenName().equals("dschejtman") && fechaBaja.compareTo(fecha_cierre_periodo) < 0
				|| (recibo.getBaja_fecha() != null && recibo.getBaja_fecha()
						.compareTo(fecha_cierre_periodo) < 0)) {
			throw new FechaMenorACierreContableException();
		}

		ReciboServiceImpl.getInstance().anular(reciboId, user.getScreenName(),
				fechaBaja, entidad);
		
		//Borra Aportes --
		Connection con = null;
		Connection con2 = null;
		try {
			
			con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			con2.setAutoCommit(false);
			ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			ReciboServiceImpl.getInstance().borrarConceptosAportes(recibo.getNumero(), con, entidad);
			ReciboServiceImpl.getInstance().borrarConceptosAportes(recibo.getNumero(), con2, entidad);
			con.commit();
			con2.commit();
		}catch(Exception e) {
			ConnectionHelper.rollback(con);
			ConnectionHelper.rollback(con2);
			
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(con2);
		}
		//Fin Borra Aportes
		
	}

	public static void reactivarRecibo(int reciboId, User user, int entidad)
			throws SystemException, ReciboDerivadoException,
			FechaBajaMenorQueAltaException, FechaMenorACierreContableException {
		if (ReciboServiceImpl.getInstance().verificarReciboDerivado(reciboId,
				entidad)) {
			throw new ReciboDerivadoException();
		}
		Recibo recibo = ReciboServiceImpl.getInstance().get(reciboId, entidad);
		
		// Necesario para Volver a crear Aportes si los hubiera
		List<ReciboConcepto> conceptos = ReciboServiceImpl.getInstance().getConceptos(reciboId, entidad);
		try {
			asignarConceptos(recibo, conceptos, entidad);
		} catch (SystemException e1) {
			
		} catch (Exception e1) {
			
		}
		
				

		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (!user.getScreenName().equals("dschejtman") && recibo.getFecha().compareTo(fecha_cierre_periodo) < 0
				|| (recibo.getBaja_fecha() != null && recibo.getBaja_fecha()
						.compareTo(fecha_cierre_periodo) < 0)) {
			throw new FechaMenorACierreContableException();
		}

		ReciboServiceImpl.getInstance().reactivar(reciboId,
				user.getScreenName(), entidad);

		
		//Vuelvo a crear los Aportes
		if (recibo.getOtrosConceptos() != null) {
			Connection con = null;
			Connection con2 = null;
			try {
				
				con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
				con2.setAutoCommit(false);
				ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
				
				con = ConnectionHelper.getConnection();
				con.setAutoCommit(false);
			    for (ReciboOtroConcepto oc : recibo.getOtrosConceptos()) {
				    if(oc.getBoletaNro()!=null && oc.getBoletaNro()>0){
					    actualizaEmpleadores(recibo,oc,con,con2,servicio); 
				    }	
			    }
			    con.commit();
				con2.commit();
			}catch(Exception e) {
				ConnectionHelper.rollback(con);
				ConnectionHelper.rollback(con2);
				
			} finally {
				ConnectionHelper.cerrar(con);
				ConnectionHelper.cerrar(con2);
			}   
		}
		//Fin Aportes
		
	}

	public static List<ReciboIngreso> getEfectivosRecibidos(int entidad)
			throws SystemException {
		return ReciboServiceImpl.getInstance().getEfectivosRecibidos(
				Efectivo.Estado.RECIBIDO, entidad);
	}

	public static void cambiarEstadoReciboIngreso(ReciboIngreso ri,
			Estado estado, String screenName, Connection con, int entidad)
			throws SystemException {
		ReciboServiceImpl.getInstance().cambiarEstadoReciboEfectivo(ri, estado,
				screenName, con, entidad);
	}

	public static List<Recibo> getReporteRecibos(Date fechaIni, Date fechaFin,
			Empresa empresa, boolean filtrar_0001, boolean filtrar_0002,
			boolean filtrar_0003, boolean filtrar_rend, boolean filtrar_bcap,
			boolean filtrar_otro, int entidad) throws SystemException,
			Exception {
		List<Recibo> recibos = ReciboServiceImpl.getInstance().get(fechaIni,
				fechaFin, empresa, filtrar_0001, filtrar_0002, filtrar_0003,
				filtrar_rend, filtrar_bcap, filtrar_otro, entidad);
		List<Recibo> recibosAnulados = new ArrayList<Recibo>();
		Map<Recibo, List<ReciboConcepto>> conceptos = ReciboServiceImpl
				.getInstance().getConceptos(fechaIni, fechaFin, entidad);
		List<Recibo> ingresos = ReciboServiceImpl.getInstance().getIngresos(
				fechaIni, fechaFin, entidad);

		for (Recibo rec : recibos) {
			List<ReciboConcepto> list = conceptos.get(rec);
			asignarConceptos(rec, list, entidad);

			int indexOf = ingresos.indexOf(rec);
			if (indexOf != -1) {
				Recibo recibo = ingresos.get(indexOf);
				rec.setIngresos(recibo.getIngresos());
			}
			// ESTO
			if (rec.getBaja_fecha() != null
					&& rec.getBaja_fecha().compareTo(rec.getFecha()) > 0) {
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
			int entidad) throws SystemException {
		return ReciboServiceImpl.getInstance().getAnticiposParaAplicar(empresa,
				entidad);
	}

	public static List<ReporteAnticipos> getReporteAnticipos(Date fechaIni,
			Date fechaFin, Empresa empresa) throws SystemException {
		return ReciboServiceImpl.getInstance().getReporteAnticipos(fechaIni,
				fechaFin, empresa);
	}

	public static String getNumeroReciboSugerido(String pre, int entidad)
			throws SystemException {
		return ReciboServiceImpl.getInstance().getNumeroReciboSugerido(pre,
				entidad);
	}
	
	private static void actualizaEmpleadores(Recibo recibo,ReciboOtroConcepto oc,
			Connection con,Connection con2,ProcesaArchivosServiceImpl servicio) 
					throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			ListadoRendicionNacion nuevoArchivo = new ListadoRendicionNacion();
			List<RendicionNacion> detalleList = new ArrayList<RendicionNacion>();
			int codEntidad=0;
			String boletaEmpleadores="";
			String fechaRendStr="";
			Date fechaRendicion=null;
			Integer nroSecuenciaDDJJ=0;
			Integer tipoBoleta=WebKeysTesoreria.PORTAL_EMPLEADORES_EQUIVALENCIA_CONCEPTOS.get(oc.getConcepto().getId());
			if(CalculaCapitalCuotaServiceUtil.SOCIAL==tipoBoleta) {
      			   codEntidad=5783;
      			   boletaEmpleadores="CUOTA_SOC_UOMA";
      		}else if(CalculaCapitalCuotaServiceUtil.SOLIDARIO==tipoBoleta) {
       			   codEntidad=5784;
       			   boletaEmpleadores="APORTE_SOL_UOMA";
      		}else if(CalculaCapitalCuotaServiceUtil.USUFRUCTO==tipoBoleta) {
       			   codEntidad=5783;
       			   boletaEmpleadores="CUOTA_USUFRUCTO";
      		}else if(CalculaCapitalCuotaServiceUtil.ART_46==tipoBoleta) {
       			   codEntidad=5785;
       			   boletaEmpleadores="ART_46";
      		}else if(CalculaCapitalCuotaServiceUtil.AMTIMA==tipoBoleta) {
				codEntidad=5652;
				boletaEmpleadores="CUOTA_AMTIMA";
      		}
			nroSecuenciaDDJJ= oc.getNroSecuenciaDDJJ();
			
			RendicionNacion rendi = new RendicionNacion();
			Date fecha = null;
			try {
			   fecha=recibo.getIngresos().get(0).getIngreso().getFecha();
			} catch(Exception e) {
			   fecha=recibo.getFecha();	
			}
			rendi.setFecha_recauda(fecha); 
			rendi.setFecha_rendicion(recibo.getFecha());
			rendi.setImporte(oc.getTotalBoleta());
            rendi.setEnte(BigInteger.valueOf(codEntidad));
            rendi.setPeriodo_cod_barras(oc.getPeriodo());
            rendi.setCod_movimiento(String.valueOf(oc.getConcepto().getId()));
            Integer nmov = 0;
            try {
              nmov= Integer.parseInt(recibo.getIngresos().get(0).getIngreso().getNumeroStr());	
            }catch(Exception e){
              String nb= oc.getBoletaNro().toString();	
              nmov=	Integer.parseInt(sdf.format(oc.getPeriodo())+tipoBoleta.toString()+
            		  nb.substring(nb.length()-3));
            }
            rendi.setNro_movimiento(nmov);
            rendi.setCuit(recibo.getEmpresa().getCuit());
            rendi.setNro_boleta_portal_emple(oc.getBoletaNro());
            rendi.setNro_dec_portal_emple(nroSecuenciaDDJJ);
              
            rendi.setTipo_boleta(tipoBoleta);
            rendi.setEstado_cheque("");
            rendi.setCod_barras("Recibo "+ recibo.getNumero());
              
            detalleList.add(rendi);
			nuevoArchivo.setDetalle(detalleList);
			
			servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con, false);
			servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con2, true);
	}
	
	public static String proximoNumeroDisponible(int entidad) throws SystemException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
		Date d = new Date();
	    List<Recibo> recibos = get(sdf.format(d) , null,null, null, null, entidad);
	    String aux="";
	    Integer max=0;
	    Integer evalua=0;
	    for(Recibo r:recibos) {
	    	try {
	    	   aux=r.getNumero();	
	    	   evalua=Integer.parseInt(aux.replace(sdf.format(d), ""));
	    	}catch(Exception e) {
	    		
	    	}
	    	if(max<evalua) {
	    		max=evalua;
	    	}
	    }
	    
	    return sdf.format(d)+String.format("%03d",max+1); 
	    
	}
}
