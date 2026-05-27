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

    public static final String ESTADOS_REQUERIMIENTO = "ESTADOS_REQUERIMIENTO";
    public static final String SECTORES_REQUERIMIENTO = "SECTORES_REQUERIMIENTO";
    public static final String ESTADOS_REQUERIMIENTO_COMPRA = ESTADOS_REQUERIMIENTO;
    public static final String SECTORES_REQUERIMIENTO_COMPRA = SECTORES_REQUERIMIENTO;

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";
    public static final String SOLO_LECTURA_ATTR = "REQUERIMIENTO_COMPRA_SOLO_LECTURA";

    public static final String FORWARD_COMPRAS_VIEW = "portlet.compras.view";
    public static final String FORWARD_COMPRAS_ERROR = "portlet.compras.error";
    public static final String FORWARD_COMPRAS_RESULT_SEARCH = "portlet.compras.result.search";
    public static final String FORWARD_COMPRAS_EDITAR_REQUERIMIENTO = "portlet.compras.editar_requerimiento";
    public static final String FORWARD_COMPRAS_VER_REQUERIMIENTO = "portlet.compras.ver_requerimiento";

    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_COTIZADO = 2;
    public static final int ESTADO_ANULADO = 3;

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_BORRADOR:
                return "Borrador";
            case ESTADO_COTIZADO:
                return "Cotizado";
            case ESTADO_ANULADO:
                return "Anulado";
            default:
                return "";
        }
    }

    public static boolean esEstadoValido(int estado) {
        return esBorrador(estado) || esCotizado(estado) || esAnulado(estado);
    }

    public static boolean esBorrador(int estado) {
        return estado == ESTADO_BORRADOR;
    }

    public static boolean esCotizado(int estado) {
        return estado == ESTADO_COTIZADO;
    }

    public static boolean esAnulado(int estado) {
        return estado == ESTADO_ANULADO;
    }

    public static boolean puedeEditar(int estado) {
        return esBorrador(estado);
    }

    public static boolean esEditable(int estado) {
        return puedeEditar(estado);
    }

    public static boolean puedeCotizar(int estado) {
        return esBorrador(estado);
    }

    public static boolean puedeAnular(int estado) {
        return esBorrador(estado) || esCotizado(estado);
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

        if (esAnulado(estadoNuevo)) {
            return puedeAnular(estadoActual);
        }

        return esBorrador(estadoActual) && esCotizado(estadoNuevo);
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
