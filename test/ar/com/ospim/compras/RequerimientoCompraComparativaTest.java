package ar.com.ospim.compras;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

public class RequerimientoCompraComparativaTest extends TestCase {

    private final RequerimientoCompraComparativaHelper helper =
            new RequerimientoCompraComparativaHelper();

    public void testNulosYImportesCero() {
        assertNull(RequerimientoCompraComparativaHelper.cantidad(""));
        assertNull(RequerimientoCompraComparativaHelper.importe(" ", "Importe"));
        assertEquals(Integer.valueOf(0), RequerimientoCompraComparativaHelper.cantidad("0"));
        assertEquals(new BigDecimal("0.00"),
                RequerimientoCompraComparativaHelper.importe("0", "Importe"));
        assertEquals(new BigDecimal("123.45"),
                RequerimientoCompraComparativaHelper.importe("123,45", "Importe"));
    }

    public void testCantidadesIndependientesYTributosComoImportes() {
        RequerimientoCompraComparativa a = cabecera();
        RequerimientoCompraComparativa b = cabecera();
        a.getDetalles().add(detalle(3, "10.25"));
        b.getDetalles().add(detalle(7, "10.25"));
        a.setIva(new BigDecimal("6.46"));
        a.setIibb(new BigDecimal("1.00"));
        helper.calcular(a);
        helper.calcular(b);
        assertEquals(new BigDecimal("30.75"), a.getNeto());
        assertEquals(new BigDecimal("38.21"), a.getTotal());
        assertEquals(new BigDecimal("71.75"), b.getTotal());
    }

    public void testParcialesNoInventanCeros() {
        RequerimientoCompraComparativa c = cabecera();
        c.getDetalles().add(detalle(null, "10.25"));
        helper.calcular(c);
        assertNull(c.getDetalles().get(0).getSubtotal());
        assertNull(c.getTotal());
        assertTrue(c.getIncompleto());
        c.getDetalles().add(detalle(0, "12.50"));
        helper.calcular(c);
        assertEquals(new BigDecimal("0.00"), c.getTotal());
        assertTrue(c.getIncompleto());
    }

    public void testEntradaConFechaOpcionesYCamposVacios() {
        RequerimientoCompraComparativa c = cabecera();
        c.getDetalles().add(detalle(null, null));
        Map<String, String> entrada = entrada();
        entrada.put("fecha_10", "23/09/2026");
        entrada.put("envio_10", "Delegación");
        entrada.put("cantidad_10_100", "0");
        entrada.put("importe_10_100", "");
        helper.aplicarEntrada(Arrays.asList(c), entrada);
        assertEquals("23/09/2026", RequerimientoCompraComparativaHelper.texto(c.getFechaPresupuesto()));
        assertEquals(Integer.valueOf(30), c.getFormaPago());
        assertNull(c.getPlazoEntrega());
        assertNull(c.getIva());
        assertEquals(Integer.valueOf(0), c.getDetalles().get(0).getCantidad());
        assertNull(c.getDetalles().get(0).getImporteUnitario());
    }

    public void testRechazaDatosFueraDeContrato() {
        String[] campos = {"fecha_10", "pago_10", "plazo_10", "validez_10", "envio_10", "iva_10", "prestador_10"};
        String[] valores = {"31/02/2026", "90", "12/24", "72", "Otro", "1.234,50", "11"};
        for (int i = 0; i < campos.length; i++) {
            Map<String, String> entrada = entrada();
            entrada.put(campos[i], valores[i]);
            try {
                helper.aplicarEntrada(Arrays.asList(cabecera()), entrada);
                fail("Aceptó " + campos[i]);
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().length() > 0);
            }
        }
        try {
            RequerimientoCompraComparativaHelper.cantidad("1.5");
            fail("Aceptó cantidad fraccionaria");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testRechazaPrestacionesDuplicadasOSinId() {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();
        d.setIdPrestacion(Integer.valueOf(100));
        try {
            helper.validarPrestaciones(Arrays.asList(d, d));
            fail("Aceptó duplicados");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
        d.setIdPrestacion(null);
        try {
            helper.validarPrestaciones(Arrays.asList(d));
            fail("Aceptó ítem sin nomenclador");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testPdfUsaSoloDatosGuardadosYMismaCuenta() {
        RequerimientoCompraComparativa c = cabecera();
        RequerimientoCompraComparativaDetalle d = detalle(2, "0.10");
        d.setIdDetalle(1);
        c.getDetalles().add(d);
        c.getDetalles().add(detalle(null, null));
        c.setIva(new BigDecimal("0.03"));
        helper.calcular(c);
        List<Map<String, ?>> filas = helper.filasPdf(new RequerimientoCompra(), Arrays.asList(c));
        assertEquals(1, filas.size());
        assertEquals("0.20", filas.get(0).get("subtotal"));
        assertTrue(filas.get(0).get("totales").toString().endsWith("Total: 0.23"));
        c.setIdComparativa(0);
        try {
            helper.filasPdf(new RequerimientoCompra(), Arrays.asList(c));
            fail("Imprimió comparativa sin guardar");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testJasperMultipaginaConDatosJava() throws Exception {
        RequerimientoCompraComparativa c = cabecera();
        for (int i = 0; i < 90; i++) {
            RequerimientoCompraComparativaDetalle d = detalle(i, "123456789.99");
            d.setIdDetalle(i + 1);
            d.setPrestacion("Prestación de prueba " + i
                    + ": descripción extensa para comprobar el ajuste de texto y la continuidad de las filas.");
            c.getDetalles().add(d);
        }
        c.setIva(new BigDecimal("210.00"));
        helper.calcular(c);
        RequerimientoCompra r = new RequerimientoCompra();
        List<Map<String, ?>> filas = helper.filasPdf(r, Arrays.asList(c));
        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put("REQUERIMIENTO", "PRUEBA");
        JasperPrint print = JasperFillManager.fillReport(
                JasperCompileManager.compileReport(
                    "ext-web/docroot/WEB-INF/classes/jasper/compras/requerimiento_comparativa.jrxml"),
                parametros, new JRMapCollectionDataSource(filas));
        assertTrue(print.getPages().size() > 1);
        byte[] pdf = JasperExportManager.exportReportToPdf(print);
        assertTrue(pdf.length > 1000);
        assertEquals("%PDF", new String(pdf, 0, 4, "ISO-8859-1"));
        String salida = System.getProperty("comparativa.pdf.prueba");
        if (salida != null) {
            JasperExportManager.exportReportToPdfFile(print, salida);
        }
    }

    private RequerimientoCompraComparativa cabecera() {
        RequerimientoCompraComparativa c = new RequerimientoCompraComparativa();
        c.setIdComparativa(1);
        c.setIdRequerimiento(1);
        c.setIdPrestador(10);
        c.setPrestador("Prestador de prueba");
        return c;
    }

    private RequerimientoCompraComparativaDetalle detalle(Integer cantidad, String importe) {
        RequerimientoCompraComparativaDetalle d = new RequerimientoCompraComparativaDetalle();
        d.setIdPrestacion(100);
        d.setCodigo("P100");
        d.setPrestacion("Prestación de prueba");
        d.setCantidad(cantidad);
        d.setImporteUnitario(importe == null ? null : new BigDecimal(importe));
        return d;
    }

    private Map<String, String> entrada() {
        Map<String, String> entrada = new HashMap<String, String>();
        entrada.put("prestador_10", "10");
        entrada.put("pago_10", "30");
        return entrada;
    }

    public void testNoGuardaDetalleVacioYConservaCero() throws Exception {
        JdbcPrueba jdbc = new JdbcPrueba(false);
        try {
            RequerimientoCompraComparativa c = cabecera();
            c.getDetalles().add(detalle(null, null));
            helper.guardar(Arrays.asList(c), entrada(), "prueba");
            assertEquals(0, jdbc.detallesGuardados);
            assertTrue(jdbc.commit);
            assertFalse(jdbc.rollback);
        } finally {
            jdbc.cerrar();
        }
        jdbc = new JdbcPrueba(false);
        try {
            RequerimientoCompraComparativa c = cabecera();
            c.getDetalles().add(detalle(null, null));
            Map<String, String> e = entrada();
            e.put("cantidad_10_100", "0");
            e.put("importe_10_100", "0");
            helper.guardar(Arrays.asList(c), e, "prueba");
            assertEquals(1, jdbc.detallesGuardados);
            assertTrue(jdbc.commit);
        } finally {
            jdbc.cerrar();
        }
    }

    public void testVaciarDetalleExistenteNoCreaOtraVersion() throws Exception {
        JdbcPrueba jdbc = new JdbcPrueba(false);
        try {
            RequerimientoCompraComparativa c = cabecera();
            RequerimientoCompraComparativaDetalle d = detalle(2, "15.00");
            d.setIdDetalle(99);
            c.getDetalles().add(d);
            helper.guardar(Arrays.asList(c), entrada(), "prueba");
            assertEquals(1, jdbc.cabecerasActualizadas);
            assertEquals(0, jdbc.cabecerasInsertadas);
            assertEquals(0, jdbc.detallesGuardados);
            assertEquals(1, jdbc.detallesVaciados);
            assertTrue(jdbc.commit);
        } finally {
            jdbc.cerrar();
        }
    }

    public void testFalloDeDetalleRevierteTodaLaComparativa() throws Exception {
        JdbcPrueba jdbc = new JdbcPrueba(true);
        try {
            RequerimientoCompraComparativa c = cabecera();
            c.getDetalles().add(detalle(null, null));
            Map<String, String> e = entrada();
            e.put("cantidad_10_100", "2");
            e.put("importe_10_100", "10");
            try {
                helper.guardar(Arrays.asList(c), e, "prueba");
                fail("No propagó el fallo de persistencia");
            } catch (java.sql.SQLException expected) {
                assertTrue(jdbc.rollback);
                assertFalse(jdbc.commit);
                assertEquals(1, jdbc.cabecerasActualizadas);
            }
        } finally {
            jdbc.cerrar();
        }
    }

    private static class JdbcPrueba implements java.lang.reflect.InvocationHandler {
        private final java.lang.reflect.Field fuente;
        private final Object anterior;
        private final boolean fallar;
        private boolean commit;
        private boolean rollback;
        private int detallesGuardados;
        private int detallesVaciados;
        private int cabecerasActualizadas;
        private int cabecerasInsertadas;

        private JdbcPrueba(boolean fallar) throws Exception {
            this.fallar = fallar;
            fuente = ar.com.ospim.util.ConnectionHelper.class.getDeclaredField("datasource");
            fuente.setAccessible(true);
            anterior = fuente.get(null);
            fuente.set(null, proxy(com.mchange.v2.c3p0.PooledDataSource.class, this));
        }

        private void cerrar() throws Exception { fuente.set(null, anterior); }

        private Object proxy(Class<?> tipo, java.lang.reflect.InvocationHandler handler) {
            return java.lang.reflect.Proxy.newProxyInstance(
                    tipo.getClassLoader(), new Class[] {tipo}, handler);
        }

        public Object invoke(Object obj, java.lang.reflect.Method method, Object[] args)
                throws Throwable {
            String nombre = method.getName();
            if ("getNumBusyConnections".equals(nombre)) { return Integer.valueOf(0); }
            if ("getConnection".equals(nombre)) { return proxy(java.sql.Connection.class, this); }
            if ("commit".equals(nombre)) { commit = true; }
            if ("rollback".equals(nombre)) { rollback = true; }
            if ("prepareStatement".equals(nombre)) {
                final String sql = (String) args[0];
                return proxy(java.sql.PreparedStatement.class, new java.lang.reflect.InvocationHandler() {
                    public Object invoke(Object statement, java.lang.reflect.Method operation, Object[] params)
                            throws Throwable {

                        if ("executeQuery".equals(operation.getName())) {
                            cabecerasInsertadas++;
                            return proxy(java.sql.ResultSet.class, new java.lang.reflect.InvocationHandler() {
                                private boolean leido;
                                public Object invoke(Object rs, java.lang.reflect.Method m, Object[] a) {
                                    if ("next".equals(m.getName())) {
                                        boolean siguiente = !leido;
                                        leido = true;
                                        return Boolean.valueOf(siguiente);
                                    }
                                    if ("getInt".equals(m.getName())) { return Integer.valueOf(42); }
                                    return null;
                                }
                            });
                        }
                        if ("executeUpdate".equals(operation.getName())) {
                            if (sql.startsWith("DELETE")) {
                                detallesVaciados++;
                            } else if (sql.contains("requerimiento_comparativa_detalle")) {
                                detallesGuardados++;
                                if (fallar) { throw new java.sql.SQLException("Fallo de prueba"); }
                            } else if (sql.startsWith("UPDATE")) {
                                cabecerasActualizadas++;
                            } else {
                                cabecerasInsertadas++;
                            }
                            return Integer.valueOf(1);
                        }
                        return null;
                    }
                });
            }
            return null;
        }
    }

    public void testAltaUsaLaCabeceraGenerada() throws Exception {
        JdbcPrueba jdbc = new JdbcPrueba(false);
        try {
            RequerimientoCompraComparativa c = cabecera();
            c.setIdComparativa(0);
            c.getDetalles().add(detalle(null, null));
            Map<String, String> e = entrada();
            e.put("cantidad_10_100", "3");
            e.put("importe_10_100", "10.50");
            helper.guardar(Arrays.asList(c), e, "prueba");
            assertEquals(42, c.getIdComparativa());
            assertEquals(1, jdbc.cabecerasInsertadas);
            assertEquals(1, jdbc.detallesGuardados);
            assertTrue(jdbc.commit);
        } finally {
            jdbc.cerrar();
        }
    }

    public void testPermisosRechazanUsuarioAnonimo() throws Exception {
        for (boolean editar : new boolean[] {false, true}) {
            try {
                ar.com.ospim.compras.requerimientos.action.RequerimientoCompraComparativaAction
                        .validarPermiso(null, editar);
                fail("Permitió acceso anónimo");
            } catch (IllegalArgumentException expected) {
                assertNotNull(expected.getMessage());
            }
        }
    }
}
