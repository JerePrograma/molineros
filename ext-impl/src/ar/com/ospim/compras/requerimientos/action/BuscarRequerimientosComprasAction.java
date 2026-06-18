package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.*;
import java.util.ArrayList;
import java.util.List;

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

            SessionErrors.add(actionRequest, "requerimientos-compra-error");

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    getMensajeError(e)
            );

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
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

            if (requerimientos == null) {
                requerimientos = new ArrayList<RequerimientoCompra>();
            }

            cargarCatalogos(renderRequest);
            setResultadoBusqueda(renderRequest, filtro, requerimientos);
        } catch (Exception e) {
            _log.error(e);

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    getMensajeError(e)
            );

            setResultadoBusqueda(
                    renderRequest,
                    new RequerimientoCompraFiltro(),
                    new ArrayList<RequerimientoCompra>()
            );
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
    }

    private void validarPermisoView(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {

            throw new Exception("No posee permisos para consultar requerimientos de compras.");
        }
    }

    private void cargarCatalogos(RenderRequest request) {
        try {
            request.setAttribute(
                    WebKeysCompras.ESTADOS_REQUERIMIENTO,
                    BusquedaRequerimientoCompraServiceUtil.listarEstados()
            );
        } catch (Exception e) {
            request.setAttribute(
                    WebKeysCompras.ESTADOS_REQUERIMIENTO,
                    new ArrayList()
            );
        }

        try {
            request.setAttribute(
                    WebKeysCompras.SECTORES_REQUERIMIENTO,
                    BusquedaRequerimientoCompraServiceUtil.listarSectores()
            );
        } catch (Exception e) {
            request.setAttribute(
                    WebKeysCompras.SECTORES_REQUERIMIENTO,
                    new ArrayList()
            );
        }
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

    private void setResultadoBusqueda(RenderRequest renderRequest,
                                      RequerimientoCompraFiltro filtro,
                                      List<RequerimientoCompra> requerimientos) {

        if (filtro == null) {
            filtro = new RequerimientoCompraFiltro();
        }

        if (requerimientos == null) {
            requerimientos = new ArrayList<RequerimientoCompra>();
        }

        renderRequest.setAttribute(
                WebKeysCompras.FILTRO_REQUERIMIENTOS_COMPRA,
                filtro
        );

        renderRequest.setAttribute(
                WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA,
                requerimientos
        );
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

        String afiliadoCuilTitular = ParamUtil.getString(request, "afiliado_cuil_titular", null);

        if (!WebKeysCompras.isEmpty(afiliadoCuilTitular)) {
            filtro.setAfiliadoCuilTitular(afiliadoCuilTitular);
        }

        String afiliadoIntRaw = ParamUtil.getString(request, "afiliado_int", null);

        if (!WebKeysCompras.isEmpty(afiliadoIntRaw)) {
            afiliadoIntRaw = afiliadoIntRaw.trim();

            if (afiliadoIntRaw.matches("^[0-9]+$")) {
                filtro.setAfiliadoInt(Integer.valueOf(Integer.parseInt(afiliadoIntRaw)));
            }
        }

        String idTercerizadora = getParametro(request, "id_tercerizadora");
        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = idTercerizadora.trim();

            if (!"0".equals(idTercerizadora)) {
                filtro.setIdTercerizadora(idTercerizadora.toUpperCase());
            }
        }
        String recupero = ParamUtil.getString(request, "recupero", null);

        if (!WebKeysCompras.isEmpty(recupero)) {
            filtro.setRecupero(Boolean.valueOf("true".equalsIgnoreCase(recupero) || "1".equals(recupero)));
        }

        String texto = ParamUtil.getString(request, "texto", null);

        if (!WebKeysCompras.isEmpty(texto)) {
            filtro.setTexto(texto);
        }

        return filtro;
    }

    private String getMensajeError(Exception e) {
        if (e == null || WebKeysCompras.isEmpty(e.getMessage())) {
            return "No se pudo buscar requerimientos de compras.";
        }

        return e.getMessage();
    }

    private String getParametro(RenderRequest request, String name) {
        String value = ParamUtil.getString(request, name, null);

        if (!WebKeysCompras.isEmpty(value)) {
            return value;
        }

        String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));

        value = ParamUtil.getString(request, namespace + name, null);

        if (!WebKeysCompras.isEmpty(value)) {
            return value;
        }

        return null;
    }
}
