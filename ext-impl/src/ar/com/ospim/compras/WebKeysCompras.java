package ar.com.ospim.compras;

import ar.com.ospim.compras.requerimientos.helper.ImportesCompraHelper;

import ar.com.ospim.compras.requerimientos.helper.TextoCompraHelper;

import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class WebKeysCompras implements com.liferay.portal.kernel.util.WebKeys {



    public static final String TITULO_ORDEN_MEDICA =
            "Orden médica";

    /*
     * Estos valores son filtros para la búsqueda.
     *
     * El valor cero significa "sin filtro positivo específico".
     * No debe persistirse necesariamente como id_tipo_nomenclador:
     * el resultado seleccionado devuelve el tipo real y positivo.
     */
    public static final int FILTRO_NOMENCLADOR_GENERAL = 0;



    public static boolean esTipoPrestacionInsumos(
            int idTipoPrestacion) {
        return ConfiguracionComprasHelper.contieneEntero("COMPRAS_RUBROS_INSUMOS", ConfiguracionComprasHelper.leerObligatoria("COMPRAS_RUBROS_INSUMOS"), idTipoPrestacion);
    }

    public static boolean esTipoPrestacionProtesis(
            int idTipoPrestacion) {
        return ConfiguracionComprasHelper.contieneEntero("COMPRAS_RUBROS_PROTESIS", ConfiguracionComprasHelper.leerObligatoria("COMPRAS_RUBROS_PROTESIS"), idTipoPrestacion);
    }

    public static boolean esTipoNomencladorPrestacionesMedicas(
            int idTipoNomenclador) {
        return NomencladorCompraBusquedaHelper.esNomencladorValidoParaSector(ConfiguracionComprasHelper.leerObligatoria("COMPRAS_SECTOR_MEDICO"), idTipoNomenclador, 0, null);
    }



    public static final String BUSQUEDA_REQUERIMIENTOS_COMPRA =
            "BUSQUEDA_REQUERIMIENTOS_COMPRA";

    public static final String BUSQUEDA_EMPRESAS_COTIZACION =
            "BUSQUEDA_EMPRESAS_COTIZACION";

    public static final String PAGINA_REQUERIMIENTOS_COMPRA =
            "PAGINA_REQUERIMIENTOS_COMPRA";

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

    // Clave anterior conservada únicamente para limpiar sesiones existentes.
    public static final String RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA =
            "RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA";

    public static final String PARAM_ID_REQUERIMIENTO_COMPRA =
            "id_requerimiento_compra";

    public static final String PARAM_COTIZADOS_INCLUYE_RECLAMO_RP =
            "cotizados_incluye_rp";

    public static final String PARAM_RECLAMO_PRESTACIONAL_NONCE =
            "compras_reclamo_nonce";

    public static final String VINCULO_RECLAMO_RESERVADO =
            "RESERVADO";

    public static final String VINCULO_RECLAMO_VINCULADO =
            "VINCULADO";

    public static final String VINCULO_RECLAMO_ERROR =
            "ERROR";

    public static final String VINCULO_RECLAMO_BAJA =
            "BAJA";

    /*
     * Parámetro único del prestador adjudicado para todo el requerimiento.
     * Se mantiene el parseo de los parámetros legacy por detalle durante la
     * transición para no romper pantallas compiladas o formularios antiguos.
     */
    public static final String PARAM_ID_PRESTADOR_ADJUDICADO =
            "id_prestador_adjudicado";


    public static final String ENVIO_PENDIENTE = "PENDIENTE";
    public static final String ENVIO_PROCESANDO = "PROCESANDO";
    public static final String ENVIO_ENVIADO = "ENVIADO";
    public static final String ENVIO_COTIZADO = "COTIZADO";
    public static final String ENVIO_ERROR = "ERROR";
    public static final String ENVIO_EMAIL_INVALIDO = "EMAIL_INVALIDO";

    public static final int ESCALA_IMPORTE = ImportesCompraHelper.ESCALA_IMPORTE;

    public static final RoundingMode REDONDEO_IMPORTE =
            ImportesCompraHelper.REDONDEO_IMPORTE;

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
     * El grupo legacy se lee de configuracion. Las operaciones nuevas
     * deben usar el scopeGroupId de la request actual.
     */

    public static final long DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS = 0L;

    public static final String DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS =
            "ComprasPresupuestos";

    public static final String DOCUMENT_LIBRARY_FOLDER_DESCRIPCION_COMPRAS =
            "Presupuestos asociados a requerimientos de compra";

    public static final String DOCUMENT_LIBRARY_PREFIJO_PRESUPUESTO_COMPRA =
            "PRESUPUESTO-COMPRA-";


    public static final String
            PARAM_TIPO_DOCUMENTO_COMPRA_RECLAMO =
            "tipo_documento_compra";

    public static final String
            DOCUMENTO_COMPRA_RECLAMO_PEDIDO_COTIZACION =
            "PEDIDO_COTIZACION";

    public static final String
            DOCUMENTO_COMPRA_RECLAMO_COMPARATIVA =
            "COMPARATIVA";

    public static final String
            ATTR_COMPARATIVA_DOCUMENTACION_COMPRAS =
            "compras.comparativaDocumentacion";

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
        return ConfiguracionComprasHelper.getEstadoDescripcion(estado);
    }

    public static List<RequerimientoCompraEstado> listarEstados() {
        return ConfiguracionComprasHelper.listarEstados();
    }

    public static boolean esEstadoValido(int estado) {
        return ConfiguracionComprasHelper.esEstadoValido(estado);
    }

    public static boolean esPendiente(int estado) {
        return ConfiguracionComprasHelper.esPendiente(estado);
    }

    public static boolean esACotizar(int estado) {
        return ConfiguracionComprasHelper.esACotizar(estado);
    }

    public static boolean esCotizado(int estado) {
        return ConfiguracionComprasHelper.esCotizado(estado);
    }

    public static boolean esReclamoRP(int estado) {
        return ConfiguracionComprasHelper.esReclamoRP(estado);
    }

    public static boolean esParaAutorizar(int estado) {
        return ConfiguracionComprasHelper.esParaAutorizar(estado);
    }

    public static boolean esAutorizado(int estado) {
        return ConfiguracionComprasHelper.esAutorizado(estado);
    }

    public static boolean puedeEnviarAAutorizar(int estado) {
        return ConfiguracionComprasHelper.puedeEnviarAAutorizar(estado);
    }

    public static boolean puedeEnviarAAutorizar(
            int estado,
            String sectorDescripcion,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {
        return ConfiguracionComprasHelper.puedeEnviarAAutorizar(estado, sectorDescripcion, hayDetalles, hayCotizacionesEmpresa);
    }

    public static boolean puedeConsultarReclamoPrestacional(int estado) {
        return ConfiguracionComprasHelper.puedeConsultarReclamoPrestacional(estado);
    }

    public static boolean esOrdenCompra(int estado) {
        return ConfiguracionComprasHelper.esOrdenCompra(estado);
    }

    public static boolean esAnulado(int estado) {
        return ConfiguracionComprasHelper.esAnulado(estado);
    }

    public static boolean puedeCorregirItem(int estado) {
        return ConfiguracionComprasHelper.puedeCorregirItem(estado);
    }

    public static boolean puedeEditarEstructura(int estado) {
        return ConfiguracionComprasHelper.puedeEditarEstructura(estado);
    }

    public static boolean puedeEliminarDetalle(int estado) {
        return ConfiguracionComprasHelper.puedeEliminarDetalle(estado);
    }

    public static boolean puedeEditarSurge(int estado) {
        return ConfiguracionComprasHelper.puedeEditarSurge(estado);
    }

    public static boolean puedeEditarCotizacion(int estado) {
        return ConfiguracionComprasHelper.puedeEditarCotizacion(estado);
    }

    public static boolean puedeAdministrarPresupuestos(int estado) {
        return ConfiguracionComprasHelper.puedeAdministrarPresupuestos(estado);
    }

    public static boolean puedeVerCotizacion(int estado) {
        return ConfiguracionComprasHelper.puedeVerCotizacion(estado);
    }

    public static boolean puedeVerPresupuestos(int estado) {
        return ConfiguracionComprasHelper.puedeVerPresupuestos(estado);
    }

    public static boolean puedeEnviarACotizar(int estado) {
        return ConfiguracionComprasHelper.puedeEnviarACotizar(estado);
    }

    public static boolean puedeReintentarNotificaciones(int estado) {
        return ConfiguracionComprasHelper.puedeReintentarNotificaciones(estado);
    }

    public static boolean puedeReintentarNotificaciones(
            int estado,
            boolean hayPrestadoresPendientes) {
        return ConfiguracionComprasHelper.puedeReintentarNotificaciones(estado, hayPrestadoresPendientes);
    }

    public static boolean puedePasarAOrdenCompra(
            int estado,
            String sectorDescripcion,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {
        return ConfiguracionComprasHelper.puedePasarAOrdenCompra(estado, sectorDescripcion, hayDetalles, hayCotizacionesEmpresa);
    }

    public static boolean puedeAnular(int estado) {
        return ConfiguracionComprasHelper.puedeAnular(estado);
    }

    public static boolean puedeAnular(
            int estado,
            boolean reclamoPrestacionalDeBaja) {
        return ConfiguracionComprasHelper.puedeAnular(estado, reclamoPrestacionalDeBaja);
    }

    public static boolean esSoloLectura(int estado) {
        return ConfiguracionComprasHelper.esSoloLectura(estado);
    }

    public static boolean validarTransicionEstado(
            int estadoActual,
            int estadoNuevo) {
        return ConfiguracionComprasHelper.validarTransicionEstado(estadoActual, estadoNuevo);
    }

    public static boolean validarTransicionEstado(
            int estadoActual,
            int estadoNuevo,
            String sectorDescripcion) {
        return ConfiguracionComprasHelper.validarTransicionEstado(estadoActual, estadoNuevo, sectorDescripcion);
    }

    public static boolean puedeCambiarEstado(
            int estadoActual,
            int estadoNuevo) {
        return ConfiguracionComprasHelper.puedeCambiarEstado(estadoActual, estadoNuevo);
    }

    public static Integer getFiltroTipoNomencladorCompras(
            String sectorDescripcion) {
        return ConfiguracionComprasHelper.getFiltroTipoNomenclador(sectorDescripcion);
    }

    public static boolean esNomencladorValidoParaSectorCompras(
            String sectorDescripcion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigoNomenclador) {
        return NomencladorCompraBusquedaHelper.esNomencladorValidoParaSector(sectorDescripcion, idTipoNomenclador, marcaReinLiq, codigoNomenclador);
    }

    public static boolean esNomencladorValidoParaTipoPrestacionCompras(
            String sectorDescripcion,
            int idTipoPrestacion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigoNomenclador) {
        return NomencladorCompraBusquedaHelper.esNomencladorValidoParaTipoPrestacion(sectorDescripcion, idTipoPrestacion, idTipoNomenclador, marcaReinLiq, codigoNomenclador);
    }

    public static boolean esSectorDetalleObservacionCompras(
            String sectorDescripcion) {
        return ConfiguracionComprasHelper.esSectorDetalleObservacion(sectorDescripcion);
    }

    public static boolean esSectorSinCotizacionPrestador(
            String sectorDescripcion) {
        return ConfiguracionComprasHelper.esSectorSinCotizacionPrestador(sectorDescripcion);
    }

    public static String normalizarSectorCompra(
            String value) {
        return TextoCompraHelper.normalizarSectorCompra(value);
    }

    /**
     * Conserva las tildes y compone los caracteres en NFC antes de que el
     * texto atraviese JDBC, Document Library o una vista JSP.
     */
    public static String normalizarTexto(
            String value) {
        return TextoCompraHelper.normalizarTexto(value);
    }

    /**
     * Genera una clave funcional estable para comparar palabras aunque
     * lleguen con o sin tilde, con espacios repetidos o en otra caja.
     */
    public static String normalizarClaveTexto(
            String value) {
        return TextoCompraHelper.normalizarClaveTexto(value);
    }

    public static boolean esTituloOrdenMedica(
            String value) {
        return TextoCompraHelper.esTituloOrdenMedica(value);
    }

    public static String getSectorDescripcionVisible(
            String value) {
        return ConfiguracionComprasHelper.getSectorDescripcionVisible(value);
    }

    public static String getSectorReclamoPrestacional(
            String sectorDescripcion) {
        return ConfiguracionComprasHelper.getSectorReclamoPrestacional(sectorDescripcion);
    }

    public static boolean puedeGenerarReclamoPrestacional(
            String sectorDescripcion) {

        return ConfiguracionComprasHelper.puedeGenerarReclamoPrestacional(sectorDescripcion);
    }

    public static BigDecimal normalizarImporte(
            BigDecimal value) {
        return ImportesCompraHelper.normalizarImporte(value);
    }

    public static BigDecimal calcularPrecioTotal(
            Integer cantidad,
            BigDecimal precioUnitario) {
        return ImportesCompraHelper.calcularPrecioTotal(cantidad, precioUnitario);
    }

    public static String formatearImporte(
            BigDecimal value) {
        return ImportesCompraHelper.formatearImporte(value);
    }

    public static String getBooleanDescripcion(
            Boolean value) {
        return TextoCompraHelper.getBooleanDescripcion(value);
    }

    public static boolean isEmpty(String value) {
        return TextoCompraHelper.isEmpty(value);
    }

    public static String trimToNull(String value) {
        return TextoCompraHelper.trimToNull(value);
    }



    private WebKeysCompras() {
    }
}
