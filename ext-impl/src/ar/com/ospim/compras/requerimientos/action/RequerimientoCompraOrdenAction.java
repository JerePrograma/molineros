package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
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
import javax.portlet.PortletRequest;
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

    public static boolean puedeEditar(User user) throws Exception {
        return user != null && PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS);
    }

    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest request, ActionResponse response) throws Exception {
        int id = ParamUtil.getInteger(request, "id_requerimiento_compra", 0);
        try {
            User user = obtenerUsuario(request);
            if (!puedeEditar(user)) {
                throw new IllegalArgumentException("No posee permisos para crear o editar órdenes de compra.");
            }
            String cmd = ParamUtil.getString(request, "cmd");
            if ("update".equals(cmd) || "create".equals(cmd)) {
                RequerimientoCompraOrden orden = obtenerHelper().prepararParaGuardar(id, user.getFullName());
                Map<String, String> entrada = obtenerEntrada(request, orden);
                request.setAttribute("ordenCompraEntrada", entrada);
                consumirToken(request);
                if ("update".equals(cmd)) {
                    obtenerHelper().actualizar(id, ParamUtil.getInteger(request, "id_orden_compra", 0),
                            ParamUtil.getInteger(request, "version_edicion", -1), entrada, user.getScreenName());
                    request.setAttribute("ordenCompraActualizada", Boolean.TRUE);
                } else {
                    obtenerHelper().crear(id, entrada, user.getScreenName());
                    request.setAttribute("ordenCompraCreada", Boolean.TRUE);
                }
            } else if (cmd.length() == 0) {
                RequerimientoCompraOrden orden = obtenerHelper().prepararParaGuardar(id, user.getFullName());
                if (orden.getIdOrdenCompra() == 0
                        && !obtenerHelper().requiereCompletarDatos(orden)) {
                    consumirToken(request);
                    obtenerHelper().crearDirecta(id, user.getFullName(), user.getScreenName());
                    request.setAttribute("ordenCompraCreada", Boolean.TRUE);
                }
            } else {
                throw new IllegalArgumentException("La operación de la Orden de Compra no es válida.");
            }
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
            User user = obtenerUsuario(request);
            if (!puedeConsultar(user)) {
                throw new IllegalArgumentException("No posee permisos para consultar órdenes de compra.");
            }
            RequerimientoCompraOrden orden = obtenerHelper().preparar(
                    ParamUtil.getInteger(request, "id_requerimiento_compra", 0), user.getFullName());
            boolean editable = puedeEditar(user);
            boolean nueva = orden.getIdOrdenCompra() <= 0;
            if ((nueva && (!editable
                    || !orden.getRequerimiento().esSectorSinCotizacionPrestador()))
                    || (!nueva && !orden.getRequerimiento().isOrdenCompra())) {
                throw new IllegalArgumentException(
                        "Todavia no se emitio una Orden de Compra para este requerimiento.");
            }
            request.setAttribute("ordenCompra", orden);
            request.setAttribute("ordenCompraEditable", Boolean.valueOf(editable));
            request.setAttribute("ordenCompraPuedeCrear", Boolean.valueOf(nueva));
            request.setAttribute("ordenCompraRequiereDatos",
                    Boolean.valueOf(obtenerHelper().requiereCompletarDatos(orden)));
            if (editable) {
                publicarToken(request);
            }
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

    private Map<String, String> obtenerEntrada(ActionRequest request,
            RequerimientoCompraOrden orden) {
        Map<String, String> entrada = new HashMap<String, String>();
        String[] campos = { "condicion", "forma_pago", "plazo_pago", "entrega",
                "observaciones", "iva", "iibb" };
        for (int i = 0; i < campos.length; i++) {
            entrada.put(campos[i], ParamUtil.getString(request, campos[i]));
        }
        for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
            String campo = "precio_" + detalle.getIdInt();
            entrada.put(campo, ParamUtil.getString(request, campo));
        }
        return entrada;
    }

    protected RequerimientoCompraOrdenHelper obtenerHelper() {
        return helper;
    }

    protected User obtenerUsuario(PortletRequest request) throws Exception {
        return PortalUtil.getUser(request);
    }

    protected void consumirToken(ActionRequest request) throws Exception {
        RequerimientoCompraGuardadoToken.consumir(request,
                ParamUtil.getString(request, RequerimientoCompraGuardadoToken.PARAMETRO));
    }

    protected void publicarToken(RenderRequest request) {
        RequerimientoCompraGuardadoToken.publicar(request);
    }

}
