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

    private static final Pattern DIACRITICOS_TEXTO_COMPRAS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final Pattern ESPACIOS_TEXTO_COMPRAS =
            Pattern.compile("\\s+");

    // RC0-A - VOCABULARIO COMUN DE COMPRAS.
    public static final String SECTOR_FARMACIA = "FARMACIA";
    public static final String SECTOR_DISCAPACIDAD = "DISCAPACIDAD";
    public static final String SECTOR_ODONTOLOGIA = "ODONTOLOGIA";
    public static final String SECTOR_PRESTACIONES_MEDICAS =
            "PRESTACIONES MEDICAS";
    public static final String SECTOR_RRHH = "RRHH";
    public static final String SECTOR_LEGALES = "LEGALES";
    public static final String SECTOR_SISTEMAS = "SISTEMAS";
    public static final String SECTOR_OTROS = "OTROS";

    public static final String PARAM_STRUTS_ACTION = "struts_action";
    public static final String PARAM_MODO = "modo";
    public static final String PARAM_TABS1 = "tabs1";
    public static final String PARAM_COMPRAS_OPERACION = "compras_operacion";
    public static final String PARAM_COMPRAS_ERROR = "compras_error";
    public static final String PARAM_COMPRAS_GUARDADO = "compras_guardado";

    public static final String MODO_REQUERIMIENTO_COMPRA_ATTR =
            "MODO_REQUERIMIENTO_COMPRA";
    public static final String MODO_REQUERIMIENTO_ALTA = "ALTA";
    public static final String MODO_REQUERIMIENTO_EDICION = "EDICION";
    public static final String MODO_REQUERIMIENTO_VISTA = "VISTA";
    public static final String MODO_ALTA = "alta";
    public static final String MODO_EDITAR = "editar";
    public static final String MODO_VER = "ver";

    public static final String TAB_REQUERIMIENTOS = "requerimientos";
    public static final String TAB_COTIZADOS = "cotizados";
    public static final String TAB_CONFIGURACION_CORREOS =
            "configuracion-de-correos";

    public static final String STRUTS_ACTION_COMPRAS_VIEW =
            "/compras/view";
    public static final String STRUTS_ACTION_NUEVO_REQUERIMIENTO =
            "/compras/nuevo_requerimiento";
    public static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";
    public static final String STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";
    public static final String STRUTS_ACTION_IMPRIMIR_REQUERIMIENTO =
            "/compras/imprimir_requerimiento";

    public static final String CMD_SAVE_ALL = "saveAll";
    public static final String CMD_SAVE_COTIZACION = "saveCotizacion";
    public static final String CMD_CERRAR_COTIZACION = "cerrarCotizacion";
    public static final String CMD_ADD_ITEM = "addItem";
    public static final String CMD_ADD_ITEMS = "addItems";
    public static final String CMD_UPDATE_ITEM = "updateItem";
    public static final String CMD_DELETE_ITEM = "deleteItem";

    public static final String PARAM_COMPRAS_SAVE_TOKEN =
            "compras_save_token";
    public static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";
    public static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";
    public static final int MAX_TOKENS_GUARDADO_COMPRA = 20;
    public static final int MAX_DETALLES_COTIZACION_RETORNO = 1000;
    public static final String PARAM_ORDEN_MEDICA_COUNT =
            "orden_medica_count";
    public static final int MAX_ORDENES_MEDICAS_POR_CARGA = 20;
    public static final int MAX_ITEMS_HISTORICOS_AFILIADO = 20;
    public static final boolean EXIGIR_DETALLES_EN_SAVE_ALL = true;

    public static final String OPERACION_PRESUPUESTO_AGREGAR =
            "presupuestoAgregar";
    public static final String OPERACION_PRESUPUESTO_BORRAR =
            "presupuestoBorrar";
    public static final String OPERACION_PRESUPUESTO_ERROR =
            "presupuestoError";

    public static final int MAX_RESULTADOS_EMPRESAS_COTIZACION = 100;
    public static final int LIMITE_EMPRESAS_COTIZACION_CON_MARCA =
            MAX_RESULTADOS_EMPRESAS_COTIZACION + 1;
    public static final int MIN_CARACTERES_RAZON_SOCIAL_EMPRESA = 3;

    public static final String ATTR_EMPRESAS_BUSQUEDA_REALIZADA =
            "compras.empresas.busqueda.realizada";
    public static final String ATTR_EMPRESAS_BUSQUEDA_LIMITADA =
            "compras.empresas.busqueda.limitada";
    public static final String ATTR_EMPRESAS_BUSQUEDA_ERROR =
            "compras.empresas.busqueda.error";
    public static final String FORWARD_COMPRAS_EMPRESAS_RESULT_SEARCH =
            "portlet.compras.empresas.result.search";

    public static final String ATTR_CALLBACK_BUSQUEDA_NOMENCLADOR =
            "COMPRAS_CALLBACK_BUSQUEDA";
    public static final String ATTR_SECTOR_NOMENCLADOR =
            "COMPRAS_SECTOR_NOMENCLADOR";
    public static final String ATTR_MARCA_REIN_LIQ =
            "COMPRAS_MARCA_REIN_LIQ";
    public static final String ATTR_ES_PRESTACIONES_MEDICAS =
            "COMPRAS_ES_PREST_MED";
    public static final String ATTR_CODIGO_NOMENCLADOR =
            "COMPRAS_CODIGO_NOMENCLADOR";
    public static final String ATTR_DESCRIPCION_NOMENCLADOR =
            "COMPRAS_DESCRIPCION_NOMENCLADOR";
    public static final String ATTR_ID_TIPO_NOMENCLADOR =
            "COMPRAS_ID_TIPO_NOMENCLADOR";
    public static final String ATTR_RESULTADOS_NOMENCLADOR =
            "COMPRAS_RESULTADOS_NOMENCLADOR";
    public static final String ATTR_ERROR_BUSQUEDA_NOMENCLADOR =
            "COMPRAS_ERROR_BUSQUEDA";
    public static final String FORWARD_COMPRAS_BUSCAR_ITEM_TECNICO =
            "portlet.compras.buscar_item_tecnico";

    public static final String ATTR_ID_REQUERIMIENTO_COMPRA_PDF =
            "ID_REQUERIMIENTO_COMPRA_PDF";

    public static final String REPORTE_EXPORTACION_REQUERIMIENTOS =
            "COMPRAS_REQUERIMIENTOS";
    public static final String PARAM_EXPORTACION_TOKEN =
            "compras_exportacion_token";
    public static final String SESSION_EXPORTACIONES_REQUERIMIENTOS =
            "COMPRAS_EXPORTACIONES";
    public static final int MAX_CONTEXTOS_EXPORTACION = 20;

    public static final String PARAM_CONTACTO_AFILIADO_TOKEN =
            "contacto_afiliado_token";
    public static final String ATTR_CONTACTO_AFILIADO_TOKEN =
            "COMPRAS_CONTACTO_AFILIADO_TOKEN";
    public static final String SESSION_CONTACTO_AFILIADO_CONTEXTOS =
            "COMPRAS_CONTACTO_AFILIADO_CONTEXTOS";
    public static final int MAX_CONTEXTOS_CONTACTO_AFILIADO = 20;

    public static final String ATTR_PRESTADORES_ENVIADOS_REQUERIMIENTO =
            "compras.requerimiento.prestadoresEnviados";
    public static final String ATTR_ERROR_PRESTADORES_ENVIADOS_REQUERIMIENTO =
            "compras.requerimiento.errorPrestadoresEnviados";
    public static final String ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO =
            "compras.requerimiento.prestadoresDisponiblesPresupuesto";
    public static final String ATTR_PRESUPUESTOS_REQUERIMIENTO =
            "compras.requerimiento.presupuestos";
    public static final String ATTR_IDS_PRESTADORES_CON_PRESUPUESTO =
            "compras.requerimiento.idsPrestadoresConPresupuesto";
    public static final String ATTR_ERROR_PRESUPUESTOS_REQUERIMIENTO =
            "compras.requerimiento.errorPresupuestos";
    public static final String ATTR_PRESUPUESTO_DOCUMENTO_VALIDO =
            "compras.requerimiento.presupuestoDocumentoValido";
    public static final String ATTR_PRESUPUESTO_DOWNLOAD_URL =
            "compras.requerimiento.presupuestoDownloadURL";
    public static final String ATTR_ORDENES_MEDICAS_REQUERIMIENTO =
            "compras.requerimiento.ordenesMedicas";
    public static final String ATTR_ERROR_ORDENES_MEDICAS_REQUERIMIENTO =
            "compras.requerimiento.errorOrdenesMedicas";

    // RC0-B - CONTRATO JSP RUNTIME.
    public static final String ATTR_REQUERIMIENTO_MODELO =
            "compras.requerimiento.req";
    public static final String ATTR_REQUERIMIENTO_ES_NUEVO =
            "compras.requerimiento.esNuevo";
    public static final String ATTR_REQUERIMIENTO_SURGE_SELECCIONADO =
            "compras.requerimiento.surgeSeleccionado";
    public static final String ATTR_REQUERIMIENTO_PUEDE_ABM =
            "compras.requerimiento.puedeABM";
    public static final String ATTR_REQUERIMIENTO_PUEDE_COTIZAR =
            "compras.requerimiento.puedeCotizar";
    public static final String ATTR_REQUERIMIENTO_SOLO_LECTURA_SOLICITADA =
            "compras.requerimiento.soloLecturaSolicitada";
    public static final String ATTR_REQUERIMIENTO_PUEDE_EDITAR_ESTRUCTURA =
            "compras.requerimiento.puedeEditarEstructura";
    public static final String ATTR_REQUERIMIENTO_PUEDE_EDITAR_SURGE =
            "compras.requerimiento.puedeEditarSurge";
    public static final String ATTR_REQUERIMIENTO_PUEDE_EDITAR_COTIZACION =
            "compras.requerimiento.puedeEditarCotizacion";
    public static final String ATTR_REQUERIMIENTO_MODO_EDITABLE =
            "compras.requerimiento.modoEditable";
    public static final String ATTR_REQUERIMIENTO_SECTORES =
            "compras.requerimiento.sectores";
    public static final String ATTR_REQUERIMIENTO_VOLVER_URL =
            "compras.requerimiento.volverURL";
    public static final String ATTR_REQUERIMIENTO_IMPRIMIR_URL =
            "compras.requerimiento.imprimirURL";
    public static final String ATTR_REQUERIMIENTO_SECTOR_ID =
            "compras.requerimiento.reqSectorId";
    public static final String ATTR_REQUERIMIENTO_SECTOR_DESCRIPCION =
            "compras.requerimiento.sectorDescripcion";
    public static final String ATTR_REQUERIMIENTO_ID_TERCERIZADORA =
            "compras.requerimiento.idTercerizadora";
    public static final String ATTR_REQUERIMIENTO_CARGO_OSPIM =
            "compras.requerimiento.cargoOspim";
    public static final String ATTR_REQUERIMIENTO_CARGO_TERCERIZADORA =
            "compras.requerimiento.cargoTercerizadora";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_CUIL =
            "compras.requerimiento.afiliadoCuil";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_INT =
            "compras.requerimiento.afiliadoInt";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_TIPO_DOCUMENTO =
            "compras.requerimiento.afiliadoTipoDocumento";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NUMERO_DOCUMENTO =
            "compras.requerimiento.afiliadoNumeroDocumento";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_APELLIDO =
            "compras.requerimiento.afiliadoApellido";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NOMBRE =
            "compras.requerimiento.afiliadoNombre";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_SECCIONAL =
            "compras.requerimiento.afiliadoSeccional";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_BAJA_FECHA =
            "compras.requerimiento.afiliadoBajaFecha";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_FECHA_ALTA =
            "compras.requerimiento.afiliadoFechaAlta";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_ID_TERCERIZADORA =
            "compras.requerimiento.afiliadoIdTercerizadora";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_INCAPACIDAD =
            "compras.requerimiento.afiliadoIncapacidad";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_ANTECEDENTES =
            "compras.requerimiento.afiliadoAntecedentes";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_ID_SECCIONAL =
            "compras.requerimiento.afiliadoIdSeccional";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NUMERO_OSPIM =
            "compras.requerimiento.afiliadoNumeroOspim";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NUMERO_UOMA =
            "compras.requerimiento.afiliadoNumeroUoma";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NUMERO_AMTIMA =
            "compras.requerimiento.afiliadoNumeroAmtima";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NUMERO =
            "compras.requerimiento.afiliadoNumero";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_NOMBRE_PLAN =
            "compras.requerimiento.afiliadoNombrePlan";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_ID_PLAN =
            "compras.requerimiento.afiliadoIdPlan";
    public static final String ATTR_REQUERIMIENTO_AFILIADO_TERCERIZADORA =
            "compras.requerimiento.afiliadoTercerizadora";
    public static final String ATTR_REQUERIMIENTO_MOSTRAR_PANEL_AFILIADO =
            "compras.requerimiento.mostrarPanelAfiliado";
    public static final String ATTR_REQUERIMIENTO_ERROR_ALERT =
            "compras.requerimiento.errorParaAlert";
    public static final String ATTR_REQUERIMIENTO_ERROR_CAMPO =
            "compras.requerimiento.errorCampo";
    public static final String ATTR_REQUERIMIENTO_MSG_DETALLE_GUARDADO =
            "compras.requerimiento.msgDetalleGuardado";
    public static final String ATTR_REQUERIMIENTO_MSG_DETALLE_BORRADO =
            "compras.requerimiento.msgDetalleBorrado";
    public static final String ATTR_REQUERIMIENTO_MSG_ANULADO =
            "compras.requerimiento.msgAnulado";
    public static final String ATTR_REQUERIMIENTO_OPERACION =
            "compras.requerimiento.operacion";
    public static final String ATTR_REQUERIMIENTO_MOSTRAR_MENSAJE_GUARDADO =
            "compras.requerimiento.mostrarMensajeGuardado";
    public static final String ATTR_REQUERIMIENTO_MOSTRAR_ERROR_GENERICO =
            "compras.requerimiento.mostrarErrorGenerico";
    public static final String ATTR_REQUERIMIENTO_ID_MENSAJE =
            "compras.requerimiento.idMensaje";
    public static final String ATTR_REQUERIMIENTO_TITULO =
            "compras.requerimiento.titulo";

    // RC0-C - CODIGOS DE DOMINIO.
    public static final String TIPO_ITEM_NOMENCLADOR = "NOMENCLADOR";
    public static final String TIPO_ITEM_MEDICAMENTO = "MEDICAMENTO";
    public static final String TIPO_ITEM_OBSERVACION = "OBSERVACION";

    public static final int TIPO_DOCUMENTO_PRESUPUESTO = 1;
    public static final int TIPO_DOCUMENTO_ORDEN_MEDICA = 2;
    public static final int TIPO_DOCUMENTO_COTIZACION_EMPRESA = 3;

    public static final String RESULTADO_NOTIFICACION_ENVIADO = "ENVIADO";
    public static final String RESULTADO_NOTIFICACION_OMITIDO = "OMITIDO";
    public static final String RESULTADO_NOTIFICACION_EMAIL_INVALIDO =
            "EMAIL_INVALIDO";
    public static final String RESULTADO_NOTIFICACION_ERROR = "ERROR";

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
    public static final int FILTRO_NOMENCLADOR_ODONTOLOGIA = 1;
    public static final int FILTRO_NOMENCLADOR_DISCAPACIDAD = 8;
    public static final int FILTRO_NOMENCLADOR_FARMACIA = 9;
    public static final int MARCA_REIN_LIQ_DISCAPACIDAD = 6;

    public static final int TIPO_NOMENCLADOR_PRACTICAS_ESPECIALIZADAS = 2;
    public static final int TIPO_NOMENCLADOR_PROPIO = 3;
    public static final int TIPO_NOMENCLADOR_ANALISIS_CLINICOS = 4;
    public static final int TIPO_NOMENCLADOR_QUIRURGICO = 6;
    public static final int TIPO_NOMENCLADOR_PROTESIS_INSUMOS = 10;
    public static final int TIPO_PRESTACION_INSUMOS = 6;

    public static boolean esTipoPrestacionInsumos(
            int idTipoPrestacion) {

        return idTipoPrestacion == TIPO_PRESTACION_INSUMOS;
    }

    public static boolean esTipoNomencladorPrestacionesMedicas(
            int idTipoNomenclador) {

        return idTipoNomenclador
                == TIPO_NOMENCLADOR_ANALISIS_CLINICOS
                || idTipoNomenclador
                == TIPO_NOMENCLADOR_PRACTICAS_ESPECIALIZADAS
                || idTipoNomenclador
                == TIPO_NOMENCLADOR_PROTESIS_INSUMOS
                || idTipoNomenclador
                == TIPO_NOMENCLADOR_QUIRURGICO
                || idTipoNomenclador
                == TIPO_NOMENCLADOR_PROPIO;
    }

    public static final String
            CODIGO_ESPECIAL_DISCAPACIDAD = "431003";

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

    public static String getEstadoDescripcion(int estado) {
        switch (estado) {
            case ESTADO_PENDIENTE:
                return "PENDIENTE";
            case ESTADO_A_COTIZAR:
                return "ENVIADO A COTIZAR";
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
            String sectorDescripcion,
            boolean hayDetalles,
            boolean hayCotizacionesEmpresa) {

        return esPendiente(estado)
                && esSectorSinCotizacionPrestador(
                        sectorDescripcion
                )
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

    public static Integer getFiltroTipoNomencladorCompras(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        if (SECTOR_FARMACIA.equals(sector)) {
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_FARMACIA
            );
        }

        if (SECTOR_DISCAPACIDAD.equals(sector)) {
            /*
             * Reclamos Prestacionales parte del tipo 8,
             * pero la consulta efectiva utiliza marca ReinLiq 6.
             */
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_DISCAPACIDAD
            );
        }

        if (SECTOR_ODONTOLOGIA.equals(sector)) {
            return Integer.valueOf(
                    FILTRO_NOMENCLADOR_ODONTOLOGIA
            );
        }

        if (SECTOR_PRESTACIONES_MEDICAS.equals(sector)) {
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

        if (SECTOR_FARMACIA.equals(sector)) {
            return idTipoNomenclador
                    == FILTRO_NOMENCLADOR_FARMACIA;
        }

        if (SECTOR_DISCAPACIDAD.equals(sector)) {
            return marcaReinLiq
                    == MARCA_REIN_LIQ_DISCAPACIDAD
                    || CODIGO_ESPECIAL_DISCAPACIDAD
                    .equals(codigo);
        }

        if (SECTOR_ODONTOLOGIA.equals(sector)) {
            return idTipoNomenclador
                    == FILTRO_NOMENCLADOR_ODONTOLOGIA;
        }

        if (SECTOR_PRESTACIONES_MEDICAS.equals(sector)) {
            return esTipoNomencladorPrestacionesMedicas(
                    idTipoNomenclador
            );
        }

        return false;
    }

    public static boolean esNomencladorValidoParaTipoPrestacionCompras(
            String sectorDescripcion,
            int idTipoPrestacion,
            int idTipoNomenclador,
            int marcaReinLiq,
            String codigoNomenclador) {

        if (!esNomencladorValidoParaSectorCompras(
                sectorDescripcion,
                idTipoNomenclador,
                marcaReinLiq,
                codigoNomenclador
        )) {
            return false;
        }

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        if (!SECTOR_PRESTACIONES_MEDICAS.equals(sector)) {
            return true;
        }

        if (idTipoPrestacion <= 0) {
            return false;
        }

        if (esTipoPrestacionInsumos(idTipoPrestacion)) {
            return idTipoNomenclador
                    == TIPO_NOMENCLADOR_PROTESIS_INSUMOS;
        }

        return idTipoNomenclador
                != TIPO_NOMENCLADOR_PROTESIS_INSUMOS;
    }

    public static boolean esSectorDetalleObservacionCompras(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        return SECTOR_RRHH.equals(sector)
                || SECTOR_LEGALES.equals(sector)
                || SECTOR_SISTEMAS.equals(sector)
                || SECTOR_OTROS.equals(sector);
    }

    public static boolean esSectorSinCotizacionPrestador(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        return SECTOR_RRHH.equals(sector)
                || SECTOR_SISTEMAS.equals(sector);
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

    public static String getSectorReclamoPrestacional(
            String sectorDescripcion) {

        String sector =
                normalizarSectorCompra(
                        sectorDescripcion
                );

        if (SECTOR_PRESTACIONES_MEDICAS.equals(sector)) {
            return "PRESTACIONES MEDICAS";
        }

        if (SECTOR_DISCAPACIDAD.equals(sector)
                || sector.indexOf("DISCAPAC") >= 0) {

            return "DISCAPACIDAD";
        }

        if (SECTOR_FARMACIA.equals(sector)
                || sector.indexOf("FARMAC") >= 0) {

            return "FARMACIA";
        }

        if (SECTOR_ODONTOLOGIA.equals(sector)
                || sector.indexOf("ODONTO") >= 0) {

            return "ODONTOLOGIA";
        }

        if (SECTOR_LEGALES.equals(sector)
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
        String normalizado = normalizarTexto(value);

        if (normalizado.length() == 0) {
            return null;
        }

        return normalizado;
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
