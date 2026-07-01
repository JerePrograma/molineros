package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.ReclamoPrestacionalCompraPrecargaServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.List;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class IniciarReclamoPrestacionalCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    IniciarReclamoPrestacionalCompraAction.class
            );

    private static final String STRUTS_ACTION_RECLAMO =
            "/autorizaciones/editar_reclamosprestaciones_entry";

    private static final String STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";

    /*
     * Es el portlet-name real definido en portlet-ext.xml y
     * liferay-portlet-ext.xml.
     *
     * Como el portlet es instanceable, este valor es únicamente el root ID.
     * La navegación utiliza el ID completo encontrado en el layout:
     *
     * AUT_1_INSTANCE_<instanceId>
     */
    private static final String AUTORIZACIONES_ROOT_PORTLET_ID =
            "AUT_1";

    private static final String PARAM_ORIGEN =
            "origen";

    private static final String ORIGEN_COMPRAS =
            "compras";

    private static final String PARAM_ID_RECLAMO =
            "id_reclamosel";

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

        HttpSession session = null;

        ReclamoPrestacionalCompraContexto contextoHandoff = null;

        ReclamoPrestacionalCompraPrecargaServiceUtil.Precarga
                precargaHandoff = null;

        try {
            User user =
                    PortalUtil.getUser(actionRequest);

            validarPermisoCreacion(user);

            RequerimientoCompra requerimiento =
                    obtenerRequerimientoCotizado(
                            idRequerimientoCompra
                    );

            HttpServletRequest httpRequest =
                    PortalUtil.getHttpServletRequest(
                            actionRequest
                    );

            session =
                    httpRequest.getSession();

            String usuario =
                    user.getScreenName();

            contextoHandoff =
                    new ReclamoPrestacionalCompraContexto(
                            requerimiento.getIdRequerimientoCompra(),
                            requerimiento.getAfiliadoCuilTitular(),
                            requerimiento.getAfiliadoInt(),
                            usuario,
                            System.currentTimeMillis(),
                            UUID.randomUUID().toString()
                    );

            DestinoPortlet destino =
                    resolverDestinoAutorizaciones(
                            actionRequest
                    );

            String redirect =
                    construirURLAutorizaciones(
                            httpRequest,
                            destino,
                            contextoHandoff.getNonce(),
                            null
                    );

            /*
             * Registra solamente metadata temporal del requerimiento origen.
             *
             * No crea relación.
             * No reserva un RP.
             * No asigna idRequerimientoCompra como idReclamo.
             */
            registrarContextoBorrador(
                    session,
                    contextoHandoff
            );

            /*
             * Construye objetos Java en sesión.
             * No persiste el Reclamo Prestacional.
             */
            precargaHandoff =
                    ReclamoPrestacionalCompraPrecargaServiceUtil
                            .precargar(
                                    session,
                                    contextoHandoff.getNonce(),
                                    usuario
                            );

            actionResponse.sendRedirect(
                    redirect
            );

        } catch (Exception e) {
            if (session != null
                    && contextoHandoff != null) {

                ReclamoPrestacionalCompraPrecargaServiceUtil
                        .limpiarHandoffFallido(
                                session,
                                contextoHandoff.getNonce(),
                                precargaHandoff
                        );
            }

            _log.error(
                    "No se pudo precargar el borrador de Reclamo "
                            + "Prestacional desde Compras. "
                            + "idRequerimiento="
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
                        WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
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

    /**
     * Después de una redirección exitosa este método no interviene en el
     * render de Autorizaciones.
     * Se conserva solamente para el retorno de error dentro de COMPRA_1.
     */
    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        return mapping.findForward(
                WebKeysCompras
                        .FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private String construirURLAutorizaciones(
            HttpServletRequest httpRequest,
            DestinoPortlet destino,
            String nonce,
            Integer idReclamo) throws Exception {

        if (httpRequest == null) {
            throw new Exception(
                    "No se pudo obtener la petición HTTP para navegar "
                            + "al portlet de Autorizaciones."
            );
        }

        if (destino == null
                || WebKeysCompras.isEmpty(
                destino.getPortletId()
        )
                || destino.getPlid() <= 0L) {

            throw new Exception(
                    "No se pudo determinar el portlet de Autorizaciones "
                            + "y la página que lo contiene."
            );
        }

        PortletURL url =
                PortletURLFactoryUtil.create(
                        httpRequest,
                        destino.getPortletId(),
                        destino.getPlid(),
                        PortletRequest.RENDER_PHASE
                );

        if (url == null) {
            throw new Exception(
                    "Liferay no pudo construir la URL del portlet "
                            + "de Autorizaciones."
            );
        }

        url.setWindowState(
                WindowState.MAXIMIZED
        );

        url.setParameter(
                "struts_action",
                STRUTS_ACTION_RECLAMO
        );

        url.setParameter(
                PARAM_ORIGEN,
                ORIGEN_COMPRAS
        );

        if (!WebKeysCompras.isEmpty(nonce)) {
            url.setParameter(
                    WebKeysCompras
                            .PARAM_RECLAMO_PRESTACIONAL_NONCE,
                    nonce
            );
        }

        if (idReclamo != null
                && idReclamo.intValue() > 0) {

            url.setParameter(
                    Constants.CMD,
                    Constants.VIEW
            );

            url.setParameter(
                    PARAM_ID_RECLAMO,
                    String.valueOf(
                            idReclamo.intValue()
                    )
            );
        }

        return url.toString();
    }

    private DestinoPortlet resolverDestinoAutorizaciones(
            ActionRequest actionRequest) throws Exception {

        HttpServletRequest httpRequest =
                PortalUtil.getHttpServletRequest(
                        actionRequest
                );

        ThemeDisplay themeDisplay =
                (ThemeDisplay) httpRequest.getAttribute(
                        WebKeys.THEME_DISPLAY
                );

        if (themeDisplay == null
                || themeDisplay.getLayout() == null) {

            throw new Exception(
                    "No se pudo determinar la página actual de Liferay."
            );
        }

        Layout layoutActual =
                themeDisplay.getLayout();

        /*
         * Primera prioridad: la instancia colocada en la misma página.
         */
        DestinoPortlet destino =
                buscarDestinoEnLayout(
                        layoutActual
                );

        if (destino != null) {
            return destino;
        }

        /*
         * Segunda prioridad: otra página del mismo sitio y del mismo
         * tipo público/privado.
         */
        destino =
                buscarDestinoUnicoEnGrupo(
                        layoutActual.getGroupId(),
                        layoutActual.isPrivateLayout(),
                        layoutActual.getPlid()
                );

        if (destino != null) {
            return destino;
        }

        /*
         * Última alternativa: una página del mismo sitio con visibilidad
         * contraria.
         *
         * Esto permite Compras privada -> Autorizaciones pública, o a la
         * inversa, sin inventar un plid.
         */
        destino =
                buscarDestinoUnicoEnGrupo(
                        layoutActual.getGroupId(),
                        !layoutActual.isPrivateLayout(),
                        0L
                );

        if (destino != null) {
            return destino;
        }

        throw new Exception(
                "No se encontró una instancia de "
                        + AUTORIZACIONES_ROOT_PORTLET_ID
                        + " colocada en una página del sitio actual."
        );
    }

    private DestinoPortlet buscarDestinoUnicoEnGrupo(
            long groupId,
            boolean privateLayout,
            long plidExcluido) throws Exception {

        List<Layout> layouts =
                LayoutLocalServiceUtil.getLayouts(
                        groupId,
                        privateLayout,
                        LayoutConstants.TYPE_PORTLET
                );

        DestinoPortlet encontrado = null;

        if (layouts == null) {
            return null;
        }

        for (Layout layout : layouts) {
            if (layout == null
                    || layout.getPlid() == plidExcluido) {

                continue;
            }

            DestinoPortlet candidato =
                    buscarDestinoEnLayout(
                            layout
                    );

            if (candidato == null) {
                continue;
            }

            if (encontrado != null
                    && !encontrado.esMismoDestino(
                    candidato
            )) {

                throw new Exception(
                        "Se encontraron varias instancias del portlet "
                                + AUTORIZACIONES_ROOT_PORTLET_ID
                                + " en páginas del sitio. "
                                + "No es seguro seleccionar una "
                                + "automáticamente. Instancias: "
                                + encontrado.getPortletId()
                                + " en plid "
                                + encontrado.getPlid()
                                + " y "
                                + candidato.getPortletId()
                                + " en plid "
                                + candidato.getPlid()
                                + "."
                );
            }

            encontrado = candidato;
        }

        return encontrado;
    }

    private DestinoPortlet buscarDestinoEnLayout(
            Layout layout) throws Exception {

        if (layout == null
                || !(layout.getLayoutType()
                instanceof LayoutTypePortlet)) {

            return null;
        }

        LayoutTypePortlet layoutTypePortlet =
                (LayoutTypePortlet)
                        layout.getLayoutType();

        List<String> portletIds =
                layoutTypePortlet.getPortletIds();

        DestinoPortlet encontrado = null;

        if (portletIds == null) {
            return null;
        }

        for (String portletId : portletIds) {
            if (WebKeysCompras.isEmpty(portletId)) {
                continue;
            }

            String rootPortletId =
                    PortletConstants.getRootPortletId(
                            portletId
                    );

            if (!AUTORIZACIONES_ROOT_PORTLET_ID.equals(
                    rootPortletId
            )) {
                continue;
            }

            /*
             * Se consulta PortalUtil con el ID completo de la instancia.
             * Consultar solamente AUT_1 no encuentra necesariamente un
             * portlet instanceable colocado como AUT_1_INSTANCE_x.
             */
            long plidLocalizado =
                    PortalUtil.getPlidFromPortletId(
                            layout.getGroupId(),
                            layout.isPrivateLayout(),
                            portletId
                    );

            if (plidLocalizado <= 0L) {
                continue;
            }

            DestinoPortlet candidato =
                    new DestinoPortlet(
                            portletId,
                            layout.getPlid()
                    );

            if (encontrado != null
                    && !encontrado.esMismoDestino(
                    candidato
            )) {

                throw new Exception(
                        "La página con plid "
                                + layout.getPlid()
                                + " contiene más de una instancia de "
                                + AUTORIZACIONES_ROOT_PORTLET_ID
                                + ". No es seguro seleccionar una "
                                + "automáticamente."
                );
            }

            encontrado = candidato;
        }

        return encontrado;
    }

    private void registrarContextoBorrador(
            HttpSession session,
            ReclamoPrestacionalCompraContexto nuevoContexto)
            throws Exception {

        if (session == null) {
            throw new Exception(
                    "No se pudo obtener la sesión del usuario."
            );
        }

        if (nuevoContexto == null
                || WebKeysCompras.isEmpty(
                nuevoContexto.getNonce()
        )) {

            throw new Exception(
                    "No se pudo construir el contexto temporal de Compras."
            );
        }

        synchronized (session) {
            Object contextoAnteriorObj =
                    session.getAttribute(
                            WebKeysCompras
                                    .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                    );

            Object reclamoEnEdicion =
                    session.getAttribute(
                            WebKeysAutorizaciones
                                    .RECLAMO_PRESTACION_EN_EDICION
                    );

            /*
             * Si existe un RP en edición sin contexto de Compras, pertenece
             * al flujo normal de Autorizaciones. No se pisa silenciosamente.
             */
            if (reclamoEnEdicion != null
                    && !(contextoAnteriorObj
                    instanceof ReclamoPrestacionalCompraContexto)) {

                throw new Exception(
                        "Existe un Reclamo Prestacional en edición iniciado "
                                + "desde Autorizaciones. Finalícelo o descártelo "
                                + "antes de precargar uno desde Compras."
                );
            }

            /*
             * Si hay contexto de Compras, lo existente es solamente un
             * borrador temporal anterior. Se reemplaza por el requerimiento
             * seleccionado actualmente.
             */
            limpiarBorradorComprasEnSesion(
                    session
            );

            session.setAttribute(
                    WebKeysCompras
                            .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA,
                    nuevoContexto
            );
        }
    }

    private void limpiarBorradorComprasEnSesion(
            HttpSession session) {

        session.removeAttribute(
                WebKeysCompras
                        .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
        );

        session.removeAttribute(
                WebKeysAutorizaciones
                        .RECLAMO_PRESTACION_EN_EDICION
        );

        session.removeAttribute(
                WebKeysAutorizaciones
                        .LISTADO_PRESTACIONES_RECLAMOS_EN_SESION
        );

        session.removeAttribute(
                WebKeysAutorizaciones
                        .LISTADO_REVISIONES_RECLAMOS_EN_SESION
        );

        session.removeAttribute(
                WebKeysAutorizaciones
                        .LISTADO_CONTACTOS_RECLAMOS_EN_SESION
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

    private void validarPermisoCreacion(
            User user) throws Exception {

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

        if (!permisoCompras
                || !permisoReclamo) {

            throw new Exception(
                    "No posee permisos para crear un Reclamo "
                            + "Prestacional desde Compras."
            );
        }
    }

    private void validarUsuario(
            User user) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }
    }

    private String mensajeError(
            Exception e) {

        if (e == null
                || WebKeysCompras.isEmpty(
                e.getMessage()
        )) {

            return "No se pudo procesar el Reclamo Prestacional.";
        }

        return e.getMessage();
    }

    private static final class DestinoPortlet {

        private final String portletId;
        private final long plid;

        private DestinoPortlet(
                String portletId,
                long plid) {

            this.portletId = portletId;
            this.plid = plid;
        }

        private String getPortletId() {
            return portletId;
        }

        private long getPlid() {
            return plid;
        }

        private boolean esMismoDestino(
                DestinoPortlet otro) {

            return otro != null
                    && plid == otro.plid
                    && portletId != null
                    && portletId.equals(
                    otro.portletId
            );
        }
    }
}