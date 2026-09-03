package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuscarRequerimientosComprasAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(BuscarRequerimientosComprasAction.class);

    private final RequerimientoCompraReclamoPrestacionalHelper reclamoHelper =
            new RequerimientoCompraReclamoPrestacionalHelper();

    private static final String[] SEARCH_PARAMS = new String[] {
            "id_estado",
            "estado",
            "id_sector",
            "sector_id",
            "fechaAltaDesdeDia",
            "fechaAltaDesdeMes",
            "fechaAltaDesdeAnio",
            "fechaAltaHastaDia",
            "fechaAltaHastaMes",
            "fechaAltaHastaAnio",
            "afiliado_cuil_titular",
            "afiliado_int",
            "id_tercerizadora",
            "surge",
            "texto",
            WebKeysCompras.PARAM_COTIZADOS_INCLUYE_RECLAMO_RP
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

            RequerimientoCompraFiltro filtro =
                    getFiltroFromRequest(
                            renderRequest
                    );

            boolean cotizadosIncluyeReclamoRp =
                    ParamUtil.getBoolean(
                            renderRequest,
                            WebKeysCompras
                                    .PARAM_COTIZADOS_INCLUYE_RECLAMO_RP,
                            false
                    );

            boolean incluirReclamoRpEnCotizados =
                    cotizadosIncluyeReclamoRp
                            && filtro.getIdEstado() != null
                            && filtro.getIdEstado().intValue()
                            == WebKeysCompras.ESTADO_COTIZADO;

            List<RequerimientoCompra> requerimientos =
                    BusquedaRequerimientoCompraServiceUtil
                            .buscarRequerimientosListado(
                            filtro,
                            incluirReclamoRpEnCotizados
                    );

            /*
             * Id RP debe estar disponible tanto en la solapa general
             * Requerimientos como en Cotizados.
             *
             * Las relaciones se recuperan en una única consulta batch;
             * no se consulta individualmente desde el JSP.
             */
            Map<Integer, RequerimientoCompraReclamoPrestacional>
                    relacionesRp =
                    cargarRelacionesRpListado(
                            requerimientos
                    );

            boolean mostrarIdRpListado = true;

            cargarCatalogos(renderRequest);
            setResultadoBusqueda(renderRequest, filtro, requerimientos);

            renderRequest.setAttribute(
                    WebKeysCompras.MOSTRAR_ID_RP_LISTADO,
                    Boolean.valueOf(
                            mostrarIdRpListado
                    )
            );

            renderRequest.setAttribute(
                    WebKeysCompras
                            .RELACIONES_RECLAMO_PRESTACIONAL_COMPRA,
                    relacionesRp
            );
        } catch (Exception e) {
            _log.error(e);

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    getMensajeError(e)
            );

            renderRequest.setAttribute(
                    WebKeysCompras.MOSTRAR_ID_RP_LISTADO,
                    Boolean.FALSE
            );

            renderRequest.setAttribute(
                    WebKeysCompras
                            .RELACIONES_RECLAMO_PRESTACIONAL_COMPRA,
                    new HashMap<Integer, RequerimientoCompraReclamoPrestacional>()
            );

            setResultadoBusqueda(
                    renderRequest,
                    new RequerimientoCompraFiltro(),
                    new ArrayList<RequerimientoCompra>()
            );
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH);
    }

private Map<Integer, RequerimientoCompraReclamoPrestacional>
            cargarRelacionesRpListado(
                    List<RequerimientoCompra> requerimientos)
                    throws Exception {

        List<Integer> ids =
                new ArrayList<Integer>();

        for (int i = 0;
                requerimientos != null
                && i < requerimientos.size();
                i++) {

            RequerimientoCompra requerimiento =
                    requerimientos.get(i);

            if (requerimiento != null
                    && requerimiento
                            .getIdRequerimientoCompra() > 0) {

                ids.add(
                        Integer.valueOf(
                                requerimiento
                                        .getIdRequerimientoCompra()
                        )
                );
            }
        }

        return reclamoHelper
                .obtenerVinculadasPorRequerimientos(
                        ids
                );
    }

    private void validarPermisoView(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {

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

    private RequerimientoCompraFiltro getFiltroFromRequest(
            RenderRequest request) throws Exception {

        RequerimientoCompraFiltro filtro =
                new RequerimientoCompraFiltro();

        Date fechaAltaDesde =
                getFechaFromRequest(
                        request,
                        "fechaAltaDesde",
                        "fecha de alta desde"
                );

        Date fechaAltaHasta =
                getFechaFromRequest(
                        request,
                        "fechaAltaHasta",
                        "fecha de alta hasta"
                );

        if (fechaAltaDesde != null
                && fechaAltaHasta != null
                && fechaAltaDesde.after(fechaAltaHasta)) {

            throw new Exception(
                    "La fecha de alta desde no puede ser posterior "
                            + "a la fecha de alta hasta."
            );
        }

        filtro.setFechaAltaDesde(fechaAltaDesde);
        filtro.setFechaAltaHasta(fechaAltaHasta);

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

        String surge = ParamUtil.getString(request, "surge", null);

        if (!WebKeysCompras.isEmpty(surge)) {
            filtro.setSurge(
                    Boolean.valueOf(
                            "true".equalsIgnoreCase(surge)
                                    || "1".equals(surge)
                    )
            );
        }

        String texto = ParamUtil.getString(request, "texto", null);

        if (!WebKeysCompras.isEmpty(texto)) {
            filtro.setTexto(texto);
        }

        return filtro;
    }

    private Date getFechaFromRequest(
            RenderRequest request,
            String prefijo,
            String descripcion) throws Exception {

        String diaRaw =
                getParametro(
                        request,
                        prefijo + "Dia"
                );

        String mesRaw =
                getParametro(
                        request,
                        prefijo + "Mes"
                );

        String anioRaw =
                getParametro(
                        request,
                        prefijo + "Anio"
                );

        boolean diaInformado =
                esParteFechaInformada(diaRaw);
        boolean mesInformado =
                esParteFechaInformada(mesRaw);
        boolean anioInformado =
                esParteFechaInformada(anioRaw);

        if (!diaInformado
                && !mesInformado
                && !anioInformado) {

            return null;
        }

        if (!diaInformado
                || !mesInformado
                || !anioInformado) {

            throw new Exception(
                    "Debe completar dia, mes y anio de la "
                            + descripcion
                            + "."
            );
        }

        try {
            int dia = Integer.parseInt(diaRaw);
            int mes = Integer.parseInt(mesRaw);
            int anio = Integer.parseInt(anioRaw);

            if (dia < 1
                    || dia > 31
                    || mes < 0
                    || mes > 11
                    || anio < 1900
                    || anio > 9999) {

                throw new Exception();
            }

            SimpleDateFormat formato =
                    new SimpleDateFormat("dd/MM/yyyy");

            formato.setLenient(false);

            return formato.parse(
                    dia
                            + "/"
                            + (mes + 1)
                            + "/"
                            + anio
            );
        } catch (Exception e) {
            throw new Exception(
                    "La "
                            + descripcion
                            + " no es valida."
            );
        }
    }

    private boolean esParteFechaInformada(String value) {
        return !WebKeysCompras.isEmpty(value)
                && !"-1".equals(value);
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
