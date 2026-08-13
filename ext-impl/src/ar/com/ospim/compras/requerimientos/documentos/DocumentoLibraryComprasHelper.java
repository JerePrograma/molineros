package ar.com.ospim.compras.requerimientos.documentos;

import ar.com.ospim.compras.WebKeysCompras;

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
            LogFactoryUtil.getLog(DocumentoLibraryComprasHelper.class);

    public static final String TITULO_ORDEN_MEDICA = "Orden médica";
    public static final String PARAM_ARCHIVO_ORDEN_MEDICA = "orden_medica";
    public static final String PARAM_FECHA_ORDEN_MEDICA =
            "fecha_orden_medica";

    private static final String DESCRIPCION_ORDEN_MEDICA = "Orden médica";
    private static final String CONTENT_TYPE_JPEG = "image/jpeg";
    private static final String CONTENT_TYPE_PNG = "image/png";

    private final ServiceContext serviceContext;
    private final long groupId;
    private final long userId;

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

        return new DocumentoLibraryComprasHelper(serviceContext);
    }

    protected DocumentoLibraryComprasHelper(
            ServiceContext serviceContext) throws Exception {

        if (serviceContext == null) {
            throw new Exception(
                    "No se pudo preparar el contexto de Document Library."
            );
        }

        this.serviceContext = serviceContext;
        this.groupId = serviceContext.getScopeGroupId();
        this.userId = serviceContext.getUserId();

        validarContextoDocumentLibrary(groupId, userId);
    }


    public DocumentoComprasCreado crearOrdenMedica(
            int idRequerimientoCompra,
            OrdenMedicaValidada ordenMedica) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "El requerimiento de compra no es v\u00e1lido."
            );
        }

        validarOrdenMedicaPreparada(ordenMedica);

        DLFolder folder = obtenerOCrearFolderCompras();
        String identificador = UUID.randomUUID()
                .toString()
                .replace("-", "");
        String nombrePersistido = construirNombreOrdenMedica(
                idRequerimientoCompra,
                identificador,
                ordenMedica.getExtension()
        );

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.addFileEntry(
                        userId,
                        folder.getFolderId(),
                        nombrePersistido,
                        TITULO_ORDEN_MEDICA,
                        DESCRIPCION_ORDEN_MEDICA,
                        "",
                        ordenMedica.getArchivo(),
                        serviceContext
                );

        try {
            if (entry == null || entry.getFileEntryId() <= 0L) {
                throw new Exception(
                        "Document Library no devolvi\u00f3 una Orden médica v\u00e1lida."
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

            validarIdentidadDocumento(documento);

            if (!coincideIdentidad(documento, entry)
                    || !TITULO_ORDEN_MEDICA.equals(entry.getTitle())) {

                throw new Exception(
                        "La Orden médica creada no conserva la identidad requerida."
                );
            }

            return documento;
        } catch (Exception postAddError) {
            if (entry != null && entry.getFileEntryId() > 0L) {
                try {
                    DLFileEntryLocalServiceUtil.deleteFileEntry(entry);
                } catch (Exception cleanupError) {
                    _log.error(
                            "Fall\u00f3 la compensaci\u00f3n de una Orden médica "
                                    + "creada pero no validada. fileEntryId="
                                    + entry.getFileEntryId()
                                    + ", folderId=" + entry.getFolderId()
                                    + ", nombre=" + entry.getName(),
                            cleanupError
                    );
                }
            }

            throw postAddError;
        }
    }

    public void eliminarDocumento(
            DocumentoComprasCreado documento) throws Exception {

        validarIdentidadDocumento(documento);

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.getDLFileEntry(
                        documento.getFileEntryId()
                );

        if (!coincideIdentidad(documento, entry)) {
            throw new Exception(
                    "El documento a compensar no coincide con la identidad persistida."
            );
        }

        DLFileEntryLocalServiceUtil.deleteFileEntry(entry);
    }

    public void validarIdentidadDocumento(
            DocumentoComprasCreado documento) throws Exception {

        if (documento == null
                || documento.getGroupId() <= 0L
                || documento.getFolderId() <= 0L
                || documento.getFileEntryId() <= 0L
                || WebKeysCompras.isEmpty(documento.getNombrePersistido())
                || WebKeysCompras.isEmpty(documento.getTitulo())) {

            throw new Exception(
                    "La identidad del documento de Compras no es v\u00e1lida."
            );
        }
    }

    public boolean coincideIdentidad(
            DocumentoComprasCreado documento,
            DLFileEntry entry) {

        if (documento == null || entry == null) {
            return false;
        }

        boolean coincide = entry.getFileEntryId()
                        == documento.getFileEntryId()
                && entry.getGroupId() == documento.getGroupId()
                && entry.getFolderId() == documento.getFolderId()
                && documento.getNombrePersistido().equals(entry.getName());

        return coincide
                && (WebKeysCompras.isEmpty(documento.getUuid())
                || documento.getUuid().equals(entry.getUuid()));
    }

    public String construirNombreOrdenMedica(
            int idRequerimientoCompra,
            String identificador,
            String extension) throws Exception {

        if (idRequerimientoCompra <= 0
                || WebKeysCompras.isEmpty(identificador)
                || !identificador.matches("^[A-Za-z0-9]+$")
                || !esExtensionOrdenMedica(extension)) {

            throw new Exception(
                    "No se pudo construir el nombre persistido de la Orden médica."
            );
        }

        return "ORDEN-MEDICA-COMPRA-"
                + idRequerimientoCompra
                + "-"
                + identificador
                + extension;
    }

    public String obtenerNombreArchivo(String filename) {
        if (filename == null) {
            return "";
        }

        String nombre = filename.trim();

        if (WebKeysCompras.isEmpty(nombre)
                || ".".equals(nombre)
                || "..".equals(nombre)
                || nombre.indexOf("..") >= 0
                || nombre.indexOf('/') >= 0
                || nombre.indexOf('\\') >= 0
                || nombre.matches(".*\\p{Cntrl}.*")) {

            return "";
        }

        return nombre;
    }

    public String obtenerExtensionSegura(String nombreOriginal) {
        if (WebKeysCompras.isEmpty(nombreOriginal)) {
            return "";
        }

        int posicionExtension = nombreOriginal.lastIndexOf('.');

        if (posicionExtension < 0
                || posicionExtension >= nombreOriginal.length() - 1) {
            return "";
        }

        String extension = nombreOriginal.substring(posicionExtension);

        if (extension.length()
                > WebKeysCompras.DOCUMENT_LIBRARY_MAX_EXTENSION_LENGTH
                || !extension.matches("^\\.[A-Za-z0-9]+$")) {

            return "";
        }

        return extension.toLowerCase(Locale.ENGLISH);
    }

    public boolean esExtensionOrdenMedica(String extension) {
        return ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".png".equals(extension);
    }

    public long obtenerMaximoTamanoArchivo() throws Exception {
        String valor = PropsUtil.get("dl.file.max.size");

        if (WebKeysCompras.isEmpty(valor)) {
            return Long.MAX_VALUE;
        }

        try {
            long maximo = Long.parseLong(valor.trim());
            return maximo > 0L ? maximo : Long.MAX_VALUE;
        } catch (NumberFormatException e) {
            throw new Exception(
                    "La configuraci\u00f3n dl.file.max.size no es v\u00e1lida.",
                    e
            );
        }
    }

    protected Date parseFechaDocumento(String value) throws Exception {
        String fecha = value != null ? value.trim() : "";

        if (WebKeysCompras.isEmpty(fecha)) {
            throw new Exception(
                    "Fecha de la Orden médica: debe informar una fecha."
            );
        }

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        ParsePosition posicion = new ParsePosition(0);
        java.util.Date parsed = formato.parse(fecha, posicion);

        if (parsed == null || posicion.getIndex() != fecha.length()) {
            throw new Exception(
                    "Fecha de la Orden médica: el formato no es v\u00e1lido."
            );
        }

        return new Date(parsed.getTime());
    }

    protected void validarFirmaImagen(
            File archivo,
            String contentTypeEsperado) throws Exception {

        InputStream input = null;

        try {
            input = new FileInputStream(archivo);
            byte[] firma = new byte[8];
            int leidos = 0;

            while (leidos < firma.length) {
                int cantidad = input.read(
                        firma,
                        leidos,
                        firma.length - leidos
                );

                if (cantidad < 0) {
                    break;
                }

                leidos += cantidad;
            }

            boolean valida;

            if (CONTENT_TYPE_PNG.equals(contentTypeEsperado)) {
                valida = leidos >= 8
                        && (firma[0] & 0xFF) == 0x89
                        && firma[1] == 0x50
                        && firma[2] == 0x4E
                        && firma[3] == 0x47
                        && firma[4] == 0x0D
                        && firma[5] == 0x0A
                        && firma[6] == 0x1A
                        && firma[7] == 0x0A;
            } else {
                valida = leidos >= 3
                        && (firma[0] & 0xFF) == 0xFF
                        && (firma[1] & 0xFF) == 0xD8
                        && (firma[2] & 0xFF) == 0xFF;
            }

            if (!valida) {
                throw new Exception(
                        "Orden médica: el contenido no coincide con una imagen JPEG o PNG v\u00e1lida."
                );
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar la lectura de la firma de imagen.",
                                closeError
                        );
                    }
                }
            }
        }
    }

    private void validarContentTypeCompatible(
            String origen,
            String contentType,
            String esperado) throws Exception {

        if (WebKeysCompras.isEmpty(contentType)
                || "application/octet-stream".equals(contentType)) {

            throw new Exception(
                    "Orden médica: el tipo MIME "
                            + origen
                            + " no identifica una imagen JPEG o PNG."
            );
        }

        if (!esperado.equals(contentType)) {
            throw new Exception(
                    "Orden médica: el tipo MIME "
                            + origen
                            + " no coincide con la extensi\u00f3n del archivo."
            );
        }
    }

    private String normalizarContentType(String value) {
        if (value == null) {
            return "";
        }

        String contentType = value.trim().toLowerCase(Locale.ENGLISH);
        int separador = contentType.indexOf(';');

        if (separador >= 0) {
            contentType = contentType.substring(0, separador).trim();
        }

        if ("image/jpg".equals(contentType)
                || "image/pjpeg".equals(contentType)) {
            return CONTENT_TYPE_JPEG;
        }

        if ("image/x-png".equals(contentType)) {
            return CONTENT_TYPE_PNG;
        }

        return contentType;
    }

    private void validarOrdenMedicaPreparada(
            OrdenMedicaValidada ordenMedica) throws Exception {

        if (ordenMedica == null
                || ordenMedica.getArchivo() == null
                || !ordenMedica.getArchivo().exists()
                || ordenMedica.getArchivo().length() <= 0L
                || WebKeysCompras.isEmpty(ordenMedica.getNombreOriginal())
                || !esExtensionOrdenMedica(ordenMedica.getExtension())
                || ordenMedica.getFechaDocumento() == null
                || !(CONTENT_TYPE_JPEG.equals(ordenMedica.getContentType())
                || CONTENT_TYPE_PNG.equals(ordenMedica.getContentType()))) {

            throw new Exception(
                    "La Orden médica validada no contiene todos los datos requeridos."
            );
        }

        String nombreOriginal = obtenerNombreArchivo(
                ordenMedica.getNombreOriginal()
        );

        if (WebKeysCompras.isEmpty(nombreOriginal)
                || nombreOriginal.length()
                > WebKeysCompras.DOCUMENT_LIBRARY_MAX_TITLE_LENGTH) {

            throw new Exception(
                    "La Orden médica validada tiene un nombre de archivo inv\u00e1lido."
            );
        }

        String extensionNombre = obtenerExtensionSegura(nombreOriginal);

        if (!ordenMedica.getExtension().equals(extensionNombre)) {
            throw new Exception(
                    "La extensi\u00f3n de la Orden médica no coincide con su nombre original."
            );
        }

        long maximoTamanoArchivo = obtenerMaximoTamanoArchivo();

        if (ordenMedica.getArchivo().length() > maximoTamanoArchivo) {
            throw new Exception(
                    "La Orden médica validada supera el tama\u00f1o permitido."
            );
        }

        String contentTypeEsperado = ".png".equals(extensionNombre)
                ? CONTENT_TYPE_PNG
                : CONTENT_TYPE_JPEG;
        String contentTypeInformado = normalizarContentType(
                ordenMedica.getContentType()
        );
        String contentTypeDetectado = normalizarContentType(
                detectarContentTypePorNombre(nombreOriginal)
        );

        validarContentTypeCompatible(
                "informado",
                contentTypeInformado,
                contentTypeEsperado
        );
        validarContentTypeCompatible(
                "detectado",
                contentTypeDetectado,
                contentTypeEsperado
        );
        validarFirmaImagen(
                ordenMedica.getArchivo(),
                contentTypeEsperado
        );
    }

    protected String detectarContentTypePorNombre(String nombreOriginal) {
        return MimeTypesUtil.getContentType(nombreOriginal);
    }

    private DLFolder obtenerOCrearFolderCompras() throws Exception {
        try {
            return getFolderCompras();
        } catch (NoSuchFolderException e) {
            if (_log.isDebugEnabled()) {
                _log.debug(
                        "La carpeta de documentos de Compras no existe; se crear\u00e1. groupId="
                                + groupId
                );
            }
        }

        try {
            return DLFolderLocalServiceUtil.addFolder(
                    userId,
                    groupId,
                    WebKeysCompras.DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                    WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS,
                    WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_DESCRIPCION_COMPRAS,
                    serviceContext
            );
        } catch (Exception createError) {
            try {
                return getFolderCompras();
            } catch (Exception lookupError) {
                _log.error(
                        "No se pudo crear ni recuperar la carpeta de documentos de Compras. groupId="
                                + groupId,
                        createError
                );
                throw createError;
            }
        }
    }

    private DLFolder getFolderCompras() throws Exception {
        return DLFolderLocalServiceUtil.getFolder(
                groupId,
                WebKeysCompras.DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
        );
    }

    private void validarContextoDocumentLibrary(
            long groupId,
            long userId) throws Exception {

        if (groupId <= 0L) {
            throw new Exception(
                    "No se pudo determinar el groupId del sitio actual."
            );
        }

        if (userId <= 0L) {
            throw new Exception(
                    "No se pudo determinar el usuario de Document Library."
            );
        }
    }

    public OrdenMedicaValidada validarOrdenMedica(
            UploadPortletRequest uploadRequest) throws Exception {

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
            String fechaNormalizada) throws Exception {

        /*
         * Contrato histórico.
         *
         * Conserva exactamente el nombre del campo original y delega
         * al nuevo método parametrizado.
         */
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
                    "No se recibió el formulario multipart de la Orden médica."
            );
        }

        if (WebKeysCompras.isEmpty(nombreCampoArchivo)) {
            throw new Exception(
                    "No se informó el campo de archivo de la Orden médica."
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
                    "Orden médica: debe seleccionar una imagen no vacía."
            );
        }

        if (WebKeysCompras.isEmpty(nombreOriginal)) {
            throw new Exception(
                    "Orden médica: el nombre del archivo no es válido."
            );
        }

        long maximoTamanoArchivo =
                obtenerMaximoTamanoArchivo();

        if (maximoTamanoArchivo > 0L
                && archivo.length() > maximoTamanoArchivo) {

            throw new Exception(
                    "Orden médica: el archivo supera el tamaño permitido."
            );
        }

        String extension =
                obtenerExtensionSegura(
                        nombreOriginal
                );

        if (!esExtensionOrdenMedica(extension)) {
            throw new Exception(
                    "Orden médica: sólo se permiten archivos JPG, JPEG o PNG."
            );
        }

        String contentTypeUpload =
                normalizarContentType(
                        uploadRequest.getContentType(
                                nombreCampoArchivo
                        )
                );

        String contentTypeDetectado =
                normalizarContentType(
                        detectarContentTypePorNombre(
                                nombreOriginal
                        )
                );

        String contentTypeEsperado =
                ".png".equals(extension)
                        ? CONTENT_TYPE_PNG
                        : CONTENT_TYPE_JPEG;

        validarContentTypeCompatible(
                "declarado",
                contentTypeUpload,
                contentTypeEsperado
        );

        validarContentTypeCompatible(
                "detectado",
                contentTypeDetectado,
                contentTypeEsperado
        );

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
}
