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
import ar.com.ospim.compras.service.AprobacionRequerimientoCompraServiceUtil;

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
        String comentario = ParamUtil.getString(actionRequest, "comentario", null);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = user != null ? user.getScreenName() : "sistema";

            AprobacionRequerimientoCompraServiceUtil.cambiarEstado(idRequerimientoCompra, estadoNuevo, comentario, usuario);

            actionRequest.setAttribute(WebKeysCompras.ID_COMPRA_EN_EDICION, Integer.valueOf(idRequerimientoCompra));
            SessionMessages.add(actionRequest, "estado-requerimiento-compra-actualizado");
            setForward(actionRequest, "portlet.compras.ver_requerimiento");
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, "portlet.compras.ver_requerimiento");
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        return mapping.findForward("portlet.compras.ver_requerimiento");
    }
}
