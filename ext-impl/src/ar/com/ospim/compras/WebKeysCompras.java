package ar.com.ospim.compras;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    private static final Pattern DIACRITICOS_SECTOR_COMPRAS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /*
     * Estos valores son filtros para la búsqueda.
     *
     * El valor cero significa "sin filtro positivo específico".
     * No debe persistirse necesariamente como id_tipo_nomenclador:
     * el resultado seleccionado devuelve el tipo real y positivo.
     */
    public static final int FILTRO_NOMENCLADOR_GENERAL = 0;
    public static final int FILTRO_NOMENCLADOR_ODONTOLOGIA = 1;
    public static final int FILTRO_NOMENCLADOR_DISCAPACIDAD = 8;
    public static final int FILTRO_NOMENCLADOR_FARMACIA = 9;
    public static final int MARCA_REIN_LIQ_DISCAPACIDAD = 6;

    public static final String
            CODIGO_ESPECIAL_DISCAPACIDAD = "431003";

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";
    public static final String ROL_COTIZAR_COMPRAS = "COTIZAR_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA =
            "BUSQUEDA_REQUERIMIENTOS_COMPRA";

    public static final String FILTRO_REQUERIMIENTOS_COMPRA =
            "FILTRO_REQUERIMIENTOS_COMPRA";

    public static final String REQUERIMIENTO_COMPRA_EN_EDICION =
            "REQUERIMIENTO_COMPRA_EN_EDICION";

    public static final String REQUERIMIENTO_COMPRA_EN_VIEW =
            "REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION =
            "ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION";

    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW =
            "ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ID_REQUERIMIENTO_COMPRA_EN_EDICION =
            "id_requerimiento_compra";

    public static final String ESTADOS_REQUERIMIENTO =
            "ESTADOS_REQUERIMIENTO";

    public static final String SECTORES_REQUERIMIENTO =
            "SECTORES_REQUERIMIENTO";

    public static final String ESTADOS_REQUERIMIENTO_COMPRA =
            ESTADOS_REQUERIMIENTO;

    public static final String SECTORES_REQUERIMIENTO_COMPRA =
            SECTORES_REQUERIMIENTO;

    public static final String TIPOS_PRESTADOR_SECTOR =
            "TIPOS_PRESTADOR_SECTOR";

    public static final String PRESTADORES_ENVIADOS_COTIZACION =
            "PRESTADORES_ENVIADOS_COTIZACION";

    public static final String ID_SECTOR_CONFIGURACION_COTIZACION =
            "ID_SECTOR_CONFIGURACION_COTIZACION";

    public static final String RESULTADO_NOTIFICACION_COTIZACION =
            "RESULTADO_NOTIFICACION_COTIZACION";

    /*
     * Boolean cargado por los actions que renderizan un requerimiento.
     * La botonera lo utiliza para ocultar el reintento cuando la función
     * canónica de candidatos ya no devuelve prestadores pendientes.
     */
    public static final String HAY_PRESTADORES_PENDIENTES_NOTIFICACION =
            "HAY_PRESTADORES_PENDIENTES_NOTIFICACION";

    public static final String RELACION_RECLAMO_PRESTACIONAL_COMPRA =
            "RELACION_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK =
            "RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK";

    public static final String CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA =
            "CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String PARAM_ID_REQUERIMIENTO_COMPRA =
            "id_requerimiento_compra";

    public static final String PARAM_RECLAMO_PRESTACIONAL_NONCE =
            "compras_reclamo_nonce";

    public static final String VINCULO_RECLAMO_RESERVADO =
            "RESERVADO";

    public static final String VINCULO_RECLAMO_VINCULADO =
            "VINCULADO";

    public static final String VINCULO_RECLAMO_ERROR =
            "ERROR";

    /*
     * Parámetro único del prestador adjudicado para todo el requerimiento.
     * Se mantiene el parseo de los parámetros legacy por detalle durante la
     * transición para no romper pantallas compiladas o formularios antiguos.
     */
    public static final String PARAM_ID_PRESTADOR_ADJUDICADO =
            "id_prestador_adjudicado";

    public static final int ESTADO_PENDIENTE = 1;
    public static final int ESTADO_A_COTIZAR = 2;
    public static final int ESTADO_COTIZADO = 3;
    public static final int ESTADO_RECLAMO_RP = 4;

    /**
     * @deprecated Alias exclusivo para compatibilidad con código legacy.
     */
    @Deprecated
    public static final int ESTADO_AUTORIZADO = ESTADO_RECLAMO_RP;

    public static final int ESTADO_ORDEN_COMPRA = 5;
    public static final int ESTADO_ANULADO = 99;

    public static final String ENVIO_PENDIENTE = "PENDIENTE";
    public static final String ENVIO_PROCESANDO = "PROCESANDO";
    public static final String ENVIO_ENVIADO = "ENVIADO";
    public static final String ENVIO_ENVIADO_QA = "ENVIADO_QA";
    public static final String ENVIO_COTIZADO = "COTIZADO";
    public static final String ENVIO_ERROR = "ERROR";
    public static final String ENVIO_EMAIL_INVALIDO = "EMAIL_INVALIDO";

    public static final int ESCALA_IMPORTE = 2;

    public static final RoundingMode REDONDEO_IMPORTE =
            RoundingMode.HALF_UP;

    public static final String ERROR_PARA_ALERT =
            "ERROR_PARA_ALERT";

    public static final String ERROR_CAMPO_COMPRA =
            "ERROR_CAMPO_COMPRA";

    public static final String SOLO_LECTURA_ATTR =
            "REQUERIMIENTO_COMPRA_SOLO_LECTURA";

    public static final String AFILIADO_REQUERIMIENTO_COMPRA =
            "AFILIADO_REQUERIMIENTO_COMPRA";

    public static final String FORWARD_COMPRAS_VIEW =
            "portlet.compras.view";

    public static final String FORWARD_COMPRAS_ERROR =
            "portlet.compras.error";

    public static final String FORWARD_COMPRAS_RESULT_SEARCH =
            "portlet.compras.result.search";

    public static final String FORWARD_COMPRAS_ALTA_REQUERIMIENTO =
            "portlet.compras.alta_requerimiento";

    public static final String FORWARD_COMPRAS_EDITAR_REQUERIMIENTO =
            "portlet.compras.editar_requerimiento";

    public static final String FORWARD_COMPRAS_VER_REQUERIMIENTO =
            "portlet.compras.ver_requerimiento";

    public static final String FORWARD_COMPRAS_IMPRIMIR_REQUERIMIENTO =
            "portlet.compras.imprimir_requerimiento";

    public static final String FORWARD_COMPRAS_CONFIGURAR_TIPOS_PRESTADOR =
            "portlet.compras.configurar_tipos_prestador";

    public static final String FORWARD_COMPRAS_PRESTADORES_ENVIADOS =
            "portlet.compras.prestadores_enviados";

    /*
     * Compatibilidad legacy con JSP existentes.
     *
     * Las operaciones nuevas de Document Library no deben depender de este
     * valor fijo: deben usar el scopeGroupId de la request actual.
     */
    public static final long DOCUMENT_LIBRARY_GROUP_ID_COMPRAS = 10136L;

    public static final long DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS = 0L;

    public static final String DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS =
            "ComprasPresupuestos";

    public static final String DOCUMENT_LIBRARY_FOLDER_DESCRIPCION_COMPRAS =
            "Presupuestos asociados a requerimientos de compra";

    public static final String DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA =
            "PRESUPUESTO-COMPRA-";

    public static final int DOCUMENT_LIBRARY_MAX_EXTENSION_LENGTH = 20;
    public static final int DOCUMENT_LIBRARY_MAX_TITLE_LENGTH = 240;
    public static final int MAX_PRESUPUESTOS_POR_CARGA = 10;
    public static final int MAX_PRESTADORES_ENVIADOS_REQUERIMIENTO = 500;

    public static String getPrefijoDocumentoRequerimientoCompra(
            int idRequerimientoCompra) {

        if (idRequerimientoCompra <= 0) {
            return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA;
        }

        return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA
                + idRequerimientoCompra
                + "-";
    }

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_PENDIENTE:
                return "PENDIENTE";
            case ESTADO_A_COTIZAR:
                return "A COTIZAR";
            case ESTADO_COTIZADO:
                return "COTIZADO";
            case ESTADO_RECLAMO_RP:
                return "RECLAMO (RP)";
            case ESTADO_ORDEN_COMPRA:
                return "ORDEN DE COMPRA";
            case ESTADO_ANULADO:
                return "ANULADO";
            default:
                return "";
        }
    }

    public static List<RequerimientoCompraEstado> listarEstados() {
        List<RequerimientoCompraEstado> estados =
                new ArrayList<RequerimientoCompraEstado>();

        agregarEstado(estados, ESTADO_PENDIENTE);
        agregarEstado(estados, ESTADO_A_COTIZAR);
        agregarEstado(estados, ESTADO_COTIZADO);
        agregarEstado(estados, ESTADO_RECLAMO_RP);
        agregarEstado(estados, ESTADO_ORDEN_COMPRA);
        agregarEstado(estados, ESTADO_ANULADO);

        return estados;
    }

    public static boolean esEstadoValido(int estado) {
        return estado == ESTADO_PENDIENTE
                || estado == ESTADO_A_COTIZAR
                || estado == ESTADO_COTIZADO
                || estado == ESTADO_RECLAMO_RP
                || estado == ESTADO_ORDEN_COMPRA
                || estado == ESTADO_ANULADO;
    }

    public static boolean esPendiente(int estado) {
        return estado == ESTADO_PENDIENTE;
    }

    public static boolean esACotizar(int estado) {
        return estado == ESTADO_A_COTIZAR;
    }

    public static boolean esCotizado(int estado) {
        return estado == ESTADO_COTIZADO;
    }

    public static boolean esReclamoRP(int estado) {
        return estado == ESTADO_RECLAMO_RP;
    }

    public static boolean esOrdenCompra(int estado) {
        return estado == ESTADO_ORDEN_COMPRA;
    }

    public static boolean esAnulado(int estado) {
        return estado == ESTADO_ANULADO;
    }

    public static boolean puedeEditarEstructura(int estado) {
        return esPendiente(estado);
    }

    public static boolean puedeEditarCotizacion(int estado) {
        return esACotizar(estado);
    }

    public static boolean puedeAdministrarPresupuestos(int estado) {
        return esACotizar(estado);
    }

    public static boolean puedeVerCotizacion(int estado) {
        return esACotizar(estado) || esSoloLectura(estado);
    }

    public static boolean puedeVerPresupuestos(int estado) {
        return esACotizar(estado)
                || esCotizado(estado)
                || esReclamoRP(estado)
                || esOrdenCompra(estado)
                || esAnulado(estado);
    }

    public static boolean puedeEnviarACotizar(int estado) {
        return esPendiente(estado);
    }

    public static boolean puedeReintentarNotificaciones(int estado) {
        return esACotizar(estado);
    }

    public static boolean puedeReintentarNotificaciones(
            int estado,
            boolean hayPrestadoresPendientes) {

        return puedeReintentarNotificaciones(estado)
                && hayPrestadoresPendientes;
    }

    public static boolean puedeAnular(int estado) {
        return esPendiente(estado) || esACotizar(estado);
    }

    public static boolean esSoloLectura(int estado) {
        return esCotizado(estado)
                || esReclamoRP(estado)
                || esOrdenCompra(estado)
                || esAnulado(estado);
    }

    public static boolean validarTransicionEstado(
            int estadoActual,
            int estadoNuevo) {

        if (!esEstadoValido(estadoActual)
                || !esEstadoValido(estadoNuevo)) {

            return false;
        }

        /*
         * Los estados 4 y 5 siguen reconocidos, pero continúan sin transición
         * activa hasta implementar y vincular el Reclamo Prestacional y la
         * Orden de Compra reales.
         */
        if (estadoActual == estadoNuevo
                || esAnulado(estadoActual)) {

            return false;
        }

        if (esCotizado(estadoActual)) {
            return esReclamoRP(
                    estadoNuevo
            );
        }

        if (esAnulado(estadoNuevo)) {
            return puedeAnular(
                    estadoActual
            );
        }

        if (esPendiente(estadoActual)
                && esACotizar(estadoNuevo)) {

            return true;
        }

        return esACotizar(estadoActual)
                && esCotizado(estadoNuevo);
    }

    public static boolean puedeCambiarEstado(
            int estadoActual,
            int estadoNuevo) {

        return validarTransicionEstado(
                estadoActual,
                estadoNuevo
        );
    }

    public static Integer getFiltroTipoNomencladorCompras(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        if ("FARMACIA".equals(sector)) {
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_FARMACIA
            );
        }

        if ("DISCAPACIDAD".equals(sector)) {
            /*
             * Reclamos Prestacionales parte del tipo 8,
             * pero la consulta efectiva utiliza marca ReinLiq 6.
             */
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_DISCAPACIDAD
            );
        }

        if ("ODONTOLOGIA".equals(sector)) {
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_ODONTOLOGIA
            );
        }

        if ("PRESTACIONES MEDICAS".equals(sector)) {
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_GENERAL
            );
        }

        return null;
    }

    public static boolean esNomencladorValidoParaSectorCompras(
            String sectorDescripcion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigoNomenclador) {

        if (idTipoNomenclador <= 0) {
            return false;
        }

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        String codigo =
                codigoNomenclador == null
                        ? ""
                        : codigoNomenclador.trim();

        if ("FARMACIA".equals(sector)) {
            return idTipoNomenclador
                    == FILTRO_NOMENCLADOR_FARMACIA;
        }

        if ("DISCAPACIDAD".equals(sector)) {
            return marcaReinLiq
                    == MARCA_REIN_LIQ_DISCAPACIDAD
                    || CODIGO_ESPECIAL_DISCAPACIDAD
                    .equals(codigo);
        }

        if ("ODONTOLOGIA".equals(sector)) {
            return idTipoNomenclador
                    == FILTRO_NOMENCLADOR_ODONTOLOGIA;
        }

        if ("PRESTACIONES MEDICAS".equals(sector)) {
            return idTipoNomenclador
                    != FILTRO_NOMENCLADOR_ODONTOLOGIA
                    && idTipoNomenclador
                    != FILTRO_NOMENCLADOR_FARMACIA;
        }

        return false;
    }

    public static boolean esSectorDetalleObservacionCompras(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        return "RRHH".equals(sector)
                || "LEGALES".equals(sector)
                || "SISTEMAS".equals(sector)
                || "OTROS".equals(sector);
    }

    public static String normalizarSectorCompra(
            String value) {

        if (value == null) {
            return "";
        }

        String normalizado =
                Normalizer.normalize(
                        value.trim(),
                        Normalizer.Form.NFD
                );

        normalizado =
                DIACRITICOS_SECTOR_COMPRAS
                        .matcher(normalizado)
                        .replaceAll("");

        String sector =
                normalizado
                        .toUpperCase(Locale.ROOT)
                        .trim();

        /*
         * MONOTRIBUTO no constituye un circuito diferente para Compras
         * ni para Reclamos Prestacionales.
         *
         * Cualquier descripción que identifique Monotributo se procesa
         * con las reglas de PRESTACIONES MEDICAS.
         */
        if (sector.indexOf("MONOTRIBUTO") >= 0) {
            return "PRESTACIONES MEDICAS";
        }

        return sector;
    }

    public static String getSectorReclamoPrestacional(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        /*
         * MONOTRIBUTO ya fue convertido por normalizarSectorCompra()
         * en PRESTACIONES MEDICAS.
         */
        if ("PRESTACIONES MEDICAS".equals(sector)) {
            return "PRESTACIONES MEDICAS";
        }

        if ("DISCAPACIDAD".equals(sector)
                || sector.indexOf("DISCAPAC") >= 0) {

            return "DISCAPACIDAD";
        }

        if ("FARMACIA".equals(sector)
                || sector.indexOf("FARMAC") >= 0) {

            return "FARMACIA";
        }

        if ("ODONTOLOGIA".equals(sector)
                || sector.indexOf("ODONTO") >= 0) {

            return "ODONTOLOGIA";
        }

        if ("LEGALES".equals(sector)
                || sector.indexOf("LEGAL") >= 0) {

            return "LEGALES";
        }

        /*
         * OTROS, SISTEMAS, RRHH y cualquier sector no reconocido
         * no pueden generar Reclamos Prestacionales.
         *
         * La regla es deliberadamente fail closed: un sector nuevo
         * tampoco queda habilitado accidentalmente.
         */
        return "";
    }

    public static boolean puedeGenerarReclamoPrestacional(
            String sectorDescripcion) {

        return !isEmpty(
                getSectorReclamoPrestacional(
                        sectorDescripcion
                )
        );
    }

    public static BigDecimal normalizarImporte(
            BigDecimal value) {

        if (value == null) {
            return null;
        }

        return value.setScale(
                ESCALA_IMPORTE,
                REDONDEO_IMPORTE
        );
    }

    public static BigDecimal calcularPrecioTotal(
            Integer cantidad,
            BigDecimal precioUnitario) {

        if (cantidad == null
                || precioUnitario == null) {

            return null;
        }

        return normalizarImporte(
                precioUnitario.multiply(
                        new BigDecimal(cantidad.intValue())
                )
        );
    }

    public static String formatearImporte(
            BigDecimal value) {

        BigDecimal normalizado =
                normalizarImporte(value);

        return normalizado != null
                ? normalizado.toPlainString()
                : "";
    }

    public static String getBooleanDescripcion(
            Boolean value) {

        if (value == null) {
            return "";
        }

        return value.booleanValue()
                ? "Sí"
                : "No";
    }

    public static boolean isEmpty(String value) {
        return value == null
                || value.trim().length() == 0;
    }

    public static String trimToNull(String value) {
        if (isEmpty(value)) {
            return null;
        }

        return value.trim();
    }

    private static void agregarEstado(
            List<RequerimientoCompraEstado> estados,
            int id) {

        RequerimientoCompraEstado estado =
                new RequerimientoCompraEstado();

        estado.setId(Integer.valueOf(id));
        estado.setDescripcion(
                getEstadoDescripcion(id)
        );

        estados.add(estado);
    }

    private WebKeysCompras() {
    }
}
