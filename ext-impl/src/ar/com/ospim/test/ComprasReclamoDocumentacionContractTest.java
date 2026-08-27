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
        String vinculacion = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraReclamoPrestacionalHelper.java"
        );
        String alta = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String fachadaVinculacion = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "RequerimientoCompraReclamoPrestacionalServiceUtil.java"
        );
        String pantallaArchivos = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "reclamo_prestacional_imagen.jsp"
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
                "documentacion Compras integrada en archivos RP",
                pantallaArchivos,
                "documentacion_compras.jsp"
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

        validarLecturaSinMaterializacion(
                vinculacion,
                alta,
                fachadaVinculacion,
                pantallaArchivos,
                listadoArchivos
        );

        System.out.println("COMPRAS_RECLAMO_DOCUMENTACION_OK");
    }

    private static void validarLecturaSinMaterializacion(
            String vinculacion,
            String alta,
            String fachadaVinculacion,
            String pantallaArchivos,
            String listadoArchivos) {

        File helperMaterializacion = new File(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraDocumentacionHelper.java"
        );

        if (helperMaterializacion.exists()) {
            throw new AssertionError(
                    "El helper de materializacion documental no fue eliminado."
            );
        }

        assertNotContains(
                "vinculacion sin copia documental",
                vinculacion,
                "adjuntarDocumentacion"
        );
        assertNotContains(
                "vinculacion sin compensacion documental",
                vinculacion,
                "compensarDocumentacion"
        );
        assertNotContains(
                "vinculacion sin contexto DL",
                vinculacion,
                "ServiceContext"
        );
        assertNotContains(
                "alta sin contexto DL de copia",
                alta,
                "serviceContextDocumentacion"
        );
        assertNotContains(
                "fachada sin sobrecarga documental",
                fachadaVinculacion,
                "ServiceContext"
        );
        assertContains(
                "vinculo se finaliza",
                vinculacion,
                "finalizarCreacion("
        );
        assertContains(
                "vinculo conserva rollback",
                vinculacion,
                "transaccion.rollback();"
        );
        assertContains(
                "lectura Compras separada del listado propio",
                pantallaArchivos,
                "documentacion_compras.jsp"
        );
        assertContains(
                "listado propio conserva carpeta RP",
                listadoArchivos,
                "\"ReclamosPrestacionales\""
        );
        assertContains(
                "listado propio conserva prefijo por RP",
                listadoArchivos,
                "RestrictionsFactoryUtil.ilike(\"title\", String.valueOf(reclamoprestacional.getId_reclamo())"
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

    private ComprasReclamoDocumentacionContractTest() {
    }
}
