package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.afiliados.AfliadoYaTieneConyugeException;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.global.PrestacionComprobanteExistenteException;
import ar.com.ospim.liquidaciones.AfiliadoSinPlanException;
import ar.com.ospim.liquidaciones.DuplicateReintegroIdException;
import ar.com.ospim.liquidaciones.DuplicateReintegroPrestacionIdException;
import ar.com.ospim.liquidaciones.FechaPrestacionMayorFechaBajaExcepcion;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import ar.com.ospim.liquidaciones.NoSuchReintegroPrestacionEntryException;
import ar.com.ospim.liquidaciones.PrestacionYaHechaAAfiliadoExcepcion;
import ar.com.ospim.liquidaciones.TopeCantidadIndividualExedidoException;
import ar.com.ospim.liquidaciones.TopeCantidadTotalExedidoException;
import ar.com.ospim.liquidaciones.TopeImporteIndividualExedidoException;
import ar.com.ospim.liquidaciones.TopeImporteTotalExedidoException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.beans.DetalleCuota;
import ar.com.ospim.liquidaciones.beans.Reintegro;
import ar.com.ospim.liquidaciones.beans.ReintegroPrestacionNormal;
import ar.com.ospim.liquidaciones.beans.ReporteOrdenPagoReintegros;
import ar.com.ospim.liquidaciones.services.ReintegroServiceImpl.CantidadImporteRegistrados;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

/**
 * Mascara del servicio que da acceso a los datos de la aplicaci�n (BD).
 */
public class ReintegroServiceUtil {

	private static ReintegroServiceImpl instance = null;

	public static ReintegroServiceImpl getInstance() {
		if (null == instance) {
			instance = new ReintegroServiceImpl();
		}
		return instance;
	}
	
	private static double valorTopeMaximoExcedido = 0;
	

	/**
	 * Obtiene el reintegro por su clave primaria
	 * 
	 * @param id_reintegro
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public static Reintegro getReintegroEntry(int id_reintegro)
			throws SystemException, NoSuchReintegroEntryException {
		Reintegro reintegro = getInstance().getReintegroEntry(id_reintegro);
		if (WebKeysLiquidaciones.REINTEGRO_PRE.equalsIgnoreCase(reintegro.getTipo_reintegro())){
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroEntry(id_reintegro));
			getInstance().traeResumenOP(reintegro, reintegro.getId_reintegro(),
					reintegro.getTipo_reintegro());
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroOdoProtesisEntry(id_reintegro));
			getInstance().traeResumenOP(reintegro, reintegro.getId_reintegro(),
					reintegro.getTipo_reintegro());
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroOdoOrtopediaOrtodonciaEntry(
							id_reintegro));
			reintegro.setDetalleCuota(getInstance()
					.getDetalleCuotaReintegroOdoOrtopediaOrtodonciaEntry(
							id_reintegro));
		}
		return reintegro;
	}

	/**
	 * Setea al reintegro los valores de OP
	 */
	public static Reintegro traeResumenOP(Reintegro reintegro,
			int id_reintegro, String tipo_reintegro) {
		return getInstance().traeResumenOP(reintegro, id_reintegro,
				tipo_reintegro);
	}

	/**
	 * Obtiene el reintegro por su clave de ortodoncia alternativa
	 * 
	 * @param id_reintegro
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public static Reintegro getReintegroPorIdCuota(int id_reintegro)
			throws SystemException, NoSuchReintegroEntryException {
		DetalleCuota detalleCuota = getInstance()
				.getDetalleCuotaReintegroOrtoEntry(id_reintegro);
		Reintegro reintegro = getInstance().getReintegroEntry(
				detalleCuota.getId_reintegro());
		if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_PRE)) {
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroEntry(id_reintegro));
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroOdoProtesisEntry(id_reintegro));
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			reintegro.setReintegroPrestacion(getInstance()
					.getPrestacionesReintegroOdoOrtopediaOrtodonciaEntry(
							reintegro.getId_reintegro()));
			
			//id_reintegro
			
			List<DetalleCuota> detalleCuotas = new ArrayList<DetalleCuota>();
			detalleCuotas.add(detalleCuota);
			reintegro.setDetalleCuota(detalleCuotas);
		}
		return reintegro;
	}

	/**
	 * carga un nuevo afiliado
	 * 
	 * @param topeIndivImporte
	 * @param topeIndivCant
	 * @param topeImporte
	 * @param topeCant
	 * 
	 * @throws SystemException
	 *             , DuplicateReintegroIdException
	 * @throws TopeCantidadIndividualExedidoException
	 * @throws TopeImporteIndividualExedidoException
	 * @throws TopeCantidadTotalExedidoException
	 * @throws TopeImporteTotalExedidoException
	 * @throws AfiliadoSinPlanException
	 * @throws FechaPrestacionMayorFechaBajaExcepcion
	 * @throws PrestacionYaHechaAAfiliadoExcepcion
	 * @throws SQLException
	 */
	public static int cargaReintegroEntry(Date fecha, Date periodo,
			String entidad, String cuilTitular, int inte, int seccional,
			Date prestacionFecha, int idPrestacion, String codigo,
			String cuit, String descripcion, String tipoReintegro, int estado,
			String userName, String cantidad, String importe,
			String comproaDebitarTipo, String comproaDebitarNumero,
			String tercerizado, double topeCant, double topeImporte,
			double topeIndivCant, double topeIndivImporte, Date bajaFecha,
			String obs, int pieza, String cara, int idPrestadorExterno,
			String presupuesto, int nroCuotas, String cuitEntidad,
			String sucuEntidad, Date comprobanteFecha,
			String importeComprobante, int motivoAltaDiscapacidad, 
			boolean esExcepcion, int idReclamo, int idPrestacionReclamo,
			String cargoOspim, String cargoPrestadora, String comproaDebitarSucursal, 
			String comproaDebitarLetra, String cbu, String cuilCuenta,
			String emailCuenta , String apellidoCuenta, String nombreCuenta,String cargoImesa)
			throws SystemException, DuplicateReintegroIdException,
			DuplicateReintegroPrestacionIdException,
			TopeCantidadIndividualExedidoException,
			TopeImporteIndividualExedidoException,
			TopeCantidadTotalExedidoException,
			TopeImporteTotalExedidoException, AfiliadoSinPlanException,
			FechaPrestacionMayorFechaBajaExcepcion,
			PrestacionYaHechaAAfiliadoExcepcion, SQLException, Exception {

		Connection connection = null;
		int idReintegro = 0;
		int idReintegroRenglon = 0;
		try {
			connection = ConnectionHelper.getConnectionForTransaction();

			validaFechas(bajaFecha, prestacionFecha);

			idReintegro = getInstance().cargaReintegroEntry(fecha, periodo,
					entidad, cuilTitular, inte, seccional, tipoReintegro,
					estado, userName, null, null, obs, cbu, cuilCuenta, emailCuenta,apellidoCuenta, nombreCuenta , connection);
			
			validaPrestacion(idReintegro, tipoReintegro, idPrestacion, cuilTitular, inte, cuitEntidad, sucuEntidad,
					comproaDebitarTipo, comproaDebitarLetra, comproaDebitarNumero, idPrestadorExterno, periodo, null, connection);

			if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
				idReintegroRenglon= getInstance().cargaReintegroPrestacionEntry(cuilTitular, inte, idReintegro, cuit, descripcion, 
				idPrestacion, codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
				comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, tercerizado, periodo, userName, 
				cuitEntidad, sucuEntidad, comprobanteFecha, new BigDecimal(importeComprobante), motivoAltaDiscapacidad, 
				new BigDecimal(cargoOspim) , new BigDecimal(cargoPrestadora),new BigDecimal(cargoImesa), connection);
				
				// graba los datos del reclamo prestacional asociado 
				if (idReclamo!=0 && idPrestacionReclamo!=0){
					getInstance().grabaDatosDelReclamoPrestacionaldelReintegro(idReintegro,  idReclamo , idPrestacionReclamo,idReintegroRenglon  , userName,  connection);
				}
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				
				if (idPrestacion != 0) {
					validaPrestacionAfiliado(cuilTitular, inte, idPrestacion,pieza, cara);
					/*
					 * idReclamo: 21911, 
					 * idPrestacionReclamo: 26923
					 * */
					getInstance().cargaReintegroPrestacionOdoProtesisEntry(cuilTitular, inte, idReintegro, cuit, descripcion, idPrestacion, 
							codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), comproaDebitarTipo, comproaDebitarLetra,
							comproaDebitarSucursal, comproaDebitarNumero, tercerizado, periodo, userName, pieza, cara, idPrestadorExterno, 
							esExcepcion, 
							idReclamo, idPrestacionReclamo, 
							new BigDecimal(cargoOspim) , new BigDecimal(cargoPrestadora),new BigDecimal(cargoImesa), 
							connection);
				}
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				if (idPrestacion != 0) {
					validaTopes(
							cuilTitular,
							inte,
							idPrestacion,
							new BigDecimal(cantidad),
							new BigDecimal(importe),
							topeCant,
							topeImporte,
							topeIndivCant,
							topeIndivImporte,
							WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA);
					
					getInstance().cargaReintegroPrestacionOdoOrtoEntry(cuilTitular,inte, idReintegro, cuit, descripcion, idPrestacion, 
							codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
							comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, tercerizado, periodo, 
							userName, pieza, cara, idPrestadorExterno, new BigDecimal(presupuesto), nroCuotas, connection);
					       
					cargaOrtoCuotasEnCero(idReintegro, nroCuotas, new BigDecimal(
							importe), userName, connection);
				}
			}
			
			connection.commit();
			
		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
			if (e instanceof PrestacionYaHechaAAfiliadoExcepcion) {
				throw new PrestacionYaHechaAAfiliadoExcepcion();
			}else if(e instanceof PrestacionComprobanteExistenteException) {
				throw new PrestacionComprobanteExistenteException();
			}else if(e instanceof TopeImporteIndividualExedidoException) {
				throw new TopeImporteIndividualExedidoException(new Double(importe), topeIndivImporte);
			} else if (e instanceof TopeImporteTotalExedidoException) {
				//throw new TopeImporteTotalExedidoException(new Double(importe), topeIndivImporte);
				throw new TopeImporteTotalExedidoException(valorTopeMaximoExcedido, topeIndivImporte);
			}	
		} finally {
			ConnectionHelper.cerrar(connection);
		}
		return idReintegro;
	}

	private static void validaTopes(String cuil_titular, int inte,
			int id_prestacion, BigDecimal cantidad, BigDecimal importe,
			double topeCant, double topeImporte, double topeIndivCant,
			double topeIndivImporte, String tipo)
			throws TopeCantidadIndividualExedidoException,
			TopeImporteIndividualExedidoException, SystemException,
			TopeCantidadTotalExedidoException, TopeImporteTotalExedidoException {
		double imp = importe.doubleValue();
		double cantidadNorm = cantidad.doubleValue() > 0 ? cantidad
				.doubleValue() : 1d;
		double total = cantidadNorm * imp;
				
		if (topeIndivCant > 0 && cantidadNorm > topeIndivCant) {
			throw new TopeCantidadIndividualExedidoException(cantidadNorm,
					topeIndivCant);
		}
		if (topeIndivImporte > 0 && total > topeIndivImporte) {
			throw new TopeImporteIndividualExedidoException(total,
					topeIndivImporte);
		}

		CantidadImporteRegistrados regs = getInstance()
				.getCantidadImporteRegistrados(id_prestacion, cuil_titular,
						inte, Calendar.getInstance().get(Calendar.YEAR), tipo);
		if (regs != null) {
			if (null != tipo
					&& !tipo.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)
					&& topeCant > 0 && cantidadNorm + regs.cantidad > topeCant) {
				throw new TopeCantidadTotalExedidoException(regs.cantidad,
						topeCant);
			}

			valorTopeMaximoExcedido = total + (regs.cantidad * regs.importe);
			if (topeImporte > 0
					&& valorTopeMaximoExcedido > topeImporte) {
				throw new TopeImporteTotalExedidoException(
						valorTopeMaximoExcedido, topeImporte);
			}
		}
	}

	private static void validaFechas(Date bajaFecha, Date prestacionFecha)
			throws FechaPrestacionMayorFechaBajaExcepcion {
		if (bajaFecha != null) {
			if (prestacionFecha.after(bajaFecha)) {
				throw new FechaPrestacionMayorFechaBajaExcepcion();
			}
		}
	}

	private static void validaPrestacionAfiliado(String cuil_titular, int inte,
			int id_prestacion, int pieza, String cara)
			throws PrestacionYaHechaAAfiliadoExcepcion, SystemException {
		if (getInstance().getPrestacionHechaAAfiliado(cuil_titular, inte,
				id_prestacion, pieza, cara)) {
			throw new PrestacionYaHechaAAfiliadoExcepcion();
		}
	}

	/**
	 * actualiza un reintegro existente
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 * @throws TopeImporteTotalExedidoException
	 * @throws TopeCantidadTotalExedidoException
	 * @throws TopeImporteIndividualExedidoException
	 * @throws TopeCantidadIndividualExedidoException
	 * @throws AfiliadoSinPlanException
	 * @throws FechaPrestacionMayorFechaBajaExcepcion
	 * @throws AfliadoYaTieneConyugeException
	 */
	public static void actualizaReintegroEntry(int idReintegro, Date fecha, String cuilTitular, int inte, 
		String cuit, String descripcion, int idPrestacion, String codigo, Date prestacionFecha, String cantidad, 
		String importe, String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, 
		String comproaDebitarNumero, String tercerizado, String userName, Date periodo, double topeCant, 
		double topeImporte, double topeIndivCant, double topeIndivImporte, Date bajaFecha, int idSeccional, 
		String obs, int pieza, String cara, String tipoReintegro, int idPrestadorExterno, String presupuesto, 
		int nroCuotas, String cuitEntidad, String sucuEntidad, Date comprobanteFecha, String importeComprobante, 
		int motivoAltaDiscapacidad, boolean esExcepcion, int idReclamo, int idPrestacionReclamo, String cargoOspim, 
		String cargoOspimPrestadora,String cargoImesa) 
		throws NoSuchReintegroEntryException, SystemException, DuplicateReintegroPrestacionIdException, TopeCantidadIndividualExedidoException, 
			TopeImporteIndividualExedidoException, TopeCantidadTotalExedidoException, TopeImporteTotalExedidoException, AfiliadoSinPlanException,
			FechaPrestacionMayorFechaBajaExcepcion, SQLException, Exception {
		
		Connection connection = null;
		int idReintegroRenglon;
		try {
			connection = ConnectionHelper.getConnectionForTransaction();
			
			validaFechas(bajaFecha, prestacionFecha);
			

			getInstance().actualizaReintegroEntry(idReintegro, fecha,
					userName, idSeccional, obs);
			
			validaPrestacion(idReintegro, tipoReintegro, idPrestacion, cuilTitular, inte, 
					cuitEntidad, sucuEntidad, comproaDebitarTipo, comproaDebitarLetra,
					comproaDebitarNumero, idPrestadorExterno, periodo, null, connection);
			
			if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
				
				idReintegroRenglon= getInstance().cargaReintegroPrestacionEntry(cuilTitular, inte,
						idReintegro, cuit, descripcion, idPrestacion, codigo, prestacionFecha, new BigDecimal(cantidad), 
						new BigDecimal(importe), comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, 
						comproaDebitarNumero, tercerizado, periodo, userName, 
						cuitEntidad, sucuEntidad, comprobanteFecha, new BigDecimal(importeComprobante), motivoAltaDiscapacidad, 
						new BigDecimal(cargoOspim) , new BigDecimal(cargoOspimPrestadora),new BigDecimal(cargoImesa), connection);
				
				// graba los datos del reclamo prestacional asociado 
				if (idReclamo!=0 && idPrestacionReclamo!=0){
					getInstance().grabaDatosDelReclamoPrestacionaldelReintegro(idReintegro, idReclamo, idPrestacionReclamo, 
							idReintegroRenglon, userName,  connection);
				}
				
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				if (idPrestacion != 0) {
					getInstance().cargaReintegroPrestacionOdoProtesisEntry(
							cuilTitular, inte, idReintegro, cuit, descripcion, idPrestacion, codigo, prestacionFecha, 
							new BigDecimal(cantidad), new BigDecimal(importe), comproaDebitarTipo, comproaDebitarLetra, 
							comproaDebitarSucursal, comproaDebitarNumero, 
							tercerizado, periodo, userName, pieza, cara, idPrestadorExterno, esExcepcion,
							idReclamo, idPrestacionReclamo, 
							new BigDecimal(cargoOspim) , new BigDecimal(cargoOspimPrestadora),new BigDecimal(cargoImesa),
							connection);
				}
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				// si estado es pendiente y no tiene prestaci�n carga una nueva
				// prestaci�n, la condici�n se eval�a en el stored p.
				if (idPrestacion != 0) {
					getInstance().cargaReintegroPrestacionOdoOrtoEntry(cuilTitular, inte, idReintegro, cuit, descripcion, 
							idPrestacion, codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
							comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, tercerizado, 
							periodo, userName, pieza, cara, idPrestadorExterno, new BigDecimal(presupuesto), nroCuotas, connection);

					actualizaOrtoCuotas(idReintegro, nroCuotas, new BigDecimal(importe), userName, connection);
				}
			}
			if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
				// actualiza el estado a cargado por haberse modificado el
				// reintegro
				// prestacional
				cambiarEstadoReintegroEntry(idReintegro, WebKeysLiquidaciones.REINTEGRO_ESTADO_CARGADO, userName, tipoReintegro, connection);
			}
			connection.commit();
		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	/**
	 * actualiza una prestaciosn existente
	 * 
	 */
	public static void actualizaReintegroPrestacionEntry(int idReintegro, Date fecha, String cuilTitular, int inte, String cuit, 
		String descripcion, int idPrestacion, String codigo, Date prestacionFecha, String cantidad, String importe, 
		String comproaDebitarTipo, String comproaDebitarLetra, String comproaDebitarSucursal, String comproaDebitarNumero, 
		String tercerizado, String userName, Date periodo, double topeCant, double topeImporte, double topeIndivCant, 
		double topeIndivImporte, Date bajaFecha, int idSeccional, String obs, Date altaFecha, int idPrestacionAnterior, 
		String codigoAnterior, String tipoReintegro, int pieza, String cara, int idPrestadorExterno, String honorarios, 
		int nroCuotas, String cuitEntidad, String sucuEntidad, Date comprobanteFecha, String importeComprobante, 
		int motivoAltaDiscapacidad,boolean esExcepcion, String cargoOspim, String cargoPrestadora,String cargoImesa) throws NoSuchReintegroEntryException, 
	SystemException, DuplicateReintegroPrestacionIdException, TopeCantidadIndividualExedidoException, TopeImporteIndividualExedidoException, 
	TopeCantidadTotalExedidoException, TopeImporteTotalExedidoException, AfiliadoSinPlanException, FechaPrestacionMayorFechaBajaExcepcion, 
	Exception {
		
		Connection connection = null;
		try {
			connection = ConnectionHelper.getConnectionForTransaction();
			
			validaFechas(bajaFecha, prestacionFecha);
			
			validaPrestacion(idReintegro, tipoReintegro, idPrestacion, cuilTitular, inte, cuitEntidad, sucuEntidad, 
					comproaDebitarTipo, comproaDebitarLetra, comproaDebitarNumero, idPrestadorExterno, periodo, 
					idPrestacionAnterior, connection);
			
			getInstance().actualizaReintegroEntry(idReintegro, fecha, userName, idSeccional, obs);
			
			if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
				
				getInstance().actualizaReintegroPrestacionEntry(cuilTitular, inte, idReintegro, cuit, descripcion, idPrestacion, 
						codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
						comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, 
						tercerizado, periodo, userName, altaFecha, idPrestacionAnterior, codigoAnterior, 
						cuitEntidad, sucuEntidad, comprobanteFecha, new BigDecimal(importeComprobante), motivoAltaDiscapacidad, 
						new BigDecimal(cargoOspim), new BigDecimal(cargoPrestadora),new BigDecimal(cargoImesa));
				
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
				
				getInstance().actualizaReintegroPrestacionOdoProtesisEntry(cuilTitular, inte, idReintegro, cuit, descripcion,
						idPrestacion, codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
						comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, 
						tercerizado, periodo, userName, altaFecha, idPrestacionAnterior, codigoAnterior, pieza, cara, idPrestadorExterno, 
						esExcepcion);
				
			} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
				
				getInstance().actualizaReintegroPrestacionOdoOrtoEntry(cuilTitular, inte, idReintegro, cuit, descripcion, idPrestacion, 
						codigo, prestacionFecha, new BigDecimal(cantidad), new BigDecimal(importe), 
						comproaDebitarTipo, comproaDebitarLetra, comproaDebitarSucursal, comproaDebitarNumero, 
						tercerizado, periodo, userName, altaFecha, idPrestacionAnterior, codigoAnterior, 
						pieza, cara, idPrestadorExterno, new BigDecimal(honorarios), nroCuotas);

				actualizaOrtoCuotas(idReintegro, nroCuotas, new BigDecimal(importe), userName, connection);
			}
			
			connection.commit();
			
		} catch (Exception e) {
			ConnectionHelper.rollback(connection);
		} finally {
			ConnectionHelper.cerrar(connection);
		}
	}

	/**
	 * borra un reintegro
	 * 
	 * @throws SystemException
	 * @throws NoSuchReintegroEntryException
	 */
	public static void borraReintegroEntry(int id_reintegro, String userName)
			throws NoSuchReintegroEntryException, SystemException {
		getInstance().borraReintegroEntry(id_reintegro, userName);
	}

	public static void borraReintegroReclamoPrestacion(int idReclamo, int idPrestacionReclamo , String userName)
			throws NoSuchReintegroEntryException, SystemException {
		getInstance().borraReintegroReclamoPrestacion (idReclamo, idPrestacionReclamo,userName);
	}
	
	public static void borraReintegroPrestacionEntry(int id_reintegro,
			int id_prestacion, Date altaFecha, int id_plan, String tipo_compro,
			String nro_compro, String userName, String tipo_reintegro)
			throws NoSuchReintegroPrestacionEntryException, SystemException {
		if (tipo_reintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
			getInstance().borraReintegroPrestacionEntry(id_reintegro,
					id_prestacion, altaFecha, id_plan, tipo_compro, nro_compro,
					userName);
		} else if (tipo_reintegro
				.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			getInstance().borraReintegroPrestacionOdoProtesisEntry(
					id_reintegro, id_prestacion, altaFecha, id_plan,
					tipo_compro, nro_compro, userName);
		}
	}

	
	public static List<PrestacionesReclamo> buscarPrestacionesReclamosAfiliadoReintegroFarmacia(int inte, String cuil ) {
		return getInstance().buscarPrestacionesReclamosdeAfiliadoEnReintegroFarmacia(cuil, inte);
	}
	
	public static List<PrestacionesReclamo> buscarPrestacionesReclamosAfiliadoReintegro(int inte, String cuil ,boolean esReintegro, int marca_rein_liq, String plan  ) {
		return getInstance().buscarPrestacionesReclamosdeAfiliadoEnReintegro(cuil, inte, esReintegro, marca_rein_liq, plan );
	}
	
	public static List<PrestacionesReclamo> buscarPrestacionesReclamosAfiliadoReintegroPorLote(int inte, String cuil ,boolean esReintegro, String nroLoteFiltro  ) {
		return getInstance().buscarPrestacionesReclamosdeAfiliadoEnReintegroPorLote(cuil, inte, esReintegro ,  nroLoteFiltro );
	}
	
			
	public static List<Reintegro> buscarReintegros(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String pagos,
			String alta_usr) throws Exception {
		if (pagos.equals("1")) {
			return getInstance().buscarReintegros(entidad, fechaDesde,
					fechaHasta, periodoDesde, periodoHasta, codPrestad, nroAfi,
					inte, cuil_titular, seccional, numero, alta_usr);
		} else if (pagos.equals("0")) {
			return getInstance().buscarReintegrosImpagos(entidad, fechaDesde,
					fechaHasta, periodoDesde, periodoHasta, codPrestad, nroAfi,
					inte, cuil_titular, seccional, numero, alta_usr);
		} else if (pagos.equals("2")) {
			return getInstance().buscarReintegrosPagos(entidad, fechaDesde,
					fechaHasta, periodoDesde, periodoHasta, codPrestad, nroAfi,
					inte, cuil_titular, seccional, numero, alta_usr);
		} else {
			return new ArrayList<Reintegro>();
		}
	}

	public static List<Reintegro> buscarReintegrosOdoProtesis(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String pagos,
			String alta_usr, int estado) {
		if (pagos.equals("1")) {
			return getInstance().buscarReintegrosOdoProtesis(entidad,
					fechaDesde, fechaHasta, periodoDesde, periodoHasta,
					codPrestad, nroAfi, inte, cuil_titular, seccional, numero,
					alta_usr, estado);
		} else if (pagos.equals("0")) {
			return getInstance().buscarReintegrosOdoProtesisImpagos(entidad,
					fechaDesde, fechaHasta, periodoDesde, periodoHasta,
					codPrestad, nroAfi, inte, cuil_titular, seccional, numero,
					alta_usr, estado);
		} else if (pagos.equals("2")) {
			return getInstance().buscarReintegrosOdoProtesisPagos(entidad,
					fechaDesde, fechaHasta, periodoDesde, periodoHasta,
					codPrestad, nroAfi, inte, cuil_titular, seccional, numero,
					alta_usr, estado);
		} else {
			return new ArrayList<Reintegro>();
		}
	}

	public static List<Reintegro> buscarReintegrosOdoOrto(String entidad,
			Date fechaDesde, Date fechaHasta, Date periodoDesde,
			Date periodoHasta, String codPrestad, int nroAfi, int inte,
			String cuil_titular, int seccional, int numero, String pagos,
			String alta_usr, int estado) {
		if (pagos.equals("1")) {
			return getInstance().buscarReintegrosOdoOrto(entidad, fechaDesde,
					fechaHasta, periodoDesde, periodoHasta, codPrestad, nroAfi,
					inte, cuil_titular, seccional, numero, alta_usr, estado);
		} else if (pagos.equals("0")) {
			return getInstance().buscarReintegrosOdoOrtoImpagos(entidad,
					fechaDesde, fechaHasta, periodoDesde, periodoHasta,
					codPrestad, nroAfi, inte, cuil_titular, seccional, numero,
					alta_usr, estado);
		} else if (pagos.equals("2")) {
			return getInstance().buscarReintegrosOdoOrtoPagos(entidad,
					fechaDesde, fechaHasta, periodoDesde, periodoHasta,
					codPrestad, nroAfi, inte, cuil_titular, seccional, numero,
					alta_usr, estado);
		} else {
			return new ArrayList<Reintegro>();
		}
	}

	public static BigDecimal getCantidadPrestacionesAnio(String cuil_titular,
			int inte, int id_prestacion) throws Exception {
		return getInstance().getCantidadPrestacionesAnio(cuil_titular, inte,
				id_prestacion);
	}

	public static String getIdReintegrosAnio(String cuil_titular, int inte,
			int id_prestacion) throws Exception {
		return getInstance().getIdReintegrosAnio(cuil_titular, inte,
				id_prestacion);
	}

	public static BigDecimal getTotalPrestacionesEntreFechas(
			String cuil_titular, int inte, int id_prestacion, Date fecha_desde,
			Date fecha_hasta, String cuit, String sucu) throws Exception {
		return getInstance().getTotalPrestacionesEntreFechas(cuil_titular,
				inte, id_prestacion, fecha_desde, fecha_hasta, cuit, sucu);
	}

	public static BigDecimal getCantidadPrestacionesEntreFechas(
			String cuil_titular, int inte, int id_prestacion, Date fecha_desde,
			Date fecha_hasta, String cuit, String sucu) throws Exception {
		return getInstance().getCantidadPrestacionesEntreFechas(cuil_titular,
				inte, id_prestacion, fecha_desde, fecha_hasta, cuit, sucu);
	}

	public static BigDecimal getCantidadPrestacionesProtesisAnio(
			String cuil_titular) throws Exception {
		return getInstance().getCantidadPrestacionesProtesisAnio(cuil_titular);
	}

	public static List<Reintegro> buscarHistoricoPrestacionesOdoProtesis(
			String cuil_titular, int inte) throws SystemException {
		return getInstance().getHistoricoPrestacionesOdoProtesis(cuil_titular,
				inte);
	}

	public static List<Reintegro> buscarHistoricoPrestacionesOdoOrto(
			String cuil_titular, int inte) throws SystemException {
		return getInstance()
				.getHistoricoPrestacionesOdoOrto(cuil_titular, inte);
	}

	public static List<ReporteOrdenPagoReintegros> getReintegros(int listaId)
			throws SystemException, NoSuchReintegroEntryException {
		ArrayList<ReporteOrdenPagoReintegros> list = new ArrayList<ReporteOrdenPagoReintegros>();
		int id_reintegro = getInstance().getPrimerReitnegroLista(listaId);

		String tipo_reintegro = getInstance().getTipoReintegroLista(listaId,
				id_reintegro);

		Reintegro reintegro = null;
		if (!tipo_reintegro
				.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			reintegro = ReintegroServiceUtil.getReintegroEntry(id_reintegro);
		} else {
			reintegro = ReintegroServiceUtil
					.getReintegroPorIdCuota(id_reintegro);
		}

		if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_PRE)) {
			getInstance().getReintegros(listaId, list);
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			getInstance().getReintegrosOdoProtesis(listaId, list);
		} else if (reintegro.getTipo_reintegro().equalsIgnoreCase(
				WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			getInstance().getReintegrosOdoOrto(listaId, list);
		}

		Collections.sort(list, new Comparator<ReporteOrdenPagoReintegros>() {
			public int compare(ReporteOrdenPagoReintegros o1,
					ReporteOrdenPagoReintegros o2) {
				if (o1.getReintegro().getId_reintegro() == o2.getReintegro()
						.getId_reintegro()) {
					return 0;
				} else if (o1.getReintegro().getId_reintegro() < o2
						.getReintegro().getId_reintegro()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return list;
	}

	public static void cambiarEstadoReintegroEntry(int id_reintegro,
			int estado, String userName, String tipo_reintegro, Connection conn)
			throws NoSuchReintegroEntryException, SystemException {
		getInstance().cambiarEstadoReintegroEntry(id_reintegro, estado,
				userName, tipo_reintegro, conn);
		if (tipo_reintegro
				.equals(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			cambiarEstadoCuota(id_reintegro, 1, estado, userName);
			cambiarEstadoCuota(id_reintegro, 2, estado, userName);
			cambiarEstadoCuota(id_reintegro, 3, estado, userName);
		}
	}

	public static void cambiarEstadoCuota(int id_reintegro, int cuota,
			int estado, String userName) throws SystemException {

		getInstance().cambiarEstadoCuota(id_reintegro, cuota, estado, userName);
	}

	public static int getIndexOfReintegroList(Reintegro reintegro,
			List<Reintegro> reintegrosList) {
		int indice = 0;
		for (int i = 0; i < reintegrosList.size(); i++) {
			if (reintegro.equals(reintegrosList.get(i))) {
				indice = i;
				break;
			}
		}
		return indice;
	}
	
	public static double getImporteRestanteCuota(ArrayList<DetalleCuota> detalleCuotas, int cuota) {
		// Suma todas menos la recibida
		double importeRestante = 0;
		for (DetalleCuota detallec : detalleCuotas) {
			if (cuota != detallec.getNro_cuota()) {
				importeRestante = importeRestante + detallec.getImporte().doubleValue();
			}
		}
		return importeRestante;
	}

	public static DetalleCuota getDetalleCuota(
			ArrayList<DetalleCuota> detalleCuotas, int cuota) {
		DetalleCuota detalleCuota = null;
		for (DetalleCuota detallec : detalleCuotas) {
			if (cuota == detallec.getNro_cuota()) {
				detalleCuota = detallec;
				break;
			}
		}
		return detalleCuota;
	}

	public static void cargaOrtoCuotas(int id_reintegro, int nro_cuotas,
			BigDecimal importe, String username, Connection con)
			throws SystemException {
		for (int i = 0; i < nro_cuotas; i++) {
			BigDecimal importe_cuota = BigDecimal.ZERO;
			int porcentaje = 0;
			BigDecimal porcentajeBigDecimal = BigDecimal.ZERO;
			switch (i) {
			case 0:
				if (nro_cuotas == 3) {
					porcentaje = 50;
				} else {
					porcentaje = 100;
				}
				break;
			case 1:
				porcentaje = 20;
				break;
			case 2:
				porcentaje = 30;
				break;
			default:
				break;
			}
			porcentajeBigDecimal = new BigDecimal(porcentaje / 100D);
			importe_cuota = importe.multiply(porcentajeBigDecimal);
			getInstance().cargaOrtoCuotas(id_reintegro, i + 1, porcentaje,
					importe_cuota, username, con);
		}
		return;
	}

	public static void cargaOrtoCuotasEnCero(int id_reintegro, int nro_cuotas,
			BigDecimal importe, String username, Connection con)
			throws SystemException {
		for (int i = 0; i < nro_cuotas; i++) {
			BigDecimal importe_cuota = BigDecimal.ZERO;
			int porcentaje = 0;
			BigDecimal porcentajeBigDecimal = BigDecimal.ZERO;

			porcentajeBigDecimal = new BigDecimal(porcentaje / 100D);
			importe_cuota = importe.multiply(porcentajeBigDecimal);
			getInstance().cargaOrtoCuotas(id_reintegro, i + 1, porcentaje,
					importe_cuota, username, con);
		}
		return;
	}

	public static void actualizaImporteCuotas(int id_reintegro, int nro_cuotas,
			BigDecimal importe, String username, Connection con)
			throws SystemException {
		for (int i = 0; i < nro_cuotas; i++) {
			BigDecimal importe_cuota = BigDecimal.ZERO;
			int porcentaje = 0;
			BigDecimal porcentajeBigDecimal = BigDecimal.ZERO;
			switch (i) {
			case 0:
				if (nro_cuotas == 3) {
					porcentaje = 50;
				} else {
					porcentaje = 100;
				}
				break;
			case 1:
				porcentaje = 20;
				break;
			case 2:
				porcentaje = 30;
				break;
			default:
				break;
			}
			porcentajeBigDecimal = new BigDecimal(porcentaje / 100D);
			importe_cuota = importe.multiply(porcentajeBigDecimal);
			getInstance().actualizaImporteOrtoCuotas(id_reintegro, i + 1,
					porcentaje, importe_cuota, username, con);
		}
		return;
	}

	public static void actualizaImporteCuotasEnCero(int id_reintegro, int nro_cuotas,
			BigDecimal importe, String username, Connection con)
			throws SystemException {
		for (int i = 0; i < nro_cuotas; i++) {
			BigDecimal importe_cuota = BigDecimal.ZERO;
			int porcentaje = 0;
			BigDecimal porcentajeBigDecimal = BigDecimal.ZERO;
			
			porcentajeBigDecimal = new BigDecimal(porcentaje / 100D);
			importe_cuota = importe.multiply(porcentajeBigDecimal);
			getInstance().actualizaImporteOrtoCuotas(id_reintegro, i + 1,
					porcentaje, importe_cuota, username, con);
		}
		return;
	}
	
	public static void actualizaOrtoDetalleCuota(DetalleCuota detalleCuota,
			User user) throws SystemException {
		getInstance();
		ReintegroServiceImpl.actualizaOrtoDetalleCuota(detalleCuota,
				user.getScreenName());
	}

	public static void actualizaOrtoCuotas(int id_reintegro, int nro_cuotas,
			BigDecimal importe, String userName, Connection con) {
		Reintegro tratamiento = null;
		int cant_cuotas = 0;
		try {
			tratamiento = getReintegroEntry(id_reintegro);
			cant_cuotas = tratamiento.getDetalleCuota().size();
			// si no ten�a cuotas las crea
			if (cant_cuotas == 0) {
				cargaOrtoCuotasEnCero(id_reintegro, nro_cuotas, importe, userName,
						con);
			}
			// si tiene cuotas reivsa casos
			else {
				// si no cambia la cantidad de cuotas solo actualiza importes
/*				
				if (cant_cuotas == nro_cuotas) {
					actualizaImporteCuotasEnCero(id_reintegro, nro_cuotas, importe,
							userName, con);
				}
*/				
				if (cant_cuotas == 3 && nro_cuotas == 1) {
					getInstance().borrarOrtoCuota(id_reintegro, 2);
					getInstance().borrarOrtoCuota(id_reintegro, 3);
					actualizaImporteCuotas(id_reintegro, nro_cuotas, importe,
							userName, con);
				}
				/* Graba cuotas con porcentaje en cero (antes 20 y 30) */
				if (cant_cuotas == 1 && nro_cuotas == 3) {
					getInstance().cargaOrtoCuotas(id_reintegro, 2, 0,
							BigDecimal.ZERO, userName, con);
					getInstance().cargaOrtoCuotas(id_reintegro, 3, 0,
							BigDecimal.ZERO, userName, con);
					actualizaImporteCuotas(id_reintegro, nro_cuotas, importe,
							userName, con);
				}
			}
		} catch (NoSuchReintegroEntryException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static Object[] evaluaTopesReintegro(
			String origen,String cuil,BigDecimal aReintegrar,
			String idPrestacionAnterior,Integer idPlan) throws SystemException {
			
		boolean ret = false;
		Object[] retObj = new Object[3];
		List<ReintegroPrestacionNormal> reintegros = getInstance().recuperaReintegrosDelAnio(origen,cuil);
		List<ConvenioPrestacionalDetalle> topes = getInstance().recuperaTopesReintegrosDelAnio(origen,idPlan);
		
		for(ReintegroPrestacionNormal r:reintegros){
			
			for(ConvenioPrestacionalDetalle t:topes){
			   if(r.getFecha_prestacion().compareTo(t.getFechaDesde())>=0 &&
				  r.getFecha_prestacion().compareTo(t.getFechaHasta())<=0){
				   
				   //En Porcentaje se guarda la suma de los totales por reintegro existentes en la BD.
				   if(t.getPorcentaje()==null) t.setPorcentaje(BigDecimal.ZERO);
				   t.setPorcentaje(t.getPorcentaje().add(r.getImporte()));
			   }
			}
			
		}
		
		//Importe contiene el Tope para el A�o 
		Double totalGrupo= 0D;
		
		retObj[1]=0;
		for(int xi=0 ; xi<topes.size();xi++){
			totalGrupo = 0D;
			
			for(int j=0;j<=xi;j++){
				totalGrupo += topes.get(j).getPorcentaje().doubleValue();
			}
			
			if(topes.get(xi).getImporte().compareTo( BigDecimal.valueOf(totalGrupo + (xi==topes.size()-1 &&
					"".equalsIgnoreCase(idPrestacionAnterior)?aReintegrar.doubleValue():0D )))<0){
				ret=true;
				retObj[1]=topes.get(xi).getImporte();
				break;
			}
			
		}
				
		NumberFormat formatter = new DecimalFormat("#0.00");
		retObj[0]=ret;
		retObj[2]=formatter.format(totalGrupo);
		if(topes.size()>0 && !ret){
			retObj[1]=topes.get(topes.size()-1).getImporte();
	    }
		retObj[1]=formatter.format(retObj[1]);
		
		return retObj;
	}
	
	
	
	public static Object[] evaluaTopesReintegroPorFecha(
			String origen,String cuilTitular,int inte,
			String pieza, String cara, String diaPer,
            String mesPer, String anioPer, String codigo) throws SystemException {
		
		Object[] retObj = new Object[2];
		retObj[0]=false;
		retObj[1]=false;
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		int cantDias =0 ;
		
		Date prestacionFechaActual;
		try {
			prestacionFechaActual = formatoDeFecha.parse(diaPer + "/"
					+ (Integer.parseInt(mesPer) + 1) + "/"
					+ anioPer);
		} catch (Exception e) {
			prestacionFechaActual = null;
		}
		
		
		ReintegroPrestacionNormal ultimaPrestacion = getInstance().recuperaReintegrosUltimaProtesisDental(origen, cuilTitular, inte, cara, pieza,codigo);
		//Obtengo el valor absoluto siempre positivo
		if (ultimaPrestacion != null) {			
			cantDias = Math.abs(DateUtils.diferenciaDias(DateUtils.toCalendar(prestacionFechaActual), DateUtils.toCalendar(ultimaPrestacion.getFecha_prestacion())));
			if("040412".equals(ultimaPrestacion.getCodigo())) {
				//es menor a un a�o
				if ( cantDias <=  365 ) {
					retObj[0]=true;
				}
			}else {
				//es menor a 5 a�os
				if ( cantDias <= 1825) {
					retObj[1]=true;
				}
			}
		}
		
		
		return retObj;
	}
	
	
	private static void validaPrestacion(int idReintegro, String tipoReintegro, int idPrestacion, String cuilTitular, int inte,
			String cuit, String sucursal, String comproTipo, String comproLetra, String comproNro, int idPrestadorExterno, Date periodo,
			Integer idPrestacionAnterior, Connection connection)
			throws DuplicateReintegroPrestacionIdException, SystemException, PrestacionComprobanteExistenteException {
		
		BigDecimal comproNroC1 = new BigDecimal(StringUtils.checkNotEmpty(comproNro)?comproNro:"0");
		
		if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_PRE)) {
			
			List<ReintegroPrestacionNormal>list= getInstance().getComprobantesPrestacionesReintegro(idPrestacion, comproTipo, comproLetra, comproNro, cuit, sucursal, cuilTitular, inte, connection);
			
			for(ReintegroPrestacionNormal l:list){
				BigDecimal comproNroC2 = new BigDecimal(l.getCompro_a_debitar_numero());
				if(l.getId_prestacion()==idPrestacion && 
						l.getCuit_entidad().equalsIgnoreCase(cuit) &&
						l.getSucursal_entidad().equalsIgnoreCase(sucursal) &&
						l.getCompro_a_debitar_tipo().equalsIgnoreCase(comproTipo) &&
						l.getComproaDebitarLetra().equalsIgnoreCase(comproLetra) &&
						//l.getCompro_a_debitar_numero().equalsIgnoreCase(comproNro) &&
						comproNroC1.equals(comproNroC2) &&
						l.getPeriodo().getTime()==periodo.getTime() &&
						l.getReintegro().getAfiliado().getCuil_titular().equalsIgnoreCase(cuilTitular) &&
						l.getReintegro().getAfiliado().getInte()==inte 
						){
					if(l.getId_reintegro()!=idReintegro) throw new PrestacionComprobanteExistenteException();
					//if(l.getId_reintegro()==idReintegro && idPrestacionAnterior==null) throw new PrestacionComprobanteExistenteException();
				}
			}
		} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_PROTESIS)) {
			
		} else if (tipoReintegro.equalsIgnoreCase(WebKeysLiquidaciones.REINTEGRO_ODO_ORTOPEDIA_ORTODONCIA)) {
			
		}
		
	}
	
	public static Integer getIdOPReintegroLista(Integer idLista	) throws Exception {
		return getInstance().getIdOPReintegroLista(idLista);
	}

}
