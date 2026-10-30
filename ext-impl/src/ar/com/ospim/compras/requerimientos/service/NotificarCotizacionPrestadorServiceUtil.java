package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

import ar.com.ospim.compras.requerimientos.beans.CotizacionPrestadorDiagnostico;
import ar.com.ospim.compras.requerimientos.beans.FinalizacionCotizacionPrestador;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion;
import ar.com.ospim.compras.requerimientos.beans.ReservaCotizacionPrestador;

import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import ar.com.ospim.compras.requerimientos.helper.NotificarCotizacionPrestadorHelper;

/**
 * Fachada estática legacy para el servicio de notificación
 * de cotizaciones a prestadores.
 */
public class NotificarCotizacionPrestadorServiceUtil {

    private static NotificarCotizacionPrestadorServiceImpl instance = null;

    public static NotificarCotizacionPrestadorServiceImpl getInstance() {

        if (instance == null) {
            instance =
                    new NotificarCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        return new NotificarCotizacionPrestadorHelper().listarPrestadoresCandidatosJava(
                        idRequerimientoCompra
                );
    }

    public static List<PrestadorCotizacion>
    listarPrestadoresConfiguracionCorreosPorRubro(
            int idTipoPrestacion) throws Exception {

        return new NotificarCotizacionPrestadorHelper().listarPrestadoresConfiguracionCorreosPorRubroJava(
                        idTipoPrestacion
                );
    }

    public static CotizacionPrestadorDiagnostico diagnosticarPrestadores(
            int idRequerimientoCompra) throws Exception {

        return new NotificarCotizacionPrestadorHelper().diagnosticarPrestadoresJava(
                        idRequerimientoCompra
                );
    }

    public static ReservaCotizacionPrestador reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        return new NotificarCotizacionPrestadorHelper().reservarCotizacionPrestadorJava(
                        idRequerimientoCompra,
                        idPrestador,
                        usuario
                );
    }

    public static FinalizacionCotizacionPrestador
    finalizarCotizacionPrestador(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) throws Exception {

        return new NotificarCotizacionPrestadorHelper().finalizarCotizacionPrestadorJava(
                        idRequerimiento,
                        idPrestador,
                        estado,
                        error,
                        usuario
                );
    }

    public static int registrarPedidoCotizacionDocumento(
            RequerimientoCompraPedidoCotizacion documento,
            String usuario) throws Exception {

        return new NotificarCotizacionPrestadorHelper().registrarPedidoCotizacionDocumentoJava(
                        documento,
                        usuario
                );
    }

    public static boolean reservarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        return getInstance()
                .reservarCopiaCotizacion(
                        idRequerimiento
                );
    }

    public static void liberarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        getInstance()
                .liberarCopiaCotizacion(
                        idRequerimiento
                );
    }
    public static Transaccion abrirTransaccion() throws Exception {
        return new Transaccion(getInstance().abrirTransaccion());
    }

    public static Transaccion usarTransaccion(java.sql.Connection con) throws Exception {return new Transaccion(getInstance().usarTransaccion(con));}

    public static final class Transaccion {
        private final NotificarCotizacionPrestadorServiceImpl.Transaccion delegate;
        private Transaccion(NotificarCotizacionPrestadorServiceImpl.Transaccion valor) { delegate = valor; }
        public RequerimientoCompra getRequerimiento(int id, boolean bloquear) throws Exception { return delegate.getRequerimiento(id, bloquear); }
        public String getConfiguracionCompra(String clave) throws Exception { return delegate.getConfiguracionCompra(clave); }
        public List<Map<String, Object>> listarHabilitacionPrestadores(int id) throws Exception { return delegate.listarHabilitacionPrestadores(id); }
        public Map<String, Object> obtenerRubro(int id) throws Exception { return delegate.obtenerRubro(id); }
        public String getUsuarioActual() throws Exception { return delegate.getUsuarioActual(); }
        public List<PrestadorCotizacion> listarPrestadoresNotificados(int id) throws Exception {return delegate.listarPrestadoresNotificados(id);}
        public List<String> listarEstadosNotificaciones(int id) throws Exception {return delegate.listarEstadosNotificaciones(id);}
        public int cambiarEstadoRequerimiento(int id,int anterior,int nuevo,String usuario)throws Exception {return delegate.cambiarEstadoRequerimiento(id,anterior,nuevo,usuario);}
        public Timestamp getFechaEvento() throws Exception { return delegate.getFechaEvento(); }
        public Timestamp getAhora() throws Exception { return delegate.getAhora(); }
        public List<Map<String, Object>> listarRubrosPrestadores(Integer id) throws Exception { return delegate.listarRubrosPrestadores(id); }
        public List<Map<String, Object>> listarRubrosRequerimiento(int id) throws Exception { return delegate.listarRubrosRequerimiento(id); }
        public List<PrestadorCotizacion> listarPrestadores(List<Integer> ids, int id) throws Exception { return delegate.listarPrestadores(ids, id); }
        public List<Map<String, Object>> listarContactos(int id) throws Exception { return delegate.listarContactos(id); }
        public ReservaCotizacionPrestador obtenerNotificacion(int id, int p, boolean bloquear) throws Exception { return delegate.obtenerNotificacion(id, p, bloquear); }
        public int insertarNotificacion(int id, int p, String estado, String email, String usuario) throws Exception { return delegate.insertarNotificacion(id, p, estado, email, usuario); }
        public int actualizarReserva(int id, int p, String estado, int intento, String email, String usuario, String previo, int intentoPrevio) throws Exception { return delegate.actualizarReserva(id, p, estado, intento, email, usuario, previo, intentoPrevio); }
        public int finalizar(int id, int p, String estado, Timestamp fecha, String error, String usuario, String previo) throws Exception { return delegate.finalizar(id, p, estado, fecha, error, usuario, previo); }
        public RequerimientoCompraPedidoCotizacion obtenerPedido(int id, int p, int intento) throws Exception { return delegate.obtenerPedido(id, p, intento); }
        public int insertarPedido(RequerimientoCompraPedidoCotizacion d, String usuario) throws Exception { return delegate.insertarPedido(d, usuario); }
        public void commit() throws Exception { delegate.commit(); }
        public void rollback() throws Exception { delegate.rollback(); }
        public void cerrar() { delegate.cerrar(); }
    }


}