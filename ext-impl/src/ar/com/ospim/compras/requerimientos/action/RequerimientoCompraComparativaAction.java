package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import java.util.Collections;
import java.util.List;
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
        validarPermiso(PortalUtil.getUser(request), false);
        response.setRenderParameter("struts_action", "/compras/comparativa");
        response.setRenderParameter("id_requerimiento_compra",
                ParamUtil.getString(request, "id_requerimiento_compra"));
        request.setAttribute("comparativaError",
                "La comparativa consolidada es de solo lectura. Cargue el presupuesto desde Prestador enviado.");
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
            PortletConfig config, RenderRequest request, RenderResponse response) throws Exception {
        try {
            User user = PortalUtil.getUser(request);
            validarPermiso(user, false);
            RequerimientoCompra r = helper.obtenerRequerimiento(
                    ParamUtil.getInteger(request, "id_requerimiento_compra"));
            int idPrestador = ParamUtil.getInteger(request, "id_prestador");
            boolean editar = idPrestador > 0;
            List<RequerimientoCompraComparativa> lista;
            if (editar) {
                validarPermiso(user, true);
                if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)
                        || !r.puedeAdministrarPresupuestos()) {
                    throw new IllegalArgumentException("No se pueden administrar presupuestos en este contexto.");
                }
                lista = Collections.singletonList(helper.cargarPrestador(r, idPrestador));
                RequerimientoCompraPresupuesto presupuesto =
                        new PresupuestoCompraHelper().obtenerPresupuesto(
                                r.getIdRequerimientoCompra(), idPrestador);
                request.setAttribute("comparativaArchivoActual",
                        presupuesto == null ? null : presupuesto.getNombreOriginal());
            } else {
                lista = helper.cargar(r);
            }
            boolean guardada = false;
            for (RequerimientoCompraComparativa c : lista) {
                guardada = guardada || c.getIdComparativa() > 0;
            }
            request.setAttribute("comparativaRequerimiento", r);
            request.setAttribute("comparativas", lista);
            request.setAttribute("comparativaEditable", Boolean.valueOf(editar));
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
