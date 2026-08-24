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
        String busquedaHelper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "BusquedaRequerimientoCompraHelper.java"
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
                busquedaHelper,
                busquedaUtil,
                schema
        );
        verificarConsultaInversa(
                vinculo,
                vinculoHelper,
                vinculoUtil,
                schema
        );
        verificarCoherenciaPrecarga(precarga);

        System.out.println("CONTRATO_DOCUMENTACION_COMPRAS_RP_DATOS_OK");
    }

    private static void verificarPresupuestoAdjudicado(
            String busqueda,
            String helper,
            String util,
            String schema) {

        contiene(
                busqueda,
                "prestadores mediante función PostgreSQL",
                "compras.listar_prestadores_adjudicados(?)"
        );
        contiene(
                busqueda,
                "presupuestos mediante función PostgreSQL",
                "compras.listar_presupuestos_prestador(?,?)"
        );
        contiene(schema, "prestador deriva de detalles", "SELECT DISTINCT d.id_prestador");
        contiene(schema, "prestador pertenece al requerimiento", "d.id_requerimiento = p_id_requerimiento");
        contiene(schema, "solo detalles activos", "d.baja_fecha IS NULL");
        contiene(schema, "documento del prestador", "rp.id_prestador = p_id_prestador");
        contiene(schema, "solo tipo presupuesto", "rp.tipo_documento = 1");
        contiene(schema, "solo documento activo", "rp.baja_fecha IS NULL");

        String getter = extraerMetodo(
                helper,
                "public RequerimientoCompraPresupuesto getPresupuestoAdjudicado("
        );
        contiene(getter, "lista adjudicados", "service.listarPrestadoresAdjudicados(");
        contiene(getter, "rechaza múltiples adjudicados", "prestadores.size() != 1");
        contiene(getter, "lista presupuesto exacto", "service.listarPresupuestosPrestador(");
        contiene(getter, "rechaza documentos ambiguos", "presupuestos.size() > 1");
        contiene(
                util,
                "API publica de presupuesto adjudicado",
                "public static RequerimientoCompraPresupuesto "
                        + "getPresupuestoAdjudicado("
        );
    }

    private static void verificarConsultaInversa(
            String servicio,
            String helper,
            String util,
            String schema) {

        contiene(
                servicio,
                "consulta inversa mediante función PostgreSQL",
                "compras.get_requerimiento_por_reclamo_prestacional(?,?)"
        );
        contiene(schema, "consulta tabla persistente", "compras.requerimiento_reclamo_prestacional relacion");
        contiene(schema, "busca por RP", "relacion.id_reclamo_prestacional = p_id_reclamo_prestacional");
        contiene(schema, "exige estado", "relacion.estado = p_estado");
        contiene(
                schema,
                "exige requerimiento existente",
                "INNER JOIN compras.requerimiento requerimiento"
        );
        contiene(
                schema,
                "exige requerimiento activo",
                "requerimiento.baja_fecha IS NULL"
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
