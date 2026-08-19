package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Reglas documentales y compensacion de presupuestos de Compras.
 *
 * No interpreta requests ni decide forwards. Tampoco utiliza JDBC: las
 * asociaciones persistentes se delegan al Helper funcional de edicion.
 */
public final class PresupuestoCompraHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    PresupuestoCompraHelper.class
            );

    private final EditarRequerimientoCompraHelper requerimientoHelper =
            new EditarRequerimientoCompraHelper();

    public int guardarPresupuestos(
            int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas,
            ServiceContext serviceContext,
            String usuario) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "El requerimiento de compra no es valido."
            );
        }

        validarCantidadPresupuestos(
                entradas != null
                        ? entradas.size()
                        : 0
        );

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        validarAccesoCarga(requerimiento);

        List<PrestadorCotizacion> prestadores =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPrestadoresEnviados(
                                idRequerimientoCompra
                        );

        List<PresupuestoValidado> presupuestos =
                validarPresupuestos(
                        idRequerimientoCompra,
                        entradas,
                        prestadores,
                        obtenerMaximoTamanoArchivo()
                );

        /*
         * Revalidacion inmediatamente antes de crear documentos.
         */
        RequerimientoCompra actual =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        validarAccesoCarga(actual);
        validarContextoDocumentLibrary(serviceContext);

        long groupId = serviceContext.getScopeGroupId();
        long userId = serviceContext.getUserId();

        DLFolder folder =
                obtenerOCrearFolderCompras(
                        groupId,
                        userId,
                        serviceContext
                );

        guardarPresupuestosValidados(
                idRequerimientoCompra,
                presupuestos,
                userId,
                folder.getFolderId(),
                normalizarUsuario(usuario),
                serviceContext
        );

        return presupuestos.size();
    }

    public void borrarPresupuesto(
            int idRequerimientoCompra,
            int idRequerimientoPresupuesto,
            long scopeGroupId,
            String usuario) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "No se encontro el requerimiento de compra informado."
            );
        }

        if (idRequerimientoPresupuesto <= 0) {
            throw new Exception(
                    "Debe informar el presupuesto a eliminar."
            );
        }

        if (scopeGroupId <= 0L) {
            throw new Exception(
                    "No se pudo determinar el sitio actual."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        validarAccesoCarga(requerimiento);

        RequerimientoCompraPresupuesto presupuesto =
                BusquedaRequerimientoCompraServiceUtil
                        .getPresupuesto(
                                idRequerimientoPresupuesto,
                                idRequerimientoCompra
                        );

        if (presupuesto == null) {
            throw new Exception(
                    "El presupuesto informado no pertenece "
                            + "al requerimiento actual."
            );
        }

        validarIdentidadAsociacion(presupuesto);

        if (presupuesto.getDlGroupId().longValue()
                != scopeGroupId) {
            throw new Exception(
                    "El presupuesto informado no pertenece al sitio actual."
            );
        }

        DLFileEntry fileEntry =
                DLFileEntryLocalServiceUtil.getDLFileEntry(
                        presupuesto.getDlFileEntryId().longValue()
                );

        validarDocumentoAsociado(
                presupuesto,
                fileEntry
        );

        boolean asociacionDadaDeBaja =
                requerimientoHelper.darDeBajaPresupuesto(
                        idRequerimientoPresupuesto,
                        idRequerimientoCompra,
                        normalizarUsuario(usuario)
                );

        if (!asociacionDadaDeBaja) {
            throw new Exception(
                    "El presupuesto ya fue eliminado o fue modificado "
                            + "por otro proceso."
            );
        }

        try {
            eliminarArchivoPresupuesto(
                    fileEntry.getFolderId(),
                    fileEntry.getName()
            );

        } catch (Exception deleteError) {
            try {
                boolean reactivada =
                        requerimientoHelper.reactivarPresupuesto(
                                idRequerimientoPresupuesto,
                                idRequerimientoCompra
                        );

                if (!reactivada) {
                    _log.error(
                            "La eliminacion fisica del presupuesto fallo "
                                    + "y la asociacion no pudo reactivarse. "
                                    + "idRequerimientoPresupuesto="
                                    + idRequerimientoPresupuesto
                    );
                }
            } catch (Exception reactivarError) {
                _log.error(
                        "La eliminacion fisica del presupuesto fallo y "
                                + "tambien fallo la reactivacion de su asociacion. "
                                + "idRequerimientoPresupuesto="
                                + idRequerimientoPresupuesto,
                        reactivarError
                );
            }

            throw deleteError;
        }
    }

    private List<PresupuestoValidado> validarPresupuestos(
            int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas,
            List<PrestadorCotizacion> prestadores,
            long maximoTamanoArchivo) throws Exception {

        Map<Integer, PrestadorCotizacion> prestadoresPorId =
                new HashMap<Integer, PrestadorCotizacion>();

        for (int i = 0;
             prestadores != null && i < prestadores.size();
             i++) {

            PrestadorCotizacion prestador = prestadores.get(i);

            if (prestador != null
                    && prestador.getIdPrestador() > 0
                    && WebKeysCompras.ENVIO_ENVIADO.equals(
                    prestador.getEstadoEnvio()
            )) {
                prestadoresPorId.put(
                        Integer.valueOf(prestador.getIdPrestador()),
                        prestador
                );
            }
        }

        List<PresupuestoValidado> validados =
                new ArrayList<PresupuestoValidado>();

        Set<Integer> prestadoresSeleccionados =
                new HashSet<Integer>();

        for (int i = 0; i < entradas.size(); i++) {
            PresupuestoEntrada entrada = entradas.get(i);

            if (entrada == null || entrada.getIndice() != i) {
                throw new Exception(
                        "El indice del presupuesto "
                                + (i + 1)
                                + " no es valido."
                );
            }

            File archivo = entrada.getArchivo();

            if (archivo == null
                    || !archivo.exists()
                    || archivo.length() <= 0L) {
                throw new Exception(
                        "Debe seleccionar el archivo del presupuesto "
                                + (i + 1)
                                + "."
                );
            }

            if (maximoTamanoArchivo > 0L
                    && archivo.length() > maximoTamanoArchivo) {
                throw new Exception(
                        "El presupuesto "
                                + (i + 1)
                                + " supera el tamano permitido."
                );
            }

            String nombreOriginal =
                    obtenerNombreArchivo(
                            entrada.getNombreOriginal()
                    );

            if (WebKeysCompras.isEmpty(nombreOriginal)) {
                throw new Exception(
                        "El nombre del presupuesto "
                                + (i + 1)
                                + " no es valido."
                );
            }

            String extension =
                    obtenerExtensionSegura(
                            nombreOriginal
                    );

            if (!".pdf".equals(extension)) {
                throw new Exception(
                        "El presupuesto "
                                + (i + 1)
                                + " debe presentarse en formato PDF."
                );
            }

            validarContenidoPdf(
                    archivo,
                    i + 1
            );

            if (entrada.getIdPrestador() <= 0) {
                throw new Exception(
                        "Debe seleccionar el prestador del presupuesto "
                                + (i + 1)
                                + "."
                );
            }

            PrestadorCotizacion prestador =
                    prestadoresPorId.get(
                            Integer.valueOf(
                                    entrada.getIdPrestador()
                            )
                    );

            if (prestador == null) {
                throw new Exception(
                        "El prestador del presupuesto "
                                + (i + 1)
                                + " no fue notificado correctamente "
                                + "para este requerimiento."
                );
            }

            Integer idPrestador =
                    Integer.valueOf(
                            prestador.getIdPrestador()
                    );

            if (!prestadoresSeleccionados.add(idPrestador)) {
                throw new Exception(
                        "El prestador del presupuesto "
                                + (i + 1)
                                + " esta repetido. Solo puede cargarse "
                                + "un archivo por prestador."
                );
            }

            String identificador =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "");

            validados.add(
                    new PresupuestoValidado(
                            archivo,
                            nombreOriginal,
                            prestador,
                            construirNombrePersistido(
                                    idRequerimientoCompra,
                                    prestador.getIdPrestador(),
                                    identificador,
                                    extension
                            ),
                            construirTituloVisible(
                                    idRequerimientoCompra,
                                    nombreOriginal,
                                    identificador
                            ),
                            prestador.getEtiquetaVisible()
                    )
            );
        }

        return validados;
    }

    private void guardarPresupuestosValidados(
            int idRequerimientoCompra,
            List<PresupuestoValidado> presupuestos,
            long userId,
            long folderId,
            String usuario,
            ServiceContext serviceContext) throws Exception {

        List<PresupuestoCreado> creados =
                new ArrayList<PresupuestoCreado>();

        try {
            for (int i = 0; i < presupuestos.size(); i++) {
                PresupuestoValidado presupuesto = presupuestos.get(i);
                DocumentoPresupuestoCreado documento = null;

                try {
                    documento =
                            crearArchivoPresupuesto(
                                    userId,
                                    folderId,
                                    presupuesto,
                                    serviceContext
                            );

                    RequerimientoCompraPresupuesto asociacion =
                            registrarAsociacionPresupuesto(
                                    idRequerimientoCompra,
                                    presupuesto,
                                    documento,
                                    usuario
                            );

                    creados.add(
                            new PresupuestoCreado(
                                    documento,
                                    asociacion
                            )
                    );

                } catch (Exception errorCreacion) {
                    if (documento != null) {
                        try {
                            eliminarArchivoPresupuesto(
                                    documento.getFolderId(),
                                    documento.getNombre()
                            );
                        } catch (Exception cleanupError) {
                            _log.error(
                                    "No se pudo eliminar el documento creado "
                                            + "antes de fallar su asociacion. "
                                            + "fileEntryId="
                                            + documento.getFileEntryId(),
                                    cleanupError
                            );
                        }
                    }

                    throw errorCreacion;
                }
            }

        } catch (Exception errorPrincipal) {
            compensarPresupuestosCreados(
                    idRequerimientoCompra,
                    creados,
                    usuario
            );

            throw traducirErrorDocumento(errorPrincipal);
        }
    }

    private void compensarPresupuestosCreados(
            int idRequerimientoCompra,
            List<PresupuestoCreado> creados,
            String usuario) {

        for (int i = creados.size() - 1; i >= 0; i--) {
            PresupuestoCreado creado = creados.get(i);

            if (creado == null
                    || creado.getAsociacion() == null
                    || creado.getDocumento() == null) {
                continue;
            }

            Integer idAsociacion =
                    creado.getAsociacion()
                            .getIdRequerimientoPresupuesto();

            if (idAsociacion == null
                    || idAsociacion.intValue() <= 0) {
                continue;
            }

            boolean asociacionDadaDeBaja = false;

            try {
                asociacionDadaDeBaja =
                        requerimientoHelper.darDeBajaPresupuesto(
                                idAsociacion.intValue(),
                                idRequerimientoCompra,
                                usuario
                        );
            } catch (Exception cleanupSqlError) {
                _log.error(
                        "No se pudo dar de baja una asociacion durante "
                                + "la compensacion de presupuestos. "
                                + "idRequerimientoPresupuesto="
                                + idAsociacion,
                        cleanupSqlError
                );
            }

            if (!asociacionDadaDeBaja) {
                continue;
            }

            try {
                eliminarArchivoPresupuesto(
                        creado.getDocumento().getFolderId(),
                        creado.getDocumento().getNombre()
                );
            } catch (Exception cleanupFileError) {
                _log.error(
                        "No se pudo eliminar un presupuesto durante "
                                + "la compensacion. fileEntryId="
                                + creado.getDocumento().getFileEntryId(),
                        cleanupFileError
                );

                try {
                    requerimientoHelper.reactivarPresupuesto(
                            idAsociacion.intValue(),
                            idRequerimientoCompra
                    );
                } catch (Exception reactivarError) {
                    _log.error(
                            "No se pudo reactivar la asociacion despues de "
                                    + "fallar la eliminacion del documento. "
                                    + "idRequerimientoPresupuesto="
                                    + idAsociacion,
                            reactivarError
                    );
                }
            }
        }
    }

    private DocumentoPresupuestoCreado crearArchivoPresupuesto(
            long userId,
            long folderId,
            PresupuestoValidado presupuesto,
            ServiceContext serviceContext) throws Exception {

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil.addOrOverwriteFileEntry(
                        userId,
                        folderId,
                        presupuesto.getNombrePersistido(),
                        presupuesto.getNombreOriginal(),
                        presupuesto.getTitulo(),
                        presupuesto.getDescripcionPrestador(),
                        "",
                        presupuesto.getArchivo(),
                        serviceContext
                );

        if (entry == null || entry.getFileEntryId() <= 0L) {
            throw new Exception(
                    "Document Library no devolvio un documento valido."
            );
        }

        return new DocumentoPresupuestoCreado(
                entry.getGroupId(),
                entry.getFolderId(),
                entry.getFileEntryId(),
                entry.getUuid(),
                entry.getName(),
                entry.getTitle()
        );
    }

    private RequerimientoCompraPresupuesto registrarAsociacionPresupuesto(
            int idRequerimientoCompra,
            PresupuestoValidado presupuesto,
            DocumentoPresupuestoCreado documento,
            String usuario) throws Exception {

        RequerimientoCompraPresupuesto asociacion =
                new RequerimientoCompraPresupuesto();

        asociacion.setIdRequerimiento(
                Integer.valueOf(idRequerimientoCompra)
        );
        asociacion.setIdPrestador(
                Integer.valueOf(presupuesto.getIdPrestador())
        );
        asociacion.setDlGroupId(
                Long.valueOf(documento.getGroupId())
        );
        asociacion.setDlFolderId(
                Long.valueOf(documento.getFolderId())
        );
        asociacion.setDlFileEntryId(
                Long.valueOf(documento.getFileEntryId())
        );
        asociacion.setDlFileUuid(documento.getUuid());
        asociacion.setNombreOriginal(presupuesto.getNombreOriginal());
        asociacion.setNombrePersistido(documento.getNombre());
        asociacion.setTitulo(documento.getTitulo());
        asociacion.setDescripcionPrestador(
                presupuesto.getDescripcionPrestador()
        );

        int id =
                requerimientoHelper.registrarPresupuesto(
                        asociacion,
                        usuario
                );

        if (id <= 0) {
            throw new Exception(
                    "No se pudo obtener el identificador de la asociacion "
                            + "del presupuesto."
            );
        }

        asociacion.setIdRequerimientoPresupuesto(
                Integer.valueOf(id)
        );

        return asociacion;
    }

    private void validarCantidadPresupuestos(int cantidad) throws Exception {
        if (cantidad <= 0
                || cantidad > WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA) {
            throw new Exception(
                    "La cantidad de presupuestos debe estar entre 1 y "
                            + WebKeysCompras.MAX_PRESUPUESTOS_POR_CARGA
                            + "."
            );
        }
    }

    private void validarAccesoCarga(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null
                || requerimiento.getIdRequerimientoCompra() <= 0) {
            throw new Exception(
                    "No se encontro el requerimiento de compra informado."
            );
        }

        if (!requerimiento.puedeAdministrarPresupuestos()) {
            throw new Exception(
                    "Solo se pueden administrar presupuestos "
                            + "en estado A COTIZAR. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible()
                            + "."
            );
        }
    }

    private void validarIdentidadAsociacion(
            RequerimientoCompraPresupuesto presupuesto) throws Exception {

        if (presupuesto.getDlGroupId() == null
                || presupuesto.getDlGroupId().longValue() <= 0L
                || presupuesto.getDlFolderId() == null
                || presupuesto.getDlFolderId().longValue() <= 0L
                || presupuesto.getDlFileEntryId() == null
                || presupuesto.getDlFileEntryId().longValue() <= 0L
                || WebKeysCompras.isEmpty(
                presupuesto.getNombrePersistido()
        )) {
            throw new Exception(
                    "La asociacion del presupuesto contiene una identidad "
                            + "de documento invalida."
            );
        }
    }

    private void validarDocumentoAsociado(
            RequerimientoCompraPresupuesto presupuesto,
            DLFileEntry fileEntry) throws Exception {

        if (fileEntry == null) {
            throw new Exception(
                    "No se encontro el documento asociado al presupuesto."
            );
        }

        boolean coincide =
                fileEntry.getFileEntryId()
                        == presupuesto.getDlFileEntryId().longValue()
                        && fileEntry.getGroupId()
                        == presupuesto.getDlGroupId().longValue()
                        && fileEntry.getFolderId()
                        == presupuesto.getDlFolderId().longValue()
                        && presupuesto.getNombrePersistido().equals(
                        fileEntry.getName()
                );

        String uuidPersistido = presupuesto.getDlFileUuid();

        if (coincide && !WebKeysCompras.isEmpty(uuidPersistido)) {
            coincide = uuidPersistido.equals(fileEntry.getUuid());
        }

        if (!coincide) {
            throw new Exception(
                    "El documento persistido no coincide con la asociacion "
                            + "del presupuesto."
            );
        }
    }

    private DLFolder obtenerOCrearFolderCompras(
            long groupId,
            long userId,
            ServiceContext serviceContext) throws Exception {

        try {
            return getFolderCompras(groupId);
        } catch (NoSuchFolderException e) {
            if (_log.isDebugEnabled()) {
                _log.debug(
                        "La carpeta de presupuestos de Compras no existe; "
                                + "se intentara crear. groupId="
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
                return getFolderCompras(groupId);
            } catch (Exception lookupError) {
                _log.error(
                        "No se pudo crear ni recuperar la carpeta de "
                                + "presupuestos de Compras. groupId="
                                + groupId,
                        createError
                );
                throw createError;
            }
        }
    }

    private DLFolder getFolderCompras(long groupId) throws Exception {
        return DLFolderLocalServiceUtil.getFolder(
                groupId,
                WebKeysCompras.DOCUMENT_LIBRARY_PARENT_FOLDER_ID_COMPRAS,
                WebKeysCompras.DOCUMENT_LIBRARY_FOLDER_PRESUPUESTOS_COMPRAS
        );
    }

    private void validarContextoDocumentLibrary(
            ServiceContext serviceContext) throws Exception {

        if (serviceContext == null
                || serviceContext.getScopeGroupId() <= 0L
                || serviceContext.getUserId() <= 0L) {
            throw new Exception(
                    "No se pudo preparar el contexto de Document Library."
            );
        }
    }

    private void eliminarArchivoPresupuesto(
            long folderId,
            String nombre) throws Exception {

        if (folderId <= 0L || WebKeysCompras.isEmpty(nombre)) {
            throw new Exception(
                    "La identidad del documento a eliminar no es valida."
            );
        }

        DLFileEntryLocalServiceUtil.deleteFileEntry(
                folderId,
                nombre
        );
    }

    private String obtenerNombreArchivo(String filename) {
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

    private String obtenerExtensionSegura(String nombreOriginal) {
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

    private String construirNombrePersistido(
            int idRequerimientoCompra,
            int idPrestador,
            String identificador,
            String extension) {

        return WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                idRequerimientoCompra
        )
                + "PRESTADOR-"
                + idPrestador
                + "-"
                + identificador
                + extension;
    }

    private String construirTituloVisible(
            int idRequerimientoCompra,
            String nombreOriginal,
            String identificador) {

        String prefijo =
                WebKeysCompras.getPrefijoDocumentoRequerimientoCompra(
                        idRequerimientoCompra
                );

        String sufijo = "_" + identificador.substring(0, 8);

        int longitudDisponible =
                WebKeysCompras.DOCUMENT_LIBRARY_MAX_TITLE_LENGTH
                        - prefijo.length()
                        - sufijo.length();

        String nombre = normalizarComponenteTitulo(nombreOriginal);

        if (longitudDisponible <= 0) {
            nombre = "";
        } else if (nombre.length() > longitudDisponible) {
            nombre = nombre.substring(0, longitudDisponible);
        }

        return prefijo + nombre + sufijo;
    }

    private String normalizarComponenteTitulo(String nombreOriginal) {
        if (nombreOriginal == null) {
            return "";
        }

        String nombre =
                nombreOriginal
                        .replaceAll("[\\p{Cntrl}\\\\/:*?\"<>|]+", "_")
                        .replaceAll("\\s+", " ")
                        .trim();

        return ".".equals(nombre) || "..".equals(nombre)
                ? ""
                : nombre;
    }

    private long obtenerMaximoTamanoArchivo() throws Exception {
        String valor = PropsUtil.get("dl.file.max.size");

        if (WebKeysCompras.isEmpty(valor)) {
            return Long.MAX_VALUE;
        }

        try {
            long maximo = Long.parseLong(valor.trim());
            return maximo > 0L
                    ? maximo
                    : Long.MAX_VALUE;
        } catch (NumberFormatException e) {
            throw new Exception(
                    "La configuracion dl.file.max.size no es valida.",
                    e
            );
        }
    }

    private void validarContenidoPdf(
            File archivo,
            int numeroPresupuesto) throws Exception {

        if (archivo == null
                || !archivo.exists()
                || archivo.length() < 5L) {
            throw new Exception(
                    "El presupuesto "
                            + numeroPresupuesto
                            + " no contiene un archivo PDF valido."
            );
        }

        InputStream input = null;

        try {
            input = new FileInputStream(archivo);
            byte[] firma = new byte[5];
            int totalLeido = 0;

            while (totalLeido < firma.length) {
                int leido =
                        input.read(
                                firma,
                                totalLeido,
                                firma.length - totalLeido
                        );

                if (leido < 0) {
                    break;
                }

                totalLeido += leido;
            }

            boolean pdf =
                    totalLeido == firma.length
                            && firma[0] == '%'
                            && firma[1] == 'P'
                            && firma[2] == 'D'
                            && firma[3] == 'F'
                            && firma[4] == '-';

            if (!pdf) {
                throw new Exception(
                        "El presupuesto "
                                + numeroPresupuesto
                                + " no contiene un archivo PDF valido."
                );
            }

        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar la validacion del PDF.",
                                closeError
                        );
                    }
                }
            }
        }
    }

    private Exception traducirErrorDocumento(Exception error) {
        if (error instanceof DuplicateFileException) {
            return new Exception(
                    "Uno de los presupuestos se encuentra duplicado.",
                    error
            );
        }

        if (error instanceof FileSizeException) {
            return new Exception(
                    "Uno de los presupuestos supera el tamano permitido.",
                    error
            );
        }

        if (error instanceof FileNameException) {
            return new Exception(
                    "El tipo de uno de los presupuestos no esta permitido.",
                    error
            );
        }

        return error;
    }

    private String normalizarUsuario(String usuario) {
        String value = WebKeysCompras.trimToNull(usuario);
        return value != null
                ? value
                : "sistema";
    }

    public static final class PresupuestoEntrada {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final int idPrestador;

        public PresupuestoEntrada(
                int indice,
                File archivo,
                String nombreOriginal,
                int idPrestador) {

            this.indice = indice;
            this.archivo = archivo;
            this.nombreOriginal = nombreOriginal;
            this.idPrestador = idPrestador;
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

        public int getIdPrestador() {
            return idPrestador;
        }
    }

    private static final class PresupuestoValidado {

        private final File archivo;
        private final String nombreOriginal;
        private final PrestadorCotizacion prestador;
        private final String nombrePersistido;
        private final String titulo;
        private final String descripcionPrestador;

        private PresupuestoValidado(
                File archivo,
                String nombreOriginal,
                PrestadorCotizacion prestador,
                String nombrePersistido,
                String titulo,
                String descripcionPrestador) {

            this.archivo = archivo;
            this.nombreOriginal = nombreOriginal;
            this.prestador = prestador;
            this.nombrePersistido = nombrePersistido;
            this.titulo = titulo;
            this.descripcionPrestador = descripcionPrestador;
        }

        private File getArchivo() {
            return archivo;
        }

        private String getNombreOriginal() {
            return nombreOriginal;
        }

        private int getIdPrestador() {
            return prestador != null
                    ? prestador.getIdPrestador()
                    : 0;
        }

        private String getNombrePersistido() {
            return nombrePersistido;
        }

        private String getTitulo() {
            return titulo;
        }

        private String getDescripcionPrestador() {
            return descripcionPrestador;
        }
    }

    private static final class DocumentoPresupuestoCreado {

        private final long groupId;
        private final long folderId;
        private final long fileEntryId;
        private final String uuid;
        private final String nombre;
        private final String titulo;

        private DocumentoPresupuestoCreado(
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

        private long getGroupId() {
            return groupId;
        }

        private long getFolderId() {
            return folderId;
        }

        private long getFileEntryId() {
            return fileEntryId;
        }

        private String getUuid() {
            return uuid;
        }

        private String getNombre() {
            return nombre;
        }

        private String getTitulo() {
            return titulo;
        }
    }

    private static final class PresupuestoCreado {

        private final DocumentoPresupuestoCreado documento;
        private final RequerimientoCompraPresupuesto asociacion;

        private PresupuestoCreado(
                DocumentoPresupuestoCreado documento,
                RequerimientoCompraPresupuesto asociacion) {

            this.documento = documento;
            this.asociacion = asociacion;
        }

        private DocumentoPresupuestoCreado getDocumento() {
            return documento;
        }

        private RequerimientoCompraPresupuesto getAsociacion() {
            return asociacion;
        }
    }
}
