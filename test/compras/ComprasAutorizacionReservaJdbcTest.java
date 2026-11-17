import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;

/** Frontera JDBC simulada, adaptada de ComprasEfectosTransaccionTest. Sin BD ni SMTP. */
public class ComprasAutorizacionReservaJdbcTest {
    private static int checks;
    private static void verificar(boolean valor, String mensaje) {
        if (!valor) throw new IllegalStateException(mensaje);
        checks++;
    }
    private static Object defecto(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Long.TYPE) return Long.valueOf(0);
        return null;
    }
    private static final class JDBC implements InvocationHandler {
        final Map<Integer,Map<String,Object>> filas = new HashMap<Integer,Map<String,Object>>();
        final List<Sentencia> sentencias = new ArrayList<Sentencia>();
        int commits;
        int rollbacks;
        int resultadosCerrados;
        boolean fallar;
        boolean huellaPresente = true;
        boolean fallarLecturaHuella;
        String huella = "0123456789abcdef0123456789abcdef";
        int conexionesCerradas;
        Connection conexion() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[] {Connection.class}, this);
        }
        public Object invoke(Object proxy, Method metodo, Object[] args) throws Throwable {
            String n = metodo.getName();
            if ("prepareStatement".equals(n)) {
                Sentencia s = new Sentencia(this, (String) args[0]);
                sentencias.add(s);
                return Proxy.newProxyInstance(getClass().getClassLoader(),
                        new Class[] {PreparedStatement.class}, s);
            }
            if ("close".equals(n)) conexionesCerradas++;
            if ("commit".equals(n)) commits++;
            if ("rollback".equals(n)) rollbacks++;
            return defecto(metodo.getReturnType());
        }
    }
    private static final class Sentencia implements InvocationHandler {
        final JDBC jdbc;
        final String sql;
        final Map<Integer,Object> parametros = new HashMap<Integer,Object>();
        boolean cerrada;
        Sentencia(JDBC jdbc, String sql) { this.jdbc = jdbc; this.sql = sql; }
        public Object invoke(Object proxy, Method metodo, Object[] args) throws Throwable {
            String n = metodo.getName();
            if ("close".equals(n)) { cerrada = true; return null; }
            if (n.startsWith("set")) { parametros.put((Integer) args[0], args[1]); return null; }
            if ("executeUpdate".equals(n) || "executeQuery".equals(n)) {
                if (jdbc.fallar) throw new SQLException("Fallo JDBC de prueba.");
            }
            if ("executeUpdate".equals(n)) {
                if (sql.startsWith("INSERT INTO compras.requerimiento_autorizacion_notificacion")) {
                    verificar(sql.indexOf("ON CONFLICT (id_requerimiento) DO NOTHING") >= 0,
                            "La reserva no reemplaza una notificacion previa.");
                    Integer id = (Integer) parametros.get(Integer.valueOf(1));
                    if (jdbc.filas.containsKey(id)) return Integer.valueOf(0);
                    Map<String,Object> fila = new HashMap<String,Object>();
                    fila.put("token", parametros.get(Integer.valueOf(2)));
                    fila.put("estado_envio", "ENVIANDO");
                    fila.put("alta_usr", parametros.get(Integer.valueOf(3)));
                    jdbc.filas.put(id, fila);
                    return Integer.valueOf(1);
                }
                verificar(sql.indexOf("AND token = ? AND estado_envio = 'ENVIANDO'") >= 0,
                        "Finalizacion exige token de reserva y estado en curso.");
                verificar(sql.indexOf("resultado_smtp = ?") >= 0,
                        "El resultado SMTP se registra separado del estado de reserva.");
                Integer id = (Integer) parametros.get(Integer.valueOf(3));
                Map<String,Object> fila = jdbc.filas.get(id);
                if (fila == null || !"ENVIANDO".equals(fila.get("estado_envio"))
                        || !fila.get("token").equals(parametros.get(Integer.valueOf(4)))) return Integer.valueOf(0);
                fila.put("estado_envio", parametros.get(Integer.valueOf(1)));
                fila.put("resultado_smtp", parametros.get(Integer.valueOf(2)));
                return Integer.valueOf(1);
            }
            if ("executeQuery".equals(n)) {
                Map<String,Object> encontrada;
                if (sql.startsWith("SELECT md5(")) {
                    encontrada = jdbc.huellaPresente ? new HashMap<String,Object>() : null;
                    if (encontrada != null) encontrada.put("huella", jdbc.huella);
                } else if (sql.indexOf("compras.requerimiento_autorizacion_notificacion") >= 0) {
                    verificar(sql.indexOf("WHERE id_requerimiento = ? FOR UPDATE") >= 0,
                            "La lectura de reserva utiliza el bloqueo transaccional existente.");
                    encontrada = jdbc.filas.get(parametros.get(Integer.valueOf(1)));
                } else {
                    verificar(sql.indexOf("FOR UPDATE") >= 0,
                            "Cada consulta previa a la huella bloquea sus filas.");
                    encontrada = null;
                }
                final Map<String,Object> fila = encontrada;
                return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {ResultSet.class},
                        new InvocationHandler() {
                            boolean consumido;
                            public Object invoke(Object p, Method m, Object[] a) throws Throwable {
                                String nombre = m.getName();
                                if ("next".equals(nombre)) {
                                    boolean disponible = !consumido && fila != null;
                                    consumido = true;
                                    return Boolean.valueOf(disponible);
                                }
                                if ("getString".equals(nombre)) {
                                    if ("huella".equals(a[0]) && jdbc.fallarLecturaHuella) {
                                        throw new SQLException("Fallo al leer huella de prueba.");
                                    }
                                    return fila.get(a[0]);
                                }
                                if ("close".equals(nombre)) jdbc.resultadosCerrados++;
                                return defecto(m.getReturnType());
                            }
                        });
            }
            return defecto(metodo.getReturnType());
        }
    }
    private static EditarRequerimientoCompraServiceUtil.Transaccion transaccion(JDBC jdbc) throws Exception {
        Constructor c = EditarRequerimientoCompraServiceImpl.Transaccion.class.getDeclaredConstructor(
                new Class[] {EditarRequerimientoCompraServiceImpl.class, Connection.class});
        c.setAccessible(true);
        Object impl = c.newInstance(new Object[] {new EditarRequerimientoCompraServiceImpl(), jdbc.conexion()});
        Constructor u = EditarRequerimientoCompraServiceUtil.Transaccion.class.getDeclaredConstructor(
                new Class[] {EditarRequerimientoCompraServiceImpl.Transaccion.class});
        u.setAccessible(true);
        return (EditarRequerimientoCompraServiceUtil.Transaccion) u.newInstance(new Object[] {impl});
    }
    public static void main(String[] args) throws Exception {
        JDBC jdbc = new JDBC();
        EditarRequerimientoCompraServiceUtil.Transaccion tx = transaccion(jdbc);
        String token = "00000000-0000-0000-0000-000000000001";
        String otro = "00000000-0000-0000-0000-000000000002";
        verificar(tx.obtenerReservaCorreoAutorizacion(1458) == null, "Sin fila no se inventa un envio.");
        verificar(tx.insertarReservaCorreoAutorizacion(1458, token, "usuario-prueba") == 1,
                "Primera reserva obtiene una fila.");
        verificar(tx.insertarReservaCorreoAutorizacion(1458, otro, "otro-usuario") == 0,
                "Segunda reserva no reemplaza token ni auditor.");
        Map<String,Object> fila = tx.obtenerReservaCorreoAutorizacion(1458);
        verificar(token.equals(fila.get("token")) && "ENVIANDO".equals(fila.get("estado_envio")),
                "La consulta devuelve el propietario original de la reserva.");
        verificar(tx.finalizarCorreoAutorizacion(1458, otro, "ENVIADO") == 0,
                "Otra ejecucion no confirma el correo.");
        verificar(tx.finalizarCorreoAutorizacion(1458, token, "ENVIADO") == 1,
                "El propietario confirma su resultado.");
        verificar("ACEPTADO".equals(jdbc.filas.get(Integer.valueOf(1458)).get("resultado_smtp")),
                "La firma legacy de tres parametros conserva la aceptacion SMTP.");
        verificar(tx.finalizarCorreoAutorizacion(1458, token, "INCIERTO") == 0,
                "Un resultado final no se sobreescribe.");
        verificar(tx.insertarReservaCorreoAutorizacion(1458, otro, "otro-usuario") == 0,
                "Un correo confirmado no se reserva de nuevo.");
        verificar(tx.insertarReservaCorreoAutorizacion(1459, otro, "usuario-prueba") == 1,
                "Otro requerimiento posee su reserva independiente.");
        verificar(tx.finalizarCorreoAutorizacion(1459, otro, "INCIERTO") == 1,
                "La incertidumbre se conserva como resultado final.");
        verificar("INCIERTO".equals(jdbc.filas.get(Integer.valueOf(1459)).get("resultado_smtp")),
                "La firma legacy conserva resultado incierto sin inferir rechazo.");
        verificar(tx.insertarReservaCorreoAutorizacion(1459, token, "usuario-prueba") == 0,
                "Un resultado incierto no se libera para reintento.");
        verificar(jdbc.commits == 0 && jdbc.rollbacks == 0,
                "La persistencia no decide commit ni rollback por el Helper.");
        jdbc.fallar = true;
        boolean fallo = false;
        try { tx.insertarReservaCorreoAutorizacion(1460, token, "usuario-prueba"); }
        catch (SQLException e) { fallo = true; }
        verificar(fallo, "Fallo JDBC se propaga sin devolver exito.");
        fallo = false;
        try { tx.obtenerReservaCorreoAutorizacion(1460); }
        catch (SQLException e) { fallo = true; }
        verificar(fallo, "Fallo de lectura no se confunde con fila inexistente.");
        for (int i = 0; i < jdbc.sentencias.size(); i++) {
            Sentencia s = jdbc.sentencias.get(i);
            verificar(s.cerrada, "Toda sentencia se cierra incluso ante excepcion.");
            verificar(s.sql.indexOf("compras.requerimiento_autorizacion_notificacion") >= 0
                    && s.sql.indexOf("requerimiento_cotizacion_prestador") < 0,
                    "No se modifica estado de compra ni notificaciones a prestadores.");
        }
        verificar(jdbc.resultadosCerrados == 2, "Los resultados existentes y vacios se cierran.");
        probarResultadosSmtp();
        probarHuellaPreparacion();
        System.out.println("COMPRAS_AUTORIZACION_RESERVA_JDBC_OK checks=" + checks);
        System.out.println("JDBC simulado: bindings, locks, huella y cierres. No ejecuta SQL ni contencion PostgreSQL.");
    }
    private static void probarResultadosSmtp() throws Exception {
        JDBC jdbc = new JDBC();
        EditarRequerimientoCompraServiceUtil.Transaccion tx = transaccion(jdbc);
        String[] resultados = {"RECHAZADO", "PARCIAL", "INCIERTO", "ACEPTADO"};
        for (int i = 0; i < resultados.length; i++) {
            int id = 1500 + i;
            String estado = "ACEPTADO".equals(resultados[i]) ? "ENVIADO" : "INCIERTO";
            verificar(tx.insertarReservaCorreoAutorizacion(id, "token-prueba", "usuario-prueba") == 1,
                    "Cada resultado dispone de reserva propia.");
            verificar(tx.finalizarCorreoAutorizacion(id, "token-prueba", estado, resultados[i]) == 1,
                    "Registra resultado tipado " + resultados[i]);
            verificar(resultados[i].equals(jdbc.filas.get(Integer.valueOf(id)).get("resultado_smtp")),
                    "No colapsa la clasificacion SMTP en la persistencia.");
            verificar(resultados[i].equals(tx.obtenerReservaCorreoAutorizacion(id).get("resultado_smtp")),
                    "El bloqueo posterior conserva el resultado SMTP tipado para el usuario.");
            verificar(tx.insertarReservaCorreoAutorizacion(id, "otro-token", "otro-usuario") == 0,
                    "Ningun resultado final se libera automaticamente.");
        }
        verificar(jdbc.commits == 0 && jdbc.rollbacks == 0,
                "Registrar clasificacion SMTP no decide la transaccion del helper.");
        for (int i = 0; i < jdbc.sentencias.size(); i++) verificar(jdbc.sentencias.get(i).cerrada,
                "La finalizacion tipada cierra cada sentencia.");
    }

    private static void probarHuellaPreparacion() throws Exception {
        JDBC jdbc = new JDBC();
        BusquedaRequerimientoCompraServiceImpl service = new BusquedaRequerimientoCompraServiceImpl();
        String inicial = service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376);
        verificar(jdbc.huella.equals(inicial), "Devuelve huella calculada por PostgreSQL sin reconstruir datos en Java.");
        Sentencia consulta = jdbc.sentencias.get(0);
        String[] tablas = {"compras.requerimiento r", "compras.requerimiento_detalle", "compras.requerimiento_reclamo_prestacional",
            "autorizaciones.reclamos_prestacionales rp", "autorizaciones.reclamos_prestacionales_prestaciones",
            "autorizaciones.reclamos_prestacionales_estados", "compras.requerimiento_cotizacion_prestador",
            "compras.requerimiento_pedido_cotizacion", "compras.requerimiento_presupuesto",
            "compras.requerimiento_comparativa c", "compras.requerimiento_comparativa_detalle"};
        for (int i = 0; i < tablas.length; i++) verificar(consulta.sql.indexOf(tablas[i]) >= 0,
                "La huella incluye dataset documental directo " + tablas[i]);
        verificar(consulta.parametros.size() == 13, "Cada dataset usa un id parametrizado.");
        for (int i = 1; i <= 13; i++) verificar(Integer.valueOf(1376).equals(consulta.parametros.get(Integer.valueOf(i))),
                "No cruza requerimientos en parametro " + i);
        verificar(consulta.sql.indexOf("ORDER BY s.tipo,s.datos") >= 0,
                "La serializacion tiene orden determinista.");
        verificar(consulta.sql.indexOf("row_to_json(") >= 0 && consulta.sql.indexOf("md5(") >= 0,
                "El snapshot retorna solo huella, no datos sensibles al caller.");
        verificar(consulta.sql.indexOf("'cabecera_resuelta'") >= 0
                && consulta.sql.indexOf("public.afiliado") >= 0 && consulta.sql.indexOf("public.afi_domicilio") >= 0,
                "La huella incluye afiliado y domicilio resueltos usados en asunto y PDF.");
        verificar(consulta.sql.indexOf("'detalle_resuelto'") >= 0
                && consulta.sql.indexOf("public.prestador") >= 0 && consulta.sql.indexOf("autorizaciones.nomenclador") >= 0,
                "La huella incluye nombres y prestaciones resueltos usados en documentos.");
        verificar(jdbc.conexionesCerradas == 0 && jdbc.resultadosCerrados == 1 && consulta.cerrada,
                "La lectura respeta la conexion prestada y cierra statement/resultset.");
        jdbc.huella = "fedcba9876543210fedcba9876543210";
        String cambiada = service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376);
        verificar(!inicial.equals(cambiada), "Una nueva lectura conserva cambio de huella para rechazar snapshot obsoleto.");
        jdbc.huellaPresente = false;
        boolean fallo = false;
        try { service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376); }
        catch (Exception e) { fallo = true; }
        verificar(fallo, "Sin resultado no se inventa una huella valida.");
        jdbc.huellaPresente = true;
        jdbc.huella = null;
        fallo = false;
        try { service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376); }
        catch (Exception e) { fallo = true; }
        verificar(fallo, "Huella null bloquea sin devolver exito.");
        jdbc.huella = "incompleta";
        fallo = false;
        try { service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376); }
        catch (Exception e) { fallo = true; }
        verificar(fallo, "Huella incompleta bloquea sin devolver exito.");
        jdbc.huella = inicial;
        jdbc.fallarLecturaHuella = true;
        fallo = false;
        try { service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376); }
        catch (SQLException e) { fallo = true; }
        verificar(fallo, "La excepcion al leer huella se propaga y no se interpreta como faltante.");
        verificar(jdbc.resultadosCerrados == 6, "Se cierran resultados incluso ante fallo de lectura/mapeo.");
        jdbc.fallarLecturaHuella = false;
        jdbc.fallar = true;
        fallo = false;
        try { service.getHuellaPreparacionAutorizacion(jdbc.conexion(), 1376); }
        catch (SQLException e) { fallo = true; }
        verificar(fallo, "El fallo JDBC de preparacion se propaga.");
        for (int i = 0; i < jdbc.sentencias.size(); i++) verificar(jdbc.sentencias.get(i).cerrada,
                "Toda consulta de huella cierra su sentencia.");

        JDBC bloqueada = new JDBC();
        EditarRequerimientoCompraServiceUtil.Transaccion tx = transaccion(bloqueada);
        verificar(bloqueada.huella.equals(tx.getHuellaPreparacionAutorizacion(1376)),
                "La fachada transaccional lee huella por su misma conexion.");
        verificar(bloqueada.sentencias.size() == 8, "Siete datasets se bloquean antes de consultar huella.");
        for (int i = 0; i < 7; i++) {
            Sentencia lock = bloqueada.sentencias.get(i);
            verificar(lock.sql.indexOf("FOR UPDATE") >= 0, "La huella de reserva bloquea dataset " + i);
            verificar(Integer.valueOf(1376).equals(lock.parametros.get(Integer.valueOf(1))),
                    "Bloqueo parametrizado del mismo requerimiento.");
        }
        verificar(bloqueada.sentencias.get(7).sql.startsWith("SELECT md5("),
                "La huella se obtiene despues de todos los locks documentales.");
        verificar(bloqueada.resultadosCerrados == 8 && bloqueada.conexionesCerradas == 0,
                "La transaccion conserva conexion propia y cierra todos sus resultados.");
        verificar(bloqueada.commits == 0 && bloqueada.rollbacks == 0,
                "La comprobacion no confirma ni revierte transaccion implicitamente.");
    }
}
