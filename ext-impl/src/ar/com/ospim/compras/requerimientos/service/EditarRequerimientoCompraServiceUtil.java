package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
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

        private Transaccion(
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
