package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.services.ReclamoPrestacionServiceImpl;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.model.User;

import java.sql.Connection;

/**
 * Infraestructura transaccional focalizada para la creación y vinculación
 * de un Reclamo Prestacional desde Compras.
 *
 * Mantiene una única conexión física durante todo el flujo y no contiene
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

    public static RequerimientoCompraReclamoPrestacionalTransaccion sobreConexion(
            RequerimientoCompraReclamoPrestacionalServiceImpl service,Connection con) {
        if(service==null||con==null)throw new IllegalArgumentException("Debe informar la persistencia y conexion caller.");
        return new RequerimientoCompraReclamoPrestacionalTransaccion(service,con);
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
                    "No se obtuvo una conexión transaccional para vincular el RP."
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

        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper()
                .reservarCreacionEnTransaccion(this,idRequerimientoCompra,tokenReserva,usuario);
    }

    public boolean finalizarCreacion(int id,String token,int reclamo,String usuario)throws Exception {
        return new ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper()
                .finalizarCreacionEnTransaccion(this,id,token,reclamo,usuario);
    }

    public java.util.Map<String,Object> obtenerCabeceraReserva(int id)throws Exception {return comprasPersistence.obtenerCabeceraReserva(getConnection(),id);}
    public int insertarReserva(int id,String estado,String token,String usuario)throws Exception {return comprasPersistence.insertarReserva(getConnection(),id,estado,token,usuario);}
    public int actualizarVinculo(int id,String anterior,String token,int reclamo,String nuevo,String error,boolean limpiarToken,String usuario)throws Exception {
        return comprasPersistence.actualizarVinculo(getConnection(),id,anterior,token,reclamo,nuevo,error,limpiarToken,usuario);
    }
    public int eliminarReserva(int id,String estado,String token)throws Exception {return comprasPersistence.eliminarReserva(getConnection(),id,estado,token);}
    public String resolverUsuarioAuditoria(String usuario)throws Exception {return new EditarRequerimientoCompraServiceImpl().resolverUsuarioAuditoria(getConnection(),usuario);}

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

        comprasPersistence.cambiarEstado(getConnection(), idRequerimientoCompra, idEstadoNuevo, usuario);
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

    public void guardarObservacionEstadoReclamo(
            int idReclamoPrestacional,
            int idEstado,
            String observacion) throws Exception {

        comprasPersistence.guardarObservacionEstadoReclamo(
                getConnection(),
                idReclamoPrestacional,
                idEstado,
                observacion
        );
    }

    public String getConfiguracionCompra(String clave) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave, getConnection());
    }

    public ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra getRequerimientoCompra(
            int idRequerimiento) throws Exception {
        getEstadoRequerimientoForUpdate(idRequerimiento);
        return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(getConnection(), idRequerimiento);
    }

    public ar.com.ospim.autorizaciones.beans.Nomenclador obtenerNomencladorCanonico(int id)
            throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.obtenerNomencladorCanonico(getConnection(), id);
    }

    public java.util.List<ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector>
            listarSectores() throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarSectores(getConnection());
    }

    public java.util.List<ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra>
            listarTiposPrestacion() throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarTiposPrestacion(getConnection());
    }

    public java.util.List<ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra>
            listarAsociacionesNomenclador(int idSector) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarAsociacionesNomenclador(getConnection(), idSector);
    }

    public java.sql.Timestamp bloquearReclamoPrestacional(int idReclamo) throws Exception {
        return comprasPersistence.bloquearReclamoPrestacional(getConnection(), idReclamo);
    }

    public java.util.List<RequerimientoCompraReclamoPrestacional>
            obtenerVinculosPorRPForUpdate(int idReclamo) throws Exception {
        return comprasPersistence.obtenerVinculosPorRPForUpdate(getConnection(), idReclamo);
    }

    public int guardarBajaVinculo(RequerimientoCompraReclamoPrestacional vinculo,
            java.sql.Timestamp fecha, String usuario, String estadoEsperado) throws Exception {
        return comprasPersistence.guardarBajaVinculo(
                getConnection(), vinculo, fecha, usuario, estadoEsperado);
    }

    public int borrarReclamoPrestacional(int idReclamo, String usuario) throws Exception {
        return ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil
                .borrar(getConnection(), idReclamo, usuario);
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
                    "La transacción de Compras / RP ya se encuentra cerrada."
            );
        }

        return con;
    }
}
