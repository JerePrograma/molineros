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

public class CambiarEstadoRequerimientoCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(CambiarEstadoRequerimientoCompraAction.class);

    private static final String STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra =
                getIntegerParam(actionRequest, "id_requerimiento_compra", 0);

        int estadoNuevo =
                getIntegerParam(actionRequest, "estado_nuevo", 0);

        boolean reintentarNotificaciones =
                getBooleanParam(
                        actionRequest,
                        "reintentar_notificaciones",
                        false
                );

        try {
            if (_log.isInfoEnabled()) {
                _log.info(
                        "Cambiar estado requerimiento compra. id="
                                + idRequerimientoCompra
                                + ", estadoNuevo="
                                + estadoNuevo
                                + ", reintentarNotificaciones="
                                + reintentarNotificaciones
                );
            }

            User user = PortalUtil.getUser(actionRequest);
            String usuario = user != null ? user.getScreenName() : "sistema";

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

            prepararRenderVerRequerimiento(
                    actionResponse,
                    idRequerimientoCompra
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );

        } catch (Exception e) {
            _log.error("Error al cambiar estado de requerimiento de compra", e);

            SessionErrors.add(
                    actionRequest,
                    "estado-requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    e.getMessage()
            );

            prepararRenderVerRequerimiento(
                    actionResponse,
                    idRequerimientoCompra
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );
        }
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private void procesarCambioEstado(ActionRequest actionRequest,
                                      int idRequerimientoCompra,
                                      int estadoNuevo,
                                      String usuario,
                                      User user) throws Exception {

        validarParametrosCambioEstado(idRequerimientoCompra, estadoNuevo);
        validarTransicionEstado(idRequerimientoCompra, estadoNuevo);
        validarPermisoCambioEstado(user, estadoNuevo);

        long companyId = user.getCompanyId();

        EditarRequerimientoCompraServiceUtil.cambiarEstado(
                idRequerimientoCompra,
                estadoNuevo,
                usuario
        );

        if (estadoNuevo == WebKeysCompras.ESTADO_COTIZACIONES) {
            notificarPrestadoresCotizacion(
                    actionRequest,
                    idRequerimientoCompra,
                    usuario,
                    companyId
            );
        }

        SessionMessages.add(
                actionRequest,
                "estado-requerimiento-compra-actualizado"
        );
    }

    private void procesarReintentoCotizaciones(ActionRequest actionRequest,
                                               int idRequerimientoCompra,
                                               String usuario,
                                               User user) throws Exception {

        validarReintentoCotizaciones(idRequerimientoCompra, user);

        notificarPrestadoresCotizacion(
                actionRequest,
                idRequerimientoCompra,
                usuario,
                user.getCompanyId()
        );
    }

    private void notificarPrestadoresCotizacion(ActionRequest actionRequest,
                                                int idRequerimientoCompra,
                                                String usuario,
                                                long companyId) {

        try {
            NotificacionCotizacionResultado resultado =
                    NotificarCotizacionPrestadorServiceUtil.notificarPrestadores(
                            idRequerimientoCompra,
                            usuario,
                            companyId
                    );

            if (resultado == null) {
                _log.warn(
                        "La notificacion de cotizaciones no devolvio resultado. id="
                                + idRequerimientoCompra
                );

                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-sin-resultado"
                );

                return;
            }

            if (resultado.getTotalCandidatos() == 0) {
                if (_log.isInfoEnabled()) {
                    _log.info(
                            "No hay prestadores pendientes de notificacion. id="
                                    + idRequerimientoCompra
                    );
                }

                SessionMessages.add(
                        actionRequest,
                        "cotizaciones-prestadores-sin-destinatarios"
                );

                return;
            }

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Notificacion de cotizaciones finalizada. id="
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

            actionRequest.setAttribute(
                    "RESULTADO_NOTIFICACION_COTIZACIONES",
                    resultado
            );

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

        } catch (Exception e) {
            /*
             * No relanzar.
             *
             * El cambio de estado, cuando corresponde, ya fue exitoso. Si falla
             * la busqueda de prestadores, la auditoria o el servicio de mail, el
             * requerimiento no debe volver automaticamente al estado anterior.
             */
            _log.error(
                    "Fallo la notificacion de prestadores para cotizacion. id="
                            + idRequerimientoCompra,
                    e
            );

            SessionMessages.add(
                    actionRequest,
                    "cotizaciones-prestadores-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    "No se pudieron notificar prestadores: "
                            + e.getMessage()
            );
        }
    }

    private void prepararRenderVerRequerimiento(ActionResponse actionResponse,
                                                int idRequerimientoCompra) {

        actionResponse.setRenderParameter(
                "struts_action",
                STRUTS_ACTION_VER_REQUERIMIENTO
        );

        if (idRequerimientoCompra > 0) {
            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(idRequerimientoCompra)
            );
        }
    }

    private void validarParametrosCambioEstado(int idRequerimientoCompra,
                                               int estadoNuevo) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (!WebKeysCompras.esEstadoValido(estadoNuevo)) {
            throw new Exception("Estado de requerimiento invalido.");
        }
    }

    private void validarTransicionEstado(int idRequerimientoCompra,
                                         int estadoNuevo) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontro el requerimiento de compra informado."
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
                    "La transicion de estado solicitada no es valida."
            );
        }
    }

    private void validarReintentoCotizaciones(int idRequerimientoCompra,
                                              User user) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        validarPermisoCambioEstado(
                user,
                WebKeysCompras.ESTADO_COTIZACIONES
        );

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontro el requerimiento de compra informado."
            );
        }

        if (requerimiento.getEstado() != WebKeysCompras.ESTADO_COTIZACIONES) {
            throw new Exception(
                    "Solo se pueden reintentar notificaciones de requerimientos en Cotizaciones."
            );
        }
    }

    private void validarPermisoCambioEstado(User user,
                                            int estadoNuevo) throws Exception {

        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_ANULADO) {
            if (!userTieneRol(user, WebKeysCompras.ROL_ANULAR_COMPRAS)
                    && !userTieneRol(user, WebKeysCompras.ROL_ABM_COMPRAS)) {

                throw new Exception(
                        "No posee permisos para anular requerimientos de compras."
                );
            }

            return;
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_REQUERIMIENTO) {
            if (!userTieneRol(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
                throw new Exception(
                        "No posee permisos para enviar requerimientos a autorizacion."
                );
            }

            return;
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_AUTORIZADO) {
            if (!userTieneRol(user, WebKeysCompras.ROL_AUTORIZAR_COMPRAS)) {
                throw new Exception(
                        "No posee permisos para autorizar requerimientos de compra."
                );
            }

            return;
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_COTIZACIONES) {
            if (!userTieneRol(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
                throw new Exception(
                        "No posee permisos para iniciar cotizaciones de requerimientos de compra."
                );
            }

            return;
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_ORDEN_COMPRA) {
            if (!userTieneRol(user, WebKeysCompras.ROL_ORDEN_COMPRA_COMPRAS)) {
                throw new Exception(
                        "No posee permisos para generar orden de compra."
                );
            }

            return;
        }

        throw new Exception("No posee permisos para cambiar el estado solicitado.");
    }

    private boolean userTieneRol(User user, String rol) throws Exception {
        return user != null
                && rol != null
                && PermissionUtil.userContainsRole(user, rol);
    }

    private int getIntegerParam(ActionRequest actionRequest,
                                String paramName,
                                int defaultValue) {

        int value = ParamUtil.getInteger(
                actionRequest,
                paramName,
                defaultValue
        );

        if (value != defaultValue) {
            return value;
        }

        String rawValue = getNamespacedParam(actionRequest, paramName);

        if (rawValue == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private boolean getBooleanParam(ActionRequest actionRequest,
                                    String paramName,
                                    boolean defaultValue) {

        String directValue = actionRequest.getParameter(paramName);

        if (directValue != null) {
            return parseBoolean(directValue, defaultValue);
        }

        String rawValue = getNamespacedParam(actionRequest, paramName);

        return parseBoolean(rawValue, defaultValue);
    }

    private String getNamespacedParam(ActionRequest actionRequest,
                                      String paramName) {

        Map parameterMap = actionRequest.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return null;
        }

        for (Object entryObject : parameterMap.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObject;

            Object keyObject = entry.getKey();

            if (keyObject == null) {
                continue;
            }

            String key = String.valueOf(keyObject);

            if (!key.endsWith(paramName)) {
                continue;
            }

            Object valueObject = entry.getValue();

            if (!(valueObject instanceof String[])) {
                continue;
            }

            String[] values = (String[]) valueObject;

            if (values.length == 0 || values[0] == null) {
                return null;
            }

            return values[0];
        }

        return null;
    }

    private boolean parseBoolean(String value,
                                 boolean defaultValue) {

        if (value == null) {
            return defaultValue;
        }

        String normalized = value.trim();

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
}
