package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusquedaRequerimientoCompraServiceUtil {

    private static BusquedaRequerimientoCompraServiceImpl instance = null;
    private static final BusquedaRequerimientoCompraLecturaSeguraServiceImpl
            lecturaSeguraInstance =
            new BusquedaRequerimientoCompraLecturaSeguraServiceImpl();

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        return getInstance().buscarRequerimientos(filtro);
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return lecturaSeguraInstance.getRequerimientoCompra(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getItems(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {

        return getInstance().listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return getInstance().listarSectores();
    }

    public static RequerimientoCompraEstado getEstado(int idEstado)
            throws Exception {

        return getInstance().getEstado(idEstado);
    }

    public static RequerimientoCompraSector getSector(int idSector)
            throws Exception {

        return getInstance().getSector(idSector);
    }

    public static List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        return getInstance().buscarPrestadoresEnviados(
                idRequerimientoCompra,
                texto,
                limite
        );
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        List<PrestadorCotizacion> prestadores =
                getInstance().listarPrestadoresEnviados(
                        idRequerimientoCompra
                );

        if (prestadores == null || prestadores.isEmpty()) {
            return prestadores;
        }

        List<RequerimientoCompraPresupuesto> presupuestos =
                getInstance().listarPresupuestos(
                        idRequerimientoCompra
                );

        if (presupuestos == null || presupuestos.isEmpty()) {
            return prestadores;
        }

        Set<Integer> idsPrestadoresConPresupuesto =
                new HashSet<Integer>();

        for (int i = 0; i < presupuestos.size(); i++) {
            RequerimientoCompraPresupuesto presupuesto =
                    presupuestos.get(i);

            if (presupuesto != null
                    && presupuesto.isActivo()
                    && presupuesto.getIdPrestador() != null
                    && presupuesto.getIdPrestador().intValue() > 0) {

                idsPrestadoresConPresupuesto.add(
                        presupuesto.getIdPrestador()
                );
            }
        }

        for (int i = 0; i < prestadores.size(); i++) {
            PrestadorCotizacion prestador =
                    prestadores.get(i);

            if (prestador == null) {
                continue;
            }

            prestador.setPresupuestoCargado(
                    idsPrestadoresConPresupuesto.contains(
                            Integer.valueOf(
                                    prestador.getIdPrestadorPersistido()
                            )
                    )
            );
        }

        return prestadores;
    }

    public static boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        return getInstance().hayPrestadoresPendientesNotificacion(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        return getInstance().listarPresupuestos(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return getInstance().getPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}
