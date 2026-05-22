package ar.com.ospim.compras;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA = "BUSQUEDA_REQUERIMIENTOS_COMPRA";
    public static final String FILTRO_REQUERIMIENTOS_COMPRA = "FILTRO_REQUERIMIENTOS_COMPRA";

    public static final String REQUERIMIENTO_COMPRA_EN_EDICION = "REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String ID_REQUERIMIENTO_COMPRA_EN_EDICION = "id_requerimiento_compra";
    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION = "ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String REQUERIMIENTO_COMPRA_EN_VIEW = "REQUERIMIENTO_COMPRA_EN_VIEW";

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";

    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_SOLICITADO = 2;
    public static final int ESTADO_ANULADO = 9;

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

    public static String getBooleanDescripcion(Boolean value) {
        if (value == null) {
            return "";
        }

        return value.booleanValue() ? "SI" : "NO";
    }

    private WebKeysCompras() {
    }
}
