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

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = detalleHelper.getParametroTrim(actionRequest, Constants.CMD);
        int idRequerimientoCompra = 0;

        try {
            idRequerimientoCompra =
                    detalleHelper.getIdRequerimientoCompraFromRequest(actionRequest);
        } catch (Exception e) {
            idRequerimientoCompra = 0;
        }

        try {
            User user = PortalUtil.getUser(actionRequest);
            detalleHelper.validarPermisoABM(user);
            String usuario = detalleHelper.getUsuario(user);

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                detalleHelper.guardarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );
            } else if ("deleteItem".equals(cmd)) {
                detalleHelper.borrarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );

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
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el detalle del requerimiento de compra.";
            }

            SessionErrors.add(
                    actionRequest,
                    "requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            if (e instanceof RequerimientoCompraDetalleHelper.ValidacionCompraException) {
                RequerimientoCompraDetalleHelper.ValidacionCompraException validacion =
                        (RequerimientoCompraDetalleHelper.ValidacionCompraException) e;

                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            detalleHelper.setRenderEdicion(
                    actionResponse,
                    idRequerimientoCompra
            );

            actionResponse.setRenderParameter("compras_error", "true");
            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null ? cmd : ""
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        }
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);
            detalleHelper.validarPermisoABM(user);

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No posee permisos para administrar detalles de compras.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }
    }
}
