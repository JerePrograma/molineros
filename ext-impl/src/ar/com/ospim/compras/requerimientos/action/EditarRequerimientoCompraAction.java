package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;
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
import java.util.*;

public class EditarRequerimientoCompraAction extends PortletAction {

    private static final String ARTICULOS_COMPRA =
            "ARTICULOS_COMPRA";

    /*
     * Blindaje anti doble envio.
     *
     * Se usa un SET de tokens, no un unico token, para no romper pantallas
     * abiertas en multiples tabs. Cada render agrega un token valido.
     * Cada save consume exactamente un token.
     */
    private static final String PARAM_COMPRAS_SAVE_TOKEN =
            "compras_save_token";

    private static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";

    private static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";

    private static final int MAX_TOKENS_GUARDADO_COMPRA = 20;

    private static final String STRUTS_ACTION_NUEVO_REQUERIMIENTO =
            "/compras/nuevo_requerimiento";

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    /*
     * La logica de detalles queda separada en helper:
     * - parseo de detalle
     * - validacion de detalle
     * - guardado/borrado de detalles
     * - normalizacion de textos nuevos
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

    public void processAction(ActionMapping mapping,
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

        /*
         * Importante:
         * El popup de articulo SOLO debe tratarse como popup si el cmd real es saveArticuloPopup.
         * No uses struts_action para decidir esto, porque si struts_action queda contaminado
         * con /compras/alta_articulo_popup, cualquier error de saveAll puede terminar
         * redirigiendo al popup incorrecto.
         */
        boolean accionPopupArticulo = "saveArticuloPopup".equals(cmd);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = getUsuario(user);

            if ("saveAll".equals(cmd)) {
                validarPermisoABM(user);

                /*
                 * Blindaje anti doble click / doble submit.
                 * Debe ejecutarse despues del permiso y antes de guardar cualquier cosa.
                 */
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(requerimiento.getIdRequerimientoCompra());

                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_BORRADOR);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(
                                requerimiento,
                                usuario
                        );

                int detallesGuardados =
                        detalleHelper.guardarDetallesDesdeRequest(
                                actionRequest,
                                idRequerimientoCompra,
                                usuario
                        );

                actionResponse.setRenderParameter("compras_guardado", "true");
                actionResponse.setRenderParameter("compras_operacion", "saveAll");
                actionResponse.setRenderParameter(
                        "compras_detalles_guardados",
                        String.valueOf(detallesGuardados)
                );

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_EDITAR_REQUERIMIENTO
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if (Constants.ADD.equals(cmd) || Constants.UPDATE.equals(cmd)) {
                validarPermisoABM(user);

                /*
                 * Blindaje tambien para flujos legacy ADD/UPDATE.
                 * Si siguen vivos, tambien guardan cabecera.
                 */
                consumirTokenGuardadoCompra(actionRequest);

                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(requerimiento.getIdRequerimientoCompra());

                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_BORRADOR);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(
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

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

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

                SessionMessages.add(actionRequest, "requerimiento-compra-anulado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VIEW);

                return;
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el requerimiento de compra.";
            }

            SessionErrors.add(actionRequest, "requerimiento-compra-error");
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);

            if (e instanceof ValidacionCompraException) {
                ValidacionCompraException validacion = (ValidacionCompraException) e;
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

                actionResponse.setRenderParameter(
                        "modo",
                        "alta"
                );

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

            actionResponse.setRenderParameter(
                    "compras_error",
                    "true"
            );

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null ? cmd : ""
            );
        }
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        /*
         * Cada render de la pantalla de edicion genera un token nuevo.
         * El JSP debe enviarlo en un hidden llamado compras_save_token.
         */
        generarTokenGuardadoCompra(renderRequest);

        try {
            int idRequerimientoCompra =
                    ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            Object idAttr =
                    renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);

            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            RequerimientoCompra requerimiento;

            if (idRequerimientoCompra > 0) {
                requerimiento =
                        BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                                idRequerimientoCompra
                        );

                if (requerimiento == null) {
                    throw new Exception("No se encontro el requerimiento de compra informado.");
                }
            } else {
                requerimiento = new RequerimientoCompra();

                int idSectorParam = ParamUtil.getInteger(renderRequest, "sector_id", 0);

                if (idSectorParam > 0) {
                    requerimiento.setIdSector(Integer.valueOf(idSectorParam));
                }
            }

            cargarCatalogos(renderRequest, requerimiento);

            boolean soloLectura = esModoSoloLectura(renderRequest);

            cargarAfiliadoRequerimiento(renderRequest, requerimiento);

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
        }

        if (esModoSoloLectura(renderRequest)) {
            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
        }

        if (esAltaRequerimiento(renderRequest)) {
            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ALTA_REQUERIMIENTO);
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
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

            session.setAttribute(
                    SESSION_COMPRAS_SAVE_TOKENS,
                    tokens
            );
        }

        renderRequest.setAttribute(
                ATTR_COMPRAS_SAVE_TOKEN,
                token
        );
    }

    private void consumirTokenGuardadoCompra(ActionRequest actionRequest)
            throws ValidacionCompraException {

        String tokenRequest = getParametroTrim(actionRequest, PARAM_COMPRAS_SAVE_TOKEN);

        PortletSession session = actionRequest.getPortletSession();

        synchronized (session) {
            Object tokensObj = session.getAttribute(SESSION_COMPRAS_SAVE_TOKENS);

            if (!(tokensObj instanceof Set)) {
                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla esta desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            Set tokens = (Set) tokensObj;

            if (WebKeysCompras.isEmpty(tokenRequest)
                    || !tokens.contains(tokenRequest)) {

                errorCampo(
                        "guardar",
                        "El requerimiento ya fue enviado o la pantalla esta desactualizada. "
                                + "Vuelva a cargar la pantalla antes de guardar nuevamente."
                );
            }

            tokens.remove(tokenRequest);

            if (tokens.isEmpty()) {
                session.removeAttribute(SESSION_COMPRAS_SAVE_TOKENS);
            } else {
                session.setAttribute(
                        SESSION_COMPRAS_SAVE_TOKENS,
                        tokens
                );
            }
        }
    }

    private boolean esModoSoloLectura(RenderRequest renderRequest) {
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        return "/compras/ver_requerimiento".equals(strutsAction)
                || "ver".equalsIgnoreCase(modo);
    }

    private void cargarCatalogos(RenderRequest request,
                                 RequerimientoCompra requerimiento) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );

        /*
         * Performance:
         * No precargar articulos en render.
         *
         * La lista puede ser grande y termina serializada en JS por
         * _detalle_scripts_comunes.jsp. Los articulos deben cargarse
         * bajo demanda por /compras/listar_articulos_sector.
         */
        request.setAttribute(
                ARTICULOS_COMPRA,
                new ArrayList<CompraArticulo>()
        );
    }

    private void cargarAfiliadoRequerimiento(RenderRequest renderRequest,
                                             RequerimientoCompra requerimiento) {

        renderRequest.removeAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);

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
        }
    }

    private void setIdRequerimientoEnRequest(ActionRequest request,
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

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    private void validarPermisoAnular(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS)) {

            errorCampo(
                    "permisos",
                    "No posee permisos para anular requerimientos de compras."
            );
        }
    }

    private RequerimientoCompra validarRequerimientoEditable(int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            return null;
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontro el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.isEditable()) {
            errorCampo(
                    "estado",
                    "Solo se pueden editar requerimientos en estado Borrador. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }

        return requerimiento;
    }

    private void validarRequerimientoPuedeAnular(int idRequerimientoCompra)
            throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontro el requerimiento de compra informado. ID recibido: "
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

    private void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento)
            throws Exception {

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
                        "Sector: el sector seleccionado no existe o no esta disponible. ID recibido: "
                                + requerimiento.getIdSector() + "."
                );
            }

            requerimiento.setSectorDescripcion(sector.getDescripcion());
            requerimiento.setRequiereAfiliado(sector.isRequiereAfiliado());

            /*
             * Si el sector no requiere afiliado, no hay reparto de cargos:
             * OSPIM 100%, tercerizadora 0%, recupero false.
             *
             * Importante:
             * No borrar idTercerizadora en requerimientos existentes.
             * Si se borra automaticamente, se pisan datos historicos.
             */
            if (!sector.isRequiereAfiliado()) {
                aplicarReglaSectorSinAfiliado(requerimiento);
                return;
            }
        }

        /*
         * Regla unica:
         * Si hay cualquier porcentaje a cargo de tercerizadora, hay recupero.
         */
        Integer cargoTercerizadora = requerimiento.getCargoTercerizadora();

        requerimiento.setRecupero(
                cargoTercerizadora != null && cargoTercerizadora.intValue() > 0
        );
    }

    private void validarCabecera(RequerimientoCompra requerimiento) throws Exception {
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
        validarPorcentaje(requerimiento.getCargoTercerizadora(), "Cargo tercerizadora");

        int cargoOspim = requerimiento.getCargoOspim() != null
                ? requerimiento.getCargoOspim().intValue()
                : 0;

        int cargoTercerizadora = requerimiento.getCargoTercerizadora() != null
                ? requerimiento.getCargoTercerizadora().intValue()
                : 0;

        int sumaCargos = cargoOspim + cargoTercerizadora;

        if (sumaCargos != 100) {
            errorCampo(
                    "cargo_tercerizadora",
                    "Cargos: la suma de Cargo OSPIM (" + cargoOspim
                            + ") y Cargo tercerizadora (" + cargoTercerizadora
                            + ") es " + sumaCargos + ". Debe ser exactamente 100."
            );
        }

        /*
         * Regla unica:
         * Recupero es true cuando Cargo tercerizadora es mayor a 0.
         */
        requerimiento.setRecupero(cargoTercerizadora > 0);

        /*
         * No se limpia tercerizadora automaticamente por cargos.
         * La tercerizadora queda como vino del afiliado/formulario
         * o como estaba guardada si el afiliado no cambio.
         *
         * Si hay cargo a tercerizadora, si se exige que exista tercerizadora.
         */
        if (requerimiento.isRequiereAfiliado()
                && cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {

            errorCampo(
                    "id_tercerizadora",
                    "Tercerizadora: debe seleccionar un afiliado con tercerizadora porque Cargo tercerizadora es mayor a 0."
            );
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(requerimiento.getAfiliadoCuilTitular())) {
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

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    label,
                    label + ": debe estar entre 0 y 100. Valor recibido: " + parsed + "."
            );
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request)
            throws Exception {

        RequerimientoCompra requerimiento = new RequerimientoCompra();

        int idRequerimientoCompra =
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                );

        requerimiento.setIdRequerimientoCompra(idRequerimientoCompra);

        int idSector = parseEnteroConDefault(request, "id_sector", "Sector", 0);

        if (idSector <= 0) {
            idSector = parseEnteroConDefault(request, "sector_id", "Sector", 0);
        }

        requerimiento.setIdSector(idSector > 0 ? Integer.valueOf(idSector) : null);

        requerimiento.setAfiliadoCuilTitular(
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(request, "afiliado_cuil_titular", null)
                )
        );

        String afiliadoIntRaw = getParametroTrim(request, "afiliado_int");

        if (WebKeysCompras.isEmpty(afiliadoIntRaw) || "-1".equals(afiliadoIntRaw)) {
            requerimiento.setAfiliadoInt(null);
        } else {
            requerimiento.setAfiliadoInt(
                    parseEnteroOpcional(request, "afiliado_int", "Afiliado - integrante")
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

        String idTercerizadora = getParametroTrim(request, "id_tercerizadora");

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(request, "requerimiento_id_tercerizadora_visible");
        }

        if (WebKeysCompras.isEmpty(idTercerizadora)) {
            idTercerizadora = getParametroTrim(request, "requerimiento_id_tercerizadora");
        }

        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            requerimiento.setIdTercerizadora(idTercerizadora.trim().toUpperCase());
        } else {
            requerimiento.setIdTercerizadora(null);
        }

        /*
         * Blindaje contra pisado de tercerizadora:
         * si el requerimiento ya existia y el afiliado no cambio,
         * se conserva la tercerizadora persistida.
         */
        preservarTercerizadoraExistenteSiNoCambioAfiliado(requerimiento);

        /*
         * Regla unica:
         * recupero true si hay cualquier cargo a tercerizadora.
         */
        requerimiento.setRecupero(
                cargoTercerizadora != null && cargoTercerizadora.intValue() > 0
        );

        requerimiento.setObservaciones(getParametroRaw(request, "observaciones", null));

        return requerimiento;
    }

    private void errorCampo(String campo, String mensaje)
            throws ValidacionCompraException {

        throw new ValidacionCompraException(campo, mensaje);
    }

    private String getParametroTrim(ActionRequest request, String nombre) {
        String value = getParametroRaw(request, nombre, null);

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private String getParametroRaw(ActionRequest request, String nombre, String defaultValue) {
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

            if (values.length == 0) {
                return defaultValue;
            }

            return values[0];
        }

        return String.valueOf(raw);
    }

    private Integer parseEnteroOpcional(ActionRequest request,
                                        String nombre,
                                        String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' no es un numero entero valido."
            );
        }

        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
        }

        return null;
    }

    private int parseEnteroConDefault(ActionRequest request,
                                      String nombre,
                                      String label,
                                      int defaultValue)
            throws ValidacionCompraException {

        Integer parsed = parseEnteroOpcional(request, nombre, label);

        if (parsed == null) {
            return defaultValue;
        }

        return parsed.intValue();
    }

    private Integer parsePorcentajeDesdeRequest(ActionRequest request,
                                                String nombre,
                                                String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            value = "0";
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un numero entero entre 0 y 100. Valor recibido: '"
                            + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
            return null;
        }

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    nombre,
                    label + ": debe estar entre 0 y 100. Valor recibido: " + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

    private void aplicarReglaSectorSinAfiliado(RequerimientoCompra requerimiento) {
        if (requerimiento == null) {
            return;
        }

        requerimiento.setAfiliadoCuilTitular(null);
        requerimiento.setAfiliadoInt(null);

        /*
         * En requerimientos nuevos sin afiliado, no corresponde guardar tercerizadora.
         * En requerimientos existentes, NO se limpia para evitar pisar datos historicos.
         */
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
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        requerimiento.getIdRequerimientoCompra()
                );

        if (existente == null) {
            return;
        }

        boolean mismoAfiliado =
                mismoTexto(
                        existente.getAfiliadoCuilTitular(),
                        requerimiento.getAfiliadoCuilTitular()
                )
                        && mismoInteger(
                        existente.getAfiliadoInt(),
                        requerimiento.getAfiliadoInt()
                );

        if (!mismoAfiliado) {
            return;
        }

        /*
         * Si el afiliado no cambio, no aceptamos que el componente visual
         * reemplace automaticamente CSA por MCE u otra tercerizadora.
         */
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
