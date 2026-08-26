package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Contrato de normalizacion del sector al guardar detalles de Compras. */
public final class ComprasTipoDetalleSectorMigracionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql",
                LATIN1
        );

        contiene(
                schema,
                "normalizador canonico",
                "CREATE FUNCTION compras.normalizar_sector("
        );
        contiene(
                schema,
                "escape independiente de codificacion",
                "U&'\\00C1\\00C9\\00CD\\00D3\\00DA\\00DC"
        );
        noContiene(
                schema,
                "instalacion no parchea funciones existentes",
                "pg_get_functiondef"
        );
        contiene(
                schema,
                "seed Prestaciones Medicas acentuado",
                "'Prestaciones Médicas'"
        );
        iguales(
                "dos clasificadores usan el normalizador",
                2,
                ocurrencias(
                        schema,
                        "compras.normalizar_sector(sr.descripcion)"
                )
        );

        String trigger = entre(
                schema,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.validar_requerimiento_detalle_fila()",
                "CREATE TRIGGER trg_compras_detalle_validar"
        );
        String guardar = entre(
                schema,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.guardar_requerimiento_detalle(",
                "CREATE FUNCTION "
                        + "compras.validar_tipo_prestacion_detalle_fila()"
        );

        noContiene(trigger, "trigger sin literal corrupto", "\ufffd");
        noContiene(guardar, "guardado sin literal corrupto", "\ufffd");
        contiene(trigger, "prestaciones usa nomenclador", "'PRESTACIONES MEDICAS'");
        contiene(trigger, "tipo esperado trigger", "'NOMENCLADOR'");
        contiene(guardar, "prestaciones se puede guardar", "'PRESTACIONES MEDICAS'");
        contiene(guardar, "tipo esperado guardado", "'NOMENCLADOR'");

        System.out.println("COMPRAS_TIPO_DETALLE_SECTOR_CANONICO_OK");
    }

    private static String leer(String ruta, Charset charset)
            throws Exception {

        return new String(
                Files.readAllBytes(Paths.get(ruta)),
                charset
        );
    }

    private static String entre(
            String contenido,
            String inicio,
            String fin) {

        int desde = contenido.indexOf(inicio);
        int hasta = contenido.indexOf(fin, desde);

        if (desde < 0 || hasta < 0) {
            throw new AssertionError(
                    "No se pudo aislar el bloque SQL requerido."
            );
        }

        return contenido.substring(desde, hasta);
    }

    private static int ocurrencias(String contenido, String buscado) {
        int cantidad = 0;
        int posicion = 0;

        while ((posicion = contenido.indexOf(buscado, posicion)) >= 0) {
            cantidad++;
            posicion += buscado.length();
        }

        return cantidad;
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

    private static void iguales(
            String etiqueta,
            int esperado,
            int actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    etiqueta + ": esperado=" + esperado + ", actual=" + actual
            );
        }
    }

    private ComprasTipoDetalleSectorMigracionContractTest() {
    }
}
