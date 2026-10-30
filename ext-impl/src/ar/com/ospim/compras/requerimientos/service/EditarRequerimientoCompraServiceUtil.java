package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import java.util.List;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoComprasCreado;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;

import java.math.BigDecimal;

/**
 * Fachada estatica de persistencia para la edicion de requerimientos.
 * Las validaciones y la coordinacion documental permanecen en el Helper.
 */
public class EditarRequerimientoCompraServiceUtil {

    private static EditarRequerimientoCompraServiceImpl instance;

    private static EditarRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new EditarRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static Transaccion abrirTransaccion() throws Exception {
        return new Transaccion(getInstance().abrirTransaccion());
    }

    public static int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        return getInstance().guardarRequerimientoCompra(
                requerimiento,
                usuario
        );
    }

    public static int guardarObservacionInterna(
            int idRequerimientoCompra,
            String observacionInterna,
            String usuario) throws Exception {
        return getInstance().guardarObservacionInterna(
                idRequerimientoCompra, observacionInterna, usuario);
    }

    public static int corregirItem(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper()
                .corregirItem(detalle, usuario);
    }

    public static int guardarDetalle(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {

        return getInstance().guardarDetalle(
                detalle,
                usuario
        );
    }

    public static void borrarDetalle(
            int idDetalle,
            String usuario) throws Exception {

        getInstance().borrarDetalle(
                idDetalle,
                usuario
        );
    }

    public static void anularRequerimiento(
            int idRequerimientoCompra,
            String motivoBaja,
            String usuario) throws Exception {

        getInstance().anularRequerimiento(
                idRequerimientoCompra,
                motivoBaja,
                usuario
        );
    }

    public static void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        getInstance().cambiarEstado(
                idRequerimientoCompra,
                idEstadoNuevo,
                usuario
        );
    }

    public static int guardarCotizacion(
            int idRequerimientoCompra,
            Integer[] idsDetalle,
            BigDecimal[] preciosUnitarios,
            Integer[] idsDetalleEliminados,
            Integer idPrestadorAdjudicado,
            boolean surge,
            String usuario) throws Exception {

        return getInstance().guardarCotizacion(
                idRequerimientoCompra,
                idsDetalle,
                preciosUnitarios,
                idsDetalleEliminados,
                idPrestadorAdjudicado,
                surge,
                usuario
        );
    }

    public static int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        return getInstance().registrarPresupuesto(
                presupuesto,
                usuario
        );
    }

    public static void guardarEmpresaAdjudicada(
            int idRequerimientoCompra,
            String empresaCuit,
            String empresaSucursal,
            String usuario) throws Exception {

        getInstance().guardarEmpresaAdjudicada(
                idRequerimientoCompra, empresaCuit, empresaSucursal, usuario);
    }

    public static void guardarEmpresaAdjudicada(
            int idRequerimientoCompra,
            int idRequerimientoPresupuesto,
            String usuario) throws Exception {

        getInstance().guardarEmpresaAdjudicada(
                idRequerimientoCompra, idRequerimientoPresupuesto, usuario);
    }

    public static void completarEmpresaCotizacion(
            int idRequerimientoCompra,
            int idRequerimientoPresupuesto,
            String empresaCuit,
            String empresaSucursal,
            String usuario) throws Exception {

        getInstance().completarEmpresaCotizacion(
                idRequerimientoCompra, idRequerimientoPresupuesto,
                empresaCuit, empresaSucursal, usuario);
    }

    public static void editarEmpresaCotizacion(
            int idRequerimientoCompra,
            int idRequerimientoPresupuesto,
            String descripcionEmpresa,
            String empresaCuit,
            String empresaSucursal,
            String usuario) throws Exception {

        getInstance().editarEmpresaCotizacion(
                idRequerimientoCompra, idRequerimientoPresupuesto,
                descripcionEmpresa, empresaCuit, empresaSucursal, usuario);
    }

    public static boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return getInstance().darDeBajaPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra,
                usuario
        );
    }

    public static boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return getInstance().reactivarPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    public static boolean darDeBajaCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return getInstance().darDeBajaCotizacionEmpresa(
                idRequerimientoPresupuesto,
                idRequerimientoCompra,
                usuario
        );
    }

    public static boolean reactivarCotizacionEmpresa(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return getInstance().reactivarCotizacionEmpresa(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    public static int confirmarEnvioACotizar(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return getInstance().confirmarEnvioACotizar(
                idRequerimientoCompra,
                usuario
        );
    }

    public static int confirmarOrdenCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return getInstance().confirmarOrdenCompra(
                idRequerimientoCompra,
                usuario
        );
    }

    /**
     * Delega sobre la misma transaccion del Impl, sin abrir conexiones
     * adicionales ni alterar commit, rollback o cierre.
     */
    public static final class Transaccion {

        private final EditarRequerimientoCompraServiceImpl.Transaccion delegate;

        Transaccion(
                EditarRequerimientoCompraServiceImpl.Transaccion delegate) {

            this.delegate = delegate;
        }

        public int guardarRequerimientoCompra(
                RequerimientoCompra requerimiento,
                String usuario) throws Exception {

            return delegate.guardarRequerimientoCompra(
                    requerimiento,
                    usuario
            );
        }

        public int guardarDetalle(
                RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {

            return delegate.guardarDetalle(detalle, usuario);
        }

        public int registrarPresupuesto(
                RequerimientoCompraPresupuesto presupuesto,
                String usuario) throws Exception {

            return delegate.registrarPresupuesto(presupuesto, usuario);
        }

        public void guardarEmpresaAdjudicada(
                int idRequerimientoCompra,
                String empresaCuit,
                String empresaSucursal,
                String usuario) throws Exception {

            delegate.guardarEmpresaAdjudicada(
                    idRequerimientoCompra, empresaCuit,
                    empresaSucursal, usuario);
        }

        public void guardarEmpresaAdjudicada(
                int idRequerimientoCompra,
                int idRequerimientoPresupuesto,
                String usuario) throws Exception {

            delegate.guardarEmpresaAdjudicada(
                    idRequerimientoCompra, idRequerimientoPresupuesto, usuario);
        }

        public int registrarOrdenMedica(
                int idRequerimiento,
                OrdenMedicaValidada ordenMedica,
                DocumentoComprasCreado documento,
                String usuario) throws Exception {

            return delegate.registrarOrdenMedica(
                    idRequerimiento,
                    ordenMedica,
                    documento,
                    usuario
            );
        }

        public boolean tieneCreacionReclamoPrestacional(int idRequerimiento) throws Exception {
            return delegate.tieneCreacionReclamoPrestacional(idRequerimiento);
        }

        public boolean existeRequerimientoDuplicado(String cuilTitular, int inte,
                int idPrestacion, java.util.Date fechaOrdenMedica,
                int idRequerimientoExcluir) throws Exception {
            return delegate.existeRequerimientoDuplicado(cuilTitular, inte,
                    idPrestacion, fechaOrdenMedica, idRequerimientoExcluir);
        }

        public int persistirObservacionInterna(int id,int estado,String observacion,String usuario)throws Exception {
            return delegate.persistirObservacionInterna(id,estado,observacion,usuario);
        }
        public int persistirEstado(int id,int anterior,int nuevo,String usuario,String motivo,boolean baja)throws Exception {
            return delegate.persistirEstado(id,anterior,nuevo,usuario,motivo,baja);
        }
        public int persistirSurge(int id,int estado,boolean surge,String usuario)throws Exception {
            return delegate.persistirSurge(id,estado,surge,usuario);
        }
        public int persistirBajaDetalle(int id,int detalle,String usuario)throws Exception {
            return delegate.persistirBajaDetalle(id,detalle,usuario);
        }
        public java.util.List<java.util.Map<String,Object>> listarCatalogoSectores()throws Exception {
            return delegate.listarCatalogoSectores();
        }
        public java.util.Map<String,Object> obtenerSectorCatalogo(int id)throws Exception {
            return delegate.obtenerSectorCatalogo(id);
        }
        public java.util.List<java.util.Map<String,Object>> obtenerRelacionesRp(int id)throws Exception {
            return delegate.obtenerRelacionesRp(id);
        }

        public RequerimientoCompra bloquearYObtenerRequerimiento(
                int idRequerimiento) throws Exception {
            return delegate.bloquearYObtenerRequerimiento(idRequerimiento);
        }

        public List<RequerimientoCompraDetalle> getDetalles(int idRequerimiento) throws Exception {
            return delegate.getDetalles(idRequerimiento);
        }

        public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int idRequerimiento) throws Exception {
            return delegate.listarOrdenesMedicas(idRequerimiento);
        }

        public List<RequerimientoCompraPresupuesto> listarPresupuestos(int idRequerimiento) throws Exception {
            return delegate.listarPresupuestos(idRequerimiento);
        }

        public List<TipoPrestacionCompra> listarTiposPrestacion() throws Exception {
            return delegate.listarTiposPrestacion();
        }

        public List<RequerimientoCompraSector> listarSectores() throws Exception {
            return delegate.listarSectores();
        }

        public List<SectorNomencladorCompra> listarAsociacionesNomenclador(
                int idSector) throws Exception {
            return delegate.listarAsociacionesNomenclador(idSector);
        }

        public Nomenclador obtenerNomencladorCanonico(int idPrestacion) throws Exception {
            return delegate.obtenerNomencladorCanonico(idPrestacion);
        }

        public String getConfiguracionCompra(String clave) throws Exception {
            return delegate.getConfiguracionCompra(clave);
        }

        public List<Integer> bloquearPrestacionesComparativaCorreccion(int id) throws Exception {
            return delegate.bloquearPrestacionesComparativaCorreccion(id);
        }
        public int actualizarReferenciaComparativaCorreccion(int id, Integer origen,
                int destino, String usuario) throws Exception {
            return delegate.actualizarReferenciaComparativaCorreccion(id, origen, destino, usuario);
        }
        public java.util.Map<String, Object> consultarPrestadorCorreccion(int id) throws Exception {
            return delegate.consultarPrestadorCorreccion(id);
        }
        public List<String> listarRubrosPrestadorCorreccion(int id) throws Exception {
            return delegate.listarRubrosPrestadorCorreccion(id);
        }

        public int corregirItem(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return delegate.corregirItem(detalle, usuario);
        }

        public int guardarConceptoAutomatico(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return delegate.guardarConceptoAutomatico(detalle, usuario);
        }

        public int persistirTotalDetalle(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return delegate.persistirTotalDetalle(detalle, usuario);
        }

        public int persistirImportesDetalle(RequerimientoCompraDetalle detalle,
                String usuario) throws Exception {
            return delegate.persistirImportesDetalle(detalle, usuario);
        }

        public int guardarCotizacion(int idRequerimiento, Integer[] idsDetalle,
                BigDecimal[] preciosUnitarios, Integer[] idsEliminados,
                Integer idPrestador, boolean surge, String usuario) throws Exception {
            return delegate.guardarCotizacion(idRequerimiento, idsDetalle,
                    preciosUnitarios, idsEliminados, idPrestador, surge, usuario);
        }


        public RequerimientoCompraPresupuesto bloquearDocumento(int idRequerimiento, int idDocumento) throws Exception {
            return delegate.bloquearDocumento(idRequerimiento, idDocumento);
        }
        public java.util.List<ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion> listarPrestadoresEnviados(int id,int limite)throws Exception {
            return delegate.listarPrestadoresEnviados(id,limite);
        }
        public List<RequerimientoCompraPresupuesto> listarDocumentos(int idRequerimiento, int tipoDocumento) throws Exception {
            return delegate.listarDocumentos(idRequerimiento, tipoDocumento);
        }
        public java.util.List<ar.com.ospim.global.beans.Empresa> buscarEmpresasCotizacion(String cuit, String sucursal) throws Exception {
            return delegate.buscarEmpresasCotizacion(cuit, sucursal);
        }
        public void marcarEmpresaAdjudicada(int idRequerimiento, int idPresupuesto, String usuario) throws Exception {
            delegate.marcarEmpresaAdjudicada(idRequerimiento, idPresupuesto, usuario);
        }

        public int insertarDocumento(RequerimientoCompraPresupuesto documento, String usuario) throws Exception {
            return delegate.insertarDocumento( documento, usuario);
        }
        public String bloquearEstadoPrestadorCotizacion(int idRequerimiento, int idPrestador) throws Exception {
            return delegate.bloquearEstadoPrestadorCotizacion( idRequerimiento, idPrestador);
        }
        public boolean actualizarEstadoPrestadorCotizacion(int idRequerimiento, int idPrestador, String anterior, String nuevo, String usuario) throws Exception {
            return delegate.actualizarEstadoPrestadorCotizacion( idRequerimiento, idPrestador, anterior, nuevo, usuario);
        }
        public boolean cambiarBajaDocumento(int idRequerimiento, int idDocumento, boolean reactivar, boolean limpiarAdjudicacion, String usuario) throws Exception {
            return delegate.cambiarBajaDocumento( idRequerimiento, idDocumento, reactivar, limpiarAdjudicacion, usuario);
        }
        public void bloquearClaveReceta(int idRequerimiento, java.util.Date fecha, String receta) throws Exception {
            delegate.bloquearClaveReceta(idRequerimiento, fecha, receta);
        }
        public Integer buscarRequerimientoRecetaDuplicada(int idRequerimiento, java.util.Date fecha, String receta, int estadoExcluido) throws Exception {
            return delegate.buscarRequerimientoRecetaDuplicada(idRequerimiento, fecha, receta, estadoExcluido);
        }

        public void guardarDatosComparativas(
                java.util.List<ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa> comparativas,
                java.util.List<Integer> vaciados, String usuario) throws Exception {
            delegate.guardarDatosComparativas(comparativas, vaciados, usuario);
        }

        public boolean actualizarEmpresaDocumento(int idRequerimiento, int idDocumento,
                String nombre, String cuit, String sucursal, String usuario) throws Exception {
            return delegate.actualizarEmpresaDocumento(idRequerimiento, idDocumento, nombre, cuit, sucursal, usuario);
        }

        public void commit() throws Exception {
            delegate.commit();
        }

        public void rollback() throws Exception {
            delegate.rollback();
        }

        public void cerrar() {
            delegate.cerrar();
        }
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}
