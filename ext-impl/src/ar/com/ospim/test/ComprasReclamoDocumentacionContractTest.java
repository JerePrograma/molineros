package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class ComprasReclamoDocumentacionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String endpoint = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "DescargarDocumentoCompraReclamoAction.java"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "documentacion_compras.jsp"
        );
        String wrapper = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "editar_reclamosprestacionales_entry.jsp"
        );
        String viewReclamo = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String struts = leer(
                "ext-web/docroot/WEB-INF/struts-config.xml"
        );
        String materializacion = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraDocumentacionHelper.java"
        );
        String compensacion = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/documentos/"
                        + "DocumentoLibraryComprasHelper.java"
        );
        String listadoArchivos = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "reclamo_prestacional_imagenes_search_documentos.jsp"
        );

        assertContains(
                "consulta inversa RP a requerimiento",
                vista,
                ".getRelacionPorReclamoPrestacional("
        );
        assertContains(
                "bloque condicionado a vinculo valido",
                vista,
                ".isVinculado()"
        );
        assertContains(
                "RP exacto en vista",
                vista,
                ".getIdReclamoPrestacionalInt()"
        );
        assertContains(
                "titulo documentacion Compras",
                vista,
                "Documentaci&oacute;n de Compras"
        );
        assertContains(
                "Orden medica por tipo 2",
                vista,
                ".TIPO_DOCUMENTO_ORDEN_MEDICA"
        );
        assertContains(
                "fecha Orden medica",
                vista,
                ".getFechaDocumento()"
        );
        assertContains(
                "presupuesto por adjudicacion explicita",
                vista,
                ".getPresupuestoAdjudicado("
        );
        assertContains(
                "presupuesto tipo 1",
                vista,
                ".TIPO_DOCUMENTO_PRESUPUESTO"
        );
        assertContains(
                "documento activo vista",
                vista,
                ".isActivo()"
        );
        assertContains(
                "solapa legacy de archivos",
                wrapper,
                "reclamo_prestacional_imagen.jsp"
        );

        assertContains(
                "endpoint recibe RP controlado",
                endpoint,
                "\"id_reclamo_prestacional\""
        );
        assertContains(
                "endpoint recibe id documento controlado",
                endpoint,
                "\"id_requerimiento_presupuesto\""
        );
        assertNotContains(
                "endpoint no recibe folder",
                endpoint,
                "ParamUtil.getLong(actionRequest, \"folderId\""
        );
        assertNotContains(
                "endpoint no recibe nombre",
                endpoint,
                "ParamUtil.getString(actionRequest, \"name\""
        );
        assertNotContains(
                "endpoint no recibe path",
                endpoint,
                "ParamUtil.getString(actionRequest, \"path\""
        );
        assertContains(
                "endpoint consulta relacion persistente",
                endpoint,
                ".getRelacionPorReclamoPrestacional("
        );
        assertContains(
                "endpoint exige RP exacto",
                endpoint,
                "relacion.getIdReclamoPrestacionalInt()"
        );
        assertContains(
                "endpoint exige requerimiento exacto",
                endpoint,
                "!= idRequerimientoCompra"
        );
        assertContains(
                "endpoint resuelve todas las Órdenes médicas",
                endpoint,
                ".listarOrdenesMedicas("
        );
        assertContains(
                "endpoint resuelve adjudicado exacto",
                endpoint,
                ".getPresupuestoAdjudicado("
        );
        assertNotContains(
                "endpoint no lista presupuestos",
                endpoint,
                ".listarPresupuestos("
        );
        assertNotContains(
                "endpoint no usa presupuesto arbitrario",
                endpoint,
                ".getPresupuesto("
        );
        assertContains(
                "endpoint rechaza documento dado de baja",
                endpoint,
                "!documento.isActivo()"
        );
        assertContains(
                "endpoint valida tipo Orden medica",
                endpoint,
                ".TIPO_DOCUMENTO_ORDEN_MEDICA"
        );
        assertContains(
                "endpoint valida tipo presupuesto",
                endpoint,
                ".TIPO_DOCUMENTO_PRESUPUESTO"
        );
        assertContains(
                "endpoint valida identidad completa DL",
                endpoint,
                ".coincideIdentidadAsociacionDocumento("
        );
        assertContains(
                "endpoint valida permiso DL",
                endpoint,
                "DLFileEntryPermission.check("
        );
        assertContains(
                "endpoint valida rol consulta RP",
                endpoint,
                ".ROL_CONSULTA_RECLAMOS_PRESTACIONALES"
        );
        assertContains(
                "endpoint valida rol ABM RP",
                endpoint,
                ".ROL_ABM_RECLAM_PREST"
        );
        assertContains(
                "endpoint rechaza RP dado de baja",
                endpoint,
                "reclamo.getBaja_fecha() != null"
        );
        assertContains(
                "endpoint limpia stream DL",
                endpoint,
                "ServletResponseUtil.cleanUp("
        );
        assertNotContains(
                "endpoint no pierde referencia antes del cleanup",
                endpoint,
                "contentType\n            );\n            input = null;"
        );
        assertContains(
                "mapping descarga segura",
                struts,
                "path=\"/autorizaciones/descargar_documento_compra_reclamo\""
        );

        assertContains(
                "nonce Compras preservado",
                wrapper,
                "PARAM_RECLAMO_PRESTACIONAL_NONCE"
        );
        assertContains(
                "origen Compras preservado",
                wrapper,
                "portletURL.setParameter(\n            \"origen\","
        );
        assertContains(
                "handoff sigue fail closed",
                viewReclamo,
                "handoffReclamoComprasValido"
        );
        assertContains(
                "origen no Compras sigue en VIEW",
                viewReclamo,
                "request.setAttribute(\n            Constants.CMD,\n            Constants.VIEW"
        );

        validarMaterializacionRp(
                materializacion,
                compensacion,
                listadoArchivos
        );

        System.out.println("COMPRAS_RECLAMO_DOCUMENTACION_OK");
    }

    private static void validarMaterializacionRp(
            String materializacion,
            String compensacion,
            String listadoArchivos) {

        assertContains(
                "T1 Orden medica separada por RP",
                materializacion,
                "String.valueOf(idReclamoPrestacional)\n"
                        + "                            + \"-ORDEN-MEDICA-\""
        );
        assertContains(
                "T1 pedido separado por RP",
                materializacion,
                "+ \"-PEDIDO-COTIZACION-\""
        );
        assertContains(
                "T1 cotizacion separada por RP",
                materializacion,
                "+ \"-COTIZACION-ADJUDICADA-\""
        );
        assertContains(
                "T1 listado legacy por prefijo RP",
                listadoArchivos,
                "RestrictionsFactoryUtil.ilike(\"title\", String.valueOf(reclamoprestacional.getId_reclamo())"
        );

        assertNotContains(
                "T2 nombre original no es titulo de Orden medica",
                materializacion,
                "String titulo = ordenMedica.getNombreOriginal();"
        );
        assertNotContains(
                "T2 nombre original no es titulo de pedido",
                materializacion,
                "pedidoCotizacion.getNombreOriginal(),"
        );
        assertNotContains(
                "T2 nombre original no es titulo de cotizacion",
                materializacion,
                "presupuestoAdjudicado.getNombreOriginal(),"
        );

        assertContains(
                "T3 identidad idempotente por titulo",
                materializacion,
                ".getFileEntryByTitle("
        );
        assertContains(
                "T3 mismos bytes reconocidos",
                materializacion,
                "Arrays.equals("
        );
        assertBefore(
                "T3 consulta antes del alta",
                materializacion,
                ".getFileEntryByTitle(",
                ".addFileEntry("
        );
        assertContains(
                "T3 preexistente no se registra como creado",
                materializacion,
                "if (documentoCreado != null)"
        );

        assertContains(
                "T4 contenido distinto falla cerrado",
                materializacion,
                "identidad o contenido diferente"
        );
        assertNotContains(
                "T4 no sobrescribe Document Library",
                materializacion,
                ".addOrOverwriteFileEntry("
        );
        assertContains(
                "T4 alta sin overwrite",
                materializacion,
                ".addFileEntry("
        );

        assertContains(
                "T5 fuente solo se lee por fileEntryId",
                materializacion,
                ".getDlFileEntryId()"
        );
        assertContains(
                "T5 compensacion exige identidad creada",
                compensacion,
                "documento.getFileEntryId()"
        );
        assertContains(
                "T5 compensacion valida UUID creado",
                compensacion,
                "documento.getUuid()"
        );
        assertContains(
                "T6 compensacion limitada a creados",
                materializacion,
                ".eliminarDocumentoCreado(documentos.get(i))"
        );
        assertContains(
                "T6 reintento devuelve documento no creado",
                materializacion,
                "return null;"
        );
    }

    private static String leer(String path) throws Exception {
        return new String(
                Files.readAllBytes(new File(path).toPath()),
                LATIN1
        );
    }

    private static void assertContains(
            String descripcion,
            String texto,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion + ": falta [" + esperado + "]"
            );
        }
    }

    private static void assertNotContains(
            String descripcion,
            String texto,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion + ": contiene [" + prohibido + "]"
            );
        }
    }

    private static void assertBefore(
            String descripcion,
            String texto,
            String primero,
            String segundo) {

        int posicionPrimero = texto.indexOf(primero);
        int posicionSegundo = texto.indexOf(segundo);

        if (posicionPrimero < 0
                || posicionSegundo < 0
                || posicionPrimero >= posicionSegundo) {

            throw new AssertionError(
                    descripcion
                            + ": orden invalido ["
                            + primero
                            + "] / ["
                            + segundo
                            + "]"
            );
        }
    }

    private ComprasReclamoDocumentacionContractTest() {
    }
}
