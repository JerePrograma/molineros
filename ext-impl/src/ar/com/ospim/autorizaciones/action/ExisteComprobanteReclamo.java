package ar.com.ospim.autorizaciones.action;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

public class ExisteComprobanteReclamo extends JSONAction implements Comparator<PrestacionesReclamo> {

	private static Log _log = LogFactoryUtil.getLog(ExisteComprobanteReclamo.class);


	public String getJSON(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws Exception {
		boolean existe = false;
		String mensaje = "";
		try {
			PrestacionesReclamo prestacionDesdeRequest = getPrestacionesReclamosFromRequest(req);
			if (!tieneDatosMinimosComprobante(prestacionDesdeRequest)) {
				return construirResultadoJson(false, "No se puede validar el comprobante. " + "Complete tipo, letra, número, sucursal, CUIT " + "y fecha de prestación.");
			}
			if (!tienePrestacionOMedicamento(prestacionDesdeRequest)) {
				return construirResultadoJson(false, "No se puede validar el comprobante porque no se " + "identificó la prestación o el medicamento.");
			}
			mensaje = ReclamosPrestacionesServiceUtil.validarExisteComprobante(prestacionDesdeRequest);
			if (!estaVacio(mensaje)) {
				return construirResultadoJson(false, mensaje);
			}
			@SuppressWarnings("unchecked")
			List<PrestacionesReclamo> prestaciones = (List<PrestacionesReclamo>) req.getSession().getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
			if (prestaciones != null && !prestaciones.isEmpty()) {
				for (PrestacionesReclamo prestacionSesion : prestaciones) {
					if (prestacionSesion == null) {
						continue;
					}
					if (prestacionSesion.getBajaFecha() != null) {
						continue;
					}
					if (!tieneDatosMinimosComprobante(prestacionSesion)) {
						_log.warn("Se omite la validación de una prestación de sesión " + "por tener datos de comprobante incompletos. " + "Id registro: " + prestacionSesion.getIdregistroString());
						continue;
					}
					if (!tienePrestacionOMedicamento(prestacionSesion)) {
						_log.warn("Se omite la validación de una prestación de sesión " + "porque no tiene prestación ni medicamento. " + "Id registro: " + prestacionSesion.getIdregistroString());
						continue;
					}
					mensaje = ReclamosPrestacionesServiceUtil.validarExisteComprobante(prestacionSesion);
					if (!estaVacio(mensaje)) {
						return construirResultadoJson(false, mensaje);
					}
				}
				for (PrestacionesReclamo prestacionSesion : prestaciones) {
					if (prestacionSesion == null) {
						continue;
					}
					if (prestacionSesion.getBajaFecha() != null) {
						continue;
					}
					if (compare(prestacionSesion, prestacionDesdeRequest) == 1) {
						existe = true;
						break;
					}
				}
			}
			return construirResultadoJson(existe, "");
		} catch (Exception e) {
			_log.error("Error inesperado al validar la existencia del comprobante", e);
			return construirResultadoJson(false, "No se pudo validar el comprobante. Intente nuevamente.");
		}
	}

	public PrestacionesReclamo getPrestacionesReclamosFromRequest(HttpServletRequest req) {

		String frecuencia = ParamUtil.getString(req, "frecuencia");
		double importe = ParamUtil.getDouble(req, "importe");
		int troquel = ParamUtil.getInteger(req, "troquel",0) != 0 ? ParamUtil.getInteger(req, "troquel",0) :  ParamUtil.getInteger(req, "id_medicamento_edit",0) ;
		String prestacion = ParamUtil.getString(req, "prestacion");
		if (prestacion != null && prestacion.equalsIgnoreCase("Graba Edicion")){
			prestacion = ParamUtil.getString(req, "codigoSeguimiento_filtro_edit");
		}

		int tiponomenclador = ParamUtil.getInteger(req, "tiponomenclador") != 0  ? ParamUtil.getInteger(req, "tiponomenclador") : ParamUtil.getInteger(req, "nom_seleccionado_edit",0); ;
		int cantidad = ParamUtil.getInteger(req, "cantidad");
		String nombreMedicamento = ParamUtil.getString(req, "nombre_medicamento");
		if (estaVacio(nombreMedicamento)) {
			nombreMedicamento = ParamUtil.getString(req, "nombre_medicamento_edit");
		}
		String nombrePrestacion = ParamUtil.getString(req, "nombre_prestacion");
		if (estaVacio(nombrePrestacion) && "Graba Edicion".equalsIgnoreCase(ParamUtil.getString(req, "prestacion"))) {
			nombrePrestacion = ParamUtil.getString(req, "descripcionSeguimiento_filtro_edit");
		}
		int tipoNomnecladorPrestacion= ParamUtil.getInteger(req, "tiponomnecladorprestacion");
		if (tipoNomnecladorPrestacion == 0) {
			tipoNomnecladorPrestacion = ParamUtil.getInteger(req, "tipoNomenclador_edit", 0);
		}

		String cpbteTipo = ParamUtil.getString(req, "cpbte_tipo");
		String cpbteNro = ParamUtil.getString(req, "cpbte_nro");
		int cpbteDia = ParamUtil.getInteger(req, "cpbte_dia");
		int cpbteMes = ParamUtil.getInteger(req, "cpbte_mes");
		int cpbteAnio = ParamUtil.getInteger(req, "cpbte_anio");
		Double cpbteCantidad = ParamUtil.getDouble(req, "cpbte_cantidad");
		Double cpbteImporte = ParamUtil.getDouble(req, "cpbte_importe");
		Double cpbteTotal = ParamUtil.getDouble(req, "importeFC");
		String cpbteCUIT = ParamUtil.getString(req, "cpbte_cuit");
		String cpbteSucursal = ParamUtil.getString(req, "cpbte_sucursal");
		String cpbteCuitSucursal = ParamUtil.getString(req, "cpbte_cuit_sucursal");

		String comprobanteLetra = ParamUtil.getString(req, "cpbte_letra");

		int fechaPrestacionDia = ParamUtil.getInteger(req, "fecha_prestacion_dia");
		int fechaPrestacionMes = ParamUtil.getInteger(req, "fecha_prestacion_mes");
		int fechaPrestacionAnio = ParamUtil.getInteger(req, "fecha_prestacion_anio");

		int idRegistro = ParamUtil.getInteger(req, "idRegistro");

		String cuil = ParamUtil.getString(req, "cuil");
		int inte = ParamUtil.getInteger(req, "inte");

		Date cpbteFecha = crearFecha(cpbteDia, cpbteMes, cpbteAnio);
		Date fechaPrestacion = crearFecha(fechaPrestacionDia, fechaPrestacionMes, fechaPrestacionAnio);
		int Idprestacion = 0;
		try {
			if (tiponomenclador == 1 && !estaVacio(prestacion)) {
				List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(
						tipoNomnecladorPrestacion, "", 0, prestacion, false, "");
				if (nomencladores != null) {
					for (Nomenclador nom : nomencladores) {
						if (nom != null && prestacion.equals(nom.getCodigo())) {
							Idprestacion = nom.getId_prestacion();
						}
					}
				}
				if (Idprestacion == 0) {
					_log.debug("Error en la busqueda de id prestacion : ");
				}
			}
		} catch (SystemException e) {
			_log.error("Error en la busqueda de id prestacion", e);
		}

		PrestacionesReclamo prestacionreclamo = new PrestacionesReclamo(null,frecuencia,0,importe,0,Idprestacion ,
				troquel ,tiponomenclador,nombreMedicamento,nombrePrestacion,false,cantidad,
				cpbteTipo, cpbteNro,cpbteFecha, cpbteCantidad, cpbteImporte,cpbteTotal,cpbteCUIT,cpbteSucursal,
				cpbteCuitSucursal,comprobanteLetra, fechaPrestacion, 0);
		prestacionreclamo.setEstado(PrestacionesReclamo.ESTADOS.NUEVO );
		prestacionreclamo.setIdRegistro(idRegistro);
		prestacionreclamo.setCuilTitular(cuil);
		prestacionreclamo.setInte(inte);
		return prestacionreclamo;
	}


	@Override
	public int compare(PrestacionesReclamo o1, PrestacionesReclamo o2) {
		if (!tieneDatosMinimosComprobante(o1) || !tieneDatosMinimosComprobante(o2)
				|| o1.getIdRegistro() == o2.getIdRegistro()) {
			return 0;
		}
		String sucursalO1 = normalizarNumeroComprobante(o1.getComprobanteSucursal());
		String sucursalO2 = normalizarNumeroComprobante(o2.getComprobanteSucursal());
		String numeroO1 = normalizarNumeroComprobante(o1.getComprobanteNro());
		String numeroO2 = normalizarNumeroComprobante(o2.getComprobanteNro());
		if (sucursalO1 == null || sucursalO2 == null || numeroO1 == null || numeroO2 == null) {
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (!mismoTexto(o1.getComprobanteLetra(), o2.getComprobanteLetra())
				|| !mismoTexto(o1.getComprobanteTipo(), o2.getComprobanteTipo())
				|| !mismoTexto(o1.getComprobanteCUIT(), o2.getComprobanteCUIT())
				|| !sdf.format(o1.getFechaPrestacion()).equals(sdf.format(o2.getFechaPrestacion()))
				|| !sucursalO1.equals(sucursalO2) || !numeroO1.equals(numeroO2)) {
			return 0;
		}
		if (o1.getId_medicamento() != 0) {
			return o1.getId_medicamento() == o2.getId_medicamento() ? 1 : 0;
		}
		return o1.getId_prestacion() == o2.getId_prestacion() ? 1 : 0;
	}

	private boolean tieneDatosMinimosComprobante(PrestacionesReclamo prestacion) {
		return prestacion != null && !estaVacio(prestacion.getComprobanteTipo())
				&& !estaVacio(prestacion.getComprobanteNro()) && !estaVacio(prestacion.getComprobanteCUIT())
				&& !estaVacio(prestacion.getComprobanteLetra()) && !estaVacio(prestacion.getComprobanteSucursal())
				&& prestacion.getFechaPrestacion() != null;
	}

	private boolean tienePrestacionOMedicamento(PrestacionesReclamo prestacion) {
		return prestacion != null && (prestacion.getId_prestacion() > 0 || prestacion.getId_medicamento() > 0);
	}

	private Date crearFecha(int dia, int mes, int anio) {
		// Los selectores legacy envian Calendar.MONTH (0 a 11).
		if (dia <= 0 || mes < Calendar.JANUARY || mes > Calendar.DECEMBER || anio <= 0) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setLenient(false);
		try {
			calendar.set(anio, mes, dia, 0, 0, 0);
			return calendar.getTime();
		} catch (IllegalArgumentException e) {
			_log.warn("Fecha inválida recibida. Día: " + dia + ", mes: " + mes + ", año: " + anio);
			return null;
		}
	}

	private boolean estaVacio(String valor) {
		return valor == null || valor.trim().length() == 0 || "undefined".equalsIgnoreCase(valor.trim()) || "null".equalsIgnoreCase(valor.trim());
	}

	private boolean mismoTexto(String valor1, String valor2) {
		if (estaVacio(valor1) || estaVacio(valor2)) {
			return false;
		}
		return valor1.trim().equals(valor2.trim());
	}

	private String normalizarNumeroComprobante(String valor) {
		if (estaVacio(valor)) {
			return null;
		}
		String numero = valor.trim();
		for (int indice = 0; indice < numero.length(); indice++) {
			char caracter = numero.charAt(indice);
			if (caracter < '0' || caracter > '9') {
				return null;
			}
		}
		int primerDigitoSignificativo = 0;
		while (primerDigitoSignificativo < numero.length() - 1 && numero.charAt(primerDigitoSignificativo) == '0') {
			primerDigitoSignificativo++;
		}
		return numero.substring(primerDigitoSignificativo);
	}

	private String construirResultadoJson(boolean existe, String mensaje) {
		return "{ \"existe\" : \"" + existe + "\",\"mensajeError\" : \"" + escaparJson(mensaje) + "\" }";
	}

	private String escaparJson(String valor) {
		if (valor == null) {
			return "";
		}
		StringBuilder resultado = new StringBuilder(valor.length() + 16);
		for (int indice = 0; indice < valor.length(); indice++) {
			char caracter = valor.charAt(indice);
			switch (caracter) {
				case '"':
				resultado.append("\\\"");
				break;
				case '\\':
				resultado.append("\\\\");
				break;
				case '\b':
				resultado.append("\\b");
				break;
				case '\f':
				resultado.append("\\f");
				break;
				case '\n':
				resultado.append("\\n");
				break;
				case '\r':
				resultado.append("\\r");
				break;
				case '\t':
				resultado.append("\\t");
				break;
				default:
				if (caracter < 0x20) {
					String hexadecimal = Integer.toHexString(caracter);
					resultado.append("\\u");
					for (int relleno = hexadecimal.length(); relleno < 4; relleno++) {
						resultado.append('0');
					}
					resultado.append(hexadecimal);
				} else {
					resultado.append(caracter);
				}
				break;
			}
		}
		return resultado.toString();
	}
}
