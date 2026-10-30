package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.math.BigDecimal;

public class RequerimientoCompraOrdenServiceUtil {

    private static final RequerimientoCompraOrdenServiceImpl service =
            new RequerimientoCompraOrdenServiceImpl();

    public static void completarDatosConsulta(RequerimientoCompraOrden orden,
            RequerimientoCompra requerimiento) throws Exception {
        service.completarDatosConsulta(orden, requerimiento);
    }

    public static RequerimientoCompraOrden obtenerPorRequerimiento(int idRequerimiento)
            throws Exception {
        return service.obtenerPorRequerimiento(idRequerimiento);
    }

    public static RequerimientoCompraOrden crear(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return new RequerimientoCompraOrdenHelper().crear(orden, usuario);
    }

    public static RequerimientoCompraOrden actualizar(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return new RequerimientoCompraOrdenHelper().actualizar(orden, usuario);
    }
    public static Transaccion abrirTransaccion() throws Exception {return new Transaccion(service.abrirTransaccion());}
    public static class Transaccion {
        private final RequerimientoCompraOrdenServiceImpl.Transaccion tx;
        private Transaccion(RequerimientoCompraOrdenServiceImpl.Transaccion tx){this.tx=tx;}
        public RequerimientoCompra bloquearYObtenerRequerimiento(int id)throws Exception{return tx.bloquearYObtenerRequerimiento(id);}
        public String getConfiguracionCompra(String clave)throws Exception{return tx.getConfiguracionCompra(clave);}
        public Map<String,Object> obtenerSector(int id)throws Exception{return tx.obtenerSector(id);}
        public Map<String,Object> obtenerRelacionRp(int id)throws Exception{return tx.obtenerRelacionRp(id);}
        public RequerimientoCompraOrden obtenerOrden(int id)throws Exception{return tx.obtenerOrden(id);}
        public List<RequerimientoCompraPresupuesto> listarDocumentos(int id,int tipo)throws Exception{return tx.listarDocumentos(id,tipo);}
        public List<RequerimientoCompraPresupuesto> listarOrdenesMedicas(int id)throws Exception{return tx.listarOrdenesMedicas(id);}
        public List<RequerimientoCompraComparativa> listarComparativas(int id,int prestador)throws Exception{return tx.listarComparativas(id,prestador);}
        public List<ar.com.ospim.global.beans.Empresa> buscarEmpresa(String cuit,String sucursal)throws Exception{return tx.buscarEmpresa(cuit,sucursal);}
        public int obtenerRequerimientoOrigenOrden(int id)throws Exception{return tx.obtenerRequerimientoOrigenOrden(id);}
        public int obtenerPresupuestoOrigenOrden(int id)throws Exception{return tx.obtenerPresupuestoOrigenOrden(id);}
        public Date getFechaActual()throws Exception{return tx.getFechaActual();}
        public void insertar(RequerimientoCompraOrden orden,int presupuesto,String usuario)throws Exception{tx.insertar(orden,presupuesto,usuario);}
        public int actualizar(RequerimientoCompraOrden orden,String usuario)throws Exception{return tx.actualizar(orden,usuario);}
        public int completarPrecio(int orden,int detalle,BigDecimal precio)throws Exception{return tx.completarPrecio(orden,detalle,precio);}
        public int confirmarEstado(int id,int esperado,int nuevo,String usuario)throws Exception{return tx.confirmarEstado(id,esperado,nuevo,usuario);}
        public void commit()throws Exception{tx.commit();}
        public void rollback()throws Exception{tx.rollback();}
        public void cerrar(){tx.cerrar();}
    }
}
