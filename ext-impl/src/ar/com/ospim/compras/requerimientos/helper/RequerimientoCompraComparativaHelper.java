package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraComparativaServiceUtil;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequerimientoCompraComparativaHelper {

    public RequerimientoCompra obtenerRequerimiento(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Debe informar un requerimiento.");
        }
        RequerimientoCompra r =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(id);
        if (r == null) {
            throw new IllegalArgumentException("No se encontró el requerimiento.");
        }
        r.setDetalles(BusquedaRequerimientoCompraServiceUtil.getDetalles(id));
        validarPrestaciones(r.getDetalles());
        return r;
    }

    public void validarPrestaciones(List<RequerimientoCompraDetalle> prestaciones) {
        Set<Integer> ids = new HashSet<Integer>();
        if (prestaciones == null || prestaciones.isEmpty()) {
            throw new IllegalArgumentException("El requerimiento no tiene prestaciones.");
        }
        for (RequerimientoCompraDetalle d : prestaciones) {
            if (d.getIdPrestacionInt() <= 0) {
                throw new IllegalArgumentException(
                        "La comparativa requiere que todos los ítems tengan una prestación del nomenclador.");
            }
            if (!ids.add(d.getIdPrestacion())) {
                throw new IllegalArgumentException(
                        "El requerimiento repite una prestación del nomenclador.");
            }
        }
    }

    public List<RequerimientoCompraComparativa> cargar(final RequerimientoCompra r)
            throws Exception {
        List<RequerimientoCompraComparativa> lista =
                RequerimientoCompraComparativaServiceUtil.listar(r.getIdRequerimientoCompra());
        if (lista.isEmpty()) {
            throw new IllegalArgumentException(
                    "El requerimiento no tiene presupuestos de prestadores cargados.");
        }
        for (RequerimientoCompraComparativa c : lista) {
            completarDetalles(r, c);
        }
        Collections.sort(lista, new Comparator<RequerimientoCompraComparativa>() {
            public int compare(RequerimientoCompraComparativa a,
                    RequerimientoCompraComparativa b) {
                int adjudicado = r.getIdPrestadorAdjudicadoInt();
                if (a.getIdPrestador() == adjudicado && b.getIdPrestador() != adjudicado) {
                    return -1;
                }
                if (b.getIdPrestador() == adjudicado && a.getIdPrestador() != adjudicado) {
                    return 1;
                }
                return a.getIdPrestador() < b.getIdPrestador() ? -1
                        : a.getIdPrestador() == b.getIdPrestador() ? 0 : 1;
            }
        });
        return lista;
    }

    public Map<Integer, Map<Integer, String>> obtenerPreciosPorPrestador(int idRequerimiento)
            throws Exception {
        Map<Integer, Map<Integer, String>> precios =
                new HashMap<Integer, Map<Integer, String>>();
        for (RequerimientoCompraComparativa c :
                RequerimientoCompraComparativaServiceUtil.listar(idRequerimiento)) {
            Map<Integer, String> importes = new HashMap<Integer, String>();
            for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                if (d.getImporteUnitario() != null) {
                    importes.put(Integer.valueOf(d.getIdPrestacion()),
                            d.getImporteUnitario().toPlainString());
                }
            }
            precios.put(Integer.valueOf(c.getIdPrestador()), importes);
        }
        return precios;
    }

    public RequerimientoCompraComparativa cargarPrestador(RequerimientoCompra r,
            int idPrestador) throws Exception {
        boolean enviado = false;
        for (PrestadorCotizacion p : BusquedaRequerimientoCompraServiceUtil
                .listarPrestadoresEnviados(r.getIdRequerimientoCompra())) {
            if (p.getIdPrestador() == idPrestador
                    && (WebKeysCompras.ENVIO_ENVIADO.equals(p.getEstadoEnvio())
                        || WebKeysCompras.ENVIO_COTIZADO.equals(p.getEstadoEnvio()))) {
                enviado = true;
                break;
            }
        }
        if (!enviado || r.esSectorSinCotizacionPrestador()) {
            throw new IllegalArgumentException("Seleccione un prestador notificado para este requerimiento.");
        }
        List<RequerimientoCompraComparativa> lista =
                RequerimientoCompraComparativaServiceUtil.listar(
                        r.getIdRequerimientoCompra(), idPrestador);
        if (lista.size() != 1) {
            throw new IllegalArgumentException("No se pudo cargar el prestador seleccionado.");
        }
        RequerimientoCompraComparativa c = lista.get(0);
        completarDetalles(r, c);
        return c;
    }

    private void completarDetalles(RequerimientoCompra r,
            RequerimientoCompraComparativa c) {
        Map<Integer, RequerimientoCompraComparativaDetalle> guardados =
                new HashMap<Integer, RequerimientoCompraComparativaDetalle>();
        for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
            guardados.put(Integer.valueOf(d.getIdPrestacion()), d);
        }
        List<RequerimientoCompraComparativaDetalle> detalles =
                new ArrayList<RequerimientoCompraComparativaDetalle>();
        for (RequerimientoCompraDetalle item : r.getDetalles()) {
            RequerimientoCompraComparativaDetalle d = guardados.remove(item.getIdPrestacion());
            if (d == null) {
                d = new RequerimientoCompraComparativaDetalle();
                d.setIdPrestacion(item.getIdPrestacionInt());
            }
            d.setCodigo(item.getCodigoItemVisible());
            d.setPrestacion(item.getDescripcionItemVisible());
            detalles.add(d);
        }
        if (!guardados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Hay prestaciones guardadas que ya no pertenecen al requerimiento.");
        }
        c.setDetalles(detalles);
        calcular(c);
    }

    public void guardar(List<RequerimientoCompraComparativa> lista,
            Map<String, String> entrada, String usuario) throws Exception {
        if (usuario == null || usuario.trim().length() == 0 || usuario.length() > 100) {
            throw new IllegalArgumentException("No se pudo determinar el usuario de auditoría.");
        }
        aplicarEntrada(lista, entrada);
        List<Integer> vaciados = new ArrayList<Integer>();
        for (RequerimientoCompraComparativa c : lista) {
            Iterator<RequerimientoCompraComparativaDetalle> it = c.getDetalles().iterator();
            while (it.hasNext()) {
                RequerimientoCompraComparativaDetalle d = it.next();
                if (d.getCantidad() == null && d.getImporteUnitario() == null) {
                    if (d.getIdDetalle() > 0) {
                        vaciados.add(Integer.valueOf(d.getIdDetalle()));
                    }
                    it.remove();
                }
            }
        }
        RequerimientoCompraComparativaServiceUtil.guardar(lista, vaciados, usuario);
    }

    public void aplicarEntrada(List<RequerimientoCompraComparativa> lista,
            Map<String, String> entrada) {
        for (RequerimientoCompraComparativa c : lista) {
            String sufijo = "_" + c.getIdPrestador();
            String contexto = "Prestador " + c.getIdPrestador() + ": ";
            if (!String.valueOf(c.getIdPrestador()).equals(entrada.get("prestador" + sufijo))) {
                throw new IllegalArgumentException(
                        "Cambió el prestador de la carga. Vuelva a seleccionarlo.");
            }
            try {
                c.setFechaPresupuesto(fecha(entrada.get("fecha" + sufijo)));
                c.setFormaPago(Integer.valueOf(opcion(entrada.get("pago" + sufijo),
                        new String[] {"30", "45", "60"}, false, "Forma de pago")));
                c.setPlazoEntrega(opcion(entrada.get("plazo" + sufijo),
                        new String[] {"24/48", "48/72"}, true, "Plazo de entrega"));
                String validez = opcion(entrada.get("validez" + sufijo),
                        new String[] {"24", "48"}, true, "Validez");
                c.setValidezPresupuesto(validez == null ? null : Integer.valueOf(validez));
                c.setEnvio(opcion(entrada.get("envio" + sufijo),
                        new String[] {"Obra Social", "Beneficiario", "Delegación"}, true, "Envío"));
                c.setIva(importe(entrada.get("iva" + sufijo), "IVA"));
                c.setIibb(importe(entrada.get("iibb" + sufijo), "IIBB"));
                for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                    String clave = sufijo + "_" + d.getIdPrestacion();
                    d.setCantidad(cantidad(entrada.get("cantidad" + clave)));
                    d.setImporteUnitario(importe(entrada.get("importe" + clave),
                            "Importe de prestación " + d.getIdPrestacion()));
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(contexto + e.getMessage());
            }
            calcular(c);
        }
    }

    public void calcular(RequerimientoCompraComparativa c) {
        BigDecimal neto = null;
        boolean incompleto = false;
        for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
            d.setSubtotal(null);
            if (d.getCantidad() != null && d.getImporteUnitario() != null) {
                d.setSubtotal(d.getImporteUnitario()
                        .multiply(new BigDecimal(d.getCantidad().toString()))
                        .setScale(2, BigDecimal.ROUND_HALF_UP));
                neto = (neto == null ? BigDecimal.ZERO : neto).add(d.getSubtotal());
            } else if (d.getCantidad() != null || d.getImporteUnitario() != null) {
                incompleto = true;
            }
        }
        c.setNeto(neto);
        c.setIncompleto(incompleto);
        c.setTotal(neto == null ? null : neto
                .add(c.getIva() == null ? BigDecimal.ZERO : c.getIva())
                .add(c.getIibb() == null ? BigDecimal.ZERO : c.getIibb()));
    }

    public static String texto(Object valor) {
        if (valor == null) {
            return "";
        }
        if (valor instanceof Date) {
            return new SimpleDateFormat("dd/MM/yyyy").format((Date) valor);
        }
        if (valor instanceof BigDecimal) {
            return ((BigDecimal) valor).toPlainString();
        }
        return valor.toString();
    }

    public static BigDecimal importe(String valor, String campo) {
        String s = texto(valor).trim();
        if (s.length() == 0) {
            return null;
        }
        if (!s.matches("-?[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException(campo + ": use un importe sin miles y hasta dos decimales.");
        }
        BigDecimal n = new BigDecimal(s.replace(',', '.')).setScale(2);
        if (n.precision() > 18) {
            throw new IllegalArgumentException(campo + ": importe fuera de rango.");
        }
        return n;
    }

    public static Integer cantidad(String valor) {
        String s = texto(valor).trim();
        if (s.length() == 0) {
            return null;
        }
        try {
            if (!s.matches("-?[0-9]+")) {
                throw new NumberFormatException();
            }
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero.");
        }
    }

    private Date fecha(String valor) {
        String s = texto(valor).trim();
        if (s.length() == 0) {
            return null;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        ParsePosition posicion = new ParsePosition(0);
        Date fecha = formato.parse(s, posicion);
        if (!s.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")
                || fecha == null || posicion.getIndex() != s.length()) {
            throw new IllegalArgumentException("Fecha de presupuesto inválida. Use dd/mm/aaaa.");
        }
        return fecha;
    }

    private String opcion(String valor, String[] opciones, boolean vacio, String campo) {
        String s = texto(valor).trim();
        if (vacio && s.length() == 0) {
            return null;
        }
        for (String opcion : opciones) {
            if (opcion.equals(s)) {
                return s;
            }
        }
        throw new IllegalArgumentException(campo + ": seleccione una opción válida.");
    }

    public List<Map<String, ?>> filasPdf(RequerimientoCompra r,
            List<RequerimientoCompraComparativa> lista) {
        List<Map<String, ?>> filas = new ArrayList<Map<String, ?>>();
        for (RequerimientoCompraComparativa c : lista) {
            if (c.getIdComparativa() == 0) {
                continue;
            }
            boolean tieneDetalle = false;
            for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
                if (d.getIdDetalle() > 0) {
                    filas.add(filaPdf(r, c, d));
                    tieneDetalle = true;
                }
            }
            if (!tieneDetalle) {
                filas.add(filaPdf(r, c, null));
            }
        }
        if (filas.isEmpty()) {
            throw new IllegalArgumentException("Guarde la comparativa antes de imprimir.");
        }
        return filas;
    }

    private Map<String, String> filaPdf(RequerimientoCompra r,
            RequerimientoCompraComparativa c, RequerimientoCompraComparativaDetalle d) {
        Map<String, String> fila = new HashMap<String, String>();
        fila.put("idPrestador", String.valueOf(c.getIdPrestador()));
        fila.put("prestador", texto(c.getPrestador())
                + (c.getIdPrestador() == r.getIdPrestadorAdjudicadoInt() ? " (Adjudicado)" : ""));
        fila.put("condiciones", "Fecha: " + texto(c.getFechaPresupuesto())
                + "   Pago: " + texto(c.getFormaPago()) + " días"
                + "   Entrega: " + texto(c.getPlazoEntrega())
                + "   Validez (hs): " + texto(c.getValidezPresupuesto())
                + "   Envío: " + texto(c.getEnvio()));
        fila.put("codigo", d == null ? "" : texto(d.getCodigo()));
        fila.put("prestacion", d == null ? "" : texto(d.getPrestacion()));
        fila.put("cantidad", d == null ? "" : texto(d.getCantidad()));
        fila.put("importe", d == null ? "" : texto(d.getImporteUnitario()));
        fila.put("subtotal", d == null ? "" : texto(d.getSubtotal()));
        fila.put("totales", "Neto: " + texto(c.getNeto())
                + "   IVA (importe): " + texto(c.getIva())
                + "   IIBB (importe): " + texto(c.getIibb())
                + (c.getIncompleto() ? "   Total parcial: " : "   Total: ") + texto(c.getTotal()));
        return fila;
    }
}
