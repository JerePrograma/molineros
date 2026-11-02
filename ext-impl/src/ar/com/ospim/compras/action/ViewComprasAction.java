package ar.com.ospim.compras.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper;
import ar.com.ospim.compras.requerimientos.action.RequerimientoCompraOrdenAction;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil;

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

import java.text.SimpleDateFormat;
import java.util.Date;
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

            cargarOrdenesCompra(renderRequest);

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
                        ConfiguracionComprasHelper.getRolCotizarCompras()
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

    /* Referencia: BuscarRequerimientosComprasAction.getFechaFromRequest y
     * Liquidaciones/ordenes_pago/orden_pago_ospim_search_result.jsp.
     * La consulta propia de OC no modifica la busqueda de requerimientos. */
    private void cargarOrdenesCompra(RenderRequest request) throws Exception {
        User user = PortalUtil.getUser(request);
        String tab = obtenerTabActiva(request);
        boolean puedeRequerimientos = ExportarRequerimientosCompraHelper.puedeConsultar(user);
        boolean puedeOrdenes = RequerimientoCompraOrdenAction.puedeConsultar(user);
        boolean tabValida = (puedeRequerimientos && "requerimientos".equals(tab))
                || (puedeOrdenes && "orden-de-compra".equals(tab))
                || (user != null && "para-autorizar".equals(tab)
                    && PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolAutorizarCompras()))
                || (user != null && TAB_CONFIGURACION_CORREOS.equals(tab)
                    && PermissionUtil.userContainsRole(user, ConfiguracionComprasHelper.getRolCotizarCompras()));
        if (!tabValida) {
            tab = puedeRequerimientos ? "requerimientos" : (puedeOrdenes ? "orden-de-compra" : "");
        }
        request.setAttribute("tabs1", tab);
        if (!"orden-de-compra".equals(tab)) {
            return;
        }
        if (!puedeOrdenes) {
            throw new Exception("No posee permisos para consultar ordenes de compra.");
        }
        request.setAttribute("comprasOrdenesEmitidas", new ArrayList<RequerimientoCompraOrden>());
        request.setAttribute("comprasOrdenesPagina", Integer.valueOf(1));
        request.setAttribute("comprasOrdenesHaySiguiente", Boolean.FALSE);
        try {
            Integer idOrden = getEnteroOrdenes(request, "id_orden_compra", "Numero OC", false);
            Integer idSector = getEnteroOrdenes(request, "id_sector_oc", "Sector", true);
            Date desde = getFechaOrdenes(request, "fechaAltaOCDesde", "fecha de alta OC desde");
            Date hasta = getFechaOrdenes(request, "fechaAltaOCHasta", "fecha de alta OC hasta");
            if (desde != null && hasta != null && desde.after(hasta)) {
                throw new IllegalArgumentException("La fecha de alta OC desde no puede ser posterior a la fecha de alta OC hasta.");
            }
            Integer paginaRecibida = getEnteroOrdenes(request, "pagina_ordenes", "Pagina", false);
            int pagina = paginaRecibida == null ? 1 : paginaRecibida.intValue();
            int porPagina = ConfiguracionComprasHelper.getRequerimientosPorPagina();
            long inicio = ((long) pagina - 1L) * porPagina;
            if (inicio > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("La pagina solicitada no es valida.");
            }
            List<RequerimientoCompraOrden> ordenes = RequerimientoCompraOrdenServiceUtil.buscarEmitidas(
                    idOrden, desde, hasta, idSector, ConfiguracionComprasHelper.getEstadoOrdenCompra(),
                    (int) inicio, porPagina + 1);
            if (pagina > 1 && ordenes.isEmpty()) {
                pagina = 1;
                ordenes = RequerimientoCompraOrdenServiceUtil.buscarEmitidas(
                        idOrden, desde, hasta, idSector, ConfiguracionComprasHelper.getEstadoOrdenCompra(),
                        0, porPagina + 1);
            }
            boolean haySiguiente = ordenes.size() > porPagina;
            if (haySiguiente) {
                ordenes.remove(ordenes.size() - 1);
            }
            request.setAttribute("comprasOrdenesEmitidas", ordenes);
            request.setAttribute("comprasOrdenesPagina", Integer.valueOf(pagina));
            request.setAttribute("comprasOrdenesHaySiguiente", Boolean.valueOf(haySiguiente));
        } catch (IllegalArgumentException e) {
            request.setAttribute("comprasOrdenesError", e.getMessage());
        } catch (Exception e) {
            _log.error("No se pudo consultar el listado de ordenes de compra emitidas.", e);
            request.setAttribute("comprasOrdenesError", "No se pudo consultar las ordenes de compra. Verifique la disponibilidad de Compras.");
        }
    }

    private Integer getEnteroOrdenes(RenderRequest request, String nombre,
            String descripcion, boolean admiteTodos) {
        String value = getParametroOrdenes(request, nombre);
        if (WebKeysCompras.isEmpty(value) || (admiteTodos && "0".equals(value))) {
            return null;
        }
        try {
            if (!value.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int numero = Integer.parseInt(value);
            if (numero <= 0) {
                throw new NumberFormatException();
            }
            return Integer.valueOf(numero);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El filtro " + descripcion + " no es valido.");
        }
    }

    private Date getFechaOrdenes(RenderRequest request, String prefijo, String descripcion) {
        String dia = getParametroOrdenes(request, prefijo + "Dia");
        String mes = getParametroOrdenes(request, prefijo + "Mes");
        String anio = getParametroOrdenes(request, prefijo + "Anio");
        boolean tieneDia = !WebKeysCompras.isEmpty(dia) && !"-1".equals(dia);
        boolean tieneMes = !WebKeysCompras.isEmpty(mes) && !"-1".equals(mes);
        boolean tieneAnio = !WebKeysCompras.isEmpty(anio) && !"-1".equals(anio);
        if (!tieneDia && !tieneMes && !tieneAnio) {
            return null;
        }
        if (!tieneDia || !tieneMes || !tieneAnio) {
            throw new IllegalArgumentException("Debe completar dia, mes y anio de la " + descripcion + ".");
        }
        try {
            int numeroDia = Integer.parseInt(dia);
            int numeroMes = Integer.parseInt(mes);
            int numeroAnio = Integer.parseInt(anio);
            if (numeroDia < 1 || numeroDia > 31 || numeroMes < 0 || numeroMes > 11
                    || numeroAnio < 1900 || numeroAnio > 9999) {
                throw new IllegalArgumentException();
            }
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);
            return formato.parse(numeroDia + "/" + (numeroMes + 1) + "/" + numeroAnio);
        } catch (Exception e) {
            throw new IllegalArgumentException("La " + descripcion + " no es valida.");
        }
    }

    private String getParametroOrdenes(RenderRequest request, String nombre) {
        String value = ParamUtil.getString(request, nombre, null);
        if (WebKeysCompras.isEmpty(value)) {
            String namespace = PortalUtil.getPortletNamespace(PortalUtil.getPortletId(request));
            value = ParamUtil.getString(request, namespace + nombre, null);
        }
        return value == null ? null : value.trim();
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
