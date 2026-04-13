package ar.com.ospim.tesoreria.convenios.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.estudioisidro.beans.ConvenioPagosReporte;
import ar.com.ospim.global.ExisteReciboConvenioException;
import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.services.ChequeServiceUtil;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.DuplicateConvenioIdException;
import ar.com.ospim.tesoreria.FaltanCuotasConvenioException;
import ar.com.ospim.tesoreria.ImposibleBorrarConvenioException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.ReporteCobranzaConvenioBean;
import ar.com.ospim.tesoreria.beans.ReporteConvenioBean;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ActaRelacionada;
import ar.com.ospim.tesoreria.beans.convenio.Convenio.ConvenioPagoIngresado;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioEstadoSeguimiento;
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.tesoreria.service.ActaServiceImpl;
import ar.com.ospim.tesoreria.service.ActaServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.conveniosNoOS.service.ConvenioNoOSServiceImpl;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ConvenioServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(ConvenioServiceUtil.class);
	
	public static List<Convenio> getConvenios(String convenioNro, String cuit,
			String empresa) {
		return ConvenioServiceImpl.getInstance().getConvenios(convenioNro,
				cuit, empresa);
	}
	
	public static List<Convenio> getConveniosSeguimiento(String convenioNro, String cuit,
			String empresa) {
		List<Convenio> lista=ConvenioServiceImpl.getInstance().getConvenios(convenioNro,
				cuit, empresa);
		
		lista.addAll(ConvenioNoOSServiceImpl.getInstance().getConvenios(convenioNro, cuit, empresa,null));
		
		Collections.sort(lista, new Comparator<Convenio>() {
			public int compare(Convenio o1,
					Convenio o2) {
				int compareTo = o2.getFechaInicio().compareTo(o1.getFechaInicio());
				if (compareTo == 0) {
					compareTo = o1.getEntidad().compareTo(o2.getEntidad());
				}
				return compareTo;
			}
		});
		
		return lista;
	}

	public static Convenio getConvenio(int id, int reciboId) {
		Convenio convenio = ConvenioServiceImpl.getInstance().get(id);
		List<ConvenioPago> pagos = ConvenioServiceImpl.getInstance()
				.getPagosConvenios(id);
		if (pagos != null) {
			convenio.setPagos(pagos);
		}
		List<ActaRelacionada> actas = ConvenioServiceImpl.getInstance()
				.getActasRelacionadas(id);
		if (pagos != null) {
			convenio.setActasRelacionadas(actas);
		}
		List<ConvenioPagoIngresado> pagosIngresados = ConvenioServiceImpl
				.getInstance().getPagosIngresados(id, reciboId);
		if (pagosIngresados != null) {
			convenio.setPagosIngresados(pagosIngresados);
		}
		return convenio;
	}

	public static void save(Convenio convenio, User user)
			throws SystemException, DuplicateConvenioIdException, SQLException,
			DuplicateNumeroChequeException, FaltanCuotasConvenioException,
			NumberFormatException, ParseException,
			FechaMenorACierreContableException, DuplicateEmpresaIdException {

		Connection con = null;
		
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
		if (convenio.getFechaInicio().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		if (!verificarCuotas(convenio)) {
			throw new FaltanCuotasConvenioException();
		}

		if (convenio.getPagos() != null && convenio.getPagos().size() > 0) {
			for (ConvenioPago cp : convenio.getPagos()) {
				if (cp.getCheque() != null) {
//					List<Cheque> cheques = ChequeServiceUtil.getCheques(cp
//							.getCheque(), WebKeysGlobal.OSPIM);
//					if (cheques != null && !cheques.isEmpty()) {
//						throw new DuplicateNumeroChequeException(cp.getCheque());
//					}
					Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cp.getCheque(), WebKeysGlobal.OSPIM);
					if (chequeDuplicado != null && cp.getId() == 0) {
						throw new DuplicateNumeroChequeException(chequeDuplicado);
					}
				}
			}
		}
		try{
			con = ConnectionHelper.getConnectionForTransaction();
			int idCtaBcriaCh = 0;
			CuentaBancaria cb = null;
			
			int id = ConvenioServiceImpl.getInstance().save(convenio,
					user.getScreenName(), con);
			convenio.setId(id);
	
			List<ActaRelacionada> actas = convenio.getActasRelacionadas();
			if (actas != null) {
				for (ActaRelacionada actaRel : actas) {
					ConvenioServiceImpl.getInstance().saveActaRelacionada(actaRel,
							user.getScreenName(),con);
				}
			}
	
			if (convenio.getId() != 0 && convenio.getPagos() != null) {			
				for (ConvenioPago cp : convenio.getPagos()) {
					if (cp.getCheque() != null
							&& cp.getCheque().getImporte() != null) {
						
						Cheque cheque = new Cheque();
						cheque.setNumero(cp.getCheque().getNumero());
						cheque.setImporte(cp.getCheque().getImporte());
						cheque.setFecha(cp.getCheque().getFecha());
						cheque.setPrestador(false);
						cheque.setBanco(cp.getCheque().getBanco());
						cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
						cheque.setEstado(cp.getCheque().getEstado());
						cheque.setCuit(convenio.getEmpresa().getCuit());
						cb = new CuentaBancaria(cp.getCheque().getCuentaBancaria().getId_cuenta_bcria(), 
								cp.getCheque().getCuentaBancaria().getDescripcion());
						cb.setBanco(cp.getCheque().getBanco());
						cheque.setCuentaBancaria(cb);
						
						idCtaBcriaCh = EmpresaServiceUtil.saveCuentaBancaria(cheque.getCuit(),"000", cheque.getCuentaBancaria(), user.getScreenName(), con); 
						
						cb.setId_cuenta_bcria(idCtaBcriaCh);
						
						ChequeServiceUtil.save(cheque, user.getScreenName(), con, WebKeysGlobal.OSPIM);
						cp.getCheque().setCuentaBancaria(cb);
					}
					ConvenioServiceImpl.getInstance().save(convenio, cp,
							user.getScreenName(),con);				
				}
			}
			con.commit();
		}catch(Exception e){
			con.rollback();
			_log.error(e);
			throw new SystemException(e);
		}finally {
			ConnectionHelper.cerrar(con);
		}
	}

	/**
	 * Verifica si existen todas las cuotas continuas
	 * 
	 * @param convenio
	 * @return
	 */
	private static boolean verificarCuotas(Convenio convenio) {
		List<ConvenioPago> pagos = convenio.getPagos();
		boolean[] cuotas = new boolean[pagos.size() - 1];
		for (ConvenioPago pago : pagos) {
			if (pago.getTipo().equals(ConvenioPago.Tipo.PAGO)) {
				int index = pago.getNroCuota() - 1;
				if (index > cuotas.length - 1) {
					return false;
				}
				cuotas[index] = true;
			}
		}
		for (int i = 0; i < pagos.size() - 1; i++) {
			if (!cuotas[i]) {
				return false;
			}
		}
		return true;
	}
	
	

	public static void update(Convenio convenio, User user)
			throws SystemException, DuplicateConvenioIdException, SQLException,
			DuplicateNumeroChequeException, FechaMenorACierreContableException, FaltanCuotasConvenioException, ExisteReciboConvenioException {
		
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(WebKeysGlobal.OSPIM);
		if (convenio.getFechaInicio().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}
		
		if (ConvenioServiceImpl.getInstance().verificarReciboConvenio(convenio.getId())) {
			throw new ExisteReciboConvenioException();
		}
		
		if (!verificarCuotas(convenio)) {
			throw new FaltanCuotasConvenioException();
		}
		//HASTA ACA ALL IGUAL AL SAVE. AHORA HABRIA QUE ENCARAR DE UNA FORMA RADICAL LOS PAGOS
		ConvenioServiceImpl.getInstance().update(convenio,
				user.getScreenName());
		
		CuentaBancaria cb = null;
		//ACA IMPACTA LOS PAGOS...
		if (convenio.getId() != 0 && convenio.getPagos() != null) {			
			for (ConvenioPago cp : convenio.getPagos()) {
				if (cp.getCheque() != null
						&& cp.getCheque().getImporte() != null) {
					Cheque cheque = new Cheque();
					cheque.setNumero(cp.getCheque().getNumero());
					cheque.setImporte(cp.getCheque().getImporte());
					cheque.setFecha(cp.getCheque().getFecha());
					cheque.setPrestador(false);
					cheque.setBanco(cp.getCheque().getBanco());
					cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
					cheque.setEstado(cp.getCheque().getEstado());
					cheque.setCuit(convenio.getEmpresa().getCuit());
					cb = new CuentaBancaria(cp.getCheque().getCuentaBancaria().getId_cuenta_bcria(), 
							cp.getCheque().getCuentaBancaria().getDescripcion());
					cb.setBanco(cp.getCheque().getBanco());
					cheque.setCuentaBancaria(cb);					
					ChequeServiceUtil.update(cheque, user, WebKeysGlobal.OSPIM);
				}
				ConvenioServiceImpl.getInstance().update(convenio, cp,
						user.getScreenName());				
			}
		}

	}

	public static void borrar(int id, User user)
			throws ImposibleBorrarConvenioException, SQLException {
		ConvenioServiceImpl.getInstance().borrar(id, user.getScreenName());
	}

	public static ActaRelacionada getActaARelacionar(Convenio convenio,
			String actaNro) {
		List<Acta> actas = ActaServiceImpl.getInstance().getActas(actaNro,
				null!=convenio.getEmpresa()?convenio.getEmpresa().getCuit():null, null,null);
		Acta acta = null;
		if (actas != null && actas.size() > 0) {
			acta = ActaServiceUtil.getActa(actas.get(0).getId(),0);
		}
		if (acta != null) {
			return new ActaRelacionada(convenio, acta.getTotal()
					.subtract(acta.getTotalPagadoIngresado())
					.subtract(acta.getTotalPagadoPorConvenioYActas()), acta);
		}
		return null;
	}

	public static List<Convenio> getConveniosSinRecibo(String cuit) {
		return ConvenioServiceImpl.getInstance().getConveniosSinRecibos(cuit);
	}

	public static List<ReporteConvenioBean> reporteConvenios(Date fechaIni,
			Date fechaFin, int entidad) {
		return ConvenioServiceImpl.getInstance().reporteConvenios(fechaIni,
				fechaFin, entidad);
	}

	public static List<ReporteCobranzaConvenioBean> reporteCobranzaConvenios(
			Date fechaIni, Date fechaFin) {
		return ConvenioServiceImpl.getInstance().reporteCobranzaConvenios(
				fechaIni, fechaFin);
	}
	
	public static List<ConvenioEstadoSeguimiento> getEstadosSeguimientoConvenios(){
		
//		FIXME: hacer service que invoque una tabla con dichos valores
		
		List<ConvenioEstadoSeguimiento> lista = new ArrayList<ConvenioEstadoSeguimiento>();
		ConvenioEstadoSeguimiento ces1 = new ConvenioEstadoSeguimiento(1, "Intimación");
		ConvenioEstadoSeguimiento ces2 = new ConvenioEstadoSeguimiento(2, "Con Cert. Deuda");
		ConvenioEstadoSeguimiento ces3 = new ConvenioEstadoSeguimiento(3, "Concurso");
		ConvenioEstadoSeguimiento ces4 = new ConvenioEstadoSeguimiento(4, "Quiebra");
		ConvenioEstadoSeguimiento ces5 = new ConvenioEstadoSeguimiento(5, "Ejecución");
		ConvenioEstadoSeguimiento ces6 = new ConvenioEstadoSeguimiento(6, "Reemplazado");
		ConvenioEstadoSeguimiento ces7 = new ConvenioEstadoSeguimiento(7, "Impugnado");
		
		lista.add(ces1);
		lista.add(ces2);
		lista.add(ces3);
		lista.add(ces4);
		lista.add(ces5);
		lista.add(ces6);
		lista.add(ces7);
		
		return lista;
	}

	public static boolean actualizaEstadoSeguimientoConvenio(int convenioId, int estadoSegId, String usr) throws Exception{
		
		return ConvenioServiceImpl.getInstance().actualizaEstadoSeguimientoConvenio(convenioId, estadoSegId, usr);
		
	}
	
	public static boolean actualizaEstadoSeguimientoConvenioNoOS(int convenioId, int estadoSegId, String usr) throws Exception{
		
		return ConvenioServiceImpl.getInstance().actualizaEstadoSeguimientoConvenioNoOS(convenioId, estadoSegId, usr);
		
	}
	
	public static List<ConvenioPagosReporte> getConveniosAvisoVencimiento(Integer dias) {
		return ConvenioServiceImpl.getInstance().getConveniosAvisoVencimiento(dias);
	}
}
