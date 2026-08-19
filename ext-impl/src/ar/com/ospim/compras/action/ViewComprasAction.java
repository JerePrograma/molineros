package ar.com.ospim.compras.action;

import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.helper.BusquedaRequerimientoCompraHelper;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.struts.PortletAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class ViewComprasAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ViewComprasAction.class
            );

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

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
                busquedaHelper.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
                busquedaHelper.listarSectores()
        );
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
