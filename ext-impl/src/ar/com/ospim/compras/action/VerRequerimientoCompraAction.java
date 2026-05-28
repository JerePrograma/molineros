package ar.com.ospim.compras.action;

import java.math.BigDecimal;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class VerRequerimientoCompraAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(VerRequerimientoCompraAction.class);

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra = ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        try {
            User user = PortalUtil.getUser(actionRequest);
            validarPermisoView(user);

            if (idRequerimientoCompra <= 0) {
                throw new Exception("Debe informar el requerimiento de compra.");
            }

            actionResponse.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);
            validarPermisoView(user);

            int idRequerimientoCompra = ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            if (idRequerimientoCompra <= 0) {
                throw new Exception("Debe informar el requerimiento de compra.");
            }

            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);

            if (requerimiento == null) {
                throw new Exception("No se encontro el requerimiento de compra informado.");
            }

            renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW, requerimiento);
            renderRequest.setAttribute(WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW, requerimiento.getDetalles());
            renderRequest.setAttribute(WebKeysCompras.SOLO_LECTURA_ATTR, Boolean.TRUE);
            cargarAfiliadoRequerimiento(renderRequest, requerimiento);
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
    }

    private void validarPermisoView(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para consultar requerimientos de compras.");
        }
    }

    private void cargarAfiliadoRequerimiento(RenderRequest renderRequest, RequerimientoCompra requerimiento) {
        renderRequest.removeAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);

        if (requerimiento == null || !requerimiento.tieneAfiliadoInformado()) {
            return;
        }

        try {
            List<Afiliado> afiliados = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
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
                renderRequest.setAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA, afiliados.get(0));
            }
        } catch (Exception e) {
        }
    }
}
