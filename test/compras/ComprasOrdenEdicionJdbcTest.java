import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
            timestampComparable(pool);
            consultaAfiliadoJdbc(pool);
            reparacionPrecios(pool);
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
        comprobar("OC53 precio informado", new BigDecimal("85000").compareTo(creada.getDetalles().get(0).getPrecioUnitarioEstimado()) == 0);
        comprobar("OC53 subtotal informado", new BigDecimal("85000").compareTo(creada.getDetalles().get(0).getPrecioTotalEstimado()) == 0);
        comprobar("OC53 impuestos opcionales cero", creada.getIva().signum() == 0 && creada.getIibb().signum() == 0);
        comprobar("OC53 total sin impuestos", new BigDecimal("85000").compareTo(creada.getTotal()) == 0);
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
        item.setPrecioUnitarioEstimado(new BigDecimal("85000"));
        orden.getDetalles().add(item);
        return orden;
    }

    private static void consultaAfiliadoJdbc(ComboPooledDataSource pool) throws Exception {
        // Solo en la base descartable validada en main. Reproduce los once tipos
        // del contrato legacy, no las funciones ni datos de una base operativa.
        Connection con = pool.getConnection();
        try {
            Statement stmt = con.createStatement();
            try {
                stmt.execute("CREATE FUNCTION public.buscar_afiliados_componente("
                        + "VARCHAR,INTEGER,VARCHAR,VARCHAR,INTEGER,VARCHAR,VARCHAR,INTEGER,INTEGER,INTEGER,INTEGER) "
                        + "RETURNS TABLE(cuil VARCHAR,inte INTEGER,id_parentesco_sss INTEGER,id_ospim INTEGER,"
                        + "nombre VARCHAR,apellido VARCHAR,tdoc VARCHAR,documento VARCHAR,id_plan INTEGER,nombre_plan VARCHAR,email VARCHAR) "
                        + "LANGUAGE plpgsql AS $fixture$ BEGIN "
                        + "IF $8 IS DISTINCT FROM 0 OR $3 IS NOT NULL OR $4 IS NOT NULL OR $5 IS NOT NULL "
                        + "OR $6 IS NOT NULL OR $7 IS NOT NULL OR $9 IS NOT NULL OR $10 IS NOT NULL OR $11 IS NOT NULL "
                        + "THEN RAISE EXCEPTION 'Parametros de consulta distintos del contrato legacy'; END IF; "
                        + "IF $1='20111111111' THEN RETURN; END IF; "
                        + "IF $1='20333333333' THEN RAISE EXCEPTION USING ERRCODE='22012', MESSAGE='Error SQL sintetico de consulta'; END IF; "
                        + "RETURN QUERY SELECT CASE WHEN $1='20444444444' THEN '20999999999' ELSE $1 END::VARCHAR,"
                        + "CASE WHEN $2=2 THEN 99 ELSE $2 END,5,4321,'Ana JDBC'::VARCHAR,'Padron'::VARCHAR,"
                        + "'DNI'::VARCHAR,'12345678'::VARCHAR,1,'Plan JDBC'::VARCHAR,'jdbc@example.invalid'::VARCHAR "
                        + "FROM generate_series(1,CASE WHEN $1='20222222222' THEN 2 ELSE 1 END); "
                        + "END; $fixture$");
            } finally { stmt.close(); }
        } finally { con.close(); }
        ConsultaJdbc service = new ConsultaJdbc();
        Afiliado afiliado = service.consultar("20123456789", 1);
        igual("JDBC bind CUIL exacto", "20123456789", afiliado.getCuil_titular());
        igual("JDBC bind integrante exacto", Integer.valueOf(1), Integer.valueOf(afiliado.getInte()));
        igual("JDBC mapea nombre", "Ana JDBC", afiliado.getNombre());
        igual("JDBC mapea apellido", "Padron", afiliado.getApellido());
        igual("JDBC mapea documento", "12345678", afiliado.getDocu_numero());
        igual("JDBC mapea idOSPIM", Integer.valueOf(4321), Integer.valueOf(afiliado.getId_ospim()));
        igual("JDBC mapea plan", "Plan JDBC", afiliado.getNombrePlan());
        igual("JDBC mapea email", "jdbc@example.invalid", afiliado.getEmail());
        igual("JDBC cero filas es dato opcional ausente", null, service.consultar("20111111111", 1));
        String[] claves = { "20222222222", "20444444444", "20123456789" };
        int[] integrantes = { 1, 1, 2 };
        for (int i = 0; i < claves.length; i++) {
            boolean fallo = false;
            try { service.consultar(claves[i], integrantes[i]); }
            catch (IllegalArgumentException esperado) { fallo = true; }
            comprobar("JDBC rechaza duplicado o identidad distinta " + i, fallo);
        }
        boolean falloSql = false;
        try { service.consultar("20333333333", 1); }
        catch (SQLException esperado) { falloSql = "22012".equals(esperado.getSQLState()); }
        comprobar("JDBC propaga error SQL sin ocultarlo", falloSql);
        RequerimientoCompra r = new RequerimientoCompra(); r.setIdRequerimientoCompra(40);
        r.setSectorDescripcion("Prestaciones Medicas"); r.setRequiereAfiliado(true);
        r.setAfiliadoCuilTitular("20123456789"); r.setAfiliadoInt(Integer.valueOf(1));
        RequerimientoCompraOrden orden = new RequerimientoCompraOrden(); orden.setIdRequerimientoCompra(40);
        service.completarDatosConsulta(orden, r);
        igual("JDBC consulta completa nombre vigente", "Ana JDBC", r.getAfiliadoNombre());
        igual("JDBC consulta completa plan vigente", "Plan JDBC", orden.getPlan());
        igual("JDBC consulta completa email vigente", "jdbc@example.invalid", r.getAfiliadoEmail());
        r.setAfiliadoCuilTitular("20111111111"); r.setAfiliadoNombre("Dato original"); orden.setPlan("Plan original");
        service.completarDatosConsulta(orden, r);
        igual("JDBC consulta sin filas conserva nombre", "Dato original", r.getAfiliadoNombre());
        igual("JDBC consulta sin filas conserva plan", "Plan original", orden.getPlan());
    }

    private static class ConsultaJdbc extends RequerimientoCompraOrdenServiceImpl {
        Afiliado consultar(String cuil, int inte) throws Exception { return buscarAfiliado(cuil, inte); }
        protected List<Domicilio> buscarDomicilios(String cuil, int inte) { return new ArrayList<Domicilio>(); }
        protected List<Telefono> buscarTelefonos(String cuil, int inte) { return new ArrayList<Telefono>(); }
        protected RequerimientoCompraPresupuesto obtenerOrigenProveedor(RequerimientoCompraOrden orden) {
            return new RequerimientoCompraPresupuesto();
        }
    }

    private static void reparacionPrecios(ComboPooledDataSource pool) throws Exception {
        RequerimientoCompraOrdenServiceImpl service = new RequerimientoCompraOrdenServiceImpl();
        RequerimientoCompraOrden original = service.obtenerPorRequerimiento(53);
        List<Map<String, String>> cabeceraAntes = filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null);
        List<Map<String, String>> detallesAntes = detalles(pool, original.getIdOrdenCompra());
        RequerimientoCompraOrden cambio = service.obtenerPorRequerimiento(53);
        cambio.getDetalles().clear(); cambio.getDetalles().add(cambioPrecio(2053, "1"));
        boolean rechazo = false;
        try { service.actualizar(cambio, "editor_no_reemplaza"); }
        catch (IllegalArgumentException esperado) { rechazo = true; }
        comprobar("precio conocido no se reemplaza", rechazo);
        igual("rechazo conocido revierte cabecera", cabeceraAntes, filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null));
        igual("rechazo conocido preserva detalle", detallesAntes, detalles(pool, original.getIdOrdenCompra()));
        // Solo fixture aislado: simular OC historica emitida con precio NULL.
        Connection con = pool.getConnection();
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE compras.requerimiento_orden_compra_detalle SET precio_unitario=NULL WHERE id_orden_compra=?");
            try { stmt.setInt(1, original.getIdOrdenCompra()); igual("fixture precio faltante", Integer.valueOf(1), Integer.valueOf(stmt.executeUpdate())); }
            finally { stmt.close(); }
        } finally { con.close(); }
        detallesAntes = detalles(pool, original.getIdOrdenCompra());
        cambio = service.obtenerPorRequerimiento(53); cambio.getDetalles().clear();
        cambio.getDetalles().add(cambioPrecio(2053, "10")); cambio.getDetalles().add(cambioPrecio(99999, "1"));
        cambio.setObservaciones("No persistir reparacion parcial"); rechazo = false;
        try { service.actualizar(cambio, "editor_rollback_reparacion"); }
        catch (IllegalArgumentException esperado) { rechazo = true; }
        comprobar("detalle ajeno rechaza reparacion completa", rechazo);
        igual("reparacion fallida revierte cabecera y version", cabeceraAntes, filas(pool,
                "SELECT * FROM compras.requerimiento_orden_compra WHERE id_requerimiento = ?", 53, null));
        igual("reparacion fallida revierte primer precio", detallesAntes, detalles(pool, original.getIdOrdenCompra()));
        RequerimientoCompraOrden uno = service.obtenerPorRequerimiento(53);
        RequerimientoCompraOrden dos = service.obtenerPorRequerimiento(53);
        uno.getDetalles().clear(); dos.getDetalles().clear();
        uno.getDetalles().add(cambioPrecio(2053, "11")); dos.getDetalles().add(cambioPrecio(2053, "22"));
        CountDownLatch listos = new CountDownLatch(2), inicio = new CountDownLatch(1), fin = new CountDownLatch(2);
        Editor primero = new Editor(service, uno, "reparador_uno", listos, inicio, fin);
        Editor segundo = new Editor(service, dos, "reparador_dos", listos, inicio, fin);
        primero.start(); segundo.start();
        boolean preparados = listos.await(10, TimeUnit.SECONDS); inicio.countDown();
        if (!preparados || !fin.await(30, TimeUnit.SECONDS)) throw new AssertionError("Timeout reparacion concurrente");
        int exitos = (primero.resultado != null ? 1 : 0) + (segundo.resultado != null ? 1 : 0);
        igual("una sola reparacion concurrente", Integer.valueOf(1), Integer.valueOf(exitos));
        Editor ganador = primero.resultado != null ? primero : segundo;
        Editor perdedor = primero.resultado != null ? segundo : primero;
        comprobar("reparacion perdedora rechazada por version", perdedor.error instanceof IllegalArgumentException);
        RequerimientoCompraOrden reparada = service.obtenerPorRequerimiento(53);
        igual("reparacion misma numeracion", Integer.valueOf(original.getIdOrdenCompra()), Integer.valueOf(reparada.getIdOrdenCompra()));
        igual("reparacion version incrementada", Integer.valueOf(1), Integer.valueOf(reparada.getVersionEdicion()));
        igual("reparacion auditoria del ganador", ganador.usuario, reparada.getModiUsr());
        comprobar("reparacion precio ganador", ganador.orden.getDetalles().get(0).getPrecioUnitarioEstimado()
                .compareTo(reparada.getDetalles().get(0).getPrecioUnitarioEstimado()) == 0);
        comprobar("reparacion total conocido", reparada.getTotal() != null);
        igual("reparacion cantidad conservada", Integer.valueOf(1), reparada.getDetalles().get(0).getCantidad());
        igual("reparacion descripcion fuente conservada", "CELULAR PRUEBA", reparada.getDetalles().get(0).getDescripcionItemVisible());
        igual("reparacion estado conservado", "5", filas(pool,
                "SELECT estado FROM compras.requerimiento WHERE id_requerimiento = ?", 53, null).get(0).get("estado"));
        comprobar("reparacion no muta precio requerimiento", filas(pool,
                "SELECT precio_unitario_estimado FROM compras.requerimiento_detalle WHERE id_detalle = ?", 2053, null)
                .get(0).get("precio_unitario_estimado") == null);
    }

    private static RequerimientoCompraDetalle cambioPrecio(int id, String precio) {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(Integer.valueOf(id));
        d.setPrecioUnitarioEstimado(new BigDecimal(precio)); return d;
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

        inicial.getDetalles().clear();
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
        obsoleta.getDetalles().clear();
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
        incorrecta.getDetalles().clear();
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
        invalidaSql.getDetalles().clear();
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
        uno.getDetalles().clear(); dos.getDetalles().clear();
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

    private static String valorComparable(ResultSet rs, ResultSetMetaData metadata,
            int columna) throws Exception {
        // PG42.1.4 cambia getString(TIMESTAMP) al activar protocolo binario:
        // .32654 y .326540 son el mismo valor. Conservar la precision real y
        // comparar el valor tipado, incluyendo todas las fechas de auditoria.
        if (metadata.getColumnType(columna) == java.sql.Types.TIMESTAMP) {
            java.sql.Timestamp valor = rs.getTimestamp(columna);
            return valor == null ? null : valor.toString();
        }
        return rs.getString(columna);
    }

    private static void timestampComparable(ComboPooledDataSource pool) throws Exception {
        Connection con = pool.getConnection(); Set<String> formatos = new HashSet<String>();
        try {
            for (int i = 0; i < 12; i++) {
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT TIMESTAMP '2026-10-26 16:21:40.326540' AS valor WHERE ?=1");
                try {
                    stmt.setInt(1, 1); ResultSet rs = stmt.executeQuery();
                    try {
                        comprobar("timestamp fixture devuelve fila " + i, rs.next());
                        formatos.add(rs.getString(1));
                        igual("timestamp preserva valor entre protocolos " + i, "2026-10-26 16:21:40.32654",
                                valorComparable(rs, rs.getMetaData(), 1));
                    } finally { rs.close(); }
                } finally { stmt.close(); }
            }
        } finally { con.close(); }
        igual("regresion ejercita protocolo texto y binario", Integer.valueOf(2), Integer.valueOf(formatos.size()));
        System.out.println("TIMESTAMP_COMPARABLE_TEXTO_BINARIO_OK formatos=" + formatos);
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
                                fila.put(columna, valorComparable(rs, metadata, i));
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
        comprobar(mensaje + ": esperado=" + esperado + ", actual=" + actual, esperado == null ? actual == null : esperado.equals(actual));
    }

    private static void comprobar(String mensaje, boolean condicion) {
        checks++;
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}