package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;

import java.util.List;

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

        return getInstance().buscarRequerimientos(
                filtro
        );
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return lecturaSeguraInstance.getRequerimientoCompra(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraDetalle> getItems(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getDetalles(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {

        return getInstance().listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return getInstance().listarSectores();
    }

    public static RequerimientoCompraEstado getEstado(
            int idEstado) throws Exception {

        return getInstance().getEstado(
                idEstado
        );
    }

    public static RequerimientoCompraSector getSector(
            int idSector) throws Exception {

        return getInstance().getSector(
                idSector
        );
    }

    public static boolean tieneSituacionMedicaVigente(
            String cuilTitular,
            int inte) throws Exception {

        return getInstance()
                .tieneSituacionMedicaVigente(
                        cuilTitular,
                        inte
                );
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

        return getInstance().listarPrestadoresEnviados(
                idRequerimientoCompra
        );
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

    /*
     * Nuevo contrato plural.
     */
    public static List<RequerimientoCompraPresupuesto>
    listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        return getInstance().listarOrdenesMedicas(
                idRequerimientoCompra
        );
    }

    /*
     * Contrato histórico.
     *
     * Debe mantenerse. Su implementación continuará devolviendo una
     * Orden médica para callers legacy que todavía trabajen en modo
     * singular.
     */
    public static RequerimientoCompraPresupuesto getOrdenMedica(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getOrdenMedica(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraPresupuesto getPresupuestoAdjudicado(
            int idRequerimientoCompra) throws Exception {

        return getInstance().getPresupuestoAdjudicado(
                idRequerimientoCompra
        );
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }
}