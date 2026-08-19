package ar.com.ospim.compras.requerimientos.documentos;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;

import com.liferay.documentlibrary.service.DLLocalServiceUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import javax.portlet.ActionRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

public class DocumentoLibraryComprasHelper
        implements GestorOrdenMedicaDocumento {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    DocumentoLibraryComprasHelper.class
            );

    public static final String TITULO_ORDEN_MEDICA =
            "Orden médica";

    public static final String PARAM_ARCHIVO_ORDEN_MEDICA =
            "orden_medica";

    public static final String PARAM_FECHA_ORDEN_MEDICA =
            "fecha_orden_medica";

    private static final String DESCRIPCION_ORDEN_MEDICA =
            "Orden médica";

    private static final String CONTENT_TYPE_JPEG =
            "image/jpeg";

    private static final String CONTENT_TYPE_PNG =
            "image/png";

    private final ServiceContext serviceContext;
    private final long groupId;
    private final long userId;

    public static void validarRelacionOrdenMedica(
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
                || WebKeysCompras.isEmpty(
                ordenMedica.getDlFileUuid()
        )
                || WebKeysCompras.isEmpty(
                ordenMedica.getNombreOriginal()
        )
                || !TITULO_ORDEN_MEDICA.equals(
                ordenMedica.getTitulo()
        )) {

            throw new Exception(
                    "La asociacion de la Orden medica activa es inconsistente."
            );
        }

        validarIdentidadAsociacionDocumento(
                ordenMedica
        );
    }

    /**
     * Regla generica de identidad SQL -> Document Library reutilizable
     * tanto por Orden medica como por Presupuesto.
     */
    public static void validarIdentidadAsociacionDocumento(
            RequerimientoCompraPresupuesto documento)
            throws Exception {

        if (documento == null
                || documento.getDlGroupId() == null
                || documento.getDlGroupId().longValue() <= 0L
                || documento.getDlFolderId() == null
                || documento.getDlFolderId().longValue() <= 0L
                || documento.getDlFileEntryId() == null
                || documento.getDlFileEntryId().longValue() <= 0L
                || WebKeysCompras.isEmpty(
                documento.getNombrePersistido()
        )) {

            throw new Exception(
                    "La asociacion del documento contiene "
                            + "una identidad de Document Library invalida."
            );
        }
    }

    public static boolean coincideIdentidadAsociacionDocumento(
            RequerimientoCompraPresupuesto documento,
            DLFileEntry entry) {

        if (documento == null
                || entry == null
                || documento.getDlGroupId() == null
                || documento.getDlFolderId() == null
                || documento.getDlFileEntryId() == null
                || WebKeysCompras.isEmpty(
                documento.getNombrePersistido()
        )) {

            return false;
        }

        boolean coincide =
                entry.getFileEntryId()
                        == documento.getDlFileEntryId().longValue()
                        && entry.getGroupId()
                        == documento.getDlGroupId().longValue()
                        && entry.getFolderId()
                        == documento.getDlFolderId().longValue()
                        && documento.getNombrePersistido().equals(
                        entry.getName()
                );

        String uuidPersistido =
                documento.getDlFileUuid();

        if (coincide
                && !WebKeysCompras.isEmpty(
                uuidPersistido
        )) {

            coincide =
                    uuidPersistido.equals(
                            entry.getUuid()
                    );
        }

        return coincide;
    }

    public static DocumentoComprasCreado crearIdentidadOrdenMedica(
            RequerimientoCompraPresupuesto ordenMedica)
            throws Exception {

        validarIdentidadAsociacionDocumento(
                ordenMedica
        );

        return new DocumentoComprasCreado(
                ordenMedica.getDlGroupId().longValue(),
                ordenMedica.getDlFolderId().longValue(),
                ordenMedica.getDlFileEntryId().longValue(),
                ordenMedica.getDlFileUuid(),
                ordenMedica.getNombrePersistido(),
                ordenMedica.getTitulo()
        );
    }

    /**
     * Recupera la entrada de Document Library y comprueba toda la identidad
     * persistida de la Orden medica.
     *
     * No abre el contenido. Eso permite que un Action de descarga realice
     * primero su DLFileEntryPermission.check().
     */
    public static DLFileEntry obtenerEntradaOrdenMedicaValidada(
            RequerimientoCompraPresupuesto ordenMedica,
            int idRequerimientoCompra,
            long companyId) throws Exception {

        validarRelacionOrdenMedica(
                ordenMedica,
                idRequerimientoCompra
        );

        if (companyId <= 0L) {
            throw new Exception(
                    "No se pudo determinar la empresa "
                            + "de la Orden médica."
            );
        }

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.getDLFileEntry(
                        ordenMedica
                                .getDlFileEntryId()
                                .longValue()
                );

        validarIdentidadOrdenMedicaPersistida(
                ordenMedica,
                entry,
                companyId
        );

        return entry;
    }

    public static void validarIdentidadOrdenMedicaPersistida(
            RequerimientoCompraPresupuesto ordenMedica,
            DLFileEntry entry,
            long companyId) throws Exception {

        validarIdentidadAsociacionDocumento(
                ordenMedica
        );

        boolean coincide =
                entry != null
                        && entry.getCompanyId() == companyId
                        && coincideIdentidadAsociacionDocumento(
                        ordenMedica,
                        entry
                );

        if (!coincide) {
            throw new Exception(
                    "La Orden médica no coincide con "
                            + "su identidad en Document Library."
            );
        }
    }

    /**
     * Lee el contenido binario de una Orden medica ya autorizada.
     *
     * IMPORTANTE:
     *
     * No utilizar DLFileEntryLocalServiceUtil.getFileAsStream(...)
     * en esta frontera.
     *
     * En Liferay legacy esa llamada actualiza DLFileRank y readCount antes
     * de recuperar el archivo. El update de DLFileRank depende de Counter
     * y puede impedir la lectura de un archivo valido por un fallo ajeno
     * al almacenamiento del documento.
     *
     * La identidad de la entrada debe haber sido validada previamente y
     * el Action de descarga debe comprobar DLFileEntryPermission VIEW antes
     * de invocar este metodo.
     */
    public static OrdenMedicaContenido leerOrdenMedicaValidada(
            DLFileEntry entry,
            String nombreOriginal) throws Exception {

        byte[] contenido =
                leerContenidoDocumentLibrary(
                        entry
                );

        String contentType =
                validarContenidoOrdenMedica(
                        contenido,
                        nombreOriginal
                );

        return new OrdenMedicaContenido(
                contenido,
                nombreOriginal,
                contentType
        );
    }

    public static OrdenMedicaContenido recuperarOrdenMedicaValidada(
            RequerimientoCompraPresupuesto ordenMedica,
            int idRequerimientoCompra,
            long companyId) throws Exception {

        DLFileEntry entry =
                obtenerEntradaOrdenMedicaValidada(
                        ordenMedica,
                        idRequerimientoCompra,
                        companyId
                );

        return leerOrdenMedicaValidada(
                entry,
                ordenMedica.getNombreOriginal()
        );
    }

    /**
     * Fuente unica de validacion del contenido persistido de una Orden medica.
     */
    public static String validarContenidoOrdenMedica(
            byte[] contenido,
            String nombreOriginal) throws Exception {

        if (contenido == null
                || contenido.length == 0) {

            throw new Exception(
                    "La Orden médica persistida está vacía."
            );
        }

        validarNombreOriginalOrdenMedicaPersistido(
                nombreOriginal
        );

        String extension =
                obtenerExtensionSeguraDocumento(
                        nombreOriginal
                );

        if (!esExtensionOrdenMedicaSegura(
                extension
        )) {

            throw new Exception(
                    "La Orden médica persistida "
                            + "no es JPEG/JPG ni PNG."
            );
        }

        String contentTypeEsperado =
                ".png".equals(extension)
                        ? CONTENT_TYPE_PNG
                        : CONTENT_TYPE_JPEG;

        /*
         * La firma binaria es la fuente autoritativa.
         * No depender de MimeTypesUtil para aceptar/rechazar
         * un documento ya validado por contenido.
         */
        validarFirmaImagen(
                contenido,
                contentTypeEsperado
        );

        return contentTypeEsperado;
    }

    public static DocumentoLibraryComprasHelper crear(
            ActionRequest actionRequest) throws Exception {

        if (actionRequest == null) {
            throw new Exception(
                    "No se pudo preparar la carga de la Orden médica."
            );
        }

        ServiceContext serviceContext =
                ServiceContextFactory.getInstance(
                        DLFileEntry.class.getName(),
                        actionRequest
                );

        return new DocumentoLibraryComprasHelper(
                serviceContext
        );
    }

    protected DocumentoLibraryComprasHelper(
            ServiceContext serviceContext) throws Exception {

        if (serviceContext == null) {
            throw new Exception(
                    "No se pudo preparar el contexto de Document Library."
            );
        }

        this.serviceContext =
                serviceContext;

        this.groupId =
                serviceContext.getScopeGroupId();

        this.userId =
                serviceContext.getUserId();

        validarContextoDocumentLibrary(
                groupId,
                userId
        );
    }

    public DocumentoComprasCreado crearOrdenMedica(
            int idRequerimientoCompra,
            OrdenMedicaValidada ordenMedica)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "El requerimiento de compra no es válido."
            );
        }

        validarOrdenMedicaPreparada(
                ordenMedica
        );

        DLFolder folder =
                obtenerOCrearFolderCompras();

        String identificador =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");

        String nombrePersistido =
                construirNombreOrdenMedica(
                        idRequerimientoCompra,
                        identificador,
                        ordenMedica.getExtension()
                );

        String tituloPersistido =
                construirTituloOrdenMedica(
                        idRequerimientoCompra,
                        identificador
                );

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.addFileEntry(
                        userId,
                        folder.getFolderId(),
                        nombrePersistido,
                        tituloPersistido,
                        DESCRIPCION_ORDEN_MEDICA,
                        "",
                        ordenMedica.getArchivo(),
                        serviceContext
                );

        try {
            if (entry == null
                    || entry.getFileEntryId() <= 0L) {

                throw new Exception(
                        "Document Library no devolvió "
                                + "una Orden médica válida."
                );
            }

            DocumentoComprasCreado documento =
                    new DocumentoComprasCreado(
                            entry.getGroupId(),
                            entry.getFolderId(),
                            entry.getFileEntryId(),
                            entry.getUuid(),
                            entry.getName(),
                            entry.getTitle()
                    );

            validarIdentidadDocumento(
                    documento
            );

            if (!coincideIdentidad(
                    documento,
                    entry
            )
                    || !tituloPersistido.equals(
                    entry.getTitle()
            )) {

                throw new Exception(
                        "La Orden médica creada no conserva "
                                + "la identidad requerida."
                );
            }

            return documento;

        } catch (Exception postAddError) {
            if (entry != null
                    && entry.getFileEntryId() > 0L) {

                try {
                    DLFileEntryLocalServiceUtil
                            .deleteFileEntry(
                                    entry
                            );

                } catch (Exception cleanupError) {
                    _log.error(
                            "Falló la compensación de una Orden médica "
                                    + "creada pero no validada. fileEntryId="
                                    + entry.getFileEntryId()
                                    + ", folderId="
                                    + entry.getFolderId()
                                    + ", nombre="
                                    + entry.getName(),
                            cleanupError
                    );
                }
            }

            throw postAddError;
        }
    }

    public void eliminarDocumento(
            DocumentoComprasCreado documento)
            throws Exception {

        validarIdentidadDocumento(
                documento
        );

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.getDLFileEntry(
                        documento.getFileEntryId()
                );

        if (!coincideIdentidad(
                documento,
                entry
        )) {

            throw new Exception(
                    "El documento a compensar no coincide "
                            + "con la identidad persistida."
            );
        }

        DLFileEntryLocalServiceUtil.deleteFileEntry(
                entry
        );
    }

    public void validarIdentidadDocumento(
            DocumentoComprasCreado documento)
            throws Exception {

        if (documento == null
                || documento.getGroupId() <= 0L
                || documento.getFolderId() <= 0L
                || documento.getFileEntryId() <= 0L
                || WebKeysCompras.isEmpty(
                documento.getNombrePersistido()
        )
                || WebKeysCompras.isEmpty(
                documento.getTitulo()
        )) {

            throw new Exception(
                    "La identidad del documento de Compras no es válida."
            );
        }
    }

    public boolean coincideIdentidad(
            DocumentoComprasCreado documento,
            DLFileEntry entry) {

        if (documento == null
                || entry == null) {

            return false;
        }

        boolean coincide =
                entry.getFileEntryId()
                        == documento.getFileEntryId()
                        && entry.getGroupId()
                        == documento.getGroupId()
                        && entry.getFolderId()
                        == documento.getFolderId()
                        && documento.getNombrePersistido().equals(
                        entry.getName()
                );

        return coincide
                && (
                WebKeysCompras.isEmpty(
                        documento.getUuid()
                )
                        || documento.getUuid().equals(
                        entry.getUuid()
                )
        );
    }

    public String construirTituloOrdenMedica(
            int idRequerimientoCompra,
            String identificador)
            throws Exception {

        if (idRequerimientoCompra <= 0
                || WebKeysCompras.isEmpty(
                identificador
        )
                || !identificador.matches(
                "^[A-Za-z0-9]+$"
        )) {

            throw new Exception(
                    "No se pudo construir el título persistido "
                            + "de la Orden médica."
            );
        }

        return TITULO_ORDEN_MEDICA
                + " "
                + idRequerimientoCompra
                + " "
                + identificador;
    }

    public String construirNombreOrdenMedica(
            int idRequerimientoCompra,
            String identificador,
            String extension)
            throws Exception {

        if (idRequerimientoCompra <= 0
                || WebKeysCompras.isEmpty(
                identificador
        )
                || !identificador.matches(
                "^[A-Za-z0-9]+$"
        )
                || !esExtensionOrdenMedica(
                extension
        )) {

            throw new Exception(
                    "No se pudo construir el nombre persistido "
                            + "de la Orden médica."
            );
        }

        return "ORDEN-MEDICA-COMPRA-"
                + idRequerimientoCompra
                + "-"
                + identificador
                + extension;
    }

    /**
     * Primitiva documental comun.
     */
    public static String normalizarNombreArchivoSeguro(
            String filename) {

        if (filename == null) {
            return "";
        }

        String nombre =
                filename.trim();

        if (WebKeysCompras.isEmpty(
                nombre
        )
                || ".".equals(nombre)
                || "..".equals(nombre)
                || nombre.indexOf("..") >= 0
                || nombre.indexOf('/') >= 0
                || nombre.indexOf('\\') >= 0
                || nombre.matches(
                ".*\\p{Cntrl}.*"
        )) {

            return "";
        }

        return nombre;
    }

    public String obtenerNombreArchivo(
            String filename) {

        return normalizarNombreArchivoSeguro(
                filename
        );
    }

    /**
     * Primitiva documental comun.
     */
    public static String obtenerExtensionSeguraDocumento(
            String nombreOriginal) {

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )) {

            return "";
        }

        int posicionExtension =
                nombreOriginal.lastIndexOf('.');

        if (posicionExtension < 0
                || posicionExtension
                >= nombreOriginal.length() - 1) {

            return "";
        }

        String extension =
                nombreOriginal.substring(
                        posicionExtension
                );

        if (extension.length()
                > WebKeysCompras
                .DOCUMENT_LIBRARY_MAX_EXTENSION_LENGTH
                || !extension.matches(
                "^\\.[A-Za-z0-9]+$"
        )) {

            return "";
        }

        return extension.toLowerCase(
                Locale.ENGLISH
        );
    }

    public String obtenerExtensionSegura(
            String nombreOriginal) {

        return obtenerExtensionSeguraDocumento(
                nombreOriginal
        );
    }

    private static boolean esExtensionOrdenMedicaSegura(
            String extension) {

        return ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".png".equals(extension);
    }

    public boolean esExtensionOrdenMedica(
            String extension) {

        return esExtensionOrdenMedicaSegura(
                extension
        );
    }

    /**
     * Primitiva documental comun.
     */
    public static long obtenerMaximoTamanoDocumento()
            throws Exception {

        String valor =
                PropsUtil.get(
                        "dl.file.max.size"
                );

        if (WebKeysCompras.isEmpty(
                valor
        )) {

            return Long.MAX_VALUE;
        }

        try {
            long maximo =
                    Long.parseLong(
                            valor.trim()
                    );

            return maximo > 0L
                    ? maximo
                    : Long.MAX_VALUE;

        } catch (NumberFormatException e) {
            throw new Exception(
                    "La configuración dl.file.max.size no es válida.",
                    e
            );
        }
    }

    public long obtenerMaximoTamanoArchivo()
            throws Exception {

        return obtenerMaximoTamanoDocumento();
    }

    protected Date parseFechaDocumento(
            String value) throws Exception {

        String fecha =
                value != null
                        ? value.trim()
                        : "";

        if (WebKeysCompras.isEmpty(
                fecha
        )) {

            throw new Exception(
                    "Fecha de la Orden médica: "
                            + "debe informar una fecha."
            );
        }

        SimpleDateFormat formato =
                new SimpleDateFormat(
                        "yyyy-MM-dd"
                );

        formato.setLenient(
                false
        );

        ParsePosition posicion =
                new ParsePosition(
                        0
                );

        java.util.Date parsed =
                formato.parse(
                        fecha,
                        posicion
                );

        if (parsed == null
                || posicion.getIndex()
                != fecha.length()) {

            throw new Exception(
                    "Fecha de la Orden médica: "
                            + "el formato no es válido."
            );
        }

        return new Date(
                parsed.getTime()
        );
    }

    protected void validarFirmaImagen(
            File archivo,
            String contentTypeEsperado)
            throws Exception {

        InputStream input =
                null;

        try {
            input =
                    new FileInputStream(
                            archivo
                    );

            byte[] firma =
                    new byte[8];

            int leidos =
                    0;

            while (leidos < firma.length) {
                int cantidad =
                        input.read(
                                firma,
                                leidos,
                                firma.length - leidos
                        );

                if (cantidad < 0) {
                    break;
                }

                leidos +=
                        cantidad;
            }

            validarFirmaImagen(
                    firma,
                    leidos,
                    contentTypeEsperado
            );

        } finally {
            if (input != null) {
                try {
                    input.close();

                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar la lectura "
                                        + "de la firma de imagen.",
                                closeError
                        );
                    }
                }
            }
        }
    }

    private static void validarFirmaImagen(
            byte[] contenido,
            String contentTypeEsperado)
            throws Exception {

        validarFirmaImagen(
                contenido,
                contenido != null
                        ? contenido.length
                        : 0,
                contentTypeEsperado
        );
    }

    private static void validarFirmaImagen(
            byte[] firma,
            int leidos,
            String contentTypeEsperado)
            throws Exception {

        boolean valida;

        if (CONTENT_TYPE_PNG.equals(
                contentTypeEsperado
        )) {

            valida =
                    firma != null
                            && leidos >= 8
                            && (firma[0] & 0xFF) == 0x89
                            && firma[1] == 0x50
                            && firma[2] == 0x4E
                            && firma[3] == 0x47
                            && firma[4] == 0x0D
                            && firma[5] == 0x0A
                            && firma[6] == 0x1A
                            && firma[7] == 0x0A;

        } else {
            valida =
                    firma != null
                            && leidos >= 3
                            && (firma[0] & 0xFF) == 0xFF
                            && (firma[1] & 0xFF) == 0xD8
                            && (firma[2] & 0xFF) == 0xFF;
        }

        if (!valida) {
            throw new Exception(
                    "Orden médica: el contenido no coincide "
                            + "con una imagen JPEG o PNG válida."
            );
        }
    }

    private static void validarContentTypeCompatible(
            String origen,
            String contentType,
            String esperado)
            throws Exception {

        if (WebKeysCompras.isEmpty(
                contentType
        )
                || "application/octet-stream".equals(
                contentType
        )) {

            throw new Exception(
                    "Orden médica: el tipo MIME "
                            + origen
                            + " no identifica una imagen JPEG o PNG."
            );
        }

        if (!esperado.equals(
                contentType
        )) {

            throw new Exception(
                    "Orden médica: el tipo MIME "
                            + origen
                            + " no coincide con la extensión del archivo."
            );
        }
    }

    private static String normalizarContentType(
            String value) {

        if (value == null) {
            return "";
        }

        String contentType =
                value.trim()
                        .toLowerCase(
                                Locale.ENGLISH
                        );

        int separador =
                contentType.indexOf(';');

        if (separador >= 0) {
            contentType =
                    contentType.substring(
                            0,
                            separador
                    ).trim();
        }

        if ("image/jpg".equals(
                contentType
        )
                || "image/pjpeg".equals(
                contentType
        )) {

            return CONTENT_TYPE_JPEG;
        }

        if ("image/x-png".equals(
                contentType
        )) {

            return CONTENT_TYPE_PNG;
        }

        return contentType;
    }

    private static void validarNombreOriginalOrdenMedicaPersistido(
            String nombreOriginal)
            throws Exception {

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )
                || nombreOriginal.length() > 255
                || !nombreOriginal.equals(
                nombreOriginal.trim()
        )
                || WebKeysCompras.isEmpty(
                normalizarNombreArchivoSeguro(
                        nombreOriginal
                )
        )) {

            throw new Exception(
                    "El nombre original de la Orden médica es inválido."
            );
        }
    }

    private void validarOrdenMedicaPreparada(
            OrdenMedicaValidada ordenMedica)
            throws Exception {

        if (ordenMedica == null
                || ordenMedica.getArchivo() == null
                || !ordenMedica.getArchivo().exists()
                || ordenMedica.getArchivo().length() <= 0L
                || WebKeysCompras.isEmpty(
                ordenMedica.getNombreOriginal()
        )
                || !esExtensionOrdenMedica(
                ordenMedica.getExtension()
        )
                || ordenMedica.getFechaDocumento() == null
                || !(
                CONTENT_TYPE_JPEG.equals(
                        ordenMedica.getContentType()
                )
                        || CONTENT_TYPE_PNG.equals(
                        ordenMedica.getContentType()
                )
        )) {

            throw new Exception(
                    "La Orden médica validada no contiene "
                            + "todos los datos requeridos."
            );
        }

        String nombreOriginal =
                obtenerNombreArchivo(
                        ordenMedica.getNombreOriginal()
                );

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )
                || nombreOriginal.length()
                > WebKeysCompras
                .DOCUMENT_LIBRARY_MAX_TITLE_LENGTH) {

            throw new Exception(
                    "La Orden médica validada tiene "
                            + "un nombre de archivo inválido."
            );
        }

        String extensionNombre =
                obtenerExtensionSegura(
                        nombreOriginal
                );

        if (!ordenMedica.getExtension().equals(
                extensionNombre
        )) {

            throw new Exception(
                    "La extensión de la Orden médica "
                            + "no coincide con su nombre original."
            );
        }

        long maximoTamanoArchivo =
                obtenerMaximoTamanoArchivo();

        if (ordenMedica.getArchivo().length()
                > maximoTamanoArchivo) {

            throw new Exception(
                    "La Orden médica validada "
                            + "supera el tamaño permitido."
            );
        }

        String contentTypeEsperado =
                ".png".equals(
                        extensionNombre
                )
                        ? CONTENT_TYPE_PNG
                        : CONTENT_TYPE_JPEG;

        String contentTypeInformado =
                normalizarContentType(
                        ordenMedica.getContentType()
                );

        validarContentTypeCompatible(
                "informado",
                contentTypeInformado,
                contentTypeEsperado
        );

        validarFirmaImagen(
                ordenMedica.getArchivo(),
                contentTypeEsperado
        );
    }

    protected String detectarContentTypePorNombre(
            String nombreOriginal) {

        return MimeTypesUtil.getContentType(
                nombreOriginal
        );
    }

    /**
     * Primitiva comun para Presupuestos y Ordenes medicas.
     */
    public static void validarContextoDocumentLibrary(
            ServiceContext serviceContext)
            throws Exception {

        if (serviceContext == null) {
            throw new Exception(
                    "No se pudo preparar el contexto de Document Library."
            );
        }

        validarContextoDocumentLibrary(
                serviceContext.getScopeGroupId(),
                serviceContext.getUserId()
        );
    }

    /**
     * Primitiva comun para obtener la carpeta de documentos de Compras.
     */
    public static DLFolder obtenerOCrearFolderCompras(
            ServiceContext serviceContext)
            throws Exception {

        validarContextoDocumentLibrary(
                serviceContext
        );

        long groupId =
                serviceContext.getScopeGroupId();

        long userId =
                serviceContext.getUserId();

        try {
            return getFolderCompras(
                    groupId
            );

        } catch (NoSuchFolderException e) {
            if (_log.isDebugEnabled()) {
                _log.debug(
                        "La carpeta de documentos de Compras "
                                + "no existe; se intentara crear. groupId="
                                + groupId
                );
            }
        }

        try {
            return DLFolderLocalServiceUtil.addFolder(
                    userId,
                    groupId,
                    WebKeysCompras
                            .DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                    WebKeysCompras
                            .DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS,
                    WebKeysCompras
                            .DOCUMENT_LIBRARY_FOLDER_DESCRIPCION_COMPRAS,
                    serviceContext
            );

        } catch (Exception createError) {
            try {
                return getFolderCompras(
                        groupId
                );

            } catch (Exception lookupError) {
                _log.error(
                        "No se pudo crear ni recuperar "
                                + "la carpeta de documentos de Compras. "
                                + "groupId="
                                + groupId,
                        createError
                );

                throw createError;
            }
        }
    }

    private DLFolder obtenerOCrearFolderCompras()
            throws Exception {

        return obtenerOCrearFolderCompras(
                serviceContext
        );
    }

    private static DLFolder getFolderCompras(
            long groupId) throws Exception {

        return DLFolderLocalServiceUtil.getFolder(
                groupId,
                WebKeysCompras
                        .DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                WebKeysCompras
                        .DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
        );
    }

    private static void validarContextoDocumentLibrary(
            long groupId,
            long userId)
            throws Exception {

        if (groupId <= 0L) {
            throw new Exception(
                    "No se pudo determinar el groupId del sitio actual."
            );
        }

        if (userId <= 0L) {
            throw new Exception(
                    "No se pudo determinar el usuario "
                            + "de Document Library."
            );
        }
    }

    public OrdenMedicaValidada validarOrdenMedica(
            UploadPortletRequest uploadRequest)
            throws Exception {

        String fecha =
                uploadRequest != null
                        ? ParamUtil.getString(
                        uploadRequest,
                        PARAM_FECHA_ORDEN_MEDICA,
                        null
                )
                        : null;

        return validarOrdenMedica(
                uploadRequest,
                PARAM_ARCHIVO_ORDEN_MEDICA,
                fecha
        );
    }

    public OrdenMedicaValidada validarOrdenMedica(
            UploadPortletRequest uploadRequest,
            String fechaNormalizada)
            throws Exception {

        return validarOrdenMedica(
                uploadRequest,
                PARAM_ARCHIVO_ORDEN_MEDICA,
                fechaNormalizada
        );
    }

    public OrdenMedicaValidada validarOrdenMedica(
            UploadPortletRequest uploadRequest,
            String nombreCampoArchivo,
            String fechaNormalizada)
            throws Exception {

        return validarOrdenMedica(
                uploadRequest,
                nombreCampoArchivo,
                fechaNormalizada,
                null
        );
    }

    public OrdenMedicaValidada validarOrdenMedica(
            UploadPortletRequest uploadRequest,
            String nombreCampoArchivo,
            String fechaNormalizada,
            String numeroReceta)
            throws Exception {

        if (uploadRequest == null) {
            throw new Exception(
                    "No se recibió el formulario multipart "
                            + "de la Orden médica."
            );
        }

        if (WebKeysCompras.isEmpty(
                nombreCampoArchivo
        )) {

            throw new Exception(
                    "No se informó el campo de archivo "
                            + "de la Orden médica."
            );
        }

        nombreCampoArchivo =
                nombreCampoArchivo.trim();

        Date fechaDocumento =
                parseFechaDocumento(
                        fechaNormalizada
                );

        File archivo =
                uploadRequest.getFile(
                        nombreCampoArchivo
                );

        String nombreOriginal =
                obtenerNombreArchivo(
                        uploadRequest.getFileName(
                                nombreCampoArchivo
                        )
                );

        if (archivo == null
                || !archivo.exists()
                || archivo.length() <= 0L) {

            throw new Exception(
                    "Orden médica: debe seleccionar "
                            + "una imagen no vacía."
            );
        }

        if (WebKeysCompras.isEmpty(
                nombreOriginal
        )) {

            throw new Exception(
                    "Orden médica: el nombre del archivo no es válido."
            );
        }

        long maximoTamanoArchivo =
                obtenerMaximoTamanoArchivo();

        if (maximoTamanoArchivo > 0L
                && archivo.length()
                > maximoTamanoArchivo) {

            throw new Exception(
                    "Orden médica: el archivo supera "
                            + "el tamaño permitido."
            );
        }

        String extension =
                obtenerExtensionSegura(
                        nombreOriginal
                );

        if (!esExtensionOrdenMedica(
                extension
        )) {

            throw new Exception(
                    "Orden médica: sólo se permiten "
                            + "archivos JPG, JPEG o PNG."
            );
        }

        String contentTypeEsperado =
                ".png".equals(
                        extension
                )
                        ? CONTENT_TYPE_PNG
                        : CONTENT_TYPE_JPEG;

        validarFirmaImagen(
                archivo,
                contentTypeEsperado
        );

        return new OrdenMedicaValidada(
                archivo,
                nombreOriginal,
                extension,
                contentTypeEsperado,
                fechaDocumento,
                numeroReceta
        );
    }

    public static final class OrdenMedicaContenido {

        private final byte[] contenido;
        private final String nombreOriginal;
        private final String contentType;

        private OrdenMedicaContenido(
                byte[] contenido,
                String nombreOriginal,
                String contentType) {

            this.contenido =
                    contenido;

            this.nombreOriginal =
                    nombreOriginal;

            this.contentType =
                    contentType;
        }

        public byte[] getContenido() {
            return contenido;
        }

        public String getNombreOriginal() {
            return nombreOriginal;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static byte[] leerContenidoDocumentLibrary(
            DLFileEntry entry) throws Exception {

        if (entry == null
                || entry.getFileEntryId() <= 0L) {

            throw new Exception(
                    "No se pudo recuperar el documento "
                            + "desde Document Library."
            );
        }

        long maximoTamano =
                obtenerMaximoTamanoDocumento();

        if (entry.getSize() <= 0
                || entry.getSize() > maximoTamano) {

            throw new Exception(
                    "El documento persistido tiene un tamaño inválido."
            );
        }

        InputStream input =
                null;

        try {
            input =
                    DLLocalServiceUtil.getFileAsStream(
                            entry.getCompanyId(),
                            entry.getFolderId(),
                            entry.getName(),
                            entry.getVersion()
                    );

            if (input == null) {
                throw new Exception(
                        "Document Library no devolvió el documento."
                );
            }

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream(
                            entry.getSize()
                    );

            byte[] buffer =
                    new byte[8192];

            long total =
                    0L;

            int cantidad;

            while ((cantidad = input.read(buffer)) >= 0) {
                if (cantidad == 0) {
                    continue;
                }

                total += cantidad;

                if (total > maximoTamano) {
                    throw new Exception(
                            "El documento supera dl.file.max.size."
                    );
                }

                output.write(
                        buffer,
                        0,
                        cantidad
                );
            }

            byte[] contenido =
                    output.toByteArray();

            if (contenido.length
                    != entry.getSize()) {

                throw new Exception(
                        "El tamaño leído del documento "
                                + "no coincide con Document Library."
                );
            }

            return contenido;

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar la lectura "
                                        + "del documento.",
                                closeError
                        );
                    }
                }
            }
        }
    }
}