package ar.com.ospim.compras;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_APROBAR_COMPRAS = "APROBAR_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";

    public static final String BUSQUEDA_COMPRAS = "BUSQUEDA_COMPRAS";
    public static final String FILTRO_COMPRAS = "FILTRO_COMPRAS";

    public static final String COMPRA_EN_EDICION = "COMPRA_EN_EDICION";
    public static final String COMPRA_EN_VIEW = "COMPRA_EN_VIEW";
    public static final String ID_COMPRA_EN_EDICION = "id_requerimiento_compra";

    public static final String ITEMS_COMPRA_EN_EDICION = "ITEMS_COMPRA_EN_EDICION";
    public static final String HISTORIAL_COMPRA = "HISTORIAL_COMPRA";
    public static final String ADJUNTOS_COMPRA = "ADJUNTOS_COMPRA";

    public static final String ERROR_PARA_ALERT = "ERROR_PARA_ALERT";

    public static final int ESTADO_BORRADOR = 1;
    public static final int ESTADO_PENDIENTE_APROBACION = 2;
    public static final int ESTADO_APROBADO = 3;
    public static final int ESTADO_OBSERVADO = 4;
    public static final int ESTADO_RECHAZADO = 5;
    public static final int ESTADO_EN_COMPRA = 6;
    public static final int ESTADO_CERRADO = 7;
    public static final int ESTADO_ANULADO = 8;

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

    public static boolean esEditable(int estado) {
        return estado == ESTADO_BORRADOR || estado == ESTADO_OBSERVADO;
    }

    public static boolean puedeEnviarAprobacion(int estado) {
        return estado == ESTADO_BORRADOR || estado == ESTADO_OBSERVADO;
    }

    public static boolean puedeAprobar(int estado) {
        return estado == ESTADO_PENDIENTE_APROBACION;
    }

    public static boolean puedeCerrar(int estado) {
        return estado == ESTADO_APROBADO || estado == ESTADO_EN_COMPRA;
    }

    public static boolean puedeAnular(int estado) {
        return estado != ESTADO_CERRADO && estado != ESTADO_ANULADO;
    }

    private WebKeysCompras() {
    }
}
