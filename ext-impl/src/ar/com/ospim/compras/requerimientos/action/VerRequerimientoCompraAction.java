package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.helper.BusquedaRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
import ar.com.ospim.global.WebKeysGlobal;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class VerRequerimientoCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    VerRequerimientoCompraAction.class
            );

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

    private final RequerimientoCompraReclamoPrestacionalHelper reclamoHelper =
            new RequerimientoCompraReclamoPrestacionalHelper();

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        actionRequest,
                        "id_requerimiento_compra",
                        0
                );

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            validarPermisoView(
                    user
            );

            if (idRequerimientoCompra <= 0) {
                throw new Exception(
                        "Debe informar el requerimiento de compra."
                );
            }

            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(
                            idRequerimientoCompra
                    )
            );

            actionResponse.setRenderParameter(
                    "struts_action",
                    "/compras/ver_requerimiento"
            );

            actionResponse.setRenderParameter(
                    "modo",
                    "ver"
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo preparar la vista del requerimiento.",
                    e
            );

            SessionErrors.add(
                    actionRequest,
                    e.getClass().getName()
            );

            String mensaje =
                    e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo visualizar el requerimiento de compra.";
            }

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH
            );
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        try {
            User user =
                    PortalUtil.getUser(
                            renderRequest
                    );

            validarPermisoView(
                    user
            );

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            renderRequest,
                            "id_requerimiento_compra",
                            0
                    );

            if (idRequerimientoCompra <= 0) {
                throw new Exception(
                        "Debe informar el requerimiento de compra."
                );
            }

            RequerimientoCompra requerimiento =
                    busquedaHelper.getRequerimientoCompra(
                            idRequerimientoCompra
                    );

            if (requerimiento == null) {
                throw new Exception(
                        "No se encontró el requerimiento de compra informado."
                );
            }

            cargarCatalogos(
                    renderRequest
            );

            cargarAfiliadoRequerimiento(
                    renderRequest,
                    requerimiento
            );

            cargarEstadoPrestadoresPendientesNotificacion(
                    renderRequest,
                    requerimiento
            );

            cargarRelacionReclamoPrestacional(
                    renderRequest,
                    requerimiento
            );

            RequerimientoCompraRenderActionUtil
                    .publicarContexto(
                            renderRequest,
                            requerimiento
                    );

            renderRequest.setAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW,
                    requerimiento
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW,
                    requerimiento.getDetalles()
            );

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.TRUE
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo cargar la vista del requerimiento de compra.",
                    e
            );

            String mensaje =
                    e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo cargar la vista del requerimiento de compra.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );
        }

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private void validarPermisoView(
            User user) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        boolean permitido =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_VIEW_COMPRAS
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                );

        if (!permitido) {
            throw new Exception(
                    "No posee permisos para consultar requerimientos de compras."
            );
        }
    }

    private void cargarEstadoPrestadoresPendientesNotificacion(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        boolean hayPendientes = false;

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0
                && requerimiento.puedeReintentarNotificaciones()) {

            try {
                hayPendientes =
                        busquedaHelper
                                .hayPrestadoresPendientesNotificacion(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );

            } catch (Exception e) {
                _log.warn(
                        "No se pudo confirmar si quedan prestadores "
                                + "pendientes de notificación. "
                                + "El botón permanecerá oculto. "
                                + "idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra(),
                        e
                );
            }
        }

        renderRequest.setAttribute(
                WebKeysCompras
                        .HAY_PRESTADORES_PENDIENTES_NOTIFICACION,
                Boolean.valueOf(
                        hayPendientes
                )
        );
    }

    private void cargarRelacionReclamoPrestacional(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        boolean consultaOk = true;
        Object relacion = null;

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0
                && (
                WebKeysCompras.esCotizado(
                        requerimiento.getEstado()
                )
                        || WebKeysCompras.esReclamoRP(
                        requerimiento.getEstado()
                )
        )) {

            try {
                relacion =
                        reclamoHelper
                                .obtenerPorRequerimiento(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );

            } catch (Exception e) {
                consultaOk = false;

                _log.warn(
                        "No se pudo consultar la relación con el "
                                + "Reclamo Prestacional. "
                                + "La acción permanecerá oculta. "
                                + "idRequerimiento="
                                + requerimiento
                                .getIdRequerimientoCompra(),
                        e
                );
            }
        }

        renderRequest.setAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_COMPRA,
                relacion
        );

        renderRequest.setAttribute(
                WebKeysCompras
                        .RELACION_RECLAMO_PRESTACIONAL_CONSULTA_OK,
                Boolean.valueOf(
                        consultaOk
                )
        );
    }

    private void cargarCatalogos(
            RenderRequest renderRequest) {

        try {
            renderRequest.setAttribute(
                    WebKeysCompras.ESTADOS_REQUERIMIENTO,
                    busquedaHelper.listarEstados()
            );
        } catch (Exception e) {
            _log.error(
                    "No se pudieron cargar los estados de Compras.",
                    e
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ESTADOS_REQUERIMIENTO,
                    new ArrayList()
            );
        }

        try {
            renderRequest.setAttribute(
                    WebKeysCompras.SECTORES_REQUERIMIENTO,
                    busquedaHelper.listarSectores()
            );
        } catch (Exception e) {
            _log.error(
                    "No se pudieron cargar los sectores de Compras.",
                    e
            );

            renderRequest.setAttribute(
                    WebKeysCompras.SECTORES_REQUERIMIENTO,
                    new ArrayList()
            );
        }
    }

    private void cargarAfiliadoRequerimiento(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        renderRequest.removeAttribute(
                WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA
        );

        if (requerimiento == null
                || !requerimiento.tieneAfiliadoInformado()) {

            return;
        }

        try {
            List<Afiliado> afiliados =
                    BusquedaAfiliadoServiceUtil
                            .getBusquedaAfiliadosComponente(
                                    requerimiento.getAfiliadoCuilTitular(),
                                    requerimiento.getAfiliadoIntString(),
                                    null,
                                    null,
                                    0,
                                    null,
                                    null,
                                    WebKeysGlobal.ID_DEFAULT_ENTIDAD,
                                    0,
                                    0,
                                    new BigDecimal(0)
                            );

            if (afiliados != null
                    && afiliados.size() == 1) {

                renderRequest.setAttribute(
                        WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA,
                        afiliados.get(0)
                );
            }

        } catch (Exception e) {
            _log.warn(
                    "No se pudo completar el componente visual del afiliado.",
                    e
            );
        }
    }
}
