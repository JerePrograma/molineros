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
        String vinculo = leer(
                BASE
                        + "RequerimientoCompraReclamoPrestacionalServiceImpl.java"
        );
        String vinculoUtil = leer(
                BASE
                        + "RequerimientoCompraReclamoPrestacionalServiceUtil.java"
        );
        String precarga = leer(
                BASE + "ReclamoPrestacionalCompraPrecargaServiceUtil.java"
        );

        verificarPresupuestoAdjudicado(busqueda, busquedaUtil);
        verificarConsultaInversa(vinculo, vinculoUtil);
        verificarCoherenciaPrecarga(precarga);

        System.out.println("CONTRATO_DOCUMENTACION_COMPRAS_RP_DATOS_OK");
    }

    private static void verificarPresupuestoAdjudicado(
            String busqueda,
            String util) {

        String sqlPrestador = extraerConstante(
                busqueda,
                "SQL_GET_PRESTADOR_ADJUDICADO"
        );
        contiene(
                sqlPrestador,
                "prestador deriva de detalles",
                "SELECT DISTINCT d.id_prestador"
        );
        contiene(
                sqlPrestador,
                "prestador pertenece al requerimiento",
                "d.id_requerimiento = ?"
        );
        contiene(
                sqlPrestador,
                "solo detalles activos",
                "d.baja_fecha IS NULL"
        );
        noContiene(sqlPrestador, "sin orden implicito", "ORDER BY");

        String sqlDocumento = extraerConstante(
                busqueda,
                "SQL_GET_PRESUPUESTO_ADJUDICADO"
        );
        contiene(
                sqlDocumento,
                "documento del requerimiento",
                "rp.id_requerimiento = ?"
        );
        contiene(
                sqlDocumento,
                "documento del prestador adjudicado",
                "rp.id_prestador = ?"
        );
        contiene(
                sqlDocumento,
                "solo tipo presupuesto",
                "rp.tipo_documento = 1"
        );
        contiene(
                sqlDocumento,
                "solo documento activo",
                "rp.baja_fecha IS NULL"
        );
        noContiene(sqlDocumento, "sin ultimo id", "MAX(");
        noContiene(sqlDocumento, "sin primer resultado", "LIMIT 1");
        noContiene(sqlDocumento, "sin orden de listado", "ORDER BY");

        String resolver = extraerMetodo(
                busqueda,
                "private int resolverPrestadorAdjudicado("
        );
        contiene(
                resolver,
                "rechaza ausencia de adjudicacion",
                "if (!rs.next())"
        );
        contiene(
                resolver,
                "rechaza prestador nulo o invalido",
                "rs.wasNull() || idPrestador <= 0"
        );
        contiene(
                resolver,
                "rechaza prestadores distintos",
                "if (rs.next())"
        );

        String getter = extraerMetodo(
                busqueda,
                "public RequerimientoCompraPresupuesto "
                        + "getPresupuestoAdjudicado("
        );
        contiene(
                getter,
                "resuelve primero el prestador",
                "resolverPrestadorAdjudicado("
        );
        contiene(
                getter,
                "usa consulta documental explicita",
                "SQL_GET_PRESUPUESTO_ADJUDICADO"
        );
        contiene(
                util,
                "API publica de presupuesto adjudicado",
                "public static RequerimientoCompraPresupuesto "
                        + "getPresupuestoAdjudicado("
        );
    }

    private static void verificarConsultaInversa(
            String servicio,
            String util) {

        String sql = extraerConstante(
                servicio,
                "SQL_GET_RELACION_POR_RECLAMO"
        );
        contiene(
                sql,
                "consulta tabla persistente",
                "compras.requerimiento_reclamo_prestacional relacion"
        );
        contiene(
                sql,
                "busca por RP",
                "relacion.id_reclamo_prestacional = ?"
        );
        contiene(sql, "exige estado vinculado", "relacion.estado = ?");
        contiene(
                sql,
                "exige requerimiento existente",
                "JOIN compras.requerimiento requerimiento"
        );
        contiene(
                sql,
                "exige requerimiento activo",
                "requerimiento.baja_fecha IS NULL"
        );

        String getter = extraerMetodo(
                servicio,
                "getRelacionPorReclamoPrestacional("
        );
        contiene(
                getter,
                "estado vinculado controlado por servidor",
                "WebKeysCompras.VINCULO_RECLAMO_VINCULADO"
        );
        contiene(
                getter,
                "RP sin vinculo devuelve null",
                "if (!rs.next())"
        );
        contiene(
                getter,
                "multiples vinculos fallan cerrados",
                "if (rs.next())"
        );
        contiene(
                getter,
                "no elige un requerimiento arbitrario",
                "Existe mas de un requerimiento vinculado"
        );
        contiene(
                getter,
                "revalida bean vinculado",
                "relacion.isVinculado()"
        );
        contiene(
                util,
                "API publica de consulta inversa",
                "getRelacionPorReclamoPrestacional("
        );
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
