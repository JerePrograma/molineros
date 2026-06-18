package ar.com.ospim.compras;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {

    public static final String ROL_VIEW_COMPRAS = "VIEW_Compras";
    public static final String ROL_ABM_COMPRAS = "ABM_Compras";
    public static final String ROL_ANULAR_COMPRAS = "ANULAR_Compras";
    public static final String ROL_COTIZAR_COMPRAS = "COTIZAR_Compras";

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
    public static final String TIPOS_PRESTADOR_SECTOR = "TIPOS_PRESTADOR_SECTOR";
    public static final String PRESTADORES_ENVIADOS_COTIZACION = "PRESTADORES_ENVIADOS_COTIZACION";

    public static final String ID_SECTOR_CONFIGURACION_COTIZACION = "ID_SECTOR_CONFIGURACION_COTIZACION";
    public static final String RESULTADO_NOTIFICACION_COTIZACION = "RESULTADO_NOTIFICACION_COTIZACION";

    public static final int ESTADO_PENDIENTE = 1;
    public static final int ESTADO_A_COTIZAR = 2;
    public static final int ESTADO_COTIZADO = 3;
    public static final int ESTADO_AUTORIZADO = 4;
    public static final int ESTADO_ORDEN_COMPRA = 5;
    public static final int ESTADO_ANULADO = 99;

    public static final String ENVIO_PENDIENTE = "PENDIENTE";
    public static final String ENVIO_PROCESANDO = "PROCESANDO";
    public static final String ENVIO_ENVIADO = "ENVIADO";
    public static final String ENVIO_ERROR = "ERROR";
    public static final String ENVIO_EMAIL_INVALIDO = "EMAIL_INVALIDO";

    public static final int ESCALA_IMPORTE = 2;
    public static final RoundingMode REDONDEO_IMPORTE = RoundingMode.HALF_UP;

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
    public static final String FORWARD_COMPRAS_CONFIGURAR_TIPOS_PRESTADOR = "portlet.compras.configurar_tipos_prestador";
    public static final String FORWARD_COMPRAS_PRESTADORES_ENVIADOS = "portlet.compras.prestadores_enviados";

    public static final long DOCUMENT_LIBRARY_GROUP_ID_COMPRAS = 10136L;
    public static final long DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS = 0L;
    public static final String DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS = "ComprasPresupuestos";
    public static final String DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA = "PRESUPUESTO-COMPRA-";

    public static String getPrefijoDocumentoRequerimientoCompra(int idRequerimientoCompra) {
        if (idRequerimientoCompra <= 0) {
            return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA;
        }

        return DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA + idRequerimientoCompra + "-";
    }

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_PENDIENTE:
                return "Pendiente";
            case ESTADO_A_COTIZAR:
                return "A cotizar";
            case ESTADO_COTIZADO:
                return "Cotizado";
            case ESTADO_AUTORIZADO:
                return "Autorizado";
            case ESTADO_ORDEN_COMPRA:
                return "Orden de compra";
            case ESTADO_ANULADO:
                return "Anulado";
            default:
                return "";
        }
    }

    public static List<RequerimientoCompraEstado> listarEstados() {
        List<RequerimientoCompraEstado> estados = new ArrayList<RequerimientoCompraEstado>();

        agregarEstado(estados, ESTADO_PENDIENTE);
        agregarEstado(estados, ESTADO_A_COTIZAR);
        agregarEstado(estados, ESTADO_COTIZADO);
        agregarEstado(estados, ESTADO_ANULADO);

        return estados;
    }

    public static boolean esEstadoValido(int estado) {
        return estado == ESTADO_PENDIENTE
                || estado == ESTADO_A_COTIZAR
                || estado == ESTADO_COTIZADO
                || estado == ESTADO_AUTORIZADO
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

    public static boolean esAutorizadoReservado(int estado) {
        return estado == ESTADO_AUTORIZADO;
    }

    public static boolean esOrdenCompraReservado(int estado) {
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

    public static boolean puedeVerPresupuestos(int estado) {
        return esACotizar(estado) || esCotizado(estado);
    }

    public static boolean puedeEnviarACotizar(int estado) {
        return esPendiente(estado);
    }

    public static boolean puedeReintentarNotificaciones(int estado) {
        return esACotizar(estado);
    }

    public static boolean puedeCerrarCotizacion(int estado) {
        return esACotizar(estado);
    }

    public static boolean puedeAnular(int estado) {
        return esPendiente(estado) || esACotizar(estado);
    }

    public static boolean validarTransicionEstado(int estadoActual, int estadoNuevo) {
        if (!esEstadoValido(estadoActual) || !esEstadoValido(estadoNuevo)) {
            return false;
        }

        if (estadoActual == estadoNuevo || esAnulado(estadoActual) || esCotizado(estadoActual)) {
            return false;
        }

        if (esAnulado(estadoNuevo)) {
            return puedeAnular(estadoActual);
        }

        if (esPendiente(estadoActual) && esACotizar(estadoNuevo)) {
            return true;
        }

        return esACotizar(estadoActual) && esCotizado(estadoNuevo);
    }

    public static boolean puedeCambiarEstado(int estadoActual, int estadoNuevo) {
        return validarTransicionEstado(estadoActual, estadoNuevo);
    }

    public static BigDecimal normalizarImporte(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.setScale(ESCALA_IMPORTE, REDONDEO_IMPORTE);
    }

    public static BigDecimal calcularPrecioTotal(Integer cantidad, BigDecimal precioUnitario) {
        if (cantidad == null || precioUnitario == null) {
            return null;
        }

        return normalizarImporte(precioUnitario.multiply(new BigDecimal(cantidad.intValue())));
    }

    public static String formatearImporte(BigDecimal value) {
        BigDecimal normalizado = normalizarImporte(value);
        return normalizado != null ? normalizado.toPlainString() : "";
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

    private static void agregarEstado(List<RequerimientoCompraEstado> estados, int id) {
        RequerimientoCompraEstado estado = new RequerimientoCompraEstado();
        estado.setId(Integer.valueOf(id));
        estado.setDescripcion(getEstadoDescripcion(id));
        estados.add(estado);
    }

    private WebKeysCompras() {
    }
}
