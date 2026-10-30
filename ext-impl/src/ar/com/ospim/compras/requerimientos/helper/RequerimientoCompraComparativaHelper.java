package ar.com.ospim.compras.requerimientos.helper;

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
import java.util.TreeMap;

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
                RequerimientoCompraComparativaServiceUtil.listar(
                        r.getIdRequerimientoCompra());

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

                if (a.getIdPrestador() == adjudicado
                        && b.getIdPrestador() != adjudicado) {
                    return -1;
                }

                if (b.getIdPrestador() == adjudicado
                        && a.getIdPrestador() != adjudicado) {
                    return 1;
                }

                BigDecimal totalA = a.getTotal();
                BigDecimal totalB = b.getTotal();

                if (totalA == null && totalB != null) {
                    return 1;
                }

                if (totalB == null && totalA != null) {
                    return -1;
                }

                if (totalA != null && totalB != null) {
                    int comparacion = totalA.compareTo(totalB);

                    if (comparacion != 0) {
                        return comparacion;
                    }
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
        String estadosEnvio = ConfiguracionComprasHelper.leerObligatoria(ConfiguracionComprasHelper.CONFIG_ESTADOS_ENVIO);
        String estadoEnviado = ConfiguracionComprasHelper.resolverEstadoEnvio("ENVIADO", estadosEnvio);
        String estadoCotizado = ConfiguracionComprasHelper.resolverEstadoEnvio("COTIZADO", estadosEnvio);
        boolean enviado = false;
        for (PrestadorCotizacion p : BusquedaRequerimientoCompraServiceUtil
                .listarPrestadoresEnviados(r.getIdRequerimientoCompra())) {
            if (p.getIdPrestador() == idPrestador
                    && (estadoEnviado.equals(p.getEstadoEnvio())
                        || estadoCotizado.equals(p.getEstadoEnvio()))) {
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
        if (c.getIdComparativa() == 0) {
            aplicarDefaultsConfigurados(c);
        }
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
        StringBuilder prestacionesEsperadas = new StringBuilder();
        for (RequerimientoCompraDetalle item : r.getDetalles()) {
            if (prestacionesEsperadas.length() > 0) {
                prestacionesEsperadas.append(',');
            }
            prestacionesEsperadas.append(item.getIdPrestacionInt());
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
        c.setPrestacionesEsperadas(prestacionesEsperadas.toString());
        calcular(c);
    }

    public void guardar(List<RequerimientoCompraComparativa> lista,
            Map<String, String> entrada, String usuario) throws Exception {
        ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx =
                ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            guardar(tx, lista, entrada, usuario);
            commitIniciado = true;
            tx.commit();
        } catch (Exception error) {
            try {
                tx.rollback();
            } catch (Exception rollbackError) {
                com.liferay.portal.kernel.log.LogFactoryUtil.getLog(RequerimientoCompraComparativaHelper.class)
                        .error("No se pudo confirmar el rollback de la comparativa.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado de la comparativa. Actualice la pantalla antes de reintentar.", error);
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar el resultado de la comparativa. Verifique sus datos antes de reintentar.", error);
            }
            if (error instanceof java.sql.SQLException) {
                com.liferay.portal.kernel.log.LogFactoryUtil.getLog(RequerimientoCompraComparativaHelper.class)
                        .error("No se pudo persistir la comparativa.", error);
                throw new Exception("No se pudo guardar la comparativa por un error de persistencia.", error);
            }
            throw error;
        } finally {
            tx.cerrar();
        }
    }

    public void guardar(ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx,
            List<RequerimientoCompraComparativa> lista,
            Map<String, String> entrada, String usuario) throws Exception {
        if (usuario == null || usuario.trim().length() == 0 || usuario.length() > 100) {
            throw new IllegalArgumentException("No se pudo determinar el usuario de auditoría.");
        }
        if (tx == null || lista == null) {
            throw new IllegalArgumentException("No se obtuvo la transaccion de la comparativa.");
        }
        if (lista.isEmpty()) {
            return;
        }
        String estados = tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
        for (RequerimientoCompraComparativa comparativa : lista) {
            validarGuardadoEnTransaccion(tx, comparativa, estados);
        }
        aplicarEntrada(tx, lista, entrada);
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
        tx.guardarDatosComparativas(lista, vaciados, usuario);
    }

    public void guardarDatosValidados(List<RequerimientoCompraComparativa> lista,
            List<Integer> vaciados, String usuario) throws Exception {
        ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx =
                ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.abrirTransaccion();
        boolean commitIniciado = false;
        try {
            String estados = tx.getConfiguracionCompra("COMPRAS_ESTADOS_IDS");
            for (RequerimientoCompraComparativa comparativa : lista) {
                validarGuardadoEnTransaccion(tx, comparativa, estados);
            }
            tx.guardarDatosComparativas(lista, vaciados, usuario);
            commitIniciado = true;
            tx.commit();
        } catch (Exception error) {
            try { tx.rollback(); }
            catch (Exception rollbackError) {
                com.liferay.portal.kernel.log.LogFactoryUtil.getLog(RequerimientoCompraComparativaHelper.class)
                        .error("No se pudo confirmar el rollback de la comparativa.", rollbackError);
                throw new Exception("No se pudo confirmar el resultado de la comparativa. Verifique antes de reintentar.", error);
            }
            if (commitIniciado) {
                throw new Exception("No se pudo confirmar el resultado de la comparativa. Verifique antes de reintentar.", error);
            }
            if (error instanceof java.sql.SQLException) {
                com.liferay.portal.kernel.log.LogFactoryUtil.getLog(RequerimientoCompraComparativaHelper.class)
                        .error("No se pudo persistir la comparativa.", error);
                throw new Exception("No se pudo guardar la comparativa por un error de persistencia.", error);
            }
            throw error;
        } finally { tx.cerrar(); }
    }

    private void validarGuardadoEnTransaccion(
            ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx,
            RequerimientoCompraComparativa comparativa, String estados) throws Exception {
        if (comparativa == null || comparativa.getIdRequerimiento() <= 0
                || comparativa.getPrestacionesEsperadas() == null
                || !comparativa.getPrestacionesEsperadas().matches("[1-9][0-9]*(,[1-9][0-9]*)*")) {
            throw new IllegalArgumentException("No se pudo validar la lista de prestaciones. Actualice la pantalla.");
        }
        java.util.Set<Integer> esperadas = new java.util.HashSet<Integer>();
        String[] tokens = comparativa.getPrestacionesEsperadas().split(",");
        for (int i = 0; i < tokens.length; i++) {
            try {
                if (!esperadas.add(Integer.valueOf(tokens[i]))) {
                    throw new IllegalArgumentException("La lista de prestaciones tiene duplicados.");
                }
            } catch (NumberFormatException error) {
                throw new IllegalArgumentException("La lista de prestaciones no es valida.");
            }
        }
        RequerimientoCompra actual = tx.bloquearYObtenerRequerimiento(comparativa.getIdRequerimiento());
        if (actual == null || !actual.isActivo() || actual.getEstado()
                != ConfiguracionComprasHelper.resolverEstado("A_COTIZAR", estados)) {
            throw new IllegalArgumentException("La comparativa ya no puede modificarse en el estado actual.");
        }
        if (tx.tieneCreacionReclamoPrestacional(comparativa.getIdRequerimiento())) {
            throw new IllegalArgumentException("El requerimiento ya tiene una creacion de RP iniciada.");
        }
        if (actual.getDetalles() == null || actual.getDetalles().size() != esperadas.size()) {
            throw new IllegalArgumentException("Las prestaciones cambiaron durante la carga. Actualice la pantalla.");
        }
        java.util.Set<Integer> vigentes = new java.util.HashSet<Integer>();
        for (RequerimientoCompraDetalle detalle : actual.getDetalles()) {
            if (detalle == null || !esperadas.contains(detalle.getIdPrestacion())
                    || !vigentes.add(detalle.getIdPrestacion())) {
                throw new IllegalArgumentException("Las prestaciones cambiaron durante la carga. Actualice la pantalla.");
            }
        }
    }

    public void aplicarEntrada(List<RequerimientoCompraComparativa> lista,
            Map<String, String> entrada) {
        aplicarEntrada(null, lista, entrada);
    }

    public void aplicarEntrada(ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx,
            List<RequerimientoCompraComparativa> lista, Map<String, String> entrada) {
        Map<String, List<String>> opciones = cargarOpciones(tx);
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
                        opciones.get("PAGO"), false, "Forma de pago")));
                c.setPlazoEntrega(opcion(entrada.get("plazo" + sufijo),
                        opciones.get("PLAZO"), true, "Plazo de entrega"));
                String validez = opcion(entrada.get("validez" + sufijo),
                        opciones.get("VALIDEZ"), true, "Validez");
                c.setValidezPresupuesto(validez == null ? null : Integer.valueOf(validez));
                c.setEnvio(opcion(entrada.get("envio" + sufijo),
                        opciones.get("ENVIO"), true, "Envío"));
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

    public static Map<String, List<String>> cargarOpciones() {
        return cargarOpciones(null);
    }

    private static Map<String, List<String>> cargarOpciones(
            ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx) {
        Map<String, List<String>> resultado = new java.util.LinkedHashMap<String, List<String>>();
        for (String campo : new String[] {"PAGO", "PLAZO", "VALIDEZ", "ENVIO"}) {
            String clave = "COMPRAS_COMPARATIVA_" + campo;
            List<String> opciones = ConfiguracionComprasHelper.resolverListaOpciones(clave, leerConfiguracion(tx, clave));
            if ("PAGO".equals(campo) || "VALIDEZ".equals(campo)) {
                for (String opcion : opciones) {
                    try {
                        if (!opcion.matches("[1-9][0-9]*") || Integer.parseInt(opcion) <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException error) {
                        throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave, error);
                    }
                }
            }
            resultado.put(campo, opciones);
        }
        return resultado;
    }

    public static void aplicarDefaultsConfigurados(RequerimientoCompraComparativa comparativa) {
        Map<String, List<String>> opciones = cargarOpciones();
        String clave = "COMPRAS_COMPARATIVA_DEFAULTS";
        Map<String, String> defaults = ConfiguracionComprasHelper.parsearPares(clave, leerConfiguracion(null, clave));
        if (defaults.size() != opciones.size()) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave);
        }
        for (String campo : opciones.keySet()) {
            if (!opciones.get(campo).contains(defaults.get(campo))) {
                throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave);
            }
        }
        comparativa.setFormaPago(Integer.valueOf(defaults.get("PAGO")));
        comparativa.setPlazoEntrega(defaults.get("PLAZO"));
        comparativa.setValidezPresupuesto(Integer.valueOf(defaults.get("VALIDEZ")));
        comparativa.setEnvio(defaults.get("ENVIO"));
    }

    private static String leerConfiguracion(
            ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil.Transaccion tx, String clave) {
        if (tx == null) {
            return ConfiguracionComprasHelper.leerObligatoria(clave);
        }
        try {
            return tx.getConfiguracionCompra(clave);
        } catch (Exception error) {
            throw new ConfiguracionComprasHelper.ConfiguracionInvalidaException(clave, error);
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
        if (fecha.after(new Date())) {
            throw new IllegalArgumentException(
                    "La fecha del presupuesto no puede ser posterior al día de hoy.");
        }
        return fecha;
    }

    private String opcion(String valor, List<String> opciones, boolean vacio, String campo) {
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
        // Datos ya cargados, separados solamente para distribuir el documento.
        fila.put("idPrestacion", d == null ? "" : String.valueOf(d.getIdPrestacion()));
        for (int i = 0; d != null && i < r.getDetalles().size(); i++) {
            RequerimientoCompraDetalle item = r.getDetalles().get(i);
            if (item.getIdPrestacionInt() == d.getIdPrestacion()) {
                fila.put("ordenItem", String.valueOf(i));
                fila.put("unidad", texto(item.getCantidad()));
                break;
            }
        }
        fila.put("adjudicado", String.valueOf(c.getIdPrestador() == r.getIdPrestadorAdjudicadoInt()));
        fila.put("neto", texto(c.getNeto()));
        fila.put("iva", texto(c.getIva()));
        fila.put("iibb", texto(c.getIibb()));
        fila.put("total", texto(c.getTotal()));
        fila.put("incompleto", String.valueOf(c.getIncompleto()));
        fila.put("pago", texto(c.getFormaPago()) + " días");
        fila.put("envio", texto(c.getEnvio()));
        fila.put("plazo", texto(c.getPlazoEntrega()));
        fila.put("fecha", texto(c.getFechaPresupuesto()));
        fila.put("validez", texto(c.getValidezPresupuesto()));
        fila.put("totales", "Neto: " + texto(c.getNeto())
                + "   IVA (importe): " + texto(c.getIva())
                + "   IIBB (importe): " + texto(c.getIibb())
                + (c.getIncompleto() ? "   Total parcial: " : "   Total: ") + texto(c.getTotal()));
        return fila;
    }

    public List<Map<String, ?>> filasReportePdf(
            RequerimientoCompra r,
            List<RequerimientoCompraComparativa> lista,
            Map<String, Map<String, ?>> proveedores) {

        List<Map<String, ?>> datos = filasPdf(r, lista);
        TreeMap<Integer, Map<String, ?>> items =
                new TreeMap<Integer, Map<String, ?>>();

        for (Map<String, ?> dato : datos) {
            String prestador = (String) dato.get("idPrestador");
            proveedores.put(prestador, dato);

            if (!"".equals(dato.get("idPrestacion"))) {
                Integer orden =
                        Integer.valueOf((String) dato.get("ordenItem"));

                if (!items.containsKey(orden)) {
                    Map<String, Object> item =
                            new HashMap<String, Object>();

                    item.put("tipo", "detalle");
                    item.put(
                            "etiqueta",
                            dato.get("codigo") + " - " + dato.get("prestacion"));
                    item.put("unidad", dato.get("unidad"));
                    item.put(
                            "proveedores",
                            new HashMap<String, Map<String, ?>>());

                    items.put(orden, item);
                }

                ((Map<String, Map<String, ?>>)
                        items.get(orden).get("proveedores"))
                        .put(prestador, dato);
            }
        }

        List<Map<String, ?>> filas =
                new ArrayList<Map<String, ?>>(items.values());

        String[] campos = {
                "neto",
                "iva",
                "iibb",
                "total",
                "pago",
                "envio",
                "plazo",
                "fecha",
                "validez",
                "dictamen"
        };

        String[] etiquetas = {
                "Subtotal (sin IVA)",
                "IVA",
                "IIBB",
                "Total",
                "Forma de pago",
                "Envío a obra social - Beneficiario - Delegación",
                "Plazo de entrega",
                "Fecha del presupuesto",
                "Validez del presupuesto (hs)",
                "Dictamen de Auditoría Médica/Compras"
        };

        for (int i = 0; i < campos.length; i++) {
            Map<String, Object> fila =
                    new HashMap<String, Object>();

            fila.put(
                    "tipo",
                    i == 9 ? "dictamen"
                            : i == 3 ? "total"
                            : i < 4 ? "resumen"
                            : "condicion");

            fila.put("etiqueta", etiquetas[i]);
            fila.put("unidad", "");

            Map<String, Object> valores =
                    new HashMap<String, Object>();

            for (String prestador : proveedores.keySet()) {
                Map<String, ?> proveedor =
                        proveedores.get(prestador);

                Map<String, Object> valor =
                        new HashMap<String, Object>();

                valor.put(
                        "valor",
                        i == 9 ? "" : proveedor.get(campos[i]));

                valor.put(
                        "incompleto",
                        proveedor.get("incompleto"));

                valores.put(prestador, valor);
            }

            fila.put("proveedores", valores);
            filas.add(fila);
        }

        return filas;
    }
}
