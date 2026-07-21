package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;

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

    public static NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return getInstance().enviarACotizar(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public static NotificacionCotizacionResultado reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return getInstance().reintentarNotificacionesCotizacion(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public static GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return getInstance().guardarAvanceCotizacion(
                idRequerimientoCompra,
                detalles,
                usuario
        );
    }

    private EditarRequerimientoCompraServiceUtil() {
    }

    public static synchronized int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario)
            throws Exception {

        validarPresupuestoUnicoPorPrestador(
                presupuesto
        );

        return getInstance()
                .registrarPresupuesto(
                        presupuesto,
                        usuario
                );
    }

    private static void validarPresupuestoUnicoPorPrestador(
            RequerimientoCompraPresupuesto presupuesto)
            throws Exception {

        if (presupuesto == null
                || presupuesto.getIdRequerimiento() == null
                || presupuesto.getIdRequerimiento().intValue() <= 0
                || presupuesto.getIdPrestador() == null
                || presupuesto.getIdPrestador().intValue() <= 0) {

            return;
        }

        int idRequerimiento =
                presupuesto.getIdRequerimiento().intValue();

        int idPrestador =
                presupuesto.getIdPrestador().intValue();

        List<RequerimientoCompraPresupuesto> existentes =
                BusquedaRequerimientoCompraServiceUtil
                        .listarPresupuestos(
                                idRequerimiento
                        );

        for (int i = 0;
                existentes != null && i < existentes.size();
                i++) {

            RequerimientoCompraPresupuesto existente =
                    existentes.get(i);

            if (existente != null
                    && existente.isActivo()
                    && existente.getIdPrestador() != null
                    && existente.getIdPrestador().intValue()
                    == idPrestador) {

                throw new Exception(
                        "El prestador ya tiene un presupuesto cargado "
                                + "para este requerimiento. Debe eliminarlo "
                                + "antes de cargar otro archivo."
                );
            }
        }
    }

    public static boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario)
            throws Exception {

        return getInstance()
                .darDeBajaPresupuesto(
                        idRequerimientoPresupuesto,
                        idRequerimientoCompra,
                        usuario
                );
    }

    public static boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra)
            throws Exception {

        return getInstance()
                .reactivarPresupuesto(
                        idRequerimientoPresupuesto,
                        idRequerimientoCompra
                );
    }
}
