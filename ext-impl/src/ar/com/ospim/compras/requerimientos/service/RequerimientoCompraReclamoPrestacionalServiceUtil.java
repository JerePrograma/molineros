package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;

import com.liferay.portal.model.User;

import java.util.List;
import java.util.Map;

/**
 * Fachada estática legacy. La lógica está en el Helper.
 */
public class RequerimientoCompraReclamoPrestacionalServiceUtil {

    private static RequerimientoCompraReclamoPrestacionalServiceImpl instance;

    private static final RequerimientoCompraReclamoPrestacionalHelper helper =
            new RequerimientoCompraReclamoPrestacionalHelper();

    public static RequerimientoCompraReclamoPrestacionalServiceImpl
    getInstance() {

        if (instance == null) {
            instance =
                    new RequerimientoCompraReclamoPrestacionalServiceImpl();
        }

        return instance;
    }

    public static RequerimientoCompraReclamoPrestacional
    obtenerPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        return helper.obtenerPorRequerimiento(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraReclamoPrestacional
    getRelacionPorReclamoPrestacional(
            int idReclamoPrestacional) throws Exception {

        return helper.getRelacionPorReclamoPrestacional(
                idReclamoPrestacional
        );
    }

    public static Map<Integer, RequerimientoCompraReclamoPrestacional>
    obtenerVinculadasPorRequerimientos(
            List<Integer> idsRequerimientos) throws Exception {

        return helper.obtenerVinculadasPorRequerimientos(
                idsRequerimientos
        );
    }

    public static void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        helper.reservarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                usuario
        );
    }

    public static boolean liberarReserva(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        return helper.liberarReserva(
                idRequerimientoCompra,
                tokenReserva,
                usuario
        );
    }

    public static boolean marcarErrorPosteriorAlInsert(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String error,
            String usuario) throws Exception {

        return helper.marcarErrorPosteriorAlInsert(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                error,
                usuario
        );
    }

    public static int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        return helper.crearYVincular(
                idRequerimientoCompra,
                tokenReserva,
                reclamo,
                user
        );
    }

    public static void finalizarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        helper.finalizarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                usuario
        );
    }

    private RequerimientoCompraReclamoPrestacionalServiceUtil() {
    }
}
