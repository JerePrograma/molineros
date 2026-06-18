package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
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
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import jcifs.smb.FileEntry;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class UploadPresupuestosComprasAction extends PortletAction {

    private static final Log logger =
            LogFactoryUtil.getLog(UploadPresupuestosComprasAction.class);

    private static final String ARTICULOS_COMPRA = "ARTICULOS_COMPRA";

    /*
     * Deben coincidir con EditarRequerimientoCompraAction.
     * Si no se genera token al volver del upload, el Guardar posterior falla.
     */
    private static final String ATTR_COMPRAS_SAVE_TOKEN =
            "COMPRAS_SAVE_TOKEN";

    private static final String SESSION_COMPRAS_SAVE_TOKENS =
            "COMPRAS_SAVE_TOKENS";

    private static final int MAX_TOKENS_GUARDADO_COMPRA = 20;

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, "presupuesto_accion", null);

        User user = PortalUtil.getUser(actionRequest);

        int idRequerimientoCompra =
                ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        String modo = ParamUtil.getString(actionRequest, "modo", "");

        try {
            /*
             * El form es multipart. Se usa UploadPortletRequest como en Reclamos.
             */
            UploadPortletRequest uploadReq =
                    PortalUtil.getUploadPortletRequest(actionRequest);

            cmd = ParamUtil.getString(uploadReq, "presupuesto_accion", cmd);
            modo = ParamUtil.getString(uploadReq, "modo", modo);

            idRequerimientoCompra =
                    ParamUtil.getInteger(
                            uploadReq,
                            "id_requerimiento_compra",
                            idRequerimientoCompra
                    );

            if (idRequerimientoCompra <= 0) {
                errorUpload(
                        actionRequest,
                        "Debe guardar y enviar a cotizar el requerimiento antes de subir presupuestos."
                );
                prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
                return;
            }

            validarPermisoCotizar(user);

            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                            idRequerimientoCompra
                    );

            if (requerimiento == null) {
                errorUpload(
                        actionRequest,
                        "No se encontro el requerimiento de compra informado. ID recibido: "
                                + idRequerimientoCompra + "."
                );
                prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
                return;
            }

            if (!requerimiento.puedeAdministrarPresupuestos()) {
                errorUpload(
                        actionRequest,
                        "Solo se pueden administrar presupuestos en estado A cotizar. Estado actual: "
                                + requerimiento.getEstadoDescripcionVisible() + "."
                );
                prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
                return;
            }

            if (Constants.ADD.equals(cmd)) {
                subirPresupuesto(
                        actionRequest,
                        uploadReq,
                        requerimiento
                );
            } else if (Constants.DELETE.equals(cmd)) {
                borrarPresupuesto(
                        actionRequest,
                        uploadReq,
                        requerimiento
                );
            } else {
                throw new Exception(
                        "La accion solicitada para el presupuesto no es valida."
                );
            }

            prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
        } catch (Exception e) {
            logger.error(e);

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el presupuesto del requerimiento.";
            }

            errorUpload(actionRequest, mensaje);
            prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
        }
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        String modo = ParamUtil.getString(renderRequest, "modo", "");
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");

        boolean soloLectura =
                "ver".equalsIgnoreCase(modo)
                        || "/compras/ver_requerimiento".equals(strutsAction);

        try {
            User user = PortalUtil.getUser(renderRequest);

            if (soloLectura) {
                validarPermisoConsulta(user);
            } else {
                validarPermisoCotizar(user);
            }

            int idRequerimientoCompra =
                    ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            RequerimientoCompra requerimiento = null;

            if (idRequerimientoCompra > 0) {
                requerimiento =
                        BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                                idRequerimientoCompra
                        );
            }

            if (requerimiento == null) {
                if (idRequerimientoCompra > 0) {
                    throw new Exception(
                            "No se encontro el requerimiento de compra informado."
                    );
                }

                requerimiento = new RequerimientoCompra();
            }

            if (!soloLectura && !requerimiento.puedeAdministrarPresupuestos()) {
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

            /*
             * Importante para el nuevo Tiles/JSP:
             * - Vista usa atributos EN_VIEW.
             * - Edicion usa atributos EN_EDICION.
             */
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
            logger.error(e);

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo cargar el requerimiento de compra luego de procesar el presupuesto.";
            }

            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);

            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }

        if (soloLectura) {
            return mapping.findForward(
                    getForward(renderRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO)
            );
        }

        return mapping.findForward(
                getForward(renderRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO)
        );
    }

    private void subirPresupuesto(ActionRequest actionRequest,
                              UploadPortletRequest uploadReq,
                              RequerimientoCompra requerimiento) throws Exception {

        File file = uploadReq.getFile("presupuesto");
        String filename = uploadReq.getFileName("presupuesto");
        String description = ParamUtil.getString(uploadReq, "descripcionFile", "");

        if (file == null
                || !file.exists()
                || file.length() <= 0
                || filename == null
                || filename.trim().length() == 0) {

            errorUpload(
                    actionRequest,
                    "Debe seleccionar un presupuesto valido."
            );

            return;
        }

        DLFolder folder = getFolderCompras();
        long folderId = folder.getFolderId();

        ServiceContext serviceContext =
                ServiceContextFactory.getInstance(
                        DLFileEntry.class.getName(),
                        actionRequest
                );

        String nombreOriginal =
                filename
                        .replace('\\', '/');

        int ultimaBarra =
                nombreOriginal.lastIndexOf('/');

        if (ultimaBarra >= 0
                && ultimaBarra < nombreOriginal.length() - 1) {

            nombreOriginal =
                    nombreOriginal.substring(
                            ultimaBarra + 1
                    );
        }

        String prefijo =
                WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                        requerimiento.getIdRequerimientoCompra()
                );

        String identificador =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");

        String extension = "";

        int posicionExtension =
                nombreOriginal.lastIndexOf('.');

        if (posicionExtension >= 0
                && posicionExtension < nombreOriginal.length() - 1) {

            extension =
                    nombreOriginal.substring(
                            posicionExtension
                    );

            /*
             * Evita nombres anormalmente largos o extensiones manipuladas.
             */
            if (extension.length() > 20) {
                extension = "";
            }
        }

        /*
         * El nombre físico debe ser único dentro de la carpeta.
         * addOrOverwriteFileEntry sobrescribe cuando el nombre ya existe.
         */
        String nombrePersistido =
                prefijo
                        + identificador
                        + extension;

        /*
         * El título se usa para vincular el documento con el requerimiento.
         * Debe conservar siempre el prefijo.
         */
        String sufijoTitulo =
                "_"
                        + identificador.substring(0, 8);

        int longitudDisponible =
                240
                        - prefijo.length()
                        - sufijoTitulo.length();

        String nombreTitulo =
                nombreOriginal;

        if (longitudDisponible > 0
                && nombreTitulo.length() > longitudDisponible) {

            nombreTitulo =
                    nombreTitulo.substring(
                            0,
                            longitudDisponible
                    );
        }

        String title =
                prefijo
                        + nombreTitulo
                        + sufijoTitulo;

        try {
            DLFileEntry entry =
                    DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
                            serviceContext.getUserId(),
                            folderId,
                            nombrePersistido,
                            nombreOriginal,
                            title,
                            description,
                            "",
                            file,
                            serviceContext
                    );

            SessionMessages.add(
                    actionRequest,
                    "requerimiento-compra-presupuesto-guardado"
            );

            logger.debug(
                    "AGREGAR PRESUPUESTO AL REQUERIMIENTO DE COMPRA: "
                            + requerimiento.getIdRequerimientoCompra()
                            + " - "
                            + entry.getName()
                            + " - "
                            + entry.getTitle()
            );
        } catch (DuplicateFileException e) {
            logger.error(e);

            errorUpload(
                    actionRequest,
                    "El presupuesto se encuentra duplicado."
            );
        } catch (FileSizeException e) {
            logger.error(e);

            errorUpload(
                    actionRequest,
                    "El presupuesto a subir supera el tamanio permitido."
            );
        } catch (FileNameException e) {
            logger.error(e);

            errorUpload(
                    actionRequest,
                    "El tipo de presupuesto a subir no esta permitido."
            );
        } catch (Exception e) {
            logger.error(e);

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje =
                        "No se pudo guardar el presupuesto.";
            }

            errorUpload(
                    actionRequest,
                    mensaje
            );
        }
    }

    private void borrarPresupuesto(ActionRequest actionRequest,
                               UploadPortletRequest uploadReq,
                               RequerimientoCompra requerimiento) throws Exception {

        long folderId = ParamUtil.getLong(uploadReq, "folderid", 0L);
        String name = ParamUtil.getString(uploadReq, "filename", "");
        String title = ParamUtil.getString(uploadReq, "filetitle", "");

        if (folderId <= 0
                || WebKeysCompras.isEmpty(name)
                || WebKeysCompras.isEmpty(title)) {

            errorUpload(actionRequest, "Debe informar el presupuesto a eliminar.");
            return;
        }

        try {
            DLFolder folderCompras = getFolderCompras();

            if (folderId != folderCompras.getFolderId()) {
                throw new Exception(
                        "El presupuesto informado no pertenece a la carpeta de Compras."
                );
            }

            String prefijoEsperado =
                    WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                            requerimiento.getIdRequerimientoCompra()
                    );

            DLFileEntry fileEntry =
                    DLFileEntryLocalServiceUtil.getFileEntryByTitle(
                            folderId,
                            title
                    );

            if (fileEntry == null
                    || fileEntry.getFolderId() != folderId
                    || !fileEntry.getTitle().startsWith(prefijoEsperado)) {

                throw new Exception(
                        "El presupuesto informado no pertenece al requerimiento actual."
                );
            }

            if (!name.equals(fileEntry.getName())) {
                throw new Exception(
                        "El presupuesto informado no coincide con el documento persistido."
                );
            }

            DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);

            SessionMessages.add(actionRequest, "requerimiento-compra-presupuesto-borrado");

            logger.debug(
                    "BORRAR PRESUPUESTO DEL REQUERIMIENTO DE COMPRA: "
                            + requerimiento.getIdRequerimientoCompra()
                            + " - "
                            + folderId
                            + " - "
                            + name
            );
        } catch (Exception e) {
            logger.error(e);
            errorUpload(actionRequest, e.getMessage());
        }
    }

    private DLFolder getFolderCompras() throws Exception {
        try {
            return DLFolderLocalServiceUtil.getFolder(
                    WebKeysCompras.DOCUMENT_LIBRARY_GROUP_ID_COMPRAS,
                    WebKeysCompras.DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                    WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
            );
        } catch (Exception e) {
            throw new Exception(
                    "No se encontro la carpeta de Document Library '"
                            + WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
                            + "'. Debe crearla bajo groupId "
                            + WebKeysCompras.DOCUMENT_LIBRARY_GROUP_ID_COMPRAS
                            + "."
            );
        }
    }

    private void prepararRetorno(ActionRequest request,
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

        if ("ver".equalsIgnoreCase(modo)) {
            response.setRenderParameter("modo", "ver");
            response.setRenderParameter("struts_action", "/compras/ver_requerimiento");
            setForward(request, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO);
        } else {
            response.setRenderParameter("struts_action", "/compras/editar_requerimiento");
            setForward(request, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        }
    }

    private void errorUpload(ActionRequest request, String mensaje) {
        SessionErrors.add(request, "errorUploadFile");

        if (WebKeysCompras.isEmpty(mensaje)) {
            mensaje = "No se pudo procesar el presupuesto.";
        }

        request.setAttribute("msgInsertError", mensaje);
        request.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);
    }

    private void validarPermisoConsulta(User user) throws Exception {
        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        boolean puedeVer =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_VIEW_COMPRAS
                );

        boolean puedeAdministrar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                );

        boolean puedeCotizar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                );

        boolean puedeAnular =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ANULAR_COMPRAS
                );

        if (!puedeVer
                && !puedeAdministrar
                && !puedeCotizar
                && !puedeAnular) {

            throw new Exception(
                    "No posee permisos para consultar presupuestos "
                            + "de requerimientos de compra."
            );
        }
    }

    private void validarPermisoCotizar(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
            throw new Exception(
                    "No posee permisos para administrar presupuestos de requerimientos de compra."
            );
        }
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

    private void cargarCatalogos(RenderRequest request,
                                 RequerimientoCompra requerimiento) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );

        Integer idSectorRequerimiento = null;

        if (requerimiento != null) {
            idSectorRequerimiento = requerimiento.getIdSector();
        }

        if ((idSectorRequerimiento == null || idSectorRequerimiento.intValue() <= 0)
                && request != null) {

            int idSectorParam = ParamUtil.getInteger(request, "sector_id", 0);

            if (idSectorParam > 0) {
                idSectorRequerimiento = Integer.valueOf(idSectorParam);
            }
        }

        List<CompraArticulo> articulos = new ArrayList<CompraArticulo>();

        /*
         * Performance:
         * No listar todos los articulos al volver de subir/borrar presupuestos.
         * Solo cargar articulos del sector del requerimiento.
         */
        if (idSectorRequerimiento != null && idSectorRequerimiento.intValue() > 0) {
            articulos =
                    EditarRequerimientoCompraServiceUtil.listarArticulos(
                            idSectorRequerimiento,
                            null
                    );
        }

        request.setAttribute(
                ARTICULOS_COMPRA,
                articulos
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
            logger.error(e);
        }
    }
}