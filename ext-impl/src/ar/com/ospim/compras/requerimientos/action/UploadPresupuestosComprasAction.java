package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.helper.PresupuestoCompraHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Adaptador HTTP de presupuestos.
 *
 * La validacion PDF, Document Library, compensacion y asociacion persistente
 * se encuentran en PresupuestoCompraHelper.
 */
public class UploadPresupuestosComprasAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    UploadPresupuestosComprasAction.class
            );

    private static final String MODO_VER = "ver";
    private static final String MODO_EDITAR = "editar";

    private static final String OPERACION_PRESUPUESTO_AGREGAR =
            "presupuestoAgregar";
    private static final String OPERACION_PRESUPUESTO_BORRAR =
            "presupuestoBorrar";
    private static final String OPERACION_PRESUPUESTO_ERROR =
            "presupuestoError";

    private static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";
    private static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";
    private static final int MAX_TOKENS_GUARDADO_COMPRA = 20;

    private final PresupuestoCompraHelper presupuestoHelper =
            new PresupuestoCompraHelper();

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String cmd =
                ParamUtil.getString(
                        actionRequest,
                        "presupuesto_accion",
                        null
                );

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        actionRequest,
                        "id_requerimiento_compra",
                        0
                );

        String modo =
                ParamUtil.getString(
                        actionRequest,
                        "modo",
                        ""
                );

        try {
            User user = PortalUtil.getUser(actionRequest);
            validarPermisoCotizar(user);

            UploadPortletRequest uploadReq =
                    PortalUtil.getUploadPortletRequest(
                            actionRequest
                    );

            cmd = ParamUtil.getString(
                    uploadReq,
                    "presupuesto_accion",
                    cmd
            );
            modo = ParamUtil.getString(
                    uploadReq,
                    "modo",
                    modo
            );
            idRequerimientoCompra = ParamUtil.getInteger(
                    uploadReq,
                    "id_requerimiento_compra",
                    idRequerimientoCompra
            );

            validarContextoEdicion(
                    idRequerimientoCompra,
                    modo
            );

            ServiceContext serviceContext =
                    ServiceContextFactory.getInstance(
                            DLFileEntry.class.getName(),
                            actionRequest
                    );

            String usuario = obtenerUsuarioAuditoria(user);

            if (Constants.ADD.equals(cmd)) {
                int cantidad =
                        ParamUtil.getInteger(
                                uploadReq,
                                "presupuesto_count",
                                0
                        );

                List<PresupuestoCompraHelper.PresupuestoEntrada> entradas =
                        leerEntradasPresupuesto(
                                uploadReq,
                                cantidad
                        );

                int guardados =
                        presupuestoHelper.guardarPresupuestos(
                                idRequerimientoCompra,
                                entradas,
                                serviceContext,
                                usuario
                        );

                actionResponse.setRenderParameter(
                        "presupuestos_guardados",
                        String.valueOf(guardados)
                );
                actionResponse.setRenderParameter(
                        "compras_operacion",
                        OPERACION_PRESUPUESTO_AGREGAR
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-presupuesto-guardado"
                );

            } else if (Constants.DELETE.equals(cmd)) {
                int idRequerimientoPresupuesto =
                        ParamUtil.getInteger(
                                uploadReq,
                                "id_requerimiento_presupuesto",
                                0
                        );

                presupuestoHelper.borrarPresupuesto(
                        idRequerimientoCompra,
                        idRequerimientoPresupuesto,
                        serviceContext.getScopeGroupId(),
                        usuario
                );

                actionResponse.setRenderParameter(
                        "compras_operacion",
                        OPERACION_PRESUPUESTO_BORRAR
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-presupuesto-borrado"
                );

            } else {
                throw new Exception(
                        "La accion solicitada para el presupuesto no es valida."
                );
            }

            prepararRetorno(
                    actionRequest,
                    actionResponse,
                    idRequerimientoCompra,
                    modo
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo procesar el presupuesto del requerimiento. "
                            + "idRequerimiento="
                            + idRequerimientoCompra,
                    e
            );

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo procesar el presupuesto del requerimiento.";
            }

            errorUpload(
                    actionRequest,
                    mensaje
            );

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    OPERACION_PRESUPUESTO_ERROR
            );

            prepararRetorno(
                    actionRequest,
                    actionResponse,
                    idRequerimientoCompra,
                    modo
            );
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        String modo =
                ParamUtil.getString(
                        renderRequest,
                        "modo",
                        ""
                );

        String strutsAction =
                ParamUtil.getString(
                        renderRequest,
                        "struts_action",
                        ""
                );

        boolean soloLectura =
                MODO_VER.equalsIgnoreCase(modo)
                        || "/compras/ver_requerimiento".equals(strutsAction);

        try {
            User user = PortalUtil.getUser(renderRequest);

            if (soloLectura) {
                validarPermisoConsulta(user);
            } else {
                validarPermisoCotizar(user);
            }

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            renderRequest,
                            "id_requerimiento_compra",
                            0
                    );

            RequerimientoCompra requerimiento =
                    idRequerimientoCompra > 0
                            ? BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                                    idRequerimientoCompra
                            )
                            : new RequerimientoCompra();

            if (idRequerimientoCompra > 0
                    && requerimiento == null) {
                throw new Exception(
                        "No se encontro el requerimiento de compra informado."
                );
            }

            if (!soloLectura
                    && !requerimiento.puedeAdministrarPresupuestos()) {
                soloLectura = true;
            }

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.valueOf(soloLectura)
            );

            if (!soloLectura) {
                generarTokenGuardadoCompra(renderRequest);
            }

            cargarCatalogos(renderRequest);
            cargarAfiliadoRequerimiento(
                    renderRequest,
                    requerimiento
            );
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

        } catch (Exception e) {
            _log.error(
                    "No se pudo cargar el requerimiento luego de procesar "
                            + "presupuestos.",
                    e
            );

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo cargar el requerimiento de compra.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ERROR
            );
        }

        return mapping.findForward(
                soloLectura
                        ? getForward(
                        renderRequest,
                        WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
                )
                        : getForward(
                        renderRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                )
        );
    }

    private List<PresupuestoCompraHelper.PresupuestoEntrada>
    leerEntradasPresupuesto(
            UploadPortletRequest uploadReq,
            int cantidad) {

        List<PresupuestoCompraHelper.PresupuestoEntrada> entradas =
                new ArrayList<PresupuestoCompraHelper.PresupuestoEntrada>();

        for (int i = 0; i < cantidad; i++) {
            String nombreParametro =
                    "presupuesto_" + i;

            entradas.add(
                    new PresupuestoCompraHelper.PresupuestoEntrada(
                            i,
                            uploadReq.getFile(nombreParametro),
                            uploadReq.getFileName(nombreParametro),
                            ParamUtil.getInteger(
                                    uploadReq,
                                    nombreParametro + "_id_prestador",
                                    0
                            )
                    )
            );
        }

        return entradas;
    }

    private void validarContextoEdicion(
            int idRequerimientoCompra,
            String modo) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe guardar y enviar a cotizar el requerimiento "
                            + "antes de administrar presupuestos."
            );
        }

        if (MODO_VER.equalsIgnoreCase(modo)) {
            throw new Exception(
                    "No se pueden administrar presupuestos en modo de solo lectura."
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
                        BusquedaRequerimientoCompraServiceUtil
                                .hayPrestadoresPendientesNotificacion(
                                        requerimiento
                                                .getIdRequerimientoCompra()
                                );
            } catch (Exception e) {
                _log.warn(
                        "No se pudo confirmar si quedan prestadores "
                                + "pendientes de notificacion.",
                        e
                );
            }
        }

        renderRequest.setAttribute(
                WebKeysCompras.HAY_PRESTADORES_PENDIENTES_NOTIFICACION,
                Boolean.valueOf(hayPendientes)
        );
    }

    private void prepararRetorno(
            ActionRequest request,
            ActionResponse response,
            int idRequerimientoCompra,
            String modo) {

        request.setAttribute(
                WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                Integer.valueOf(idRequerimientoCompra)
        );

        response.setRenderParameter(
                "id_requerimiento_compra",
                String.valueOf(idRequerimientoCompra)
        );

        if (MODO_VER.equalsIgnoreCase(modo)) {
            response.setRenderParameter("modo", MODO_VER);
            response.setRenderParameter(
                    "struts_action",
                    "/compras/ver_requerimiento"
            );
            setForward(
                    request,
                    WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO
            );
        } else {
            response.setRenderParameter("modo", MODO_EDITAR);
            response.setRenderParameter(
                    "struts_action",
                    "/compras/editar_requerimiento"
            );
            setForward(
                    request,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        }
    }

    private void errorUpload(
            ActionRequest request,
            String mensaje) {

        SessionErrors.add(
                request,
                "errorUploadFile"
        );

        if (WebKeysCompras.isEmpty(mensaje)) {
            mensaje = "No se pudo procesar el presupuesto.";
        }

        request.setAttribute(
                "msgInsertError",
                mensaje
        );
        request.setAttribute(
                WebKeysCompras.ERROR_PARA_ALERT,
                mensaje
        );
    }

    private void validarPermisoConsulta(User user) throws Exception {
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
                    "No posee permisos para consultar presupuestos "
                            + "de requerimientos de compra."
            );
        }
    }

    private void validarPermisoCotizar(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        )) {
            throw new Exception(
                    "No posee permisos para administrar presupuestos "
                            + "de requerimientos de compra."
            );
        }
    }

    private void generarTokenGuardadoCompra(
            RenderRequest renderRequest) {

        if (renderRequest == null) {
            return;
        }

        String token = UUID.randomUUID().toString();
        PortletSession session = renderRequest.getPortletSession();

        synchronized (session) {
            Set tokens = null;
            Object tokensObj =
                    session.getAttribute(
                            SESSION_COMPRAS_SAVE_TOKENS
                    );

            if (tokensObj instanceof Set) {
                tokens = (Set) tokensObj;
            }

            if (tokens == null
                    || tokens.size() >= MAX_TOKENS_GUARDADO_COMPRA) {
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

            if (afiliados != null && afiliados.size() == 1) {
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

    private String obtenerUsuarioAuditoria(User user) {
        if (user == null) {
            return "sistema";
        }

        String screenName = user.getScreenName();

        if (!WebKeysCompras.isEmpty(screenName)) {
            return screenName.trim();
        }

        return String.valueOf(user.getUserId());
    }

    /**
     * Tipos anidados legacy conservados para no romper subclases/callers
     * que compilen contra la API historica de esta Action. El flujo actual
     * no los utiliza: la implementacion vive en PresupuestoCompraHelper.
     */
    protected static class PresupuestoEntrada {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final int idPrestador;

        private PresupuestoEntrada(
                int indice,
                File archivo,
                String nombreOriginal,
                int idPrestador) {

            this.indice = indice;
            this.archivo = archivo;
            this.nombreOriginal = nombreOriginal;
            this.idPrestador = idPrestador;
        }
    }

    protected static class PresupuestoValidado {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final PrestadorCotizacion prestador;
        private final String nombrePersistido;
        private final String titulo;
        private final String descripcionPrestador;

        private PresupuestoValidado(
                int indice,
                File archivo,
                String nombreOriginal,
                PrestadorCotizacion prestador,
                String nombrePersistido,
                String titulo,
                String descripcionPrestador) {

            this.indice = indice;
            this.archivo = archivo;
            this.nombreOriginal = nombreOriginal;
            this.prestador = prestador;
            this.nombrePersistido = nombrePersistido;
            this.titulo = titulo;
            this.descripcionPrestador = descripcionPrestador;
        }

        public int getIndice() {
            return indice;
        }

        public File getArchivo() {
            return archivo;
        }

        public String getNombreOriginal() {
            return nombreOriginal;
        }

        public PrestadorCotizacion getPrestador() {
            return prestador;
        }

        public int getIdPrestador() {
            return prestador != null
                    ? prestador.getIdPrestador()
                    : 0;
        }

        public String getNombrePersistido() {
            return nombrePersistido;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getDescripcionPrestador() {
            return descripcionPrestador;
        }
    }

    /**
     * Contrato publico legacy conservado para callers que referencien
     * el descriptor documental anidado de esta Action.
     *
     * La implementacion funcional nueva utiliza PresupuestoCompraHelper;
     * esta clase queda exclusivamente como tipo de compatibilidad.
     */
    public static class DocumentoPresupuestoCreado {

        private final long groupId;
        private final long folderId;
        private final long fileEntryId;
        private final String uuid;
        private final String nombre;
        private final String titulo;

        protected DocumentoPresupuestoCreado(
                long groupId,
                long folderId,
                long fileEntryId,
                String uuid,
                String nombre,
                String titulo) {

            this.groupId = groupId;
            this.folderId = folderId;
            this.fileEntryId = fileEntryId;
            this.uuid = uuid;
            this.nombre = nombre;
            this.titulo = titulo;
        }

        public long getGroupId() {
            return groupId;
        }

        public long getFolderId() {
            return folderId;
        }

        public long getFileEntryId() {
            return fileEntryId;
        }

        public String getUuid() {
            return uuid;
        }

        public String getNombre() {
            return nombre;
        }

        public String getTitulo() {
            return titulo;
        }
    }

}
