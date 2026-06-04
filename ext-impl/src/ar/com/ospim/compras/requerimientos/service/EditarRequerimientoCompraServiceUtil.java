package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;

import java.util.List;

public class EditarRequerimientoCompraServiceUtil {

    private static EditarRequerimientoCompraServiceImpl instance = null;

    public static EditarRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new EditarRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static int guardarRequerimientoCompra(RequerimientoCompra requerimiento, String usuario) throws Exception {
        return getInstance().guardarRequerimientoCompra(requerimiento, usuario);
    }

    public static int guardarDetalle(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return getInstance().guardarDetalle(detalle, usuario);
    }

    public static int guardarItem(RequerimientoCompraDetalle detalle, String usuario) throws Exception {
        return guardarDetalle(detalle, usuario);
    }

    public static void borrarDetalle(int idDetalle, String usuario) throws Exception {
        getInstance().borrarDetalle(idDetalle, usuario);
    }

    public static void borrarItem(int idItem, String usuario) throws Exception {
        borrarDetalle(idItem, usuario);
    }

    public static void borrarRequerimientoCompra(int idRequerimientoCompra, String usuario) throws Exception {
        getInstance().borrarRequerimientoCompra(idRequerimientoCompra, usuario);
    }

    public static void cambiarEstado(int idRequerimientoCompra, int idEstadoNuevo, String usuario) throws Exception {
        getInstance().cambiarEstado(idRequerimientoCompra, idEstadoNuevo, usuario);
    }

    public static List<CompraArticulo> listarArticulos(Integer idSector, String texto) throws Exception {
        return getInstance().listarArticulos(idSector, texto);
    }

    public static List<CompraArticulo> listarArticulosPorSector(int idSector) throws Exception {
        return getInstance().listarArticulosPorSector(idSector);
    }

    public static CompraArticulo getArticulo(int idArticulo) throws Exception {
        return getInstance().getArticulo(idArticulo);
    }

    public static int guardarArticulo(CompraArticulo articulo) throws Exception {
        return getInstance().guardarArticulo(articulo);
    }

    public static int guardarArticulo(Integer idArticulo, Integer idSector, String descripcion) throws Exception {
        return getInstance().guardarArticulo(idArticulo, idSector, descripcion);
    }

    public static void borrarArticulo(int idArticulo) throws Exception {
        getInstance().borrarArticulo(idArticulo);
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}