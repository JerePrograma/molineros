package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class ComprasOrdenMedicaUiContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "EditarRequerimientoCompraAction.java"
        );
        String descarga = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "DescargarOrdenMedicaCompraAction.java"
        );
        String documentos = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/documentos/"
                        + "DocumentoLibraryComprasHelper.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java"
        );
        String form = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_campos_ocultos_formulario_componente.jsp"
        );
        String alta = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_orden_medica_carga_componente.jsp"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_scripts_edicion_guardado_componente.jsp"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/requerimiento_compra_orden_medica_consulta_componente.jsp"
        );
        String struts = leer(
                "ext-web/docroot/WEB-INF/struts-config.xml"
        );

        assertContains(
                "form multipart",
                form,
                "enctype=\"multipart/form-data\""
        );
        assertContains(
                "input real de archivo",
                alta,
                "type=\"file\""
        );
        assertContains(
                "archivo sin hidden",
                alta,
                "name=\"<%= nombreCampoArchivo %>\""
        );
        assertContains(
                "formatos exactos",
                alta,
                ".jpg,.jpeg,.png,image/jpeg,image/png"
        );
        assertContains(
                "fecha visible",
                alta,
                "Fecha del adjunto"
        );
        assertContains(
                "cliente valida extension",
                scripts,
                "jpe?g|png"
        );
        assertContains(
                "mueve nodo real",
                scripts,
                "form.appendChild(nodo)"
        );
        assertNotContains(
                "no copia valor del archivo a hidden",
                scripts,
                "archivo.value ="
        );
        assertContains(
                "servidor usa wrapper multipart",
                action,
                "new MultipartActionRequest("
        );
        assertContains(
                "alta usa contrato transaccional",
                action,
                ".guardarNuevoRequerimientoCompraConOrdenesMedicas("
        );
        assertOrden(
                "valida antes de guardar",
                action,
                "validarOrdenesMedicasDesdeRequest(",
                ".guardarNuevoRequerimientoCompraConOrdenesMedicas("
        );
        assertContains(
                "vista muestra fecha",
                vista,
                "Fecha del adjunto"
        );
        assertContains(
                "alta detecta carga informada",
                action,
                "if (hayCargaOrdenMedicaInformada("
        );
        assertContains(
                "alta admite cantidad cero",
                action,
                "if (cantidad < 0)"
        );
        assertContains(
                "cliente reutiliza regla central de sector",
                scripts,
                "!<portlet:namespace />esSectorSinCotizacionPrestadorCompra()"
        );
        assertContains(
                "cliente valida adjunto opcional informado",
                scripts,
                "|| <portlet:namespace />hayCargaOrdenMedicaInformadaPantalla()"
        );
        assertContains(
                "backend exceptua sectores internos",
                helper,
                "if (!requerimiento.esSectorSinCotizacionPrestador()"
        );
        assertContains(
                "resto conserva adjunto obligatorio",
                helper,
                "Debe seleccionar al menos un adjunto"
        );
        assertContains(
                "regla central incluye RRHH",
                webKeys,
                "\"RRHH\".equals(sector)"
        );
        assertContains(
                "regla central incluye SISTEMAS",
                webKeys,
                "\"SISTEMAS\".equals(sector)"
        );
        assertContains(
                "vista muestra original",
                vista,
                "getNombreOriginal()"
        );
        assertContains(
                "descarga resuelve por requerimiento",
                descarga,
                "getOrdenMedica("
        );
        assertContains(
                "descarga valida identidad DL",
                descarga,
                "obtenerEntradaOrdenMedicaValidada("
        );
        assertContains(
                "helper compara identidad persistida",
                documentos,
                "coincideIdentidadAsociacionDocumento("
        );
        assertContains(
                "descarga valida permiso DL",
                descarga,
                "DLFileEntryPermission.check("
        );
        assertNotContains(
                "cliente no envia folder",
                vista,
                "folderId"
        );
        assertNotContains(
                "cliente no envia nombre persistido",
                vista,
                "nombrePersistido"
        );
        assertContains(
                "mapping controlado",
                struts,
                "path=\"/compras/descargar_orden_medica\""
        );
    }

    private static String leer(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return new String(bytes, LATIN1);
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

    private static void assertOrden(
            String descripcion,
            String texto,
            String primero,
            String segundo) {

        int indicePrimero = texto.indexOf(primero);
        int indiceSegundo = texto.indexOf(segundo);

        if (indicePrimero < 0 || indiceSegundo <= indicePrimero) {
            throw new AssertionError(
                    descripcion
                            + ": orden invalido entre ["
                            + primero + "] y [" + segundo + "]"
            );
        }
    }

    private ComprasOrdenMedicaUiContractTest() {
    }
}
