package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
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

/**
 * Inicia la edición de un Reclamo Prestacional nuevo a partir de un
 * requerimiento de compra COTIZADO.
 *
 * Esta acción no inserta el Reclamo Prestacional y no crea ni reserva una
 * relación persistente entre Compras y Autorizaciones. Sólo construye un
 * borrador temporal en sesión y redirige al formulario de alta del portlet
 * de Autorizaciones.
 */
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
     * Portlet-name raíz definido en portlet-ext.xml y
     * liferay-portlet-ext.xml.
     *
     * Al ser instanceable, la navegación utiliza el ID completo encontrado
     * en el layout: AUT_1_INSTANCE_<instanceId>.
     */
    private static final String AUTORIZACIONES_ROOT_PORTLET_ID =
            "AUT_1";

    private static final String PARAM_ORIGEN =
            "origen";

    private static final String ORIGEN_COMPRAS =
            "compras";

    private static final String PARAM_ID_RECLAMO =
            "id_reclamosel";

    private final RequerimientoCompraReclamoPrestacionalHelper
            reclamoHelper =
            new RequerimientoCompraReclamoPrestacionalHelper();

    @Override
    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        actionRequest,
                        WebKeysCompras
                                .PARAM_ID_REQUERIMIENTO_COMPRA,
                        0
                );

        HttpSession session = null;

        ReclamoPrestacionalCompraContexto contextoHandoff =
                null;

        ReclamoPrestacionalCompraPrecargaHelper.Precarga
                precargaHandoff = null;

        if (ParamUtil.getBoolean(actionRequest, "compras_conservar_campos", false)) {
            actionResponse.setRenderParameter("compras_guardado", "false");
            actionResponse.setRenderParameter("compras_error", "false");
            actionResponse.setRenderParameter("compras_destino", "");
        }

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            HttpServletRequest httpRequest =
                    PortalUtil.getHttpServletRequest(
                            actionRequest
                    );

            if (httpRequest == null) {
                throw new Exception(
                        "No se pudo obtener la petición HTTP actual."
                );
            }

            session =
                    httpRequest.getSession();

            String usuario =
                    user != null
                            ? user.getScreenName()
                            : "";

            RequerimientoCompra requerimiento =
                    obtenerRequerimientoActivo(
                            idRequerimientoCompra
                    );

            RequerimientoCompraReclamoPrestacional relacion =
                    reclamoHelper
                            .obtenerPorRequerimiento(
                                    idRequerimientoCompra
                            );

            /*
             * Si el requerimiento ya tiene un RP realmente persistido y
             * vinculado, no se crea otro.
             *
             * La consulta abre directamente el reclamo vinculado y descarta
             * únicamente el estado temporal del editor anterior.
             */
            if (relacion != null
                    && relacion.isVinculado()) {

                validarRequerimientoParaConsultarReclamo(
                        requerimiento
                );

                validarPermisoConsulta(
                        user
                );

                int idReclamo =
                        relacion.getIdReclamoPrestacionalInt();

                if (idReclamo <= 0) {
                    throw new Exception(
                            "La relación de Compras está vinculada, "
                                    + "pero no contiene un identificador "
                                    + "válido de Reclamo Prestacional."
                    );
                }

                DestinoPortlet destino =
                        resolverDestinoAutorizaciones(
                                actionRequest
                        );

                String redirect =
                        construirURLAutorizaciones(
                                httpRequest,
                                destino,
                                null,
                                Integer.valueOf(
                                        idReclamo
                                )
                        );

                ReclamoPrestacionalCompraPrecargaHelper
                        .prepararSesionParaConsulta(
                                session,
                                idRequerimientoCompra,
                                idReclamo
                        );

                completarNavegacionReclamo(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra,
                        redirect
                );

                return;
            }

            /*
             * Una reserva o un ERROR persistente siguen siendo autoritativos.
             * El hecho de descartar memoria de sesión no habilita duplicar
             * Reclamos Prestacionales en base de datos.
             */
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

            /*
             * A partir de aquí se trata inequívocamente de un ALTA NUEVA.
             */
            validarRequerimientoParaCrearReclamo(
                    requerimiento
            );

            validarPermisoCreacion(
                    user
            );

            /*
             * Antes de destruir cualquier estado anterior se resuelve todo
             * aquello que puede fallar sin necesidad de modificar la sesión.
             */
            DestinoPortlet destino =
                    resolverDestinoAutorizaciones(
                            actionRequest
                    );

            contextoHandoff =
                    new ReclamoPrestacionalCompraContexto(
                            requerimiento
                                    .getIdRequerimientoCompra(),
                            requerimiento
                                    .getAfiliadoCuilTitular(),
                            requerimiento
                                    .getAfiliadoInt(),
                            usuario,
                            System.currentTimeMillis(),
                            UUID.randomUUID().toString(),
                            requerimiento
                                    .getCargoOspim(),
                            requerimiento
                                    .getCargoTercerizadora(),
                            requerimiento
                                    .isRecupero(),
                            requerimiento
                                    .isSurge()
                    );

            String redirect =
                    construirURLAutorizaciones(
                            httpRequest,
                            destino,
                            contextoHandoff.getNonce(),
                            null
                    );

            /*
             * Regla funcional obligatoria:
             *
             * CREAR RP desde Compras es autoritativo.
             *
             * Cualquier cabecera, borrador, lista parcial, selección,
             * contexto anterior de Compras o metadata de recuperación
             * perteneciente al editor de Reclamos Prestacionales se descarta.
             *
             * No se elimina ningún RP persistido de la base de datos.
             *
             * La limpieza y la instalación del nuevo contexto ocurren dentro
             * del mismo synchronized(session), evitando ventanas de carrera.
             */
            ReclamoPrestacionalCompraPrecargaHelper
                    .reiniciarYRegistrarContextoDesdeCompras(
                            session,
                            contextoHandoff
                    );

            _log.info(
                    "Se descarto el estado transitorio previo del editor de "
                            + "Reclamo Prestacional y se inicio una nueva "
                            + "precarga autoritativa desde Compras. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", nonce="
                            + contextoHandoff.getNonce()
            );

            /*
             * Construye la nueva cabecera temporal y sus prestaciones.
             *
             * precargar() vuelve a validar que ninguna petición concurrente
             * haya contaminado la sesión entre el reinicio y esta escritura.
             */
            precargaHandoff =
                    ReclamoPrestacionalCompraPrecargaHelper
                            .precargar(
                                    session,
                                    contextoHandoff.getNonce(),
                                    usuario
                            );

            completarNavegacionReclamo(
                    actionRequest,
                    actionResponse,
                    idRequerimientoCompra,
                    redirect
            );

        } catch (Exception e) {
            actionResponse.setRenderParameter("compras_guardado", "false");
            actionResponse.setRenderParameter("compras_error", "true");

            /*
             * Sólo compensa el handoff NUEVO que esta petición alcanzó a
             * escribir. El estado anterior descartado deliberadamente no
             * debe restaurarse.
             */
            if (session != null
                    && contextoHandoff != null) {

                ReclamoPrestacionalCompraPrecargaHelper
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
                    mensajeError(
                            e
                    )
            );

            actionResponse.setRenderParameter(
                    "struts_action",
                    STRUTS_ACTION_VER_REQUERIMIENTO
            );

            if (idRequerimientoCompra > 0) {

                actionResponse.setRenderParameter(
                        WebKeysCompras
                                .PARAM_ID_REQUERIMIENTO_COMPRA,
                        String.valueOf(
                                idRequerimientoCompra
                        )
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
     * Después de una redirección exitosa no interviene en el render del
     * portlet de Autorizaciones. Se conserva para el retorno de error dentro
     * de COMPRA_1.
     */
    @Override
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

    private void completarNavegacionReclamo(
            ActionRequest actionRequest,
            ActionResponse actionResponse,
            int idRequerimientoCompra,
            String destino) throws Exception {

        if (!ParamUtil.getBoolean(actionRequest, "compras_conservar_campos", false)) {
            actionResponse.sendRedirect(destino);
            return;
        }

        actionResponse.setRenderParameter("compras_guardado", "true");
        actionResponse.setRenderParameter("compras_error", "false");
        actionResponse.setRenderParameter("compras_destino", destino);
        actionResponse.setRenderParameter("struts_action", STRUTS_ACTION_VER_REQUERIMIENTO);
        actionResponse.setRenderParameter(
                WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
                String.valueOf(idRequerimientoCompra)
        );
        setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
    }

    /**
     * Construye la URL de alta o de consulta del RP asociado.
     */
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

        if (!WebKeysCompras.isEmpty(
                nonce
        )) {
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

        } else {
            /*
             * Es un alta nueva. El ID del requerimiento no se envía como
             * id_reclamosel y nunca se utiliza como identificador de RP.
             */
            url.setParameter(
                    Constants.CMD,
                    Constants.ADD
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

        if (httpRequest == null) {
            throw new Exception(
                    "No se pudo obtener la petición HTTP actual."
            );
        }

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
         * Primera prioridad: instancia colocada en la misma página.
         */
        DestinoPortlet destino =
                buscarDestinoEnLayout(
                        layoutActual
                );

        if (destino != null) {
            return destino;
        }

        /*
         * Segunda prioridad: otra página del mismo sitio y misma
         * visibilidad pública/privada.
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
         * Última alternativa: página del mismo sitio con visibilidad
         * contraria.
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

        DestinoPortlet encontrado =
                null;

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

            encontrado =
                    candidato;
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

        DestinoPortlet encontrado =
                null;

        if (portletIds == null) {
            return null;
        }

        for (String portletId : portletIds) {
            if (WebKeysCompras.isEmpty(
                    portletId
            )) {
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

            encontrado =
                    candidato;
        }

        return encontrado;
    }

    /**
     * Recupera el requerimiento activo utilizado como origen de la
     * navegación hacia Reclamos Prestacionales.
     */
    private RequerimientoCompra obtenerRequerimientoActivo(
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

        return requerimiento;
    }

    private void validarRequerimientoParaCrearReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null
                || !WebKeysCompras.esCotizado(
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
    }

    private void validarRequerimientoParaConsultarReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null
                || (
                        !WebKeysCompras.puedeConsultarReclamoPrestacional(
                                requerimiento.getEstado()
                        )
                )) {

            throw new Exception(
                    "El Reclamo Prestacional asociado no puede "
                            + "consultarse desde el estado actual "
                            + "del requerimiento."
            );
        }
    }

    private void validarPermisoCreacion(
            User user) throws Exception {

        validarUsuario(
                user
        );

        boolean permisoCompras =
                PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolAbmCompras()
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolCotizarCompras()
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

    private void validarPermisoConsulta(
            User user) throws Exception {

        validarUsuario(
                user
        );

        boolean permisoCompras =
                PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolViewCompras()
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolAbmCompras()
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolCotizarCompras()
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        ConfiguracionComprasHelper.getRolAutorizarCompras()
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

        if (!permisoCompras
                || !permisoReclamo) {

            throw new Exception(
                    "No posee permisos para consultar el Reclamo "
                            + "Prestacional asociado."
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

            this.portletId =
                    portletId;

            this.plid =
                    plid;
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
