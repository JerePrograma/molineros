package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
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
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class ImprimirRequerimientoCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(ImprimirRequerimientoCompraAction.class);

    private static final String ATTR_ID_REQUERIMIENTO_COMPRA_PDF =
            "ID_REQUERIMIENTO_COMPRA_PDF";

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        try {
            validarYPrepararImpresion(actionRequest, idRequerimientoCompra);

            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(idRequerimientoCompra)
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_IMPRIMIR_REQUERIMIENTO
            );
        } catch (Exception e) {
            _log.error(
                    "Error preparando impresión del requerimiento de compra. id="
                            + idRequerimientoCompra,
                    e
            );

            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

        try {
            validarYPrepararImpresion(renderRequest, idRequerimientoCompra);

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_IMPRIMIR_REQUERIMIENTO
            );
        } catch (Exception e) {
            _log.error(
                    "Error renderizando impresión del requerimiento de compra. id="
                            + idRequerimientoCompra,
                    e
            );

            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());

            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }
    }

    private void validarYPrepararImpresion(PortletRequest request,
                                           int idRequerimientoCompra) throws Exception {

        User user = PortalUtil.getUser(request);

        validarPermisoImpresion(user);
        validarIdRequerimiento(idRequerimientoCompra);

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            throw new Exception("No se encontró el requerimiento de compra informado.");
        }

        if (requerimiento.getDetalles() == null) {
            requerimiento.setDetalles(
                    BusquedaRequerimientoCompraServiceUtil.getDetalles(
                            idRequerimientoCompra
                    )
            );
        }

        request.setAttribute(
                WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW,
                requerimiento
        );
        request.setAttribute(
                WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW,
                requerimiento.getDetalles()
        );
        request.setAttribute(
                ATTR_ID_REQUERIMIENTO_COMPRA_PDF,
                String.valueOf(idRequerimientoCompra)
        );
    }

    private void validarIdRequerimiento(int idRequerimientoCompra) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar un requerimiento de compra válido para imprimir.");
        }
    }

    private void validarPermisoImpresion(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
            throw new Exception("No posee permisos para imprimir requerimientos de compras.");
        }
    }
}
