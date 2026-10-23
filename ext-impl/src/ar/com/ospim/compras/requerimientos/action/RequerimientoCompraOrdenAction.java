package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** Formulario y consulta de la OC, siguiendo el guardado e impresion de Liquidaciones. */
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
            Map<String, String> entrada = leerEntrada(request, orden);
            request.setAttribute("ordenCompraEntrada", entrada);
            if (orden.getIdOrdenCompra() == 0) {
                RequerimientoCompraGuardadoToken.consumir(request,
                        ParamUtil.getString(request, RequerimientoCompraGuardadoToken.PARAMETRO));
                helper.crear(id, entrada, user.getScreenName());
            }
            request.removeAttribute("ordenCompraEntrada");
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
            boolean puedeCrear = orden.getIdOrdenCompra() == 0
                    && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS);
            if (orden.getIdOrdenCompra() == 0 && !puedeCrear) {
                throw new IllegalArgumentException("No posee permisos para crear órdenes de compra.");
            }
            request.setAttribute("ordenCompra", orden);
            request.setAttribute("ordenCompraPuedeCrear", Boolean.valueOf(puedeCrear));
            if (puedeCrear) {
                RequerimientoCompraGuardadoToken.publicar(request);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("ordenCompraError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo consultar la orden de compra.", e);
            request.setAttribute("ordenCompraError",
                    "No se pudo consultar la orden de compra. Verifique la disponibilidad de Compras.");
        }
        return mapping.findForward(FORWARD);
    }

    private Map<String, String> leerEntrada(ActionRequest request, RequerimientoCompraOrden orden) {
        Map<String, String> entrada = new HashMap<String, String>();
        String[] campos = {"moneda", "proveedorDomicilio", "proveedorContacto", "proveedorEmail",
                "proveedorTelefono", "condicion", "formaPago", "plazoPago", "entrega", "observaciones",
                "confeccionadoPor", "telefonoCompras", "solicitante", "fechaOrdenMedica", "fechaRecepcion",
                "plan", "iva", "iibb"};
        for (int i = 0; i < campos.length; i++) {
            entrada.put(campos[i], ParamUtil.getString(request, campos[i], ""));
        }
        for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
            String campo = "precio_" + detalle.getIdInt();
            entrada.put(campo, ParamUtil.getString(request, campo, ""));
        }
        return entrada;
    }
}
