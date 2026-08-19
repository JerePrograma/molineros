package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.model.User;

import java.sql.Connection;

/**
 * Infraestructura transaccional focalizada para la creacion y vinculacion
 * de un Reclamo Prestacional desde Compras.
 *
 * Mantiene una unica conexion fisica durante todo el flujo y no contiene
 * validaciones funcionales, permisos, HTTP ni reglas de estado.
 */
public final class RequerimientoCompraReclamoPrestacionalTransaccion {

    private final RequerimientoCompraReclamoPrestacionalServiceImpl
            comprasPersistence;

    private final ReclamoPrestacionServiceImpl reclamoPersistence;

    private Connection con;

    private RequerimientoCompraReclamoPrestacionalTransaccion(
            RequerimientoCompraReclamoPrestacionalServiceImpl
                    comprasPersistence,
            Connection con) {

        this.comprasPersistence = comprasPersistence;
        this.reclamoPersistence = new ReclamoPrestacionServiceImpl();
        this.con = con;
    }

    public static RequerimientoCompraReclamoPrestacionalTransaccion abrir(
            RequerimientoCompraReclamoPrestacionalServiceImpl
                    comprasPersistence) throws Exception {

        if (comprasPersistence == null) {
            throw new IllegalArgumentException(
                    "La persistencia de Compras no puede ser nula."
            );
        }

        Connection con =
                ConnectionHelper.getConnectionForTransaction();

        if (con == null) {
            throw new Exception(
                    "No se obtuvo una conexion transaccional para vincular el RP."
            );
        }

        return new RequerimientoCompraReclamoPrestacionalTransaccion(
                comprasPersistence,
                con
        );
    }

    public RequerimientoCompraReclamoPrestacional obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        return comprasPersistence.obtenerPorRequerimiento(
                getConnection(),
                idRequerimientoCompra
        );
    }

    public boolean reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        return comprasPersistence.reservarCreacion(
                getConnection(),
                idRequerimientoCompra,
                tokenReserva,
                usuario
        );
    }

    public boolean finalizarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        return comprasPersistence.finalizarCreacion(
                getConnection(),
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                usuario
        );
    }

    public boolean bloquearRequerimiento(
            int idRequerimientoCompra) throws Exception {

        return comprasPersistence.bloquearRequerimiento(
                getConnection(),
                idRequerimientoCompra
        );
    }

    public int getEstadoRequerimientoForUpdate(
            int idRequerimientoCompra) throws Exception {

        return comprasPersistence.getEstadoRequerimientoForUpdate(
                getConnection(),
                idRequerimientoCompra
        );
    }

    public void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        comprasPersistence.cambiarEstado(
                getConnection(),
                idRequerimientoCompra,
                idEstadoNuevo,
                usuario
        );
    }

    public int insertarReclamoPrestacional(
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        return reclamoPersistence.insertar(
                getConnection(),
                reclamo,
                user
        );
    }

    public void commit() throws Exception {
        getConnection().commit();
    }

    public void rollback() throws Exception {
        getConnection().rollback();
    }

    public void cerrar() {
        Connection actual = con;
        con = null;
        ConnectionHelper.cerrar(actual);
    }

    private Connection getConnection() {
        if (con == null) {
            throw new IllegalStateException(
                    "La transaccion de Compras / RP ya se encuentra cerrada."
            );
        }

        return con;
    }
}
