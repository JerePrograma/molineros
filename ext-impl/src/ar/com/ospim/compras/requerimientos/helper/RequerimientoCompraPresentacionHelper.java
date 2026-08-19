package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;

import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prepara datos de presentación reutilizables por los Actions de Compras.
 *
 * No interpreta parámetros HTTP ni modifica RenderRequest. La capa Action
 * decide qué contexto publicar y este Helper resuelve únicamente las
 * colecciones y validaciones necesarias para renderizar cotización, adjuntos
 * y Órdenes médicas sin consultar Services desde los JSP.
 */
public final class RequerimientoCompraPresentacionHelper {

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

    public ContextoPresentacion preparar(
            RequerimientoCompra requerimiento,
            long scopeGroupId,
            String pathMain) {

        ContextoPresentacion contexto =
                new ContextoPresentacion();

        if (requerimiento == null
                || requerimiento.getIdRequerimientoCompra() <= 0) {

            return contexto;
        }

        int idRequerimientoCompra =
                requerimiento.getIdRequerimientoCompra();

        cargarPrestadores(
                contexto,
                requerimiento,
                idRequerimientoCompra
        );

        cargarPresupuestos(
                contexto,
                requerimiento,
                idRequerimientoCompra,
                scopeGroupId,
                pathMain
        );

        cargarOrdenesMedicas(
                contexto,
                idRequerimientoCompra
        );

        return contexto;
    }

    private void cargarPrestadores(
            ContextoPresentacion contexto,
            RequerimientoCompra requerimiento,
            int idRequerimientoCompra) {

        if (requerimiento == null
                || (!requerimiento.puedeVerPresupuestos()
                && !requerimiento.puedeEditarCotizacion())) {

            return;
        }

        try {
            List<PrestadorCotizacion> prestadores =
                    busquedaHelper.listarPrestadoresEnviados(
                            idRequerimientoCompra
                    );

            if (prestadores != null) {
                contexto.prestadoresEnviados.addAll(
                        prestadores
                );
            }

            for (int i = 0;
                    i < contexto.prestadoresEnviados.size();
                    i++) {

                PrestadorCotizacion prestador =
                        contexto.prestadoresEnviados.get(i);

                if (prestador != null
                        && prestador.getIdPrestador() > 0
                        && WebKeysCompras.ENVIO_ENVIADO.equals(
                                prestador.getEstadoEnvio()
                        )) {

                    contexto.prestadoresDisponiblesPresupuesto.add(
                            prestador
                    );
                }
            }

        } catch (Exception e) {
            contexto.errorPrestadoresEnviados =
                    obtenerMensaje(
                            e,
                            "No se pudieron cargar los prestadores enviados."
                    );
        }
    }

    private void cargarPresupuestos(
            ContextoPresentacion contexto,
            RequerimientoCompra requerimiento,
            int idRequerimientoCompra,
            long scopeGroupId,
            String pathMain) {

        if (requerimiento == null
                || !requerimiento.puedeVerPresupuestos()) {

            return;
        }

        try {
            List<RequerimientoCompraPresupuesto> presupuestos =
                    busquedaHelper.listarPresupuestos(
                            idRequerimientoCompra
                    );

            if (presupuestos != null) {
                contexto.presupuestos.addAll(
                        presupuestos
                );
            }

            for (int i = 0;
                    i < contexto.presupuestos.size();
                    i++) {

                RequerimientoCompraPresupuesto presupuesto =
                        contexto.presupuestos.get(i);

                if (presupuesto == null
                        || presupuesto.getIdRequerimientoPresupuesto() == null
                        || presupuesto
                                .getIdRequerimientoPresupuesto()
                                .intValue() <= 0) {

                    continue;
                }

                Integer idPresupuesto =
                        presupuesto.getIdRequerimientoPresupuesto();

                if (presupuesto.getBajaFecha() == null
                        && presupuesto.getIdPrestador() != null
                        && presupuesto.getIdPrestador().intValue() > 0
                        && presupuesto.getDlFileEntryId() != null
                        && presupuesto.getDlFileEntryId().longValue() > 0L) {

                    contexto.idsPrestadoresConPresupuesto.add(
                            presupuesto.getIdPrestador()
                    );
                }

                prepararDocumentoPresupuesto(
                        contexto,
                        idPresupuesto,
                        presupuesto,
                        scopeGroupId,
                        pathMain
                );
            }

        } catch (Exception e) {
            contexto.errorPresupuestos =
                    obtenerMensaje(
                            e,
                            "No se pudieron cargar los presupuestos asociados al requerimiento."
                    );
        }
    }

    private void prepararDocumentoPresupuesto(
            ContextoPresentacion contexto,
            Integer idPresupuesto,
            RequerimientoCompraPresupuesto presupuesto,
            long scopeGroupId,
            String pathMain) {

        boolean valido = false;
        String downloadURL = "";

        try {
            if (presupuesto != null
                    && presupuesto.getDlFileEntryId() != null
                    && presupuesto.getDlFileEntryId().longValue() > 0L) {

                DLFileEntry fileEntry =
                        DLFileEntryLocalServiceUtil.getDLFileEntry(
                                presupuesto
                                        .getDlFileEntryId()
                                        .longValue()
                        );

                valido =
                        DocumentoLibraryComprasHelper
                                .coincideIdentidadAsociacionDocumento(
                                        presupuesto,
                                        fileEntry
                                );

                if (valido && scopeGroupId > 0L) {
                    valido =
                            fileEntry.getGroupId()
                                    == scopeGroupId;
                }

                if (valido
                        && !WebKeysCompras.isEmpty(pathMain)) {

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
            valido = false;
            downloadURL = "";
        }

        contexto.presupuestoDocumentoValido.put(
                idPresupuesto,
                Boolean.valueOf(valido)
        );

        contexto.presupuestoDownloadURL.put(
                idPresupuesto,
                downloadURL
        );
    }

    private void cargarOrdenesMedicas(
            ContextoPresentacion contexto,
            int idRequerimientoCompra) {

        try {
            List<RequerimientoCompraPresupuesto> ordenes =
                    busquedaHelper.listarOrdenesMedicas(
                            idRequerimientoCompra
                    );

            if (ordenes != null) {
                contexto.ordenesMedicas.addAll(
                        ordenes
                );
            }

        } catch (Exception e) {
            contexto.errorOrdenesMedicas =
                    obtenerMensaje(
                            e,
                            "No se pudieron recuperar las Órdenes médicas del requerimiento."
                    );
        }
    }

    private String obtenerMensaje(
            Exception e,
            String defaultValue) {

        if (e != null
                && !WebKeysCompras.isEmpty(
                        e.getMessage()
                )) {

            return e.getMessage();
        }

        return defaultValue;
    }

    public static final class ContextoPresentacion {

        private final List<PrestadorCotizacion> prestadoresEnviados =
                new ArrayList<PrestadorCotizacion>();

        private final List<PrestadorCotizacion>
                prestadoresDisponiblesPresupuesto =
                new ArrayList<PrestadorCotizacion>();

        private final List<RequerimientoCompraPresupuesto> presupuestos =
                new ArrayList<RequerimientoCompraPresupuesto>();

        private final Set<Integer> idsPrestadoresConPresupuesto =
                new HashSet<Integer>();

        private final Map<Integer, Boolean> presupuestoDocumentoValido =
                new HashMap<Integer, Boolean>();

        private final Map<Integer, String> presupuestoDownloadURL =
                new HashMap<Integer, String>();

        private final List<RequerimientoCompraPresupuesto> ordenesMedicas =
                new ArrayList<RequerimientoCompraPresupuesto>();

        private String errorPrestadoresEnviados = "";
        private String errorPresupuestos = "";
        private String errorOrdenesMedicas = "";

        public List<PrestadorCotizacion> getPrestadoresEnviados() {
            return prestadoresEnviados;
        }

        public List<PrestadorCotizacion>
                getPrestadoresDisponiblesPresupuesto() {

            return prestadoresDisponiblesPresupuesto;
        }

        public List<RequerimientoCompraPresupuesto> getPresupuestos() {
            return presupuestos;
        }

        public Set<Integer> getIdsPrestadoresConPresupuesto() {
            return idsPrestadoresConPresupuesto;
        }

        public Map<Integer, Boolean> getPresupuestoDocumentoValido() {
            return presupuestoDocumentoValido;
        }

        public Map<Integer, String> getPresupuestoDownloadURL() {
            return presupuestoDownloadURL;
        }

        public List<RequerimientoCompraPresupuesto> getOrdenesMedicas() {
            return ordenesMedicas;
        }

        public String getErrorPrestadoresEnviados() {
            return errorPrestadoresEnviados;
        }

        public String getErrorPresupuestos() {
            return errorPresupuestos;
        }

        public String getErrorOrdenesMedicas() {
            return errorOrdenesMedicas;
        }
    }
}
