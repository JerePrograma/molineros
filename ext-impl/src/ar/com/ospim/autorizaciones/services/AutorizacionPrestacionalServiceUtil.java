package ar.com.ospim.autorizaciones.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.AutoPrestacional;
import ar.com.ospim.liquidaciones.DuplicateTratamientoDiscapacidadIdException;
import ar.com.ospim.liquidaciones.ImposibleBorrarTratamientoDiscapacidadException;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.beans.MotivoAltaDiscapacidad;
import ar.com.ospim.liquidaciones.services.ReintegroServiceUtil;
import ar.com.ospim.util.DateUtils;

public class AutorizacionPrestacionalServiceUtil {

	private static AutorizacionPrestacionalServiceImpl instance = null;

	public static AutorizacionPrestacionalServiceImpl getInstance() {
		if (null == instance) {
			instance = new AutorizacionPrestacionalServiceImpl();
		}
		return instance;
	}

	public static List<AutorizacionPrestacional> buscarAutorizacionPrestacional(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador,
			String cuit, String prestador, int numero, int estado,
			String codPrestaci) throws Exception {
		return getInstance().buscarAutorizacionPrestacional(entidad,
				fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, codPrestad,
				id_prestador, cuit, prestador, numero, estado, codPrestaci);
	}

	
	public static List<AutorizacionPrestacional> buscarAutorizacionPrestacional(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador,
			String cuit, String prestador, int numero, int estado,
			String codPrestaci,Boolean incluyeAntiguos,Integer nroAutorizacion) throws Exception {
		return getInstance().buscarAutorizacionPrestacional(entidad,
				fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, codPrestad,
				id_prestador, cuit, prestador, numero, estado, codPrestaci,incluyeAntiguos,nroAutorizacion,false,false,false,null);
	}
	
	public static List<AutorizacionPrestacional> buscarAutorizacionPrestacional(
			String entidad, Date fechaDesde, Date fechaHasta, int nroAfi,
			int inte, String cuil_titular, int codPrestad, int id_prestador,
			String cuit, String prestador, int numero, int estado,
			String codPrestaci,Boolean incluyeAntiguos,Integer nroAutorizacion,
			Boolean discapacidad, Boolean leche,Boolean dependencia,Integer pagina) throws Exception {
		return getInstance().buscarAutorizacionPrestacional(entidad,
				fechaDesde, fechaHasta, nroAfi, inte, cuil_titular, codPrestad,
				id_prestador, cuit, prestador, numero, estado, codPrestaci,incluyeAntiguos,
				nroAutorizacion,discapacidad,leche,dependencia,pagina);
	}
	
	public static AutorizacionPrestacional getAutorizacionPrestacional(int id)
			throws SystemException {
		AutorizacionPrestacional td = getInstance().getAutorizacionPrestacional(
				id);
		if (td != null) {
			td.setDocumentosFaltantes(getInstance()
					.getDocFaltanteAutorizacionPrestacional(id));
		}
		return td;
	}

	public static int save(AutoPrestacional autoPrestacionales, int idPreautorizacion)

	throws DuplicatePrestadorIdException, SystemException,
			DuplicateTratamientoDiscapacidadIdException {

		int[] documentos = null;
		if (autoPrestacionales.getDocumentacion() != null && !autoPrestacionales.getDocumentacion().equals("null")) {
			String[] aux = autoPrestacionales.getDocumentacion().split(",");
			documentos = new int[aux.length];
			for (int i = 0; i < aux.length; i++) {
				documentos[i] = Integer.parseInt(aux[i]);
			}
		}
		//Graba
		int id_tratamiento = getInstance().save(autoPrestacionales, idPreautorizacion);
		if (id_tratamiento != 0 && documentos != null) {
			for (int d : documentos) {
				getInstance().cargarDocumentosFaltantes(id_tratamiento, d,
						autoPrestacionales.getUser().getScreenName());
			}
		}

		return id_tratamiento;
	}

	public static void update(AutoPrestacional autoPrestacionales,  int idPreautorizacion)
			throws SystemException {
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");

		int[] documentos = null;
		if (autoPrestacionales.getDocumentacion()!= null && !autoPrestacionales.getDocumentacion().equals("null")) {
			String[] aux = autoPrestacionales.getDocumentacion().split(",");
			documentos = new int[aux.length];
			for (int i = 0; i < aux.length; i++) {
				documentos[i] = Integer.parseInt(aux[i]);
			}
		}
		if (autoPrestacionales.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE) {
			if (documentos == null
					|| (documentos != null && documentos.length == 0)) {
				autoPrestacionales.setEstado(WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO);
			}
		}
		if (autoPrestacionales.getEstado() == WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_EN_CURSO) {
			if (documentos != null && documentos.length > 0) {
					autoPrestacionales.setEstado(WebKeysLiquidaciones.TRATAMIENTO_DISCA_ESTADO_DOC_FALTANTE);
			}
		}

		getInstance().update(autoPrestacionales, idPreautorizacion);

		getInstance().borrarDocumentosFaltantes(autoPrestacionales.getIdTratamiento());
		if (documentos != null && autoPrestacionales.getIdTratamiento() != 0 && autoPrestacionales.getDocumentacion() != null && autoPrestacionales.getDocumentacion().length() > 0) {
			for (int d : documentos) {
				getInstance().cargarDocumentosFaltantes(autoPrestacionales.getIdTratamiento(), d,
						autoPrestacionales.getUser().getScreenName());
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
		List<AutorizacionPrestacional> tratamientos = buscarAutorizacionPrestacional(
				null, null, null, 0, inte, cuil, 0, 0, cuit, null, 0, 0,
				codPrestaci);

		Iterator<AutorizacionPrestacional> iterator = tratamientos.iterator();
		while (iterator.hasNext()) {
			AutorizacionPrestacional tratamientoDiscapacidad = iterator.next();
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

		for (AutorizacionPrestacional tratamientoDiscapacidad : tratamientos) {

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

	public static void cambiarEstadoAutorizacion(int id_tratamiento, int estado,
			String userName,String motivo) throws SystemException {
		getInstance().cambiarEstadoAutorizacion(id_tratamiento, estado, userName,motivo);
	}
	
	
	public static void marcaEmailSendPrestador(int id) throws SQLException {
		getInstance().marcaEmailSendPrestador(id);
	}

	public static List<AutorizacionPrestacional> getHistoricoAutorizaciones(int idTratamiento)
        throws SystemException {
    return getInstance().getHistoricoAutorizaciones(idTratamiento);
}

}