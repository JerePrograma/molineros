package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
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

public class EditarRequerimientoCompraDetalleAction extends PortletAction {

    private static final String CMD_ADD_ITEMS =
            "addItems";

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String cmd =
                detalleHelper.getParametroTrim(
                        actionRequest,
                        Constants.CMD
                );

        int idRequerimientoCompra =
                0;

        actionResponse.setRenderParameter("compras_guardado", "false");
        actionResponse.setRenderParameter("compras_error", "false");

        try {
            idRequerimientoCompra =
                    detalleHelper.getIdRequerimientoCompraFromRequest(
                            actionRequest
                    );
        } catch (Exception e) {
            idRequerimientoCompra =
                    0;
        }

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            if ("corregirItem".equals(cmd)) {
                detalleHelper.validarPermisoCorregirItem(user);
            } else {
                detalleHelper.validarPermisoABM(user);
            }

            String usuario =
                    detalleHelper.getUsuario(
                            user
                    );

            if ("corregirItem".equals(cmd)) {
                detalleHelper.corregirItemDesdeRequest(actionRequest, usuario);
                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra));
                actionResponse.setRenderParameter("struts_action", "/compras/ver_requerimiento");
                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
                return;
            }

            if ("addItem".equals(cmd)
                    || "updateItem".equals(cmd)) {

                detalleHelper.guardarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );
                actionResponse.setRenderParameter("compras_guardado", "true");

            } else if (CMD_ADD_ITEMS.equals(cmd)) {

                int cantidadGuardada =
                        detalleHelper.guardarDetallesDesdeRequest(
                                actionRequest,
                                idRequerimientoCompra,
                                usuario
                        );

                detalleHelper.setRenderEdicion(
                        actionResponse,
                        idRequerimientoCompra
                );

                if (cantidadGuardada > 0) {
                    actionResponse.setRenderParameter("compras_guardado", "true");
                    /*
                     * Se reutiliza el mensaje existente de detalle guardado
                     * para no ampliar el contrato de mensajes del módulo.
                     */
                    SessionMessages.add(
                            actionRequest,
                            "requerimiento-compra-item-guardado"
                    );
                }

            } else if ("deleteItem".equals(cmd)) {

                detalleHelper.borrarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );

                actionResponse.setRenderParameter("compras_guardado", "true");
                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-item-borrado"
                );

            } else {

                detalleHelper.setRenderEdicion(
                        actionResponse,
                        idRequerimientoCompra
                );
            }

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );

        } catch (Exception e) {
            actionResponse.setRenderParameter("compras_guardado", "false");

            String mensaje =
                    e.getMessage();

            if (WebKeysCompras.isEmpty(
                    mensaje
            )) {

                mensaje =
                        "No se pudo procesar el detalle "
                                + "del requerimiento de compra.";
            }

            SessionErrors.add(
                    actionRequest,
                    "requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            if (e instanceof ValidacionCompraException) {

                ValidacionCompraException validacion =
                        (ValidacionCompraException) e;

                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            detalleHelper.setRenderEdicion(
                    actionResponse,
                    idRequerimientoCompra
            );
            if ("corregirItem".equals(cmd)) {
                actionResponse.setRenderParameter("struts_action", "/compras/ver_requerimiento");
            }

            actionResponse.setRenderParameter(
                    "compras_error",
                    "true"
            );

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null
                            ? cmd
                            : ""
            );

            setForward(
                    actionRequest,
                    "corregirItem".equals(cmd)
                            ? WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
                            : WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        try {
            User user =
                    PortalUtil.getUser(
                            renderRequest
                    );

            detalleHelper.validarPermisoABM(
                    user
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );

        } catch (Exception e) {

            String mensaje =
                    e.getMessage();

            if (WebKeysCompras.isEmpty(
                    mensaje
            )) {

                mensaje =
                        "No posee permisos para administrar "
                                + "detalles de compras.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ERROR
            );
        }
    }
}