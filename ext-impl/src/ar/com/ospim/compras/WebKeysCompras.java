package ar.com.ospim.compras;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA = "BUSQUEDA_REQUERIMIENTOS_COMPRA";
    public static final String FILTRO_REQUERIMIENTOS_COMPRA = "FILTRO_REQUERIMIENTOS_COMPRA";

    public static final String REQUERIMIENTO_COMPRA_EN_EDICION = "REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String REQUERIMIENTO_COMPRA_EN_VIEW = "REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION = "ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW = "ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ID_REQUERIMIENTO_COMPRA_EN_EDICION = "id_requerimiento_compra";

    public static final String ESTADOS_REQUERIMIENTO_COMPRA = "ESTADOS_REQUERIMIENTO_COMPRA";
    public static final String SECTORES_REQUERIMIENTO_COMPRA = "SECTORES_REQUERIMIENTO_COMPRA";

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";

    public static final String FORWARD_COMPRAS_VIEW = "portlet.compras.view";
    public static final String FORWARD_COMPRAS_ERROR = "portlet.compras.error";
    public static final String FORWARD_COMPRAS_RESULT_SEARCH = "portlet.compras.result.search";
    public static final String FORWARD_COMPRAS_EDITAR_REQUERIMIENTO = "portlet.compras.editar_requerimiento";
    public static final String FORWARD_COMPRAS_VER_REQUERIMIENTO = "portlet.compras.ver_requerimiento";

    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_SOLICITADO = 2;
    public static final int ESTADO_ANULADO = 9;

    public static final String ESTADO_CODIGO_BORRADOR = "BORRADOR";
    public static final String ESTADO_CODIGO_SOLICITADO = "SOLICITADO";
    public static final String ESTADO_CODIGO_ANULADO = "ANULADO";
    public static final String SOLO_LECTURA_ATTR = "REQUERIMIENTO_COMPRA_SOLO_LECTURA";

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_BORRADOR:
                return "Borrador";
            case ESTADO_SOLICITADO:
                return "Solicitado";
            case ESTADO_ANULADO:
                return "Anulado";
            default:
                return "";
        }
    }

    public static String getEstadoCodigo(int estado) {
        switch (estado) {
            case ESTADO_BORRADOR:
                return ESTADO_CODIGO_BORRADOR;
            case ESTADO_SOLICITADO:
                return ESTADO_CODIGO_SOLICITADO;
            case ESTADO_ANULADO:
                return ESTADO_CODIGO_ANULADO;
            default:
                return "";
        }
    }

    public static boolean esEstadoValido(int estado) {
        return estado == ESTADO_BORRADOR
                || estado == ESTADO_SOLICITADO
                || estado == ESTADO_ANULADO;
    }

    public static boolean esEditable(int estado) {
        return estado == ESTADO_BORRADOR;
    }

    public static boolean puedeSolicitar(int estado) {
        return estado == ESTADO_BORRADOR;
    }

    public static boolean puedeAnular(int estado) {
        return estado == ESTADO_BORRADOR || estado == ESTADO_SOLICITADO;
    }

    public static boolean puedeCambiarEstado(int estadoActual, int estadoNuevo) {
        if (!esEstadoValido(estadoActual) || !esEstadoValido(estadoNuevo)) {
            return false;
        }

        if (estadoActual == estadoNuevo) {
            return true;
        }

        if (estadoActual == ESTADO_ANULADO) {
            return false;
        }

        if (estadoNuevo == ESTADO_ANULADO) {
            return puedeAnular(estadoActual);
        }

        if (estadoActual == ESTADO_BORRADOR && estadoNuevo == ESTADO_SOLICITADO) {
            return true;
        }

        return false;
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