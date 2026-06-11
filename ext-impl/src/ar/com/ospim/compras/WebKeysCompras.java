package ar.com.ospim.compras;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";
    public static final String ROL_AUTORIZAR_COMPRAS = "AUTORIZAR_Compras";
    public static final String ROL_COTIZAR_COMPRAS = "COTIZAR_Compras";
    public static final String ROL_ORDEN_COMPRA_COMPRAS = "ORDEN_COMPRA_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA = "BUSQUEDA_REQUERIMIENTOS_COMPRA";
    public static final String FILTRO_REQUERIMIENTOS_COMPRA = "FILTRO_REQUERIMIENTOS_COMPRA";

    public static final String REQUERIMIENTO_COMPRA_EN_EDICION = "REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String REQUERIMIENTO_COMPRA_EN_VIEW = "REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION = "ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW = "ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ID_REQUERIMIENTO_COMPRA_EN_EDICION = "id_requerimiento_compra";

    public static final String ESTADOS_REQUERIMIENTO = "ESTADOS_REQUERIMIENTO";
    public static final String SECTORES_REQUERIMIENTO = "SECTORES_REQUERIMIENTO";
    public static final String ESTADOS_REQUERIMIENTO_COMPRA = ESTADOS_REQUERIMIENTO;
    public static final String SECTORES_REQUERIMIENTO_COMPRA = SECTORES_REQUERIMIENTO;

    /*
     * Flujo nuevo:
     *
     * Borrador
     *   -> Requerimiento
     *   -> Autorizado
     *   -> Cotizaciones
     *   -> Orden de compra
     *
     * Anulado es estado terminal lateral.
     */
    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_REQUERIMIENTO = 2;
    public static final int ESTADO_AUTORIZADO = 3;
    public static final int ESTADO_COTIZACIONES = 4;
    public static final int ESTADO_ORDEN_COMPRA = 5;
    public static final int ESTADO_ANULADO = 99;

    /*
     * Alias legacy para reducir roturas en JSPs/clases existentes.
     * Antes ESTADO_COTIZADO significaba "finalizado/cotizado".
     * En el flujo nuevo se interpreta como etapa de Cotizaciones.
     */
    public static final int ESTADO_COTIZADO = ESTADO_COTIZACIONES;

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";
    public static final String ERROR_CAMPO_COMPRA = "ERROR_CAMPO_COMPRA";
    public static final String SOLO_LECTURA_ATTR = "REQUERIMIENTO_COMPRA_SOLO_LECTURA";
    public static final String AFILIADO_REQUERIMIENTO_COMPRA = "AFILIADO_REQUERIMIENTO_COMPRA";

    public static final String FORWARD_COMPRAS_VIEW = "portlet.compras.view";
    public static final String FORWARD_COMPRAS_ERROR = "portlet.compras.error";
    public static final String FORWARD_COMPRAS_RESULT_SEARCH = "portlet.compras.result.search";
    public static final String FORWARD_COMPRAS_ALTA_REQUERIMIENTO = "portlet.compras.alta_requerimiento";
    public static final String FORWARD_COMPRAS_EDITAR_REQUERIMIENTO = "portlet.compras.editar_requerimiento";
    public static final String FORWARD_COMPRAS_VER_REQUERIMIENTO = "portlet.compras.ver_requerimiento";
    public static final String FORWARD_COMPRAS_IMPRIMIR_REQUERIMIENTO = "portlet.compras.imprimir_requerimiento";

    /*
     * Document Library - Adjuntos de requerimientos de compra.
     *
     * Se replica el mecanismo legacy de Reclamos:
     * - Carpeta fija en Document Library.
     * - Relacion por title con prefijo.
     * - Sin tabla propia de adjuntos.
     */
    public static final long DOCUMENT_LIBRARY_GROUP_ID_COMPRAS = 10136L;
    public static final long DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS = 0L;
    public static final String DOCUMENT_LIBRARY_FOLDER_REQUERIMIENTOS_COMPRAS = "RequerimientosCompras";
    public static final String DOCUMENT_LIBRARY_PREFIJO_REQUERIMIENTO_COMPRA = "RC-";

    public static String getPrefijoDocumentoRequerimientoCompra(int idRequerimientoCompra) {
        if (idRequerimientoCompra <= 0) {
            return DOCUMENT_LIBRARY_PREFIJO_REQUERIMIENTO_COMPRA;
        }

        return DOCUMENT_LIBRARY_PREFIJO_REQUERIMIENTO_COMPRA
                + idRequerimientoCompra
                + "-";
    }

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_BORRADOR:
                return "Borrador";
            case ESTADO_REQUERIMIENTO:
                return "Requerimiento";
            case ESTADO_AUTORIZADO:
                return "Autorizado";
            case ESTADO_COTIZACIONES:
                return "Cotizaciones";
            case ESTADO_ORDEN_COMPRA:
                return "Orden de compra";
            case ESTADO_ANULADO:
                return "Anulado";
            default:
                return "";
        }
    }

    public static boolean esEstadoValido(int estado) {
        return esBorrador(estado)
                || esRequerimiento(estado)
                || esAutorizado(estado)
                || esCotizaciones(estado)
                || esOrdenCompra(estado)
                || esAnulado(estado);
    }

    public static boolean esBorrador(int estado) {
        return estado == ESTADO_BORRADOR;
    }

    public static boolean esRequerimiento(int estado) {
        return estado == ESTADO_REQUERIMIENTO;
    }

    public static boolean esAutorizado(int estado) {
        return estado == ESTADO_AUTORIZADO;
    }

    public static boolean esCotizaciones(int estado) {
        return estado == ESTADO_COTIZACIONES;
    }

    public static boolean esOrdenCompra(int estado) {
        return estado == ESTADO_ORDEN_COMPRA;
    }

    public static boolean esAnulado(int estado) {
        return estado == ESTADO_ANULADO;
    }

    /*
     * Compatibilidad legacy.
     */
    public static boolean esCotizado(int estado) {
        return esCotizaciones(estado);
    }

    public static boolean puedeEditar(int estado) {
        return esBorrador(estado);
    }

    public static boolean esEditable(int estado) {
        return puedeEditar(estado);
    }

    public static boolean puedeEnviarAAutorizar(int estado) {
        return esBorrador(estado);
    }

    public static boolean puedeAutorizar(int estado) {
        return esRequerimiento(estado);
    }

    public static boolean puedeIniciarCotizaciones(int estado) {
        return esAutorizado(estado);
    }

    /*
     * Compatibilidad legacy.
     */
    public static boolean puedeCotizar(int estado) {
        return puedeIniciarCotizaciones(estado);
    }

    public static boolean puedeGenerarOrdenCompra(int estado) {
        return esCotizaciones(estado);
    }

    public static boolean puedeAnular(int estado) {
        return esBorrador(estado)
                || esRequerimiento(estado)
                || esAutorizado(estado)
                || esCotizaciones(estado);
    }

    public static boolean validarTransicionEstado(int estadoActual, int estadoNuevo) {
        if (!esEstadoValido(estadoActual) || !esEstadoValido(estadoNuevo)) {
            return false;
        }

        if (estadoActual == estadoNuevo) {
            return true;
        }

        if (esAnulado(estadoActual)) {
            return false;
        }

        if (esOrdenCompra(estadoActual)) {
            return false;
        }

        if (esAnulado(estadoNuevo)) {
            return puedeAnular(estadoActual);
        }

        if (esBorrador(estadoActual) && esRequerimiento(estadoNuevo)) {
            return true;
        }

        if (esRequerimiento(estadoActual) && esAutorizado(estadoNuevo)) {
            return true;
        }

        if (esAutorizado(estadoActual) && esCotizaciones(estadoNuevo)) {
            return true;
        }

        if (esCotizaciones(estadoActual) && esOrdenCompra(estadoNuevo)) {
            return true;
        }

        return false;
    }

    public static boolean puedeCambiarEstado(int estadoActual, int estadoNuevo) {
        return validarTransicionEstado(estadoActual, estadoNuevo);
    }

    public static String getBooleanDescripcion(Boolean value) {
        if (value == null) {
            return "";
        }

        return value.booleanValue() ? "SI" : "NO";
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String trimToNull(String value) {
        if (isEmpty(value)) {
            return null;
        }

        return value.trim();
    }

    private WebKeysCompras() {
    }
}