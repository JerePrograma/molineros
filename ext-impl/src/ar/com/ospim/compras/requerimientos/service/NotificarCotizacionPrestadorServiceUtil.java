package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.compras.requerimientos.beans.CotizacionPrestadorDiagnostico;
import ar.com.ospim.compras.requerimientos.beans.FinalizacionCotizacionPrestador;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPedidoCotizacion;
import ar.com.ospim.compras.requerimientos.beans.ReservaCotizacionPrestador;

import java.util.List;

/**
 * Fachada estática legacy para el servicio de notificación
 * de cotizaciones a prestadores.
 */
public class NotificarCotizacionPrestadorServiceUtil {

    private static NotificarCotizacionPrestadorServiceImpl instance = null;

    public static NotificarCotizacionPrestadorServiceImpl getInstance() {

        if (instance == null) {
            instance =
                    new NotificarCotizacionPrestadorServiceImpl();
        }

        return instance;
    }

    public static List<PrestadorCotizacion> listarPrestadoresCandidatos(
            int idRequerimientoCompra) throws Exception {

        return getInstance()
                .listarPrestadoresCandidatos(
                        idRequerimientoCompra
                );
    }

    public static List<PrestadorCotizacion>
    listarPrestadoresConfiguracionCorreosPorRubro(
            int idTipoPrestacion) throws Exception {

        return getInstance()
                .listarPrestadoresConfiguracionCorreosPorRubro(
                        idTipoPrestacion
                );
    }

    public static CotizacionPrestadorDiagnostico diagnosticarPrestadores(
            int idRequerimientoCompra) throws Exception {

        return getInstance()
                .diagnosticarPrestadores(
                        idRequerimientoCompra
                );
    }

    public static ReservaCotizacionPrestador reservarCotizacionPrestador(
            int idRequerimientoCompra,
            int idPrestador,
            String usuario) throws Exception {

        return getInstance()
                .reservarCotizacionPrestador(
                        idRequerimientoCompra,
                        idPrestador,
                        usuario
                );
    }

    public static FinalizacionCotizacionPrestador
    finalizarCotizacionPrestador(
            int idRequerimiento,
            int idPrestador,
            String estado,
            String error,
            String usuario) throws Exception {

        return getInstance()
                .finalizarCotizacionPrestador(
                        idRequerimiento,
                        idPrestador,
                        estado,
                        error,
                        usuario
                );
    }

    public static int registrarPedidoCotizacionDocumento(
            RequerimientoCompraPedidoCotizacion documento,
            String usuario) throws Exception {

        return getInstance()
                .registrarPedidoCotizacionDocumento(
                        documento,
                        usuario
                );
    }

    public static boolean reservarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        return getInstance()
                .reservarCopiaCotizacion(
                        idRequerimiento
                );
    }

    public static void liberarCopiaCotizacion(
            int idRequerimiento) throws Exception {

        getInstance()
                .liberarCopiaCotizacion(
                        idRequerimiento
                );
    }
}