package ar.com.ospim.compras;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_APROBAR_COMPRAS = "APROBAR_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";

    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA = "BUSQUEDA_REQUERIMIENTOS_COMPRA";
    public static final String FILTRO_REQUERIMIENTOS_COMPRA = "FILTRO_REQUERIMIENTOS_COMPRA";

    public static final String REQUERIMIENTO_COMPRA_EN_EDICION = "REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String ID_REQUERIMIENTO_COMPRA_EN_EDICION = "id_requerimiento_compra";
    public static final String ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION = "ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION";
    public static final String REQUERIMIENTO_COMPRA_EN_VIEW = "REQUERIMIENTO_COMPRA_EN_VIEW";
    public static final String HISTORIAL_REQUERIMIENTO_COMPRA = "HISTORIAL_REQUERIMIENTO_COMPRA";
    public static final String ADJUNTOS_REQUERIMIENTO_COMPRA = "ADJUNTOS_REQUERIMIENTO_COMPRA";

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";

    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_PENDIENTE_APROBACION = 2;
    public static final int ESTADO_APROBADO = 3;
    public static final int ESTADO_OBSERVADO = 4;
    public static final int ESTADO_RECHAZADO = 5;
    public static final int ESTADO_EN_COMPRA = 6;
    public static final int ESTADO_CERRADO = 7;
    public static final int ESTADO_ANULADO = 8;
    public static final int ESTADO_PENDIENTE_COTIZACION = 9;
    public static final int ESTADO_COTIZADO = 10;

    public static final int PRIORIDAD_BAJA = 1;
    public static final int PRIORIDAD_MEDIA = 2;
    public static final int PRIORIDAD_ALTA = 3;
    public static final int PRIORIDAD_URGENTE = 4;

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_BORRADOR:
                return "Borrador";
            case ESTADO_PENDIENTE_APROBACION:
                return "Pendiente aprobacion";
            case ESTADO_APROBADO:
                return "Aprobado";
            case ESTADO_OBSERVADO:
                return "Observado";
            case ESTADO_RECHAZADO:
                return "Rechazado";
            case ESTADO_EN_COMPRA:
                return "En compra";
            case ESTADO_CERRADO:
                return "Cerrado";
            case ESTADO_ANULADO:
                return "Anulado";
            case ESTADO_PENDIENTE_COTIZACION:
                return "Pendiente cotizacion";
            case ESTADO_COTIZADO:
                return "Cotizado";
            default:
                return "";
        }
    }

    public static String getPrioridadDescripcion(int prioridad) {
        switch (prioridad) {
            case PRIORIDAD_BAJA:
                return "Baja";
            case PRIORIDAD_MEDIA:
                return "Media";
            case PRIORIDAD_ALTA:
                return "Alta";
            case PRIORIDAD_URGENTE:
                return "Urgente";
            default:
                return "";
        }
    }

    public static String getBooleanDescripcion(Boolean value) {
        if (value == null) {
            return "";
        }

        return value.booleanValue() ? "SI" : "NO";
    }

    public static boolean esEditable(int estado) {
        return estado == ESTADO_BORRADOR
                || estado == ESTADO_OBSERVADO
                || estado == ESTADO_PENDIENTE_COTIZACION
                || estado == ESTADO_COTIZADO
                || estado == ESTADO_EN_COMPRA;
    }

    public static boolean puedeEnviarAprobacion(int estado) {
        return estado == ESTADO_BORRADOR || estado == ESTADO_OBSERVADO;
    }

    public static boolean puedeAprobar(int estado) {
        return estado == ESTADO_PENDIENTE_APROBACION;
    }

    public static boolean puedeEnviarACotizacion(int estado) {
        return estado == ESTADO_APROBADO || estado == ESTADO_OBSERVADO;
    }

    public static boolean puedeMarcarCotizado(int estado) {
        return estado == ESTADO_PENDIENTE_COTIZACION || estado == ESTADO_APROBADO;
    }

    public static boolean puedeCerrar(int estado) {
        return estado == ESTADO_APROBADO
                || estado == ESTADO_PENDIENTE_COTIZACION
                || estado == ESTADO_COTIZADO
                || estado == ESTADO_EN_COMPRA;
    }

    public static boolean puedeAnular(int estado) {
        return estado != ESTADO_CERRADO && estado != ESTADO_ANULADO;
    }

    private WebKeysCompras() {
    }
}