package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato de resolución persistente de documentos Compras para RP.
 */
public final class ComprasDocumentacionRpDataContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    private static final String BASE =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/service/";

    public static void main(String[] args) throws Exception {
        String busqueda = leer(
                BASE + "BusquedaRequerimientoCompraServiceImpl.java"
        );
        String busquedaUtil = leer(
                BASE + "BusquedaRequerimientoCompraServiceUtil.java"
        );
        String edicion = leer(
                BASE + "EditarRequerimientoCompraServiceImpl.java"
        );
        String vinculo = leer(
                BASE
                        + "RequerimientoCompraReclamoPrestacionalServiceImpl.java"
        );
        String vinculoUtil = leer(
                BASE
                        + "RequerimientoCompraReclamoPrestacionalServiceUtil.java"
        );
        String vinculoHelper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraReclamoPrestacionalHelper.java"
        );
        String precarga = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraPrecargaHelper.java"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );

        verificarPresupuestoAdjudicado(
                busqueda,
                busquedaUtil,
                schema
        );
        verificarConsultaInversa(
                vinculo,
                vinculoHelper,
                vinculoUtil,
                schema
        );
        verificarConsultasDirectas(busqueda, edicion, schema);
        verificarTransaccionDirecta(vinculo, schema);
        verificarCoherenciaPrecarga(precarga);

        System.out.println("CONTRATO_DOCUMENTACION_COMPRAS_RP_DATOS_OK");
    }

    private static void verificarPresupuestoAdjudicado(
            String busqueda,
            String util,
            String schema) {

        String prestadoresSql = extraerConstante(
                busqueda,
                "SQL_LISTAR_PRESTADORES_ADJUDICADOS"
        );
        String presupuestosSql = extraerConstante(
                busqueda,
                "SQL_LISTAR_PRESUPUESTOS_PRESTADOR"
        );

        contiene(
                prestadoresSql,
                "prestadores mediante SQL directo",
                "SELECT DISTINCT d.id_prestador"
        );
        contiene(
                prestadoresSql,
                "prestador pertenece al requerimiento",
                "d.id_requerimiento = ?"
        );
        contiene(prestadoresSql, "solo detalles activos", "d.baja_fecha IS NULL");
        contiene(
                presupuestosSql,
                "documento del prestador",
                "rp.id_prestador = ?"
        );
        contiene(presupuestosSql, "solo tipo presupuesto", "rp.tipo_documento = 1");
        contiene(presupuestosSql, "solo documento activo", "rp.baja_fecha IS NULL");
        contiene(
                busqueda,
                "PreparedStatement para prestadores",
                "con.prepareStatement(SQL_LISTAR_PRESTADORES_ADJUDICADOS)"
        );
        contiene(
                busqueda,
                "PreparedStatement para presupuestos",
                "con.prepareStatement(SQL_LISTAR_PRESUPUESTOS_PRESTADOR)"
        );
        noContiene(
                schema,
                "sin función trivial de prestadores",
                "compras.listar_prestadores_adjudicados("
        );
        noContiene(
                schema,
                "sin función trivial de presupuestos",
                "compras.listar_presupuestos_prestador("
        );

        String getter = extraerMetodo(
                util,
                "public static RequerimientoCompraPresupuesto "
                        + "getPresupuestoAdjudicado("
        );
        contiene(util, "lista adjudicados", ".listarPrestadoresAdjudicados(");
        contiene(util, "rechaza múltiples adjudicados", "prestadores.size() != 1");
        contiene(getter, "lista presupuesto exacto", "getInstance().listarPresupuestosPrestador(");
        contiene(getter, "rechaza documentos ambiguos", "presupuestos.size() > 1");
    }

    private static void verificarConsultaInversa(
            String servicio,
            String helper,
            String util,
            String schema) {

        String consulta = extraerConstante(
                servicio,
                "SQL_GET_RELACION_POR_RECLAMO"
        );

        contiene(
                consulta,
                "consulta inversa mediante SQL directo",
                "compras.requerimiento_reclamo_prestacional"
        );
        contiene(consulta, "busca por RP", "relacion.id_reclamo_prestacional = ?");
        contiene(consulta, "exige estado", "relacion.estado = ?");
        contiene(
                consulta,
                "exige requerimiento existente",
                "INNER JOIN compras.requerimiento requerimiento"
        );
        contiene(
                consulta,
                "exige requerimiento activo",
                "requerimiento.baja_fecha IS NULL"
        );
        contiene(
                servicio,
                "PreparedStatement para consulta inversa",
                "con.prepareStatement(SQL_GET_RELACION_POR_RECLAMO)"
        );
        noContiene(
                schema,
                "sin función trivial de consulta inversa",
                "compras.get_requerimiento_por_reclamo_prestacional("
        );

        String getter = extraerMetodo(
                helper,
                "getRelacionPorReclamoPrestacional("
        );
        contiene(
                getter,
                "estado vinculado controlado por servidor",
                "WebKeysCompras.VINCULO_RECLAMO_VINCULADO"
        );
        contiene(
                getter,
                "RP sin vínculo devuelve null",
                "relaciones.isEmpty()"
        );
        contiene(
                getter,
                "múltiples vínculos fallan cerrados",
                "relaciones.size() > 1"
        );
        contiene(
                getter,
                "no elige un requerimiento arbitrario",
                "Existe más de un requerimiento vinculado"
        );
        contiene(
                getter,
                "revalida la única relación",
                "relacion.isVinculado()"
        );
        contiene(
                util,
                "API publica de consulta inversa",
                "getRelacionPorReclamoPrestacional("
        );
    }

    private static void verificarConsultasDirectas(
            String busqueda,
            String edicion,
            String schema) {

        String[][] consultas = new String[][] {
                {"SQL_GET_ESTADO", "get_estado_actual_requerimiento"},
                {"SQL_LISTAR_SECTORES", "listar_sector_requerimiento"},
                {"SQL_GET_SECTOR", "get_sector_requerimiento"},
                {"SQL_LISTAR_TIPOS_PRESTACION", "listar_tipos_prestacion"},
                {"SQL_GET_ORDEN_MEDICA", "get_requerimiento_orden_medica"},
                {"SQL_LISTAR_PRESUPUESTOS", "listar_requerimiento_presupuestos"},
                {"SQL_GET_PRESUPUESTO", "get_requerimiento_presupuesto"},
                {"SQL_GET_PEDIDO_COTIZACION_PRESTADOR", "get_pedido_cotizacion_prestador"},
                {"SQL_TIENE_SITUACION_MEDICA_VIGENTE", "tiene_situacion_medica_vigente"},
                {"SQL_LISTAR_PRESTADORES_ADJUDICADOS", "listar_prestadores_adjudicados"},
                {"SQL_LISTAR_PRESUPUESTOS_PRESTADOR", "listar_presupuestos_prestador"}
        };

        for (int i = 0; i < consultas.length; i++) {
            String sql = extraerConstante(busqueda, consultas[i][0]);
            contiene(sql, "consulta directa " + consultas[i][0], "SELECT");
            noContiene(sql, "sin CALL " + consultas[i][0], "{call");
            noContiene(
                    schema,
                    "sin función trivial " + consultas[i][1],
                    "compras." + consultas[i][1] + "("
            );
        }

        contiene(
                busqueda,
                "consultas simples con PreparedStatement",
                "PreparedStatement stmt"
        );

        String situacion = extraerConstante(
                busqueda,
                "SQL_TIENE_SITUACION_MEDICA_VIGENTE"
        );
        contiene(situacion, "situación activa", "sm.baja_fecha IS NULL");
        contiene(situacion, "vigencia abierta", "sm.vigen_hasta IS NULL");
        contiene(situacion, "vigencia futura", "sm.vigen_hasta > CURRENT_DATE");

        String pedido = extraerConstante(
                busqueda,
                "SQL_GET_PEDIDO_COTIZACION_PRESTADOR"
        );
        contiene(pedido, "pedido del intento vigente", "pc.intento = rcp.intentos");
        contiene(pedido, "pedido enviado o cotizado", "'ENVIADO', 'COTIZADO'");

        String borrar = extraerMetodo(
                edicion,
                "public void borrarRequerimientoCompra("
        );
        contiene(borrar, "borrado delega cambio de estado", "cambiarEstado(");
        contiene(
                borrar,
                "borrado conserva estado anulado",
                "WebKeysCompras.ESTADO_ANULADO"
        );
        noContiene(edicion, "sin wrapper Java de borrado", "SQL_BORRAR_REQUERIMIENTO =");
        noContiene(
                schema,
                "sin wrapper SQL de borrado",
                "compras.borrar_requerimiento("
        );
    }

    private static void verificarTransaccionDirecta(
            String servicio,
            String schema) {

        String relacion = extraerConstante(servicio, "SQL_GET_RELACION");
        contiene(
                relacion,
                "relación por requerimiento directa",
                "compras.requerimiento_reclamo_prestacional"
        );

        String relaciones = extraerConstante(
                servicio,
                "SQL_GET_RELACIONES_BATCH"
        );
        contiene(relaciones, "lote conserva array PostgreSQL", "CAST(? AS INTEGER[])");
        contiene(
                relaciones,
                "lote sólo vinculado",
                "id_reclamo_prestacional IS NOT NULL"
        );

        String bloqueoSql = extraerConstante(
                servicio,
                "SQL_BLOQUEAR_REQUERIMIENTO"
        );
        contiene(
                bloqueoSql,
                "advisory lock canónico",
                "pg_advisory_xact_lock(5391184, ?)"
        );
        String bloqueo = extraerMetodo(
                servicio,
                "public boolean bloquearRequerimiento("
        );
        contiene(bloqueo, "lock usa conexión recibida", "con.prepareStatement(");
        noContiene(
                bloqueo,
                "lock no abre otra conexión",
                "ConnectionHelper.getConnection"
        );

        String estadoSql = extraerConstante(
                servicio,
                "SQL_GET_ESTADO_REQUERIMIENTO_FOR_UPDATE"
        );
        contiene(estadoSql, "estado bloquea fila", "FOR UPDATE");
        String estado = extraerMetodo(
                servicio,
                "public int getEstadoRequerimientoForUpdate("
        );
        contiene(estado, "estado usa conexión recibida", "con.prepareStatement(");
        noContiene(
                estado,
                "estado no abre otra conexión",
                "ConnectionHelper.getConnection"
        );

        String liberarSql = extraerConstante(servicio, "SQL_LIBERAR");
        contiene(liberarSql, "liberación directa", "DELETE FROM");
        contiene(liberarSql, "libera sólo reserva", "estado = 'RESERVADO'");
        String liberar = extraerMetodo(
                servicio,
                "public boolean liberarReserva("
        );
        contiene(liberar, "resultado por filas afectadas", "executeUpdate() > 0");

        String[] eliminadas = new String[] {
                "get_requerimiento_reclamo_prestacional",
                "listar_requerimientos_reclamo_prestacional_vinculados",
                "get_requerimiento_por_reclamo_prestacional",
                "bloquear_requerimiento_reclamo_prestacional",
                "get_estado_requerimiento_for_update",
                "liberar_reserva_reclamo_prestacional"
        };
        for (int i = 0; i < eliminadas.length; i++) {
            noContiene(
                    schema,
                    "sin wrapper RP " + eliminadas[i],
                    "compras." + eliminadas[i] + "("
            );
        }
    }

    private static void verificarCoherenciaPrecarga(String precarga) {
        String metodo = extraerMetodo(
                precarga,
                "public static List<PrestacionesReclamo> crearPrestaciones("
        );
        contiene(
                metodo,
                "conserva id de prestador consolidado",
                "Integer idPrestadorComprobante"
        );
        contiene(
                metodo,
                "compara id y no solo CUIT",
                "idPrestadorComprobante.intValue()"
        );
        contiene(
                metodo,
                "compara contra cada detalle",
                "idPrestadorDetalle.intValue()"
        );
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), LATIN1);
    }

    private static String extraerConstante(
            String contenido,
            String nombre) {

        int inicio = contenido.indexOf(nombre);
        if (inicio < 0) {
            throw new AssertionError("No se encontro constante: " + nombre);
        }

        int fin = contenido.indexOf(';', inicio);
        if (fin < 0) {
            throw new AssertionError("Constante sin cierre: " + nombre);
        }

        return contenido.substring(inicio, fin + 1);
    }

    private static String extraerMetodo(
            String contenido,
            String firma) {

        int inicio = contenido.indexOf(firma);
        if (inicio < 0) {
            throw new AssertionError("No se encontro firma: " + firma);
        }

        int apertura = contenido.indexOf('{', inicio);
        int nivel = 0;

        for (int i = apertura; i < contenido.length(); i++) {
            char actual = contenido.charAt(i);
            if (actual == '{') {
                nivel++;
            } else if (actual == '}') {
                nivel--;
                if (nivel == 0) {
                    return contenido.substring(inicio, i + 1);
                }
            }
        }

        throw new AssertionError("Metodo sin cierre: " + firma);
    }

    private static void contiene(
            String contenido,
            String etiqueta,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": falta [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String contenido,
            String etiqueta,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": contiene [" + prohibido + "]"
            );
        }
    }

    private ComprasDocumentacionRpDataContractTest() {
    }
}
