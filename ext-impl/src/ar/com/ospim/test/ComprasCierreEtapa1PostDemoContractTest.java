package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class ComprasCierreEtapa1PostDemoContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        validarDocumentacionReclamo();
        validarActualizacionContacto();
        validarBajaDetalleCotizado();
        validarTipoPrestacionPorDetalle();

        System.out.println("COMPRAS_CIERRE_ETAPA1_POST_DEMO_OK");
    }

    private static void validarDocumentacionReclamo() throws Exception {
        String action = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String cierre = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraReclamoPrestacionalHelper.java"
        );
        String documentos = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraDocumentacionHelper.java"
        );

        contiene(action, "contexto DL", "ServiceContextFactory.getInstance(");
        noContiene(
                action,
                "copia documental posterior al cierre",
                ".adjuntarDocumentacion("
        );
        antes(
                cierre,
                ".adjuntarDocumentacionControlada(",
                "finalizarCreacion("
        );
        antes(cierre, "finalizarCreacion(", "transaccion.commit();");
        contiene(
                cierre,
                "sobrecarga sin documentos falla cerrada",
                "No se puede crear el Reclamo Prestacional sin"
        );
        contiene(
                cierre,
                "compensacion documental",
                "documentacionHelper.compensarDocumentacion("
        );
        contiene(
                documentos,
                "todas las ordenes medicas",
                "i < ordenesMedicas.size();"
        );
        noContiene(documentos, "sin primera orden", "ordenesMedicas.get(0)");
        contiene(
                documentos,
                "pedido historico adjudicado",
                ".getPedidoCotizacionAdjudicado("
        );
        contiene(
                documentos,
                "cotizacion adjudicada",
                ".getPresupuestoAdjudicado("
        );
        contiene(documentos, "bytes binarios", "byte[] contenido");
        contiene(documentos, "identidad fuente", "Set<Long> archivosFuente");
        contiene(documentos, "reintento sin duplicar", "getFileEntryByTitle(");
        contiene(documentos, "reintento con mismos bytes", "Arrays.equals(");
    }

    private static void validarActualizacionContacto() throws Exception {
        String afiliado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_afiliado_editable_componente.jsp"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_scripts_base_componente.jsp"
        );
        String token = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "ActualizarContactoAfiliadoCompraToken.java"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "ActualizarContactoAfiliadoCompraAction.java"
        );
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");

        contiene(afiliado, "permiso ABM", "puedeActualizarContactoAfiliado =\n        puedeABM;");
        contiene(afiliado, "boton externo", "botonActualizarContactoAfiliado");
        contiene(afiliado, "alta deshabilitada", "disabled=\"disabled\"");
        contiene(scripts, "habilita por identidad", "establecerDisponibilidadActualizarContacto(");
        contiene(scripts, "token enviado", "contacto_afiliado_token");
        contiene(scripts, "identidad vinculada", "cmd:\n                                    'bind'");
        contiene(token, "contexto en sesion", "SESSION_CONTEXTOS");
        contiene(token, "identidad persistida", "contexto.cuilTitular");
        contiene(token, "vinculo de alta", "public static void vincular(");
        contiene(action, "permiso servidor", "ROL_ABM_COMPRAS");
        contiene(action, "afiliado existente", "getAfiliadoEntry(");
        contiene(
                struts,
                "action protegida de Compras",
                "ActualizarContactoAfiliadoCompraAction"
        );
    }

    private static void validarBajaDetalleCotizado() throws Exception {
        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String comunes = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_base_componente.jsp"
        );
        String editable = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );

        contiene(webKeys, "permiso especifico", "puedeEliminarDetalle(int estado)");
        contiene(webKeys, "estado cotizar", "|| esACotizar(estado);");
        contiene(helper, "autoridad backend", "public void borrarDetalle(");
        contiene(schema, "bloqueo del padre", "FOR UPDATE;");
        contiene(schema, "mínimo al guardar", "v_total_conservados <= 0");
        contiene(
                comunes,
                "muestra última baja al cotizar",
                "puedeCotizarDetalle ? \"true\" : \"false\""
        );
        contiene(
                editable,
                "baja diferida en cotización",
                "detalleDeletedIds.push("
        );
        contiene(
                editable,
                "último detalle se valida al guardar",
                "Debe conservar al menos una prestación"
        );
    }

    private static void validarTipoPrestacionPorDetalle() throws Exception {
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String detalle = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompraDetalle.java"
        );
        String encabezado = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompra.java"
        );
        String editor = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_editor_componente.jsp"
        );
        String scriptsEditable = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );
        String busqueda = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );

        contiene(schema, "tabla maestra", "CREATE TABLE compras.tipo_prestacion");
        contiene(schema, "FK sector", "REFERENCES compras.sector_requerimiento");
        contiene(schema, "tipo nullable", "id_tipo_prestacion SMALLINT");
        contiene(schema, "Alimentacion", "(1, 'Alimentación', 'FARMACIA')");
        contiene(schema, "Medicamentos", "(2, 'Medicamentos', 'FARMACIA')");
        contiene(schema, "Protesis Trauma", "(3, 'Prótesis Traumatología'");
        contiene(schema, "Protesis Cardio", "(4, 'Prótesis Cardiología'");
        contiene(schema, "Protesis General", "(5, 'Prótesis General'");
        contiene(schema, "Insumos", "(6, 'Insumos'");
        contiene(schema, "Panales", "(7, 'Pañales'");
        contiene(schema, "valida sector", "v_id_sector_tipo <> v_id_sector_requerimiento");
        contiene(detalle, "modelo por detalle", "idTipoPrestacion");
        noContiene(encabezado, "sin duplicar en encabezado", "idTipoPrestacion");
        contiene(editor, "combo tipo", "detalle_id_tipo_prestacion");
        contiene(service, "funcion canonica clasificada", "guardar_requerimiento_detalle_clasificado");
        contiene(busqueda, "lectura clasificada", "get_requerimiento_detalle_clasificado");
        noContiene(schema, "conserva lectura canonica", "DROP FUNCTION compras.get_requerimiento_detalle");
        noContiene(editor, "sin catalogo hardcodeado", "Alimentación");
        contiene(
                scriptsEditable,
                "etiqueta visible con jQuery legacy",
                "opcionTipo.text(tipo.descripcion);"
        );
        noContiene(
                scriptsEditable,
                "sin constructor de atributos incompatible",
                "jQuery('<option/>', {"
        );
    }

    private static String leer(String path) throws Exception {
        return new String(
                Files.readAllBytes(new File(path).toPath()),
                LATIN1
        );
    }

    private static void contiene(
            String texto,
            String descripcion,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion + ": falta [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String texto,
            String descripcion,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion + ": contiene [" + prohibido + "]"
            );
        }
    }

    private static void antes(
            String texto,
            String primero,
            String segundo) {

        int a = texto.indexOf(primero);
        int b = texto.indexOf(segundo, a + 1);

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden invalido: " + primero + " / " + segundo
            );
        }
    }

    private ComprasCierreEtapa1PostDemoContractTest() {
    }
}
