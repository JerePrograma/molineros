package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public final class ReclamoPrestacionalCompraDocumentacionHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ReclamoPrestacionalCompraDocumentacionHelper.class
            );

    /*
     * Contrato legacy de Reclamos Prestacionales.
     *
     * Debe coincidir exactamente con UploadImagenesReclamosAction
     * y reclamo_prestacional_imagenes_search_documentos.jsp.
     */
    private static final long GROUP_ID_RECLAMOS =
            10136L;

    private static final long PARENT_FOLDER_ID_RECLAMOS =
            0L;

    private static final String FOLDER_RECLAMOS =
            "ReclamosPrestacionales";

    public int adjuntarDocumentacion(
            int idRequerimientoCompra,
            int idReclamoPrestacional,
            ServiceContext serviceContext)
            throws Exception {

        validarParametros(
                idRequerimientoCompra,
                idReclamoPrestacional,
                serviceContext
        );

        DLFolder folder =
                obtenerFolderReclamos();

        int cantidadAdjunta =
                0;

        /*
         * ==========================================================
         * 1. ORDENES MEDICAS
         * ==========================================================
         */
        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                BusquedaRequerimientoCompraServiceUtil
                        .listarOrdenesMedicas(
                                idRequerimientoCompra
                        );

        if (ordenesMedicas == null
                || ordenesMedicas.isEmpty()) {

            throw new Exception(
                    "El requerimiento no posee Ordenes medicas "
                            + "activas para adjuntar al Reclamo Prestacional."
            );
        }

        for (int i = 0;
             i < ordenesMedicas.size();
             i++) {

            RequerimientoCompraPresupuesto ordenMedica =
                    ordenesMedicas.get(i);

            validarOrdenMedica(
                    ordenMedica,
                    idRequerimientoCompra
            );

            byte[] contenido =
                    leerDocumentoCompra(
                            ordenMedica
                    );

            DocumentoLibraryComprasHelper
                    .validarContenidoOrdenMedica(
                            contenido,
                            ordenMedica.getNombreOriginal()
                    );

            String extension =
                    DocumentoLibraryComprasHelper
                            .obtenerExtensionSeguraDocumento(
                                    ordenMedica
                                            .getNombreOriginal()
                            );

            String nombreDestino =
                    "RP-"
                            + idReclamoPrestacional
                            + "-ORDEN-MEDICA-"
                            + ordenMedica
                            .getIdRequerimientoPresupuesto()
                            + extension;

            String titulo =
                    idReclamoPrestacional
                            + "-COMPRA-ORDEN-MEDICA-"
                            + ordenMedica
                            .getIdRequerimientoPresupuesto();

            String descripcion =
                    "Orden medica proveniente del "
                            + "Requerimiento de Compra #"
                            + idRequerimientoCompra;

            guardarArchivo(
                    folder,
                    nombreDestino,
                    titulo,
                    descripcion,
                    contenido,
                    serviceContext
            );

            cantidadAdjunta++;
        }

        /*
         * ==========================================================
         * 2. PEDIDO DE COTIZACION
         * ==========================================================
         *
         * No regenerar el PDF en este punto.
         *
         * El requerimiento ya se encuentra COTIZADO y el Jasper
         * imprime datos de cotizacion y prestador. Regenerarlo
         * podria producir un documento distinto del pedido que
         * recibio originalmente el prestador.
         *
         * Se recupera exclusivamente el pedido exacto persistido
         * durante el envio correspondiente al prestador finalmente
         * adjudicado.
         */
        RequerimientoCompraPedidoCotizacion pedidoCotizacion =
                BusquedaRequerimientoCompraServiceUtil
                        .getPedidoCotizacionAdjudicado(
                                idRequerimientoCompra
                        );

        validarPedidoCotizacion(
                pedidoCotizacion,
                idRequerimientoCompra
        );

        byte[] contenidoPedidoCotizacion =
                leerPedidoCotizacion(
                        pedidoCotizacion
                );

        validarPdf(
                contenidoPedidoCotizacion,
                pedidoCotizacion.getNombreOriginal()
        );

        guardarArchivo(
                folder,
                "RP-"
                        + idReclamoPrestacional
                        + "-PEDIDO-COTIZACION-"
                        + idRequerimientoCompra
                        + ".pdf",
                idReclamoPrestacional
                        + "-COMPRA-PEDIDO-COTIZACION-"
                        + idRequerimientoCompra,
                "Pedido de cotizacion efectivamente enviado "
                        + "al prestador adjudicado del "
                        + "Requerimiento de Compra #"
                        + idRequerimientoCompra,
                contenidoPedidoCotizacion,
                serviceContext
        );

        cantidadAdjunta++;

        /*
         * ==========================================================
         * 3. COTIZACION DEL PRESTADOR ADJUDICADO
         * ==========================================================
         */
        RequerimientoCompraPresupuesto presupuestoAdjudicado =
                BusquedaRequerimientoCompraServiceUtil
                        .getPresupuestoAdjudicado(
                                idRequerimientoCompra
                        );

        validarPresupuestoAdjudicado(
                presupuestoAdjudicado,
                idRequerimientoCompra
        );

        byte[] cotizacionAdjudicada =
                leerDocumentoCompra(
                        presupuestoAdjudicado
                );

        validarPdf(
                cotizacionAdjudicada,
                presupuestoAdjudicado
                        .getNombreOriginal()
        );

        guardarArchivo(
                folder,
                "RP-"
                        + idReclamoPrestacional
                        + "-COTIZACION-ADJUDICADA-"
                        + presupuestoAdjudicado
                        .getIdRequerimientoPresupuesto()
                        + ".pdf",
                idReclamoPrestacional
                        + "-COMPRA-COTIZACION-ADJUDICADA-"
                        + presupuestoAdjudicado
                        .getIdRequerimientoPresupuesto(),
                "Cotizacion del prestador adjudicado proveniente "
                        + "del Requerimiento de Compra #"
                        + idRequerimientoCompra,
                cotizacionAdjudicada,
                serviceContext
        );

        cantidadAdjunta++;

        if (_log.isInfoEnabled()) {
            _log.info(
                    "Documentacion de Compras adjuntada "
                            + "al Reclamo Prestacional. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", idReclamo="
                            + idReclamoPrestacional
                            + ", cantidad="
                            + cantidadAdjunta
            );
        }

        return cantidadAdjunta;
    }

    private void validarParametros(
            int idRequerimientoCompra,
            int idReclamoPrestacional,
            ServiceContext serviceContext)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (idReclamoPrestacional <= 0) {
            throw new Exception(
                    "Debe informar el Reclamo Prestacional."
            );
        }

        if (serviceContext == null
                || serviceContext.getUserId() <= 0L) {

            throw new Exception(
                    "No se pudo determinar el contexto "
                            + "de Document Library."
            );
        }
    }

    private DLFolder obtenerFolderReclamos()
            throws Exception {

        DLFolder folder =
                DLFolderLocalServiceUtil.getFolder(
                        GROUP_ID_RECLAMOS,
                        PARENT_FOLDER_ID_RECLAMOS,
                        FOLDER_RECLAMOS
                );

        if (folder == null
                || folder.getFolderId() <= 0L
                || folder.getGroupId()
                != GROUP_ID_RECLAMOS) {

            throw new Exception(
                    "No se pudo recuperar la carpeta "
                            + "ReclamosPrestacionales."
            );
        }

        return folder;
    }

    private void validarOrdenMedica(
            RequerimientoCompraPresupuesto ordenMedica,
            int idRequerimientoCompra)
            throws Exception {

        if (ordenMedica == null
                || ordenMedica.getIdRequerimientoPresupuesto() == null
                || ordenMedica
                .getIdRequerimientoPresupuesto()
                .intValue() <= 0
                || ordenMedica.getIdRequerimiento() == null
                || ordenMedica
                .getIdRequerimiento()
                .intValue()
                != idRequerimientoCompra
                || ordenMedica.getTipoDocumento() == null
                || ordenMedica
                .getTipoDocumento()
                .intValue()
                != RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_ORDEN_MEDICA
                || ordenMedica.getIdPrestador() != null
                || !ordenMedica.isActivo()
                || ordenMedica.getFechaDocumento() == null) {

            throw new Exception(
                    "Una Orden medica del requerimiento "
                            + "no posee una asociacion valida."
            );
        }

        DocumentoLibraryComprasHelper
                .validarIdentidadAsociacionDocumento(
                        ordenMedica
                );
    }

    private void validarPedidoCotizacion(
            RequerimientoCompraPedidoCotizacion pedido,
            int idRequerimientoCompra)
            throws Exception {

        if (pedido == null
                || pedido.getIdRequerimiento() == null
                || pedido
                .getIdRequerimiento()
                .intValue()
                != idRequerimientoCompra
                || pedido.getIdPrestador() == null
                || pedido
                .getIdPrestador()
                .intValue() <= 0
                || pedido.getIntento() == null
                || pedido
                .getIntento()
                .intValue() <= 0
                || pedido.getDlGroupId() == null
                || pedido
                .getDlGroupId()
                .longValue() <= 0L
                || pedido.getDlFolderId() == null
                || pedido
                .getDlFolderId()
                .longValue() <= 0L
                || pedido.getDlFileEntryId() == null
                || pedido
                .getDlFileEntryId()
                .longValue() <= 0L
                || WebKeysCompras.isEmpty(
                pedido.getDlFileUuid()
        )
                || WebKeysCompras.isEmpty(
                pedido.getNombreOriginal()
        )
                || WebKeysCompras.isEmpty(
                pedido.getNombrePersistido()
        )
                || WebKeysCompras.isEmpty(
                pedido.getTitulo()
        )) {

            throw new Exception(
                    "No se pudo determinar el pedido de cotizacion "
                            + "efectivamente enviado al prestador adjudicado."
            );
        }

        String extension =
                DocumentoLibraryComprasHelper
                        .obtenerExtensionSeguraDocumento(
                                pedido.getNombreOriginal()
                        );

        if (!".pdf".equals(
                extension
        )) {

            throw new Exception(
                    "El pedido de cotizacion persistido "
                            + "no posee formato PDF."
            );
        }

        DocumentoLibraryComprasHelper
                .validarIdentidadAsociacionDocumento(
                        pedido
                );
    }

    private void validarPresupuestoAdjudicado(
            RequerimientoCompraPresupuesto presupuesto,
            int idRequerimientoCompra)
            throws Exception {

        if (presupuesto == null
                || presupuesto.getIdRequerimientoPresupuesto() == null
                || presupuesto
                .getIdRequerimientoPresupuesto()
                .intValue() <= 0
                || presupuesto.getIdRequerimiento() == null
                || presupuesto
                .getIdRequerimiento()
                .intValue()
                != idRequerimientoCompra
                || presupuesto.getTipoDocumento() == null
                || presupuesto
                .getTipoDocumento()
                .intValue()
                != RequerimientoCompraPresupuesto
                .TIPO_DOCUMENTO_PRESUPUESTO
                || presupuesto.getIdPrestador() == null
                || presupuesto
                .getIdPrestador()
                .intValue() <= 0
                || !presupuesto.isActivo()) {

            throw new Exception(
                    "No se pudo determinar una cotizacion "
                            + "adjudicada valida."
            );
        }

        DocumentoLibraryComprasHelper
                .validarIdentidadAsociacionDocumento(
                        presupuesto
                );
    }

    private byte[] leerDocumentoCompra(
            RequerimientoCompraPresupuesto documento)
            throws Exception {

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil
                        .getDLFileEntry(
                                documento
                                        .getDlFileEntryId()
                                        .longValue()
                        );

        if (entry == null
                || !DocumentoLibraryComprasHelper
                .coincideIdentidadAsociacionDocumento(
                        documento,
                        entry
                )) {

            throw new Exception(
                    "El documento de Compras no coincide "
                            + "con su identidad en Document Library."
            );
        }

        return DocumentoLibraryComprasHelper
                .leerContenidoDocumentLibrary(
                        entry
                );
    }

    private byte[] leerPedidoCotizacion(
            RequerimientoCompraPedidoCotizacion pedido)
            throws Exception {

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil
                        .getDLFileEntry(
                                pedido
                                        .getDlFileEntryId()
                                        .longValue()
                        );

        if (entry == null
                || !DocumentoLibraryComprasHelper
                .coincideIdentidadAsociacionDocumento(
                        pedido,
                        entry
                )) {

            throw new Exception(
                    "El pedido de cotizacion persistido "
                            + "no coincide con su identidad "
                            + "en Document Library."
            );
        }

        return DocumentoLibraryComprasHelper
                .leerContenidoDocumentLibrary(
                        entry
                );
    }

    private void validarPdf(
            byte[] contenido,
            String nombreDocumento)
            throws Exception {

        if (contenido == null
                || contenido.length < 5
                || contenido[0] != '%'
                || contenido[1] != 'P'
                || contenido[2] != 'D'
                || contenido[3] != 'F'
                || contenido[4] != '-') {

            throw new Exception(
                    nombreDocumento
                            + ": el contenido no corresponde "
                            + "a un PDF valido."
            );
        }

        long maximo =
                DocumentoLibraryComprasHelper
                        .obtenerMaximoTamanoDocumento();

        if (contenido.length > maximo) {
            throw new Exception(
                    nombreDocumento
                            + ": el archivo supera "
                            + "el tamano permitido."
            );
        }
    }

    private void guardarArchivo(
            DLFolder folder,
            String nombrePersistido,
            String titulo,
            String descripcion,
            byte[] contenido,
            ServiceContext serviceContext)
            throws Exception {

        if (folder == null
                || folder.getFolderId() <= 0L) {

            throw new Exception(
                    "No se pudo determinar la carpeta "
                            + "del Reclamo Prestacional."
            );
        }

        String nombreSeguro =
                DocumentoLibraryComprasHelper
                        .normalizarNombreArchivoSeguro(
                                nombrePersistido
                        );

        if (WebKeysCompras.isEmpty(
                nombreSeguro
        )
                || !nombreSeguro.equals(
                nombrePersistido
        )) {

            throw new Exception(
                    "No se pudo construir un nombre seguro "
                            + "para el documento del Reclamo Prestacional."
            );
        }

        if (WebKeysCompras.isEmpty(
                titulo
        )) {

            throw new Exception(
                    "No se pudo construir el titulo "
                            + "del documento del Reclamo Prestacional."
            );
        }

        long maximo =
                DocumentoLibraryComprasHelper
                        .obtenerMaximoTamanoDocumento();

        if (contenido == null
                || contenido.length <= 0
                || contenido.length > maximo) {

            throw new Exception(
                    "El documento del Reclamo Prestacional "
                            + "posee un tamano invalido."
            );
        }

        String extension =
                DocumentoLibraryComprasHelper
                        .obtenerExtensionSeguraDocumento(
                                nombrePersistido
                        );

        if (WebKeysCompras.isEmpty(
                extension
        )) {

            throw new Exception(
                    "El documento del Reclamo Prestacional "
                            + "no posee una extension valida."
            );
        }

        File temporal =
                null;

        FileOutputStream output =
                null;

        try {
            temporal =
                    File.createTempFile(
                            "rp-compra-",
                            extension
                    );

            output =
                    new FileOutputStream(
                            temporal
                    );

            output.write(
                    contenido
            );

            output.flush();
            output.close();
            output = null;

            /*
             * El nombre es deterministico.
             *
             * Si por una ejecucion parcial este metodo vuelve
             * a correrse para el mismo RP/documento, se sobrescribe
             * el mismo archivo en lugar de duplicarlo.
             */
            DLFileEntry entry =
                    DLFileEntryLocalServiceUtil
                            .addOrOverwriteFileEntry(
                                    serviceContext.getUserId(),
                                    folder.getFolderId(),
                                    nombrePersistido,
                                    nombrePersistido,
                                    titulo,
                                    descripcion,
                                    "",
                                    temporal,
                                    serviceContext
                            );

            if (entry == null
                    || entry.getFileEntryId() <= 0L
                    || entry.getFolderId()
                    != folder.getFolderId()
                    || entry.getGroupId()
                    != folder.getGroupId()
                    || !nombrePersistido.equals(
                    entry.getName()
            )
                    || !titulo.equals(
                    entry.getTitle()
            )) {

                throw new Exception(
                        "Document Library no confirmo "
                                + "el documento del Reclamo Prestacional."
                );
            }

        } finally {

            if (output != null) {
                try {
                    output.close();
                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar el archivo temporal.",
                                closeError
                        );
                    }
                }
            }

            if (temporal != null
                    && temporal.exists()
                    && !temporal.delete()
                    && _log.isDebugEnabled()) {

                _log.debug(
                        "No se pudo eliminar el archivo temporal: "
                                + temporal.getAbsolutePath()
                );
            }
        }
    }

    public ReclamoPrestacionalCompraDocumentacionHelper() {
    }
}
