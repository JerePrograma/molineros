package ar.com.ospim.compras;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    private static final Pattern DIACRITICOS_TEXTO_COMPRAS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final Pattern ESPACIOS_TEXTO_COMPRAS =
            Pattern.compile("\\s+");

    public static final String TITULO_ORDEN_MEDICA =
            "Orden médica";

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";
    public static final String ROL_COTIZAR_COMPRAS = "COTIZAR_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA =
            "BUSQUEDA_REQUERIMIENTOS_COMPRA";

    public static final String BUSQUEDA_EMPRESAS_COTIZACION =
            "BUSQUEDA_EMPRESAS_COTIZACION";

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

    public static final String TIPOS_PRESTACION_REQUERIMIENTO_COMPRA =
            "TIPOS_PRESTACION_REQUERIMIENTO_COMPRA";

    public static final String PRESTADORES_ENVIADOS_COTIZACION =
            "PRESTADORES_ENVIADOS_COTIZACION";

    public static final String PRESTADORES_HABILITADOS_COTIZACION =
            "PRESTADORES_HABILITADOS_COTIZACION";

    public static final String ERROR_PRESTADORES_HABILITADOS_COTIZACION =
            "ERROR_PRESTADORES_HABILITADOS_COTIZACION";

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

    public static final String RELACIONES_RECLAMO_PRESTACIONAL_COMPRA =
            "RELACIONES_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String MOSTRAR_ID_RP_LISTADO =
            "MOSTRAR_ID_RP_LISTADO";

    public static final String COTIZADOS_INCLUYE_RECLAMO_RP =
            "COTIZADOS_INCLUYE_RECLAMO_RP";

    public static final String RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK =
            "RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK";

    public static final String CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA =
            "CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA =
            "RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String PARAM_ID_REQUERIMIENTO_COMPRA =
            "id_requerimiento_compra";

    public static final String PARAM_COTIZADOS_INCLUYE_RECLAMO_RP =
            "cotizados_incluye_rp";

    public static final String PARAM_RECLAMO_PRESTACIONAL_NONCE =
            "compras_reclamo_nonce";

    public static final String PARAM_RECUPERACION_RECLAMO_NONCE =
            "compras_recuperacion_reclamo_nonce";

    public static final String CMD_DESCARTAR_EDICION_RECLAMO =
            "descartar_edicion_reclamo";

    public static final String ATR_RECUPERACION_RECLAMO_ACTIVA =
            "recuperacion_reclamo_activa";

    public static final String ATR_RECUPERACION_RECLAMO_URL_DESCARTAR =
            "recuperacion_reclamo_url_descartar";

    public static final String ATR_RECUPERACION_RECLAMO_URL_VOLVER =
            "recuperacion_reclamo_url_volver";

    public static final String ATR_RECUPERACION_RECLAMO_ID_ACTUAL =
            "recuperacion_reclamo_id_actual";

    public static final String ATR_RECUPERACION_CONTEXTO_VENCIDO =
            "recuperacion_reclamo_contexto_vencido";

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
    public static final int ESTADO_ORDEN_COMPRA = 5;
    public static final int ESTADO_ANULADO = 99;

    /**
     * @deprecated Alias exclusivo para compatibilidad con código legacy.
     */
    @Deprecated
    public static final int ESTADO_AUTORIZADO = ESTADO_RECLAMO_RP;

    public static final String ENVIO_PENDIENTE = "PENDIENTE";
    public static final String ENVIO_PROCESANDO = "PROCESANDO";
    public static final String ENVIO_ENVIADO = "ENVIADO";
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

    public static final String SITUACIONES_MEDICAS_VIGENTES_COMPRA =
            "SITUACIONES_MEDICAS_VIGENTES_COMPRA";

    public static final String FORWARD_COMPRAS_SITUACION_MEDICA_VIGENTE =
            "portlet.compras.situacion_medica_vigente";

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

    public static final String
            PARAM_TIPO_DOCUMENTO_COMPRA_RECLAMO =
            "tipo_documento_compra";

    public static final String
            DOCUMENTO_COMPRA_RECLAMO_PEDIDO_COTIZACION =
            "PEDIDO_COTIZACION";

    public static String getPrefijoDocumentoRequerimientoCompra(
            int idRequerimientoCompra) {

        if (idRequerimientoCompra <= 0) {
            return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA;
        }

        return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA
                + idRequerimientoCompra
                + "-";
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

    public static boolean puedeEliminarDetalle(int estado) {
        return esPendiente(estado)
                || esACotizar(estado);
    }

    public static boolean puedeEditarSurge(int estado) {
        return esPendiente(estado) || esACotizar(estado);
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

    public static boolean puedePasarAOrdenCompra(
            int estado,
            RequerimientoCompraSector sector,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {

        return esPendiente(estado)
                && sector != null && sector.isPermiteOrdenCompraDirecta()
                && hayDetalles
                && hayCotizacionesEmpresa;
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
         * RECLAMO_RP se activa únicamente cuando existe un Reclamo
         * Prestacional real y correctamente vinculado. ORDEN_COMPRA utiliza
         * una operación explícita según sector, por lo que no forma parte
         * de esta matriz genérica del flujo de prestadores.
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

    public static Integer getFiltroTipoNomencladorCompras(RequerimientoCompraSector sector) {
        return sector != null ? sector.getFiltroTipoNomenclador() : null;
    }

    public static boolean esNomencladorValidoParaSectorCompras(
            RequerimientoCompraSector sector, int idTipoNomenclador) {
        return sector != null && sector.isNomenclador()
                && sector.getNomencladores().contains(Integer.valueOf(idTipoNomenclador));
    }

    public static boolean esNomencladorValidoParaTipoPrestacionCompras(
            RequerimientoCompraSector sector, TipoPrestacionCompra tipo, int idTipoNomenclador) {
        return sector != null && tipo != null && tipo.getIdSectorInt() == sector.getIdSector()
                && sector.isNomenclador() && tipo.admiteNomenclador(idTipoNomenclador);
    }

    public static boolean esSectorDetalleObservacionCompras(RequerimientoCompraSector sector) {
        return sector != null && sector.isObservacion();
    }

    public static boolean esSectorSinCotizacionPrestador(RequerimientoCompraSector sector) {
        return sector != null && sector.isPermiteCotizacionEmpresa();
    }

    public static String normalizarSectorCompra(
            String value) {

        return normalizarClaveTexto(value);
    }

    /**
     * Conserva las tildes y compone los caracteres en NFC antes de que el
     * texto atraviese JDBC, Document Library o una vista JSP.
     */
    public static String normalizarTexto(
            String value) {

        if (value == null) {
            return "";
        }

        return Normalizer.normalize(
                value,
                Normalizer.Form.NFC
        ).trim();
    }

    /**
     * Genera una clave funcional estable para comparar palabras aunque
     * lleguen con o sin tilde, con espacios repetidos o en otra caja.
     */
    public static String normalizarClaveTexto(
            String value) {

        String texto = normalizarTexto(value);

        if (texto.length() == 0) {
            return "";
        }

        String normalizado =
                Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                );

        normalizado =
                DIACRITICOS_TEXTO_COMPRAS
                        .matcher(normalizado)
                        .replaceAll("");

        normalizado =
                ESPACIOS_TEXTO_COMPRAS
                        .matcher(normalizado)
                        .replaceAll(" ");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    public static boolean esTituloOrdenMedica(
            String value) {

        return normalizarClaveTexto(TITULO_ORDEN_MEDICA).equals(
                normalizarClaveTexto(value)
        );
    }

    public static String getSectorDescripcionVisible(
            String value) {

        String clave = normalizarSectorCompra(value);

        if ("PRESTACIONES MEDICAS".equals(clave)) {
            return "PRESTACIONES MÉDICAS";
        }

        if ("ODONTOLOGIA".equals(clave)) {
            return "ODONTOLOGÍA";
        }

        String texto = normalizarTexto(value);

        return texto.length() > 0
                ? texto.toUpperCase(Locale.ROOT)
                : "";
    }

    public static String getSectorReclamoPrestacional(RequerimientoCompraSector sector) {
        return sector != null && sector.getSectorReclamoPrestacional() != null
                ? sector.getSectorReclamoPrestacional() : "";
    }

    public static boolean puedeGenerarReclamoPrestacional(RequerimientoCompraSector sector) {
        return !isEmpty(getSectorReclamoPrestacional(sector));
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
        String normalizado = normalizarTexto(value);

        if (normalizado.length() == 0) {
            return null;
        }

        return normalizado;
    }



    private WebKeysCompras() {
    }
}
