package ar.com.ospim.compras.action;

import java.text.SimpleDateFormat;
import java.util.Date;
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
            "numero",
            "fechaDesde",
            "fechaDesdeDia",
            "fechaDesdeMes",
            "fechaDesdeAnio",
            "fechaHasta",
            "fechaHastaDia",
            "fechaHastaMes",
            "fechaHastaAnio",
            "id_sector",
            "sector_id",
            "id_estado",
            "estado",
            "solicitante_usr",
            "texto",
            "afiliado_cuil_titular",
            "afiliado_inte",
            "tipo_articulo"
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
                WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA,
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

        int numero = ParamUtil.getInteger(request, "numero", 0);
        if (numero > 0) {
            filtro.setNumero(Integer.valueOf(numero));
        }

        filtro.setFechaDesde(parseInputDate(request, "fechaDesde"));
        filtro.setFechaHasta(parseInputDate(request, "fechaHasta"));

        int idSector = ParamUtil.getInteger(request, "id_sector", 0);
        if (idSector <= 0) {
            idSector = ParamUtil.getInteger(request, "sector_id", 0);
        }
        if (idSector > 0) {
            filtro.setIdSector(Integer.valueOf(idSector));
        }

        int idEstado = ParamUtil.getInteger(request, "id_estado", 0);
        if (idEstado <= 0) {
            idEstado = ParamUtil.getInteger(request, "estado", 0);
        }
        if (idEstado > 0) {
            filtro.setIdEstado(Integer.valueOf(idEstado));
        }

        filtro.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        filtro.setTexto(ParamUtil.getString(request, "texto", null));
        filtro.setAfiliadoCuilTitular(ParamUtil.getString(request, "afiliado_cuil_titular", null));

        int afiliadoInte = ParamUtil.getInteger(request, "afiliado_inte", -1);
        if (afiliadoInte >= 0) {
            filtro.setAfiliadoInte(Integer.valueOf(afiliadoInte));
        }

        filtro.setTipoArticulo(ParamUtil.getString(request, "tipo_articulo", null));

        return filtro;
    }

    private Date parseInputDate(RenderRequest request, String prefix) {
        String value = ParamUtil.getString(request, prefix, null);
        Date parsed = parseDate(value);

        if (parsed != null) {
            return parsed;
        }

        String dia = ParamUtil.getString(request, prefix + "Dia");
        String mes = ParamUtil.getString(request, prefix + "Mes");
        String anio = ParamUtil.getString(request, prefix + "Anio");

        try {
            if (WebKeysCompras.isEmpty(dia)
                    || WebKeysCompras.isEmpty(mes)
                    || WebKeysCompras.isEmpty(anio)) {
                return null;
            }

            int mesInt = Integer.parseInt(mes);

            if (mesInt >= 0 && mesInt <= 11) {
                mesInt = mesInt + 1;
            }

            return new SimpleDateFormat("dd/MM/yyyy").parse(dia + "/" + mesInt + "/" + anio);
        } catch (Exception e) {
            return null;
        }
    }

    private Date parseDate(String value) {
        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        String clean = value.trim();

        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(clean);
        } catch (Exception ignored) {
        }

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(clean);
        } catch (Exception ignored) {
        }

        return null;
    }
}