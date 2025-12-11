package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.liquidaciones.ChequeSinChequeraException;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.Chequera;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

/**
 * @author Martin Moreyra
 * 
 */
public class ChequeServiceUtil {

	private static ChequeServiceImpl instance = null;

	public static ChequeServiceImpl getInstance() {
		if (null == instance) {
			instance = new ChequeServiceImpl();
		}
		return instance;
	}

	public static void save(Cheque cheque, String user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		save(cheque, user, null, entidad);
	}
	
	public static void update(Cheque cheque, String user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		update(cheque, user, null, entidad);
	}

	public static void save(Cheque cheque, User user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		save(cheque, user.getScreenName(), entidad);
	}
	
	public static void update(Cheque cheque, User user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		update(cheque, user.getScreenName(), entidad);
	}

	public static void save(Cheque cheque, String user, Connection con,
			int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		getInstance().save(
				cheque.getCuit(),
				cheque.getNumero(),
				cheque.getImporte(),
				cheque.getANombreDe(),
				cheque.getFecha(),
				user,
				cheque.isPrestador(),
				cheque.getConcepto(),
				cheque.getCuentaBancaria() != null ? cheque.getCuentaBancaria().getId_cuenta_bcria() : null,
				cheque.getDebitoCredito(), 
				cheque.getBanco().getId_banco(),
				cheque.getEstado(), 
				con, 
				entidad);
	}
	
	public static void update(Cheque cheque, String user, Connection con,
			int entidad) throws SystemException,
			DuplicateNumeroChequeException {
		getInstance().update(
				cheque.getCuit(),
				cheque.getNumero(),
				cheque.getImporte(),
				cheque.getANombreDe(),
				cheque.getFecha(),
				user,
				cheque.isPrestador(),
				cheque.getConcepto(),
				cheque.getCuentaBancaria() != null ? cheque.getCuentaBancaria().getId_cuenta_bcria() : null,
				cheque.getDebitoCredito(), 
				cheque.getBanco().getId_banco(),
				cheque.getEstado(), 
				con, 
				entidad);
	}
	
	public static void updateDatos(Cheque cheque, String usuario, Connection connectionParam,
			int entidad) throws DuplicateNumeroChequeException, SystemException{
		getInstance().updateDatos(cheque, usuario, connectionParam, entidad);
	}

	public static void anularcheque(Cheque ch, Date fechaBaja,
			User user, int entidad) throws SystemException {
		getInstance().anularcheque(ch, fechaBaja, user.getScreenName(),
				entidad);
	}

	public static List<Estado> getEstadosCheque() {
		return getInstance().getChequeEstados();
	}
	
	
	public static List<Cheque> getCheques(Cheque cheque, int entidad) throws SystemException {
		return getInstance().getCheques(
				cheque.getCuit(),
				cheque.getNumero(),
				cheque.getBanco() != null ? Integer.valueOf(cheque.getBanco()
						.getId_banco()) : null,null, entidad);
	}
	
	public static Cheque getChequePorCuitBancoCtaBancariaNro(Cheque cheque, int entidad) throws SystemException {
		return getInstance().getChequePorCuitBancoCtaBancariaNro(cheque, entidad);
	}
	
	public static List<Cheque> getCheques(String cuit, int estadoId, BigDecimal nroCheque, BigDecimal importe, int entidad)
			throws SystemException {
		List<Cheque> cheques = getInstance().getCheques(cuit, nroCheque, null, importe, entidad);
		
		List<Cheque> chequesRecibidos = new ArrayList<Cheque>();
		if (cheques != null) {
			chequesRecibidos = new ArrayList<Cheque>();
			for (Cheque ch : cheques) {
				if (ch.getEstado().getId() == estadoId) {
					chequesRecibidos.add(ch);
				}
			}
		}
		return chequesRecibidos;
	}

	public static List<Cheque> getChequesRecibidos(String cuit, int entidad)
			throws SystemException {
		return getCheques(cuit, Cheque.Estado.RECIBIDO, null, null, entidad);
	}

	public static List<Cheque> getChequesRechazados(String cuit, int entidad)
			throws SystemException {
		return getCheques(cuit, Cheque.Estado.RECHAZADO, null, null, entidad);
	}

	public static void cambiarEstadoCheque(Cheque cheque, Estado estado,
			String user, Connection connectionParameter, int entidad) throws SystemException {
		getInstance().cambiarEstadoCheque(cheque, estado, user,
				connectionParameter, entidad);
	}

	public static void update(Cheque cheque, String user,
			Connection connectionParameter) throws SystemException {
		getInstance().update(cheque, user, connectionParameter);
	}

	public static List<Cheque> getChequesRecibidos(int entidad) throws SystemException {
		return getInstance().getChequesRecibidos(entidad);
	}

	public static List<Cheque> getChequesDepositados(int entidad) throws SystemException {
		return getInstance().getChequesDepositados(entidad);
	}

	public static void anularcheque(Cheque ch,
			Date fechaBaja, User user, Connection connection, int entidad)
			throws SystemException {
		getInstance().anularcheque(ch, fechaBaja,
				user.getScreenName(), connection, entidad);
	}

	public static List<Cheque> getChequesReutilizables(int entidad) throws SystemException {
		return getInstance().getChequesReutilizables(entidad);
	}
	
	public static void saveChequera(Chequera chequera, String user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		getInstance().saveChequera(chequera, user, entidad);
	}
	
	public static void borrarChequera(int id_chequera, String user, int entidad)
			throws SystemException, DuplicateNumeroChequeException {
		getInstance().borrarChequera(id_chequera, user, entidad);
	}
	
	public static List<Chequera> getUltimasChequeras(int entidad) throws SystemException {
		return getInstance().getUltimasChequeras(entidad);
	}
	
	public static boolean validarCheque(Cheque cheque, int entidad) throws DuplicateNumeroChequeException, ChequeSinChequeraException, SystemException {
		return getInstance().validarCheque(cheque, entidad);
	}
	

}
