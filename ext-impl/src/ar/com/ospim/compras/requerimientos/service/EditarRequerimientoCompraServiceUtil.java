package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.documentos.GestorOrdenMedicaDocumento;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;

import java.util.List;

/**
 * Fachada estática legacy.
 *
 * Las reglas funcionales se ejecutan en EditarRequerimientoCompraHelper.
 * EditarRequerimientoCompraServiceImpl queda reservado a persistencia.
 */
public class EditarRequerimientoCompraServiceUtil {

    private static EditarRequerimientoCompraServiceImpl instance = null;

    private static final EditarRequerimientoCompraHelper helper =
            new EditarRequerimientoCompraHelper();

    public static EditarRequerimientoCompraServiceImpl getInstance() {
        if (instance == null) {
            instance = new EditarRequerimientoCompraServiceImpl();
        }

        return instance;
    }

    public static int guardarRequerimientoCompra(
            RequerimientoCompra requerimiento,
            String usuario) throws Exception {

        return helper.guardarRequerimientoCompra(
                requerimiento,
                usuario
        );
    }

    public static int guardarNuevoRequerimientoCompraConOrdenMedica(
            RequerimientoCompra requerimiento,
            OrdenMedicaValidada ordenMedica,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        return helper.guardarNuevoRequerimientoCompraConOrdenMedica(
                requerimiento,
                ordenMedica,
                gestorDocumento,
                usuario
        );
    }

    public static int guardarNuevoRequerimientoCompraConOrdenesMedicas(
            RequerimientoCompra requerimiento,
            List<OrdenMedicaValidada> ordenesMedicas,
            GestorOrdenMedicaDocumento gestorDocumento,
            String usuario) throws Exception {

        return helper.guardarNuevoRequerimientoCompraConOrdenesMedicas(
                requerimiento,
                ordenesMedicas,
                gestorDocumento,
                usuario
        );
    }

    public static int guardarDetalle(
            RequerimientoCompraDetalle detalle,
            String usuario) throws Exception {

        return helper.guardarDetalle(
                detalle,
                usuario
        );
    }

    public static void borrarDetalle(
            int idDetalle,
            String usuario) throws Exception {

        helper.borrarDetalle(
                idDetalle,
                usuario
        );
    }

    public static void borrarItem(
            int idItem,
            String usuario) throws Exception {

        helper.borrarDetalle(
                idItem,
                usuario
        );
    }

    public static void borrarRequerimientoCompra(
            int idRequerimientoCompra,
            String usuario) throws Exception {

        helper.borrarRequerimientoCompra(
                idRequerimientoCompra,
                usuario
        );
    }

    public static void cambiarEstado(
            int idRequerimientoCompra,
            int idEstadoNuevo,
            String usuario) throws Exception {

        helper.cambiarEstado(
                idRequerimientoCompra,
                idEstadoNuevo,
                usuario
        );
    }

    public static NotificacionCotizacionResultado enviarACotizar(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return helper.enviarACotizar(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public static NotificacionCotizacionResultado
    reintentarNotificacionesCotizacion(
            int idRequerimientoCompra,
            String usuario,
            long companyId) throws Exception {

        return helper.reintentarNotificacionesCotizacion(
                idRequerimientoCompra,
                usuario,
                companyId
        );
    }

    public static GuardadoCotizacionResultado guardarAvanceCotizacion(
            int idRequerimientoCompra,
            List<RequerimientoCompraDetalle> detalles,
            String usuario) throws Exception {

        return helper.guardarAvanceCotizacion(
                idRequerimientoCompra,
                detalles,
                usuario
        );
    }

    public static int registrarPresupuesto(
            RequerimientoCompraPresupuesto presupuesto,
            String usuario) throws Exception {

        return helper.registrarPresupuesto(
                presupuesto,
                usuario
        );
    }

    public static boolean darDeBajaPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        return helper.darDeBajaPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra,
                usuario
        );
    }

    public static boolean reactivarPresupuesto(
            int idRequerimientoPresupuesto,
            int idRequerimientoCompra) throws Exception {

        return helper.reactivarPresupuesto(
                idRequerimientoPresupuesto,
                idRequerimientoCompra
        );
    }

    private EditarRequerimientoCompraServiceUtil() {
    }
}
