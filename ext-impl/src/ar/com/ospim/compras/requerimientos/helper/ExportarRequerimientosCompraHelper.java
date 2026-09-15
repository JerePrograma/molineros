package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraFiltro;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/** Criterios de una respuesta exitosa, sin conservar filas ni workbooks. */
public final class ExportarRequerimientosCompraHelper {

    public static final String REPORTE = "COMPRAS_REQUERIMIENTOS";
    public static final String TOKEN = "compras_exportacion_token";
    private static final String CONTEXTOS = "COMPRAS_EXPORTACIONES";
    private static final int MAX_CONTEXTOS = 20;

    public static boolean puedeConsultar(User user) throws Exception {
        return user != null
                && (PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_VIEW_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)
                || PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS));
    }

    public static void validarPermiso(User user) throws Exception {
        if (!puedeConsultar(user)) {
            throw new SecurityException(
                    "No posee permisos para consultar requerimientos de compras.");
        }
    }

    public static String publicar(HttpSession session, long userId,
            RequerimientoCompraFiltro filtro, boolean incluirRp,
            boolean mostrarRp, Locale locale) {
        String token = UUID.randomUUID().toString();
        // Liferay crea wrappers distintos para la misma sesion en peticiones concurrentes.
        synchronized (ExportarRequerimientosCompraHelper.class) {
            Contextos contextos = (Contextos) session.getAttribute(CONTEXTOS);
            if (contextos == null) {
                contextos = new Contextos();
            }
            if (contextos.valores.size() >= MAX_CONTEXTOS) {
                Iterator<String> it = contextos.valores.keySet().iterator();
                it.next();
                it.remove();
            }
            contextos.valores.put(token,
                    new Criterios(userId, filtro, incluirRp, mostrarRp, locale));
            session.setAttribute(CONTEXTOS, contextos);
        }
        return token;
    }

    public static Criterios obtener(HttpSession session, long userId, String token) {
        if (session != null && token != null) {
            synchronized (ExportarRequerimientosCompraHelper.class) {
                Contextos contextos = (Contextos) session.getAttribute(CONTEXTOS);
                Criterios criterios = contextos == null
                        ? null : contextos.valores.get(token);
                if (criterios != null && criterios.userId == userId) {
                    return criterios;
                }
            }
        }
        throw new IllegalArgumentException(
                "La busqueda ya no esta disponible. Vuelva a buscar antes de exportar.");
    }

    public static List<Integer> obtenerIds(List<RequerimientoCompra> requerimientos) {
        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < requerimientos.size(); i++) {
            ids.add(Integer.valueOf(requerimientos.get(i).getIdRequerimientoCompra()));
        }
        return ids;
    }

    public static String obtenerPrestador(List<PrestadorCotizacion> prestadores) {
        if (prestadores == null || prestadores.isEmpty()) {
            return "";
        }
        // El contrato de guardar_cotizacion exige un prestador para todo el detalle.
        if (prestadores.size() != 1) {
            throw new IllegalArgumentException(
                    "Todos los detalles activos deben tener el mismo prestador adjudicado valido.");
        }
        PrestadorCotizacion prestador = prestadores.get(0);
        if (prestador.getIdPrestador() == 0) {
            return "";
        }
        if (prestador.getIdPrestador() < 0
                || WebKeysCompras.isEmpty(prestador.getDescripcion())) {
            throw new IllegalArgumentException(
                    "No se pudo identificar la denominacion del prestador adjudicado.");
        }
        return prestador.getDescripcion();
    }

    /** Las mismas celdas de datos que renderiza la grilla, antes de escapar HTML. */
    public static String[] valoresListado(RequerimientoCompra req,
            RequerimientoCompraReclamoPrestacional relacionRp, boolean mostrarRp) {
        String nombre = req.getAfiliadoNombreApellidoVisible();
        if (WebKeysCompras.isEmpty(nombre)) {
            nombre = req.getAfiliadoCuilTitularVisible();
            if (!WebKeysCompras.isEmpty(req.getAfiliadoIntString())) {
                nombre += " / " + req.getAfiliadoIntString();
            }
        }
        String documento = req.getAfiliadoDocumentoNroVisible();
        if (WebKeysCompras.isEmpty(documento)) {
            documento = req.getAfiliadoDocumentoVisible();
        }
        documento = documento == null ? "" : documento.trim().replaceAll("[^0-9]", "");
        String[] valores = new String[mostrarRp ? 11 : 10];
        valores[0] = req.getIdString();
        valores[1] = req.getEstadoDescripcionVisible();
        valores[2] = req.getSectorDescripcionVisible();
        valores[3] = nombre;
        valores[4] = documento;
        valores[5] = req.getIdTercerizadora() != null ? req.getIdTercerizadora() : "";
        valores[6] = req.getCargoOspimString() + "%";
        valores[7] = req.getCargoTercerizadoraString() + "%";
        valores[8] = req.getSurgeDescripcion();
        valores[9] = req.getAltaFechaAsString();
        if (mostrarRp) {
            valores[10] = relacionRp != null && relacionRp.isVinculado()
                    && relacionRp.getIdReclamoPrestacionalInt() > 0
                    ? String.valueOf(relacionRp.getIdReclamoPrestacionalInt()) : "";
        }
        return valores;
    }

    public static String[] titulosListado(boolean mostrarRp) {
        return mostrarRp
                ? new String[] {"Id", "estado", "sector", "afiliado-nombre",
                    "afiliado-dni", "tercerizadora", "cargo-ospim",
                    "cargo-tercerizadora", "SURGE", "alta-fecha", "Id RP"}
                : new String[] {"Id", "estado", "sector", "afiliado-nombre",
                    "afiliado-dni", "tercerizadora", "cargo-ospim",
                    "cargo-tercerizadora", "SURGE", "alta-fecha"};
    }

    public static final class Criterios implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long userId;
        private final RequerimientoCompraFiltro filtro;
        public final boolean incluirRp;
        public final boolean mostrarRp;
        public final Locale locale;

        private Criterios(long userId, RequerimientoCompraFiltro filtro,
                boolean incluirRp, boolean mostrarRp, Locale locale) {
            this.userId = userId;
            this.filtro = copiar(filtro);
            this.incluirRp = incluirRp;
            this.mostrarRp = mostrarRp;
            this.locale = locale;
        }

        public RequerimientoCompraFiltro getFiltro() {
            // buscarRequerimientosListado modifica temporalmente el estado en Cotizados.
            return copiar(filtro);
        }
    }

    private static RequerimientoCompraFiltro copiar(RequerimientoCompraFiltro origen) {
        RequerimientoCompraFiltro destino = new RequerimientoCompraFiltro();
        destino.setIdEstado(origen.getIdEstado());
        destino.setIdSector(origen.getIdSector());
        destino.setAfiliadoCuilTitular(origen.getAfiliadoCuilTitular());
        destino.setAfiliadoInt(origen.getAfiliadoInt());
        destino.setIdTercerizadora(origen.getIdTercerizadora());
        destino.setRecupero(origen.getRecupero());
        destino.setSurge(origen.getSurge());
        destino.setTexto(origen.getTexto());
        destino.setFechaAltaDesde(origen.getFechaAltaDesde() == null
                ? null : new Date(origen.getFechaAltaDesde().getTime()));
        destino.setFechaAltaHasta(origen.getFechaAltaHasta() == null
                ? null : new Date(origen.getFechaAltaHasta().getTime()));
        return destino;
    }

    private static final class Contextos implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Map<String, Criterios> valores =
                new LinkedHashMap<String, Criterios>();
    }

    private ExportarRequerimientosCompraHelper() {
    }
}
