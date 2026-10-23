package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** Emision directa y consulta de la OC, siguiendo el guardado e impresion de Liquidaciones. */
public class RequerimientoCompraOrdenAction extends PortletAction {

    private static final Log _log = LogFactoryUtil.getLog(RequerimientoCompraOrdenAction.class);
    private static final String RUTA = "/compras/orden_compra";
    private static final String FORWARD = "portlet.compras.orden_compra";
    private final RequerimientoCompraOrdenHelper helper = new RequerimientoCompraOrdenHelper();

    public static boolean puedeConsultar(User user) throws Exception {
        return user != null && (PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_AUTORIZAR_COMPRAS));
    }

    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest request, ActionResponse response) throws Exception {
        int id = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
        try {
            User user = PortalUtil.getUser(request);
            if (user == null || !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
                throw new IllegalArgumentException("No posee permisos para crear órdenes de compra.");
            }
            RequerimientoCompraOrden orden = helper.preparar(id, user.getFullName());
            if (orden.getIdOrdenCompra() == 0) {
                RequerimientoCompraGuardadoToken.consumir(request,
                        ParamUtil.getString(request, RequerimientoCompraGuardadoToken.PARAMETRO));
                helper.crearDirecta(id, user.getFullName(), user.getScreenName());
            }
            request.setAttribute("ordenCompraCreada", Boolean.TRUE);
        } catch (ValidacionCompraException e) {
            request.setAttribute("ordenCompraError", e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("ordenCompraError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo guardar la orden de compra. Requerimiento=" + id, e);
            request.setAttribute("ordenCompraError",
                    "No se pudo guardar la orden de compra. Consulte el requerimiento antes de reintentar.");
        }
        response.setRenderParameter("struts_action", RUTA);
        response.setRenderParameter("id_requerimiento_compra", String.valueOf(id));
    }

    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config,
            RenderRequest request, RenderResponse response) throws Exception {
        try {
            User user = PortalUtil.getUser(request);
            if (!puedeConsultar(user)) {
                throw new IllegalArgumentException("No posee permisos para consultar órdenes de compra.");
            }
            RequerimientoCompraOrden orden = helper.preparar(
                    ParamUtil.getInteger(request, "id_requerimiento_compra", 0), user.getFullName());
            if (orden.getIdOrdenCompra() <= 0 || !orden.getRequerimiento().isOrdenCompra()) {
                throw new IllegalArgumentException(
                        "Todavia no se emitio una Orden de Compra para este requerimiento.");
            }
            request.setAttribute("ordenCompra", orden);
        } catch (IllegalArgumentException e) {
            if (request.getAttribute("ordenCompraError") == null) {
                request.setAttribute("ordenCompraError", e.getMessage());
            }
        } catch (Exception e) {
            _log.error("No se pudo consultar la orden de compra.", e);
            if (request.getAttribute("ordenCompraError") == null) {
                request.setAttribute("ordenCompraError",
                        "No se pudo consultar la orden de compra. Verifique la disponibilidad de Compras.");
            }
        }
        return mapping.findForward(FORWARD);
    }

}
