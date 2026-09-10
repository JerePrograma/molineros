package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.RenderRequest;

/** Publica el contexto de presentación requerido por los JSP de Compras. */
public final class RequerimientoCompraRenderActionUtil {

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_PRESTADORES_ENVIADOS =
            WebKeysCompras.ATTR_PRESTADORES_ENVIADOS_REQUERIMIENTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_ERROR_PRESTADORES_ENVIADOS =
            WebKeysCompras.ATTR_ERROR_PRESTADORES_ENVIADOS_REQUERIMIENTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO =
            WebKeysCompras.ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_PRESUPUESTOS =
            WebKeysCompras.ATTR_PRESUPUESTOS_REQUERIMIENTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_IDS_PRESTADORES_CON_PRESUPUESTO =
            WebKeysCompras.ATTR_IDS_PRESTADORES_CON_PRESUPUESTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_ERROR_PRESUPUESTOS =
            WebKeysCompras.ATTR_ERROR_PRESUPUESTOS_REQUERIMIENTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_PRESUPUESTO_DOCUMENTO_VALIDO =
            WebKeysCompras.ATTR_PRESUPUESTO_DOCUMENTO_VALIDO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_PRESUPUESTO_DOWNLOAD_URL =
            WebKeysCompras.ATTR_PRESUPUESTO_DOWNLOAD_URL;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_ORDENES_MEDICAS =
            WebKeysCompras.ATTR_ORDENES_MEDICAS_REQUERIMIENTO;

    /** @deprecated Valor canonico en WebKeysCompras. */
    @Deprecated
    public static final String ATTR_ERROR_ORDENES_MEDICAS =
            WebKeysCompras.ATTR_ERROR_ORDENES_MEDICAS_REQUERIMIENTO;

    private RequerimientoCompraRenderActionUtil() {
    }

    public static void publicarContexto(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        if (renderRequest == null) {
            return;
        }

        List<PrestadorCotizacion> prestadoresEnviados =
                new ArrayList<PrestadorCotizacion>();
        List<PrestadorCotizacion> prestadoresDisponiblesPresupuesto =
                new ArrayList<PrestadorCotizacion>();
        List<RequerimientoCompraPresupuesto> presupuestos =
                new ArrayList<RequerimientoCompraPresupuesto>();
        Set<Integer> idsPrestadoresConPresupuesto =
                new HashSet<Integer>();
        Map<Integer, Boolean> presupuestoDocumentoValido =
                new HashMap<Integer, Boolean>();
        Map<Integer, String> presupuestoDownloadURL =
                new HashMap<Integer, String>();
        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                new ArrayList<RequerimientoCompraPresupuesto>();

        String errorPrestadoresEnviados = "";
        String errorPresupuestos = "";
        String errorOrdenesMedicas = "";

        ThemeDisplay themeDisplay =
                (ThemeDisplay) renderRequest.getAttribute(
                        WebKeys.THEME_DISPLAY
                );

        long scopeGroupId =
                themeDisplay != null
                        ? themeDisplay.getScopeGroupId()
                        : 0L;
        String pathMain =
                themeDisplay != null
                        ? themeDisplay.getPathMain()
                        : "";

        if (requerimiento != null
                && requerimiento.getIdRequerimientoCompra() > 0) {

            int idRequerimientoCompra =
                    requerimiento.getIdRequerimientoCompra();

            if (!requerimiento.esSectorSinCotizacionPrestador()
                    && (requerimiento.puedeVerPresupuestos()
                    || requerimiento.puedeEditarCotizacion())) {

                try {
                    prestadoresEnviados.addAll(
                            BusquedaRequerimientoCompraServiceUtil
                                    .listarPrestadoresEnviados(
                                            idRequerimientoCompra
                                    )
                    );

                    for (int i = 0;
                            i < prestadoresEnviados.size();
                            i++) {

                        PrestadorCotizacion prestador =
                                prestadoresEnviados.get(i);

                        if (prestador != null
                                && prestador.getIdPrestador() > 0
                                && WebKeysCompras.ENVIO_ENVIADO.equals(
                                        prestador.getEstadoEnvio()
                                )) {

                            prestadoresDisponiblesPresupuesto.add(
                                    prestador
                            );
                        }
                    }
                } catch (Exception e) {
                    errorPrestadoresEnviados =
                            !WebKeysCompras.isEmpty(e.getMessage())
                                    ? e.getMessage()
                                    : "No se pudieron cargar los prestadores enviados.";
                }
            }

            if (requerimiento.puedeVerPresupuestos()) {
                try {
                    if (requerimiento
                            .esSectorSinCotizacionPrestador()) {

                        presupuestos.addAll(
                                BusquedaRequerimientoCompraServiceUtil
                                        .listarCotizacionesEmpresa(
                                                idRequerimientoCompra
                                        )
                        );

                    } else {
                        presupuestos.addAll(
                                BusquedaRequerimientoCompraServiceUtil
                                        .listarPresupuestos(
                                                idRequerimientoCompra
                                        )
                        );
                    }

                    for (int i = 0; i < presupuestos.size(); i++) {
                        RequerimientoCompraPresupuesto presupuesto =
                                presupuestos.get(i);

                        if (presupuesto == null
                                || presupuesto
                                        .getIdRequerimientoPresupuesto()
                                        == null
                                || presupuesto
                                        .getIdRequerimientoPresupuesto()
                                        .intValue() <= 0) {

                            continue;
                        }

                        Integer idPresupuesto =
                                presupuesto
                                        .getIdRequerimientoPresupuesto();

                        if (presupuesto.getBajaFecha() == null
                                && presupuesto.getIdPrestador() != null
                                && presupuesto
                                        .getIdPrestador()
                                        .intValue() > 0
                                && presupuesto.getDlFileEntryId() != null
                                && presupuesto
                                        .getDlFileEntryId()
                                        .longValue() > 0L) {

                            idsPrestadoresConPresupuesto.add(
                                    presupuesto.getIdPrestador()
                            );
                        }

                        boolean documentoValido = false;
                        String downloadURL = "";

                        try {
                            if (presupuesto.getDlFileEntryId() != null
                                    && presupuesto
                                            .getDlFileEntryId()
                                            .longValue() > 0L) {

                                DLFileEntry fileEntry =
                                        DLFileEntryLocalServiceUtil
                                                .getDLFileEntry(
                                                        presupuesto
                                                                .getDlFileEntryId()
                                                                .longValue()
                                                );

                                documentoValido =
                                        DocumentoLibraryComprasHelper
                                                .coincideIdentidadAsociacionDocumento(
                                                        presupuesto,
                                                        fileEntry
                                                );

                                if (documentoValido
                                        && scopeGroupId > 0L) {
                                    documentoValido =
                                            fileEntry.getGroupId()
                                                    == scopeGroupId;
                                }

                                if (documentoValido
                                        && !WebKeysCompras.isEmpty(
                                                pathMain
                                        )) {

                                    downloadURL =
                                            pathMain
                                                    + "/document_library/get_file?folderId="
                                                    + fileEntry.getFolderId()
                                                    + "&name="
                                                    + HttpUtil.encodeURL(
                                                            fileEntry.getName()
                                                    );
                                }
                            }
                        } catch (Exception ignored) {
                            documentoValido = false;
                            downloadURL = "";
                        }

                        presupuestoDocumentoValido.put(
                                idPresupuesto,
                                Boolean.valueOf(documentoValido)
                        );
                        presupuestoDownloadURL.put(
                                idPresupuesto,
                                downloadURL
                        );
                    }
                } catch (Exception e) {
                    errorPresupuestos =
                            !WebKeysCompras.isEmpty(e.getMessage())
                                    ? e.getMessage()
                                    : "No se pudieron cargar los presupuestos asociados al requerimiento.";
                }
            }

            try {
                ordenesMedicas.addAll(
                        BusquedaRequerimientoCompraServiceUtil
                                .listarOrdenesMedicas(
                                        idRequerimientoCompra
                                )
                );
            } catch (Exception e) {
                errorOrdenesMedicas =
                        !WebKeysCompras.isEmpty(e.getMessage())
                                ? e.getMessage()
                                : "No se pudieron recuperar los adjuntos del requerimiento.";
            }
        }

        renderRequest.setAttribute(
                ATTR_PRESTADORES_ENVIADOS,
                prestadoresEnviados
        );
        renderRequest.setAttribute(
                ATTR_ERROR_PRESTADORES_ENVIADOS,
                errorPrestadoresEnviados
        );
        renderRequest.setAttribute(
                ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO,
                prestadoresDisponiblesPresupuesto
        );
        renderRequest.setAttribute(
                ATTR_PRESUPUESTOS,
                presupuestos
        );
        renderRequest.setAttribute(
                ATTR_IDS_PRESTADORES_CON_PRESUPUESTO,
                idsPrestadoresConPresupuesto
        );
        renderRequest.setAttribute(
                ATTR_ERROR_PRESUPUESTOS,
                errorPresupuestos
        );
        renderRequest.setAttribute(
                ATTR_PRESUPUESTO_DOCUMENTO_VALIDO,
                presupuestoDocumentoValido
        );
        renderRequest.setAttribute(
                ATTR_PRESUPUESTO_DOWNLOAD_URL,
                presupuestoDownloadURL
        );
        renderRequest.setAttribute(
                ATTR_ORDENES_MEDICAS,
                ordenesMedicas
        );
        renderRequest.setAttribute(
                ATTR_ERROR_ORDENES_MEDICAS,
                errorOrdenesMedicas
        );
    }
}
