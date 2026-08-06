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
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ExisteComprobanteReclamo extends JSONAction implements Comparator<PrestacionesReclamo> {
	private static Log _log = LogFactoryUtil.getLog(ExisteComprobanteReclamo.class);


    public String getJSON(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest req,
            HttpServletResponse response) throws Exception {

        boolean existe = false;
        String mensaje = "";

        try {
            PrestacionesReclamo prestacionDesdeRequest =
                    getPrestacionesReclamosFromRequest(req);

            /*
             * El comprobante que se está editando o agregando debe estar completo.
             * Ante datos incompletos se responde JSON y no se continúa al servicio
             * ni al método compare().
             */
            if (!tieneDatosMinimosComprobante(prestacionDesdeRequest)) {
                return construirResultadoJson(
                        false,
                        "No se puede validar el comprobante. " +
                                "Complete tipo, letra, número, sucursal, CUIT " +
                                "y fecha de prestación."
                );
            }

            if (!tienePrestacionOMedicamento(prestacionDesdeRequest)) {
                return construirResultadoJson(
                        false,
                        "No se puede validar el comprobante porque no se " +
                                "identificó la prestación o el medicamento."
                );
            }

            /*
             * Primero se valida el comprobante actualmente informado.
             */
            mensaje =
                    ReclamosPrestacionesServiceUtil
                            .validarExisteComprobante(
                                    prestacionDesdeRequest
                            );

            if (!estaVacio(mensaje)) {
                return construirResultadoJson(
                        false,
                        mensaje
                );
            }

            @SuppressWarnings("unchecked")
            List<PrestacionesReclamo> prestaciones =
                    (List<PrestacionesReclamo>) req.getSession()
                            .getAttribute(
                                    WebKeysAutorizaciones
                                            .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
                            );

            if (prestaciones != null && !prestaciones.isEmpty()) {

                /*
                 * Se conserva la validación contra base de las prestaciones
                 * almacenadas en sesión.
                 *
                 * Las prestaciones antiguas o incompletas se omiten porque no
                 * pueden formar un comprobante comparable. No se las envía al
                 * procedimiento almacenado con fechas o números nulos.
                 */
                for (PrestacionesReclamo prestacionSesion : prestaciones) {
                    if (prestacionSesion == null) {
                        continue;
                    }

                    if (prestacionSesion.getBajaFecha() != null) {
                        continue;
                    }

                    if (!tieneDatosMinimosComprobante(prestacionSesion)) {
                        _log.warn(
                                "Se omite la validación de una prestación de sesión " +
                                        "por tener datos de comprobante incompletos. " +
                                        "Id registro: " +
                                        prestacionSesion.getIdregistroString()
                        );
                        continue;
                    }

                    if (!tienePrestacionOMedicamento(prestacionSesion)) {
                        _log.warn(
                                "Se omite la validación de una prestación de sesión " +
                                        "porque no tiene prestación ni medicamento. " +
                                        "Id registro: " +
                                        prestacionSesion.getIdregistroString()
                        );
                        continue;
                    }

                    mensaje =
                            ReclamosPrestacionesServiceUtil
                                    .validarExisteComprobante(
                                            prestacionSesion
                                    );

                    if (!estaVacio(mensaje)) {
                        return construirResultadoJson(
                                false,
                                mensaje
                        );
                    }
                }

                /*
                 * Luego se valida que el comprobante actual no esté repetido
                 * dentro de la lista mantenida en sesión.
                 */
                for (PrestacionesReclamo prestacionSesion : prestaciones) {
                    if (prestacionSesion == null) {
                        continue;
                    }

                    if (prestacionSesion.getBajaFecha() != null) {
                        continue;
                    }

                    if (compare(
                            prestacionSesion,
                            prestacionDesdeRequest
                    ) == 1) {
                        existe = true;
                        break;
                    }
                }
            }

            return construirResultadoJson(
                    existe,
                    ""
            );

        } catch (Exception e) {
            /*
             * Este catch está intencionalmente en el límite de la acción JSON.
             * Evita que Struts/Liferay genere una página HTML que luego sea
             * enviada a $.parseJSON().
             */
            _log.error(
                    "Error inesperado al validar la existencia del comprobante",
                    e
            );

            return construirResultadoJson(
                    false,
                    "No se pudo validar el comprobante. Intente nuevamente."
            );
        }
    }


    public PrestacionesReclamo getPrestacionesReclamosFromRequest(
            HttpServletRequest req) {

        String frecuencia =
                ParamUtil.getString(
                        req,
                        "frecuencia"
                );

        double importe =
                ParamUtil.getDouble(
                        req,
                        "importe"
                );

        int troquel =
                ParamUtil.getInteger(
                        req,
                        "troquel",
                        0
                );

        if (troquel == 0) {
            troquel =
                    ParamUtil.getInteger(
                            req,
                            "id_medicamento_edit",
                            0
                    );
        }

        String prestacion =
                ParamUtil.getString(
                        req,
                        "prestacion"
                );

        boolean esEdicion =
                "Graba Edicion".equalsIgnoreCase(
                        prestacion
                );

        if (esEdicion) {
            prestacion =
                    ParamUtil.getString(
                            req,
                            "codigoSeguimiento_filtro_edit"
                    );
        }

        int tipoNomenclador =
                ParamUtil.getInteger(
                        req,
                        "tiponomenclador",
                        0
                );

        if (tipoNomenclador == 0) {
            tipoNomenclador =
                    ParamUtil.getInteger(
                            req,
                            "nom_seleccionado_edit",
                            0
                    );
        }

        int cantidad =
                ParamUtil.getInteger(
                        req,
                        "cantidad"
                );

        String nombreMedicamento =
                ParamUtil.getString(
                        req,
                        "nombre_medicamento"
                );

        if (estaVacio(nombreMedicamento)) {
            nombreMedicamento =
                    ParamUtil.getString(
                            req,
                            "nombre_medicamento_edit"
                    );
        }

        String nombrePrestacion =
                ParamUtil.getString(
                        req,
                        "nombre_prestacion"
                );

        if (estaVacio(nombrePrestacion) && esEdicion) {
            nombrePrestacion =
                    ParamUtil.getString(
                            req,
                            "descripcionSeguimiento_filtro_edit"
                    );
        }

        int tipoNomencladorPrestacion =
                ParamUtil.getInteger(
                        req,
                        "tiponomnecladorprestacion",
                        0
                );

        /*
         * En edición, el request comprobado contiene:
         *
         * tipoNomenclador_edit=3
         *
         * mientras tiponomnecladorprestacion llega como undefined.
         * Sin este fallback la búsqueda del id de prestación se ejecuta
         * con tipo 0.
         */
        if (tipoNomencladorPrestacion == 0) {
            tipoNomencladorPrestacion =
                    ParamUtil.getInteger(
                            req,
                            "tipoNomenclador_edit",
                            0
                    );
        }

        String cpbteTipo =
                ParamUtil.getString(
                        req,
                        "cpbte_tipo"
                );

        String cpbteNro =
                ParamUtil.getString(
                        req,
                        "cpbte_nro"
                );

        int cpbteDia =
                ParamUtil.getInteger(
                        req,
                        "cpbte_dia"
                );

        int cpbteMes =
                ParamUtil.getInteger(
                        req,
                        "cpbte_mes"
                );

        int cpbteAnio =
                ParamUtil.getInteger(
                        req,
                        "cpbte_anio"
                );

        Double cpbteCantidad =
                ParamUtil.getDouble(
                        req,
                        "cpbte_cantidad"
                );

        Double cpbteImporte =
                ParamUtil.getDouble(
                        req,
                        "cpbte_importe"
                );

        Double cpbteTotal =
                ParamUtil.getDouble(
                        req,
                        "importeFC"
                );

        String cpbteCUIT =
                ParamUtil.getString(
                        req,
                        "cpbte_cuit"
                );

        String cpbteSucursal =
                ParamUtil.getString(
                        req,
                        "cpbte_sucursal"
                );

        String cpbteCuitSucursal =
                ParamUtil.getString(
                        req,
                        "cpbte_cuit_sucursal"
                );

        String comprobanteLetra =
                ParamUtil.getString(
                        req,
                        "cpbte_letra"
                );

        int fechaPrestacionDia =
                ParamUtil.getInteger(
                        req,
                        "fecha_prestacion_dia"
                );

        int fechaPrestacionMes =
                ParamUtil.getInteger(
                        req,
                        "fecha_prestacion_mes"
                );

        int fechaPrestacionAnio =
                ParamUtil.getInteger(
                        req,
                        "fecha_prestacion_anio"
                );

        int idRegistro =
                ParamUtil.getInteger(
                        req,
                        "idRegistro"
                );

        String cuil =
                ParamUtil.getString(
                        req,
                        "cuil"
                );

        int inte =
                ParamUtil.getInteger(
                        req,
                        "inte"
                );

        Date cpbteFecha =
                crearFecha(
                        cpbteDia,
                        cpbteMes,
                        cpbteAnio
                );

        Date fechaPrestacion =
                crearFecha(
                        fechaPrestacionDia,
                        fechaPrestacionMes,
                        fechaPrestacionAnio
                );

        int idPrestacion = 0;

        if (tipoNomenclador == 1 && !estaVacio(prestacion)) {
            try {
                List<Nomenclador> nomencladores =
                        NomencladorServiceUtil.getListaNomenclador(
                                tipoNomencladorPrestacion,
                                "",
                                0,
                                prestacion,
                                false,
                                ""
                        );

                if (nomencladores != null) {
                    for (Nomenclador nomenclador : nomencladores) {
                        if (nomenclador == null) {
                            continue;
                        }

                        if (prestacion.equals(nomenclador.getCodigo())) {
                            idPrestacion =
                                    nomenclador.getId_prestacion();
                            break;
                        }
                    }
                }

                if (idPrestacion == 0) {
                    _log.error(
                            "No se encontró el id de prestación. " +
                                    "Código: " + prestacion +
                                    ", tipo nomenclador: " +
                                    tipoNomencladorPrestacion
                    );
                }

            } catch (SystemException e) {
                _log.error(
                        "Error al buscar el id de prestación. " +
                                "Código: " + prestacion +
                                ", tipo nomenclador: " +
                                tipoNomencladorPrestacion,
                        e
                );
            }
        }

        PrestacionesReclamo prestacionReclamo =
                new PrestacionesReclamo(
                        null,
                        frecuencia,
                        0,
                        importe,
                        0,
                        idPrestacion,
                        troquel,
                        tipoNomenclador,
                        nombreMedicamento,
                        nombrePrestacion,
                        false,
                        cantidad,
                        cpbteTipo,
                        cpbteNro,
                        cpbteFecha,
                        cpbteCantidad,
                        cpbteImporte,
                        cpbteTotal,
                        cpbteCUIT,
                        cpbteSucursal,
                        cpbteCuitSucursal,
                        comprobanteLetra,
                        fechaPrestacion,
                        0
                );

        prestacionReclamo.setEstado(
                PrestacionesReclamo.ESTADOS.NUEVO
        );

        prestacionReclamo.setIdRegistro(
                idRegistro
        );

        prestacionReclamo.setCuilTitular(
                cuil
        );

        prestacionReclamo.setInte(
                inte
        );

        return prestacionReclamo;
    }


    @Override
    public int compare(
            PrestacionesReclamo o1,
            PrestacionesReclamo o2) {

        if (!tieneDatosMinimosComprobante(o1)) {
            return 0;
        }

        if (!tieneDatosMinimosComprobante(o2)) {
            return 0;
        }

        String sucursalO1 =
                normalizarNumeroComprobante(
                        o1.getComprobanteSucursal()
                );

        String sucursalO2 =
                normalizarNumeroComprobante(
                        o2.getComprobanteSucursal()
                );

        String numeroO1 =
                normalizarNumeroComprobante(
                        o1.getComprobanteNro()
                );

        String numeroO2 =
                normalizarNumeroComprobante(
                        o2.getComprobanteNro()
                );

        /*
         * Si algún número no es válido, no puede considerarse comprobante
         * duplicado. No se ejecuta Integer.parseInt(), por lo que tampoco
         * puede producirse NumberFormatException por null, vacío, texto
         * inválido o desbordamiento de Integer.
         */
        if (sucursalO1 == null
                || sucursalO2 == null
                || numeroO1 == null
                || numeroO2 == null) {
            return 0;
        }

        Integer idRegistroO1 =
                o1.getIdRegistro();

        Integer idRegistroO2 =
                o2.getIdRegistro();

        /*
         * El mismo registro no debe compararse contra sí mismo.
         * Si alguno no tiene identificador, se continúa porque pueden ser
         * dos registros nuevos diferentes.
         */
        if (idRegistroO1 != null
                && idRegistroO2 != null
                && idRegistroO1.intValue()
                == idRegistroO2.intValue()) {
            return 0;
        }

        if (!mismoTexto(
                o1.getComprobanteLetra(),
                o2.getComprobanteLetra()
        )) {
            return 0;
        }

        if (!mismoTexto(
                o1.getComprobanteTipo(),
                o2.getComprobanteTipo()
        )) {
            return 0;
        }

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );

        String fechaO1 =
                sdf.format(
                        o1.getFechaPrestacion()
                );

        String fechaO2 =
                sdf.format(
                        o2.getFechaPrestacion()
                );

        if (!fechaO1.equals(fechaO2)) {
            return 0;
        }

        if (!mismoTexto(
                o1.getComprobanteCUIT(),
                o2.getComprobanteCUIT()
        )) {
            return 0;
        }

        if (!sucursalO1.equals(sucursalO2)) {
            return 0;
        }

        if (!numeroO1.equals(numeroO2)) {
            return 0;
        }

        Integer idMedicamentoO1 =
                o1.getId_medicamento();

        Integer idMedicamentoO2 =
                o2.getId_medicamento();

        if (idMedicamentoO1 != null
                && idMedicamentoO1.intValue() != 0) {

            if (idMedicamentoO2 != null
                    && idMedicamentoO1.intValue()
                    == idMedicamentoO2.intValue()) {
                return 1;
            }

            return 0;
        }

        Integer idPrestacionO1 =
                o1.getId_prestacion();

        Integer idPrestacionO2 =
                o2.getId_prestacion();

        if (idPrestacionO1 != null
                && idPrestacionO2 != null
                && idPrestacionO1.intValue()
                == idPrestacionO2.intValue()) {
            return 1;
        }

        return 0;
    }

    private boolean tieneDatosMinimosComprobante(
            PrestacionesReclamo prestacion) {

        if (prestacion == null) {
            return false;
        }

        if (estaVacio(prestacion.getComprobanteTipo())) {
            return false;
        }

        if (estaVacio(prestacion.getComprobanteNro())) {
            return false;
        }

        if (estaVacio(prestacion.getComprobanteCUIT())) {
            return false;
        }

        if (estaVacio(prestacion.getComprobanteLetra())) {
            return false;
        }

        if (estaVacio(prestacion.getComprobanteSucursal())) {
            return false;
        }

        if (prestacion.getFechaPrestacion() == null) {
            return false;
        }

        return true;
    }

    private boolean tienePrestacionOMedicamento(
            PrestacionesReclamo prestacion) {

        if (prestacion == null) {
            return false;
        }

        Integer idPrestacion =
                prestacion.getId_prestacion();

        Integer idMedicamento =
                prestacion.getId_medicamento();

        boolean tienePrestacion =
                idPrestacion != null
                        && idPrestacion.intValue() > 0;

        boolean tieneMedicamento =
                idMedicamento != null
                        && idMedicamento.intValue() > 0;

        return tienePrestacion || tieneMedicamento;
    }

    private Date crearFecha(
            int dia,
            int mes,
            int anio) {

        if (dia <= 0
                || mes < 1
                || mes > 12
                || anio <= 0) {
            return null;
        }

        Calendar calendar =
                Calendar.getInstance();

        calendar.clear();
        calendar.setLenient(false);

        try {
            /*
             * Los meses recibidos desde la pantalla son 1 a 12.
             * Calendar utiliza 0 a 11.
             */
            calendar.set(
                    anio,
                    mes - 1,
                    dia,
                    0,
                    0,
                    0
            );

            return calendar.getTime();

        } catch (IllegalArgumentException e) {
            _log.warn(
                    "Fecha inválida recibida. Día: " +
                            dia +
                            ", mes: " +
                            mes +
                            ", año: " +
                            anio
            );

            return null;
        }
    }

    private boolean estaVacio(
            String valor) {

        return valor == null
                || valor.trim().length() == 0
                || "undefined".equalsIgnoreCase(
                valor.trim()
        )
                || "null".equalsIgnoreCase(
                valor.trim()
        );
    }

    private boolean mismoTexto(
            String valor1,
            String valor2) {

        if (estaVacio(valor1)
                || estaVacio(valor2)) {
            return false;
        }

        return valor1.trim().equals(
                valor2.trim()
        );
    }

    private String normalizarNumeroComprobante(
            String valor) {

        if (estaVacio(valor)) {
            return null;
        }

        String numero =
                valor.trim();

        for (int indice = 0;
             indice < numero.length();
             indice++) {

            char caracter =
                    numero.charAt(indice);

            if (caracter < '0'
                    || caracter > '9') {
                return null;
            }
        }

        int primerDigitoSignificativo = 0;

        while (primerDigitoSignificativo
                < numero.length() - 1
                && numero.charAt(
                primerDigitoSignificativo
        ) == '0') {

            primerDigitoSignificativo++;
        }

        return numero.substring(
                primerDigitoSignificativo
        );
    }

    private String construirResultadoJson(
            boolean existe,
            String mensaje) {

        return "{ \"existe\" : \"" +
                existe +
                "\",\"mensajeError\" : \"" +
                escaparJson(mensaje) +
                "\" }";
    }

    private String escaparJson(
            String valor) {

        if (valor == null) {
            return "";
        }

        StringBuilder resultado =
                new StringBuilder(
                        valor.length() + 16
                );

        for (int indice = 0;
             indice < valor.length();
             indice++) {

            char caracter =
                    valor.charAt(indice);

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
                        String hexadecimal =
                                Integer.toHexString(
                                        caracter
                                );

                        resultado.append("\\u");

                        for (int relleno =
                             hexadecimal.length();
                             relleno < 4;
                             relleno++) {
                            resultado.append('0');
                        }

                        resultado.append(
                                hexadecimal
                        );
                    } else {
                        resultado.append(
                                caracter
                        );
                    }
                    break;
            }
        }

        return resultado.toString();
    }
	
	
}