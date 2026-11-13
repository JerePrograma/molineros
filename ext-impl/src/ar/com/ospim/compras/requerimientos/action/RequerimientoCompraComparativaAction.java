package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
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
import javax.portlet.PortletRequest;
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
                && (PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolAbmCompras())
                || PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolCotizarCompras()));
    }

    public static void validarPermiso(User user, boolean editar) throws Exception {
        if (!puedeEditar(user) && (editar || user == null
                || (!PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolViewCompras())
                && !PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolAutorizarCompras())))) {
            throw new IllegalArgumentException("No posee permisos para la comparativa de Compras.");
        }
    }

    public void processAction(ActionMapping mapping, ActionForm form,
            PortletConfig config, ActionRequest request, ActionResponse response) throws Exception {
        if ("REPARAR_COMPARATIVA".equals(ParamUtil.getString(request, "cmd"))) {
            int id = ParamUtil.getInteger(request, "id_requerimiento_compra");
            try {
                User user = obtenerUsuario(request);
                validarPermiso(user, true);
                RequerimientoCompraGuardadoToken.consumir(request,
                        ParamUtil.getString(request, RequerimientoCompraGuardadoToken.PARAMETRO));
                String firma = ParamUtil.getString(request, "comparativa_firma_reparacion");
                if (id <= 0 || firma.trim().length() == 0
                        || !"SI".equals(ParamUtil.getString(request, "comparativa_confirmar_reparacion"))) {
                    throw new IllegalArgumentException("Debe confirmar las prestaciones que se quitaran de la comparativa.");
                }
                int retiradas = obtenerHelper().reparar(id, firma, user.getScreenName());
                if (retiradas <= 0) {
                    throw new IllegalArgumentException("No hay prestaciones pendientes de correccion. Actualice la comparativa.");
                }
                request.setAttribute("comparativaReparada", Integer.valueOf(retiradas));
            } catch (ValidacionCompraException e) {
                request.setAttribute("comparativaError", e.getMessage());
            } catch (IllegalArgumentException e) {
                request.setAttribute("comparativaError", e.getMessage());
            } catch (Exception e) {
                _log.error("No se pudo reparar la comparativa. Requerimiento=" + id, e);
                request.setAttribute("comparativaError",
                        "No se pudo confirmar la correccion. Actualice la comparativa antes de reintentar.");
            }
            response.setRenderParameter("struts_action", "/compras/comparativa");
            response.setRenderParameter("id_requerimiento_compra", String.valueOf(id));
            return;
        }
        validarPermiso(obtenerUsuario(request), false);
        response.setRenderParameter("struts_action", "/compras/comparativa");
        response.setRenderParameter("id_requerimiento_compra",
                ParamUtil.getString(request, "id_requerimiento_compra"));
        request.setAttribute("comparativaError",
                "La comparativa consolidada es de solo lectura. Cargue el presupuesto desde Prestador enviado.");
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
            PortletConfig config, RenderRequest request, RenderResponse response) throws Exception {
        try {
            User user = obtenerUsuario(request);
            validarPermiso(user, false);
            RequerimientoCompraComparativaHelper helper = obtenerHelper();
            RequerimientoCompra r = helper.obtenerRequerimiento(
                    ParamUtil.getInteger(request, "id_requerimiento_compra"));
            int idPrestador = ParamUtil.getInteger(request, "id_prestador");
            boolean editar = idPrestador > 0;
            if (editar) {
                validarPermiso(user, true);
                if (!PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolCotizarCompras())
                        || !r.puedeAdministrarPresupuestos()) {
                    throw new IllegalArgumentException("No se pueden administrar presupuestos en este contexto.");
                }
            }
            request.setAttribute("comparativaRequerimiento", r);
            request.setAttribute("comparativaEditable", Boolean.valueOf(editar));
            List<RequerimientoCompraComparativa> guardadas =
                    helper.listarParaRecuperacion(r.getIdRequerimientoCompra());
            List<RequerimientoCompraComparativa> inconsistencias =
                    helper.obtenerInconsistencias(r, guardadas);
            if (!inconsistencias.isEmpty()) {
                request.setAttribute("comparativaInconsistencias", inconsistencias);
                boolean puedeReparar = !editar && puedeEditar(user)
                        && helper.puedeReparar(r, inconsistencias);
                request.setAttribute("comparativaPuedeReparar", Boolean.valueOf(puedeReparar));
                if (puedeReparar) {
                    request.setAttribute("comparativaFirmaReparacion", helper.firmaRecuperacion(r, guardadas));
                    RequerimientoCompraGuardadoToken.publicar(request);
                }
                if (request.getAttribute("comparativaError") == null) {
                    request.setAttribute("comparativaError",
                            "Hay prestaciones guardadas que ya no pertenecen al requerimiento.");
                }
                return mapping.findForward("portlet.compras.comparativa");
            }
            List<RequerimientoCompraComparativa> lista;
            if (editar) {
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
            boolean puedeEnviarAAutorizar = !editar && r.isActivo()
                    && WebKeysCompras.puedeEnviarAAutorizar(r.getEstado())
                    && PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolCotizarCompras());
            boolean puedeAutorizar = !editar && r.isActivo()
                    && r.getEstado() == ConfiguracionComprasHelper.getEstadoParaAutorizar()
                    && PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolAutorizarCompras());
            if (puedeEnviarAAutorizar || puedeAutorizar) {
                try {
                    RequerimientoCompraReclamoPrestacional relacion =
                            new RequerimientoCompraReclamoPrestacionalHelper()
                                    .obtenerPorRequerimiento(r.getIdRequerimientoCompra());
                    boolean vinculado = relacion != null && relacion.isVinculado();
                    puedeEnviarAAutorizar = puedeEnviarAAutorizar && vinculado;
                    puedeAutorizar = puedeAutorizar && vinculado;
                } catch (Exception e) {
                    puedeEnviarAAutorizar = false;
                    puedeAutorizar = false;
                    _log.warn("No se pudo consultar el Reclamo Prestacional. "
                            + "Las acciones de autorizacion permaneceran ocultas. Requerimiento="
                            + r.getIdRequerimientoCompra(), e);
                }
            }
            request.setAttribute("comparativaPuedeEnviarAAutorizar",
                    Boolean.valueOf(puedeEnviarAAutorizar));
            request.setAttribute("comparativaPuedeAutorizar", Boolean.valueOf(puedeAutorizar));
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

    protected RequerimientoCompraComparativaHelper obtenerHelper() {
        return helper;
    }

    protected User obtenerUsuario(PortletRequest request) throws Exception {
        return PortalUtil.getUser(request);
    }
}
