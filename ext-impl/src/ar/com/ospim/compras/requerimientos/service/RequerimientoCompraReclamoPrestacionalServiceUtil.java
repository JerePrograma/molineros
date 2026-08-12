package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import com.liferay.portal.model.User;

import java.util.List;
import java.util.Map;

public class RequerimientoCompraReclamoPrestacionalServiceUtil {

    private static RequerimientoCompraReclamoPrestacionalServiceImpl instance;

    public static RequerimientoCompraReclamoPrestacionalServiceImpl
            getInstance() {

        if (instance == null) {
            instance =
                    new RequerimientoCompraReclamoPrestacionalServiceImpl();
        }

        return instance;
    }

    public static RequerimientoCompraReclamoPrestacional
            obtenerPorRequerimiento(int idRequerimientoCompra)
            throws Exception {

        return getInstance().obtenerPorRequerimiento(
                idRequerimientoCompra
        );
    }

    public static RequerimientoCompraReclamoPrestacional
            getRelacionPorReclamoPrestacional(
                    int idReclamoPrestacional) throws Exception {

        return getInstance().getRelacionPorReclamoPrestacional(
                idReclamoPrestacional
        );
    }

    public static Map<Integer, RequerimientoCompraReclamoPrestacional>
            obtenerVinculadasPorRequerimientos(
                    List<Integer> idsRequerimientos)
                    throws Exception {

        return getInstance()
                .obtenerVinculadasPorRequerimientos(
                        idsRequerimientos
                );
    }

    public static void reservarCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        getInstance().reservarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                usuario
        );
    }

    public static boolean liberarReserva(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        return getInstance().liberarReserva(
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

        return getInstance().marcarErrorPosteriorAlInsert(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                error,
                usuario
        );
    }

    private RequerimientoCompraReclamoPrestacionalServiceUtil() {
    }

    public static int crearYVincular(
            int idRequerimientoCompra,
            String tokenReserva,
            ReclamoPrestacional reclamo,
            User user) throws Exception {

        return getInstance().crearYVincular(
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

        getInstance().finalizarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                usuario
        );
    }
}
