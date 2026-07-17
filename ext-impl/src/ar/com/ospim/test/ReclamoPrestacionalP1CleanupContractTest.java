package ar.com.ospim.test;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual de la limpieza P1 de Reclamos Prestacionales.
 *
 * Protege la ruta única de baja AN y la reparación física del fragmento JSP
 * sin depender de las librerías del runtime Liferay.
 */
public final class ReclamoPrestacionalP1CleanupContractTest {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset LATIN_1 = Charset.forName("ISO-8859-1");

    private ReclamoPrestacionalP1CleanupContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String action = leerLegacy(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String service = leerLegacy(
                "ext-impl/src/ar/com/ospim/autorizaciones/services/"
                        + "ReclamosPrestacionesServiceUtil.java"
        );
        String editor = leerLegacy(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/datos_edicion_prestacion.jsp"
        );

        String deleteBlock = bloque(
                action,
                "\t\t\tif(cmd.equals(Constants.DELETE))",
                "\t\t\t\t  BusquedaReclamosPrestacionalesFiltro filtro = null ;"
        );

        assertContains(
                "DELETE delega la baja al servicio",
                deleteBlock,
                "ReclamosPrestacionesServiceUtil.borrar(idReclamoDeBuscador, user)"
        );
        assertNotContains(
                "DELETE no relee el reclamo borrado",
                deleteBlock,
                "getReclamoPrestacional(idReclamoDeBuscador)"
        );
        assertNotContains(
                "DELETE no sincroniza AppMobile directamente",
                deleteBlock,
                "ClienteAppMobile"
        );
        assertContains(
                "se preserva transición externa PE CE RE",
                action,
                "ClienteAppMobile.actualizarEstadoReintegro(idExterno, codigoExterno, token)"
        );

        assertNotContains("cache temporal eliminado", service, "BAJAS_RECIENTES");
        assertNotContains("guard temporal eliminado", service, "esBajaReciente");
        assertNotContains("registro temporal eliminado", service, "registrarBajaReciente");
        assertContains(
                "baja transaccional conservada",
                service,
                "ReclamoPrestacionalBajaTransaccionalService.borrar("
        );
        assertContains(
                "outbox de fallback conservada",
                service,
                "registrarOutboxSeguro("
        );

        assertContains(
                "tipo de edición conservado",
                editor,
                "tipoedicion = (Integer) request.getAttribute(\"tipoEdicion\")"
        );
        assertContains(
                "fecha de comprobante conservada",
                editor,
                "fechaseccional.setTime(prestacionEnEdicion.getComprobanteFecha())"
        );
        assertContains(
                "fecha de prestación conservada",
                editor,
                "fechaPrestacion.setTime(prestacionEnEdicion.getFechaPrestacion())"
        );
        assertContains(
                "caption de edición conservado",
                editor,
                "captionbotoncancelar=\"Cancelar Edicion de la Prestacion\""
        );
        assertContains(
                "caption de proceso conservado",
                editor,
                "captionlabelproceso=\"PRESTACION EN PROCESO DE EDICION\""
        );
        assertContains(
                "regla seccional conservada",
                editor,
                "ocultarSeccional = (String) request.getAttribute(\"ocultar\")"
        );
        assertOccurrences(
                "preparación legacy no duplicada",
                editor,
                "if(prestacionEnEdicion != null  ){",
                1
        );

        assertNotContains(
                "API no disponible eliminada",
                editor,
                "HtmlUtil.escapeJS"
        );
        assertContains(
                "inicialización posterior al render",
                editor,
                "jQuery(function() {"
        );
        assertContains(
                "código leído desde el control renderizado",
                editor,
                "codigoSeguimiento_filtro_edit\").val() || \"\""
        );
        assertContains(
                "búsqueda de nomenclador defensiva",
                editor,
                "typeof buscarNomenclador === \"function\""
        );
        assertNotContains(
                "asignación duplicada eliminada",
                editor,
                "jQuery(\"#<portlet:namespace />idRegistro\").val"
        );

        System.out.println("CONTRATO_RECLAMO_PRESTACIONAL_P1_CLEANUP_OK");
    }

    private static String leerLegacy(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        byte[] bytes = Files.readAllBytes(path);
        CharsetDecoder decoder = UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            return new String(bytes, LATIN_1);
        }
    }

    private static String bloque(
            String contenido,
            String inicio,
            String fin) {

        int desde = contenido.indexOf(inicio);
        int hasta = contenido.indexOf(fin, desde);
        if (desde < 0 || hasta <= desde) {
            throw new AssertionError(
                    "No se pudo delimitar el bloque entre ["
                            + inicio + "] y [" + fin + "]"
            );
        }
        return contenido.substring(desde, hasta);
    }

    private static void assertContains(
            String etiqueta,
            String contenido,
            String esperado) {

        if (contenido.indexOf(esperado) < 0) {
            throw new AssertionError(
                    etiqueta + ": no se encontró [" + esperado + "]"
            );
        }
    }

    private static void assertNotContains(
            String etiqueta,
            String contenido,
            String prohibido) {

        if (contenido.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    etiqueta + ": se encontró [" + prohibido + "]"
            );
        }
    }

    private static void assertOccurrences(
            String etiqueta,
            String contenido,
            String buscado,
            int esperado) {

        int cantidad = 0;
        int posicion = 0;
        while ((posicion = contenido.indexOf(buscado, posicion)) >= 0) {
            cantidad++;
            posicion += buscado.length();
        }
        if (cantidad != esperado) {
            throw new AssertionError(
                    etiqueta + ": se esperaban " + esperado
                            + " coincidencias de [" + buscado + "] y se encontraron "
                            + cantidad
            );
        }
    }
}
