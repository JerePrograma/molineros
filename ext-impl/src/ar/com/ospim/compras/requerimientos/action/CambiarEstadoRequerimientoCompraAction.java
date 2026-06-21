package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
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
                ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        int estadoNuevo =
                ParamUtil.getInteger(actionRequest, "estado_nuevo", 0);

        boolean reintentarNotificaciones =
                ParamUtil.getBoolean(actionRequest, "reintentar_notificaciones", false);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = user != null ? user.getScreenName() : "sistema";

            if (reintentarNotificaciones) {
                validarRolCotizar(user);

                NotificacionCotizacionResultado resultado =
                        EditarRequerimientoCompraServiceUtil
                                .reintentarNotificacionesCotizacion(
                                        idRequerimientoCompra,
                                        usuario,
                                        user.getCompanyId()
                                );

                registrarResultado(actionRequest, resultado, false);
            } else if (estadoNuevo == WebKeysCompras.ESTADO_A_COTIZAR) {
                validarRolCotizar(user);

                NotificacionCotizacionResultado resultado =
                        EditarRequerimientoCompraServiceUtil.enviarACotizar(
                                idRequerimientoCompra,
                                usuario,
                                user.getCompanyId()
                );

                registrarResultado(actionRequest, resultado, true);
            } else if (estadoNuevo == WebKeysCompras.ESTADO_ANULADO) {
                validarRolAnular(user);

                EditarRequerimientoCompraServiceUtil.cambiarEstado(
                        idRequerimientoCompra,
                        WebKeysCompras.ESTADO_ANULADO,
                        usuario
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-anulado");
            } else {
                throw new Exception("La transición de estado solicitada no es válida.");
            }
        } catch (Exception e) {
            _log.error(
                    "Error procesando estado del requerimiento de compra. id="
                            + idRequerimientoCompra
                            + ", estadoNuevo="
                            + estadoNuevo,
                    e
            );

            SessionErrors.add(actionRequest, "estado-requerimiento-compra-error");
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensajeError(e));
        }

        actionResponse.setRenderParameter("struts_action", STRUTS_ACTION_VER_REQUERIMIENTO);

        if (idRequerimientoCompra > 0) {
            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(idRequerimientoCompra)
            );
        }

        setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
    }

    private void registrarResultado(
            ActionRequest actionRequest,
            NotificacionCotizacionResultado resultado,
            boolean cambiaAAcotizar) {

        actionRequest.setAttribute(
                WebKeysCompras.RESULTADO_NOTIFICACION_COTIZACION,
                resultado
        );

        if (resultado == null) {
            SessionMessages.add(actionRequest, "cotizacion-prestadores-sin-resultado");
            return;
        }

        if (resultado.getEnviados() <= 0) {
            SessionMessages.add(
                    actionRequest,
                    "cotizacion-prestadores-no-enviados"
            );
        } else if (resultado.tieneErrores()) {
            SessionMessages.add(
                    actionRequest,
                    cambiaAAcotizar
                            ? "requerimiento-compra-enviado-a-cotizar-con-errores"
                            : "cotizacion-prestadores-notificados-con-errores"
            );
        } else if (cambiaAAcotizar) {
            SessionMessages.add(
                    actionRequest,
                    "requerimiento-compra-enviado-a-cotizar"
            );
        } else {
            SessionMessages.add(actionRequest, "cotizacion-prestadores-notificados");
        }
    }

    private void validarRolCotizar(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
            throw new Exception("No posee permisos para cotizar requerimientos de compras.");
        }
    }

    private void validarRolAnular(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS)) {
            throw new Exception("No posee permisos para anular requerimientos de compras.");
        }
    }

    private String mensajeError(Exception e) {
        if (e == null || WebKeysCompras.isEmpty(e.getMessage())) {
            return "No se pudo procesar el requerimiento de compra.";
        }

        return e.getMessage();
    }
}
