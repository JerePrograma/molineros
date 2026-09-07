package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.action.BuscarRequerimientosComprasAction;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper.Criterios;
import ar.com.ospim.compras.requerimientos.reportes.ReporteRequerimientosCompraExcel;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.servlets.XLSServlet;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import com.mchange.v2.c3p0.PooledDataSource;
import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionMapping;

/** Ejecuta Action, servicios JDBC, servlet y POI con datos sinteticos, sin red ni BD real. */
public final class ExportarRequerimientosCompraTest {
    private static int checks;
    private static User currentUser;
    private static final List<String> calls = new ArrayList<String>();
    private static final List<Map<Integer, Object>> bindings = new ArrayList<Map<Integer, Object>>();
    private static List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
    private static List<Map<String, Object>> awards = new ArrayList<Map<String, Object>>();
    private static boolean failAwards;
    private static int closedConnections;
    private static final Locale ES = new Locale("es", "AR");

    public static void main(String[] args) throws Exception {
        // Instalar todos los dobles ANTES de ejecutar codigo que pueda consultar persistencia.
        Field dataSource = ConnectionHelper.class.getDeclaredField("datasource");
        dataSource.setAccessible(true);
        dataSource.set(null, proxy(PooledDataSource.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                if ("getConnection".equals(m.getName())) return connection();
                return empty(m.getReturnType());
            }
        }));
        currentUser = user(WebKeysCompras.ROL_VIEW_COMPRAS);
        final Properties language = new Properties();
        InputStream labels = new FileInputStream("ext-impl/src/content/Language-ext.properties");
        try { language.load(labels); } finally { labels.close(); }
        new LanguageUtil().setLanguage(proxy(Language.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                if ("get".equals(m.getName())) {
                    String key = (String) a[a.length - 1];
                    return language.getProperty(key, key);
                }
                return empty(m.getReturnType());
            }
        }));
        new PortalUtil().setPortal(proxy(Portal.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                if ("getUser".equals(m.getName())) return currentUser;
                if ("getHttpServletRequest".equals(m.getName())) {
                    Request r = (Request) Proxy.getInvocationHandler(a[0]);
                    return r.http();
                }
                if ("getPortletId".equals(m.getName())) return "compras";
                if ("getPortletNamespace".equals(m.getName())) return "_compras_";
                return empty(m.getReturnType());
            }
        }));

        filtros();
        contextos();
        contextosConcurrentes();
        descarga(args.length == 0 ? ".codex/artifacts/exportar-requerimientos/muestra.xlsx" : args[0]);
        System.out.println("EXPORTAR_REQUERIMIENTOS_OK checks=" + checks
                + " (dobles JDBC/HTTP, POI real; no prueba SQL instalada ni UI real)");
    }

    private static void filtros() throws Exception {
        BuscarRequerimientosComprasAction action = new BuscarRequerimientosComprasAction();
        Method parser = action.getClass().getDeclaredMethod("getFiltroFromRequest", RenderRequest.class);
        parser.setAccessible(true);
        for (int estado = 0; estado < 2; estado++) {
            for (int sector = 0; sector < 2; sector++) {
                for (int surge = 0; surge < 3; surge++) {
                    for (int fecha = 0; fecha < 3; fecha++) {
                        Request req = new Request(session());
                        req.params.put(estado == 0 ? "estado" : "id_estado", estado == 0 ? "0" : "3");
                        req.params.put(sector == 0 ? "sector_id" : "id_sector", sector == 0 ? "0" : "2");
                        if (surge > 0) req.params.put("surge", surge == 1 ? "true" : "false");
                        req.params.put("afiliado_cuil_titular", "20-12345678-9");
                        req.params.put("afiliado_int", "0");
                        req.params.put("id_tercerizadora", "omi");
                        req.params.put("texto", "Ñ & prueba");
                        req.params.put("afiliado_nombre", "auxiliar no aplicado");
                        if (fecha > 0) fecha(req, "fechaAltaDesde", "1", "0", "2026");
                        if (fecha > 1) fecha(req, "fechaAltaHasta", "31", "0", "2026");
                        RequerimientoCompraFiltro f = (RequerimientoCompraFiltro) parser.invoke(action, req.render());
                        calls.clear(); bindings.clear();
                        BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(f, false);
                        Map<Integer, Object> bind = bindings.get(0);
                        eq("estado", estado == 0 ? null : Integer.valueOf(3), bind.get(1));
                        eq("sector", sector == 0 ? null : Integer.valueOf(2), bind.get(2));
                        eq("CUIL", "20-12345678-9", bind.get(3));
                        eq("integrante cero", Integer.valueOf(0), bind.get(4));
                        eq("tercerizadora", "OMI", bind.get(5));
                        eq("SURGE tres estados", surge == 0 ? null : Boolean.valueOf(surge == 1), bind.get(7));
                        eq("texto", "Ñ & prueba", bind.get(8));
                        eq("fecha desde", Boolean.valueOf(fecha > 0), Boolean.valueOf(bind.get(9) != null));
                        eq("fecha hasta", Boolean.valueOf(fecha > 1), Boolean.valueOf(bind.get(10) != null));
                        eq("auxiliar no usado", null, f.getAfiliadoNombre());
                    }
                }
            }
        }
        String[] invalidKeys = {"estado", "id_estado", "sector_id", "id_sector", "afiliado_int", "surge", "afiliado_cuil_titular"};
        for (int i = 0; i < invalidKeys.length; i++) {
            Request r = new Request(session());
            r.params.put(invalidKeys[i], "invalido");
            invalidParser(parser, action, r);
        }
        Request r = new Request(session());
        r.params.put("afiliado_int", "2147483648"); invalidParser(parser, action, r);
        r = new Request(session());
        r.params.put("fechaAltaDesdeDia", "1"); invalidParser(parser, action, r);
        r = new Request(session());
        fecha(r, "fechaAltaDesde", "31", "1", "2026"); invalidParser(parser, action, r);
        r = new Request(session());
        fecha(r, "fechaAltaDesde", "2", "0", "2026");
        fecha(r, "fechaAltaHasta", "1", "0", "2026"); invalidParser(parser, action, r);
        r = new Request(session());
        r.params.put("surge", "false");
        RequerimientoCompraFiltro f = (RequerimientoCompraFiltro) parser.invoke(action, r.render());
        eq("false no es ausencia", Boolean.FALSE, f.getSurge());
        f.setIdEstado(Integer.valueOf(WebKeysCompras.ESTADO_COTIZADO));
        calls.clear(); bindings.clear();
        BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(f, true);
        eq("Cotizados hace dos consultas", Integer.valueOf(2), Integer.valueOf(calls.size()));
        eq("Cotizados preserva estado", Integer.valueOf(WebKeysCompras.ESTADO_COTIZADO), f.getIdEstado());
        eq("incluye RP", Integer.valueOf(WebKeysCompras.ESTADO_RECLAMO_RP), bindings.get(1).get(1));
    }

    private static void contextos() throws Exception {
        HttpSession s = session();
        RequerimientoCompraFiltro f = new RequerimientoCompraFiltro();
        f.setIdEstado(Integer.valueOf(1));
        f.setFechaAltaDesde(new java.util.Date(1000));
        String a = ExportarRequerimientosCompraHelper.publicar(s, 7, f, false, true, ES);
        f.setIdEstado(Integer.valueOf(2));
        String b = ExportarRequerimientosCompraHelper.publicar(s, 7, f, false, true, ES);
        f.getFechaAltaDesde().setTime(2000);
        Criterios ca = ExportarRequerimientosCompraHelper.obtener(s, 7, a);
        eq("pestana A", Integer.valueOf(1), ca.getFiltro().getIdEstado());
        eq("pestana B", Integer.valueOf(2), ExportarRequerimientosCompraHelper.obtener(s, 7, b).getFiltro().getIdEstado());
        eq("copia defensiva fecha", Long.valueOf(1000), Long.valueOf(ca.getFiltro().getFechaAltaDesde().getTime()));
        ca.getFiltro().setIdEstado(Integer.valueOf(99));
        eq("lectura no muta contexto", Integer.valueOf(1), ca.getFiltro().getIdEstado());
        try { ExportarRequerimientosCompraHelper.obtener(s, 8, a); fail("otro usuario"); }
        catch (IllegalArgumentException expected) { checks++; }
        try { ExportarRequerimientosCompraHelper.obtener(session(), 7, a); fail("otra sesion"); }
        catch (IllegalArgumentException expected) { checks++; }
        for (int i = 0; i < 20; i++) ExportarRequerimientosCompraHelper.publicar(s, 7, f, false, true, ES);
        try { ExportarRequerimientosCompraHelper.obtener(s, 7, a); fail("contextos acotados"); }
        catch (IllegalArgumentException expected) { checks++; }
    }

    private static void contextosConcurrentes() throws Exception {
        final Map<String, Object> valores = new java.util.concurrent.ConcurrentHashMap<String, Object>();
        HttpSession base = proxy(HttpSession.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) throws Exception {
                if ("getAttribute".equals(m.getName())) {
                    Object valor = valores.get(a[0]);
                    // Amplifica la carrera de inicializacion entre wrappers de la misma sesion.
                    Thread.sleep(40);
                    return valor;
                }
                if ("setAttribute".equals(m.getName())) {
                    valores.put((String) a[0], a[1]);
                }
                return empty(m.getReturnType());
            }
        });
        final HttpSession[] wrappers = new HttpSession[] {
            new com.liferay.util.servlet.SharedSessionWrapper(base, new HashMap<String, Object>()),
            new com.liferay.util.servlet.SharedSessionWrapper(base, new HashMap<String, Object>())
        };
        final String[] tokens = new String[2];
        final java.util.concurrent.CountDownLatch inicio = new java.util.concurrent.CountDownLatch(1);
        Thread[] hilos = new Thread[2];
        for (int i = 0; i < hilos.length; i++) {
            final int indice = i;
            hilos[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        inicio.await();
                        tokens[indice] = ExportarRequerimientosCompraHelper.publicar(
                                wrappers[indice], 7, new RequerimientoCompraFiltro(), false, true, ES);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            });
            hilos[i].start();
        }
        inicio.countDown();
        for (int i = 0; i < hilos.length; i++) hilos[i].join();
        for (int i = 0; i < tokens.length; i++) {
            check(ExportarRequerimientosCompraHelper.obtener(base, 7, tokens[i]) != null,
                    "dos wrappers Liferay no pierden contextos concurrentes");
        }
    }

    private static void descarga(String muestra) throws Exception {
        rows.add(row("id", 12, "afiliado_nombre_apellido", "=Ñandú & \"Prueba\"\nSegunda linea",
                "afiliado_documento_nro", "01.234.567", "cargo_ospim", 25,
                "cargo_tercerizadora", 75, "id_tercerizadora", "OMI",
                "sector_descripcion", "Prótesis", "estado_descripcion", "Cotizado",
                "surge", true, "alta_fecha", Timestamp.valueOf("2026-09-01 13:42:17")));
        rows.add(row("id", 11, "afiliado_cuil_titular", "20123456789", "afiliado_int", 0));
        awards.add(row("id_requerimiento", 12, "id_prestador", 9, "descripcion", "+Clínica Ñ & Hijos"));
        Request req = new Request(session());
        BuscarRequerimientosComprasAction action = new BuscarRequerimientosComprasAction();
        action.render(mapping(), null, null, req.render(), proxy(RenderResponse.class, new Defaults()));
        String token = (String) req.attrs.get(ExportarRequerimientosCompraHelper.TOKEN);
        check(token != null, "Action publica token tras busqueda exitosa");
        req.params.put("reporte", ExportarRequerimientosCompraHelper.REPORTE);
        req.params.put(ExportarRequerimientosCompraHelper.TOKEN, token);
        req.params.put("estado", "99"); // Nunca debe reemplazar el criterio guardado.
        calls.clear(); bindings.clear();
        Response response = descargar(req);
        eq("HTTP exito", Integer.valueOf(200), Integer.valueOf(response.status));
        check(response.type.indexOf("spreadsheetml.sheet") >= 0, "MIME xlsx");
        check(response.headers.get("Content-Disposition").indexOf(".xlsx") >= 0, "extension");
        eq("consulta mas dos enriquecimientos", Integer.valueOf(3), Integer.valueOf(calls.size()));
        eq("ignora filtro alterado al descargar", null, bindings.get(0).get(1));
        check(closedConnections >= calls.size(), "conexiones cerradas");
        XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.bytes.toByteArray()));
        try {
            Sheet sheet = wb.getSheetAt(0);
            eq("cantidad filas", Integer.valueOf(2), Integer.valueOf(sheet.getLastRowNum()));
            eq("columnas", Short.valueOf((short)12), Short.valueOf(sheet.getRow(0).getLastCellNum()));
            eq("cabecera final", "Prestador adjudicado", sheet.getRow(0).getCell(11).getStringCellValue());
            eq("titulo idioma", "Apellido y nombre", sheet.getRow(0).getCell(3).getStringCellValue());
            eq("orden", "12", sheet.getRow(1).getCell(0).getStringCellValue());
            eq("DNI cero", "01234567", sheet.getRow(1).getCell(4).getStringCellValue());
            eq("porcentaje", "25%", sheet.getRow(1).getCell(6).getStringCellValue());
            eq("fecha sin hora inventada", "01/09/2026", sheet.getRow(1).getCell(9).getStringCellValue());
            eq("RP", "88", sheet.getRow(1).getCell(10).getStringCellValue());
            eq("sin RP", "", sheet.getRow(2).getCell(10).getStringCellValue());
            eq("sin adjudicar", "", sheet.getRow(2).getCell(11).getStringCellValue());
            eq("prestador", "+Clínica Ñ & Hijos", sheet.getRow(1).getCell(11).getStringCellValue());
            eq("fallback", "20123456789 / 0", sheet.getRow(2).getCell(3).getStringCellValue());
            @SuppressWarnings("unchecked")
            List<RequerimientoCompra> listado = (List<RequerimientoCompra>)
                    req.attrs.get(WebKeysCompras.BUSQUEDA_REQUERIMIENTOS_COMPRA);
            @SuppressWarnings("unchecked")
            Map<Integer, RequerimientoCompraReclamoPrestacional> rp =
                    (Map<Integer, RequerimientoCompraReclamoPrestacional>) req.attrs.get(
                            WebKeysCompras.RELACIONES_RECLAMO_PRESTACIONAL_COMPRA);
            for (int i = 0; i < listado.size(); i++) {
                String[] visible = ExportarRequerimientosCompraHelper.valoresListado(
                        listado.get(i), rp.get(Integer.valueOf(listado.get(i).getIdRequerimientoCompra())), true);
                for (int c = 0; c < visible.length; c++) {
                    eq("paridad celda " + i + "/" + c, visible[c], sheet.getRow(i+1).getCell(c).getStringCellValue());
                    eq("texto nunca formula", CellType.STRING, sheet.getRow(i+1).getCell(c).getCellType());
                }
            }
        } finally { wb.close(); }
        OutputStream file = new FileOutputStream(muestra);
        try { file.write(response.bytes.toByteArray()); } finally { file.close(); }

        // Validar los cuatro prefijos peligrosos y los limites reales del formato.
        Map<Integer, RequerimientoCompraReclamoPrestacional> sinRp =
                new HashMap<Integer, RequerimientoCompraReclamoPrestacional>();
        Map<Integer, List<PrestadorCotizacion>> sinPrestador =
                new HashMap<Integer, List<PrestadorCotizacion>>();
        String[] nombres = {"=SUM(A1)", "+Prueba", "-Prueba", "@Prueba"};
        List<RequerimientoCompra> textos = new ArrayList<RequerimientoCompra>();
        for (int i = 0; i < nombres.length; i++) {
            RequerimientoCompra dato = new RequerimientoCompra();
            dato.setIdRequerimientoCompra(i + 1);
            dato.setAfiliadoNombreApellido(nombres[i]);
            textos.add(dato);
        }
        byte[] contenido = ReporteRequerimientosCompraExcel.generar(
                textos, sinRp, sinPrestador, false, ES);
        wb = new XSSFWorkbook(new ByteArrayInputStream(contenido));
        try {
            eq("sin columna RP", Short.valueOf((short)11),
                    Short.valueOf(wb.getSheetAt(0).getRow(0).getLastCellNum()));
            for (int i = 0; i < nombres.length; i++) {
                eq("prefijo como texto", CellType.STRING,
                        wb.getSheetAt(0).getRow(i+1).getCell(3).getCellType());
                eq("prefijo preservado", nombres[i],
                        wb.getSheetAt(0).getRow(i+1).getCell(3).getStringCellValue());
            }
        } finally { wb.close(); }
        char[] largo = new char[32768];
        Arrays.fill(largo, 'x');
        textos.get(0).setAfiliadoNombreApellido(new String(largo));
        try {
            ReporteRequerimientosCompraExcel.generar(textos, sinRp, sinPrestador, true, ES);
            fail("texto no debe truncarse");
        } catch (IllegalArgumentException expected) { checks++; }
        List<RequerimientoCompra> demasiados = new AbstractList<RequerimientoCompra>() {
            public int size() { return 1048576; }
            public RequerimientoCompra get(int i) { throw new AssertionError("No debe generar filas"); }
        };
        try {
            ReporteRequerimientosCompraExcel.generar(demasiados, sinRp, sinPrestador, true, ES);
            fail("filas no deben truncarse");
        } catch (IllegalArgumentException expected) { checks++; }

        awards.clear();
        awards.add(row("id_requerimiento", 12, "id_prestador", null, "descripcion", null));
        response = descargar(req);
        wb = new XSSFWorkbook(new ByteArrayInputStream(response.bytes.toByteArray()));
        try { eq("detalle sin prestador", "", wb.getSheetAt(0).getRow(1).getCell(11).getStringCellValue()); }
        finally { wb.close(); }
        awards.clear();
        awards.add(row("id_requerimiento", 12, "id_prestador", 9, "descripcion", null));
        error(descargar(req), 400, "prestador sin denominacion");
        awards.clear();
        awards.add(row("id_requerimiento", 12, "id_prestador", 9, "descripcion", "+Clinica"));
        awards.add(row("id_requerimiento", 12, "id_prestador", 10, "descripcion", "Segundo"));
        error(descargar(req), 400, "multiplicidad");
        awards.remove(awards.size()-1);
        failAwards = true; error(descargar(req), 500, "fallo SQL"); failAwards = false;
        currentUser = user("sin-permiso"); error(descargar(req), 403, "sin rol");
        currentUser = null; error(descargar(req), 403, "anonimo");
        currentUser = user(WebKeysCompras.ROL_VIEW_COMPRAS);
        check(!ar.com.ospim.util.PermissionUtil.userContainsRole(currentUser, WebKeysCompras.ROL_ABM_COMPRAS),
                "usuario consulta sin alta");
        req.params.remove(ExportarRequerimientosCompraHelper.TOKEN);
        error(descargar(req), 400, "acceso directo sin token");
        req.params.put(ExportarRequerimientosCompraHelper.TOKEN, token);

        rows.clear(); awards.clear(); calls.clear();
        response = descargar(req);
        wb = new XSSFWorkbook(new ByteArrayInputStream(response.bytes.toByteArray()));
        try { eq("cero filas solo cabecera", Integer.valueOf(0), Integer.valueOf(wb.getSheetAt(0).getLastRowNum())); }
        finally { wb.close(); }
        eq("cero filas sin enriquecimiento", Integer.valueOf(1), Integer.valueOf(calls.size()));

        for (int i = 1200; i > 0; i--) rows.add(row("id", i, "afiliado_nombre_apellido", "@Prueba " + i));
        calls.clear(); response = descargar(req);
        wb = new XSSFWorkbook(new ByteArrayInputStream(response.bytes.toByteArray()));
        try { eq("volumen sin truncar", Integer.valueOf(1200), Integer.valueOf(wb.getSheetAt(0).getLastRowNum())); }
        finally { wb.close(); }
        eq("volumen mantiene tres consultas", Integer.valueOf(3), Integer.valueOf(calls.size()));

        req.params.put("estado", "invalido"); req.attrs.clear(); calls.clear();
        action.render(mapping(), null, null, req.render(), proxy(RenderResponse.class, new Defaults()));
        eq("error Action sin token", null, req.attrs.get(ExportarRequerimientosCompraHelper.TOKEN));
        eq("error sin consulta general", Integer.valueOf(0), Integer.valueOf(calls.size()));
    }

    private static ActionMapping mapping() {
        ActionMapping mapping = new ActionMapping();
        mapping.addForwardConfig(new org.apache.struts.action.ActionForward(
                WebKeysCompras.FORWARD_COMPRAS_RESULT_SEARCH, "/resultado.jsp", false));
        return mapping;
    }
    private static Response descargar(Request req) throws Exception {
        Response response = new Response();
        new XLSServlet().doPost(req.http(), proxy(HttpServletResponse.class, response));
        return response;
    }

    private static void error(Response r, int status, String label) {
        eq(label, Integer.valueOf(status), Integer.valueOf(r.status));
        eq(label + " sin adjunto", null, r.headers.get("Content-Disposition"));
        eq(label + " sin bytes Excel", Integer.valueOf(0), Integer.valueOf(r.bytes.size()));
        check(r.text.toString().length() > 0, label + " mensaje");
    }

    private static void invalidParser(Method parser, Object action, Request req) throws Exception {
        try { parser.invoke(action, req.render()); fail("filtro invalido"); }
        catch (InvocationTargetException expected) { checks++; }
    }
    private static void fecha(Request req, String prefix, String d, String m, String y) {
        req.params.put(prefix+"Dia",d); req.params.put(prefix+"Mes",m); req.params.put(prefix+"Anio",y);
    }
    private static User user(final String roleName) {
        final Role role = proxy(Role.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                return "getName".equals(m.getName()) ? roleName : empty(m.getReturnType());
            }
        });
        return proxy(User.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                if ("getRoles".equals(m.getName())) return Arrays.asList(role);
                if ("getUserId".equals(m.getName())) return Long.valueOf(7);
                return empty(m.getReturnType());
            }
        });
    }
    private static HttpSession session() {
        final Map<String,Object> values = new HashMap<String,Object>();
        return proxy(HttpSession.class, new InvocationHandler() {
            public Object invoke(Object p, Method m, Object[] a) {
                if ("getAttribute".equals(m.getName())) return values.get(a[0]);
                if ("setAttribute".equals(m.getName())) { values.put((String)a[0],a[1]); return null; }
                return empty(m.getReturnType());
            }
        });
    }
    private static final class Request implements InvocationHandler {
        final Map<String,String> params = new HashMap<String,String>();
        final Map<String,Object> attrs = new HashMap<String,Object>();
        final HttpSession session;
        Request(HttpSession session) { this.session=session; }
        RenderRequest render() { return proxy(RenderRequest.class,this); }
        HttpServletRequest http() { return proxy(HttpServletRequest.class,this); }
        public Object invoke(Object p,Method m,Object[] a) {
            String n=m.getName();
            if ("getParameter".equals(n)) return params.get(a[0]);
            if ("getAttribute".equals(n)) return attrs.get(a[0]);
            if ("setAttribute".equals(n)) { attrs.put((String)a[0],a[1]); return null; }
            if ("getSession".equals(n)) return session;
            if ("getLocale".equals(n)) return ES;
            return empty(m.getReturnType());
        }
    }
    private static final class Response implements InvocationHandler {
        int status=200;
        String type;
        final Map<String,String> headers=new HashMap<String,String>();
        final ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        final StringWriter text=new StringWriter();
        public Object invoke(Object p,Method m,Object[] a) {
            String n=m.getName();
            if ("setStatus".equals(n)) status=((Integer)a[0]).intValue();
            if ("setHeader".equals(n)) headers.put((String)a[0],(String)a[1]);
            if ("setContentType".equals(n)) type=(String)a[0];
            if ("getWriter".equals(n)) return new PrintWriter(text);
            if ("getOutputStream".equals(n)) return new ServletOutputStream() {
                public void write(int b) { bytes.write(b); }
            };
            return empty(m.getReturnType());
        }
    }
    private static Connection connection() {
        return proxy(Connection.class,new InvocationHandler() {
            public Object invoke(Object p,Method m,Object[] a) {
                if ("close".equals(m.getName())) closedConnections++;
                if ("prepareCall".equals(m.getName())) return statement((String)a[0]);
                return empty(m.getReturnType());
            }
        });
    }
    private static CallableStatement statement(final String sql) {
        final Map<Integer,Object> params=new HashMap<Integer,Object>();
        return proxy(CallableStatement.class,new InvocationHandler() {
            public Object invoke(Object p,Method m,Object[] a) throws Exception {
                String n=m.getName();
                if (n.startsWith("set") && a != null && a[0] instanceof Integer) {
                    params.put((Integer)a[0],"setNull".equals(n) ? null : a[1]);
                }
                if ("executeQuery".equals(n)) {
                    calls.add(sql); bindings.add(params);
                    if (sql.indexOf("buscar_requerimientos(")>=0) return result(rows);
                    if (sql.indexOf("listar_relaciones_reclamo_prestacional_batch")>=0) {
                        return result(Arrays.asList(row("id_requerimiento",12,
                                "id_reclamo_prestacional",88,"estado","VINCULADO")));
                    }
                    if (sql.indexOf("listar_prestadores_adjudicados_batch")>=0) {
                        if (failAwards) throw new SQLException("Fallo JDBC sintetico");
                        return result(awards);
                    }
                    if (sql.indexOf("listar_sectores_requerimiento")>=0)
                        return result(new ArrayList<Map<String,Object>>());
                    throw new AssertionError("Consulta no prevista: "+sql);
                }
                return empty(m.getReturnType());
            }
        });
    }
    private static ResultSet result(final List<Map<String,Object>> data) {
        return proxy(ResultSet.class,new InvocationHandler() {
            int i=-1; boolean wasNull;
            public Object invoke(Object p,Method m,Object[] a) {
                String n=m.getName();
                if ("next".equals(n)) return Boolean.valueOf(++i<data.size());
                if ("wasNull".equals(n)) return Boolean.valueOf(wasNull);
                if (n.startsWith("get") && a != null) {
                    Object value=data.get(i).get(a[0]); wasNull=value==null;
                    return value==null ? empty(m.getReturnType()) : value;
                }
                return empty(m.getReturnType());
            }
        });
    }
    private static Map<String,Object> row(Object... values) {
        Map<String,Object> r=new HashMap<String,Object>();
        for(int i=0;i<values.length;i+=2) r.put((String)values[i],values[i+1]);
        return r;
    }
    private static class Defaults implements InvocationHandler {
        public Object invoke(Object p,Method m,Object[] a) { return empty(m.getReturnType()); }
    }
    private static Object empty(Class<?> c) {
        if(c==Boolean.TYPE)return Boolean.FALSE;
        if(c==Integer.TYPE)return Integer.valueOf(0);
        if(c==Long.TYPE)return Long.valueOf(0);
        if(c==Short.TYPE)return Short.valueOf((short)0);
        if(c==Double.TYPE)return Double.valueOf(0);
        return null;
    }
    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> c,InvocationHandler h) {
        return (T) Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},h);
    }
    private static void eq(String label,Object a,Object b) {
        check(a==null ? b==null : a.equals(b),label+" esperado="+a+" obtenido="+b);
    }
    private static void check(boolean value,String label) { if(!value)fail(label); checks++; }
    private static void fail(String label) { throw new AssertionError(label); }
}
