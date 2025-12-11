package ar.com.uoma.conveniosNoOS.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import ar.com.ospim.tesoreria.beans.convenio.ConvenioPago;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceImpl;
import ar.com.uoma.actasNoOS.service.ActaNoOSServiceUtil;
import ar.com.uoma.beans.ActasAcuerdos;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class ConvenioNoOSServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(ConvenioNoOSServiceUtil.class);
	
	public static List<Convenio> getConvenios(String convenioNro, String cuit,
			String empresa, String entidad) {
		return ConvenioNoOSServiceImpl.getInstance().getConvenios(convenioNro,
				cuit, empresa, entidad);
	}

	public static Convenio getConvenio(int id, int reciboId, int entidad) {
		Convenio convenio = ConvenioNoOSServiceImpl.getInstance().get(id);
		List<ConvenioPago> pagos = ConvenioNoOSServiceImpl.getInstance()
				.getPagosConvenios(id, entidad);
		if (pagos != null) {
			convenio.setPagos(pagos);
		}
		List<ActaRelacionada> actas = ConvenioNoOSServiceImpl.getInstance()
				.getActasRelacionadas(id);
		if (pagos != null) {
			convenio.setActasRelacionadas(actas);
		}
		List<ConvenioPagoIngresado> pagosIngresados = ConvenioNoOSServiceImpl
				.getInstance().getPagosIngresados(id, reciboId, entidad);
		if (pagosIngresados != null) {
			convenio.setPagosIngresados(pagosIngresados);
		}
		return convenio;
	}
	
	/*public static Convenio getConvenioPagosRecibo(int id, int entidad) {
		Convenio convenio = ConvenioNoOSServiceImpl.getInstance().get(id);
		List<ConvenioPago> pagos = ConvenioNoOSServiceImpl.getInstance()
				.getPagosConvenios(id, entidad);
		if (pagos != null) {
			convenio.setPagos(pagos);
		}
		List<ActaRelacionada> actas = ConvenioNoOSServiceImpl.getInstance()
				.getActasRelacionadas(id);
		if (pagos != null) {
			convenio.setActasRelacionadas(actas);
		}
		List<ConvenioPagoIngresado> pagosIngresados = ConvenioNoOSServiceImpl
				.getInstance().getPagosIngresados(id, entidad);
		if (pagosIngresados != null) {
			convenio.setPagosIngresados(pagosIngresados);
		}
		return convenio;
	}*/

	public static void save(Convenio convenio, User user, int entidad)
			throws SystemException, DuplicateConvenioIdException, SQLException,
			DuplicateNumeroChequeException, FaltanCuotasConvenioException,
			NumberFormatException, ParseException,
			FechaMenorACierreContableException {

		Connection con = null;
		
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
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
//							.getCheque(), entidad);
//					if (cheques != null && !cheques.isEmpty()) {
//						throw new DuplicateNumeroChequeException(cp.getCheque());
//					}
					Cheque chequeDuplicado = ChequeServiceUtil.getChequePorCuitBancoCtaBancariaNro(cp.getCheque(), entidad);
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
			
			int id = ConvenioNoOSServiceImpl.getInstance().save(convenio, user.getScreenName(), con);
			convenio.setId(id);
	
			List<ActaRelacionada> actas = convenio.getActasRelacionadas();
			if (actas != null) {
				for (ActaRelacionada actaRel : actas) {
					ConvenioNoOSServiceImpl.getInstance().saveActaRelacionada(actaRel,user.getScreenName(), con);
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
//						EmpresaServiceUtil esu = new EmpresaServiceUtil();
//						idCtaBcriaCh = esu.saveCuentaBancariaAux(cheque.getCuit(),"000", cheque.getCuentaBancaria(), user.getScreenName(), con); 
						idCtaBcriaCh = EmpresaServiceUtil.saveCuentaBancaria(cheque.getCuit(),"000", cheque.getCuentaBancaria(), user.getScreenName(), con);
						cb.setId_cuenta_bcria(idCtaBcriaCh);
						
						ChequeServiceUtil.save(cheque, user.getScreenName() , con, entidad);
						cp.getCheque().setCuentaBancaria(cb);
					}
					
					ConvenioNoOSServiceImpl.getInstance().save(convenio, cp, user.getScreenName(), con);
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

	public static void update(Convenio convenio, User user, int entidad)
			throws SystemException, DuplicateConvenioIdException, SQLException,
			DuplicateNumeroChequeException,FechaMenorACierreContableException, FaltanCuotasConvenioException, ExisteReciboConvenioException {
		
		Connection con = null;
		
		// TODO Auto-generated method stub
		Date fecha_cierre_periodo = ContabilidadServiceUtil
				.getFechaUltimoPeriodoContable(entidad);
		if (convenio.getFechaInicio().compareTo(fecha_cierre_periodo) <= 0) {
			throw new FechaMenorACierreContableException();
		}

		if (!verificarCuotas(convenio)) {
			throw new FaltanCuotasConvenioException();
		}

		//VERIFICAR SI EXISTE RECIBO?
		if (ConvenioNoOSServiceImpl.getInstance().verificarReciboConvenio(convenio.getId(), entidad)) {
			throw new ExisteReciboConvenioException();
		}
		
		try{
			
			con = ConnectionHelper.getConnectionForTransaction();

			ConvenioNoOSServiceImpl.getInstance().update(convenio,user.getScreenName(), con);
			
			//BORRO LAS ACTAS RELACIONADAS
			ConvenioNoOSServiceImpl.getInstance().borraActasRelacionadas(convenio.getId(), con);
	
			List<ActaRelacionada> actas = convenio.getActasRelacionadas();
			if (actas != null) {
				for (ActaRelacionada actaRel : actas) {
					ConvenioNoOSServiceImpl.getInstance().saveActaRelacionada(actaRel,
							user.getScreenName(), con);
				}
			}
			
			//BORRO LOS PAGOS
			ConvenioNoOSServiceImpl.getInstance().borraPagosConvenio(convenio.getId(), entidad);
	
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
						if(null!=cp.getCheque().getEstado()){
							cheque.setEstado(cp.getCheque().getEstado());	
						}else{
							cheque.setEstado(new Cheque.Estado(Cheque.Estado.CARGADO));
						}
						
						cheque.setCuit(convenio.getEmpresa().getCuit());
						ChequeServiceUtil.save(cheque, user, entidad);
					}
					ConvenioNoOSServiceImpl.getInstance().save(convenio, cp, user.getScreenName(),con);			
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

	public static void borrar(int id, Date fechaBaja, User user)
			throws ImposibleBorrarConvenioException, SQLException {
		Convenio convenio=ConvenioNoOSServiceImpl.getInstance().get(id);
		//VERIFICO QUE NO TENGA RECIBO
		List<Convenio> conveniosSinRecibos=ConvenioNoOSServiceImpl.getInstance().getConveniosSinRecibos(convenio.getEmpresa().getCuit(), convenio.getEntidad().equals("U.O.M.A.")?WebKeysGlobal.UOMA:WebKeysGlobal.AMTIMA);
		boolean existe=conveniosSinRecibos.contains(convenio);
		//NO TIENE RECIBO SIGO
		if(existe){
			ConvenioNoOSServiceImpl.getInstance().borrar(id, fechaBaja, user.getScreenName());
		}else{
			throw new ImposibleBorrarConvenioException();
		}
	}
	public static void reactivar(int convenioId, User user)
			throws ImposibleBorrarConvenioException, SystemException, SQLException {
		ConvenioNoOSServiceImpl.getInstance().reactivar(convenioId, user.getScreenName());
	}

	public static ActaRelacionada getActaARelacionar(Convenio convenio,
			String actaNro) throws Exception{
		List<Acta> actas = ActaNoOSServiceImpl.getInstance().getActas(convenio.getEntidad(), actaNro,
				convenio.getEmpresa().getCuit(), null,null,null);
		Acta acta = null;
		if (actas != null && actas.size() > 0) {
			acta = ActaNoOSServiceUtil.getActa(actas.get(0).getId(),0);
		}
		if (acta != null) {
			return new ActaRelacionada(convenio, acta.getTotal()
					.subtract(acta.getTotalPagadoIngresado())
					.subtract(acta.getTotalPagadoPorConvenioYActas()), acta);
		}
		return null;
	}

	public static List<Convenio> getConveniosSinRecibo(String cuit, int entidad) {
		return ConvenioNoOSServiceImpl.getInstance().getConveniosSinRecibos(cuit, entidad);
	}

	public static List<ReporteConvenioBean> reporteConvenios(Date fechaIni,
			Date fechaFin) {
		return ConvenioNoOSServiceImpl.getInstance().reporteConvenios(fechaIni,
				fechaFin);
	}

	public static List<ReporteCobranzaConvenioBean> reporteCobranzaConvenios(
			Date fechaIni, Date fechaFin) {
		return ConvenioNoOSServiceImpl.getInstance().reporteCobranzaConvenios(
				fechaIni, fechaFin);
	}
	public static List<ActasAcuerdos> reporteAcuerdosNoOS(String cuit, String sucu, Date fechaIni, Date fechaFin, Date fechaPago, int conSaldo) {
		return ConvenioNoOSServiceImpl.getInstance().reporteAcuerdosNoOS(cuit, sucu, fechaIni, fechaFin, fechaPago, conSaldo);
	}
	
	public static List<ConvenioPagosReporte> getPagosConveniosAvisoVencimiento(Integer dias) {
		return ConvenioNoOSServiceImpl.getInstance().getPagosConveniosAvisoVencimiento(dias);
	}
}
