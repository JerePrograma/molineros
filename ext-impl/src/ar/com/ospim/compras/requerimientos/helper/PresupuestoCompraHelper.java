package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.util.CuilUtils;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.documentlibrary.FileNameException;
import com.liferay.documentlibrary.FileSizeException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Reglas documentales y compensación de presupuestos de Compras.
 *
 * No interpreta requests ni decide forwards. Tampoco utiliza JDBC: las
 * asociaciones persistentes se delegan al Helper funcional de edición.
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
                    "El requerimiento de compra no es válido."
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

        validarAccesoCarga(
                requerimiento
        );

        boolean cotizacionEmpresa =
                requerimiento.esSectorSinCotizacionPrestador();

        List<PrestadorCotizacion> prestadores = null;

        if (!cotizacionEmpresa) {
            prestadores =
                    BusquedaRequerimientoCompraServiceUtil
                            .listarPrestadoresEnviados(
                                    idRequerimientoCompra
                            );
        }

        List<PresupuestoValidado> presupuestos =
                validarPresupuestos(
                        idRequerimientoCompra,
                        entradas,
                        prestadores,
                        cotizacionEmpresa,
                        DocumentoLibraryComprasHelper
                                .obtenerMaximoTamanoDocumento()
                );

        RequerimientoCompra actual =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        validarAccesoCarga(
                actual
        );

        DocumentoLibraryComprasHelper
                .validarContextoDocumentLibrary(
                        serviceContext
                );

        long userId =
                serviceContext.getUserId();

        DLFolder folder =
                DocumentoLibraryComprasHelper
                        .obtenerOCrearFolderCompras(
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
                    "No se encontró el requerimiento de compra informado."
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

        validarAccesoCarga(
                requerimiento
        );

        RequerimientoCompraPresupuesto presupuesto =
                BusquedaRequerimientoCompraServiceUtil
                        .getPresupuesto(
                                idRequerimientoPresupuesto,
                                idRequerimientoCompra,
                                requerimiento.esSectorSinCotizacionPrestador()
                                        ? RequerimientoCompraPresupuesto
                                                .TIPO_DOCUMENTO_COTIZACION_EMPRESA
                                        : RequerimientoCompraPresupuesto
                                                .TIPO_DOCUMENTO_PRESUPUESTO
                        );

        if (presupuesto == null) {
            throw new Exception(
                    "El presupuesto informado no pertenece "
                            + "al requerimiento actual."
            );
        }

        validarIdentidadAsociacion(
                presupuesto
        );

        if (presupuesto.getDlGroupId().longValue()
                != scopeGroupId) {

            throw new Exception(
                    "El presupuesto informado no pertenece "
                            + "al sitio actual."
            );
        }

        DLFileEntry fileEntry =
                DLFileEntryLocalServiceUtil.getDLFileEntry(
                        presupuesto
                                .getDlFileEntryId()
                                .longValue()
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
                    "El presupuesto ya fue eliminado "
                            + "o fue modificado por otro proceso."
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
                            "La eliminación física del presupuesto falló "
                                    + "y la asociación no pudo reactivarse. "
                                    + "idRequerimientoPresupuesto="
                                    + idRequerimientoPresupuesto
                    );
                }

            } catch (Exception reactivarError) {
                _log.error(
                        "La eliminación física del presupuesto falló y "
                                + "también falló la reactivación "
                                + "de su asociación. "
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
            boolean cotizacionEmpresa,
            long maximoTamanoArchivo) throws Exception {

        Map<Integer, PrestadorCotizacion> prestadoresPorId =
                new HashMap<Integer, PrestadorCotizacion>();

        for (int i = 0;
             prestadores != null
                     && i < prestadores.size();
             i++) {

            PrestadorCotizacion prestador =
                    prestadores.get(i);

            if (prestador != null
                    && prestador.getIdPrestador() > 0
                    && WebKeysCompras.ENVIO_ENVIADO.equals(
                    prestador.getEstadoEnvio()
            )) {

                prestadoresPorId.put(
                        Integer.valueOf(
                                prestador.getIdPrestador()
                        ),
                        prestador
                );
            }
        }

        List<PresupuestoValidado> validados =
                new ArrayList<PresupuestoValidado>();

        Set<Integer> prestadoresSeleccionados =
                new HashSet<Integer>();

        Set<String> empresasSeleccionadas =
                new HashSet<String>();

        for (int i = 0;
             i < entradas.size();
             i++) {

            PresupuestoEntrada entrada =
                    entradas.get(i);

            if (entrada == null
                    || entrada.getIndice() != i) {

                throw new Exception(
                        "El indice del presupuesto "
                                + (i + 1)
                                + " no es válido."
                );
            }

            File archivo =
                    entrada.getArchivo();

            if (archivo == null
                    || !archivo.exists()
                    || archivo.length() <= 0L) {

                throw new Exception(
                        "Debe seleccionar el archivo "
                                + "del presupuesto "
                                + (i + 1)
                                + "."
                );
            }

            if (maximoTamanoArchivo > 0L
                    && archivo.length()
                    > maximoTamanoArchivo) {

                throw new Exception(
                        "El presupuesto "
                                + (i + 1)
                                + " supera el tamaño permitido."
                );
            }

            String nombreOriginal =
                    DocumentoLibraryComprasHelper
                            .normalizarNombreArchivoSeguro(
                                    entrada.getNombreOriginal()
                            );

            if (WebKeysCompras.isEmpty(
                    nombreOriginal
            )) {

                throw new Exception(
                        "El nombre del presupuesto "
                                + (i + 1)
                                + " no es válido."
                );
            }

            String extension =
                    DocumentoLibraryComprasHelper
                            .obtenerExtensionSeguraDocumento(
                                    nombreOriginal
                            );

            if (!".pdf".equals(
                    extension
            )) {

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

            String identificador =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "");

            if (cotizacionEmpresa) {
                if (entrada.getIdPrestador() > 0) {
                    throw new Exception(
                            "La cotización de empresa "
                                    + (i + 1)
                                    + " no puede asociarse a un prestador."
                    );
                }

                Empresa empresa =
                        obtenerEmpresaActiva(
                                entrada.getEmpresaCuit(),
                                entrada.getEmpresaSucursal(),
                                i + 1
                        );

                String claveEmpresa =
                        empresa.getCuit()
                                + "|"
                                + empresa.getSucursal();

                if (!empresasSeleccionadas.add(claveEmpresa)) {
                    throw new Exception(
                            "La empresa de la cotización "
                                    + (i + 1)
                                    + " está repetida. Solo puede cargarse "
                                    + "un archivo por empresa."
                    );
                }

                validados.add(
                        new PresupuestoValidado(
                                archivo,
                                nombreOriginal,
                                RequerimientoCompraPresupuesto
                                        .TIPO_DOCUMENTO_COTIZACION_EMPRESA,
                                null,
                                empresa,
                                construirNombrePersistidoEmpresa(
                                        idRequerimientoCompra,
                                        empresa.getCuit(),
                                        empresa.getSucursal(),
                                        identificador,
                                        extension
                                ),
                                construirTituloVisible(
                                        idRequerimientoCompra,
                                        nombreOriginal,
                                        identificador
                                ),
                                empresa.getRazon_soc()
                        )
                );

            } else {
                if (!WebKeysCompras.isEmpty(entrada.getEmpresaCuit())
                        || !WebKeysCompras.isEmpty(
                                entrada.getEmpresaSucursal()
                        )) {

                    throw new Exception(
                            "El presupuesto del prestador "
                                    + (i + 1)
                                    + " no puede asociarse a una empresa."
                    );
                }

                if (entrada.getIdPrestador() <= 0) {
                    throw new Exception(
                            "Debe seleccionar el prestador "
                                    + "del presupuesto "
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
                                    + " está repetido. Solo puede cargarse "
                                    + "un archivo por prestador."
                    );
                }

                validados.add(
                        new PresupuestoValidado(
                                archivo,
                                nombreOriginal,
                                RequerimientoCompraPresupuesto
                                        .TIPO_DOCUMENTO_PRESUPUESTO,
                                prestador,
                                null,
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
        }

        return validados;
    }

    private void guardarPresupuestosValidados(
            int idRequerimientoCompra,
            List<PresupuestoValidado> presupuestos,
            long userId,
            long folderId,
            String usuario,
            ServiceContext serviceContext)
            throws Exception {

        List<PresupuestoCreado> creados =
                new ArrayList<PresupuestoCreado>();

        try {
            for (int i = 0;
                 i < presupuestos.size();
                 i++) {

                PresupuestoValidado presupuesto =
                        presupuestos.get(i);

                DocumentoPresupuestoCreado documento =
                        null;

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
                                    "No se pudo eliminar el documento "
                                            + "creado antes de fallar "
                                            + "su asociación. fileEntryId="
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

            throw traducirErrorDocumento(
                    errorPrincipal
            );
        }
    }

    private void compensarPresupuestosCreados(
            int idRequerimientoCompra,
            List<PresupuestoCreado> creados,
            String usuario) {

        for (int i = creados.size() - 1;
             i >= 0;
             i--) {

            PresupuestoCreado creado =
                    creados.get(i);

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

            boolean asociacionDadaDeBaja =
                    false;

            try {
                asociacionDadaDeBaja =
                        requerimientoHelper.darDeBajaPresupuesto(
                                idAsociacion.intValue(),
                                idRequerimientoCompra,
                                usuario
                        );

            } catch (Exception cleanupSqlError) {
                _log.error(
                        "No se pudo dar de baja una asociación "
                                + "durante la compensación "
                                + "de presupuestos. "
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
                        creado.getDocumento()
                                .getFolderId(),
                        creado.getDocumento()
                                .getNombre()
                );

            } catch (Exception cleanupFileError) {
                _log.error(
                        "No se pudo eliminar un presupuesto "
                                + "durante la compensación. fileEntryId="
                                + creado.getDocumento()
                                .getFileEntryId(),
                        cleanupFileError
                );

                try {
                    requerimientoHelper.reactivarPresupuesto(
                            idAsociacion.intValue(),
                            idRequerimientoCompra
                    );

                } catch (Exception reactivarError) {
                    _log.error(
                            "No se pudo reactivar la asociación "
                                    + "después de fallar la eliminación "
                                    + "del documento. "
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
            ServiceContext serviceContext)
            throws Exception {

        DLFileEntry entry =
                DLFileEntryLocalServiceUtil
                        .addOrOverwriteFileEntry(
                                userId,
                                folderId,
                                presupuesto.getNombrePersistido(),
                                presupuesto.getNombreOriginal(),
                                presupuesto.getTitulo(),
                                presupuesto.getDescripcionDocumento(),
                                "",
                                presupuesto.getArchivo(),
                                serviceContext
                        );

        if (entry == null
                || entry.getFileEntryId() <= 0L) {

            throw new Exception(
                    "Document Library no devolvió "
                            + "un documento válido."
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
            String usuario)
            throws Exception {

        RequerimientoCompraPresupuesto asociacion =
                new RequerimientoCompraPresupuesto();

        asociacion.setIdRequerimiento(
                Integer.valueOf(
                        idRequerimientoCompra
                )
        );

        asociacion.setTipoDocumento(
                Integer.valueOf(
                        presupuesto.getTipoDocumento()
                )
        );

        asociacion.setIdPrestador(
                presupuesto.getIdPrestador() > 0
                        ? Integer.valueOf(
                                presupuesto.getIdPrestador()
                        )
                        : null
        );

        asociacion.setEmpresaCuit(
                presupuesto.getEmpresaCuit()
        );

        asociacion.setEmpresaSucursal(
                presupuesto.getEmpresaSucursal()
        );

        asociacion.setDescripcionEmpresa(
                presupuesto.getDescripcionEmpresa()
        );

        asociacion.setDlGroupId(
                Long.valueOf(
                        documento.getGroupId()
                )
        );

        asociacion.setDlFolderId(
                Long.valueOf(
                        documento.getFolderId()
                )
        );

        asociacion.setDlFileEntryId(
                Long.valueOf(
                        documento.getFileEntryId()
                )
        );

        asociacion.setDlFileUuid(
                documento.getUuid()
        );

        asociacion.setNombreOriginal(
                presupuesto.getNombreOriginal()
        );

        asociacion.setNombrePersistido(
                documento.getNombre()
        );

        asociacion.setTitulo(
                documento.getTitulo()
        );

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
                    "No se pudo obtener el identificador "
                            + "de la asociación del presupuesto."
            );
        }

        asociacion.setIdRequerimientoPresupuesto(
                Integer.valueOf(
                        id
                )
        );

        return asociacion;
    }

    private void validarCantidadPresupuestos(
            int cantidad)
            throws Exception {

        if (cantidad <= 0
                || cantidad
                > WebKeysCompras
                .MAX_PRESUPUESTOS_POR_CARGA) {

            throw new Exception(
                    "La cantidad de presupuestos debe estar entre 1 y "
                            + WebKeysCompras
                            .MAX_PRESUPUESTOS_POR_CARGA
                            + "."
            );
        }
    }

    private void validarAccesoCarga(
            RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null
                || requerimiento
                .getIdRequerimientoCompra() <= 0) {

            throw new Exception(
                    "No se encontró el requerimiento de compra informado."
            );
        }

        if (!requerimiento.puedeAdministrarPresupuestos()) {

            if (requerimiento.esSectorSinCotizacionPrestador()) {
                throw new Exception(
                        "Las cotizaciones de empresas solo pueden "
                                + "administrarse mientras el requerimiento "
                                + "está PENDIENTE y activo. Estado actual: "
                                + requerimiento.getEstadoDescripcionVisible()
                                + "."
                );
            }

            throw new Exception(
                    "Solo se pueden administrar presupuestos "
                            + "en estado A COTIZAR. Estado actual: "
                            + requerimiento
                            .getEstadoDescripcionVisible()
                            + "."
            );
        }
    }

    private Empresa obtenerEmpresaActiva(
            String empresaCuit,
            String empresaSucursal,
            int numeroPresupuesto) throws Exception {

        String cuit =
                WebKeysCompras.trimToNull(
                        empresaCuit
                );

        String sucursal =
                WebKeysCompras.trimToNull(
                        empresaSucursal
                );

        if (cuit == null
                || cuit.length() > 11
                || !CuilUtils.validarNum(cuit)) {

            throw new Exception(
                    "El CUIT de la empresa de la cotización "
                            + numeroPresupuesto
                            + " no es válido."
            );
        }

        if (sucursal == null
                || sucursal.length() > 6) {

            throw new Exception(
                    "La sucursal de la empresa de la cotización "
                            + numeroPresupuesto
                            + " no es válida."
            );
        }

        List<Empresa> empresas =
                EmpresaServiceUtil.getEmpleadores(
                        cuit,
                        null,
                        sucursal,
                        0
                );

        if (empresas == null) {
            throw new Exception(
                    "No se pudo validar la empresa seleccionada."
            );
        }

        Empresa encontrada = null;

        for (int i = 0; i < empresas.size(); i++) {
            Empresa empresa = empresas.get(i);

            if (empresa != null
                    && cuit.equals(
                            WebKeysCompras.trimToNull(
                                    empresa.getCuit()
                            )
                    )
                    && sucursal.equals(
                            WebKeysCompras.trimToNull(
                                    empresa.getSucursal()
                            )
                    )) {

                if (encontrada != null) {
                    throw new Exception(
                            "La empresa seleccionada no tiene "
                                    + "una identidad única en el padrón."
                    );
                }

                encontrada = empresa;
            }
        }

        if (encontrada == null
                || encontrada.getBaja_fecha() != null
                || WebKeysCompras.isEmpty(
                        encontrada.getRazon_soc()
                )
                || encontrada.getRazon_soc().trim().length() > 200) {

            throw new Exception(
                    "La empresa seleccionada no existe o no está activa "
                            + "en el padrón de empleadores."
            );
        }

        return encontrada;
    }

    private void validarIdentidadAsociacion(
            RequerimientoCompraPresupuesto presupuesto)
            throws Exception {

        DocumentoLibraryComprasHelper
                .validarIdentidadAsociacionDocumento(
                        presupuesto
                );
    }

    private void validarDocumentoAsociado(
            RequerimientoCompraPresupuesto presupuesto,
            DLFileEntry fileEntry)
            throws Exception {

        if (fileEntry == null) {
            throw new Exception(
                    "No se encontró el documento "
                            + "asociado al presupuesto."
            );
        }

        if (!DocumentoLibraryComprasHelper
                .coincideIdentidadAsociacionDocumento(
                        presupuesto,
                        fileEntry
                )) {

            throw new Exception(
                    "El documento persistido no coincide "
                            + "con la asociación del presupuesto."
            );
        }
    }

    private void eliminarArchivoPresupuesto(
            long folderId,
            String nombre)
            throws Exception {

        if (folderId <= 0L
                || WebKeysCompras.isEmpty(
                nombre
        )) {

            throw new Exception(
                    "La identidad del documento "
                            + "a eliminar no es válida."
            );
        }

        DLFileEntryLocalServiceUtil
                .deleteFileEntry(
                        folderId,
                        nombre
                );
    }

    private String construirNombrePersistido(
            int idRequerimientoCompra,
            int idPrestador,
            String identificador,
            String extension) {

        return WebKeysCompras
                .getPrefijoDocumentoRequerimientoCompra(
                        idRequerimientoCompra
                )
                + "PRESTADOR-"
                + idPrestador
                + "-"
                + identificador
                + extension;
    }

    private String construirNombrePersistidoEmpresa(
            int idRequerimientoCompra,
            String empresaCuit,
            String empresaSucursal,
            String identificador,
            String extension) {

        return WebKeysCompras
                .getPrefijoDocumentoRequerimientoCompra(
                        idRequerimientoCompra
                )
                + "EMPRESA-"
                + normalizarComponenteIdentidad(empresaCuit)
                + "-"
                + normalizarComponenteIdentidad(empresaSucursal)
                + "-"
                + identificador
                + extension;
    }

    private String normalizarComponenteIdentidad(String value) {
        String normalizado = value != null
                ? value.replaceAll("[^A-Za-z0-9]", "")
                : "";

        return normalizado.length() > 0
                ? normalizado
                : "SIN-DATO";
    }

    private String construirTituloVisible(
            int idRequerimientoCompra,
            String nombreOriginal,
            String identificador) {

        String prefijo =
                WebKeysCompras
                        .getPrefijoDocumentoRequerimientoCompra(
                                idRequerimientoCompra
                        );

        String sufijo =
                "_"
                        + identificador.substring(
                        0,
                        8
                );

        int longitudDisponible =
                WebKeysCompras
                        .DOCUMENT_LIBRARY_MAX_TITLE_LENGTH
                        - prefijo.length()
                        - sufijo.length();

        String nombre =
                normalizarComponenteTitulo(
                        nombreOriginal
                );

        if (longitudDisponible <= 0) {
            nombre =
                    "";

        } else if (nombre.length()
                > longitudDisponible) {

            nombre =
                    nombre.substring(
                            0,
                            longitudDisponible
                    );
        }

        return prefijo
                + nombre
                + sufijo;
    }

    private String normalizarComponenteTitulo(
            String nombreOriginal) {

        if (nombreOriginal == null) {
            return "";
        }

        String nombre =
                nombreOriginal
                        .replaceAll(
                                "[\\p{Cntrl}\\\\/:*?\"<>|]+",
                                "_"
                        )
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .trim();

        return ".".equals(nombre)
                || "..".equals(nombre)
                ? ""
                : nombre;
    }

    /**
     * Regla específica de Presupuesto.
     *
     * No se mueve a DocumentoLibraryComprasHelper porque la firma PDF es una
     * regla propia de este tipo documental, no una primitiva común de DL.
     */
    private void validarContenidoPdf(
            File archivo,
            int numeroPresupuesto)
            throws Exception {

        if (archivo == null
                || !archivo.exists()
                || archivo.length() < 5L) {

            throw new Exception(
                    "El presupuesto "
                            + numeroPresupuesto
                            + " no contiene un archivo PDF válido."
            );
        }

        InputStream input =
                null;

        try {
            input =
                    new FileInputStream(
                            archivo
                    );

            byte[] firma =
                    new byte[5];

            int totalLeido =
                    0;

            while (totalLeido
                    < firma.length) {

                int leido =
                        input.read(
                                firma,
                                totalLeido,
                                firma.length
                                        - totalLeido
                        );

                if (leido < 0) {
                    break;
                }

                totalLeido +=
                        leido;
            }

            boolean pdf =
                    totalLeido
                            == firma.length
                            && firma[0] == '%'
                            && firma[1] == 'P'
                            && firma[2] == 'D'
                            && firma[3] == 'F'
                            && firma[4] == '-';

            if (!pdf) {
                throw new Exception(
                        "El presupuesto "
                                + numeroPresupuesto
                                + " no contiene un archivo PDF válido."
                );
            }

        } finally {
            if (input != null) {
                try {
                    input.close();

                } catch (Exception closeError) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(
                                "No se pudo cerrar "
                                        + "la validación del PDF.",
                                closeError
                        );
                    }
                }
            }
        }
    }

    private Exception traducirErrorDocumento(
            Exception error) {

        if (error instanceof DuplicateFileException) {
            return new Exception(
                    "Uno de los presupuestos se encuentra duplicado.",
                    error
            );
        }

        if (error instanceof FileSizeException) {
            return new Exception(
                    "Uno de los presupuestos supera el tamaño permitido.",
                    error
            );
        }

        if (error instanceof FileNameException) {
            return new Exception(
                    "El tipo de uno de los presupuestos "
                            + "no está permitido.",
                    error
            );
        }

        return error;
    }

    private String normalizarUsuario(
            String usuario) {

        String value =
                WebKeysCompras.trimToNull(
                        usuario
                );

        return value != null
                ? value
                : "sistema";
    }

    public static final class PresupuestoEntrada {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final int idPrestador;
        private final String empresaCuit;
        private final String empresaSucursal;

        public PresupuestoEntrada(
                int indice,
                File archivo,
                String nombreOriginal,
                int idPrestador) {

            this(
                    indice,
                    archivo,
                    nombreOriginal,
                    idPrestador,
                    null,
                    null
            );
        }

        public PresupuestoEntrada(
                int indice,
                File archivo,
                String nombreOriginal,
                int idPrestador,
                String empresaCuit,
                String empresaSucursal) {

            this.indice =
                    indice;

            this.archivo =
                    archivo;

            this.nombreOriginal =
                    nombreOriginal;

            this.idPrestador =
                    idPrestador;

            this.empresaCuit =
                    empresaCuit;

            this.empresaSucursal =
                    empresaSucursal;
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

        public String getEmpresaCuit() {
            return empresaCuit;
        }

        public String getEmpresaSucursal() {
            return empresaSucursal;
        }
    }

    private static final class PresupuestoValidado {

        private final File archivo;
        private final String nombreOriginal;
        private final int tipoDocumento;
        private final PrestadorCotizacion prestador;
        private final Empresa empresa;
        private final String nombrePersistido;
        private final String titulo;
        private final String descripcionDocumento;

        private PresupuestoValidado(
                File archivo,
                String nombreOriginal,
                int tipoDocumento,
                PrestadorCotizacion prestador,
                Empresa empresa,
                String nombrePersistido,
                String titulo,
                String descripcionDocumento) {

            this.archivo =
                    archivo;

            this.nombreOriginal =
                    nombreOriginal;

            this.tipoDocumento =
                    tipoDocumento;

            this.prestador =
                    prestador;

            this.empresa =
                    empresa;

            this.nombrePersistido =
                    nombrePersistido;

            this.titulo =
                    titulo;

            this.descripcionDocumento =
                    descripcionDocumento;
        }

        private File getArchivo() {
            return archivo;
        }

        private String getNombreOriginal() {
            return nombreOriginal;
        }

        private int getTipoDocumento() {
            return tipoDocumento;
        }

        private int getIdPrestador() {
            return prestador != null
                    ? prestador.getIdPrestador()
                    : 0;
        }

        private String getEmpresaCuit() {
            return empresa != null
                    ? empresa.getCuit()
                    : null;
        }

        private String getEmpresaSucursal() {
            return empresa != null
                    ? empresa.getSucursal()
                    : null;
        }

        private String getDescripcionEmpresa() {
            return empresa != null
                    ? empresa.getRazon_soc()
                    : null;
        }

        private String getNombrePersistido() {
            return nombrePersistido;
        }

        private String getTitulo() {
            return titulo;
        }

        private String getDescripcionDocumento() {
            return descripcionDocumento;
        }

        private String getDescripcionPrestador() {
            return prestador != null
                    ? descripcionDocumento
                    : null;
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

            this.groupId =
                    groupId;

            this.folderId =
                    folderId;

            this.fileEntryId =
                    fileEntryId;

            this.uuid =
                    uuid;

            this.nombre =
                    nombre;

            this.titulo =
                    titulo;
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

            this.documento =
                    documento;

            this.asociacion =
                    asociacion;
        }

        private DocumentoPresupuestoCreado getDocumento() {
            return documento;
        }

        private RequerimientoCompraPresupuesto getAsociacion() {
            return asociacion;
        }
    }
}
