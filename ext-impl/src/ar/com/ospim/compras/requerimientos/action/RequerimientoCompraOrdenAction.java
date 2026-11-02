package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** Emision directa y consulta de la OC, siguiendo el guardado e impresion de Liquidaciones. */
public class RequerimientoCompraOrdenAction extends PortletAction {

    private static final Log _log = LogFactoryUtil.getLog(RequerimientoCompraOrdenAction.class);
    private static final String RUTA = "/compras/orden_compra";
    private static final String FORWARD = "portlet.compras.orden_compra";
    private static final String DESCARGA_EMISION = "COMPRAS_ORDEN_COMPRA_DESCARGA_EMISION";
    private static final long VIGENCIA_DESCARGA_EMISION = 5L * 60L * 1000L;
    private final RequerimientoCompraOrdenHelper helper = new RequerimientoCompraOrdenHelper();

    public static boolean puedeConsultar(User user) throws Exception {
        return user != null && PermissionUtil.userContainsRole(user,
                ConfiguracionComprasHelper.getRolGestionCompras());
    }

    public static boolean puedeEditar(User user) throws Exception {
        return user != null && PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolAbmCompras());
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
            if ("update".equals(cmd)) {
                throw new IllegalArgumentException("La Orden de Compra emitida se encuentra en modo consulta.");
            }
            if ("create".equals(cmd)) {
                RequerimientoCompraOrden orden = obtenerHelper().prepararParaGuardar(id, user.getFullName());
                if (orden.getIdOrdenCompra() > 0) {
                    throw new IllegalArgumentException("La Orden de Compra ya fue emitida. Su consulta requiere Gestion de Compras.");
                }
                Map<String, String> entrada = obtenerEntrada(request, orden);
                request.setAttribute("ordenCompraEntrada", entrada);
                consumirToken(request);
                obtenerHelper().crear(id, entrada, user.getScreenName());
                publicarDescargaEmision(request, user, id);
                request.setAttribute("ordenCompraCreada", Boolean.TRUE);
            } else if (cmd.length() == 0) {
                RequerimientoCompraOrden orden = obtenerHelper().prepararParaGuardar(id, user.getFullName());
                if (orden.getIdOrdenCompra() == 0
                        && !obtenerHelper().requiereCompletarDatos(orden)) {
                    consumirToken(request);
                    obtenerHelper().crearDirecta(id, user.getFullName(), user.getScreenName());
                    publicarDescargaEmision(request, user, id);
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
        response.setRenderParameter("origen_ordenes", ParamUtil.getString(request, "origen_ordenes", "0"));
    }

    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config,
            RenderRequest request, RenderResponse response) throws Exception {
        try {
            User user = obtenerUsuario(request);
            boolean consulta = puedeConsultar(user);
            boolean emisor = puedeEditar(user);
            if (!consulta && !emisor) {
                throw new IllegalArgumentException("No posee permisos para consultar órdenes de compra.");
            }
            RequerimientoCompraOrden orden = obtenerHelper().preparar(
                    ParamUtil.getInteger(request, "id_requerimiento_compra", 0), user.getFullName());
            boolean nueva = orden.getIdOrdenCompra() <= 0;
            boolean constancia = !nueva && emisor && !consulta
                    && Boolean.TRUE.equals(request.getAttribute("ordenCompraCreada"));
            if (!nueva && !consulta && !constancia) {
                throw new IllegalArgumentException("No posee permisos para consultar ordenes de compra emitidas.");
            }
            boolean editable = nueva && emisor;
            if ((nueva && (!editable
                    || !orden.getRequerimiento().esSectorSinCotizacionPrestador()))
                    || (!nueva && !orden.getRequerimiento().isOrdenCompra())) {
                throw new IllegalArgumentException(
                        "Todavia no se emitio una Orden de Compra para este requerimiento.");
            }
            request.setAttribute("ordenCompra", orden);
            request.setAttribute("ordenCompraSoloConstancia", Boolean.valueOf(constancia));
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

    /** Permiso de una sola descarga, limitado al resultado de la emision. */
    protected void publicarDescargaEmision(ActionRequest request, User user, int id) {
        HttpServletRequest http = PortalUtil.getHttpServletRequest(request);
        if (http == null) {
            throw new IllegalStateException("No se pudo preparar la descarga de la Orden de Compra emitida.");
        }
        HttpSession session = http.getSession();
        synchronized (session) {
            session.setAttribute(DESCARGA_EMISION + "_" + id,
                    new long[] { user.getUserId(), id, System.currentTimeMillis() });
        }
    }

    public static boolean consumirDescargaEmision(HttpServletRequest request, User user, int id)
            throws Exception {
        if (request == null || id <= 0 || !puedeEditar(user)) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        synchronized (session) {
            Object valor = session.getAttribute(DESCARGA_EMISION + "_" + id);
            if (!(valor instanceof long[]) || ((long[]) valor).length != 3) {
                return false;
            }
            long[] permiso = (long[]) valor;
            long antiguedad = System.currentTimeMillis() - permiso[2];
            if (antiguedad < 0 || antiguedad > VIGENCIA_DESCARGA_EMISION) {
                session.removeAttribute(DESCARGA_EMISION + "_" + id);
                return false;
            }
            if (permiso[0] != user.getUserId() || permiso[1] != id) {
                return false;
            }
            session.removeAttribute(DESCARGA_EMISION + "_" + id);
            return true;
        }
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
