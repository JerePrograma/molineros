package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import java.util.Map;

/** Delegacion legacy como RequerimientoCompraOrdenServiceUtil.Transaccion. */
public class RequerimientoCompraOrdenSeguimientoServiceUtil {
    private static final RequerimientoCompraOrdenSeguimientoServiceImpl service =
            new RequerimientoCompraOrdenSeguimientoServiceImpl();
    public static RequerimientoCompraOrdenSeguimiento obtener(int idOrden) throws Exception {
        return service.obtener(idOrden);
    }
    public static Transaccion abrirTransaccion() throws Exception {
        return new Transaccion(service.abrirTransaccion());
    }
    public static class Transaccion {
        private final RequerimientoCompraOrdenSeguimientoServiceImpl.Transaccion tx;
        private Transaccion(RequerimientoCompraOrdenSeguimientoServiceImpl.Transaccion tx) { this.tx = tx; }
        public RequerimientoCompra bloquearYObtenerRequerimiento(int id) throws Exception { return tx.bloquearYObtenerRequerimiento(id); }
        public Map<String, Object> obtenerSector(int id) throws Exception { return tx.obtenerSector(id); }
        public RequerimientoCompraOrden obtenerOrden(int id) throws Exception { return tx.obtenerOrden(id); }
        public String getConfiguracionCompra(String clave) throws Exception { return tx.getConfiguracionCompra(clave); }
        public RequerimientoCompraOrdenSeguimiento obtener(int id) throws Exception { return tx.obtener(id); }
        public int insertar(RequerimientoCompraOrdenSeguimiento datos, String usuario) throws Exception { return tx.insertar(datos, usuario); }
        public int actualizar(RequerimientoCompraOrdenSeguimiento datos, String usuario) throws Exception { return tx.actualizar(datos, usuario); }
        public void commit() throws Exception { tx.commit(); }
        public void rollback() throws Exception { tx.rollback(); }
        public void cerrar() { tx.cerrar(); }
    }
}
