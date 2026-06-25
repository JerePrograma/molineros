package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.GuardadoCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EditarRequerimientoCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(EditarRequerimientoCompraAction.class);

    private static final String ARTICULOS_COMPRA =
            "ARTICULOS_COMPRA";

    /*
     * Blindaje anti doble envío.
     *
     * Se usa un SET de tokens, no un único token, para no romper pantallas
     * abiertas en múltiples tabs. Cada render agrega un token válido.
     * Cada save consume exactamente un token.
     */
    private static final String PARAM_COMPRAS_SAVE_TOKEN =
            "compras_save_token";

    private static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";

    private static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";

    private static final int MAX_TOKENS_GUARDADO_COMPRA = 20;

    private static final int MAX_DETALLES_COTIZACION_RETORNO = 1000;

    private static final String STRUTS_ACTION_NUEVO_REQUERIMIENTO =
            "/compras/nuevo_requerimiento";

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    /*
     * La lógica de detalles queda separada en helper:
     * - parseo de detalle
     * - validación de detalle
     * - guardado/borrado de detalles
     * - normalización de textos nuevos
     */
    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    private boolean esAltaRequerimiento(RenderRequest renderRequest) {
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        int idRequerimientoCompra =
                ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

        Object idAttr =
                renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);

        if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
            idRequerimientoCompra = ((Integer) idAttr).intValue();
        }

        return STRUTS_ACTION_NUEVO_REQUERIMIENTO.equals(strutsAction)
                || "alta".equalsIgnoreCase(modo)
                || idRequerimientoCompra <= 0;
    }

    private boolean vieneDeAlta(ActionRequest actionRequest) {
        String strutsAction = getParametroTrim(actionRequest, "struts_action");
        String modo = getParametroTrim(actionRequest, "modo");

        int idRequerimientoCompra = 0;

        try {
            idRequerimientoCompra =
                    parseEnteroConDefault(
                            actionRequest,
                            "id_requerimiento_compra",
                            "ID del requerimiento",
                            0
                    );
        } catch (Exception e) {
            idRequerimientoCompra = 0;
        }

        return STRUTS_ACTION_NUEVO_REQUERIMIENTO.equals(strutsAction)
                || "alta".equalsIgnoreCase(modo)
                || idRequerimientoCompra <= 0;
    }

    private static class ValidacionCompraException extends Exception {

        private final String campo;

        public ValidacionCompraException(String campo, String message) {
            super(message);
            this.campo = campo;
        }

        public String getCampo() {
            return campo;
        }
    }

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String cmd = getParametroTrim(actionRequest, Constants.CMD);

        int idRequerimientoCompra =
                parseEnteroConDefault(
                        actionRequest,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                );

        boolean altaOriginal = vieneDeAlta(actionRequest);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = getUsuario(user);

            if ("saveCotizacion".equals(cmd)
                    || "cerrarCotizacion".equals(cmd)) {

                validarPermisoCotizar(user);
                consumirTokenGuardadoCompra(actionRequest);

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra."
                    );
                }

                List detallesCotizacion =
                        getDetallesCotizacionFromRequest(actionRequest);

                GuardadoCotizacionResultado resultado =
                        EditarRequerimientoCompraServiceUtil
                                .guardarAvanceCotizacion(
                                        idRequerimientoCompra,
                                        detallesCotizacion,
                                        usuario
                                );

                if (resultado.getEstadoFinal()
                        == WebKeysCompras.ESTADO_COTIZADO) {

                    SessionMessages.add(
                            actionRequest,
                            "requerimiento-compra-cotizacion-completa"
                    );
                    actionResponse.setRenderParameter(
                            "struts_action",
                            "/compras/ver_requerimiento"
                    );
                    setForward(
                            actionRequest,
                            WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
                    );
                } else {
                    SessionMessages.add(
                            actionRequest,
                            "requerimiento-compra-cotizacion-guardada"
                    );
                    actionResponse.setRenderParameter(
                            "struts_action",
                            STRUTS_ACTION_EDITAR_REQUERIMIENTO
                    );
                    setForward(
                            actionRequest,
                            WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                    );
                }

                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
                actionResponse.setRenderParameter(
                        "compras_operacion",
                        "saveCotizacion"
                );

                return;
            }

            if ("saveAll".equals(cmd)) {
                validarPermisoABM(user);
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento =
                        getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(
                                    requerimiento.getIdRequerimientoCompra()
                            );
                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_PENDIENTE);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil
                                .guardarRequerimientoCompra(
                                        requerimiento,
                                        usuario
                                );

                detalleHelper.guardarDetallesDesdeRequest(
                        actionRequest,
                        idRequerimientoCompra,
                        usuario
                );

                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("compras_operacion", "saveAll");

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-guardado"
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if (Constants.ADD.equals(cmd)
                    || Constants.UPDATE.equals(cmd)) {

                validarPermisoABM(user);
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento =
                        getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(
                                    requerimiento.getIdRequerimientoCompra()
                            );
                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_PENDIENTE);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil
                                .guardarRequerimientoCompra(
                                        requerimiento,
                                        usuario
                                );

                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("compras_operacion", cmd);

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-guardado"
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                validarPermisoAnular(user);

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra a anular."
                    );
                }

                validarRequerimientoPuedeAnular(idRequerimientoCompra);

                EditarRequerimientoCompraServiceUtil.cambiarEstado(
                        idRequerimientoCompra,
                        WebKeysCompras.ESTADO_ANULADO,
                        usuario
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-anulado"
                );
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VIEW);
                return;
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el requerimiento de compra.";
            }

            SessionErrors.add(actionRequest, "requerimiento-compra-error");
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);

            if (e instanceof ValidacionCompraException) {
                ValidacionCompraException validacion =
                        (ValidacionCompraException) e;
                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            if (e instanceof RequerimientoCompraDetalleHelper.ValidacionCompraException) {
                RequerimientoCompraDetalleHelper.ValidacionCompraException validacion =
                        (RequerimientoCompraDetalleHelper.ValidacionCompraException) e;
                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            if (altaOriginal && idRequerimientoCompra <= 0) {
                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_NUEVO_REQUERIMIENTO
                );
                actionResponse.setRenderParameter("modo", "alta");
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_ALTA_REQUERIMIENTO
                );
            } else {
                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );
                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );
            }

            actionResponse.setRenderParameter("compras_error", "true");

            if ("saveCotizacion".equals(cmd)
                    || "cerrarCotizacion".equals(cmd)) {

                copiarParametrosCotizacion(actionRequest, actionResponse);
            }

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null ? cmd : ""
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
            User user = PortalUtil.getUser(renderRequest);

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            renderRequest,
                            "id_requerimiento_compra",
                            0
                    );

            Object idAttr =
                    renderRequest.getAttribute(
                            WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION
                    );

            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            if (idRequerimientoCompra > 0) {
                validarPermisoConsulta(user);
            } else {
                validarPermisoABM(user);
            }

            RequerimientoCompra requerimiento;

            if (idRequerimientoCompra > 0) {
                requerimiento =
                        BusquedaRequerimientoCompraServiceUtil
                                .getRequerimientoCompra(idRequerimientoCompra);

                if (requerimiento == null) {
                    throw new Exception(
                            "No se encontró el requerimiento de compra informado."
                    );
                }
            } else {
                requerimiento = new RequerimientoCompra();

                int idSectorParam =
                        ParamUtil.getInteger(renderRequest, "sector_id", 0);

                if (idSectorParam > 0) {
                    requerimiento.setIdSector(Integer.valueOf(idSectorParam));
                }
            }

            boolean soloLectura = esModoSoloLectura(renderRequest);

            if (!puedeEditarRender(user, requerimiento)) {
                soloLectura = true;
            }

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.valueOf(soloLectura)
            );

            if (!soloLectura) {
                generarTokenGuardadoCompra(renderRequest);
            }

            cargarCatalogos(renderRequest, requerimiento);
            cargarAfiliadoRequerimiento(renderRequest, requerimiento);
            cargarEstadoPrestadoresPendientesNotificacion(
                    renderRequest,
                    requerimiento
            );

            if (soloLectura) {
                renderRequest.setAttribute(
                        WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW,
                        requerimiento
                );
                renderRequest.setAttribute(
                        WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_VIEW,
                        requerimiento.getDetalles()
                );
            } else {
                renderRequest.setAttribute(
                        WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION,
                        requerimiento
                );
                renderRequest.setAttribute(
                        WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                        requerimiento.getDetalles()
                );
            }

            /* Compatibilidad con JSP legacy que leen siempre el atributo EDICION. */
            renderRequest.setAttribute(
                    WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento.getDetalles()
            );
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo cargar el requerimiento de compra.";
            }

            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);
            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }

        if (Boolean.TRUE.equals(
                renderRequest.getAttribute(WebKeysCompras.SOLO_LECTURA_ATTR)
        ) || esModoSoloLectura(renderRequest)) {

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );
        }

        if (esAltaRequerimiento(renderRequest)) {
            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ALTA_REQUERIMIENTO
            );
        }

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
        );
    }

    private void cargarEstadoPrestadoresPendientesNotificacion(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) throws Exception {

        boolean hayPendientes = false;

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0
                && requerimiento.puedeReintentarNotificaciones()) {

            try {
                hayPendientes =
                        BusquedaRequerimientoCompraServiceUtil
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
                hayPendientes = false;
            }
        }

        renderRequest.setAttribute(
                WebKeysCompras.HAY_PRESTADORES_PENDIENTES_NOTIFICACION,
                Boolean.valueOf(hayPendientes)
        );
    }

    private void generarTokenGuardadoCompra(RenderRequest renderRequest) {
        if (renderRequest == null) {
            return;
        }

        String token = UUID.randomUUID().toString();
        PortletSession session = renderRequest.getPortletSession();

        synchronized (session) {
            Set tokens = null;
            Object tokensObj = session.getAttribute(SESSION_COMPRAS_SAVE_TOKENS);

            if (tokensObj instanceof Set) {
                tokens = (Set) tokensObj;
            }

            if (tokens == null || tokens.size() >= MAX_TOKENS_GUARDADO_COMPRA) {
                tokens = new HashSet();
            }

            tokens.add(token);
            session.setAttribute(SESSION_COMPRAS_SAVE_TOKENS, tokens);
        }

        renderRequest.setAttribute(ATTR_COMPRAS_SAVE_TOKEN, token);
    }

    private void consumirTokenGuardadoCompra(ActionRequest actionRequest)
            throws ValidacionCompraException {

        String tokenRequest =
                getParametroTrim(actionRequest, PARAM_COMPRAS_SAVE_TOKEN);
        PortletSession session = actionRequest.getPortletSession();

        synchronized (session) {
            Object tokensObj = session.getAttribute(SESSION_COMPRAS_SAVE_TOKENS);

            if (!(tokensObj instanceof Set)) {
                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla está desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            Set tokens = (Set) tokensObj;

            if (WebKeysCompras.isEmpty(tokenRequest)
                    || !tokens.contains(tokenRequest)) {

                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla está desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            tokens.remove(tokenRequest);

            if (tokens.isEmpty()) {
                session.removeAttribute(SESSION_COMPRAS_SAVE_TOKENS);
            } else {
                session.setAttribute(SESSION_COMPRAS_SAVE_TOKENS, tokens);
            }
        }
    }

    private boolean esModoSoloLectura(RenderRequest renderRequest) {
        String strutsAction =
                ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        return "/compras/ver_requerimiento".equals(strutsAction)
                || "ver".equalsIgnoreCase(modo);
    }

    private void cargarCatalogos(
            RenderRequest request,
            RequerimientoCompra requerimiento) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );
        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );

        /* Los artículos continúan cargándose bajo demanda. */
        request.setAttribute(
                ARTICULOS_COMPRA,
                new ArrayList<CompraArticulo>()
        );
    }

    private void cargarAfiliadoRequerimiento(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        renderRequest.removeAttribute(
                WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA
        );

        if (requerimiento == null || !requerimiento.tieneAfiliadoInformado()) {
            return;
        }

        try {
            List<Afiliado> afiliados =
                    BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
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

            if (afiliados != null && afiliados.size() == 1) {
                renderRequest.setAttribute(
                        WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA,
                        afiliados.get(0)
                );
            }
        } catch (Exception e) {
            _log.warn(
                    "No se pudo completar el componente visual del afiliado. "
                            + "Se conservará el snapshot del requerimiento.",
                    e
            );
        }
    }

    private void setIdRequerimientoEnRequest(
            ActionRequest request,
            ActionResponse response,
            int idRequerimientoCompra) {

        request.setAttribute(
                WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                Integer.valueOf(idRequerimientoCompra)
        );
        response.setRenderParameter(
                "id_requerimiento_compra",
                String.valueOf(idRequerimientoCompra)
        );
    }

    private String getUsuario(User user) {
        return user != null ? user.getScreenName() : "sistema";
    }

    private boolean puedeEditarRender(
            User user,
            RequerimientoCompra requerimiento) throws Exception {

        if (user == null || requerimiento == null) {
            return false;
        }

        if (PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        ) && requerimiento.puedeEditarEstructura()) {

            return true;
        }

        return PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        ) && requerimiento.puedeEditarCotizacion();
    }

    private void validarPermisoConsulta(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        boolean puedeVer = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_VIEW_COMPRAS
        );
        boolean puedeAdministrar = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        );
        boolean puedeCotizar = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        );
        boolean puedeAnular = PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ANULAR_COMPRAS
        );

        if (!puedeVer && !puedeAdministrar && !puedeCotizar && !puedeAnular) {
            errorCampo(
                    "permisos",
                    "No posee permisos para consultar requerimientos de compras."
            );
        }
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    private void validarPermisoCotizar(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para cotizar requerimientos de compras."
            );
        }
    }

    private void validarPermisoAnular(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ANULAR_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para anular requerimientos de compras."
            );
        }
    }

    private RequerimientoCompra validarRequerimientoEditable(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            return null;
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontró el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.puedeEditarEstructura()) {
            errorCampo(
                    "estado",
                    "Solo se puede editar la estructura en estado PENDIENTE. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }

        return requerimiento;
    }

    private void validarRequerimientoPuedeAnular(
            int idRequerimientoCompra) throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontró el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.puedeAnular()) {
            errorCampo(
                    "estado",
                    "El requerimiento no puede anularse en su estado actual. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }
    }

    private void prepararRequerimientoParaGuardar(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            return;
        }

        if (requerimiento.getIdSector() != null
                && requerimiento.getIdSector().intValue() > 0) {

            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil.getSector(
                            requerimiento.getIdSector().intValue()
                    );

            if (sector == null) {
                errorCampo(
                        "sector_id",
                        "Sector: el sector seleccionado no existe o no está disponible. ID recibido: "
                                + requerimiento.getIdSector() + "."
                );
            }

            requerimiento.setSectorDescripcion(sector.getDescripcion());
            requerimiento.setRequiereAfiliado(sector.isRequiereAfiliado());

            if (!sector.isRequiereAfiliado()) {
                aplicarReglaSectorSinAfiliado(requerimiento);
                return;
            }
        }

        if (requerimiento.tieneAfiliadoInformado()) {
            cargarSnapshotAfiliado(requerimiento);
        }

        Integer cargoTercerizadora =
                requerimiento.getCargoTercerizadora();

        requerimiento.setRecupero(
                cargoTercerizadora != null
                        && cargoTercerizadora.intValue() > 0
        );
    }

    private void cargarSnapshotAfiliado(
            RequerimientoCompra requerimiento) throws Exception {

        List<Afiliado> afiliados =
                BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
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

        if (afiliados == null || afiliados.size() != 1) {
            errorCampo(
                    "afiliado_cuil_titular",
                    "No se pudo obtener un único afiliado para guardar el requerimiento."
            );
        }

        Afiliado afiliado = afiliados.get(0);

        requerimiento.setAfiliadoIdOspim(afiliado.getId_ospim());
        requerimiento.setAfiliadoNombre(afiliado.getNombre());
        requerimiento.setAfiliadoApellido(afiliado.getApellido());
        requerimiento.setAfiliadoDocumentoTipo(afiliado.getDocumento_tipo());
        requerimiento.setAfiliadoDocumentoNro(afiliado.getDocu_numero());
        requerimiento.setAfiliadoEmail(afiliado.getEmail());

        List<Domicilio> domicilios =
                BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                        requerimiento.getAfiliadoCuilTitular(),
                        requerimiento.getAfiliadoInt().intValue()
                );

        if ((domicilios == null || domicilios.isEmpty())
                && requerimiento.getAfiliadoInt().intValue() != 0) {

            domicilios =
                    BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(
                            requerimiento.getAfiliadoCuilTitular(),
                            0
                    );
        }

        if (domicilios == null || domicilios.isEmpty()) {
            return;
        }

        Domicilio domicilio = domicilios.get(0);

        requerimiento.setAfiliadoDireccion(formatearDireccion(domicilio));
        requerimiento.setAfiliadoLocalidad(domicilio.getLocalidadAsString());
        requerimiento.setAfiliadoProvincia(domicilio.getProvinciaAsString());
        requerimiento.setAfiliadoCelular(
                formatearTelefono(
                        domicilio.getCod_area_celular(),
                        domicilio.getCelular()
                )
        );
        requerimiento.setAfiliadoTelefono(
                formatearTelefono(
                        domicilio.getCod_area_telefono(),
                        domicilio.getTelefono()
                )
        );
    }

    private String formatearDireccion(Domicilio domicilio) {
        if (domicilio == null) {
            return null;
        }

        StringBuilder direccion = new StringBuilder();
        agregarParte(direccion, domicilio.getCalle());
        agregarParte(direccion, domicilio.getNumero());
        agregarParte(direccion, prefijar("Piso", domicilio.getPiso()));
        agregarParte(direccion, prefijar("Dto.", domicilio.getDepto()));
        agregarParte(direccion, prefijar("Of.", domicilio.getOficina()));
        return WebKeysCompras.trimToNull(direccion.toString());
    }

    private String formatearTelefono(String codigoArea, String numero) {
        StringBuilder telefono = new StringBuilder();
        agregarParte(telefono, codigoArea);
        agregarParte(telefono, numero);
        return WebKeysCompras.trimToNull(telefono.toString());
    }

    private String prefijar(String prefijo, String valor) {
        String normalizado = WebKeysCompras.trimToNull(valor);
        return normalizado != null ? prefijo + " " + normalizado : null;
    }

    private void agregarParte(StringBuilder destino, String valor) {
        String normalizado = WebKeysCompras.trimToNull(valor);

        if (normalizado == null) {
            return;
        }

        if (destino.length() > 0) {
            destino.append(' ');
        }

        destino.append(normalizado);
    }

    private void validarCabecera(RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {
            errorCampo("requerimiento", "Debe informar el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {

            errorCampo("sector_id", "Sector: debe seleccionar un sector.");
        }

        if (!requerimiento.isRequiereAfiliado()) {
            aplicarReglaSectorSinAfiliado(requerimiento);
        }

        validarPorcentaje(requerimiento.getCargoOspim(), "Cargo OSPIM");
        validarPorcentaje(
                requerimiento.getCargoTercerizadora(),
                "Cargo tercerizadora"
        );

        int cargoOspim = requerimiento.getCargoOspim() != null
                ? requerimiento.getCargoOspim().intValue()
                : 0;
        int cargoTercerizadora =
                requerimiento.getCargoTercerizadora() != null
                        ? requerimiento.getCargoTercerizadora().intValue()
                        : 0;
        int sumaCargos = cargoOspim + cargoTercerizadora;

        if (sumaCargos != 100) {
            errorCampo(
                    "cargo_tercerizadora",
                    "Cargos: la suma de Cargo OSPIM (" + cargoOspim
                            + ") y Cargo tercerizadora (" + cargoTercerizadora
                            + ") es " + sumaCargos
                            + ". Debe ser exactamente 100."
            );
        }

        requerimiento.setRecupero(cargoTercerizadora > 0);

        if (requerimiento.isRequiereAfiliado()
                && cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {

            errorCampo(
                    "id_tercerizadora",
                    "Tercerizadora: debe seleccionar un afiliado con tercerizadora "
                            + "porque Cargo tercerizadora es mayor a 0."
            );
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(
                    requerimiento.getAfiliadoCuilTitular()
            )) {
                errorCampo(
                        "afiliado_cuil_titular",
                        "Afiliado: debe seleccionar un afiliado. Falta CUIL titular."
                );
            }

            if (requerimiento.getAfiliadoInt() == null) {
                errorCampo(
                        "afiliado_int",
                        "Afiliado: debe seleccionar un afiliado. Falta integrante."
                );
            }
        }
    }

    private void validarPorcentaje(Integer value, String label)
            throws Exception {

        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    label,
                    label + ": debe estar entre 0 y 100. Valor recibido: "
                            + parsed + "."
            );
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(
            ActionRequest request) throws Exception {

        RequerimientoCompra requerimiento = new RequerimientoCompra();

        int idRequerimientoCompra = parseEnteroConDefault(
                request,
                "id_requerimiento_compra",
                "ID del requerimiento",
                0
        );
        requerimiento.setIdRequerimientoCompra(idRequerimientoCompra);

        int idSector =
                parseEnteroConDefault(request, "id_sector", "Sector", 0);

        if (idSector <= 0) {
            idSector =
                    parseEnteroConDefault(request, "sector_id", "Sector", 0);
        }

        requerimiento.setIdSector(
                idSector > 0 ? Integer.valueOf(idSector) : null
        );
        requerimiento.setAfiliadoCuilTitular(
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(
                                request,
                                "afiliado_cuil_titular",
                                null
                        )
                )
        );

        String afiliadoIntRaw = getParametroTrim(request, "afiliado_int");

        if (WebKeysCompras.isEmpty(afiliadoIntRaw)
                || "-1".equals(afiliadoIntRaw)) {
            requerimiento.setAfiliadoInt(null);
        } else {
            requerimiento.setAfiliadoInt(
                    parseEnteroOpcional(
                            request,
                            "afiliado_int",
                            "Afiliado - integrante"
                    )
            );
        }

        Integer cargoOspim = parsePorcentajeDesdeRequest(
                request,
                "cargo_ospim",
                "Cargo OSPIM"
        );
        Integer cargoTercerizadora = parsePorcentajeDesdeRequest(
                request,
                "cargo_tercerizadora",
                "Cargo tercerizadora"
        );

        requerimiento.setCargoOspim(cargoOspim);
        requerimiento.setCargoTercerizadora(cargoTercerizadora);

        String idTercerizadora =
                getParametroTrim(request, "id_tercerizadora");

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(
                    request,
                    "requerimiento_id_tercerizadora_visible"
            );
        }

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(
                    request,
                    "requerimiento_id_tercerizadora"
            );
        }

        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            requerimiento.setIdTercerizadora(
                    idTercerizadora.trim().toUpperCase()
            );
        } else {
            requerimiento.setIdTercerizadora(null);
        }

        preservarTercerizadoraExistenteSiNoCambioAfiliado(requerimiento);

        requerimiento.setRecupero(
                cargoTercerizadora != null
                        && cargoTercerizadora.intValue() > 0
        );
        requerimiento.setSurge(getParametroBoolean(request, "surge"));
        requerimiento.setObservaciones(
                getParametroRaw(request, "observaciones", null)
        );

        return requerimiento;
    }

    /*
     * Contrato transitorio compatible:
     *
     * 1. Si llega id_prestador_adjudicado, se aplica a todos los detalles y
     *    cualquier ID legacy diferente se considera manipulación.
     * 2. Si todavía llega el formulario antiguo por detalle, se acepta solo
     *    cuando todos los IDs no vacíos coinciden y se replica ese único ID.
     * 3. Si no se eligió prestador, se conservan null para permitir guardar un
     *    avance parcial de precios en A COTIZAR.
     */
    private List getDetallesCotizacionFromRequest(ActionRequest request)
            throws Exception {

        int count = parseEnteroConDefault(
                request,
                "detalle_count",
                "Cantidad de detalles",
                0
        );

        int idPrestadorAdjudicado = parseEnteroConDefault(
                request,
                WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                "Prestador adjudicado",
                0
        );

        List detalles = new ArrayList();
        Set idsPrestadorLegacy = new HashSet();

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";

            int idDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id",
                    "Detalle #" + (i + 1),
                    0
            );

            if (idDetalle <= 0) {
                continue;
            }

            RequerimientoCompraDetalle detalle =
                    new RequerimientoCompraDetalle();
            detalle.setId(Integer.valueOf(idDetalle));

            BigDecimal precioUnitario = parseBigDecimalNullable(
                    getParametroTrim(
                            request,
                            prefix + "precio_unitario_estimado"
                    ),
                    "Detalle #" + (i + 1) + " - Precio unitario"
            );
            detalle.setPrecioUnitarioEstimado(precioUnitario);

            int idPrestadorDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id_prestador",
                    "Detalle #" + (i + 1) + " - Prestador",
                    0
            );

            if (idPrestadorDetalle > 0) {
                idsPrestadorLegacy.add(Integer.valueOf(idPrestadorDetalle));
            }

            if (idPrestadorAdjudicado > 0
                    && idPrestadorDetalle > 0
                    && idPrestadorDetalle != idPrestadorAdjudicado) {

                errorCampo(
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        "La cotización fue manipulada: el prestador del detalle #"
                                + (i + 1)
                                + " no coincide con el prestador adjudicado."
                );
            }

            detalles.add(detalle);
        }

        if (idPrestadorAdjudicado <= 0) {
            if (idsPrestadorLegacy.size() > 1) {
                errorCampo(
                        WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                        "Debe seleccionar un único prestador adjudicado para "
                                + "todo el requerimiento."
                );
            }

            if (idsPrestadorLegacy.size() == 1) {
                idPrestadorAdjudicado =
                        ((Integer) idsPrestadorLegacy.iterator().next())
                                .intValue();
            }
        }

        Integer prestadorUnico = idPrestadorAdjudicado > 0
                ? Integer.valueOf(idPrestadorAdjudicado)
                : null;

        for (int i = 0; i < detalles.size(); i++) {
            RequerimientoCompraDetalle detalle =
                    (RequerimientoCompraDetalle) detalles.get(i);
            detalle.aplicarPrestadorAdjudicado(prestadorUnico);
        }

        return detalles;
    }

    private void copiarParametrosCotizacion(
            ActionRequest request,
            ActionResponse response) {

        int count;

        try {
            count = Integer.parseInt(
                    getParametroTrim(request, "detalle_count")
            );
        } catch (Exception e) {
            count = 0;
        }

        if (count < 0) {
            count = 0;
        } else if (count > MAX_DETALLES_COTIZACION_RETORNO) {
            count = MAX_DETALLES_COTIZACION_RETORNO;
        }

        response.setRenderParameter("detalle_count", String.valueOf(count));

        String prestadorAdjudicado = getParametroRaw(
                request,
                WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                null
        );

        if (prestadorAdjudicado != null) {
            response.setRenderParameter(
                    WebKeysCompras.PARAM_ID_PRESTADOR_ADJUDICADO,
                    prestadorAdjudicado
            );
        }

        String[] campos = {
                "id",
                "precio_unitario_estimado",
                "id_prestador",
                "prestador_label"
        };

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";

            for (int j = 0; j < campos.length; j++) {
                String nombre = prefix + campos[j];
                String valor = getParametroRaw(request, nombre, null);

                if (valor != null) {
                    response.setRenderParameter(nombre, valor);
                }
            }
        }
    }

    private BigDecimal parseBigDecimalNullable(
            String value,
            String label) throws ValidacionCompraException {

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        String original = value.trim();
        String clean = original.replace(" ", "");

        if (clean.indexOf(',') >= 0) {
            clean = clean.replace(".", "").replace(",", ".");
        }

        if (!clean.matches("^-?[0-9]+(\\.[0-9]+)?$")) {
            errorCampo(
                    label,
                    label + ": importe inválido. Valor recibido: '"
                            + original
                            + "'. Use formatos como 1234.56 o 1.234,56."
            );
        }

        try {
            return new BigDecimal(clean);
        } catch (Exception e) {
            errorCampo(
                    label,
                    label + ": no se pudo interpretar el importe '"
                            + original + "'."
            );
        }

        return null;
    }

    private void errorCampo(String campo, String mensaje)
            throws ValidacionCompraException {

        throw new ValidacionCompraException(campo, mensaje);
    }

    private String getParametroTrim(ActionRequest request, String nombre) {
        String value = getParametroRaw(request, nombre, null);
        return value != null ? value.trim() : "";
    }

    private boolean getParametroBoolean(
            ActionRequest request,
            String nombre) {

        String value = getParametroTrim(request, nombre);

        return "true".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value)
                || "1".equals(value)
                || "si".equalsIgnoreCase(value)
                || "s".equalsIgnoreCase(value);
    }

    private String getParametroRaw(
            ActionRequest request,
            String nombre,
            String defaultValue) {

        if (request == null || nombre == null) {
            return defaultValue;
        }

        String value = request.getParameter(nombre);

        if (value != null) {
            return value;
        }

        try {
            value = ParamUtil.getString(request, nombre, null);

            if (value != null) {
                return value;
            }
        } catch (Exception e) {
            // Sigue fallback manual.
        }

        Map parameterMap = request.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return defaultValue;
        }

        String bestKey = null;
        Iterator it = parameterMap.keySet().iterator();

        while (it.hasNext()) {
            Object keyObj = it.next();

            if (keyObj == null) {
                continue;
            }

            String key = String.valueOf(keyObj);

            if (key.equals(nombre)
                    || key.endsWith("_" + nombre)
                    || key.endsWith(nombre)) {

                if (bestKey == null || key.length() < bestKey.length()) {
                    bestKey = key;
                }
            }
        }

        if (bestKey == null) {
            return defaultValue;
        }

        Object raw = parameterMap.get(bestKey);

        if (raw == null) {
            return defaultValue;
        }

        if (raw instanceof String[]) {
            String[] values = (String[]) raw;
            return values.length > 0 ? values[0] : defaultValue;
        }

        return String.valueOf(raw);
    }

    private Integer parseEnteroOpcional(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' no es un número entero válido."
            );
        }

        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' está fuera del rango permitido."
            );
        }

        return null;
    }

    private int parseEnteroConDefault(
            ActionRequest request,
            String nombre,
            String label,
            int defaultValue) throws ValidacionCompraException {

        Integer parsed = parseEnteroOpcional(request, nombre, label);
        return parsed != null ? parsed.intValue() : defaultValue;
    }

    private Integer parsePorcentajeDesdeRequest(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            value = "0";
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un número entero entre 0 y 100. "
                            + "Valor recibido: '" + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' está fuera del rango permitido."
            );
            return null;
        }

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    nombre,
                    label + ": debe estar entre 0 y 100. Valor recibido: "
                            + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

    private void aplicarReglaSectorSinAfiliado(
            RequerimientoCompra requerimiento) {

        if (requerimiento == null) {
            return;
        }

        requerimiento.setAfiliadoCuilTitular(null);
        requerimiento.setAfiliadoInt(null);
        requerimiento.setAfiliadoIdOspim((Integer) null);

        if (requerimiento.getIdRequerimientoCompra() <= 0) {
            requerimiento.setIdTercerizadora(null);
        }

        requerimiento.setCargoOspim(Integer.valueOf(100));
        requerimiento.setCargoTercerizadora(Integer.valueOf(0));
        requerimiento.setRecupero(false);
    }

    private void preservarTercerizadoraExistenteSiNoCambioAfiliado(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null
                || requerimiento.getIdRequerimientoCompra() <= 0) {
            return;
        }

        RequerimientoCompra existente =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                requerimiento.getIdRequerimientoCompra()
                        );

        if (existente == null) {
            return;
        }

        boolean mismoAfiliado = mismoTexto(
                existente.getAfiliadoCuilTitular(),
                requerimiento.getAfiliadoCuilTitular()
        ) && mismoInteger(
                existente.getAfiliadoInt(),
                requerimiento.getAfiliadoInt()
        );

        if (!mismoAfiliado) {
            return;
        }

        if (!WebKeysCompras.isEmpty(existente.getIdTercerizadora())) {
            requerimiento.setIdTercerizadora(
                    existente.getIdTercerizadora().trim().toUpperCase()
            );
        }
    }

    private boolean mismoTexto(String a, String b) {
        String aa = a != null ? a.trim() : "";
        String bb = b != null ? b.trim() : "";
        return aa.equalsIgnoreCase(bb);
    }

    private boolean mismoInteger(Integer a, Integer b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.intValue() == b.intValue();
    }
}
