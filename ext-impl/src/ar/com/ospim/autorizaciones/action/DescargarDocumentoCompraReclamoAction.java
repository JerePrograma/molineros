package ar.com.ospim.autorizaciones.action;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraReclamoPrestacionalServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletResponse;

public class DescargarDocumentoCompraReclamoAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    DescargarDocumentoCompraReclamoAction.class
            );

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse)
            throws Exception {

        InputStream input = null;

        int idReclamoPrestacional = 0;
        int idRequerimientoPresupuesto = 0;

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            validarPermisoReclamoPrestacional(
                    user
            );

            idReclamoPrestacional =
                    ParamUtil.getInteger(
                            actionRequest,
                            "id_reclamo_prestacional",
                            0
                    );

            idRequerimientoPresupuesto =
                    ParamUtil.getInteger(
                            actionRequest,
                            "id_requerimiento_presupuesto",
                            0
                    );

            if (idReclamoPrestacional <= 0
                    || idRequerimientoPresupuesto <= 0) {

                throw new Exception(
                        "Debe informar el Reclamo Prestacional "
                                + "y el documento de Compras."
                );
            }

            RequerimientoCompraReclamoPrestacional relacion =
                    RequerimientoCompraReclamoPrestacionalServiceUtil
                            .getRelacionPorReclamoPrestacional(
                                    idReclamoPrestacional
                            );

            validarRelacion(
                    relacion,
                    idReclamoPrestacional
            );

            validarReclamoPersistido(
                    idReclamoPrestacional
            );

            int idRequerimientoCompra =
                    relacion.getIdRequerimientoCompra();

            RequerimientoCompraPresupuesto documento =
                    resolverDocumentoAutorizado(
                            idRequerimientoCompra,
                            idRequerimientoPresupuesto
                    );

            validarDocumento(
                    documento,
                    idRequerimientoCompra
            );

            ThemeDisplay themeDisplay =
                    (ThemeDisplay)
                            actionRequest.getAttribute(
                                    WebKeys.THEME_DISPLAY
                            );

            if (themeDisplay == null) {
                throw new Exception(
                        "No se pudo determinar el contexto del portal."
                );
            }

            DLFileEntry entry =
                    DLFileEntryLocalServiceUtil
                            .getDLFileEntry(
                                    documento
                                            .getDlFileEntryId()
                                            .longValue()
                            );

            DocumentoLibraryComprasHelper
                    .validarIdentidadAsociacionDocumento(
                            documento
                    );

            if (entry == null
                    || entry.getCompanyId()
                    != themeDisplay.getCompanyId()
                    || !DocumentoLibraryComprasHelper
                    .coincideIdentidadAsociacionDocumento(
                            documento,
                            entry
                    )) {

                throw new Exception(
                        "La identidad del documento de Compras "
                                + "no coincide con Document Library."
                );
            }

            if (entry.getGroupId()
                    != themeDisplay.getScopeGroupId()) {

                throw new Exception(
                        "El documento de Compras "
                                + "no pertenece al sitio actual."
                );
            }

            DLFileEntryPermission.check(
                    themeDisplay.getPermissionChecker(),
                    entry.getFolderId(),
                    entry.getName(),
                    ActionKeys.VIEW
            );

            String nombreDescarga =
                    validarNombreDescarga(
                            documento.getNombreOriginal()
                    );

            /*
             * Lee directamente desde DL sin atravesar
             * DLFileRank/Counter.
             */
            byte[] contenido =
                    DocumentoLibraryComprasHelper
                            .leerContenidoDocumentLibrary(
                                    entry
                            );

            String contentType =
                    validarContenidoDocumento(
                            documento,
                            nombreDescarga,
                            contenido
                    );

            input =
                    new ByteArrayInputStream(
                            contenido
                    );

            HttpServletResponse response =
                    PortalUtil.getHttpServletResponse(
                            actionResponse
                    );

            if (response == null) {
                throw new Exception(
                        "No se pudo preparar la respuesta de descarga."
                );
            }

            ServletResponseUtil.sendFile(
                    response,
                    nombreDescarga,
                    input,
                    contenido.length,
                    contentType
            );

            setForward(
                    actionRequest,
                    ActionConstants.COMMON_NULL
            );

        } catch (Exception e) {
            _log.error(
                    "Descarga segura de documento Compras/RP rechazada. "
                            + "idReclamoPrestacional="
                            + idReclamoPrestacional
                            + ", idRequerimientoPresupuesto="
                            + idRequerimientoPresupuesto,
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

    protected void validarRelacion(
            RequerimientoCompraReclamoPrestacional relacion,
            int idReclamoPrestacional)
            throws Exception {

        if (relacion == null
                || !relacion.isVinculado()
                || relacion.getIdReclamoPrestacionalInt()
                != idReclamoPrestacional
                || relacion.getIdRequerimientoCompra() <= 0) {

            throw new Exception(
                    "El Reclamo Prestacional no posee "
                            + "un vínculo válido con Compras."
            );
        }
    }

    protected RequerimientoCompraPresupuesto resolverDocumentoAutorizado(
            int idRequerimientoCompra,
            int idRequerimientoPresupuesto)
            throws Exception {

        if (idRequerimientoCompra <= 0
                || idRequerimientoPresupuesto <= 0) {

            throw new Exception(
                    "Los identificadores del documento "
                            + "de Compras no son válidos."
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

                if (tieneId(
                        ordenMedica,
                        idRequerimientoPresupuesto
                )) {

                    return ordenMedica;
                }
            }
        }

        RequerimientoCompraPresupuesto presupuestoAdjudicado =
                BusquedaRequerimientoCompraServiceUtil
                        .getPresupuestoAdjudicado(
                                idRequerimientoCompra
                        );

        if (tieneId(
                presupuestoAdjudicado,
                idRequerimientoPresupuesto
        )) {

            return presupuestoAdjudicado;
        }

        throw new Exception(
                "El documento solicitado no es documentación "
                        + "autorizada del Reclamo Prestacional."
        );
    }

    protected void validarDocumento(
            RequerimientoCompraPresupuesto documento,
            int idRequerimientoCompra)
            throws Exception {

        if (documento == null
                || documento.getIdRequerimientoPresupuesto() == null
                || documento.getIdRequerimientoPresupuesto().intValue() <= 0
                || documento.getIdRequerimiento() == null
                || documento.getIdRequerimiento().intValue()
                != idRequerimientoCompra
                || documento.getTipoDocumento() == null
                || !documento.isActivo()
                || documento.getDlGroupId() == null
                || documento.getDlGroupId().longValue() <= 0L
                || documento.getDlFolderId() == null
                || documento.getDlFolderId().longValue() <= 0L
                || documento.getDlFileEntryId() == null
                || documento.getDlFileEntryId().longValue() <= 0L
                || WebKeysCompras.isEmpty(
                documento.getDlFileUuid()
        )
                || WebKeysCompras.isEmpty(
                documento.getNombreOriginal()
        )
                || WebKeysCompras.isEmpty(
                documento.getNombrePersistido()
        )
                || WebKeysCompras.isEmpty(
                documento.getTitulo()
        )) {

            throw new Exception(
                    "El documento de Compras no posee "
                            + "una identidad activa válida."
            );
        }

        int tipoDocumento =
                documento.getTipoDocumento().intValue();

        if (tipoDocumento
                == RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_ORDEN_MEDICA) {

            DocumentoLibraryComprasHelper
                    .validarRelacionOrdenMedica(
                            documento,
                            idRequerimientoCompra
                    );

        } else if (tipoDocumento
                == RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_PRESUPUESTO) {

            if (documento.getIdPrestador() == null
                    || documento.getIdPrestador().intValue() <= 0) {

                throw new Exception(
                        "El presupuesto adjudicado "
                                + "no posee un prestador válido."
                );
            }

        } else {
            throw new Exception(
                    "El tipo de documento de Compras "
                            + "no está autorizado "
                            + "para el Reclamo Prestacional."
            );
        }
    }

    private String validarContenidoDocumento(
            RequerimientoCompraPresupuesto documento,
            String nombreDescarga,
            byte[] contenido)
            throws Exception {

        int tipoDocumento =
                documento.getTipoDocumento().intValue();

        if (tipoDocumento
                == RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_ORDEN_MEDICA) {

            return DocumentoLibraryComprasHelper
                    .validarContenidoOrdenMedica(
                            contenido,
                            nombreDescarga
                    );
        }

        if (tipoDocumento
                == RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_PRESUPUESTO) {

            validarContenidoPdf(
                    contenido,
                    nombreDescarga
            );

            return "application/pdf";
        }

        throw new Exception(
                "El tipo de documento solicitado no es válido."
        );
    }

    private void validarContenidoPdf(
            byte[] contenido,
            String nombreDescarga)
            throws Exception {

        if (contenido == null
                || contenido.length < 5
                || WebKeysCompras.isEmpty(
                nombreDescarga
        )
                || !nombreDescarga
                .toLowerCase(Locale.ENGLISH)
                .endsWith(".pdf")
                || contenido[0] != '%'
                || contenido[1] != 'P'
                || contenido[2] != 'D'
                || contenido[3] != 'F'
                || contenido[4] != '-') {

            throw new Exception(
                    "El presupuesto adjudicado "
                            + "no conserva un contenido PDF válido."
            );
        }
    }

    private void validarReclamoPersistido(
            int idReclamoPrestacional)
            throws Exception {

        ReclamoPrestacional reclamo =
                ReclamosPrestacionesServiceUtil
                        .getReclamoPrestacional(
                                idReclamoPrestacional
                        );

        if (reclamo == null
                || reclamo.getId_reclamo()
                != idReclamoPrestacional
                || reclamo.getBaja_fecha() != null) {

            throw new Exception(
                    "El Reclamo Prestacional informado "
                            + "no existe o no esta activo."
            );
        }
    }

    private boolean tieneId(
            RequerimientoCompraPresupuesto documento,
            int idRequerimientoPresupuesto) {

        return documento != null
                && documento.getIdRequerimientoPresupuesto() != null
                && documento
                .getIdRequerimientoPresupuesto()
                .intValue()
                == idRequerimientoPresupuesto;
    }

    private String validarNombreDescarga(
            String nombre)
            throws Exception {

        if (WebKeysCompras.isEmpty(nombre)
                || nombre.indexOf('/') >= 0
                || nombre.indexOf('\\') >= 0
                || nombre.indexOf("..") >= 0
                || nombre.indexOf('\r') >= 0
                || nombre.indexOf('\n') >= 0
                || nombre.indexOf('"') >= 0
                || nombre.matches(
                ".*\\p{Cntrl}.*"
        )) {

            throw new Exception(
                    "El nombre original del documento "
                            + "de Compras no es seguro."
            );
        }

        return nombre;
    }

    private void validarPermisoReclamoPrestacional(
            User user)
            throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        boolean permitido =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_ABM_RECLAM_PREST
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_CONSULTA_RECLAMOS_PRESTACIONALES
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysAutorizaciones
                                .ROL_REABRIR_RECLAMO_PRESTACIONAL
                );

        if (!permitido) {
            throw new Exception(
                    "No posee permisos para consultar "
                            + "documentos del Reclamo Prestacional."
            );
        }
    }

    protected boolean isCheckMethodOnProcessAction() {
        return false;
    }
}