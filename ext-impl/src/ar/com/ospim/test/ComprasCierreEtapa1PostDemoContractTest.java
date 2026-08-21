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
        contiene(documentos, "reintento sin duplicar", "getFileEntry(");
        contiene(documentos, "reintento con mismos bytes", "Arrays.equals(");
    }

    private static void validarActualizacionContacto() throws Exception {
        String afiliado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "_afiliado_editable.jsp"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "_scripts_comunes.jsp"
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
                        + "_detalle_scripts_comunes.jsp"
        );
        String editable = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "_detalle_scripts_editable.jsp"
        );

        contiene(webKeys, "permiso especifico", "puedeEliminarDetalle(int estado)");
        contiene(webKeys, "estado cotizar", "|| esACotizar(estado);");
        contiene(helper, "autoridad backend", "public void borrarDetalle(");
        contiene(schema, "bloqueo del padre", "FOR UPDATE;");
        contiene(schema, "minimo persistido", "v_total_detalles_activos <= 1");
        contiene(comunes, "oculta ultima baja", "detallesCompra.length > 1");
        contiene(editable, "rechaza ultima baja", "detallesCompra.length <= 1");
    }

    private static void validarTipoPrestacionPorDetalle() throws Exception {
        String migration = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/"
                        + "20260821_cierre_etapa1_post_demo.sql"
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
                        + "_detalle_editor.jsp"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );
        String busqueda = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );

        contiene(migration, "tabla maestra", "CREATE TABLE compras.tipo_prestacion");
        contiene(migration, "FK sector", "REFERENCES compras.sector_requerimiento");
        contiene(migration, "tipo nullable historico", "ADD COLUMN id_tipo_prestacion SMALLINT;");
        contiene(migration, "Alimentacion", "(1, 'Alimentación', 'FARMACIA')");
        contiene(migration, "Medicamentos", "(2, 'Medicamentos', 'FARMACIA')");
        contiene(migration, "Protesis Trauma", "(3, 'Prótesis Traumatología'");
        contiene(migration, "Protesis Cardio", "(4, 'Prótesis Cardiología'");
        contiene(migration, "Protesis General", "(5, 'Prótesis General'");
        contiene(migration, "Insumos", "(6, 'Insumos'");
        contiene(migration, "Panales", "(7, 'Pañales'");
        contiene(migration, "siete filas", "count(*) FROM compras.tipo_prestacion) <> 7");
        contiene(migration, "valida sector", "v_id_sector_tipo <> v_id_sector_requerimiento");
        contiene(detalle, "modelo por detalle", "idTipoPrestacion");
        noContiene(encabezado, "sin duplicar en encabezado", "idTipoPrestacion");
        contiene(editor, "combo tipo", "detalle_id_tipo_prestacion");
        contiene(service, "funcion canonica clasificada", "guardar_requerimiento_detalle_clasificado");
        contiene(busqueda, "lectura clasificada", "get_requerimiento_detalle_clasificado");
        noContiene(migration, "conserva lectura historica", "DROP FUNCTION compras.get_requerimiento_detalle");
        noContiene(editor, "sin catalogo hardcodeado", "Alimentación");
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
