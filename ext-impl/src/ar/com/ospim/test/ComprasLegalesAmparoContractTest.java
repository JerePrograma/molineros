package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Contrato focalizado del alta del RP desde Compras.
 */
public final class ComprasLegalesAmparoContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String precarga = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraPrecargaHelper.java"
        );
        String requerimiento = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompra.java"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );
        String alta = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "ReclamosBaseAction.java"
        );
        String persistencia = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamoPrestacionServiceImpl.java"
        );
        String scriptEdicion = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "view_reclamo_scripts_revision.jsp"
        );

        String crearReclamo = extraerMetodo(
                precarga,
                "public static ReclamoPrestacional crearReclamo("
        );
        contiene(
                crearReclamo,
                "la precarga asigna AMPARO",
                "reclamo.setAmparo("
        );
        String trasladoAmparo = extraerEntre(
                crearReclamo,
                "reclamo.setAmparo(",
                ");"
        );
        contiene(
                trasladoAmparo,
                "LEGALES se traslada sin conversion",
                "requerimiento.isLegales()"
        );
        noContiene(
                trasladoAmparo,
                "LEGALES no se invierte",
                "!"
        );
        antes(
                crearReclamo,
                "reclamo.setAmparo(",
                "return reclamo;"
        );

        String isLegales = extraerMetodo(
                requerimiento,
                "public boolean isLegales("
        );
        contiene(
                isLegales,
                "LEGALES null se interpreta como false",
                "Boolean.TRUE.equals(legales)"
        );
        contiene(
                vista,
                "el checkbox conserva el nombre legacy",
                "name=\"<%= reclamoPortletNamespace %>chk_amparo\""
        );
        contiene(
                vista,
                "el checkbox refleja el bean",
                "reclamoprestacional.isAmparo()  ? \"checked\" : \"Unchecked\""
        );
        contiene(
                alta,
                "el alta conserva el parametro legacy",
                "ParamUtil.getBoolean(req, \"chk_amparo\")"
        );
        contiene(
                persistencia,
                "la persistencia conserva AMPARO",
                "reclamoPrestacional.isAmparo()"
        );

        String editarPrestacion = extraerMetodo(
                scriptEdicion,
                "function <%= reclamoPortletNamespace %>"
                        + "editarPrestacionSeleccionada("
        );
        String validacionCodigoCero = extraerEntre(
                editarPrestacion,
                "if (codigoSeguimiento_filtro_edit<1",
                ") {"
        );
        contiene(
                validacionCodigoCero,
                "el codigo canonico cero seleccionado puede editarse",
                "codigoSeguimiento_filtro_edit!='0'"
        );

        System.out.println("CONTRATO_COMPRAS_LEGALES_AMPARO_OK");
    }

    private static String leer(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        sinBom(path, bytes);
        String contenido = new String(bytes, LATIN1);
        noContiene(
                contenido,
                path + " sin mojibake C3",
                String.valueOf((char) 0x00C3)
        );
        noContiene(
                contenido,
                path + " sin mojibake C2",
                String.valueOf((char) 0x00C2)
        );
        noContiene(
                contenido,
                path + " sin reemplazo",
                String.valueOf((char) 0xFFFD)
        );
        return contenido;
    }

    private static void sinBom(String path, byte[] bytes) {
        boolean utf8 = bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xEF
                && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF;
        boolean utf16 = bytes.length >= 2
                && (((bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xFE)
                || ((bytes[0] & 0xFF) == 0xFE
                && (bytes[1] & 0xFF) == 0xFF));
        if (utf8 || utf16) {
            throw new AssertionError(path + " contiene BOM");
        }
    }

    private static String extraerEntre(
            String contenido,
            String inicio,
            String fin) {

        int posicionInicio = contenido.indexOf(inicio);
        int posicionFin = contenido.indexOf(fin, posicionInicio);
        if (posicionInicio < 0 || posicionFin < posicionInicio) {
            throw new AssertionError(
                    "No se encontro el bloque entre ["
                            + inicio + "] y [" + fin + "]"
            );
        }
        return contenido.substring(
                posicionInicio,
                posicionFin + fin.length()
        );
    }

    private static String extraerMetodo(String contenido, String firma) {
        int inicio = contenido.indexOf(firma);
        int apertura = contenido.indexOf('{', inicio);
        if (inicio < 0 || apertura < 0) {
            throw new AssertionError("No se encontro la firma: " + firma);
        }
        int nivel = 0;
        for (int i = apertura; i < contenido.length(); i++) {
            char caracter = contenido.charAt(i);
            if (caracter == '{') {
                nivel++;
            } else if (caracter == '}') {
                nivel--;
                if (nivel == 0) {
                    return contenido.substring(inicio, i + 1);
                }
            }
        }
        throw new AssertionError("Metodo sin cierre: " + firma);
    }

    private static void antes(
            String contenido,
            String primero,
            String segundo) {

        int posicionPrimero = contenido.indexOf(primero);
        int posicionSegundo = contenido.indexOf(segundo, posicionPrimero + 1);
        if (posicionPrimero < 0 || posicionSegundo <= posicionPrimero) {
            throw new AssertionError(
                    "Orden invalido entre [" + primero + "] y [" + segundo + "]"
            );
        }
    }

    private static void contiene(
            String contenido,
            String etiqueta,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontro [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String contenido,
            String etiqueta,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontro [" + prohibido + "]"
            );
        }
    }

    private ComprasLegalesAmparoContractTest() {
    }
}
