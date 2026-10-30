package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.global.beans.Empresa;
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
import java.util.Collections;
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

    public boolean esRequerimientoHabilitadoBusquedaEmpresaCotizacion(int id) throws Exception {
        if (id <= 0) {
            return false;
        }
        RequerimientoCompra actual = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(id);
        return actual != null && actual.isActivo()
                && actual.getEstado() == ConfiguracionComprasHelper.getEstadoPendiente()
                && actual.esSectorSinCotizacionPrestador();
    }

    public int guardarPresupuestos(int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas, ServiceContext serviceContext,
            String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new IllegalArgumentException("El requerimiento de compra no es valido.");
        }
        return guardarPresupuestosValidados(idRequerimientoCompra, entradas,
                normalizarUsuario(usuario), serviceContext);
    }

    public void validarCotizacionesEmpresaAlta(
            List<PresupuestoEntrada> entradas) throws Exception {

        if (entradas == null || entradas.isEmpty()) {
            return;
        }
        validarCantidadPresupuestos(entradas.size());
        validarPresupuestos(0, entradas, null, true,
                DocumentoLibraryComprasHelper.obtenerMaximoTamanoDocumento());
    }

    /**
     * El caller controla commit, rollback y compensacion de todos los archivos
     * del alta. La asociacion usa la misma transaccion de cabecera y detalles.
     */
    public List<RequerimientoCompraPresupuesto> guardarCotizacionesEmpresa(
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion,
            int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas,
            ServiceContext serviceContext,
            String usuario,
            List<DocumentoPresupuestoCreado> documentosCreados) throws Exception {

        List<RequerimientoCompraPresupuesto> guardadas =
                new ArrayList<RequerimientoCompraPresupuesto>();
        if (entradas == null || entradas.isEmpty()) {
            return guardadas;
        }
        if (transaccion == null || idRequerimientoCompra <= 0
                || documentosCreados == null) {
            throw new IllegalArgumentException(
                    "No se obtuvo la transaccion del alta de cotizaciones.");
        }
        validarCantidadPresupuestos(transaccion, entradas.size());
        List<PresupuestoValidado> presupuestos = validarPresupuestos(
                idRequerimientoCompra, entradas, null, true,
                DocumentoLibraryComprasHelper.obtenerMaximoTamanoDocumento(), transaccion);
        DocumentoLibraryComprasHelper.validarContextoDocumentLibrary(serviceContext);
        DLFolder folder = DocumentoLibraryComprasHelper.obtenerOCrearFolderCompras(serviceContext);
        try {
            for (PresupuestoValidado presupuesto : presupuestos) {
                DocumentoPresupuestoCreado documento = crearArchivoPresupuesto(
                        serviceContext.getUserId(), folder.getFolderId(), presupuesto, serviceContext);
                documentosCreados.add(documento);
                guardadas.add(registrarAsociacionPresupuesto(transaccion, idRequerimientoCompra,
                        presupuesto, documento, normalizarUsuario(usuario)));
            }
        } catch (Exception e) {
            throw traducirErrorDocumento(e);
        }
        return guardadas;
    }

    public boolean compensarDocumentosAlta(
            List<DocumentoPresupuestoCreado> documentosCreados) {

        boolean completa = true;
        for (int i = documentosCreados.size() - 1; i >= 0; i--) {
            DocumentoPresupuestoCreado documento = documentosCreados.get(i);
            try {
                eliminarArchivoPresupuesto(documento.getFolderId(), documento.getNombre());
            } catch (Exception error) {
                completa = false;
                _log.error("No se pudo compensar la cotizacion del alta. fileEntryId="
                        + documento.getFileEntryId(), error);
            }
        }
        return completa;
    }

    private void validarEmpresasNoRepetidas(int idRequerimientoCompra,
            List<PresupuestoValidado> presupuestos) throws Exception {

        List<RequerimientoCompraPresupuesto> actuales =
                BusquedaRequerimientoCompraServiceUtil.listarCotizacionesEmpresa(idRequerimientoCompra);
        if (actuales == null) {
            throw new IllegalStateException("No se pudieron validar las cotizaciones de Empresas actuales.");
        }
        for (PresupuestoValidado presupuesto : presupuestos) {
            for (RequerimientoCompraPresupuesto actual : actuales) {
                if (presupuesto.getEmpresaCuit() != null && actual != null && actual.isActivo()
                        && presupuesto.getEmpresaCuit().equals(WebKeysCompras.trimToNull(actual.getEmpresaCuit()))
                        && String.valueOf(presupuesto.getEmpresaSucursal()).equals(String.valueOf(WebKeysCompras.trimToNull(actual.getEmpresaSucursal())))) {
                    throw new IllegalArgumentException(
                            "La Empresa ya tiene una cotizacion activa en este requerimiento.");
                }
            }
        }
    }

    public RequerimientoCompraPresupuesto obtenerPresupuesto(int idRequerimiento,
            int idPrestador) throws Exception {
        RequerimientoCompraPresupuesto encontrado = null;
        for (RequerimientoCompraPresupuesto p :
                BusquedaRequerimientoCompraServiceUtil.listarPresupuestos(idRequerimiento)) {
            if (p.isActivo() && p.getIdPrestador() != null
                    && p.getIdPrestador().intValue() == idPrestador) {
                if (encontrado != null) {
                    throw new IllegalArgumentException("Existe más de un presupuesto activo del prestador.");
                }
                encontrado = p;
            }
        }
        return encontrado;
    }

    public void guardarPresupuestoComparativa(RequerimientoCompraComparativa comparativa,
            PresupuestoEntrada entrada, Map<String, String> datos,
            ServiceContext serviceContext, String usuario) throws Exception {
        if (comparativa == null || entrada == null) {
            throw new IllegalArgumentException("Debe informar la comparativa y su presupuesto.");
        }
        int id = comparativa.getIdRequerimiento();
        RequerimientoCompraComparativaHelper comparativaHelper = new RequerimientoCompraComparativaHelper();
        List<RequerimientoCompraComparativa> lista = Collections.singletonList(comparativa);
        DocumentoLibraryComprasHelper.validarContextoDocumentLibrary(serviceContext);
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        DocumentoPresupuestoCreado documento = null;
        DLFileEntry archivoAnterior = null;
        boolean commitIniciado = false;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(id);
            validarEstadoPresupuesto(tx, actual, false, tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS"));
            comparativaHelper.aplicarEntrada(tx, lista, datos);
            RequerimientoCompraPresupuesto anterior = null;
            for (RequerimientoCompraPresupuesto candidato : tx.listarDocumentos(id,
                    RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO)) {
                if (candidato.isActivo() && candidato.getIdPrestador() != null
                        && candidato.getIdPrestador().intValue() == comparativa.getIdPrestador()) {
                    if (anterior != null) {
                        throw new IllegalArgumentException("Existe mas de un presupuesto activo del prestador.");
                    }
                    anterior = candidato;
                }
            }
            if (anterior != null) {
                anterior = tx.bloquearDocumento(id, anterior.getIdRequerimientoPresupuesto().intValue());
                validarIdentidadAsociacion(anterior);
                if (anterior.getDlGroupId().longValue() != serviceContext.getScopeGroupId()) {
                    throw new IllegalArgumentException("El presupuesto no pertenece al sitio actual.");
                }
                archivoAnterior = DLFileEntryLocalServiceUtil.getDLFileEntry(anterior.getDlFileEntryId().longValue());
                validarDocumentoAsociado(anterior, archivoAnterior);
            }
            boolean archivoNuevo = !WebKeysCompras.isEmpty(entrada.getNombreOriginal())
                    || (entrada.getArchivo() != null && entrada.getArchivo().length() > 0L);
            if (!archivoNuevo && anterior == null) {
                throw new IllegalArgumentException("Debe seleccionar el PDF del primer presupuesto.");
            }
            if (archivoNuevo) {
                PrestadorCotizacion prestador = new PrestadorCotizacion();
                prestador.setIdPrestador(comparativa.getIdPrestador());
                prestador.setDescripcion(comparativa.getPrestador());
                prestador.setEstadoEnvio(tx.bloquearEstadoPrestadorCotizacion(id, comparativa.getIdPrestador()));
                PresupuestoValidado validado = validarPresupuestos(id, Collections.singletonList(entrada),
                        Collections.singletonList(prestador), false,
                        DocumentoLibraryComprasHelper.obtenerMaximoTamanoDocumento(), tx).get(0);
                DLFolder folder = DocumentoLibraryComprasHelper.obtenerOCrearFolderCompras(serviceContext);
                documento = crearArchivoPresupuesto(serviceContext.getUserId(), folder.getFolderId(), validado, serviceContext);
                if (anterior != null && !cambiarAsociacionEnTransaccion(tx, id,
                        anterior.getIdRequerimientoPresupuesto().intValue(), false, usuario)) {
                    throw new IllegalArgumentException("El presupuesto cambio. Actualice la pantalla.");
                }
                registrarAsociacionPresupuesto(tx, id, validado, documento, usuario);
            }
            comparativaHelper.guardar(tx, lista, datos, usuario);
            commitIniciado = true;
            tx.commit();
        } catch (Exception error) {
            boolean rollbackConfirmado = tx == null;
            if (tx != null) {
                try {
                    tx.rollback();
                    rollbackConfirmado = true;
                } catch (Exception rollbackError) {
                    _log.error("No se pudo confirmar el rollback del presupuesto y comparativa.", rollbackError);
                }
            }
            if (!commitIniciado && rollbackConfirmado) {
                if (documento != null && !compensarDocumentosAlta(Collections.singletonList(documento))) {
                    throw new Exception("Fallo el guardado y no se pudo compensar el nuevo archivo. Revise los adjuntos antes de reintentar.", error);
                }
            } else {
                throw new Exception("No se pudo confirmar el resultado del presupuesto y comparativa. Se conservan ambos archivos para verificar el estado.", error);
            }
            throw traducirErrorDocumento(error);
        } finally {
            if (tx != null) {
                tx.cerrar();
            }
        }
        if (documento != null && archivoAnterior != null) {
            try {
                eliminarArchivoPresupuesto(archivoAnterior.getFolderId(), archivoAnterior.getName());
            } catch (Exception cleanupError) {
                throw new Exception("El presupuesto y sus datos se guardaron, pero no se pudo quitar el archivo anterior. El nuevo es el unico vigente.", cleanupError);
            }
        }
    }

    public void borrarPresupuesto(int idRequerimientoCompra, int idRequerimientoPresupuesto,
            long scopeGroupId, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0 || idRequerimientoPresupuesto <= 0 || scopeGroupId <= 0L) {
            throw new IllegalArgumentException("Debe informar el requerimiento, presupuesto y sitio actual.");
        }
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        RequerimientoCompraPresupuesto presupuesto = null;
        DLFileEntry fileEntry = null;
        boolean commitIniciado = false;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimientoCompra);
            presupuesto = tx.bloquearDocumento(idRequerimientoCompra, idRequerimientoPresupuesto);
            if (actual == null || !actual.isActivo() || presupuesto == null || !presupuesto.isActivo()) {
                throw new IllegalArgumentException("No se encontro el presupuesto activo del requerimiento.");
            }
            validarIdentidadAsociacion(presupuesto);
            if (presupuesto.getDlGroupId().longValue() != scopeGroupId) {
                throw new IllegalArgumentException("El presupuesto no pertenece al sitio actual.");
            }
            fileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(presupuesto.getDlFileEntryId().longValue());
            validarDocumentoAsociado(presupuesto, fileEntry);
            if (!cambiarAsociacionEnTransaccion(tx, idRequerimientoCompra,
                    idRequerimientoPresupuesto, false, usuario)) {
                throw new IllegalArgumentException("El presupuesto cambio o ya fue eliminado.");
            }
            commitIniciado = true;
            tx.commit();
        } catch (Exception error) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception rollbackError) {
                    _log.error("No se pudo confirmar el rollback de la baja del presupuesto.", rollbackError);
                    throw new Exception("No se pudo confirmar la baja. Se conserva el archivo para verificar el resultado.", error);
                }
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar la baja. Se conserva el archivo para verificar el resultado.", error);
            }
            throw traducirErrorDocumento(error);
        } finally {
            if (tx != null) {
                tx.cerrar();
            }
        }
        try {
            eliminarArchivoPresupuesto(fileEntry.getFolderId(), fileEntry.getName());
        } catch (Exception deleteError) {
            try {
                if (!reactivarAsociacion(presupuesto, idRequerimientoPresupuesto, idRequerimientoCompra)) {
                    _log.error("Fallo la eliminacion DL y la asociacion no pudo reactivarse. Documento=" + idRequerimientoPresupuesto);
                }
            } catch (Exception reactivarError) {
                _log.error("Fallo la eliminacion DL y tambien la reactivacion de su asociacion.", reactivarError);
            }
            throw deleteError;
        }
    }

    private List<PresupuestoValidado> validarPresupuestos(
            int idRequerimientoCompra, List<PresupuestoEntrada> entradas,
            List<PrestadorCotizacion> prestadores, boolean cotizacionEmpresa,
            long maximoTamanoArchivo) throws Exception {
        return validarPresupuestos(idRequerimientoCompra, entradas, prestadores,
                cotizacionEmpresa, maximoTamanoArchivo, null);
    }

    private List<PresupuestoValidado> validarPresupuestos(
            int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas,
            List<PrestadorCotizacion> prestadores,
            boolean cotizacionEmpresa,
            long maximoTamanoArchivo,
            EditarRequerimientoCompraServiceUtil.Transaccion tx) throws Exception {

        String envios = cotizacionEmpresa ? null : (tx != null
                ? tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO)
                : ConfiguracionComprasHelper.leerObligatoria(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO));
        String enviado = cotizacionEmpresa ? null : ConfiguracionComprasHelper.resolverEstadoEnvio("ENVIADO", envios);
        String cotizado = cotizacionEmpresa ? null : ConfiguracionComprasHelper.resolverEstadoEnvio("COTIZADO", envios);

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
                    && (enviado.equals(
                            prestador.getEstadoEnvio())
                        || cotizado.equals(
                            prestador.getEstadoEnvio()))) {

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

                String cuit = WebKeysCompras.trimToNull(entrada.getEmpresaCuit());
                String sucursal = WebKeysCompras.trimToNull(entrada.getEmpresaSucursal());
                String descripcion = WebKeysCompras.trimToNull(entrada.getDescripcionEmpresa());
                validarDatosEmpresaCotizacion(tx, cuit, sucursal, descripcion, i + 1);
                Empresa empresa = new Empresa(cuit, sucursal, descripcion);

                String claveEmpresa =
                        empresa.getCuit()
                                + "|"
                                + empresa.getSucursal();

                if (empresa.getCuit() != null && !empresasSeleccionadas.add(claveEmpresa)) {
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

    private int guardarPresupuestosValidados(int idRequerimientoCompra,
            List<PresupuestoEntrada> entradas, String usuario,
            ServiceContext serviceContext) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = null;
        List<DocumentoPresupuestoCreado> documentos = new ArrayList<DocumentoPresupuestoCreado>();
        boolean commitIniciado = false;
        try {
            tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimientoCompra);
            boolean empresa = actual != null && ConfiguracionComprasHelper.resolverSectorSinCotizacionPrestador(
                    actual.getSectorDescripcion(), tx.getConfiguracionCompra("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR"));
            validarEstadoPresupuesto(tx, actual, empresa, tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS"));
            validarCantidadPresupuestos(tx, entradas == null ? 0 : entradas.size());
            List<PrestadorCotizacion> prestadores = null;
            if (!empresa) {
                Map<String,String> limites = ConfiguracionComprasHelper.parsearPares("COMPRAS_LIMITES", tx.getConfiguracionCompra("COMPRAS_LIMITES"));
                int maximo = ConfiguracionComprasHelper.parsearEntero("COMPRAS_LIMITES", limites.get("PRESTADORES_ENVIADOS"), false);
                if (maximo == Integer.MAX_VALUE) {
                    throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException("COMPRAS_LIMITES");
                }
                prestadores = tx.listarPrestadoresEnviados(idRequerimientoCompra, maximo + 1);
                if (prestadores == null || prestadores.size() > maximo) {
                    throw new IllegalArgumentException("El requerimiento supera el maximo permitido de prestadores enviados para esta pantalla.");
                }
            }
            List<PresupuestoValidado> presupuestos = validarPresupuestos(idRequerimientoCompra,
                    entradas, prestadores, empresa, DocumentoLibraryComprasHelper.obtenerMaximoTamanoDocumento(), tx);
            DocumentoLibraryComprasHelper.validarContextoDocumentLibrary(serviceContext);
            DLFolder folder = DocumentoLibraryComprasHelper.obtenerOCrearFolderCompras(serviceContext);
            for (PresupuestoValidado presupuesto : presupuestos) {
                DocumentoPresupuestoCreado documento = crearArchivoPresupuesto(
                        serviceContext.getUserId(), folder.getFolderId(), presupuesto, serviceContext);
                documentos.add(documento);
                registrarAsociacionPresupuesto(tx, idRequerimientoCompra,
                        presupuesto, documento, usuario);
            }
            commitIniciado = true;
            tx.commit();
            return presupuestos.size();
        } catch (Exception error) {
            boolean rollbackConfirmado = tx == null;
            if (tx != null) {
                try {
                    tx.rollback();
                    rollbackConfirmado = true;
                } catch (Exception rollbackError) {
                    _log.error("No se pudo confirmar el rollback de los presupuestos.", rollbackError);
                }
            }
            if (!commitIniciado && rollbackConfirmado) {
                if (!compensarDocumentosAlta(documentos)) {
                    throw new Exception("Fallo el guardado y no se pudieron compensar todos los archivos. Revise los adjuntos antes de reintentar.", error);
                }
            } else {
                throw new Exception("No se pudo confirmar el resultado del guardado. Se conservan los archivos para verificar su asociacion antes de reintentar.", error);
            }
            throw traducirErrorDocumento(error);
        } finally {
            if (tx != null) {
                tx.cerrar();
            }
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
                        darDeBajaAsociacion(
                                creado.getAsociacion(),
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
                    reactivarAsociacion(
                            creado.getAsociacion(),
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
                        .addFileEntry(
                                userId,
                                folderId,
                                presupuesto.getNombrePersistido(),
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

        try {
            DocumentoLibraryComprasHelper.asegurarPermisoVistaAbm(entry);

            return new DocumentoPresupuestoCreado(
                    entry.getGroupId(),
                    entry.getFolderId(),
                    entry.getFileEntryId(),
                    entry.getUuid(),
                    entry.getName(),
                    entry.getTitle()
            );
        } catch (Exception errorCreacion) {
            try {
                DLFileEntryLocalServiceUtil.deleteFileEntry(entry);
            } catch (Exception cleanupError) {
                errorCreacion.addSuppressed(cleanupError);
                _log.error(
                        "No se pudo compensar un presupuesto sin permisos. "
                                + "fileEntryId=" + entry.getFileEntryId(),
                        cleanupError);
                throw new Exception(
                        "No se pudo autorizar el documento de Compras "
                                + "ni retirar el archivo creado. "
                                + "Informe a Sistemas antes de reintentar.",
                        errorCreacion);
            }
            throw errorCreacion;
        }
    }

    private RequerimientoCompraPresupuesto registrarAsociacionPresupuesto(
            int idRequerimientoCompra,
            PresupuestoValidado presupuesto,
            DocumentoPresupuestoCreado documento,
            String usuario)
            throws Exception {

        return registrarAsociacionPresupuesto(null, idRequerimientoCompra,
                presupuesto, documento, usuario);
    }

    private RequerimientoCompraPresupuesto registrarAsociacionPresupuesto(
            EditarRequerimientoCompraServiceUtil.Transaccion transaccion,
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

        if (presupuesto.getTipoDocumento()
                == RequerimientoCompraPresupuesto
                        .TIPO_DOCUMENTO_COTIZACION_EMPRESA) {

            asociacion.setIdPrestador(null);
            asociacion.setEmpresaCuit(
                    presupuesto.getEmpresaCuit()
            );
            asociacion.setEmpresaSucursal(
                    presupuesto.getEmpresaSucursal()
            );
            asociacion.setDescripcionEmpresa(
                    presupuesto.getDescripcionEmpresa()
            );

        } else {
            asociacion.setIdPrestador(
                    Integer.valueOf(
                            presupuesto.getIdPrestador()
                    )
            );
        }

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

        if (presupuesto.getTipoDocumento()
                == RequerimientoCompraPresupuesto
                        .TIPO_DOCUMENTO_PRESUPUESTO) {

            asociacion.setDescripcionPrestador(
                    presupuesto.getDescripcionPrestador()
            );
        }

        int id = transaccion != null
                ? registrarAsociacionEnTransaccion(transaccion, asociacion, usuario)
                : registrarAsociacionConTransaccionPropia(asociacion, usuario);

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

    private void validarCantidadPresupuestos(int cantidad) throws Exception {
        validarCantidadPresupuestos(cantidad, ConfiguracionComprasHelper.getMaxPresupuestosPorCarga());
    }

    private void validarCantidadPresupuestos(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int cantidad) throws Exception {
        Map<String,String> limites = ConfiguracionComprasHelper.parsearPares("COMPRAS_LIMITES",
                tx.getConfiguracionCompra("COMPRAS_LIMITES"));
        for (String limite : limites.values()) {
            ConfiguracionComprasHelper.parsearEntero("COMPRAS_LIMITES", limite, false);
        }
        validarCantidadPresupuestos(cantidad, ConfiguracionComprasHelper.parsearEntero(
                "COMPRAS_LIMITES", limites.get("PRESUPUESTOS_POR_CARGA"), false));
    }

    private void validarCantidadPresupuestos(int cantidad, int maximo) throws Exception {
        if (cantidad <= 0 || cantidad > maximo) {
            throw new IllegalArgumentException("La cantidad de presupuestos debe estar entre 1 y " + maximo + ".");
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

    public void validarDatosEmpresaCotizacion(String cuit, String sucursal,
            String nombre, int numero) throws Exception {
        validarDatosEmpresaCotizacion(null, cuit, sucursal, nombre, numero);
    }

    public void validarDatosEmpresaCotizacion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            String cuit, String sucursal, String nombre, int numero) throws Exception {
        validarFormatoDatosEmpresa(cuit, sucursal, nombre, numero);
        if (!WebKeysCompras.isEmpty(sucursal)) {
            obtenerEmpresaActiva(tx, cuit, sucursal, numero);
        }
    }

    public void validarDatosEmpresaCotizacion(String cuit, String sucursal,
            String nombre, int numero, List<Empresa> empresas) throws Exception {
        validarFormatoDatosEmpresa(cuit, sucursal, nombre, numero);
        String sucursalNormalizada = WebKeysCompras.trimToNull(sucursal);
        if (sucursalNormalizada != null) {
            validarEmpresaDisponible(empresas, WebKeysCompras.trimToNull(cuit), sucursalNormalizada);
        }
    }

    private void validarFormatoDatosEmpresa(String empresaCuit, String empresaSucursal,
            String descripcion, int numero) throws Exception {
        String cuit = WebKeysCompras.trimToNull(empresaCuit);
        String sucursal = WebKeysCompras.trimToNull(empresaSucursal);
        String nombre = WebKeysCompras.trimToNull(descripcion);
        if (nombre == null || nombre.length() > 200) {
            throw new Exception("Debe informar el nombre de la empresa de la cotizacion " + numero + " (hasta 200 caracteres).");
        }
        if (cuit != null && (cuit.length() > 11 || !CuilUtils.validarNum(cuit))) {
            throw new Exception("El CUIT de la empresa de la cotizacion " + numero + " no es valido.");
        }
        if (sucursal != null && (cuit == null || sucursal.length() > 6)) {
            throw new Exception("La sucursal requiere un CUIT valido de Empresa.");
        }
    }

    public void completarEmpresaCotizacion(int idRequerimiento, int idPresupuesto,
            String cuit, String sucursal, String usuario) throws Exception {
        actualizarEmpresaConTransaccionPropia(idRequerimiento, idPresupuesto,
                null, cuit, sucursal, true, usuario);
    }

    public void editarEmpresaCotizacion(int idRequerimiento, int idPresupuesto,
            String nombre, String cuit, String sucursal, String usuario) throws Exception {
        actualizarEmpresaConTransaccionPropia(idRequerimiento, idPresupuesto,
                nombre, cuit, sucursal, false, usuario);
    }

    private void actualizarEmpresaConTransaccionPropia(int idRequerimiento, int idPresupuesto,
            String nombre, String cuit, String sucursal, boolean completar, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            actualizarEmpresaEnTransaccion(tx, idRequerimiento, idPresupuesto,
                    nombre, cuit, sucursal, completar, usuario);
            commitIniciado = true;
            tx.commit();
        } catch (Exception error) {
            try {
                tx.rollback();
            } catch (Exception rollbackError) {
                _log.error("No se pudo confirmar el rollback de la identidad de Empresa.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado de la Empresa. Verifique sus datos antes de reintentar.", error);
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar el resultado de la Empresa. Actualice antes de reintentar.", error);
            }
            throw traducirErrorDocumento(error);
        } finally {
            tx.cerrar();
        }
    }

    public void actualizarEmpresaEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int idRequerimiento, int idPresupuesto, String nombreSolicitado,
            String cuitSolicitado, String sucursalSolicitada, boolean completar,
            String usuario) throws Exception {
        if (tx == null || idRequerimiento <= 0 || idPresupuesto <= 0) {
            throw new IllegalArgumentException("Debe informar la cotizacion y su transaccion.");
        }
        RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimiento);
        String estados = tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
        if (actual == null || !actual.isActivo()
                || (actual.getEstado() != ConfiguracionComprasHelper.resolverEstado("PENDIENTE", estados)
                    && actual.getEstado() != ConfiguracionComprasHelper.resolverEstado("ORDEN_COMPRA", estados))
                || !ConfiguracionComprasHelper.resolverSectorSinCotizacionPrestador(actual.getSectorDescripcion(),
                    tx.getConfiguracionCompra("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR"))) {
            throw new IllegalArgumentException("Solo puede editar la Empresa en el sector y estado configurados.");
        }
        validarSectorActivoDocumento(tx, actual);
        RequerimientoCompraPresupuesto documento = tx.bloquearDocumento(idRequerimiento, idPresupuesto);
        if (documento == null || !documento.isActivo() || !documento.isCotizacionEmpresa()) {
            throw new IllegalArgumentException("No se encontro la cotizacion activa de Empresa.");
        }
        String cuit = WebKeysCompras.trimToNull(cuitSolicitado);
        String anterior = WebKeysCompras.trimToNull(documento.getEmpresaCuit());
        if ((completar && cuit == null) || (cuit != null && !cuit.matches("[0-9]{11}"))) {
            throw new IllegalArgumentException("Debe informar un CUIT valido de Empresa.");
        }
        if (anterior != null && !anterior.equals(cuit)) {
            throw new IllegalArgumentException("El CUIT informado no puede reemplazarse ni eliminarse.");
        }
        String sucursal = WebKeysCompras.trimToNull(sucursalSolicitada);
        if (sucursal == null) {
            sucursal = WebKeysCompras.trimToNull(documento.getEmpresaSucursal());
        }
        String nombre = completar ? documento.getDescripcionEmpresa() : WebKeysCompras.trimToNull(nombreSolicitado);
        validarDatosEmpresaCotizacion(tx, cuit, sucursal, nombre, 1);
        RequerimientoCompraPresupuesto candidato = new RequerimientoCompraPresupuesto();
        candidato.setEmpresaCuit(cuit);
        candidato.setEmpresaSucursal(sucursal);
        validarOtraEmpresaActiva(tx.listarDocumentos(idRequerimiento,
                RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA), candidato, idPresupuesto);
        if (!tx.actualizarEmpresaDocumento(idRequerimiento, idPresupuesto, nombre,
                cuit, sucursal, normalizarUsuario(usuario))) {
            throw new IllegalStateException("La cotizacion cambio durante la actualizacion de Empresa.");
        }
    }

    public Empresa obtenerEmpresaActiva(
            String empresaCuit,
            String empresaSucursal,
            int numeroPresupuesto) throws Exception {
        return obtenerEmpresaActiva(null, empresaCuit, empresaSucursal, numeroPresupuesto);
    }

    private Empresa obtenerEmpresaActiva(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            String empresaCuit, String empresaSucursal, int numeroPresupuesto) throws Exception {

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

        List<Empresa> empresas = tx != null
                ? tx.buscarEmpresasCotizacion(cuit, sucursal)
                : BusquedaRequerimientoCompraServiceUtil.buscarEmpresasCotizacion(cuit, null, sucursal, 2);

        if (empresas == null) {
            throw new Exception(
                    "No se pudo validar la empresa seleccionada."
            );
        }

        return validarEmpresaDisponible(empresas, cuit, sucursal);
    }

    private boolean darDeBajaAsociacion(RequerimientoCompraPresupuesto presupuesto,
            int idRequerimientoPresupuesto, int idRequerimientoCompra,
            String usuario) throws Exception {
        return cambiarAsociacionConTransaccionPropia(idRequerimientoCompra,
                idRequerimientoPresupuesto, false, usuario);
    }

    private boolean reactivarAsociacion(RequerimientoCompraPresupuesto presupuesto,
            int idRequerimientoPresupuesto, int idRequerimientoCompra) throws Exception {
        return cambiarAsociacionConTransaccionPropia(idRequerimientoCompra,
                idRequerimientoPresupuesto, true, null);
    }

    public int registrarAsociacionConTransaccionPropia(
            RequerimientoCompraPresupuesto presupuesto, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            int id = registrarAsociacionEnTransaccion(tx, presupuesto, usuario);
            commitIniciado = true;
            tx.commit();
            return id;
        } catch (Exception error) {
            try {
                tx.rollback();
            } catch (Exception rollbackError) {
                _log.error("No se pudo confirmar el rollback de la asociacion del presupuesto.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado del presupuesto. Verifique su asociacion antes de reintentar.", error);
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar el resultado del presupuesto. Verifique antes de reintentar.", error);
            }
            throw traducirErrorDocumento(error);
        } finally {
            tx.cerrar();
        }
    }

    public boolean cambiarAsociacionConTransaccionPropia(int idRequerimiento,
            int idPresupuesto, boolean reactivar, String usuario, boolean empresa) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            tx.bloquearYObtenerRequerimiento(idRequerimiento);
            RequerimientoCompraPresupuesto documento = tx.bloquearDocumento(idRequerimiento, idPresupuesto);
            boolean resultado = documento != null && (empresa ? documento.isCotizacionEmpresa() : documento.isPresupuestoPrestador())
                    && cambiarAsociacionEnTransaccion(tx, idRequerimiento, idPresupuesto, reactivar, usuario);
            commitIniciado = true;
            tx.commit();
            return resultado;
        } catch (Exception error) {
            try { tx.rollback(); }
            catch (Exception rollbackError) {
                _log.error("No se pudo confirmar el rollback del cambio documental.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado documental. Verifique antes de reintentar.", error);
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar el resultado documental. Verifique antes de reintentar.", error);
            }
            throw traducirErrorDocumento(error);
        } finally { tx.cerrar(); }
    }

    private boolean cambiarAsociacionConTransaccionPropia(int idRequerimiento,
            int idPresupuesto, boolean reactivar, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        try {
            boolean resultado = cambiarAsociacionEnTransaccion(tx, idRequerimiento,
                    idPresupuesto, reactivar, usuario);
            tx.commit();
            return resultado;
        } catch (Exception error) {
            try {
                tx.rollback();
            } catch (Exception rollbackError) {
                _log.error("No se pudo confirmar el rollback del cambio documental.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado documental. Verifique su estado antes de reintentar.", error);
            }
            throw error;
        } finally {
            tx.cerrar();
        }
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
                        )
                        + identificador
                        + "_";

        // Liferay 5.2 recorta desde la extension aun cuando no sea el sufijo.
        // La identidad completa debe quedar antes del nombre descriptivo.
        int longitudDisponible =
                ConfiguracionComprasHelper.getMaxTituloDocumento()
                        - prefijo.length();

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
                + nombre;
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

        if (error instanceof java.sql.SQLException) {
            _log.error("No se pudieron persistir los datos documentales de Compras.", error);
            return new Exception("No se pudieron persistir los datos del presupuesto. Verifique el estado antes de reintentar.", error);
        }
        return error;
    }

    private String normalizarUsuario(String usuario) {
        // La normalizacion tecnica se aplica en JDBC con CURRENT_USER de la misma conexion.
        return usuario;
    }

    public static final class PresupuestoEntrada {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final int idPrestador;
        private final String empresaCuit;
        private final String empresaSucursal;
        private final String descripcionEmpresa;

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

            this(indice, archivo, nombreOriginal, idPrestador,
                    empresaCuit, empresaSucursal, null);
        }

        public PresupuestoEntrada(
                int indice, File archivo, String nombreOriginal, int idPrestador,
                String empresaCuit, String empresaSucursal, String descripcionEmpresa) {

            this.descripcionEmpresa = descripcionEmpresa;
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

        public String getDescripcionEmpresa() {
            return descripcionEmpresa;
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

    public static final class DocumentoPresupuestoCreado {

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

    public int registrarAsociacionEnTransaccion(
            EditarRequerimientoCompraServiceUtil.Transaccion tx,
            RequerimientoCompraPresupuesto presupuesto, String usuario) throws Exception {
        if (tx == null || presupuesto == null || presupuesto.getIdRequerimiento() == null
                || presupuesto.getIdRequerimiento().intValue() <= 0) {
            throw new IllegalArgumentException("No se obtuvo la transaccion del presupuesto.");
        }
        DocumentoLibraryComprasHelper.validarIdentidadAsociacionDocumento(presupuesto);
        if (WebKeysCompras.isEmpty(presupuesto.getNombreOriginal())
                || WebKeysCompras.isEmpty(presupuesto.getTitulo())) {
            throw new IllegalArgumentException("Debe informar el nombre y titulo del presupuesto.");
        }
        int idRequerimiento = presupuesto.getIdRequerimiento().intValue();
        RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimiento);
        String estados = tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
        validarEstadoPresupuesto(tx, actual, presupuesto.isCotizacionEmpresa(), estados);
        List<RequerimientoCompraPresupuesto> activos = tx.listarDocumentos(
                idRequerimiento, presupuesto.getTipoDocumento().intValue());
        if (presupuesto.isCotizacionEmpresa()) {
            if (presupuesto.getIdPrestador() != null
                    || !WebKeysCompras.isEmpty(presupuesto.getDescripcionPrestador())) {
                throw new IllegalArgumentException("Una cotizacion de Empresa no puede indicar prestador.");
            }
            validarDatosEmpresaCotizacion(tx, presupuesto.getEmpresaCuit(),
                    presupuesto.getEmpresaSucursal(), presupuesto.getDescripcionEmpresa(), 1);
            if (presupuesto.getEmpresaCuit() != null && !presupuesto.getEmpresaCuit().matches("[0-9]{11}")) {
                throw new IllegalArgumentException("El CUIT de la Empresa no es valido.");
            }
            validarOtraEmpresaActiva(activos, presupuesto, 0);
        } else {
            if (!presupuesto.isPresupuestoPrestador() || presupuesto.getIdPrestador() == null
                    || presupuesto.getIdPrestador().intValue() <= 0) {
                throw new IllegalArgumentException("El prestador del presupuesto no es valido.");
            }
            String envios = tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO);
            String enviado = ConfiguracionComprasHelper.resolverEstadoEnvio("ENVIADO", envios);
            String cotizado = ConfiguracionComprasHelper.resolverEstadoEnvio("COTIZADO", envios);
            int idPrestador = presupuesto.getIdPrestador().intValue();
            String estado = tx.bloquearEstadoPrestadorCotizacion(idRequerimiento, idPrestador);
            if (!enviado.equals(estado)) {
                throw new IllegalArgumentException("El prestador ya no esta ENVIADO y disponible para cotizar.");
            }
            validarOtroPresupuestoActivo(activos, idPrestador, 0);
            int id = tx.insertarDocumento(presupuesto, normalizarUsuario(usuario));
            if (id <= 0 || !tx.actualizarEstadoPrestadorCotizacion(idRequerimiento,
                    idPrestador, enviado, cotizado, normalizarUsuario(usuario))) {
                throw new IllegalStateException("No se pudo registrar el presupuesto y su estado COTIZADO.");
            }
            return id;
        }
        int id = tx.insertarDocumento(presupuesto, normalizarUsuario(usuario));
        if (id <= 0) {
            throw new IllegalStateException("El registro de la cotizacion no devolvio un ID valido.");
        }
        return id;
    }

    public boolean cambiarAsociacionEnTransaccion(
            EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int idRequerimiento, int idPresupuesto, boolean reactivar,
            String usuario) throws Exception {
        if (tx == null || idRequerimiento <= 0 || idPresupuesto <= 0) {
            throw new IllegalArgumentException("Debe informar el presupuesto y su transaccion.");
        }
        RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimiento);
        RequerimientoCompraPresupuesto documento = tx.bloquearDocumento(idRequerimiento, idPresupuesto);
        if (documento == null || reactivar == documento.isActivo()) {
            return false;
        }
        boolean empresa = documento.isCotizacionEmpresa();
        if (!empresa && !documento.isPresupuestoPrestador()) {
            throw new IllegalArgumentException("El documento no corresponde a un presupuesto.");
        }
        String estados = tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
        validarEstadoPresupuesto(tx, actual, empresa, estados);
        List<RequerimientoCompraPresupuesto> activos = tx.listarDocumentos(
                idRequerimiento, documento.getTipoDocumento().intValue());
        if (empresa) {
            if (reactivar) {
                validarOtraEmpresaActiva(activos, documento, idPresupuesto);
            }
            return tx.cambiarBajaDocumento(idRequerimiento, idPresupuesto,
                    reactivar, !reactivar, normalizarUsuario(usuario));
        }
        if (documento.getIdPrestador() == null || documento.getIdPrestador().intValue() <= 0) {
            throw new IllegalArgumentException("El presupuesto no identifica al prestador notificado.");
        }
        int prestador = documento.getIdPrestador().intValue();
        String estado = tx.bloquearEstadoPrestadorCotizacion(idRequerimiento, prestador);
        if (estado == null) {
            throw new IllegalArgumentException("No existe el prestador notificado del presupuesto.");
        }
        String envios = tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO);
        String enviado = ConfiguracionComprasHelper.resolverEstadoEnvio("ENVIADO", envios);
        String cotizado = ConfiguracionComprasHelper.resolverEstadoEnvio("COTIZADO", envios);
        boolean otroActivo = existeOtroPresupuestoActivo(activos, prestador, idPresupuesto);
        if (reactivar) {
            String claveUsuario = "COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION";
            usuario = tx.getConfiguracionCompra(claveUsuario);
            ConfiguracionComprasHelper.exigirValor(claveUsuario, usuario);
            if (usuario.codePointCount(0, usuario.length()) > 100) {
                usuario = usuario.substring(0, usuario.offsetByCodePoints(0, 100));
            }
            validarOtroPresupuestoActivo(activos, prestador, idPresupuesto);
            if (!enviado.equals(estado) && !cotizado.equals(estado)) {
                throw new IllegalArgumentException("No se puede restaurar el estado COTIZADO del prestador.");
            }
        } else if (!otroActivo && !cotizado.equals(estado)) {
            throw new IllegalArgumentException("No se puede restaurar el estado ENVIADO del prestador.");
        }
        if (!tx.cambiarBajaDocumento(idRequerimiento, idPresupuesto,
                reactivar, false, normalizarUsuario(usuario))) {
            return false;
        }
        if (reactivar || !otroActivo) {
            if (!tx.actualizarEstadoPrestadorCotizacion(idRequerimiento, prestador,
                    estado, reactivar ? cotizado : enviado, normalizarUsuario(usuario))) {
                throw new IllegalStateException("No se pudo guardar el estado del prestador del presupuesto.");
            }
        }
        return true;
    }

    public void guardarEmpresaAdjudicada(int idRequerimiento, int idPresupuesto, String usuario) throws Exception {
        guardarEmpresaAdjudicadaConTransaccionPropia(idRequerimiento, Integer.valueOf(idPresupuesto), null, null, usuario);
    }

    public void guardarEmpresaAdjudicada(int idRequerimiento, String cuit, String sucursal, String usuario) throws Exception {
        guardarEmpresaAdjudicadaConTransaccionPropia(idRequerimiento, null, cuit, sucursal, usuario);
    }

    private void guardarEmpresaAdjudicadaConTransaccionPropia(int idRequerimiento, Integer idPresupuesto,
            String cuit, String sucursal, String usuario) throws Exception {
        EditarRequerimientoCompraServiceUtil.Transaccion tx = EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            if (idPresupuesto != null) guardarEmpresaAdjudicadaEnTransaccion(tx, idRequerimiento, idPresupuesto.intValue(), usuario);
            else guardarEmpresaAdjudicadaEnTransaccion(tx, idRequerimiento, cuit, sucursal, usuario);
            commitIniciado = true; tx.commit();
        } catch (Exception error) {
            try { tx.rollback(); }
            catch (Exception rollbackError) {
                _log.error("No se pudo confirmar el rollback de la adjudicacion de Empresa.", rollbackError);
                throw new Exception("No se pudo confirmar la adjudicacion de Empresa. Verifique antes de reintentar.", error);
            }
            if (commitIniciado) throw new Exception("No se pudo confirmar la adjudicacion de Empresa. Verifique antes de reintentar.", error);
            throw traducirErrorDocumento(error);
        } finally { tx.cerrar(); }
    }

    public void guardarEmpresaAdjudicadaEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int idRequerimiento, int idPresupuesto, String usuario) throws Exception {
        if (idPresupuesto < 0) throw new IllegalArgumentException("La cotizacion de Empresa adjudicada no es valida.");
        List<RequerimientoCompraPresupuesto> documentos = bloquearCotizacionesAdjudicacion(tx, idRequerimiento);
        RequerimientoCompraPresupuesto seleccionada = null;
        for (RequerimientoCompraPresupuesto documento : documentos) {
            if (documento.getIdRequerimientoPresupuesto().intValue() == idPresupuesto) seleccionada = documento;
        }
        if (idPresupuesto > 0) {
            if (seleccionada == null || WebKeysCompras.isEmpty(seleccionada.getDescripcionEmpresa())) {
                throw new IllegalArgumentException("La Empresa adjudicada debe tener una cotizacion activa en este requerimiento.");
            }
            validarDatosEmpresaCotizacion(tx, seleccionada.getEmpresaCuit(), seleccionada.getEmpresaSucursal(),
                    seleccionada.getDescripcionEmpresa(), 1);
        }
        tx.marcarEmpresaAdjudicada(idRequerimiento, idPresupuesto, usuario);
    }

    public void guardarEmpresaAdjudicadaEnTransaccion(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            int idRequerimiento, String cuit, String sucursal, String usuario) throws Exception {
        List<RequerimientoCompraPresupuesto> documentos = bloquearCotizacionesAdjudicacion(tx, idRequerimiento);
        cuit = btrimDocumento(cuit); sucursal = btrimDocumento(sucursal);
        if ((cuit == null) != (sucursal == null)) throw new IllegalArgumentException("Debe informar CUIT y sucursal de la Empresa adjudicada.");
        int idPresupuesto = 0;
        if (cuit != null) {
            obtenerEmpresaActiva(tx, cuit, sucursal, 1);
            for (RequerimientoCompraPresupuesto documento : documentos) {
                if (cuit.equals(documento.getEmpresaCuit()) && sucursal.equals(documento.getEmpresaSucursal())) {
                    if (idPresupuesto > 0) throw new IllegalArgumentException("La Empresa adjudicada debe tener una unica cotizacion activa.");
                    idPresupuesto = documento.getIdRequerimientoPresupuesto().intValue();
                }
            }
            if (idPresupuesto == 0) throw new IllegalArgumentException("La Empresa adjudicada debe tener una cotizacion activa en este requerimiento.");
        }
        tx.marcarEmpresaAdjudicada(idRequerimiento, idPresupuesto, usuario);
    }

    private List<RequerimientoCompraPresupuesto> bloquearCotizacionesAdjudicacion(
            EditarRequerimientoCompraServiceUtil.Transaccion tx, int idRequerimiento) throws Exception {
        if (tx == null || idRequerimiento <= 0) throw new IllegalArgumentException("Debe informar el requerimiento y su transaccion.");
        RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(idRequerimiento);
        validarEstadoPresupuesto(tx, actual, true, tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS"));
        validarSectorActivoDocumento(tx, actual);
        List<RequerimientoCompraPresupuesto> documentos = tx.listarDocumentos(idRequerimiento,
                RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA);
        if (documentos == null) throw new IllegalStateException("No se pudieron leer las cotizaciones actuales.");
        java.util.Collections.sort(documentos, new java.util.Comparator<RequerimientoCompraPresupuesto>() {
            public int compare(RequerimientoCompraPresupuesto a, RequerimientoCompraPresupuesto b) {
                return a.getIdRequerimientoPresupuesto().compareTo(b.getIdRequerimientoPresupuesto());
            }
        });
        List<RequerimientoCompraPresupuesto> bloqueadas = new ArrayList<RequerimientoCompraPresupuesto>();
        for (RequerimientoCompraPresupuesto documento : documentos) {
            RequerimientoCompraPresupuesto bloqueada = tx.bloquearDocumento(idRequerimiento, documento.getIdRequerimientoPresupuesto().intValue());
            if (bloqueada == null || !bloqueada.isActivo() || !bloqueada.isCotizacionEmpresa()) {
                throw new IllegalArgumentException("Las cotizaciones de Empresa cambiaron. Actualice la pantalla.");
            }
            bloqueadas.add(bloqueada);
        }
        return bloqueadas;
    }

    private String btrimDocumento(String valor) {
        if (valor == null) return null;
        int inicio = 0; int fin = valor.length();
        while (inicio < fin && valor.charAt(inicio) == ' ') inicio++;
        while (fin > inicio && valor.charAt(fin - 1) == ' ') fin--;
        return inicio == fin ? null : valor.substring(inicio, fin);
    }

    private void validarSectorActivoDocumento(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            RequerimientoCompra actual) throws Exception {
        boolean activo = false;
        for (Map<String,Object> sector : tx.listarCatalogoSectores()) {
            activo = activo || (actual.getIdSector() != null && actual.getIdSector().equals(sector.get("id"))
                    && Boolean.TRUE.equals(sector.get("activo")) && sector.get("baja") == null);
        }
        if (!activo) throw new IllegalArgumentException("El sector del requerimiento ya no esta activo.");
    }

    private void validarEstadoPresupuesto(EditarRequerimientoCompraServiceUtil.Transaccion tx,
            RequerimientoCompra actual, boolean empresa, String estados) throws Exception {
        boolean permitido = actual != null && actual.isActivo()
                && (empresa ? actual.getEstado() == ConfiguracionComprasHelper.resolverEstado("PENDIENTE", estados)
                    : ConfiguracionComprasHelper.contieneEntero("COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS",
                        tx.getConfiguracionCompra("COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS"), actual.getEstado()));
        if (!permitido) {
            throw new IllegalArgumentException("El requerimiento no admite administrar ese presupuesto en su estado actual.");
        }
        if (empresa && !ConfiguracionComprasHelper.resolverSectorSinCotizacionPrestador(
                actual.getSectorDescripcion(), tx.getConfiguracionCompra("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR"))) {
            throw new IllegalArgumentException("El sector no admite cotizaciones de Empresa.");
        }
    }

    private void validarOtroPresupuestoActivo(List<RequerimientoCompraPresupuesto> activos,
            int prestador, int excluir) {
        if (existeOtroPresupuestoActivo(activos, prestador, excluir)) {
            throw new IllegalArgumentException("El prestador ya tiene otro presupuesto activo.");
        }
    }

    private boolean existeOtroPresupuestoActivo(List<RequerimientoCompraPresupuesto> activos,
            int prestador, int excluir) {
        if (activos == null) {
            throw new IllegalStateException("No se pudieron leer los presupuestos vigentes.");
        }
        for (RequerimientoCompraPresupuesto actual : activos) {
            if (actual != null && actual.isActivo() && actual.getIdPrestador() != null
                    && actual.getIdPrestador().intValue() == prestador
                    && actual.getIdRequerimientoPresupuesto().intValue() != excluir) {
                return true;
            }
        }
        return false;
    }

    private void validarOtraEmpresaActiva(List<RequerimientoCompraPresupuesto> activos,
            RequerimientoCompraPresupuesto documento, int excluir) {
        if (activos == null) {
            throw new IllegalStateException("No se pudieron leer las cotizaciones vigentes.");
        }
        String cuit = WebKeysCompras.trimToNull(documento.getEmpresaCuit());
        for (RequerimientoCompraPresupuesto actual : activos) {
            if (cuit != null && actual != null && actual.isActivo()
                    && actual.getIdRequerimientoPresupuesto().intValue() != excluir
                    && cuit.equals(WebKeysCompras.trimToNull(actual.getEmpresaCuit()))
                    && String.valueOf(WebKeysCompras.trimToNull(documento.getEmpresaSucursal())).equals(
                        String.valueOf(WebKeysCompras.trimToNull(actual.getEmpresaSucursal())))) {
                throw new IllegalArgumentException("La Empresa ya tiene otra cotizacion activa.");
            }
        }
    }

    private Empresa validarEmpresaDisponible(List<Empresa> empresas, String cuit,
            String sucursal) throws Exception {
        if (empresas == null) {
            throw new IllegalArgumentException("No se pudo validar la empresa seleccionada.");
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
}
