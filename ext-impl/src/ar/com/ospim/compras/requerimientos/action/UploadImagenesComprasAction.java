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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class UploadImagenesComprasAction extends PortletAction {

    private static final Log logger =
            LogFactoryUtil.getLog(UploadImagenesComprasAction.class);

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

        String cmd = ParamUtil.getString(actionRequest, "imagen", null);

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

            cmd = ParamUtil.getString(uploadReq, "imagen", cmd);
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
                        "Debe guardar el requerimiento antes de subir archivos."
                );
                prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
                return;
            }

            validarPermisoABM(user);

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

            if (!requerimiento.isEditable()) {
                errorUpload(
                        actionRequest,
                        "Solo se pueden administrar archivos en requerimientos en estado Borrador. Estado actual: "
                                + requerimiento.getEstadoDescripcionVisible() + "."
                );
                prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
                return;
            }

            if (Constants.ADD.equals(cmd)) {
                subirArchivo(actionRequest, uploadReq, user, requerimiento);
            } else if (Constants.DELETE.equals(cmd)) {
                borrarArchivo(actionRequest, uploadReq, user, requerimiento);
            }

            prepararRetorno(actionRequest, actionResponse, idRequerimientoCompra, modo);
        } catch (Exception e) {
            logger.error(e);

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el archivo del requerimiento.";
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

        generarTokenGuardadoCompra(renderRequest);

        String modo = ParamUtil.getString(renderRequest, "modo", "");

        try {
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
                requerimiento = new RequerimientoCompra();
            }

            cargarCatalogos(renderRequest, requerimiento);
            cargarAfiliadoRequerimiento(renderRequest, requerimiento);

            boolean soloLectura =
                    "ver".equalsIgnoreCase(modo)
                            || "/compras/ver_requerimiento".equals(
                            ParamUtil.getString(renderRequest, "struts_action", "")
                    );

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.valueOf(soloLectura)
            );

            renderRequest.setAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento.getDetalles()
            );
        } catch (Exception e) {
            logger.error(e);

            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo cargar el requerimiento de compra luego de procesar el archivo.";
            }

            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);
        }

        if ("ver".equalsIgnoreCase(modo)) {
            return mapping.findForward(
                    getForward(renderRequest, WebKeysCompras.FORWARD_COMPRAS_VER_REQUERIMIENTO)
            );
        }

        return mapping.findForward(
                getForward(renderRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO)
        );
    }

    private void subirArchivo(ActionRequest actionRequest,
                              UploadPortletRequest uploadReq,
                              User user,
                              RequerimientoCompra requerimiento) throws Exception {

        File file = uploadReq.getFile("importa_imagenes");
        String filename = uploadReq.getFileName("importa_imagenes");
        String description = ParamUtil.getString(uploadReq, "descripcionFile", "");

        if (file == null
                || filename == null
                || filename.trim().length() == 0) {

            errorUpload(actionRequest, "Debe seleccionar un archivo.");
            return;
        }

        DLFolder folder = getFolderCompras();
        long folderId = folder.getFolderId();

        ServiceContext serviceContext =
                ServiceContextFactory.getInstance(
                        FileEntry.class.getName(),
                        actionRequest
                );

        Random rnd = new Random();

        String title;
        DLFileEntry existente;

        do {
            existente = null;

            title = WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                    requerimiento.getIdRequerimientoCompra()
            ) + (int) (rnd.nextDouble() * 100000);

            try {
                existente =
                        DLFileEntryLocalServiceUtil.getFileEntryByTitle(
                                folderId,
                                title
                        );
            } catch (Exception e) {
                existente = null;
            }
        } while (existente != null);

        try {
            DLFileEntry entry =
                    DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
                            serviceContext.getUserId(),
                            folderId,
                            filename,
                            filename,
                            title,
                            description,
                            "",
                            file,
                            serviceContext
                    );

            SessionMessages.add(actionRequest, "requerimiento-compra-archivo-guardado");

            logger.debug(
                    "AGREGAR ARCHIVO AL REQUERIMIENTO DE COMPRA: "
                            + requerimiento.getIdRequerimientoCompra()
                            + " - "
                            + entry.getName()
            );
        } catch (DuplicateFileException e) {
            logger.error(e);
            errorUpload(actionRequest, "El archivo se encuentra duplicado.");
        } catch (FileSizeException e) {
            logger.error(e);
            errorUpload(actionRequest, "El archivo a subir supera el tamaño permitido.");
        } catch (FileNameException e) {
            logger.error(e);
            errorUpload(actionRequest, "El tipo de archivo a subir no esta permitido.");
        } catch (Exception e) {
            logger.error(e);
            errorUpload(actionRequest, e.getMessage());
        }
    }

    private void borrarArchivo(ActionRequest actionRequest,
                               UploadPortletRequest uploadReq,
                               User user,
                               RequerimientoCompra requerimiento) throws Exception {

        long folderId = ParamUtil.getLong(uploadReq, "folderid", 0L);
        String name = ParamUtil.getString(uploadReq, "filename", "");

        if (folderId <= 0 || WebKeysCompras.isEmpty(name)) {
            errorUpload(actionRequest, "Debe informar el archivo a eliminar.");
            return;
        }

        try {
            DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);

            SessionMessages.add(actionRequest, "requerimiento-compra-archivo-borrado");

            logger.debug(
                    "BORRAR ARCHIVO DEL REQUERIMIENTO DE COMPRA: "
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
                    WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_REQUERIMIENTOS_COMPRAS
            );
        } catch (Exception e) {
            throw new Exception(
                    "No se encontro la carpeta de Document Library '"
                            + WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_REQUERIMIENTOS_COMPRAS
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
            mensaje = "No se pudo procesar el archivo.";
        }

        request.setAttribute("msgInsertError", mensaje);
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception(
                    "No posee permisos para administrar archivos de requerimientos de compra."
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

        List<CompraArticulo> articulos =
                EditarRequerimientoCompraServiceUtil.listarArticulos(
                        null,
                        null
                );

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