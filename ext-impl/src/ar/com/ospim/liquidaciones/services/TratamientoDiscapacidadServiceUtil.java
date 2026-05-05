package ar.com.ospim.liquidaciones.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.liquidaciones.DuplicateTratamientoDiscapacidadIdException;
import ar.com.ospim.liquidaciones.ImposibleBorrarTratamientoDiscapacidadException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.beans.MotivoAltaDiscapacidad;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

public class TratamientoDiscapacidadServiceUtil {

	private static TratamientoDiscapacidadServiceImpl instance = null;

	public static TratamientoDiscapacidadServiceImpl getInstance() {
		if (null == instance) {
			instance = new TratamientoDiscapacidadServiceImpl();
		}
		return instance;
	}

	public static List<TratamientoDiscapacidad> buscarTratamientosDiscapacidad(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador,
			String cuit, String prestador, int numero, int estado,
			String codPrestaci) throws Exception {
		return getInstance().buscarTratamientosDiscapacidad(entidad,
				fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, codPrestad,
				id_prestador, cuit, prestador, numero, estado, codPrestaci);
	}

	
	public static List<TratamientoDiscapacidad> buscarTratamientosDiscapacidad(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador,
			String cuit, String prestador, int numero, int estado,
			String codPrestaci,Boolean incluyeAntiguos) throws Exception {
		return getInstance().buscarTratamientosDiscapacidad(entidad,
				fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, codPrestad,
				id_prestador, cuit, prestador, numero, estado, codPrestaci,incluyeAntiguos);
	}
	
	public static TratamientoDiscapacidad getTratamientoDiscapacidad(int id)
			throws SystemException {
		TratamientoDiscapacidad td = getInstance().getTratamientoDiscapacidad(
				id);
		if (td != null) {
			td.setDocumentosFaltantes(getInstance()
					.getDocFaltanteTratamientoDiscapacidad(id));
		}
		return td;
	}

	public static int save(int id_prestacion, String cuil, int inte,
			String cantidad, String importe_total, String periodicidad,
			Date periodo_desde, Date periodo_hasta, User user, String cuit,
			String prestador, String id_seccional, String observaciones,
			boolean recupera_ape, int estado, String documentacion,
			String cantidad_viajes_mes, String cantidad_kilometros_dia,
			String cantidad_kilometros_mes, String importe_kilometro_unit,
			String hs_espera_dia, String hs_espera_mes,
			String importe_hs_espera_unit, String importe_tercerizado,
			String id_tercerizadora,int id_prestador,String esExcepcion)

	throws DuplicatePrestadorIdException, SystemException,
			DuplicateTratamientoDiscapacidadIdException {

		int[] documentos = null;
		if (documentacion != null && !documentacion.equals("null")) {
			String[] aux = documentacion.split(",");
			documentos = new int[aux.length];
			for (int i = 0; i < aux.length; i++) {
				documentos[i] = Integer.parseInt(aux[i]);
			}
		}

		int id_tratamiento = getInstance().save(id_prestacion, cuil, inte,
				new BigDecimal(cantidad), new BigDecimal(importe_total),
				periodicidad, periodo_desde, periodo_hasta,
				user.getScreenName(), cuit, prestador, id_seccional,
				observaciones, recupera_ape, estado,
				new BigDecimal(cantidad_viajes_mes),
				new BigDecimal(cantidad_kilometros_dia),
				new BigDecimal(cantidad_kilometros_mes),
				new BigDecimal(importe_kilometro_unit),
				new BigDecimal(hs_espera_dia), new BigDecimal(hs_espera_mes),
				new BigDecimal(importe_hs_espera_unit),
				new BigDecimal(importe_tercerizado), id_tercerizadora,id_prestador,esExcepcion);
		if (id_tratamiento != 0 && documentos != null) {
			for (int d : documentos) {
				getInstance().cargarDocumentosFaltantes(id_tratamiento, d,
						user.getScreenName());
			}
		}

		return id_tratamiento;
	}

	public static void update(int id_tratamiento, int id_prestacion,
			String cuil, int inte, String cantidad, String importe_total,
			String periodicidad, Date periodo_desde, Date periodo_hasta,
			User user, String cuit, String prestador, String id_seccional,
			String observaciones, boolean recupera_ape, int estado,
			String documentacion, String cantidad_viajes_mes,
			String cantidad_kilometros_dia, String cantidad_kilometros_mes,
			String importe_kilometro_unit, String hs_espera_dia,
			String hs_espera_mes, String importe_hs_espera_unit,
			String importe_tercerizado, String id_tercerizadora,int id_prestador,String esExcepcion)
			throws SystemException {
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");

		int[] documentos = null;
		if (documentacion != null && !documentacion.equals("null")) {
			String[] aux = documentacion.split(",");
			documentos = new int[aux.length];
			for (int i = 0; i < aux.length; i++) {
				documentos[i] = Integer.parseInt(aux[i]);
			}
		}
		if (estado == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE) {
			if (documentos == null
					|| (documentos != null && documentos.length == 0)) {
				estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO;
			}
		}
		if (estado == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO) {
			if (documentos != null && documentos.length > 0) {
				estado = WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE;
			}
		}

		getInstance().update(id_tratamiento, id_prestacion, cuil, inte,
				new BigDecimal(cantidad), new BigDecimal(importe_total),
				periodicidad, periodo_desde, periodo_hasta,
				user.getScreenName(), cuit, prestador, id_seccional,
				observaciones, recupera_ape, estado, documentos,
				new BigDecimal(cantidad_viajes_mes),
				new BigDecimal(cantidad_kilometros_dia),
				new BigDecimal(cantidad_kilometros_mes),
				new BigDecimal(importe_kilometro_unit),
				new BigDecimal(hs_espera_dia), new BigDecimal(hs_espera_mes),
				new BigDecimal(importe_hs_espera_unit),
				new BigDecimal(importe_tercerizado), id_tercerizadora,id_prestador,esExcepcion);

		getInstance().borrarDocumentosFaltantes(id_tratamiento);
		if (id_tratamiento != 0 && documentos != null && documentos.length > 0) {
			for (int d : documentos) {
				getInstance().cargarDocumentosFaltantes(id_tratamiento, d,
						user.getScreenName());
			}
		}
	}

	public static void borrar(int id, User user)
			throws ImposibleBorrarTratamientoDiscapacidadException,
			SQLException {
		getInstance().borrar(id, user.getScreenName());
	}

	public static MotivoAltaDiscapacidad validarDiscapacidad(String cuil,
			int inte, int id_prestacion, String fecha_prestacion, int cantidad,
			String importe, String cuit, String sucu, String periodo,
			String codPrestaci, String importe_anterior,
			String cantidad_anterior) throws Exception {
		List<TratamientoDiscapacidad> tratamientos = buscarTratamientosDiscapacidad(
				null, null, null, 0, inte, cuil, 0, 0, cuit, null, 0, 0,
				codPrestaci);

		Iterator<TratamientoDiscapacidad> iterator = tratamientos.iterator();
		while (iterator.hasNext()) {
			// if
			// (!tratamientoDiscapacidad.getAcreedor().getSucursal().equalsIgnoreCase(sucu))
			// {
			// tratamientos.remove(tratamientoDiscapacidad);
			// }
			TratamientoDiscapacidad tratamientoDiscapacidad = iterator.next();
			if (tratamientoDiscapacidad.getBaja_fecha() != null) {
				iterator.remove();
			}
		}

		MotivoAltaDiscapacidad motivoAlta = new MotivoAltaDiscapacidad();

		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fecha_prestacion);
		} catch (Exception e) {
			fecha = null;
		}
		Date periodoDate = null;
		try {
			periodoDate = formatoDePeriodos.parse(periodo);
		} catch (Exception e) {
			periodoDate = null;
		}

		StringBuilder mensaje = new StringBuilder("");
		BigDecimal cant = !codPrestaci
				.equals(WebKeysLiquidaciones.PRESTACION_TRANSPORTE) ? new BigDecimal(
				cantidad)
				: new BigDecimal(1);
		BigDecimal impo = new BigDecimal(importe);
		BigDecimal importe_ant = new BigDecimal(importe_anterior);
		BigDecimal cant_ant = new BigDecimal(cantidad_anterior);
		impo = impo.subtract(importe_ant);
		cant = cant.subtract(cant_ant);
		BigDecimal cantRealPeriodo = cant;

		boolean prestacionEsTratamiento = false;		
		boolean periodoEntreFechas = false;
		boolean esDocumentacionFaltante = false;

		BigDecimal cantidadPrestacionesPeriodo = BigDecimal.ZERO;
		BigDecimal totalPrestacionesPeriodo = BigDecimal.ZERO;
		BigDecimal cantidadPrestacionesAutorizado = BigDecimal.ZERO;
		BigDecimal valorTotalPrestacionesAutorizado = BigDecimal.ZERO;
		BigDecimal totalPrestacionesAutorizado = impo;

		String periodicidad = "";

		for (TratamientoDiscapacidad tratamientoDiscapacidad : tratamientos) {

			periodicidad = tratamientoDiscapacidad.getPeriodicidad();
			
			prestacionEsTratamiento = true;

			if ((DateUtils.getFirstDateOfMonth(tratamientoDiscapacidad.getPeriodo_desde(), true).before(
						periodoDate) || DateUtils.getFirstDateOfMonth(tratamientoDiscapacidad
						.getPeriodo_desde(), true).compareTo(periodoDate) == 0)
						&& DateUtils.getLastDateOfMonth(
								tratamientoDiscapacidad.getPeriodo_hasta(),
								true).after(periodoDate)) {
					periodoEntreFechas = true;
						
				// documentación faltante
				if (tratamientoDiscapacidad.getEstado() == 2) {
					esDocumentacionFaltante = true;
				}
					
				cantidadPrestacionesAutorizado = cantidadPrestacionesAutorizado
						.add(tratamientoDiscapacidad.getCantidad());
				valorTotalPrestacionesAutorizado = valorTotalPrestacionesAutorizado
						.add(tratamientoDiscapacidad.getCantidad().multiply(
								tratamientoDiscapacidad.getImporte_total()));
	
				// valido periodicidad
				if (tratamientoDiscapacidad.getPeriodicidad().equalsIgnoreCase(
						"Diario")) {
	
					cantRealPeriodo = cant.multiply(new BigDecimal(31));// valido
																		// mensual
					cantidadPrestacionesPeriodo = ReintegroServiceUtil
							.getCantidadPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils
											.getDayBegin(periodoDate), DateUtils
											.getDayEnd(DateUtils
													.getLastDateOfMonth(
															periodoDate, true)),
									cuit, sucu);
					totalPrestacionesPeriodo = ReintegroServiceUtil
							.getTotalPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils
											.getDayBegin(periodoDate), DateUtils
											.getDayEnd(DateUtils
													.getLastDateOfMonth(
															periodoDate, true)),
									cuit, sucu);
	
				} else if (tratamientoDiscapacidad.getPeriodicidad()
						.equalsIgnoreCase("Semanal")) {
	
					cantRealPeriodo = cant.multiply(new BigDecimal(5));// valido
																		// mensual
					cantidadPrestacionesPeriodo = ReintegroServiceUtil
							.getCantidadPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfWeek(
											fecha, true), DateUtils
											.getLastDateOfWeek(fecha, true), cuit,
									sucu);
					totalPrestacionesPeriodo = ReintegroServiceUtil
							.getTotalPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfWeek(
											fecha, true), DateUtils
											.getLastDateOfWeek(fecha, true), cuit,
									sucu);
	
				} else if (tratamientoDiscapacidad.getPeriodicidad()
						.equalsIgnoreCase("Mensual")) {
	
					cantidadPrestacionesPeriodo = ReintegroServiceUtil
							.getCantidadPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfMonth(
											periodoDate, true), DateUtils
											.getLastDateOfMonth(periodoDate, true),
									cuit, sucu);
					totalPrestacionesPeriodo = ReintegroServiceUtil
							.getTotalPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfMonth(
											periodoDate, true), DateUtils
											.getLastDateOfMonth(periodoDate, true),
									cuit, sucu);
	
				} else if (tratamientoDiscapacidad.getPeriodicidad()
						.equalsIgnoreCase("Anual")) {
	
					cantidadPrestacionesPeriodo = ReintegroServiceUtil
							.getCantidadPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfYear(
											fecha, true), DateUtils
											.getLastDateOfYear(fecha, true), cuit,
									sucu);
					totalPrestacionesPeriodo = ReintegroServiceUtil
							.getTotalPrestacionesEntreFechas(cuil, inte,
									id_prestacion, DateUtils.getFirstDateOfYear(
											fecha, true), DateUtils
											.getLastDateOfYear(fecha, true), cuit,
									sucu);	
				}
			}
		}
		if (periodoEntreFechas) {
			// valido cantidad en periodo
			if ((cantRealPeriodo.add(cantidadPrestacionesPeriodo))
					.compareTo(cantidadPrestacionesAutorizado) > 0) {
				mensaje.append("Ya ha cargado una cantidad total "
						+ periodicidad + " de: "
						+ cantidadPrestacionesPeriodo.toString()
						+ " para dicha prestación de discapacidad,\\n");
				mensaje
						.append("Se está excediendo la cantidad de prestaciones autorizadas para el tratamiento -"
								+ cantidadPrestacionesAutorizado.toString()
								+ "- en el periodo\\n");
			}
			// valido importe total en periodo
			if ((cantRealPeriodo.multiply(totalPrestacionesAutorizado)).add(
					totalPrestacionesPeriodo).compareTo(
					valorTotalPrestacionesAutorizado) > 0) {
				mensaje.append("Ya ha cargado un importe total "
						+ periodicidad
						+ " de: "
						+ totalPrestacionesPeriodo.setScale(2,
								RoundingMode.HALF_DOWN).toString()
						+ " para dicha prestación de discapacidad,\\n");
				mensaje
						.append("Se está excediendo el importe total para la prestación autorizadas -"
								+ valorTotalPrestacionesAutorizado.setScale(2,
										RoundingMode.HALF_DOWN).toString()
								+ "- en el periodo\\n");
			}
		}
		if (mensaje.length() > 0) {
			if (esDocumentacionFaltante) {
				mensaje
						.append(" Además, Documentación faltante para el afiliado con discapacidad.");
				motivoAlta
						.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_DOC_FALTANTE_Y_PERIODO_DUPLICADO_EXCEDIDO);
			} else {
				motivoAlta
						.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_PERIODO_DUPLICADO_EXCEDIDO);
				motivoAlta.setMensajeAltaEstado(mensaje.toString());
			}
			motivoAlta.setMensajeAltaEstado(mensaje.toString());
			return motivoAlta;
		}
		if (esDocumentacionFaltante) {
			mensaje
					.append("Documentación faltante para el afiliado con discapacidad.");
			motivoAlta
					.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_DOC_FALTANTE);
			motivoAlta.setMensajeAltaEstado(mensaje.toString());
			return motivoAlta;
		}
		if (!prestacionEsTratamiento) {
			mensaje
					.append("No hay tratamiento de discapacidad para dicho afiliado, prestador y código.");
			motivoAlta
					.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_SIN_TRATAMIENTO);
			motivoAlta.setMensajeAltaEstado(mensaje.toString());
			return motivoAlta;
		}
		if (!periodoEntreFechas) {
			mensaje
					.append("El periodo de la prestación no está dentro de la fecha del tratamiento de discapacidad autorizado.");
			motivoAlta
					.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_PER_INCORRECTO);
			motivoAlta.setMensajeAltaEstado(mensaje.toString());
			return motivoAlta;
		}
		motivoAlta
				.setEstadoAlta(WebKeysLiquidaciones.MOTIVO_ALTA_POR_DISCAPACIDAD_ESTADO_AUTORIZADO);
		motivoAlta.setMensajeAltaEstado(mensaje.toString());
		return motivoAlta;
	}

	public static void cambiarEstadoTratamiento(int id_tratamiento, int estado,
			String userName) throws SystemException {
		getInstance()
				.cambiarEstadoTratamiento(id_tratamiento, estado, userName);
	}
}