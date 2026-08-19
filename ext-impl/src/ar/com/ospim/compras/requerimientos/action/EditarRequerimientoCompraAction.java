package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.ActionRequestWrapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EditarRequerimientoCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(EditarRequerimientoCompraAction.class);

    /*
     * Blindaje anti doble envío.
     *
     * Se usa un SET de tokens, no un único token, para no romper pantallas
     * abiertas en múltiples tabs. Cada render agrega un token válido.
     * Cada save consume exactamente un token.
     */
    private static final String PARAM_COMPRAS_SAVE_TOKEN =
            "compras_save_token";

    private static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";

    private static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";

    private static final int MAX_TOKENS_GUARDADO_COMPRA = 20;

    private static final int MAX_DETALLES_COTIZACION_RETORNO = 1000;

    private static final String STRUTS_ACTION_NUEVO_REQUERIMIENTO =
            "/compras/nuevo_requerimiento";

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    private static final String PARAM_ORDEN_MEDICA_COUNT =
            "orden_medica_count";


    /*
     * Adaptador HTTP de detalles. Reconstruye parámetros y delega las
     * reglas funcionales canónicas en EditarRequerimientoCompraHelper.
     */
    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    private final EditarRequerimientoCompraHelper requerimientoHelper =
            new EditarRequerimientoCompraHelper();

    private final RequerimientoCompraReclamoPrestacionalHelper reclamoHelper =
            new RequerimientoCompraReclamoPrestacionalHelper();

    private boolean esAltaRequerimiento(RenderRequest renderRequest) {
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        int idRequerimientoCompra =
                ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

        Object idAttr =
                renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);

        if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
            idRequerimientoCompra = ((Integer) idAttr).intValue();
        }

        return STRUTS_ACTION_NUEVO_REQUERIMIENTO.equals(strutsAction)
                || "alta".equalsIgnoreCase(modo)
                || idRequerimientoCompra <= 0;
    }

    private boolean vieneDeAlta(ActionRequest actionRequest) {
        String strutsAction = getParametroTrim(actionRequest, "struts_action");
        String modo = getParametroTrim(actionRequest, "modo");

        int idRequerimientoCompra = 0;

        try {
            idRequerimientoCompra =
                    parseEnteroConDefault(
                            actionRequest,
                            "id_requerimiento_compra",
                            "ID del requerimiento",
                            0
                    );
        } catch (Exception e) {
            idRequerimientoCompra = 0;
        }

        return STRUTS_ACTION_NUEVO_REQUERIMIENTO.equals(strutsAction)
                || "alta".equalsIgnoreCase(modo)
                || idRequerimientoCompra <= 0;
    }

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        ActionRequest actionRequestOriginal = actionRequest;
        UploadPortletRequest uploadRequest =
                obtenerUploadPortletRequest(actionRequestOriginal);

        if (uploadRequest != null) {
            actionRequest = new MultipartActionRequest(
                    actionRequestOriginal,
                    uploadRequest
            );
        }

        String cmd = getParametroTrim(actionRequest, Constants.CMD);

        int idRequerimientoCompra =
                parseEnteroConDefault(
                        actionRequest,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                );

        boolean altaOriginal = vieneDeAlta(actionRequest);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = getUsuario(user);

            if ("saveCotizacion".equals(cmd)
                    || "cerrarCotizacion".equals(cmd)) {

                validarPermisoCotizar(user);
                consumirTokenGuardadoCompra(actionRequest);

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra."
                    );
                }

                List detallesCotizacion =
                        getDetallesCotizacionFromRequest(actionRequest);

                GuardadoCotizacionResultado resultado =
                        requerimientoHelper
                                .guardarAvanceCotizacion(
                                        idRequerimientoCompra,
                                        detallesCotizacion,
                                        usuario
                                );

                if (resultado.getEstadoFinal()
                        == WebKeysCompras.ESTADO_COTIZADO) {

                    SessionMessages.add(
                            actionRequest,
                            "requerimiento-compra-cotizacion-completa"
                    );
                    actionResponse.setRenderParameter(
                            "struts_action",
                            "/compras/ver_requerimiento"
                    );
                    setForward(
                            actionRequest,
                            WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
                    );
                } else {
                    SessionMessages.add(
                            actionRequest,
                            "requerimiento-compra-cotizacion-guardada"
                    );
                    actionResponse.setRenderParameter(
                            "struts_action",
                            STRUTS_ACTION_EDITAR_REQUERIMIENTO
                    );
                    setForward(
                            actionRequest,
                            WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                    );
                }

                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
                actionResponse.setRenderParameter(
                        "compras_operacion",
                        "saveCotizacion"
                );

                return;
            }

            if ("saveAll".equals(cmd)) {
                validarPermisoABM(user);
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento =
                        getRequerimientoFromRequest(actionRequest);

                boolean guardarDetallesEstructura = true;

                if (requerimiento.getIdRequerimientoCompra() <= 0) {
                    requerimiento.setIdEstado(
                            WebKeysCompras.ESTADO_PENDIENTE
                    );
                } else {
                    RequerimientoCompra actual =
                            BusquedaRequerimientoCompraServiceUtil
                                    .getRequerimientoCompra(
                                            requerimiento
                                                    .getIdRequerimientoCompra()
                                    );

                    guardarDetallesEstructura =
                            actual != null
                                    && actual.puedeEditarEstructura();
                }

                idRequerimientoCompra = guardarCabeceraRequerimiento(
                        actionRequestOriginal,
                        actionRequest,
                        uploadRequest,
                        requerimiento,
                        usuario
                );

                if (guardarDetallesEstructura) {
                    detalleHelper.guardarDetallesDesdeRequest(
                            actionRequest,
                            idRequerimientoCompra,
                            usuario
                    );
                }

                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("compras_operacion", "saveAll");

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-guardado"
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if (Constants.ADD.equals(cmd)
                    || Constants.UPDATE.equals(cmd)) {

                validarPermisoABM(user);
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento =
                        getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() <= 0) {
                    requerimiento.setIdEstado(
                            WebKeysCompras.ESTADO_PENDIENTE
                    );
                }

                idRequerimientoCompra = guardarCabeceraRequerimiento(
                        actionRequestOriginal,
                        actionRequest,
                        uploadRequest,
                        requerimiento,
                        usuario
                );

                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("compras_operacion", cmd);

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-guardado"
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                validarPermisoAnular(user);

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra a anular."
                    );
                }

                requerimientoHelper.cambiarEstado(
                        idRequerimientoCompra,
                        WebKeysCompras.ESTADO_ANULADO,
                        usuario
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-anulado"
                );
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VIEW);
                return;
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        } catch (Exception e) {
            String mensaje =
                    obtenerMensajeUsuario(
                            e,
                            "No se pudo procesar el requerimiento de compra."
                    );

            registrarErrorAction(
                    "procesar el requerimiento de compra",
                    cmd,
                    idRequerimientoCompra,
                    mensaje,
                    e
            );

            /*
             * El JSP muestra este mensaje de manera controlada.
             * No se agrega SessionErrors para evitar que Liferay
             * presente además su mensaje genérico.
             */
            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            String campoError =
                    obtenerCampoError(
                            e
                    );

            if (!WebKeysCompras.isEmpty(campoError)) {
                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        campoError
                );
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            if (altaOriginal && idRequerimientoCompra <= 0) {
                copiarParametrosOrdenesMedicas(
                        actionRequest,
                        actionResponse
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_NUEVO_REQUERIMIENTO
                );
                actionResponse.setRenderParameter(
                        "modo",
                        "alta"
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_ALTA_REQUERIMIENTO
                );
            } else {
                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );
            }

            actionResponse.setRenderParameter(
                    "compras_error",
                    "true"
            );

            if ("saveCotizacion".equals(cmd)
                    || "cerrarCotizacion".equals(cmd)) {

                copiarParametrosCotizacion(
                        actionRequest,
                        actionResponse
                );
            }

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null ? cmd : ""
            );
        }
    }

    private int guardarCabeceraRequerimiento(
            ActionRequest actionRequestOriginal,
            ActionRequest actionRequest,
            UploadPortletRequest uploadRequest,
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        /*
         * La edición de un requerimiento existente conserva exactamente
         * el comportamiento previo: no intenta volver a registrar
         * Órdenes médicas.
         */
        if (requerimiento.getIdRequerimientoCompra() > 0) {
            return requerimientoHelper
                    .guardarRequerimientoCompra(
                            requerimiento,
                            usuario
                    );
        }

        if (uploadRequest == null) {
            errorCampo(
                    DocumentoLibraryComprasHelper
                            .PARAM_ARCHIVO_ORDEN_MEDICA,
                    "Orden médica: debe seleccionar una imagen JPEG o PNG."
            );
            return 0;
        }

        int cantidadOrdenesMedicas =
                obtenerCantidadOrdenesMedicas(
                        actionRequest
                );

        DocumentoLibraryComprasHelper gestorDocumento =
                DocumentoLibraryComprasHelper.crear(
                        actionRequestOriginal
                );

        List<OrdenMedicaValidada> ordenesMedicas =
                new ArrayList<OrdenMedicaValidada>(
                        cantidadOrdenesMedicas
                );

        for (int indice = 0;
             indice < cantidadOrdenesMedicas;
             indice++) {

            String campoArchivo =
                    obtenerCampoArchivoOrdenMedica(
                            indice
                    );

            String campoFecha =
                    obtenerCampoFechaOrdenMedica(
                            indice
                    );

            try {

                OrdenMedicaValidada ordenMedica =
                        gestorDocumento.validarOrdenMedica(
                                uploadRequest,
                                campoArchivo,
                                getParametroTrim(
                                        actionRequest,
                                        campoFecha
                                ),
                                null
                        );

                ordenesMedicas.add(
                        ordenMedica
                );

            } catch (Exception e) {
                String mensaje =
                        obtenerMensajeUsuario(
                                e,
                                "No se pudo validar la Orden médica."
                        );

                String mensajeNormalizado =
                        mensaje != null
                                ? mensaje.toLowerCase(Locale.ROOT)
                                : "";

                String campo = campoArchivo;

                if (mensajeNormalizado.indexOf("fecha") >= 0) {
                    campo = campoFecha;
                }

                errorCampo(
                        campo,
                        mensaje
                );

                return 0;
            }
        }

        return requerimientoHelper
                .guardarNuevoRequerimientoCompraConOrdenesMedicas(
                        requerimiento,
                        ordenesMedicas,
                        gestorDocumento,
                        usuario
                );
    }

    private int obtenerCantidadOrdenesMedicas(
            ActionRequest actionRequest)
            throws ValidacionCompraException {

        int cantidad =
                parseEnteroConDefault(
                        actionRequest,
                        PARAM_ORDEN_MEDICA_COUNT,
                        "Cantidad de Órdenes médicas",
                        1
                );

        /*
         * El default 1 mantiene compatibilidad con formularios o callers
         * anteriores que todavía no envían orden_medica_count.
         */
        if (cantidad <= 0) {
            errorCampo(
                    PARAM_ORDEN_MEDICA_COUNT,
                    "Debe informar al menos una Orden médica."
            );
            return 0;

        }

        if (cantidad > EditarRequerimientoCompraHelper.MAX_ORDENES_MEDICAS_POR_ALTA) {
            errorCampo(
                    PARAM_ORDEN_MEDICA_COUNT,
                    "Se pueden cargar hasta "
                            + EditarRequerimientoCompraHelper.MAX_ORDENES_MEDICAS_POR_ALTA
                            + " Órdenes médicas por operación."
            );
            return 0;
        }

        return cantidad;
    }

    private String obtenerCampoArchivoOrdenMedica(
            int indice) {

        if (indice <= 0) {
            return DocumentoLibraryComprasHelper
                    .PARAM_ARCHIVO_ORDEN_MEDICA;
        }

        return DocumentoLibraryComprasHelper
                .PARAM_ARCHIVO_ORDEN_MEDICA
                + "_"
                + indice;
    }

    private String obtenerCampoFechaOrdenMedica(
            int indice) {

        if (indice <= 0) {
            return DocumentoLibraryComprasHelper
                    .PARAM_FECHA_ORDEN_MEDICA;
        }

        return DocumentoLibraryComprasHelper
                .PARAM_FECHA_ORDEN_MEDICA
                + "_"
                + indice;
    }

    private void copiarParametrosOrdenesMedicas(
            ActionRequest actionRequest,
            ActionResponse actionResponse) {

        int cantidad =
                1;

        try {
            cantidad =
                    obtenerCantidadOrdenesMedicas(
                            actionRequest
                    );
        } catch (Exception e) {
            /*
             * Estamos procesando un retorno por error.
             * No debe reemplazarse el error original por otro error
             * generado mientras se reconstruye la pantalla.
             */
            cantidad = 1;
        }

        if (cantidad <= 0) {
            cantidad = 1;
        }

        if (cantidad > EditarRequerimientoCompraHelper.MAX_ORDENES_MEDICAS_POR_ALTA) {
            cantidad = EditarRequerimientoCompraHelper.MAX_ORDENES_MEDICAS_POR_ALTA;
        }

        actionResponse.setRenderParameter(
                PARAM_ORDEN_MEDICA_COUNT,
                String.valueOf(cantidad)
        );

        for (int indice = 0;
             indice < cantidad;
             indice++) {

            String campoFecha =
                    obtenerCampoFechaOrdenMedica(
                            indice
                    );

            actionResponse.setRenderParameter(
                    campoFecha,
                    getParametroTrim(
                            actionRequest,
                            campoFecha
                    )
            );
        }
    }

    private UploadPortletRequest obtenerUploadPortletRequest(
            ActionRequest actionRequest) {

        if (actionRequest == null) {
            return null;
        }

        String contentType = actionRequest.getContentType();

        if (contentType == null
                || !contentType.toLowerCase(Locale.ROOT)
                .startsWith("multipart/")) {

            return null;
        }

        return PortalUtil.getUploadPortletRequest(actionRequest);
    }

    private static class MultipartActionRequest
            extends ActionRequestWrapper {

        private final UploadPortletRequest uploadRequest;

        public MultipartActionRequest(
                ActionRequest actionRequest,
                UploadPortletRequest uploadRequest) {

            super(actionRequest);
            this.uploadRequest = uploadRequest;
        }

        public String getParameter(String name) {
            return uploadRequest.getParameter(name);
        }

        public Map getParameterMap() {
            return uploadRequest.getParameterMap();
        }

        public Enumeration getParameterNames() {
            return uploadRequest.getParameterNames();
        }

        public String[] getParameterValues(String name) {
            return uploadRequest.getParameterValues(name);
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            renderRequest,
                            "id_requerimiento_compra",
                            0
                    );

            Object idAttr =
                    renderRequest.getAttribute(
                            WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION
                    );

            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            if (idRequerimientoCompra > 0) {
                validarPermisoConsulta(user);
            } else {
                validarPermisoABM(user);
            }

            RequerimientoCompra requerimiento;

            if (idRequerimientoCompra > 0) {
                requerimiento =
                        BusquedaRequerimientoCompraServiceUtil
                                .getRequerimientoCompra(idRequerimientoCompra);

                if (requerimiento == null) {
                    throw new Exception(
                            "No se encontró el requerimiento de compra informado."
                    );
                }
            } else {
                requerimiento = new RequerimientoCompra();

                int idSectorParam =
                        ParamUtil.getInteger(renderRequest, "sector_id", 0);

                if (idSectorParam > 0) {
                    requerimiento.setIdSector(Integer.valueOf(idSectorParam));
                }
            }

            boolean soloLectura = esModoSoloLectura(renderRequest);

            if (!puedeEditarRender(user, requerimiento)) {
                soloLectura = true;
            }

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.valueOf(soloLectura)
            );

            if (!soloLectura) {
                generarTokenGuardadoCompra(renderRequest);
            }

            cargarCatalogos(renderRequest);
            cargarAfiliadoRequerimiento(renderRequest, requerimiento);
            cargarEstadoPrestadoresPendientesNotificacion(
                    renderRequest,
                    requerimiento
            );
            cargarRelacionReclamoPrestacional(
                    renderRequest,
                    requerimiento
            );

            RequerimientoCompraRenderActionUtil
                    .publicarContexto(
                            renderRequest,
                            requerimiento
                    );

            if (soloLectura) {
                renderRequest.setAttribute(
                        WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW,
                        requerimiento
                );
                renderRequest.setAttribute(
                        WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW,
                        requerimiento.getDetalles()
                );
            } else {
                renderRequest.setAttribute(
                        WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION,
                        requerimiento
                );
                renderRequest.setAttribute(
                        WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                        requerimiento.getDetalles()
                );
            }

            /* Compatibilidad con JSP legacy que leen siempre el atributo EDICION. */
            renderRequest.setAttribute(
                    WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento.getDetalles()
            );
        } catch (Exception e) {
            String mensaje =
                    obtenerMensajeUsuario(
                            e,
                            "No se pudo cargar el requerimiento de compra."
                    );

            registrarErrorAction(
                    "cargar el requerimiento de compra",
                    "render",
                    ParamUtil.getInteger(
                            renderRequest,
                            "id_requerimiento_compra",
                            0
                    ),
                    mensaje,
                    e
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );
            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ERROR
            );
        }

        if (Boolean.TRUE.equals(
                renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR)
        ) || esModoSoloLectura(renderRequest)) {

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );
        }

        if (esAltaRequerimiento(renderRequest)) {
            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ALTA_REQUERIMIENTO
            );
        }

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
        );
    }

    private void cargarEstadoPrestadoresPendientesNotificacion(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) throws Exception {

        boolean hayPendientes = false;

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0
                && requerimiento.puedeReintentarNotificaciones()) {

            try {
                hayPendientes =
                        BusquedaRequerimientoCompraServiceUtil
                                .hayPrestadoresPendientesNotificacion(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );
            } catch (Exception e) {
                _log.warn(
                        "No se pudo confirmar si quedan prestadores "
                                + "pendientes de notificación. "
                                + "El botón permanecerá oculto. "
                                + "idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra(),
                        e
                );
                hayPendientes = false;
            }
        }

        renderRequest.setAttribute(
                WebKeysCompras.HAY_PRESTADORES_PENDIENTES_NOTIFICACION,
                Boolean.valueOf(hayPendientes)
        );
    }

    private void cargarRelacionReclamoPrestacional(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        boolean consultaOk = true;
        Object relacion = null;

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0
                && (
                WebKeysCompras.esCotizado(
                        requerimiento.getEstado()
                )
                        || WebKeysCompras.esReclamoRP(
                        requerimiento.getEstado()
                )
        )) {

            try {
                relacion =
                        reclamoHelper
                                .obtenerPorRequerimiento(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );
            } catch (Exception e) {
                consultaOk = false;

                _log.warn(
                        "No se pudo consultar la relación con el "
                                + "Reclamo Prestacional. "
                                + "La acción permanecerá oculta. "
                                + "idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra(),
                        e
                );
            }
        }


        renderRequest.setAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_COMPRA,
                relacion
        );
        renderRequest.setAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK,
                Boolean.valueOf(consultaOk)
        );
    }

    private void generarTokenGuardadoCompra(RenderRequest renderRequest) {
        if (renderRequest == null) {
            return;
        }

        String token = UUID.randomUUID().toString();
        PortletSession session = renderRequest.getPortletSession();

        synchronized (session) {
            Set tokens = null;
            Object tokensObj = session.getAttribute(SESSION_COMPRAS_SAVE_TOKENS);

            if (tokensObj instanceof Set) {
                tokens = (Set) tokensObj;
            }

            if (tokens == null || tokens.size() >= MAX_TOKENS_GUARDADO_COMPRA) {
                tokens = new HashSet();
            }

            tokens.add(token);
            session.setAttribute(SESSION_COMPRAS_SAVE_TOKENS, tokens);
        }

        renderRequest.setAttribute(ATTR_COMPRAS_SAVE_TOKEN, token);
    }

    private void consumirTokenGuardadoCompra(ActionRequest actionRequest)
            throws ValidacionCompraException {

        String tokenRequest =
                getParametroTrim(actionRequest, PARAM_COMPRAS_SAVE_TOKEN);
        PortletSession session = actionRequest.getPortletSession();

        synchronized (session) {
            Object tokensObj = session.getAttribute(SESSION_COMPRAS_SAVE_TOKENS);

            if (!(tokensObj instanceof Set)) {
                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla está desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            Set tokens = (Set) tokensObj;

            if (WebKeysCompras.isEmpty(tokenRequest)
                    || !tokens.contains(tokenRequest)) {

                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla está desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            tokens.remove(tokenRequest);

            if (tokens.isEmpty()) {
                session.removeAttribute(SESSION_COMPRAS_SAVE_TOKENS);
            } else {
                session.setAttribute(SESSION_COMPRAS_SAVE_TOKENS, tokens);
            }
        }
    }

    private boolean esModoSoloLectura(RenderRequest renderRequest) {
        String strutsAction =
                ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        return "/compras/ver_requerimiento".equals(strutsAction)
                || "ver".equalsIgnoreCase(modo);
    }

    private void cargarCatalogos(
            RenderRequest request) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );
        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );

    }

    private void cargarAfiliadoRequerimiento(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        renderRequest.removeAttribute(
                WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA
        );

        if (requerimiento == null || !requerimiento.tieneAfiliadoInformado()) {
            return;
        }

        try {
            List<Afiliado> afiliados =
                    BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
                            requerimiento.getAfiliadoCuilTitular(),
                            requerimiento.getAfiliadoIntString(),
                            null,
                            null,
                            0,
                            null,
                            null,
                            WebKeysGlobal.ID_DEFAULT_ENTIDAD,
                            0,
                            0,
                            new BigDecimal(0)
                    );

            if (afiliados != null && afiliados.size() == 1) {
                renderRequest.setAttribute(
                        WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA,
                        afiliados.get(0)
                );
            }
        } catch (Exception e) {
            _log.warn(
                    "No se pudo completar el componente visual del afiliado. "
                            + "Se conservará el snapshot del requerimiento.",
                    e
            );
        }
    }

    private void setIdRequerimientoEnRequest(
            ActionRequest request,
            ActionResponse response,
            int idRequerimientoCompra) {

        request.setAttribute(
                WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                Integer.valueOf(idRequerimientoCompra)
        );
        response.setRenderParameter(
                "id_requerimiento_compra",
                String.valueOf(idRequerimientoCompra)
        );
    }

    private String getUsuario(User user) {
        return user != null ? user.getScreenName() : "sistema";
    }

    private boolean puedeEditarRender(
            User user,
            RequerimientoCompra requerimiento) throws Exception {

        if (user == null || requerimiento == null) {
            return false;
        }

        if (PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        ) && (
                requerimiento.puedeEditarEstructura()
                        || requerimiento.puedeEditarSurge()
        )) {

            return true;
        }

        return PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        ) && requerimiento.puedeEditarCotizacion();
    }

    private void validarPermisoConsulta(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        boolean puedeVer = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_VIEW_COMPRAS
        );
        boolean puedeAdministrar = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        );
        boolean puedeCotizar = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );
        boolean puedeAnular = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        );

        if (!puedeVer && !puedeAdministrar && !puedeCotizar && !puedeAnular) {
            errorCampo(
                    "permisos",
                    "No posee permisos para consultar requerimientos de compras."
            );
        }
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    private void validarPermisoCotizar(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para cotizar requerimientos de compras."
            );
        }
    }

    private void validarPermisoAnular(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para anular requerimientos de compras."
            );
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(
            ActionRequest request) throws Exception {

        RequerimientoCompra requerimiento = new RequerimientoCompra();

        int idRequerimientoCompra = parseEnteroConDefault(
                request,
                "id_requerimiento_compra",
                "ID del requerimiento",
                0
        );
        requerimiento.setIdRequerimientoCompra(idRequerimientoCompra);

        int idSector =
                parseEnteroConDefault(request, "id_sector", "Sector", 0);

        if (idSector <= 0) {
            idSector =
                    parseEnteroConDefault(request, "sector_id", "Sector", 0);
        }

        requerimiento.setIdSector(
                idSector > 0 ? Integer.valueOf(idSector) : null
        );
        requerimiento.setAfiliadoCuilTitular(
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(
                                request,
                                "afiliado_cuil_titular",
                                null
                        )
                )
        );

        String afiliadoIntRaw = getParametroTrim(request, "afiliado_int");

        if (WebKeysCompras.isEmpty(afiliadoIntRaw)
                || "-1".equals(afiliadoIntRaw)) {
            requerimiento.setAfiliadoInt(null);
        } else {
            requerimiento.setAfiliadoInt(
                    parseEnteroOpcional(
                            request,
                            "afiliado_int",
                            "Afiliado - integrante"
                    )
            );
        }

        Integer cargoOspim = parsePorcentajeDesdeRequest(
                request,
                "cargo_ospim",
                "Cargo OSPIM"
        );
        Integer cargoTercerizadora = parsePorcentajeDesdeRequest(
                request,
                "cargo_tercerizadora",
                "Cargo tercerizadora"
        );

        requerimiento.setCargoOspim(cargoOspim);
        requerimiento.setCargoTercerizadora(cargoTercerizadora);

        String idTercerizadora =
                getParametroTrim(request, "id_tercerizadora");

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(
                    request,
                    "requerimiento_id_tercerizadora_visible"
            );
        }

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(
                    request,
                    "requerimiento_id_tercerizadora"
            );
        }

        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            requerimiento.setIdTercerizadora(
                    idTercerizadora.trim().toUpperCase()
            );
        } else {
            requerimiento.setIdTercerizadora(null);
        }

        requerimiento.setRecupero(
                cargoTercerizadora != null
                        && cargoTercerizadora.intValue() > 0
        );
        requerimiento.setSurge(
                parseSurgeObligatorio(request)
        );
        requerimiento.setObservaciones(
                getParametroRaw(request, "observaciones", null)
        );

        return requerimiento;
    }

    /*
     * Contrato transitorio compatible:
     *
     * 1. Si llega id_prestador_adjudicado, se aplica a todos los detalles y
     *    cualquier ID legacy diferente se considera manipulación.
     * 2. Si todavía llega el formulario antiguo por detalle, se acepta solo
     *    cuando todos los IDs no vacíos coinciden y se replica ese único ID.
     * 3. Si no se eligió prestador, se conservan null para permitir guardar un
     *    avance parcial de precios en A COTIZAR.
     */
    private List getDetallesCotizacionFromRequest(ActionRequest request)
            throws Exception {

        int count = parseEnteroConDefault(
                request,
                "detalle_count",
                "Cantidad de detalles",
                0
        );

        int idPrestadorAdjudicado = parseEnteroConDefault(
                request,
                WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                "Prestador adjudicado",
                0
        );

        List detalles = new ArrayList();
        Set idsPrestadorLegacy = new HashSet();

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";

            int idDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id",
                    "Detalle #" + (i + 1),
                    0
            );

            if (idDetalle <= 0) {
                errorCampo(
                        prefix + "id",
                        "Detalle #" + (i + 1)
                                + ": no se recibió un ID válido."
                );
            }

            RequerimientoCompraDetalle detalle =
                    new RequerimientoCompraDetalle();
            detalle.setId(Integer.valueOf(idDetalle));

            String precioUnitarioRaw =
                    getParametroTrim(
                            request,
                            prefix
                                    + "precio_unitario_estimado"
                    );

            _log.info(
                    "[COMPRAS-COTIZACION][REQUEST]"
                            + " index=" + i
                            + ", idDetalle=" + idDetalle
                            + ", parametro="
                            + prefix
                            + "precio_unitario_estimado"
                            + ", valorRaw=["
                            + precioUnitarioRaw
                            + "]"
            );

            BigDecimal precioUnitario =
                    parseBigDecimalNullable(
                            precioUnitarioRaw,
                            "Detalle #" + (i + 1)
                                    + " - Precio unitario"
                    );
            detalle.setPrecioUnitarioEstimado(precioUnitario);

            int idPrestadorDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id_prestador",

                    "Detalle #" + (i + 1) + " - Prestador",
                    0
            );

            if (idPrestadorDetalle > 0) {
                idsPrestadorLegacy.add(Integer.valueOf(idPrestadorDetalle));
            }

            if (idPrestadorAdjudicado > 0
                    && idPrestadorDetalle > 0
                    && idPrestadorDetalle != idPrestadorAdjudicado) {

                errorCampo(
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        "La cotización fue manipulada: el prestador del detalle #"
                                + (i + 1)
                                + " no coincide con el prestador adjudicado."
                );
            }

            detalles.add(detalle);
        }

        if (idPrestadorAdjudicado <= 0) {
            if (idsPrestadorLegacy.size() > 1) {
                errorCampo(
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        "Debe seleccionar un único prestador adjudicado para "
                                + "todo el requerimiento."
                );
            }

            if (idsPrestadorLegacy.size() == 1) {
                idPrestadorAdjudicado =
                        ((Integer) idsPrestadorLegacy.iterator().next())
                                .intValue();
            }
        }

        Integer prestadorUnico = idPrestadorAdjudicado > 0
                ? Integer.valueOf(idPrestadorAdjudicado)
                : null;

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle =
                    (RequerimientoCompraDetalle) detalles.get(i);
            detalle.aplicarPrestadorAdjudicado(prestadorUnico);
        }

        if (detalles.size() != count) {
            throw new Exception(
                    "Se esperaban "
                            + count
                            + " detalles de cotización, "
                            + "pero se reconstruyeron "
                            + detalles.size()
                            + "."
            );
        }

        return detalles;
    }

    private void copiarParametrosCotizacion(
            ActionRequest request,
            ActionResponse response) {

        int count;

        try {
            count = Integer.parseInt(
                    getParametroTrim(request, "detalle_count")
            );
        } catch (Exception e) {
            count = 0;
        }

        if (count < 0) {
            count = 0;
        } else if (count > MAX_DETALLES_COTIZACION_RETORNO) {
            count = MAX_DETALLES_COTIZACION_RETORNO;
        }

        response.setRenderParameter("detalle_count", String.valueOf(count));

        String prestadorAdjudicado = getParametroRaw(
                request,
                WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                null
        );

        if (prestadorAdjudicado != null) {
            response.setRenderParameter(
                    WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                    prestadorAdjudicado
            );
        }

        String[] campos = {
                "id",
                "precio_unitario_estimado",
                "id_prestador",
                "prestador_label"
        };

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";

            for (int j = 0; j < campos.length; j++) {
                String nombre = prefix + campos[j];
                String valor = getParametroRaw(request, nombre, null);

                if (valor != null) {
                    response.setRenderParameter(nombre, valor);
                }
            }
        }
    }

    private BigDecimal parseBigDecimalNullable(
            String value,
            String label) throws ValidacionCompraException {

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        String original = value.trim();
        String clean = original.replace(" ", "");

        if (clean.indexOf(',') >= 0) {
            clean = clean.replace(".", "").replace(",", ".");
        }

        if (!clean.matches("^-?[0-9]+(\\.[0-9]+)?$")) {
            errorCampo(
                    label,
                    label + ": importe inválido. Valor recibido: '"
                            + original
                            + "'. Use formatos como 1234.56 o 1.234,56."
            );
        }

        try {
            return new BigDecimal(clean);
        } catch (Exception e) {
            errorCampo(
                    label,
                    label + ": no se pudo interpretar el importe '"
                            + original + "'."
            );
        }

        return null;
    }

    private String obtenerMensajeUsuario(
            Throwable error,
            String mensajePredeterminado) {

        Throwable actual = error;
        Set<Throwable> visitados =
                new HashSet<Throwable>();

        while (actual != null
                && visitados.add(actual)) {

            if (actual instanceof ValidacionCompraException
                    || "MensajeUsuarioException".equals(
                    actual.getClass().getSimpleName()
            )) {

                String mensaje =
                        normalizarMensajeUsuario(
                                actual.getMessage()
                        );

                if (!WebKeysCompras.isEmpty(mensaje)) {
                    return mensaje;
                }
            }

            actual = actual.getCause();
        }

        actual = error;
        visitados.clear();

        while (actual != null
                && visitados.add(actual)) {

            String mensaje =
                    normalizarMensajeUsuario(
                            actual.getMessage()
                    );

            if (esMensajeAptoParaUsuario(mensaje)) {
                return mensaje;
            }

            actual = actual.getCause();
        }

        return mensajePredeterminado;
    }

    private String obtenerCampoError(Throwable error) {
        Throwable actual = error;
        Set<Throwable> visitados =
                new HashSet<Throwable>();

        while (actual != null
                && visitados.add(actual)) {

            if (actual instanceof ValidacionCompraException) {
                return ((ValidacionCompraException) actual)
                        .getCampo();
            }

            actual = actual.getCause();
        }

        return null;
    }

    private String normalizarMensajeUsuario(String mensaje) {
        if (mensaje == null) {
            return null;
        }

        String limpio =
                mensaje
                        .replace('\r', ' ')
                        .replace('\n', ' ')
                        .replaceAll("\\s+", " ")
                        .trim();

        return limpio.length() > 0
                ? limpio
                : null;
    }

    private boolean esMensajeAptoParaUsuario(String mensaje) {
        if (WebKeysCompras.isEmpty(mensaje)
                || mensaje.length() > 500) {

            return false;
        }

        String clave =
                mensaje.toUpperCase(Locale.ROOT);

        return !clave.startsWith("ERROR:")
                && !clave.contains("ORG.POSTGRESQL")
                && !clave.contains("SQLSTATE")
                && !clave.contains("PL/PGSQL")
                && !clave.contains("PLPGSQL")
                && !clave.contains(" WHERE:")
                && !clave.contains(" DETAIL:")
                && !clave.contains(" CONTEXT:")
                && !clave.contains(" HINT:")
                && !clave.contains("RAISE")
                && !clave.contains("JDBC")
                && !clave.contains("CALLABLESTATEMENT")
                && !clave.contains("PREPAREDSTATEMENT")
                && !clave.contains("STACK TRACE")
                && !clave.contains(".JAVA:");
    }

    private void registrarErrorAction(
            String operacion,
            String cmd,
            int idRequerimientoCompra,
            String mensajeUsuario,
            Throwable error) {

        String contexto =
                "No se pudo " + operacion
                        + ". cmd=" + cmd
                        + ", idRequerimiento="
                        + idRequerimientoCompra
                        + ", mensajeUsuario="
                        + mensajeUsuario;

        if (esErrorFuncional(error)) {
            _log.warn(contexto);
            return;
        }

        _log.error(
                contexto,
                error
        );
    }

    private boolean esErrorFuncional(Throwable error) {
        Throwable actual = error;
        Set<Throwable> visitados =
                new HashSet<Throwable>();

        while (actual != null
                && visitados.add(actual)) {

            if (actual instanceof ValidacionCompraException
                    || "MensajeUsuarioException".equals(
                    actual.getClass().getSimpleName()
            )) {

                return true;
            }

            actual = actual.getCause();
        }

        return false;
    }

    private void errorCampo(String campo, String mensaje)
            throws ValidacionCompraException {

        throw new ValidacionCompraException(campo, mensaje);
    }

    private String getParametroTrim(ActionRequest request, String nombre) {
        String value = getParametroRaw(request, nombre, null);
        return value != null ? value.trim() : "";
    }

    private boolean parseSurgeObligatorio(
            ActionRequest request)
            throws ValidacionCompraException {

        String value =
                getParametroTrim(
                        request,
                        "surge"
                );

        if (!"0".equals(value)
                && !"1".equals(value)) {

            errorCampo(
                    "surge",
                    "Surge: debe seleccionar Sí o No."
            );
        }

        return "1".equals(value);
    }

    private boolean getParametroBoolean(
            ActionRequest request,
            String nombre) {

        String value = getParametroTrim(request, nombre);

        return "true".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value)
                || "1".equals(value)
                || "si".equalsIgnoreCase(value)
                || "s".equalsIgnoreCase(value);
    }

    private String getParametroRaw(
            ActionRequest request,
            String nombre,
            String defaultValue) {

        if (request == null || nombre == null) {
            return defaultValue;
        }

        String value = request.getParameter(nombre);

        if (value != null) {
            return value;
        }

        try {
            value = ParamUtil.getString(request, nombre, null);

            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            // Sigue fallback manual.
        }

        Map parameterMap = request.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return defaultValue;
        }

        String bestKey = null;
        Iterator it = parameterMap.keySet().iterator();

        while (it.hasNext()) {
            Object keyObj = it.next();

            if (keyObj == null) {
                continue;
            }

            String key = String.valueOf(keyObj);

            if (key.equals(nombre)
                    || key.endsWith("_" + nombre)
                    || key.endsWith(nombre)) {

                if (bestKey == null || key.length() < bestKey.length()) {
                    bestKey = key;
                }
            }
        }

        if (bestKey == null) {
            return defaultValue;
        }

        Object raw = parameterMap.get(bestKey);

        if (raw == null) {
            return defaultValue;
        }

        if (raw instanceof String[]) {
            String[] values = (String[]) raw;
            return values.length > 0 ? values[0] : defaultValue;
        }

        return String.valueOf(raw);
    }

    private Integer parseEnteroOpcional(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' no es un número entero válido."
            );
        }

        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' está fuera del rango permitido."
            );
        }

        return null;
    }

    private int parseEnteroConDefault(
            ActionRequest request,
            String nombre,
            String label,
            int defaultValue) throws ValidacionCompraException {

        Integer parsed = parseEnteroOpcional(request, nombre, label);
        return parsed != null ? parsed.intValue() : defaultValue;
    }

    private Integer parsePorcentajeDesdeRequest(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            value = "0";
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un número entero entre 0 y 100. "
                            + "Valor recibido: '" + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' está fuera del rango permitido."
            );
            return null;
        }

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    nombre,
                    label + ": debe estar entre 0 y 100. Valor recibido: "
                            + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

}