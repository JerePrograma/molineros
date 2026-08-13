package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;
import com.liferay.util.servlet.ServletResponseUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import java.io.InputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletResponse;

public class DescargarOrdenMedicaCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    DescargarOrdenMedicaCompraAction.class
            );

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        InputStream input =
                null;

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            validarPermisoConsulta(
                    user
            );

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            actionRequest,
                            "id_requerimiento_compra",
                            0
                    );

            if (idRequerimientoCompra <= 0) {
                throw new Exception(
                        "Debe informar el requerimiento de compra."
                );
            }

            long dlFileEntryIdSolicitado =
                    ParamUtil.getLong(
                            actionRequest,
                            "dl_file_entry_id",
                            0L
                    );

            RequerimientoCompraPresupuesto ordenMedica =
                    resolverOrdenMedica(
                            idRequerimientoCompra,
                            dlFileEntryIdSolicitado
                    );

            validarRelacionOrdenMedica(
                    ordenMedica,
                    idRequerimientoCompra
            );

            DLFileEntry entry =
                    DLFileEntryLocalServiceUtil
                            .getDLFileEntry(
                                    ordenMedica
                                            .getDlFileEntryId()
                                            .longValue()
                            );

            DocumentoComprasCreado identidad =
                    crearIdentidad(
                            ordenMedica
                    );

            DocumentoLibraryComprasHelper gestorDocumento =
                    DocumentoLibraryComprasHelper.crear(
                            actionRequest
                    );

            gestorDocumento.validarIdentidadDocumento(
                    identidad
            );

            if (!gestorDocumento.coincideIdentidad(
                    identidad,
                    entry
            )) {

                throw new Exception(
                        "La identidad de la Orden médica "
                                + "no coincide con Document Library."
                );
            }

            ThemeDisplay themeDisplay =
                    (ThemeDisplay)
                            actionRequest.getAttribute(
                                    WebKeys.THEME_DISPLAY
                            );

            if (themeDisplay == null
                    || entry.getGroupId()
                    != themeDisplay.getScopeGroupId()) {

                throw new Exception(
                        "La Orden médica no pertenece "
                                + "al sitio actual."
                );
            }

            DLFileEntryPermission.check(
                    themeDisplay.getPermissionChecker(),
                    entry.getFolderId(),
                    entry.getName(),
                    ActionKeys.VIEW
            );

            String nombreDescarga =
                    obtenerNombreDescarga(
                            ordenMedica.getNombreOriginal(),
                            entry.getTitleWithExtension()
                    );

            String contentType =
                    MimeTypesUtil.getContentType(
                            nombreDescarga
                    );

            if (!"image/jpeg".equals(contentType)
                    && !"image/png".equals(contentType)) {

                throw new Exception(
                        "El tipo de archivo de la Orden médica "
                                + "no es v\u00e1lido."
                );
            }

            input =
                    DLFileEntryLocalServiceUtil
                            .getFileAsStream(
                                    themeDisplay.getCompanyId(),
                                    themeDisplay.getUserId(),
                                    entry.getFolderId(),
                                    entry.getName(),
                                    entry.getVersion()
                            );

            HttpServletResponse response =
                    PortalUtil.getHttpServletResponse(
                            actionResponse
                    );

            ServletResponseUtil.sendFile(
                    response,
                    nombreDescarga,
                    input,
                    entry.getSize(),
                    contentType
            );

            input =
                    null;

            setForward(
                    actionRequest,
                    ActionConstants.COMMON_NULL
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo descargar de forma segura "
                            + "la Orden médica de Compras.",
                    e
            );

            PortalUtil.sendError(
                    e,
                    actionRequest,
                    actionResponse
            );

        } finally {
            ServletResponseUtil.cleanUp(
                    input
            );
        }
    }

    /*
     * Si la URL nueva identifica un fileEntry concreto, se busca
     * exactamente esa Orden médica dentro del requerimiento.
     *
     * Si no viene ese parámetro se conserva el comportamiento
     * histórico mediante getOrdenMedica(idRequerimientoCompra).
     */
    private RequerimientoCompraPresupuesto resolverOrdenMedica(
            int idRequerimientoCompra,
            long dlFileEntryIdSolicitado) throws Exception {

        if (dlFileEntryIdSolicitado <= 0L) {
            return BusquedaRequerimientoCompraServiceUtil
                    .getOrdenMedica(
                            idRequerimientoCompra
                    );
        }

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                BusquedaRequerimientoCompraServiceUtil
                        .listarOrdenesMedicas(
                                idRequerimientoCompra
                        );

        if (ordenesMedicas != null) {
            for (int i = 0;
                 i < ordenesMedicas.size();
                 i++) {

                RequerimientoCompraPresupuesto ordenMedica =
                        ordenesMedicas.get(i);

                if (ordenMedica == null
                        || ordenMedica.getDlFileEntryId() == null) {

                    continue;
                }

                if (ordenMedica.getDlFileEntryId().longValue()
                        == dlFileEntryIdSolicitado) {

                    return ordenMedica;
                }
            }
        }

        throw new Exception(
                "La Orden médica solicitada "
                        + "no pertenece al requerimiento informado."
        );
    }

    private void validarRelacionOrdenMedica(
            RequerimientoCompraPresupuesto ordenMedica,
            int idRequerimientoCompra) throws Exception {

        if (ordenMedica == null
                || ordenMedica.getIdRequerimiento() == null
                || ordenMedica.getIdRequerimiento().intValue()
                != idRequerimientoCompra
                || ordenMedica.getTipoDocumento() == null
                || ordenMedica.getTipoDocumento().intValue()
                != RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_ORDEN_MEDICA
                || ordenMedica.getIdPrestador() != null
                || !ordenMedica.isActivo()
                || ordenMedica.getFechaDocumento() == null
                || ordenMedica.getDlGroupId() == null
                || ordenMedica.getDlFolderId() == null
                || ordenMedica.getDlFileEntryId() == null
                || WebKeysCompras.isEmpty(
                ordenMedica.getDlFileUuid()
        )
                || WebKeysCompras.isEmpty(
                ordenMedica.getNombrePersistido()
        )
                || !DocumentoLibraryComprasHelper
                .TITULO_ORDEN_MEDICA
                .equals(
                        ordenMedica.getTitulo()
                )) {

            throw new Exception(
                    "No existe una Orden médica activa "
                            + "y v\u00e1lida para el requerimiento."
            );
        }
    }

    private DocumentoComprasCreado crearIdentidad(
            RequerimientoCompraPresupuesto ordenMedica) {

        return new DocumentoComprasCreado(
                ordenMedica.getDlGroupId().longValue(),
                ordenMedica.getDlFolderId().longValue(),
                ordenMedica.getDlFileEntryId().longValue(),
                ordenMedica.getDlFileUuid(),
                ordenMedica.getNombrePersistido(),
                ordenMedica.getTitulo()
        );
    }

    private String obtenerNombreDescarga(
            String nombreOriginal,
            String nombreFallback) {

        String nombre =
                !WebKeysCompras.isEmpty(
                        nombreOriginal
                )
                        ? nombreOriginal
                        : nombreFallback;

        if (nombre == null) {
            nombre =
                    "orden-medica";
        }

        nombre =
                nombre.replace(
                        '\\',
                        '_'
                ).replace(
                        '/',
                        '_'
                );

        nombre =
                nombre.replace(
                        '\r',
                        '_'
                ).replace(
                        '\n',
                        '_'
                );

        nombre =
                nombre.replace(
                        '"',
                        '_'
                ).trim();

        return nombre.length() > 0
                ? nombre
                : "orden-medica";
    }

    private void validarPermisoConsulta(
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
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ANULAR_COMPRAS
                );

        if (!permitido) {
            throw new Exception(
                    "No posee permisos para consultar "
                            + "documentos de Compras."
            );
        }
    }

    protected boolean isCheckMethodOnProcessAction() {
        return false;
    }
}