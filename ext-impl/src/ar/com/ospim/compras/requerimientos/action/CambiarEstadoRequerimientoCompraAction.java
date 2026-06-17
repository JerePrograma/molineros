package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.util.Map;

public class CambiarEstadoRequerimientoCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    CambiarEstadoRequerimientoCompraAction.class
            );

    private static final String
            STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse)
            throws Exception {

        int idRequerimientoCompra =
                getIntegerParam(
                        actionRequest,
                        "id_requerimiento_compra",
                        0
                );

        int estadoNuevo =
                getIntegerParam(
                        actionRequest,
                        "estado_nuevo",
                        0
                );

        boolean reintentarNotificaciones =
                getBooleanParam(
                        actionRequest,
                        "reintentar_notificaciones",
                        false
                );

        try {
            User user =
                    PortalUtil.getUser(actionRequest);

            String usuario =
                    user != null
                            ? user.getScreenName()
                            : "sistema";

            if (reintentarNotificaciones) {
                procesarReintentoCotizaciones(
                        actionRequest,
                        idRequerimientoCompra,
                        usuario,
                        user
                );

            } else {
                procesarCambioEstado(
                        actionRequest,
                        idRequerimientoCompra,
                        estadoNuevo,
                        usuario,
                        user
                );
            }

        } catch (Exception e) {
            _log.error(
                    "Error procesando estado/cotizaciones "
                            + "del requerimiento. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", estadoNuevo="
                            + estadoNuevo
                            + ", reintentar="
                            + reintentarNotificaciones,
                    e
            );

            SessionErrors.add(
                    actionRequest,
                    "estado-requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    getMensajeError(e)
            );
        }

        prepararRenderVerRequerimiento(
                actionResponse,
                idRequerimientoCompra
        );

        setForward(
                actionRequest,
                WebKeysCompras
                        .FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse)
            throws Exception {

        return mapping.findForward(
                WebKeysCompras
                        .FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private void procesarCambioEstado(
            ActionRequest actionRequest,
            int idRequerimientoCompra,
            int estadoNuevo,
            String usuario,
            User user) throws Exception {

        validarParametrosCambioEstado(
                idRequerimientoCompra,
                estadoNuevo
        );

        RequerimientoCompra requerimiento =
                validarTransicionEstado(
                        idRequerimientoCompra,
                        estadoNuevo
                );

        validarPermisoCambioEstado(
                user,
                requerimiento.getEstado(),
                estadoNuevo
        );

        EditarRequerimientoCompraServiceUtil
                .cambiarEstado(
                        idRequerimientoCompra,
                        estadoNuevo,
                        usuario
                );

        /*
         * La notificacion se realiza despues de que el requerimiento
         * haya quedado efectivamente en estado Cotizaciones.
         */
        if (estadoNuevo
                == WebKeysCompras.ESTADO_COTIZACIONES) {

            notificarPrestadoresCotizacion(
                    actionRequest,
                    idRequerimientoCompra,
                    usuario,
                    user.getCompanyId()
            );
        }

        SessionMessages.add(
                actionRequest,
                "estado-requerimiento-compra-actualizado"
        );
    }

    private void procesarReintentoCotizaciones(
            ActionRequest actionRequest,
            int idRequerimientoCompra,
            String usuario,
            User user) throws Exception {

        validarReintentoCotizaciones(
                idRequerimientoCompra,
                user
        );

        notificarPrestadoresCotizacion(
                actionRequest,
                idRequerimientoCompra,
                usuario,
                user.getCompanyId()
        );
    }

    private void notificarPrestadoresCotizacion(
            ActionRequest actionRequest,
            int idRequerimientoCompra,
            String usuario,
            long companyId) {

        try {
            NotificacionCotizacionResultado resultado =
                    NotificarCotizacionPrestadorServiceUtil
                            .notificarPrestadores(
                                    idRequerimientoCompra,
                                    usuario,
                                    companyId
                            );

            if (resultado == null) {
                _log.warn(
                        "La notificacion de cotizaciones "
                                + "no devolvio resultado. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                );

                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-sin-resultado"
                );

                return;
            }

            actionRequest.setAttribute(
                    WebKeysCompras
                            .RESULTADO_NOTIFICACION_COTIZACIONES,
                    resultado
            );

            if (resultado.getTotalCandidatos() == 0) {
                if (_log.isInfoEnabled()) {
                    _log.info(
                            "No hay prestadores pendientes "
                                    + "de notificacion. "
                                    + "idRequerimiento="
                                    + idRequerimientoCompra
                    );
                }

                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-sin-destinatarios"
                );

                return;
            }

            if (resultado.getErrores() > 0) {
                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-notificados-con-errores"
                );

            } else {
                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-notificados"
                );
            }

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Notificacion de cotizaciones finalizada. "
                                + "idRequerimiento="
                                + idRequerimientoCompra
                                + ", candidatos="
                                + resultado.getTotalCandidatos()
                                + ", enviados="
                                + resultado.getEnviados()
                                + ", errores="
                                + resultado.getErrores()
                                + ", omitidos="
                                + resultado.getOmitidos()
                );
            }

        } catch (Exception e) {
            /*
             * No relanzar.
             *
             * El cambio de estado ya pudo haberse realizado correctamente.
             * La falla de correo, consulta o auditoria no debe presentarse
             * como una falla del cambio de estado ni revertirlo.
             */
            _log.error(
                    "Fallo la notificacion de prestadores. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            SessionMessages.add(
                    actionRequest,
                    "cotizaciones-prestadores-error"
            );
        }
    }

    private void validarParametrosCambioEstado(
            int idRequerimientoCompra,
            int estadoNuevo) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (!WebKeysCompras.esEstadoValido(
                estadoNuevo
        )) {
            throw new Exception(
                    "Estado de requerimiento invalido."
            );
        }
    }

    private RequerimientoCompra validarTransicionEstado(
            int idRequerimientoCompra,
            int estadoNuevo) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontro el requerimiento informado."
            );
        }

        if (requerimiento.isAnulado()) {
            throw new Exception(
                    "El requerimiento ya se encuentra anulado."
            );
        }

        if (!WebKeysCompras.validarTransicionEstado(
                requerimiento.getEstado(),
                estadoNuevo
        )) {
            throw new Exception(
                    "La transicion de estado solicitada "
                            + "no es valida."
            );
        }

        return requerimiento;
    }

    private void validarReintentoCotizaciones(
            int idRequerimientoCompra,
            User user) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontro el requerimiento informado."
            );
        }

        if (requerimiento.getEstado()
                != WebKeysCompras.ESTADO_COTIZACIONES) {

            throw new Exception(
                    "Solo se pueden notificar prestadores "
                            + "pendientes de requerimientos "
                            + "en estado Cotizaciones."
            );
        }

        validarPermisoReintentoCotizaciones(user);
    }

    private void validarPermisoCambioEstado(
            User user,
            int estadoActual,
            int estadoNuevo) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        if (estadoNuevo
                == WebKeysCompras.ESTADO_ANULADO
                && WebKeysCompras.puedeAnular(estadoActual)) {

            if (!userTieneRol(
                    user,
                    WebKeysCompras.ROL_ANULAR_COMPRAS
            )) {
                throw new Exception(
                        "No posee permisos para anular "
                                + "requerimientos de compras."
                );
            }

            return;
        }

        if (estadoNuevo
                == WebKeysCompras.ESTADO_REQUERIMIENTO
                && WebKeysCompras.puedeEnviarAAutorizar(estadoActual)) {

            validarRol(
                    user,
                    WebKeysCompras.ROL_ABM_COMPRAS,
                    "No posee permisos para enviar "
                            + "requerimientos a autorizacion."
            );

            return;
        }

        if (estadoNuevo
                == WebKeysCompras.ESTADO_AUTORIZADO
                && WebKeysCompras.puedeAutorizar(estadoActual)) {

            validarRol(
                    user,
                    WebKeysCompras.ROL_AUTORIZAR_COMPRAS,
                    "No posee permisos para autorizar "
                            + "requerimientos de compra."
            );

            return;
        }

        if (estadoNuevo
                == WebKeysCompras.ESTADO_COTIZACIONES
                && WebKeysCompras.puedeIniciarCotizaciones(estadoActual)) {

            validarRol(
                    user,
                    WebKeysCompras.ROL_COTIZAR_COMPRAS,
                    "No posee permisos para iniciar "
                            + "cotizaciones."
            );

            return;
        }

        if (estadoNuevo
                == WebKeysCompras.ESTADO_ORDEN_COMPRA
                && WebKeysCompras.puedeGenerarOrdenCompra(estadoActual)) {

            validarRol(
                    user,
                    WebKeysCompras
                            .ROL_ORDEN_COMPRA_COMPRAS,
                    "No posee permisos para generar "
                            + "orden de compra."
            );

            return;
        }

        throw new Exception(
                "No posee permisos para cambiar "
                        + "el estado solicitado."
        );
    }

    private void validarPermisoReintentoCotizaciones(User user)
            throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        validarRol(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS,
                "No posee permisos para notificar "
                        + "prestadores pendientes."
        );
    }

    private void validarRol(User user,
                            String rol,
                            String mensaje) throws Exception {

        if (!userTieneRol(user, rol)) {
            throw new Exception(mensaje);
        }
    }

    private boolean userTieneRol(User user,
                                 String rol) throws Exception {

        return user != null
                && rol != null
                && PermissionUtil.userContainsRole(
                user,
                rol
        );
    }

    private void prepararRenderVerRequerimiento(
            ActionResponse actionResponse,
            int idRequerimientoCompra) {

        actionResponse.setRenderParameter(
                "struts_action",
                STRUTS_ACTION_VER_REQUERIMIENTO
        );

        if (idRequerimientoCompra > 0) {
            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(
                            idRequerimientoCompra
                    )
            );
        }
    }

    private int getIntegerParam(
            ActionRequest actionRequest,
            String paramName,
            int defaultValue) {

        int value =
                ParamUtil.getInteger(
                        actionRequest,
                        paramName,
                        defaultValue
                );

        if (value != defaultValue) {
            return value;
        }

        String rawValue =
                getNamespacedParam(
                        actionRequest,
                        paramName
                );

        if (rawValue == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(
                    rawValue.trim()
            );

        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private boolean getBooleanParam(
            ActionRequest actionRequest,
            String paramName,
            boolean defaultValue) {

        String directValue =
                actionRequest.getParameter(
                        paramName
                );

        if (directValue != null) {
            return parseBoolean(
                    directValue,
                    defaultValue
            );
        }

        String rawValue =
                getNamespacedParam(
                        actionRequest,
                        paramName
                );

        return parseBoolean(
                rawValue,
                defaultValue
        );
    }

    private String getNamespacedParam(
            ActionRequest actionRequest,
            String paramName) {

        Map parameterMap =
                actionRequest.getParameterMap();

        if (parameterMap == null
                || parameterMap.isEmpty()) {

            return null;
        }

        for (Object entryObject :
                parameterMap.entrySet()) {

            Map.Entry entry =
                    (Map.Entry) entryObject;

            Object keyObject =
                    entry.getKey();

            if (keyObject == null) {
                continue;
            }

            String key =
                    String.valueOf(keyObject);

            if (!key.endsWith(paramName)) {
                continue;
            }

            Object valueObject =
                    entry.getValue();

            if (!(valueObject instanceof String[])) {
                continue;
            }

            String[] values =
                    (String[]) valueObject;

            if (values.length == 0
                    || values[0] == null) {

                return null;
            }

            return values[0];
        }

        return null;
    }

    private boolean parseBoolean(
            String value,
            boolean defaultValue) {

        if (value == null) {
            return defaultValue;
        }

        String normalized =
                value.trim();

        if ("true".equalsIgnoreCase(normalized)
                || "1".equals(normalized)
                || "si".equalsIgnoreCase(normalized)
                || "yes".equalsIgnoreCase(normalized)) {

            return true;
        }

        if ("false".equalsIgnoreCase(normalized)
                || "0".equals(normalized)
                || "no".equalsIgnoreCase(normalized)) {

            return false;
        }

        return defaultValue;
    }

    private String getMensajeError(Exception e) {
        if (e == null
                || e.getMessage() == null
                || e.getMessage().trim().length() == 0) {

            return "Ocurrio un error procesando "
                    + "el requerimiento de compra.";
        }

        return e.getMessage().trim();
    }
}
