package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraComparativaServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapta la validacion de adjudicaciones de EditarRequerimientoCompraHelper y
 * la carga de importes/fechas de RequerimientoCompraComparativaHelper.
 * La emision posterior al guardado sigue EditarOrdenesPagoOspimAction.
 */
public class RequerimientoCompraOrdenHelper {

    private static final BigDecimal MAX_IMPORTE =
            new BigDecimal("99999999999999.99");

    public RequerimientoCompraOrden preparar(int id, String nombreUsuario)
            throws Exception {
        RequerimientoCompra r = cargarRequerimiento(id);
        RequerimientoCompraOrden existente = obtenerOrden(id);
        if (existente != null) {
            existente.setRequerimiento(r);
            return existente;
        }
        if (!WebKeysCompras.esAutorizado(r.getEstado())) {
            throw new IllegalArgumentException(
                    "El requerimiento debe estar AUTORIZADO para Crear Orden de Compra.");
        }
        validarDetalles(r);
        RequerimientoCompraOrden orden = new RequerimientoCompraOrden();
        orden.setIdRequerimientoCompra(id);
        orden.setRequerimiento(r);
        orden.setFechaEmision(new Date());
        orden.setFechaRecepcion(r.getAltaFecha());
        orden.setMoneda("");
        orden.setConfeccionadoPor(texto(nombreUsuario, 250, false, "Confeccionado por"));
        orden.setSolicitante(texto(r.getAltaUsr(), 250, false, "Solicitante"));
        orden.setObservaciones(texto(r.getObservaciones(), 4000, false, "Observaciones"));
        if (r.esSectorSinCotizacionPrestador()) {
            prepararEmpresa(r, orden);
        } else {
            prepararPrestador(r, orden);
        }
        return orden;
    }

    public RequerimientoCompraOrden crear(int id, Map<String, String> entrada,
            String usuario) throws Exception {
        String auditoria = texto(usuario, 100, true, "Usuario de auditoria");
        if (entrada == null) {
            throw new IllegalArgumentException("No se recibieron los datos de la Orden de Compra.");
        }
        // Se recargan estado, adjudicacion y precios al confirmar el formulario.
        RequerimientoCompraOrden orden = preparar(id, auditoria);
        if (orden.getIdOrdenCompra() > 0) {
            return orden;
        }
        aplicarEntrada(orden, entrada);
        validarImportes(orden);
        orden.setAltaUsr(auditoria);
        RequerimientoCompraOrden guardada = guardarOrden(orden, auditoria);
        if (guardada == null || guardada.getIdOrdenCompra() <= 0
                || guardada.getIdRequerimientoCompra() != id) {
            throw new IllegalStateException("No se pudo confirmar la Orden de Compra.");
        }
        guardada.setRequerimiento(orden.getRequerimiento());
        return guardada;
    }

    private RequerimientoCompra cargarRequerimiento(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Debe informar el requerimiento de compra.");
        }
        RequerimientoCompra r = obtenerRequerimiento(id);
        if (r == null || r.getIdRequerimientoCompra() != id || !r.isActivo()) {
            throw new IllegalArgumentException("El requerimiento ya no esta disponible.");
        }
        r.setDetalles(obtenerDetalles(id));
        return r;
    }

    private void validarDetalles(RequerimientoCompra r) {
        if (!r.tieneDetalles()) {
            throw new IllegalArgumentException("El requerimiento no tiene detalles activos.");
        }
        Set<Integer> ids = new HashSet<Integer>();
        for (RequerimientoCompraDetalle d : r.getDetalles()) {
            if (d == null || d.getIdInt() <= 0
                    || d.getIdRequerimientoCompra() != r.getIdRequerimientoCompra()
                    || !ids.add(d.getId()) || d.getCantidadInt() <= 0
                    || d.getDescripcionItemVisible().trim().length() == 0) {
                throw new IllegalArgumentException(
                        "Los detalles del requerimiento estan incompletos. Actualice la pantalla.");
            }
        }
    }

    private void prepararEmpresa(RequerimientoCompra r,
            RequerimientoCompraOrden orden) throws Exception {
        RequerimientoCompraPresupuesto adjudicada = null;
        List<RequerimientoCompraPresupuesto> cotizaciones =
                obtenerCotizacionesEmpresa(r.getIdRequerimientoCompra());
        for (int i = 0; cotizaciones != null && i < cotizaciones.size(); i++) {
            RequerimientoCompraPresupuesto c = cotizaciones.get(i);
            if (c != null && c.isActivo() && c.isCotizacionEmpresa()
                    && c.isEmpresaAdjudicada()) {
                if (adjudicada != null) {
                    throw new IllegalArgumentException(
                            "Debe existir exactamente una Empresa adjudicada.");
                }
                adjudicada = c;
            }
        }
        if (adjudicada == null || adjudicada.getIdRequerimiento() == null
                || adjudicada.getIdRequerimiento().intValue()
                != r.getIdRequerimientoCompra()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una Empresa adjudicada con cotizacion activa del requerimiento.");
        }
        new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(
                adjudicada.getEmpresaCuit(), adjudicada.getEmpresaSucursal(),
                adjudicada.getDescripcionEmpresa(), 1);
        orden.setProveedor(texto(adjudicada.getDescripcionEmpresa(), 250, true, "Proveedor"));
        orden.setProveedorCuit(texto(adjudicada.getEmpresaCuit(), 20, false, "CUIT proveedor"));
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        for (RequerimientoCompraDetalle original : r.getDetalles()) {
            RequerimientoCompraDetalle d = copiarDetalle(original);
            d.setPrecioUnitarioEstimado(original.getPrecioUnitarioEstimado());
            detalles.add(d);
        }
        orden.setDetalles(detalles);
    }

    private void prepararPrestador(RequerimientoCompra r,
            RequerimientoCompraOrden orden) throws Exception {
        if (!r.tieneCotizacionCompletaConPrestadorUnico()) {
            throw new IllegalArgumentException(
                    "Todos los detalles deben tener una cotizacion completa con el mismo prestador adjudicado.");
        }
        RequerimientoCompraReclamoPrestacional relacion =
                obtenerRelacionRp(r.getIdRequerimientoCompra());
        if (relacion == null || !relacion.isVinculado()
                || relacion.getIdRequerimientoCompra() != r.getIdRequerimientoCompra()) {
            throw new IllegalArgumentException(
                    "El requerimiento debe tener un Reclamo Prestacional vinculado.");
        }
        orden.setIdReclamoPrestacional(relacion.getIdReclamoPrestacionalInt());
        List<RequerimientoCompraComparativa> comparativas = obtenerComparativas(
                r.getIdRequerimientoCompra(), r.getIdPrestadorAdjudicadoInt());
        if (comparativas == null || comparativas.size() != 1) {
            throw new IllegalArgumentException(
                    "Debe guardar la comparativa completa del prestador adjudicado.");
        }
        RequerimientoCompraComparativa c = comparativas.get(0);
        if (c == null || c.getIdComparativa() <= 0
                || c.getIdRequerimiento() != r.getIdRequerimientoCompra()
                || c.getIdPrestador() != r.getIdPrestadorAdjudicadoInt()
                || c.getDetalles() == null) {
            throw new IllegalArgumentException("La comparativa adjudicada no es valida.");
        }
        Map<Integer, RequerimientoCompraComparativaDetalle> precios =
                new HashMap<Integer, RequerimientoCompraComparativaDetalle>();
        for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
            if (d == null || d.getIdDetalle() <= 0 || d.getIdPrestacion() <= 0
                    || precios.put(Integer.valueOf(d.getIdPrestacion()), d) != null) {
                throw new IllegalArgumentException("La comparativa adjudicada tiene detalles invalidos.");
            }
        }
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        String proveedor = null;
        String cuit = null;
        for (RequerimientoCompraDetalle original : r.getDetalles()) {
            String nombreActual = texto(original.getPrestadorRazonSocial(), 250, true, "Proveedor");
            String cuitActual = texto(original.getPrestadorCuit(), 20, false, "CUIT proveedor");
            if (proveedor != null && (!proveedor.equals(nombreActual) || !cuit.equals(cuitActual))) {
                throw new IllegalArgumentException("Los datos del prestador adjudicado no coinciden.");
            }
            proveedor = nombreActual;
            cuit = cuitActual;
            RequerimientoCompraComparativaDetalle cotizado =
                    precios.remove(Integer.valueOf(original.getIdPrestacionInt()));
            if (cotizado == null || cotizado.getCantidad() == null
                    || cotizado.getCantidad().intValue() <= 0) {
                throw new IllegalArgumentException(
                        "La comparativa adjudicada debe cotizar todos los detalles del requerimiento.");
            }
            validarImporte(cotizado.getImporteUnitario(), true, "Precio unitario adjudicado");
            RequerimientoCompraDetalle d = copiarDetalle(original);
            d.setCantidad(cotizado.getCantidad());
            d.setPrecioUnitarioEstimado(cotizado.getImporteUnitario());
            detalles.add(d);
        }
        if (!precios.isEmpty()) {
            throw new IllegalArgumentException(
                    "La comparativa contiene prestaciones ajenas al requerimiento.");
        }
        orden.setProveedor(proveedor);
        orden.setProveedorCuit(cuit);
        orden.setDetalles(detalles);
        orden.setIva(impuesto(c.getIva(), "IVA adjudicado"));
        orden.setIibb(impuesto(c.getIibb(), "IIBB adjudicado"));
        orden.setFormaPago(c.getFormaPago() == null ? "" : c.getFormaPago() + " dias");
        orden.setPlazoPago(orden.getFormaPago());
        orden.setEntrega(unir(c.getPlazoEntrega(), c.getEnvio()));
        orden.setCondicion(c.getValidezPresupuesto() == null ? ""
                : "Validez del presupuesto: " + c.getValidezPresupuesto() + " horas");
        orden.setFechaOrdenMedica(fechaOrdenMedica(r.getIdRequerimientoCompra()));
        validarImportes(orden);
    }

    private void aplicarEntrada(RequerimientoCompraOrden orden, Map<String, String> entrada) {
        String moneda = texto(entrada.get("moneda"), 3, true, "Moneda");
        if (!"ARS".equals(moneda) && !"USD".equals(moneda)) {
            throw new IllegalArgumentException("Seleccione una moneda valida: ARS o USD.");
        }
        orden.setMoneda(moneda);
        orden.setProveedorDomicilio(texto(entrada.get("proveedorDomicilio"), 250, false, "Domicilio proveedor"));
        orden.setProveedorContacto(texto(entrada.get("proveedorContacto"), 250, false, "Contacto proveedor"));
        orden.setProveedorEmail(texto(entrada.get("proveedorEmail"), 250, false, "Mail proveedor"));
        orden.setProveedorTelefono(texto(entrada.get("proveedorTelefono"), 100, false, "Telefono proveedor"));
        orden.setCondicion(texto(entrada.get("condicion"), 1000, false, "Condicion"));
        orden.setFormaPago(texto(entrada.get("formaPago"), 250, true, "Forma de pago"));
        orden.setPlazoPago(texto(entrada.get("plazoPago"), 250, false, "Plazo de pago"));
        orden.setEntrega(texto(entrada.get("entrega"), 1000, true, "Entrega"));
        orden.setObservaciones(texto(entrada.get("observaciones"), 4000, false, "Observaciones"));
        orden.setConfeccionadoPor(texto(entrada.get("confeccionadoPor"), 250, true, "Confeccionado por"));
        orden.setTelefonoCompras(texto(entrada.get("telefonoCompras"), 100, false, "Telefono Compras"));
        orden.setSolicitante(texto(entrada.get("solicitante"), 250, false, "Solicitante"));
        orden.setPlan(texto(entrada.get("plan"), 120, false, "Plan"));
        orden.setFechaOrdenMedica(fecha(entrada.get("fechaOrdenMedica"), "Fecha orden medica"));
        orden.setFechaRecepcion(fecha(entrada.get("fechaRecepcion"), "Fecha recibido Compras"));
        if (orden.getRequerimiento().esSectorSinCotizacionPrestador()) {
            orden.setIva(importeEntrada(entrada.get("iva"), false, "IVA"));
            orden.setIibb(importeEntrada(entrada.get("iibb"), false, "IIBB"));
            for (RequerimientoCompraDetalle d : orden.getDetalles()) {
                d.setPrecioUnitarioEstimado(importeEntrada(
                        entrada.get("precio_" + d.getIdInt()), true, "Precio del detalle " + d.getIdInt()));
            }
        }
    }

    private RequerimientoCompraDetalle copiarDetalle(RequerimientoCompraDetalle original) {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(original.getId());
        d.setIdRequerimientoCompra(original.getIdRequerimientoCompra());
        d.setTipoItem(original.getTipoItem());
        d.setIdTipoPrestacion(original.getIdTipoPrestacion());
        d.setIdPrestacion(original.getIdPrestacion());
        d.setCodigoItem(original.getCodigoItemVisible());
        String descripcion = original.getDescripcionItemVisible();
        if (original.getObservacionesVisible().length() > 0) {
            descripcion += "\n" + original.getObservacionesVisible();
        }
        d.setDescripcionItem(descripcion);
        d.setCantidad(original.getCantidad());
        d.setIdPrestador(original.getIdPrestador());
        d.setPrestadorCuit(original.getPrestadorCuit());
        d.setPrestadorRazonSocial(original.getPrestadorRazonSocial());
        return d;
    }

    private void validarImportes(RequerimientoCompraOrden orden) {
        if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La Orden de Compra no tiene detalles.");
        }
        for (RequerimientoCompraDetalle d : orden.getDetalles()) {
            if (d.getCantidadInt() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            validarImporte(d.getPrecioUnitarioEstimado(), true, "Precio unitario");
            validarImporte(d.getPrecioUnitarioEstimado().multiply(
                    new BigDecimal(d.getCantidadInt())), true, "Subtotal");
        }
        validarImporte(orden.getIva(), false, "IVA");
        validarImporte(orden.getIibb(), false, "IIBB");
        validarImporte(orden.getTotal(), true, "Total");
    }

    private BigDecimal importeEntrada(String valor, boolean positivo, String campo) {
        String s = valor == null ? "" : valor.trim();
        if (s.length() == 0 && !positivo) {
            return BigDecimal.ZERO;
        }
        if (s.length() > 18 || !s.matches("[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException(campo + ": use un importe sin miles y hasta dos decimales.");
        }
        BigDecimal importe = new BigDecimal(s.replace(',', '.'));
        validarImporte(importe, positivo, campo);
        return importe.setScale(2);
    }

    private BigDecimal impuesto(BigDecimal valor, String campo) {
        BigDecimal importe = valor == null ? BigDecimal.ZERO : valor;
        validarImporte(importe, false, campo);
        return importe;
    }

    private void validarImporte(BigDecimal valor, boolean positivo, String campo) {
        if (valor == null || valor.scale() > 2 || valor.compareTo(MAX_IMPORTE) > 0
                || valor.compareTo(BigDecimal.ZERO) < 0
                || (positivo && valor.compareTo(BigDecimal.ZERO) == 0)) {
            throw new IllegalArgumentException(campo + ": importe fuera de rango.");
        }
    }

    private String texto(String valor, int maximo, boolean obligatorio, String campo) {
        String s = valor == null ? "" : valor.trim();
        if (s.length() > maximo) {
            throw new IllegalArgumentException(campo + ": admite hasta " + maximo + " caracteres.");
        }
        if (obligatorio && s.length() == 0) {
            throw new IllegalArgumentException("Debe informar " + campo + ".");
        }
        return s;
    }

    private Date fecha(String valor, String campo) {
        String s = valor == null ? "" : valor.trim();
        if (s.length() == 0) {
            return null;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        ParsePosition posicion = new ParsePosition(0);
        Date fecha = formato.parse(s, posicion);
        if (!s.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")
                || fecha == null || posicion.getIndex() != s.length()) {
            throw new IllegalArgumentException(campo + ": use una fecha valida dd/mm/aaaa.");
        }
        return fecha;
    }

    private Date fechaOrdenMedica(int id) throws Exception {
        Date fecha = null;
        List<RequerimientoCompraPresupuesto> documentos = obtenerOrdenesMedicas(id);
        for (int i = 0; documentos != null && i < documentos.size(); i++) {
            RequerimientoCompraPresupuesto d = documentos.get(i);
            if (d != null && d.isActivo() && d.getIdRequerimiento() != null
                    && d.getIdRequerimiento().intValue() == id && d.getFechaDocumento() != null) {
                if (fecha != null && !fecha.equals(d.getFechaDocumento())) {
                    return null;
                }
                fecha = d.getFechaDocumento();
            }
        }
        return fecha;
    }

    private String unir(String a, String b) {
        String primero = texto(a, 1000, false, "Entrega");
        String segundo = texto(b, 1000, false, "Envio");
        return primero + (primero.length() > 0 && segundo.length() > 0 ? " - " : "") + segundo;
    }

    protected RequerimientoCompra obtenerRequerimiento(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(id);
    }

    protected List<RequerimientoCompraDetalle> obtenerDetalles(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getDetalles(id);
    }

    protected RequerimientoCompraOrden obtenerOrden(int id) throws Exception {
        return RequerimientoCompraOrdenServiceUtil.obtenerPorRequerimiento(id);
    }

    protected RequerimientoCompraOrden guardarOrden(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return RequerimientoCompraOrdenServiceUtil.crear(orden, usuario);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerCotizacionesEmpresa(int id)
            throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarCotizacionesEmpresa(id);
    }

    protected List<RequerimientoCompraComparativa> obtenerComparativas(int id,
            int idPrestador) throws Exception {
        return RequerimientoCompraComparativaServiceUtil.listar(id, idPrestador);
    }

    protected RequerimientoCompraReclamoPrestacional obtenerRelacionRp(int id)
            throws Exception {
        return new RequerimientoCompraReclamoPrestacionalHelper().obtenerPorRequerimiento(id);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerOrdenesMedicas(int id)
            throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(id);
    }
}
