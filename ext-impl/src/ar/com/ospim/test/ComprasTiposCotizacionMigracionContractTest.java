package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Contrato del catalogo canonico de tipos de cotizacion. */
public final class ComprasTiposCotizacionMigracionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String seeds = entre(
                schema,
                "INSERT INTO compras.sector_requerimiento (",
                "SELECT setval("
        );
        String tipos = entre(
                schema,
                "WITH tipos (",
                "SELECT setval("
        );

        contiene(
                seeds,
                "sector Prestaciones Medicas con acentuacion final",
                "(2, 'Prestaciones Médicas', TRUE, TRUE, 'sistema')"
        );
        contiene(
                tipos,
                "catalogo usa sector normalizado",
                ") = t.sector_normalizado"
        );
        contiene(
                tipos,
                "catalogo declara Prestaciones Medicas normalizado",
                "'PRESTACIONES MEDICAS'"
        );
        contiene(tipos, "Protesis Trauma", "(3, 'Prótesis Traumatología'");
        contiene(tipos, "Protesis Cardio", "(4, 'Prótesis Cardiología'");
        contiene(tipos, "Protesis General", "(5, 'Prótesis General'");
        contiene(tipos, "Insumos", "(6, 'Insumos'");
        contiene(tipos, "Panales", "(7, 'Pañales'");
        noContiene(tipos, "instalacion sin reparacion por conflicto", "ON CONFLICT");

        for (int id = 1; id <= 7; id++) {
            iguales(
                    "id de tipo no duplicado " + id,
                    1,
                    ocurrencias(tipos, "(" + id + ", '")
            );
        }

        System.out.println("COMPRAS_TIPOS_COTIZACION_CANONICO_OK");
    }

    private static String leer(String ruta) throws Exception {
        return new String(
                Files.readAllBytes(Paths.get(ruta)),
                LATIN1
        );
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

    private static String entre(
            String contenido,
            String inicio,
            String fin) {

        int desde = contenido.indexOf(inicio);
        int hasta = contenido.indexOf(fin, desde);

        if (desde < 0 || hasta <= desde) {
            throw new AssertionError("No se pudo aislar el bloque de seeds.");
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

    private ComprasTiposCotizacionMigracionContractTest() {
    }
}
