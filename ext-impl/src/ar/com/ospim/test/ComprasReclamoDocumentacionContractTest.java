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

        assertContains(
                "consulta inversa RP a requerimiento",
                vista,
                ".getRelacionPorReclamoPrestacional("
        );
        assertContains(
                "bloque condicionado a vinculo valido",
                vista,
                "relacionDocumentacionCompras.isVinculado()"
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
                "include focalizado",
                wrapper,
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
                "endpoint resuelve Orden medica",
                endpoint,
                ".getOrdenMedica("
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
                "coincideIdentidad(identidad, entry)"
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
                "ServletResponseUtil.cleanUp(input)"
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
