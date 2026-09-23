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

    /**
     * Entradas de persistencia para el Helper. Los metodos de compatibilidad
     * anteriores conservan sus validaciones y orquestacion en ese Helper.
     * Estos nombres evitan volver a entrar en el mismo flujo de validacion.
     */
    public static RequerimientoCompraReclamoPrestacional
    consultarPorRequerimiento(
            int idRequerimientoCompra) throws Exception {

        return getInstance().obtenerPorRequerimiento(
                idRequerimientoCompra
        );
    }

    public static List<RequerimientoCompraReclamoPrestacional>
    listarPorReclamoPrestacional(
            int idReclamoPrestacional,
            String estado) throws Exception {

        return getInstance().listarPorReclamoPrestacional(
                idReclamoPrestacional,
                estado
        );
    }

    public static List<RequerimientoCompraReclamoPrestacional>
    listarVinculadasPorRequerimientos(
            String estado,
            List<Integer> idsRequerimientos) throws Exception {

        return getInstance().listarVinculadasPorRequerimientos(
                estado,
                idsRequerimientos
        );
    }

    public static boolean ejecutarLiberacionReserva(
            int idRequerimientoCompra,
            String tokenReserva,
            String usuario) throws Exception {

        return getInstance().liberarReserva(
                idRequerimientoCompra,
                tokenReserva,
                usuario
        );
    }

    public static boolean registrarErrorPosteriorAlInsert(
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

    public static boolean ejecutarFinalizacionCreacion(
            int idRequerimientoCompra,
            String tokenReserva,
            int idReclamoPrestacional,
            String usuario) throws Exception {

        return getInstance().finalizarCreacion(
                idRequerimientoCompra,
                tokenReserva,
                idReclamoPrestacional,
                usuario
        );
    }

    public static RequerimientoCompraReclamoPrestacionalTransaccion
    abrirTransaccion() throws Exception {

        return RequerimientoCompraReclamoPrestacionalTransaccion.abrir(
                getInstance()
        );
    }

    private RequerimientoCompraReclamoPrestacionalServiceUtil() {
    }
}
