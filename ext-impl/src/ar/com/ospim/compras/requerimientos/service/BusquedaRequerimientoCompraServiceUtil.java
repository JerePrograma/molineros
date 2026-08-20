package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.BusquedaRequerimientoCompraHelper;

import java.util.List;

/**
 * Fachada estática legacy.
 *
 * No contiene reglas de negocio: conserva las firmas históricas y delega
 * las validaciones/composición al Helper.
 */
public class BusquedaRequerimientoCompraServiceUtil {

    private static BusquedaRequerimientoCompraServiceImpl instance = null;

    private static final BusquedaRequerimientoCompraHelper helper =
            new BusquedaRequerimientoCompraHelper();

    public static BusquedaRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new BusquedaRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static List<RequerimientoCompra> buscarRequerimientos(
            RequerimientoCompraFiltro filtro) throws Exception {

        return helper.buscarRequerimientos(filtro);
    }

    public static RequerimientoCompra getRequerimientoCompra(
            int idRequerimientoCompra) throws Exception {

        return helper.getRequerimientoCompra(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getDetalles(
            int idRequerimientoCompra) throws Exception {

        return helper.getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle> getItems(
            int idRequerimientoCompra) throws Exception {

        return helper.getDetalles(idRequerimientoCompra);
    }

    public static List<RequerimientoCompraDetalle>
    buscarItemsHistoricosAfiliado(
            String cuilTitular,
            int inte,
            int idSector,
            int idRequerimientoExcluir)
            throws Exception {

        return helper.buscarItemsHistoricosAfiliado(
                cuilTitular,
                inte,
                idSector,
                idRequerimientoExcluir
        );
    }

    public static List<RequerimientoCompraEstado> listarEstados()
            throws Exception {

        return helper.listarEstados();
    }

    public static List<RequerimientoCompraSector> listarSectores()
            throws Exception {

        return helper.listarSectores();
    }

    public static RequerimientoCompraEstado getEstado(
            int idRequerimientoCompra) throws Exception {

        return helper.getEstado(idRequerimientoCompra);
    }

    public static RequerimientoCompraSector getSector(
            int idSector) throws Exception {

        return helper.getSector(idSector);
    }

    public static boolean tieneSituacionMedicaVigente(
            String cuilTitular,
            int inte) throws Exception {

        return helper.tieneSituacionMedicaVigente(
                cuilTitular,
                inte
        );
    }

    public static boolean existeRequerimientoDuplicado(
            String cuilTitular,
            int inte,
            int idPrestacion,
            java.util.Date fechaOrdenMedica,
            int idRequerimientoExcluir)
            throws Exception {

        return helper
                .existeRequerimientoDuplicado(
                        cuilTitular,
                        inte,
                        idPrestacion,
                        fechaOrdenMedica,
                        idRequerimientoExcluir
                );
    }

    public static List<PrestadorCotizacion> buscarPrestadoresEnviados(
            int idRequerimientoCompra,
            String texto,
            int limite) throws Exception {

        return helper.buscarPrestadoresEnviados(
                idRequerimientoCompra,
                texto,
                limite
        );
    }

    public static List<PrestadorCotizacion> listarPrestadoresEnviados(
            int idRequerimientoCompra) throws Exception {

        return helper.listarPrestadoresEnviados(
                idRequerimientoCompra
        );
    }

    public static boolean hayPrestadoresPendientesNotificacion(
            int idRequerimientoCompra) throws Exception {

        return helper.hayPrestadoresPendientesNotificacion(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto> listarPresupuestos(
            int idRequerimientoCompra) throws Exception {

        return helper.listarPresupuestos(idRequerimientoCompra);
    }

    public static RequerimientoCompraPresupuesto getPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return helper.getPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraPresupuesto>
    listarOrdenesMedicas(
            int idRequerimientoCompra) throws Exception {

        return helper.listarOrdenesMedicas(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraPresupuesto getOrdenMedica(
            int idRequerimientoCompra) throws Exception {

        return helper.getOrdenMedica(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraPresupuesto getPresupuestoAdjudicado(
            int idRequerimientoCompra) throws Exception {

        return helper.getPresupuestoAdjudicado(
                idRequerimientoCompra
        );
    }

    private BusquedaRequerimientoCompraServiceUtil() {
    }


}
