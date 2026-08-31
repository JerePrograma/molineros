package ar.com.ospim.compras.action;

import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class ViewComprasAction extends PortletAction {

    private static final String TAB_CONFIGURACION_CORREOS =
            "configuracion-de-correos";

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ViewComprasAction.class
            );

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        setForward(
                actionRequest,
                WebKeysCompras.FORWARD_COMPRAS_VIEW
        );
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        try {
            cargarCatalogos(
                    renderRequest
            );

            cargarConfiguracionCorreos(
                    renderRequest
            );

            cargarTercerizadoras(
                    renderRequest
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_VIEW
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo preparar la vista principal de Compras.",
                    e
            );

            String mensaje =
                    e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo cargar la vista de Compras.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ERROR
            );
        }
    }

    private void cargarCatalogos(
            RenderRequest request) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );
    }

    private void cargarConfiguracionCorreos(
            RenderRequest request) throws Exception {

        if (!TAB_CONFIGURACION_CORREOS.equals(
                obtenerTabActiva(request)
        )) {
            return;
        }

        User user = PortalUtil.getUser(request);

        if (user == null
                || !PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                )) {

            throw new Exception(
                    "No posee permisos para consultar la configuraci\u00f3n "
                            + "de correos de Compras."
            );
        }

        List<TipoPrestacionCompra> rubros =
                BusquedaRequerimientoCompraServiceUtil
                        .listarTiposPrestacion();

        request.setAttribute(
                WebKeysCompras.TIPOS_PRESTACION_REQUERIMIENTO_COMPRA,
                rubros
        );

        int idTipoPrestacion =
                ParamUtil.getInteger(
                        request,
                        "id_tipo_prestacion",
                        0
                );

        if (idTipoPrestacion == 0) {
            return;
        }

        if (idTipoPrestacion < 0
                || !contieneRubro(
                        rubros,
                        idTipoPrestacion
                )) {

            request.setAttribute(
                    WebKeysCompras
                            .ERROR_PRESTADORES_HABILITADOS_COTIZACION,
                    "El rubro seleccionado no es v\u00e1lido."
            );
            return;
        }

        try {
            request.setAttribute(
                    WebKeysCompras.PRESTADORES_HABILITADOS_COTIZACION,
                    NotificarCotizacionPrestadorServiceUtil
                            .listarPrestadoresConfiguracionCorreosPorRubro(
                                    idTipoPrestacion
                            )
            );
        } catch (Exception e) {
            _log.error(
                    "No se pudo consultar la configuraci\u00f3n de correos "
                            + "para el rubro informado. idTipoPrestacion="
                            + idTipoPrestacion,
                    e
            );

            request.setAttribute(
                    WebKeysCompras
                            .ERROR_PRESTADORES_HABILITADOS_COTIZACION,
                    "No se pudo consultar la configuraci\u00f3n de correos. "
                            + "Intente nuevamente."
            );
        }
    }

    private String obtenerTabActiva(
            RenderRequest request) {

        String tab =
                ParamUtil.getString(
                        request,
                        "tabs1",
                        null
                );

        if (tab == null) {
            tab = (String) request.getAttribute("tabs1");
        }

        if (tab == null) {
            tab = (String) request
                    .getPortletSession()
                    .getAttribute(
                            "compras_tabs1",
                            PortletSession.APPLICATION_SCOPE
                    );
        }

        return tab;
    }

    private boolean contieneRubro(
            List<TipoPrestacionCompra> rubros,
            int idTipoPrestacion) {

        for (int i = 0; rubros != null && i < rubros.size(); i++) {
            TipoPrestacionCompra rubro = rubros.get(i);

            if (rubro != null
                    && rubro.getIdInt() == idTipoPrestacion) {

                return true;
            }
        }

        return false;
    }

    private void cargarTercerizadoras(
            RenderRequest request) {

        List<TercerizadoraServicio> tercerizadoras =
                new ArrayList<TercerizadoraServicio>();

        try {
            List<TercerizadoraServicio> recuperadas =
                    TraeListasServiceUtil
                            .getTercerizadoraServicio(
                                    request
                            );

            if (recuperadas != null) {
                tercerizadoras.addAll(
                        recuperadas
                );
            }

        } catch (Exception e) {
            _log.warn(
                    "No se pudieron cargar las tercerizadoras "
                            + "para el filtro de Compras.",
                    e
            );
        }

        request.setAttribute(
                "compras.requerimientos.tercerizadoras",
                tercerizadoras
        );
    }
}
