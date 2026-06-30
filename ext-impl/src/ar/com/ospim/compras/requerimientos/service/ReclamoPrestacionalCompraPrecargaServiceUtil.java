package ar.com.ospim.compras.requerimientos.service;

import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.crm.beans.ContactoCRM;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

/** Precarga un Reclamo Prestacional desde un requerimiento COTIZADO. */
public final class ReclamoPrestacionalCompraPrecargaServiceUtil {

    private static final int RECUPERABLE_SUR = 1;
    private static final int NO_RECUPERABLE = 2;
    private static final int RECUPERABLE_INTEGRACION = 3;
    private static final int MAX_OBSERVACION = 250;

    private ReclamoPrestacionalCompraPrecargaServiceUtil() {
    }

    public static void precargar(
            HttpSession session,
            String nonceRequest,
            String usuarioActual) throws Exception {

        if (session == null || WebKeysCompras.isEmpty(nonceRequest)) {
            throw new Exception("No se pudo validar el contexto de creación del Reclamo Prestacional.");
        }

        Object value = session.getAttribute(
                WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
        );
        if (!(value instanceof ReclamoPrestacionalCompraContexto)) {
            throw new Exception("El contexto de Compras expiró o ya no está disponible.");
        }

        ReclamoPrestacionalCompraContexto contexto =
                (ReclamoPrestacionalCompraContexto) value;
        validarContexto(contexto, nonceRequest, usuarioActual);
        validarRelacionPersistida(contexto.getIdRequerimientoCompra());

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        contexto.getIdRequerimientoCompra()
                );
        validarRequerimiento(requerimiento, contexto);

        ReclamoPrestacional reclamo = crearReclamo(requerimiento);
        List<PrestacionesReclamo> prestaciones = crearPrestaciones(requerimiento);
        if (prestaciones.isEmpty()) {
            throw new Exception("El requerimiento COTIZADO no contiene ítems para precargar.");
        }

        List<RevisionesReclamo> revisiones = new ArrayList<RevisionesReclamo>();
        List<ContactoCRM> contactos = new ArrayList<ContactoCRM>();
        reclamo.setPrestaciones(prestaciones);
        reclamo.setRevisiones(revisiones);
        reclamo.setContactosCRM(contactos);

        synchronized (session) {
            Object actual = session.getAttribute(
                    WebKeysCompras.CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
            );
            if (!(actual instanceof ReclamoPrestacionalCompraContexto)) {
                throw new Exception(
                        "El contexto de Compras cambió durante la precarga. "
                                + "Vuelva al requerimiento e inicie nuevamente el Reclamo Prestacional."
                );
            }
            validarContexto(
                    (ReclamoPrestacionalCompraContexto) actual,
                    nonceRequest,
                    usuarioActual
            );
            if (session.getAttribute(
                    WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION
            ) != null) {
                throw new Exception(
                        "Ya existe un Reclamo Prestacional en edición en esta sesión. "
                                + "Finalice o descarte esa edición antes de iniciar otro desde Compras."
                );
            }

            session.setAttribute(
                    WebKeysAutorizaciones.RECLAMO_PRESTACION_EN_EDICION,
                    reclamo
            );
            session.setAttribute(
                    WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION,
                    prestaciones
            );
            session.setAttribute(
                    WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION,
                    revisiones
            );
            session.setAttribute(
                    WebKeysAutorizaciones.LISTADO_CONTACTOS_RECLAMOS_EN_SESION,
                    contactos
            );
        }
    }

    public static ReclamoPrestacional crearReclamo(
            RequerimientoCompra requerimiento) throws Exception {

        if (requerimiento == null) {
            throw new Exception("No se pudo obtener el requerimiento de compra.");
        }

        String sector = mapearSector(requerimiento.getSectorDescripcion());
        if (WebKeysCompras.isEmpty(sector)) {
            throw new Exception(
                    "El sector de Compras '"
                            + requerimiento.getSectorDescripcionVisible()
                            + "' no tiene un mapeo válido a Reclamos Prestacionales."
            );
        }

        Date fecha = new Date();
        ReclamoPrestacional reclamo = new ReclamoPrestacional(
                requerimiento.getAfiliadoCuilTitular(),
                requerimiento.getAfiliadoInt().intValue(),
                fecha,
                sector,
                null
        );
        reclamo.setAlta_fecha(fecha);
        reclamo.setOspim_fecha(fecha);
        reclamo.setTipoPedido("EXCEPCION");
        reclamo.setEstado(0);
        reclamo.setRecuperable(requerimiento.isRecupero() || requerimiento.isSurge());
        reclamo.setSuperintendencia(requerimiento.isSurge());
        reclamo.setDebitoPrestadora(porcentaje(requerimiento.getCargoTercerizadora()) > 0);
        return reclamo;
    }

    public static List<PrestacionesReclamo> crearPrestaciones(
            RequerimientoCompra requerimiento) throws Exception {

        List<PrestacionesReclamo> result = new ArrayList<PrestacionesReclamo>();
        if (requerimiento == null || requerimiento.getDetalles() == null) {
            return result;
        }

        validarPorcentajes(requerimiento);
        int idRegistro = 1;
        for (RequerimientoCompraDetalle detalle : requerimiento.getDetalles()) {
            if (detalle != null) {
                result.add(crearPrestacion(requerimiento, detalle, idRegistro++));
            }
        }
        return result;
    }

    public static String mapearSector(String sectorCompras) {
        String sector = normalizarTexto(sectorCompras);
        if (sector.indexOf("DISCAPAC") >= 0) {
            return "DISCAPACIDAD";
        }
        if (sector.indexOf("FARMAC") >= 0) {
            return "FARMACIA";
        }
        if (sector.indexOf("ODONTO") >= 0) {
            return "ODONTOLOGIA";
        }
        if (sector.indexOf("LEGAL") >= 0) {
            return "LEGALES";
        }
        if (sector.indexOf("PRESTACION") >= 0 && sector.indexOf("MEDIC") >= 0) {
            return "PRESTACIONES MEDICAS";
        }
        return "";
    }

    public static int resolverRecuperable(RequerimientoCompra requerimiento) {
        if (requerimiento != null && requerimiento.isSurge()) {
            return RECUPERABLE_SUR;
        }
        if (requerimiento != null && requerimiento.isRecupero()) {
            return RECUPERABLE_INTEGRACION;
        }
        return NO_RECUPERABLE;
    }

    private static PrestacionesReclamo crearPrestacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle,
            int idRegistro) throws Exception {

        validarDetalleCotizado(detalle);

        BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidad().intValue());
        BigDecimal unitario = importe(detalle.getPrecioUnitarioEstimado());
        BigDecimal total = importe(detalle.getPrecioTotalEstimadoInformado());
        BigDecimal cargoOspim = total
                .multiply(BigDecimal.valueOf(porcentaje(requerimiento.getCargoOspim())))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal cargoTercerizadora = total
                .subtract(cargoOspim)
                .setScale(2, RoundingMode.HALF_UP);

        PrestacionesReclamo p = new PrestacionesReclamo();
        p.setIdRegistro(idRegistro);
        p.setId_prestacion(0);
        p.setId_medicamento(0);
        p.setId_prestacionrecord(0);
        p.setTipoPrestacion(1);
        p.setCodigoPrestacion(
                detalle.getIdArticulo() != null
                        ? "ART-" + detalle.getIdArticulo()
                        : "COMPRA"
        );
        p.setNombreprestacion(detalle.getArticuloVisible());
        p.setDescripcion(detalle.getArticuloVisible());
        p.setNombremedicacion("");
        p.setFrecuencia("UNICA");
        p.setCantidad(cantidad.doubleValue());
        p.setImporte(unitario.doubleValue());
        p.setCargo_ospim(cargoOspim.doubleValue());
        p.setCargo_ps(cargoTercerizadora.doubleValue());
        p.setCargo_imesa(Double.valueOf(0D));
        p.setReconocidoSSS(0D);
        p.setRecuperable(Integer.valueOf(resolverRecuperable(requerimiento)));
        p.setRecuperableSur(Boolean.valueOf(requerimiento.isSurge()));
        p.setIdTercerizadora(
                porcentaje(requerimiento.getCargoTercerizadora()) > 0
                        ? requerimiento.getIdTercerizadora()
                        : null
        );
        p.setCuilTitular(requerimiento.getAfiliadoCuilTitular());
        p.setInte(requerimiento.getAfiliadoInt().intValue());

        // Una cotización no es una factura; estos datos deben confirmarse en RP.
        p.setComprobanteTipo("OTR");
        p.setComprobanteNro(null);
        p.setComprobanteFecha(null);
        p.setComprobanteLetra(null);
        p.setComprobanteSucursal(null);
        p.setComprobanteCUITSucursal(null);
        p.setComprobanteCUIT(normalizarCuit(detalle.getPrestadorCuit()));
        p.setComprobanteRazonSocial(detalle.getPrestadorRazonSocial());
        p.setComprobanteCantidad(Double.valueOf(cantidad.doubleValue()));
        p.setComprobanteImporte(Double.valueOf(unitario.doubleValue()));
        p.setComprobanteTotal(Double.valueOf(total.doubleValue()));
        p.setFechaPrestacion(null);
        p.setEstado(PrestacionesReclamo.ESTADOS.NUEVO);
        p.setEstadoRechazoAprobado(0);
        p.setObservaciones(construirObservacion(requerimiento, detalle));
        return p;
    }

    private static void validarContexto(
            ReclamoPrestacionalCompraContexto contexto,
            String nonce,
            String usuario) throws Exception {

        if (contexto == null
                || !contexto.coincideNonce(nonce)
                || !contexto.perteneceAUsuario(usuario)
                || !contexto.estaVigente(System.currentTimeMillis())) {
            throw new Exception(
                    "El contexto de Compras no es válido o venció. "
                            + "Vuelva al requerimiento e inicie nuevamente el Reclamo Prestacional."
            );
        }
    }

    private static void validarRelacionPersistida(int idRequerimiento)
            throws Exception {

        RequerimientoCompraReclamoPrestacional relacion =
                RequerimientoCompraReclamoPrestacionalServiceUtil
                        .obtenerPorRequerimiento(idRequerimiento);
        if (relacion == null) {
            return;
        }
        if (relacion.isVinculado()) {
            throw new Exception("El requerimiento ya posee un Reclamo Prestacional vinculado.");
        }
        if (relacion.isError()) {
            throw new Exception(
                    "El Reclamo Prestacional fue creado, pero su vinculación requiere "
                            + "reconciliación. No se permite crear otro reclamo."
            );
        }
        throw new Exception(
                "Ya existe una creación de Reclamo Prestacional en proceso para este requerimiento."
        );
    }

    private static void validarRequerimiento(
            RequerimientoCompra requerimiento,
            ReclamoPrestacionalCompraContexto contexto) throws Exception {

        if (requerimiento == null || requerimiento.getBajaFecha() != null) {
            throw new Exception("El requerimiento de compra ya no está activo.");
        }
        if (!WebKeysCompras.esCotizado(requerimiento.getEstado())) {
            throw new Exception("El requerimiento de compra ya no está COTIZADO.");
        }
        if (!requerimiento.tieneAfiliadoInformado()) {
            throw new Exception("El requerimiento COTIZADO no tiene un afiliado válido.");
        }

        String cuilReq = normalizarCuit(requerimiento.getAfiliadoCuilTitular());
        String cuilCtx = normalizarCuit(contexto.getAfiliadoCuilTitular());
        int inteReq = requerimiento.getAfiliadoInt().intValue();
        int inteCtx = contexto.getAfiliadoInt() != null
                ? contexto.getAfiliadoInt().intValue()
                : -1;
        if (!cuilReq.equals(cuilCtx) || inteReq != inteCtx) {
            throw new Exception(
                    "El afiliado persistido del requerimiento no coincide "
                            + "con el contexto de creación del RP."
            );
        }
    }

    private static void validarDetalleCotizado(RequerimientoCompraDetalle detalle)
            throws Exception {

        if (!detalle.estaCompletoParaCotizacion()
                || detalle.getPrecioTotalEstimadoInformado() == null
                || WebKeysCompras.isEmpty(detalle.getArticuloVisible())) {
            throw new Exception(
                    "El requerimiento figura COTIZADO, pero contiene un ítem sin "
                            + "artículo, cantidad, precio o prestador adjudicado."
            );
        }

        BigDecimal unitario = importe(detalle.getPrecioUnitarioEstimado());
        BigDecimal total = importe(detalle.getPrecioTotalEstimadoInformado());
        BigDecimal calculado = unitario
                .multiply(BigDecimal.valueOf(detalle.getCantidad().intValue()))
                .setScale(2, RoundingMode.HALF_UP);
        if (unitario.signum() < 0 || total.signum() < 0 || total.compareTo(calculado) != 0) {
            throw new Exception(
                    "El requerimiento figura COTIZADO, pero contiene un ítem "
                            + "con importes inconsistentes."
            );
        }
    }

    private static void validarPorcentajes(RequerimientoCompra requerimiento)
            throws Exception {

        int ospim = porcentaje(requerimiento.getCargoOspim());
        int tercerizadora = porcentaje(requerimiento.getCargoTercerizadora());
        if (ospim < 0 || ospim > 100
                || tercerizadora < 0 || tercerizadora > 100
                || ospim + tercerizadora != 100) {
            throw new Exception(
                    "Los porcentajes de cargo deben estar entre 0 y 100 y sumar exactamente 100."
            );
        }
        if (tercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {
            throw new Exception(
                    "El requerimiento asigna cargo a una tercerizadora, "
                            + "pero no tiene una tercerizadora informada."
            );
        }
    }

    private static String construirObservacion(
            RequerimientoCompra requerimiento,
            RequerimientoCompraDetalle detalle) {

        StringBuilder value = new StringBuilder();
        value.append("Precargado desde Requerimiento de Compra #");
        value.append(requerimiento.getIdRequerimientoCompra());
        value.append(". Confirmar nomenclador, fecha, comprobante y reconocido SSS.");
        agregar(value, detalle.getObservaciones());
        agregar(value, requerimiento.getObservaciones());
        String result = value.toString();
        return result.length() <= MAX_OBSERVACION
                ? result
                : result.substring(0, MAX_OBSERVACION);
    }

    private static void agregar(StringBuilder destino, String value) {
        String texto = WebKeysCompras.trimToNull(value);
        if (texto != null) {
            destino.append(' ').append(texto);
        }
    }

    private static BigDecimal importe(BigDecimal value) throws Exception {
        if (value == null) {
            throw new Exception("Un ítem cotizado no tiene importe.");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static int porcentaje(Integer value) {
        return value != null ? value.intValue() : 0;
    }

    private static String normalizarTexto(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim()
                .toUpperCase()
                .replaceAll("\\s+", " ");
    }

    private static String normalizarCuit(String value) {
        return value != null ? value.replaceAll("[^0-9]", "") : "";
    }
}
