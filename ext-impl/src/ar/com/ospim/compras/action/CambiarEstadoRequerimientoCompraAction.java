package ar.com.ospim.compras.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class CambiarEstadoRequerimientoCompraAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(CambiarEstadoRequerimientoCompraAction.class);

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra = ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);
        int estadoNuevo = ParamUtil.getInteger(actionRequest, "estado_nuevo", 0);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = user != null ? user.getScreenName() : "sistema";

            validarParametrosCambioEstado(idRequerimientoCompra, estadoNuevo);
            validarPermisoCambioEstado(user, estadoNuevo);
            validarTransicionEstado(idRequerimientoCompra, estadoNuevo);

            EditarRequerimientoCompraServiceUtil.cambiarEstado(
                    idRequerimientoCompra,
                    estadoNuevo,
                    usuario
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                    Integer.valueOf(idRequerimientoCompra)
            );

            actionResponse.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));

            SessionMessages.add(actionRequest, "estado-requerimiento-compra-actualizado");
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
    }

    private void validarParametrosCambioEstado(int idRequerimientoCompra, int estadoNuevo) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (!WebKeysCompras.esEstadoValido(estadoNuevo)) {
            throw new Exception("Estado de requerimiento invalido.");
        }
    }

    private void validarTransicionEstado(int idRequerimientoCompra, int estadoNuevo) throws Exception {
        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw new Exception("No se encontro el requerimiento de compra informado.");
        }

        if (requerimiento.isAnulado()) {
            throw new Exception("El requerimiento ya se encuentra anulado.");
        }

        if (!WebKeysCompras.puedeCambiarEstado(requerimiento.getIdEstado(), estadoNuevo)) {
            throw new Exception("La transicion de estado solicitada no es valida.");
        }
    }

    private void validarPermisoCambioEstado(User user, int estadoNuevo) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (estadoNuevo == WebKeysCompras.ESTADO_ANULADO) {
            if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS)
                    && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
                throw new Exception("No posee permisos para anular requerimientos de compras.");
            }

            return;
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para modificar el requerimiento de compra.");
        }
    }
}