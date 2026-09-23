package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RequerimientoCompraComparativaAction extends PortletAction {

    private static final Log _log = LogFactoryUtil.getLog(RequerimientoCompraComparativaAction.class);
    private final RequerimientoCompraComparativaHelper helper =
            new RequerimientoCompraComparativaHelper();

    public static boolean puedeEditar(User user) throws Exception {
        return user != null
                && (PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS));
    }

    public static void validarPermiso(User user, boolean editar) throws Exception {
        if (!puedeEditar(user) && (editar || user == null
                || !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS))) {
            throw new IllegalArgumentException("No posee permisos para la comparativa de Compras.");
        }
    }

    public void processAction(ActionMapping mapping, ActionForm form,
            PortletConfig config, ActionRequest request, ActionResponse response) throws Exception {
        int id = ParamUtil.getInteger(request, "id_requerimiento_compra");
        response.setRenderParameter("struts_action", "/compras/comparativa");
        response.setRenderParameter("id_requerimiento_compra", String.valueOf(id));
        response.setRenderParameter("editar", "true");
        try {
            User user = PortalUtil.getUser(request);
            validarPermiso(user, true);
            if (!"guardar".equals(ParamUtil.getString(request, "cmd"))) {
                throw new IllegalArgumentException("Operación de comparativa inválida.");
            }
            RequerimientoCompra r = helper.obtenerRequerimiento(id);
            List<RequerimientoCompraComparativa> lista = helper.cargar(r);
            Map<String, String> entrada = new HashMap<String, String>();
            for (RequerimientoCompraComparativa c : lista) {
                String sufijo = "_" + c.getIdPrestador();
                String[] campos = {"prestador", "fecha", "pago", "plazo", "validez", "envio", "iva", "iibb"};
                for (String campo : campos) {
                    entrada.put(campo + sufijo, ParamUtil.getString(request, campo + sufijo));
                }
                for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                    String clave = sufijo + "_" + d.getIdPrestacion();
                    entrada.put("cantidad" + clave, ParamUtil.getString(request, "cantidad" + clave));
                    entrada.put("importe" + clave, ParamUtil.getString(request, "importe" + clave));
                }
            }
            request.setAttribute("comparativaEntrada", entrada);
            helper.guardar(lista, entrada, user.getScreenName());
            request.removeAttribute("comparativaEntrada");
            request.setAttribute("comparativaGuardada", Boolean.TRUE);
            response.setRenderParameter("editar", "false");
        } catch (IllegalArgumentException e) {
            request.setAttribute("comparativaError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo guardar la comparativa. Requerimiento=" + id, e);
            request.setAttribute("comparativaError",
                    "No se pudo guardar la comparativa. Los cambios no fueron confirmados.");
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
            PortletConfig config, RenderRequest request, RenderResponse response) throws Exception {
        try {
            User user = PortalUtil.getUser(request);
            validarPermiso(user, false);
            RequerimientoCompra r = helper.obtenerRequerimiento(
                    ParamUtil.getInteger(request, "id_requerimiento_compra"));
            List<RequerimientoCompraComparativa> lista = helper.cargar(r);
            boolean guardada = false;
            for (RequerimientoCompraComparativa c : lista) {
                guardada = guardada || c.getIdComparativa() > 0;
            }
            boolean editar = puedeEditar(user)
                    && (ParamUtil.getBoolean(request, "editar") || !guardada);
            request.setAttribute("comparativaRequerimiento", r);
            request.setAttribute("comparativas", lista);
            request.setAttribute("comparativaEditable", Boolean.valueOf(editar));
            request.setAttribute("comparativaPuedeEditar", Boolean.valueOf(puedeEditar(user)));
            request.setAttribute("comparativaExiste", Boolean.valueOf(guardada));
        } catch (IllegalArgumentException e) {
            request.setAttribute("comparativaError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo consultar la comparativa.", e);
            request.setAttribute("comparativaError",
                    "No se pudo consultar la comparativa. Revise la disponibilidad de Compras.");
        }
        return mapping.findForward("portlet.compras.comparativa");
    }
}
