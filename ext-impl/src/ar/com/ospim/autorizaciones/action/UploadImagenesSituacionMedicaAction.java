package ar.com.ospim.autorizaciones.action;


import java.io.File;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.StringUtils;
import jcifs.smb.FileEntry;

public class UploadImagenesSituacionMedicaAction extends PortletAction {

    private static Log logger = LogFactoryUtil.getLog(UploadImagenesSituacionMedicaAction.class);

    private static final long GROUP_ID = 10136L;
    private static final long PARENT_FOLDER_ID = 0L;
    private static final String FOLDER_NAME = "SituacionMedica";

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, "imagen", null);
        String solapa = ParamUtil.getString(actionRequest, "solapa", null);

        User user = PortalUtil.getUser(actionRequest);

        HttpSession session = PortalUtil.getHttpServletRequest(actionRequest).getSession();

        if (StringUtils.checkNotEmpty(cmd)) {

            UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(actionRequest);

            if (cmd.equals(Constants.ADD)) {

                Random rnd = new Random();

                ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

                ServiceContext serviceContext = ServiceContextFactory.getInstance(
                        FileEntry.class.getName(),
                        actionRequest
                );

                File file = uploadReq.getFile("importa_imagenes");
                String filename = uploadReq.getFileName("importa_imagenes");
                String description = ParamUtil.getString(uploadReq, "descripcionFile");

                String idSituacionMedica = ParamUtil.getString(uploadReq, "idSituacionMedica");

                if (idSituacionMedica == null || idSituacionMedica.trim().equals("") || "0".equals(idSituacionMedica)) {
                    idSituacionMedica = ParamUtil.getString(uploadReq, "id_registro_sitmed");
                }

                if (idSituacionMedica == null || idSituacionMedica.trim().equals("") || "0".equals(idSituacionMedica)) {
                    SituacionMedica situacionMedicaEnEdicion =
                        (SituacionMedica) uploadReq.getSession().getAttribute(WebKeysAutorizaciones.SITUACION_MEDICA_EN_EDICION);

                    if (situacionMedicaEnEdicion != null) {
                        idSituacionMedica = String.valueOf(situacionMedicaEnEdicion.getId_Situacion());
                    }
                }

                if (idSituacionMedica == null || idSituacionMedica.trim().equals("")) {
                    idSituacionMedica = "0";
                }

                try {

                    DLFolder folder = DLFolderLocalServiceUtil.getFolder(
                            GROUP_ID,
                            PARENT_FOLDER_ID,
                            FOLDER_NAME
                    );

                    long folderId = folder.getFolderId();

                    String title = "";
                    DLFileEntry dl = null;

                    do {
                        dl = null;

                        title = idSituacionMedica + "-" + (int) (rnd.nextDouble() * 100000);

                        try {
                            dl = DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title);
                        } catch (Exception e) {
                        }

                    } while (dl != null);

                    if (filename != null && !"".equalsIgnoreCase(filename)) {

                        try {

                            DLFileEntry entry = DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
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

                            logger.debug(
                                    "AGREGAR IMAGEN A SITUACION MEDICA. " +
                                    "Usuario: " + user.getScreenName() +
                                    " - FolderId: " + folderId +
                                    " - Name: " + entry.getName() +
                                    " - Title: " + entry.getTitle() +
                                    " - Description: " + entry.getDescription()
                            );

                        } catch (DuplicateFileException e) {

                            SessionErrors.add(actionRequest, "errorUploadFile");
                            actionRequest.setAttribute("msgInsertError", "El archivo se encuentra duplicado");
                            logger.error(e);

                        } catch (FileSizeException e) {

                            SessionErrors.add(actionRequest, "errorUploadFile");
                            actionRequest.setAttribute("msgInsertError", "El archivo a subir supera el tamaño permitido");
                            logger.error(e);

                        } catch (FileNameException e) {

                            SessionErrors.add(actionRequest, "errorUploadFile");
                            actionRequest.setAttribute("msgInsertError", "El tipo de archivo a subir no está permitido");
                            logger.error(e);

                        } catch (Exception e) {

                            SessionErrors.add(actionRequest, "errorUploadFile");
                            actionRequest.setAttribute("msgInsertError", e.getMessage());
                            logger.error(e);
                        }
                    }

                } catch (Exception e) {

                    SessionErrors.add(actionRequest, "errorUploadFile");
                    actionRequest.setAttribute(
                            "msgInsertError",
                            "No se encontró la carpeta de Document Library: " + FOLDER_NAME
                    );
                    logger.error(e);
                }
            }

            if (cmd.equals(Constants.DELETE)) {

                UploadPortletRequest uploadReqDelete = PortalUtil.getUploadPortletRequest(actionRequest);

                Long folderId = ParamUtil.getLong(uploadReqDelete, "folderid");
                String name = ParamUtil.getString(uploadReqDelete, "filename");

                try {

                    DLFileEntryLocalServiceUtil.deleteFileEntry(folderId, name);

                    logger.debug(
                            "BORRAR IMAGEN DE SITUACION MEDICA. " +
                            "Usuario: " + user.getScreenName() +
                            " - FolderId: " + folderId +
                            " - Name: " + name
                    );

                } catch (Exception e) {

                    SessionErrors.add(actionRequest, "errorUploadFile");
                    actionRequest.setAttribute("msgInsertError", e.getMessage());
                    logger.error(e);
                }
            }
        }

        actionRequest.setAttribute("tab", solapa);
        actionRequest.setAttribute(Constants.CMD, Constants.MOVE);
        
        setForward(actionRequest,"portlet.autorizaciones.patologias.editar_registro_situacionmedica_entry_imagen");
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {
    	
        return mapping.findForward(
                getForward(renderRequest,"portlet.autorizaciones.patologias.editar_registro_situacionmedica_entry_imagen")
        );
    }
}