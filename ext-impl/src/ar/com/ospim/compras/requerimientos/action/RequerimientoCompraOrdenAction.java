package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSuministro;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import java.math.BigDecimal;
import java.util.List;
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
        if ("guardar_suministro".equals(ParamUtil.getString(request, "cmd"))) {
            guardarSuministro(request, response, id);
            return;
        }
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
            boolean suministro = !nueva && consulta
                    && ParamUtil.getBoolean(request, "gestion_suministro", false);
            if (suministro) {
                request.setAttribute("ordenSuministro", obtenerHelper().cargarSuministro(orden));
            }
            // Mismo padron que VerRequerimientoCompraAction.cargarAfiliadoRequerimiento.
            request.removeAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);
            if (!nueva && consulta && orden.getRequerimiento().tieneAfiliadoInformado()) {
                List<Afiliado> afiliados = null;
                try {
                    afiliados = buscarAfiliadoComponente(
                            orden.getRequerimiento().getAfiliadoCuilTitular(),
                            orden.getRequerimiento().getAfiliadoIntString());
                } catch (Exception e) {
                    _log.warn("No se pudo completar el componente visual del afiliado.", e);
                }
                if (afiliados != null && afiliados.size() == 1) {
                    Afiliado afiliado = afiliados.get(0);
                    if (!orden.getRequerimiento().getAfiliadoCuilTitular().equals(afiliado.getCuil_titular())
                            || orden.getRequerimiento().getAfiliadoInt().intValue() != afiliado.getInte()) {
                        throw new IllegalArgumentException("El afiliado consultado no coincide con el requerimiento.");
                    }
                    request.setAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA, afiliado);
                }
            }
            request.setAttribute("ordenCompra", orden);
            request.setAttribute("ordenCompraSoloConstancia", Boolean.valueOf(constancia));
            request.setAttribute("ordenCompraEditable", Boolean.valueOf(editable));
            request.setAttribute("ordenCompraPuedeCrear", Boolean.valueOf(nueva));
            request.setAttribute("ordenCompraRequiereDatos",
                    Boolean.valueOf(obtenerHelper().requiereCompletarDatos(orden)));
            if (editable || suministro) {
                publicarToken(request);
            }
            if (!nueva && consulta
                    && ParamUtil.getBoolean(request, "gestion_suministro", false)
                    && orden.getRequerimiento().tieneAfiliadoInformado()) {
                ActualizarContactoAfiliadoCompraToken.publicarSuministro(
                        request, orden.getRequerimiento());
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

    /** Guardado del seguimiento: mismo formulario y validacion que la consulta de OC. */
    private void guardarSuministro(ActionRequest request, ActionResponse response, int id) {
        Map<String, String> entrada = obtenerEntradaSuministro(request);
        request.setAttribute("ordenSuministroEntrada", entrada);
        try {
            User user = obtenerUsuario(request);
            if (!puedeConsultar(user)) {
                throw new IllegalArgumentException("No posee permisos para gestionar el suministro de la Orden de Compra.");
            }
            consumirToken(request);
            RequerimientoCompraOrdenSuministro guardado = obtenerHelper().guardarSuministro(
                    id, ParamUtil.getInteger(request, "id_orden_compra", 0),
                    ParamUtil.getInteger(request, "version_suministro", -1), entrada,
                    user.getScreenName());
            request.setAttribute("ordenSuministro", guardado);
            request.removeAttribute("ordenSuministroEntrada");
            request.setAttribute("ordenSuministroGuardado", Boolean.TRUE);
        } catch (ValidacionCompraException e) {
            request.setAttribute("ordenSuministroError", e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("ordenSuministroError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo guardar la gestion de suministro. Requerimiento=" + id, e);
            request.setAttribute("ordenSuministroError",
                    "No se pudo guardar la gestion de suministro. Vuelva a consultar la OC antes de reintentar.");
        }
        response.setRenderParameter("struts_action", RUTA);
        response.setRenderParameter("id_requerimiento_compra", String.valueOf(id));
        response.setRenderParameter("origen_ordenes", "1");
        response.setRenderParameter("gestion_suministro", "1");
    }

    private Map<String, String> obtenerEntradaSuministro(ActionRequest request) {
        Map<String, String> entrada = new HashMap<String, String>();
        String[] campos = { "suministro_persona", "suministro_medio", "suministro_detalle",
                "suministro_pregunta_1", "suministro_pregunta_2", "suministro_pregunta_3",
                "suministro_pregunta_4", "suministro_pregunta_5", "version_suministro",
                "contactoDia", "contactoMes", "contactoAnio" };
        for (int i = 0; i < campos.length; i++) {
            entrada.put(campos[i], ParamUtil.getString(request, campos[i]));
        }
        String fecha = "";
        if (entrada.get("contactoDia").length() > 0 || entrada.get("contactoMes").length() > 0
                || entrada.get("contactoAnio").length() > 0) {
            int dia = ParamUtil.getInteger(request, "contactoDia", -1);
            int mes = ParamUtil.getInteger(request, "contactoMes", -2) + 1;
            int anio = ParamUtil.getInteger(request, "contactoAnio", -1);
            fecha = (dia >= 0 && dia < 10 ? "0" : "") + dia + "/"
                    + (mes >= 0 && mes < 10 ? "0" : "") + mes + "/" + anio;
        }
        entrada.put("fecha_contacto", fecha);
        return entrada;
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

    protected List<Afiliado> buscarAfiliadoComponente(String cuil, String inte) throws Exception {
        return BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
                cuil, inte, null, null, 0, null, null,
                WebKeysGlobal.ID_DEFAULT_ENTIDAD, 0, 0, new BigDecimal(0));
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
