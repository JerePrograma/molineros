package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

public class IniciarReclamoPrestacionalCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    IniciarReclamoPrestacionalCompraAction.class
            );

    private static final String STRUTS_ACTION_RECLAMO =
            "/autorizaciones/editar_reclamosprestaciones_entry";

    private static final String STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";

    private static final String FORWARD_RECLAMO =
            "portlet.autorizaciones.reclamosprestacionales."
                    + "editar_reclamos_entry";

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        actionRequest,
                        WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
                        0
                );

        try {
            User user = PortalUtil.getUser(actionRequest);
            RequerimientoCompra requerimiento =
                    obtenerRequerimientoCotizado(
                            idRequerimientoCompra
                    );

            RequerimientoCompraReclamoPrestacional relacion =
                    RequerimientoCompraReclamoPrestacionalServiceUtil
                            .obtenerPorRequerimiento(
                                    idRequerimientoCompra
                            );

            HttpSession session =
                    PortalUtil.getHttpServletRequest(actionRequest)
                            .getSession();

            if (relacion != null && relacion.isVinculado()) {
                validarPermisoConsulta(user);

                session.removeAttribute(
                        WebKeysCompras
                                .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_RECLAMO
                );
                actionResponse.setRenderParameter(
                        Constants.CMD,
                        Constants.VIEW
                );
                actionResponse.setRenderParameter(
                        "id_reclamosel",
                        String.valueOf(
                                relacion.getIdReclamoPrestacionalInt()
                        )
                );

                setForward(actionRequest, FORWARD_RECLAMO);
                return;
            }

            if (relacion != null) {
                throw new Exception(
                        relacion.isError()
                                ? "El Reclamo Prestacional fue creado, "
                                        + "pero su vinculación requiere "
                                        + "reconciliación. No se permite "
                                        + "crear otro reclamo."
                                : "Ya existe una creación de Reclamo "
                                        + "Prestacional en proceso para "
                                        + "este requerimiento."
                );
            }

            validarPermisoCreacion(user);

            String usuario =
                    user != null
                            ? user.getScreenName()
                            : "sistema";

            ReclamoPrestacionalCompraContexto contexto =
                    new ReclamoPrestacionalCompraContexto(
                            requerimiento
                                    .getIdRequerimientoCompra(),
                            requerimiento
                                    .getAfiliadoCuilTitular(),
                            requerimiento
                                    .getAfiliadoInt(),
                            usuario,
                            System.currentTimeMillis(),
                            UUID.randomUUID().toString()
                    );

            synchronized (session) {
                session.setAttribute(
                        WebKeysCompras
                                .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA,
                        contexto
                );
            }

            actionResponse.setRenderParameter(
                    "struts_action",
                    STRUTS_ACTION_RECLAMO
            );
            actionResponse.setRenderParameter(
                    "origen",
                    "compras"
            );
            actionResponse.setRenderParameter(
                    WebKeysCompras
                            .PARAM_RECLAMO_PRESTACIONAL_NONCE,
                    contexto.getNonce()
            );

            setForward(actionRequest, FORWARD_RECLAMO);
        } catch (Exception e) {
            _log.error(
                    "No se pudo iniciar el Reclamo Prestacional desde "
                            + "Compras. idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            SessionErrors.add(
                    actionRequest,
                    "reclamo-prestacional-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensajeError(e)
            );

            actionResponse.setRenderParameter(
                    "struts_action",
                    STRUTS_ACTION_VER_REQUERIMIENTO
            );

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        WebKeysCompras
                                .PARAM_ID_REQUERIMIENTO_COMPRA,
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(
                    actionRequest,
                    WebKeysCompras
                            .FORWARD_COMPRAS_VER_REQUERIMIENTO
            );
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        String strutsAction =
                ParamUtil.getString(
                        renderRequest,
                        "struts_action",
                        ""
                );

        if (STRUTS_ACTION_RECLAMO.equals(strutsAction)) {
            return mapping.findForward(FORWARD_RECLAMO);
        }

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private RequerimientoCompra obtenerRequerimientoCotizado(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        if (requerimiento == null
                || requerimiento.getBajaFecha() != null) {

            throw new Exception(
                    "No se encontró el requerimiento de compra activo."
            );
        }

        if (!WebKeysCompras.esCotizado(
                requerimiento.getEstado()
        )) {
            throw new Exception(
                    "El Reclamo Prestacional sólo puede iniciarse "
                            + "desde un requerimiento COTIZADO."
            );
        }

        if (!requerimiento.tieneAfiliadoInformado()) {
            throw new Exception(
                    "El requerimiento no tiene un afiliado válido "
                            + "para iniciar el Reclamo Prestacional."
            );
        }

        return requerimiento;
    }

    private void validarPermisoCreacion(User user) throws Exception {
        validarUsuario(user);

        boolean permisoCompras =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                )
                || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                );

        boolean permisoReclamo =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_ABM_RECLAM_PREST
                );

        if (!permisoCompras || !permisoReclamo) {
            throw new Exception(
                    "No posee permisos para crear un Reclamo "
                            + "Prestacional desde Compras."
            );
        }
    }

    private void validarPermisoConsulta(User user) throws Exception {
        validarUsuario(user);

        boolean permisoCompras =
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
                )
                || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ANULAR_COMPRAS
                );

        boolean permisoReclamo =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_ABM_RECLAM_PREST
                )
                || PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_CONSULTA_RECLAMOS_PRESTACIONALES
                );

        if (!permisoCompras || !permisoReclamo) {
            throw new Exception(
                    "No posee permisos para consultar el Reclamo "
                            + "Prestacional asociado."
            );
        }
    }

    private void validarUsuario(User user) throws Exception {
        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }
    }

    private String mensajeError(Exception e) {
        if (e == null || WebKeysCompras.isEmpty(e.getMessage())) {
            return "No se pudo procesar el Reclamo Prestacional.";
        }

        return e.getMessage();
    }
}
