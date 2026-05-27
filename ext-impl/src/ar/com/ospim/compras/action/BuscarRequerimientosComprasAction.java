package ar.com.ospim.compras.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarRequerimientosComprasAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(BuscarRequerimientosComprasAction.class);

    private static final String[] SEARCH_PARAMS = new String[] {
            "id_estado",
            "estado",
            "id_sector",
            "sector_id",
            "afiliado_cuil_titular",
            "afiliado_int",
            "id_tercerizadora",
            "recupero",
            "texto"
    };

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(actionRequest);
            validarPermisoView(user);

            copiarParametrosBusqueda(actionRequest, actionResponse);
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VIEW);
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);
            validarPermisoView(user);

            RequerimientoCompraFiltro filtro = getFiltroFromRequest(renderRequest);

            List<RequerimientoCompra> requerimientos =
                    BusquedaRequerimientoCompraServiceUtil.buscarRequerimientos(filtro);

            cargarCatalogos(renderRequest);

            renderRequest.setAttribute(WebKeysCompras.FILTRO_REQUERIMIENTOS_COMPRA, filtro);
            renderRequest.setAttribute(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA, requerimientos);

            PortletSession portletSession = renderRequest.getPortletSession();

            portletSession.setAttribute(
                    WebKeysCompras.FILTRO_REQUERIMIENTOS_COMPRA,
                    filtro,
                    PortletSession.PORTLET_SCOPE
            );

            portletSession.setAttribute(
                    WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA,
                    requerimientos,
                    PortletSession.PORTLET_SCOPE
            );
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
    }

    private void validarPermisoView(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para consultar requerimientos de compras.");
        }
    }

    private void cargarCatalogos(RenderRequest request) throws Exception {
        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );
    }

    private void copiarParametrosBusqueda(ActionRequest actionRequest, ActionResponse actionResponse) {
        for (int i = 0; i < SEARCH_PARAMS.length; i++) {
            String name = SEARCH_PARAMS[i];
            String value = actionRequest.getParameter(name);

            if (value != null) {
                actionResponse.setRenderParameter(name, value);
            }
        }
    }

    private RequerimientoCompraFiltro getFiltroFromRequest(RenderRequest request) {
        RequerimientoCompraFiltro filtro = new RequerimientoCompraFiltro();

        int idEstado = ParamUtil.getInteger(request, "id_estado", 0);
        if (idEstado <= 0) {
            idEstado = ParamUtil.getInteger(request, "estado", 0);
        }
        if (idEstado > 0) {
            filtro.setIdEstado(Integer.valueOf(idEstado));
        }

        int idSector = ParamUtil.getInteger(request, "id_sector", 0);
        if (idSector <= 0) {
            idSector = ParamUtil.getInteger(request, "sector_id", 0);
        }
        if (idSector > 0) {
            filtro.setIdSector(Integer.valueOf(idSector));
        }

        filtro.setAfiliadoCuilTitular(ParamUtil.getString(request, "afiliado_cuil_titular", null));

        int afiliadoInt = ParamUtil.getInteger(request, "afiliado_int", -1);
        if (afiliadoInt >= 0) {
            filtro.setAfiliadoInt(Integer.valueOf(afiliadoInt));
        }

        String idTercerizadora = ParamUtil.getString(request, "id_tercerizadora", null);

        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            filtro.setIdTercerizadora(idTercerizadora.trim());
        } else {
            filtro.setIdTercerizadora(null);
        }

        String recupero = ParamUtil.getString(request, "recupero", null);
        if (!WebKeysCompras.isEmpty(recupero)) {
            filtro.setRecupero(Boolean.valueOf("true".equalsIgnoreCase(recupero) || "1".equals(recupero)));
        }

        filtro.setTexto(ParamUtil.getString(request, "texto", null));

        return filtro;
    }
}
