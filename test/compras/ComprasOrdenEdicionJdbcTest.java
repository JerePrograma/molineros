import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* Solo se ejecuta contra una base de fixture descartable con nombre validado.
 * Usa el servicio JDBC productivo, sin configuracion de conexiones del portal. */
public class ComprasOrdenEdicionJdbcTest {

    private static final int REQUERIMIENTO = 1001;
    private static int checks;
    private static final Set<String> COLUMNAS_EDITABLES = new HashSet<String>(
            Arrays.asList(new String[] { "condicion", "forma_pago", "plazo_pago",
                "entrega", "observaciones", "modi_fecha", "modi_usr", "version_edicion" }));

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !args[0].matches("^compras_orden_test_edicion_[a-z0-9_]+$")) {
            throw new IllegalArgumentException("Solo se admite una base descartable de pruebas de edicion de OC.");
        }
        ComboPooledDataSource pool = new ComboPooledDataSource();
        Field datasource = null;
        Object anterior = null;
        boolean instalado = false;
        try {
            pool.setDriverClass("org.postgresql.Driver");
            int puerto = Integer.parseInt(System.getProperty("compras.test.port", "5432"));
            if (puerto <= 0 || puerto > 65535) throw new IllegalArgumentException("Puerto de prueba invalido.");
            pool.setJdbcUrl("jdbc:postgresql://127.0.0.1:" + puerto + "/" + args[0]);
            pool.setUser("postgres");
            pool.setPassword("");
            pool.setMinPoolSize(1);
            pool.setInitialPoolSize(1);
            pool.setMaxPoolSize(4);
            pool.setCheckoutTimeout(15000);
            pool.setAcquireRetryAttempts(1);
            datasource = ConnectionHelper.class.getDeclaredField("datasource");
            datasource.setAccessible(true);
            anterior = datasource.get(null);
            datasource.set(null, pool);
            instalado = true;
            creacionNormalizada(pool);
            ejecutar(pool);
            System.out.println("COMPRAS_ORDEN_EDICION_JDBC_OK checks=" + checks);
            System.out.println("Servicio productivo: creacion normalizada, origen por FK, auditoria, version, rollback y concurrencia real.");
        } finally {
            try {
                if (instalado) {
                    datasource.set(null, anterior);
                }
            } finally {
                pool.close();
            }
        }
    }

    private static void creacionNormalizada(ComboPooledDataSource pool) throws Exception {
        RequerimientoCompraOrdenServiceImpl service = new RequerimientoCompraOrdenServiceImpl();
        RequerimientoCompraOrden entrada = nuevaOrden(53, 2053, 1);
        entrada.setProveedor("Proveedor enviado que debe ignorarse");
        entrada.getDetalles().get(0).setCodigoItem("CODIGO_ENVIADO");
        entrada.getDetalles().get(0).setDescripcionItem("Descripcion enviada que debe ignorarse");
        RequerimientoCompraOrden creada = service.crear(entrada, "emisor_jdbc");
        comprobar("OC53 emitida", creada != null && creada.getIdOrdenCompra() > 0);
        igual("OC53 moneda fija", "ARS", creada.getMoneda());
        igual("OC53 proveedor de presupuesto", "LA PRUEBA", creada.getProveedor());
        igual("OC53 CUIT de presupuesto", "30700000001", creada.getProveedorCuit());
        igual("OC53 actor por identidad", "emisor_jdbc", creada.getConfeccionadoPor());
        igual("OC53 observacion desde fuente", "CELULAR PRUEBA", creada.getDetalles().get(0).getDescripcionItemVisible());
        igual("OC53 codigo ajeno ignorado", "", creada.getDetalles().get(0).getCodigoItemVisible());
        igual("OC53 cantidad uno", Integer.valueOf(1), creada.getDetalles().get(0).getCantidad());
        igual("OC53 precio no informado", null, creada.getDetalles().get(0).getPrecioUnitarioEstimado());
        igual("OC53 subtotal no informado", null, creada.getDetalles().get(0).getPrecioTotalEstimado());
        igual("OC53 impuestos no informados", null, creada.getIva());
        List<Map<String, String>> antes = filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null);
        igual("OC53 presupuesto de origen fijo", "53", antes.get(0).get("id_presupuesto"));
        igual("cabecera solo16columnas", Integer.valueOf(16), Integer.valueOf(antes.get(0).size()));
        comprobar("cabecera sin moneda duplicada", !antes.get(0).containsKey("moneda"));
        comprobar("cabecera sin proveedor duplicado", !antes.get(0).containsKey("proveedor"));
        igual("detalle solo5columnas", Integer.valueOf(5), Integer.valueOf(detalles(pool, creada.getIdOrdenCompra()).get(0).size()));
        RequerimientoCompraOrden repetida = service.crear(entrada, "otro_emisor");
        igual("creacion idempotente misma OC", Integer.valueOf(creada.getIdOrdenCompra()), Integer.valueOf(repetida.getIdOrdenCompra()));
        igual("creacion repetida conserva auditoria", antes, filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null));
        boolean fallo = false;
        try {
            service.crear(nuevaOrden(54, 2054, 0), "emisor_fallido");
        } catch (SQLException e) {
            fallo = "23514".equals(e.getSQLState());
        }
        comprobar("creacion invalida rechazada por SQL", fallo);
        comprobar("rollback de cabecera nueva", filas(pool,
                "SELECT id_orden_compra FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 54, null).isEmpty());
        igual("rollback conserva AUTORIZADO", "7", filas(pool,
                "SELECT estado FROM compras.requerimiento WHERE id_requerimiento = ?", 54, null).get(0).get("estado"));
        // Solo fixture: simular baja logica del origen y otra adjudicacion posterior.
        Connection con = pool.getConnection();
        try {
            Statement stmt = con.createStatement();
            try {
                stmt.executeUpdate("UPDATE compras.requerimiento_presupuesto SET baja_fecha=now(), empresa_adjudicada=FALSE WHERE id_requerimiento_presupuesto=53");
                stmt.executeUpdate("INSERT INTO compras.requerimiento_presupuesto (id_requerimiento_presupuesto,id_requerimiento,tipo_documento,empresa_adjudicada,descripcion_empresa,empresa_cuit) VALUES (153,53,3,TRUE,'OTRO PROVEEDOR','30700000002')");
            } finally {
                stmt.close();
            }
        } finally {
            con.close();
        }
        RequerimientoCompraOrden consultada = service.obtenerPorRequerimiento(53);
        igual("consulta origen dado de baja", "LA PRUEBA", consultada.getProveedor());
        igual("consulta conserva CUIT de FK original", "30700000001", consultada.getProveedorCuit());
        igual("otra adjudicacion no cambia OC", antes, filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null));
        repetida = service.crear(entrada, "tercer_emisor");
        igual("idempotente tras baja de origen", Integer.valueOf(creada.getIdOrdenCompra()), Integer.valueOf(repetida.getIdOrdenCompra()));
        igual("sin nueva numeracion tras baja", "53", filas(pool,
                "SELECT id_presupuesto FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null).get(0).get("id_presupuesto"));
    }

    private static RequerimientoCompraOrden nuevaOrden(int requerimiento, int detalle, int cantidad) {
        RequerimientoCompraOrden orden = new RequerimientoCompraOrden();
        orden.setIdRequerimientoCompra(requerimiento);
        orden.setFechaEmision(new Date());
        RequerimientoCompraDetalle item = new RequerimientoCompraDetalle(Integer.valueOf(detalle));
        item.setIdRequerimientoCompra(requerimiento);
        item.setCantidad(Integer.valueOf(cantidad));
        orden.getDetalles().add(item);
        return orden;
    }

    private static void ejecutar(ComboPooledDataSource pool) throws Exception {
        RequerimientoCompraOrdenServiceImpl service = new RequerimientoCompraOrdenServiceImpl();
        RequerimientoCompraOrden inicial = service.obtenerPorRequerimiento(REQUERIMIENTO);
        comprobar("fixture con OC emitida", inicial != null && inicial.getIdOrdenCompra() > 0);
        igual("fixture version cero", Integer.valueOf(0), Integer.valueOf(inicial.getVersionEdicion()));
        estado(pool);
        List<Map<String, String>> cabeceraInmutable = cabecera(pool, COLUMNAS_EDITABLES);
        List<Map<String, String>> detalles = detalles(pool, inicial.getIdOrdenCompra());
        comprobar("fixture con detalles", !detalles.isEmpty());

        inicial.setCondicion("Condicion JDBC de prueba");
        inicial.setFormaPago("Transferencia de prueba");
        inicial.setPlazoPago("30 dias");
        inicial.setEntrega("Entrega coordinada de prueba");
        inicial.setObservaciones("Observacion JDBC inicial");
        RequerimientoCompraOrden actualizada = service.actualizar(inicial, "editor_jdbc");
        igual("misma OC", Integer.valueOf(inicial.getIdOrdenCompra()), Integer.valueOf(actualizada.getIdOrdenCompra()));
        igual("condicion guardada", inicial.getCondicion(), actualizada.getCondicion());
        igual("forma pago guardada", inicial.getFormaPago(), actualizada.getFormaPago());
        igual("plazo pago guardado", inicial.getPlazoPago(), actualizada.getPlazoPago());
        igual("entrega guardada", inicial.getEntrega(), actualizada.getEntrega());
        igual("observaciones guardadas", inicial.getObservaciones(), actualizada.getObservaciones());
        igual("version incrementada", Integer.valueOf(1), Integer.valueOf(actualizada.getVersionEdicion()));
        igual("usuario editor durable", "editor_jdbc", actualizada.getModiUsr());
        comprobar("fecha edicion durable", actualizada.getModiFecha() != null);
        preservado(pool, inicial.getIdOrdenCompra(), cabeceraInmutable, detalles);

        List<Map<String, String>> despuesExito = cabecera(pool, null);
        RequerimientoCompraOrden obsoleta = service.obtenerPorRequerimiento(REQUERIMIENTO);
        obsoleta.setVersionEdicion(0);
        obsoleta.setObservaciones("No debe persistir por version obsoleta");
        boolean rechazo = false;
        try {
            service.actualizar(obsoleta, "editor_obsoleto");
        } catch (IllegalArgumentException e) {
            rechazo = true;
        }
        comprobar("version obsoleta rechazada", rechazo);
        igual("version obsoleta sin cambios", despuesExito, cabecera(pool, null));

        RequerimientoCompraOrden incorrecta = service.obtenerPorRequerimiento(REQUERIMIENTO);
        incorrecta.setIdOrdenCompra(inicial.getIdOrdenCompra() + 100000);
        incorrecta.setObservaciones("No debe persistir por ID incorrecto");
        rechazo = false;
        try {
            service.actualizar(incorrecta, "editor_id_incorrecto");
        } catch (IllegalArgumentException e) {
            rechazo = true;
        }
        comprobar("id de OC incorrecto rechazado", rechazo);
        igual("id incorrecto sin cambios", despuesExito, cabecera(pool, null));

        RequerimientoCompraOrden invalidaSql = service.obtenerPorRequerimiento(REQUERIMIENTO);
        char[] extensa = new char[1001];
        Arrays.fill(extensa, 'x');
        invalidaSql.setCondicion(new String(extensa));
        invalidaSql.setFormaPago("No persistir por fallo SQL");
        invalidaSql.setObservaciones("No persistir por rollback SQL");
        boolean falloSql = false;
        try {
            service.actualizar(invalidaSql, "editor_fallo_sql");
        } catch (SQLException e) {
            falloSql = true;
        }
        comprobar("fallo SQL real por longitud", falloSql);
        igual("fallo SQL revierte campos y version", despuesExito, cabecera(pool, null));
        preservado(pool, inicial.getIdOrdenCompra(), cabeceraInmutable, detalles);

        concurrentes(pool, service, inicial.getIdOrdenCompra(), cabeceraInmutable, detalles);
    }

    private static void concurrentes(ComboPooledDataSource pool,
            RequerimientoCompraOrdenServiceImpl service, int idOrden,
            List<Map<String, String>> cabeceraInmutable,
            List<Map<String, String>> detallesAntes) throws Exception {
        RequerimientoCompraOrden uno = service.obtenerPorRequerimiento(REQUERIMIENTO);
        RequerimientoCompraOrden dos = service.obtenerPorRequerimiento(REQUERIMIENTO);
        igual("primer formulario version uno", Integer.valueOf(1), Integer.valueOf(uno.getVersionEdicion()));
        igual("segundo formulario version uno", Integer.valueOf(1), Integer.valueOf(dos.getVersionEdicion()));
        uno.setObservaciones("Observacion concurrente uno");
        dos.setObservaciones("Observacion concurrente dos");
        CountDownLatch listos = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);
        CountDownLatch fin = new CountDownLatch(2);
        Editor primero = new Editor(service, uno, "editor_uno", listos, inicio, fin);
        Editor segundo = new Editor(service, dos, "editor_dos", listos, inicio, fin);
        primero.start();
        segundo.start();
        boolean listosATiempo = listos.await(10, TimeUnit.SECONDS);
        inicio.countDown();
        if (!listosATiempo || !fin.await(30, TimeUnit.SECONDS)) {
            primero.interrupt();
            segundo.interrupt();
            throw new AssertionError("La prueba concurrente excedio el tiempo permitido.");
        }
        int exitos = (primero.resultado != null ? 1 : 0) + (segundo.resultado != null ? 1 : 0);
        igual("un solo guardado concurrente", Integer.valueOf(1), Integer.valueOf(exitos));
        Editor ganador = primero.resultado != null ? primero : segundo;
        Editor perdedor = primero.resultado != null ? segundo : primero;
        comprobar("segundo guardado rechazado por version", perdedor.error instanceof IllegalArgumentException
                && perdedor.error.getMessage() != null
                && perdedor.error.getMessage().indexOf("modificada por otro usuario") >= 0);
        comprobar("ganador sin error", ganador.error == null);
        RequerimientoCompraOrden finalOrden = service.obtenerPorRequerimiento(REQUERIMIENTO);
        igual("version final dos", Integer.valueOf(2), Integer.valueOf(finalOrden.getVersionEdicion()));
        igual("observacion del ganador", ganador.orden.getObservaciones(), finalOrden.getObservaciones());
        igual("auditoria del ganador", ganador.usuario, finalOrden.getModiUsr());
        igual("condicion inicial preservada", "Condicion JDBC de prueba", finalOrden.getCondicion());
        igual("forma inicial preservada", "Transferencia de prueba", finalOrden.getFormaPago());
        igual("plazo inicial preservado", "30 dias", finalOrden.getPlazoPago());
        igual("entrega inicial preservada", "Entrega coordinada de prueba", finalOrden.getEntrega());
        preservado(pool, idOrden, cabeceraInmutable, detallesAntes);
    }

    private static class Editor extends Thread {
        private final RequerimientoCompraOrdenServiceImpl service;
        private final RequerimientoCompraOrden orden;
        private final String usuario;
        private final CountDownLatch listos;
        private final CountDownLatch inicio;
        private final CountDownLatch fin;
        private volatile RequerimientoCompraOrden resultado;
        private volatile Throwable error;

        private Editor(RequerimientoCompraOrdenServiceImpl service,
                RequerimientoCompraOrden orden, String usuario, CountDownLatch listos,
                CountDownLatch inicio, CountDownLatch fin) {
            super(usuario);
            this.service = service;
            this.orden = orden;
            this.usuario = usuario;
            this.listos = listos;
            this.inicio = inicio;
            this.fin = fin;
            setDaemon(true);
        }

        public void run() {
            listos.countDown();
            try {
                if (!inicio.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("No se libero el inicio concurrente.");
                }
                resultado = service.actualizar(orden, usuario);
            } catch (Throwable e) {
                error = e;
            } finally {
                fin.countDown();
            }
        }
    }

    private static List<Map<String, String>> cabecera(ComboPooledDataSource pool,
            Set<String> excluir) throws Exception {
        return filas(pool, "SELECT * FROM compras.requerimiento_orden_compra "
                + "WHERE id_requerimiento = ?", REQUERIMIENTO, excluir);
    }

    private static List<Map<String, String>> detalles(ComboPooledDataSource pool,
            int idOrden) throws Exception {
        return filas(pool, "SELECT * FROM compras.requerimiento_orden_compra_detalle "
                + "WHERE id_orden_compra = ? ORDER BY orden", idOrden, null);
    }

    private static List<Map<String, String>> filas(ComboPooledDataSource pool,
            String sql, int id, Set<String> excluir) throws Exception {
        Connection con = pool.getConnection();
        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            try {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                try {
                    List<Map<String, String>> filas = new ArrayList<Map<String, String>>();
                    ResultSetMetaData metadata = rs.getMetaData();
                    while (rs.next()) {
                        Map<String, String> fila = new LinkedHashMap<String, String>();
                        for (int i = 1; i <= metadata.getColumnCount(); i++) {
                            String columna = metadata.getColumnLabel(i);
                            if (excluir == null || !excluir.contains(columna)) {
                                fila.put(columna, rs.getString(i));
                            }
                        }
                        filas.add(fila);
                    }
                    return filas;
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            con.close();
        }
    }

    private static void preservado(ComboPooledDataSource pool, int idOrden,
            List<Map<String, String>> cabeceraAntes,
            List<Map<String, String>> detallesAntes) throws Exception {
        igual("otras columnas de cabecera preservadas", cabeceraAntes, cabecera(pool, COLUMNAS_EDITABLES));
        igual("detalles completos preservados", detallesAntes, detalles(pool, idOrden));
        estado(pool);
    }

    private static void estado(ComboPooledDataSource pool) throws Exception {
        List<Map<String, String>> filas = filas(pool,
                "SELECT estado FROM compras.requerimiento WHERE id_requerimiento = ?", REQUERIMIENTO, null);
        igual("una fila requerimiento", Integer.valueOf(1), Integer.valueOf(filas.size()));
        igual("estado ORDEN_COMPRA conservado", "5", filas.get(0).get("estado"));
    }

    private static void igual(String mensaje, Object esperado, Object actual) {
        comprobar(mensaje, esperado == null ? actual == null : esperado.equals(actual));
    }

    private static void comprobar(String mensaje, boolean condicion) {
        checks++;
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}