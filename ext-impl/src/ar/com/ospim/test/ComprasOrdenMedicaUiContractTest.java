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
        String form = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_form_hidden.jsp"
        );
        String alta = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_orden_medica_alta.jsp"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_scripts_edicion.jsp"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/_orden_medica_vista.jsp"
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
                "name=\"orden_medica\""
        );
        assertContains(
                "formatos exactos",
                alta,
                ".jpg,.jpeg,.png,image/jpeg,image/png"
        );
        assertContains(
                "fecha obligatoria visible",
                alta,
                "Fecha de la orden m\u00e9dica:"
        );
        assertContains(
                "cliente valida extension",
                scripts,
                "jpe?g|png"
        );
        assertContains(
                "mueve nodo real",
                scripts,
                "form.appendChild(archivo)"
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
                ".guardarNuevoRequerimientoCompraConOrdenMedica("
        );
        assertOrden(
                "valida antes de guardar",
                action,
                "gestorDocumento.validarOrdenMedica(",
                ".guardarNuevoRequerimientoCompraConOrdenMedica("
        );
        assertContains(
                "vista muestra fecha",
                vista,
                "Fecha de la orden m\u00e9dica"
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
                "coincideIdentidad(identidad, entry)"
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
