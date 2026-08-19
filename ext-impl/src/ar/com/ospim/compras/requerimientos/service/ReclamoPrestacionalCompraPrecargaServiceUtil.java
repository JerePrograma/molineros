package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper;

import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * Fachada de compatibilidad.
 *
 * La antigua clase era un ServiceUtil que no realizaba persistencia.
 * Toda su lógica fue trasladada a ReclamoPrestacionalCompraPrecargaHelper.
 */
public final class ReclamoPrestacionalCompraPrecargaServiceUtil {

    private ReclamoPrestacionalCompraPrecargaServiceUtil() {
    }

    public static Precarga precargar(
            HttpSession session,
            String nonceRequest,
            String usuarioActual) throws Exception {

        return new Precarga(
                ReclamoPrestacionalCompraPrecargaHelper.precargar(
                        session,
                        nonceRequest,
                        usuarioActual
                )
        );
    }

    public static void limpiarHandoffFallido(
            HttpSession session,
            String nonce,
            Precarga precarga) {

        ReclamoPrestacionalCompraPrecargaHelper.limpiarHandoffFallido(
                session,
                nonce,
                precarga != null
                        ? precarga.delegate
                        : null
        );
    }

    public static ReclamoPrestacional crearReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        return ReclamoPrestacionalCompraPrecargaHelper.crearReclamo(
                requerimiento
        );
    }

    public static List<PrestacionesReclamo> crearPrestaciones(
            RequerimientoCompra requerimiento) throws Exception {

        return ReclamoPrestacionalCompraPrecargaHelper.crearPrestaciones(
                requerimiento
        );
    }

    public static String mapearSector(
            String sectorCompras) {

        return ReclamoPrestacionalCompraPrecargaHelper.mapearSector(
                sectorCompras
        );
    }

    public static int resolverRecuperable(
            RequerimientoCompra requerimiento) {

        return ReclamoPrestacionalCompraPrecargaHelper.resolverRecuperable(
                requerimiento
        );
    }

    public static final class Precarga {

        private final ReclamoPrestacionalCompraPrecargaHelper.Precarga
                delegate;

        private Precarga(
                ReclamoPrestacionalCompraPrecargaHelper.Precarga
                        delegate) {

            this.delegate = delegate;
        }
    }
}
