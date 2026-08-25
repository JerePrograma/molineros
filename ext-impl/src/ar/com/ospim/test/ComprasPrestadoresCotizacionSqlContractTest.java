package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ComprasPrestadoresCotizacionSqlContractTest {

    public static void main(String[] args) throws Exception {
        String sql =
                leerSchema();

        String listar =
                seccion(
                        sql,
                        "CREATE FUNCTION compras.listar_prestadores_cotizacion_requerimiento",
                        "CREATE FUNCTION compras.registrar_cotizacion_prestador"
                );

        assertContains(
                "listar filtra solicitar_cotizacion",
                listar,
                "COALESCE(\n              p.solicitar_cotizacion,"
        );
        assertContains(
                "listar exige TRUE",
                listar,
                ") = TRUE"
        );
        assertContains(
                "listar descarta baja logica",
                listar,
                "AND p.baja_fecha IS NULL"
        );
        assertContains(
                "listar cruza sector",
                listar,
                "ON stp.id_sector =\n                 r.id_sector"
        );
        assertContains(
                "listar cruza tipo prestador",
                listar,
                "ON p.id_tipo_prestador =\n                 stp.id_tipo_prestador"
        );
        assertContains(
                "listar no reintenta enviado procesando",
                listar,
                "rcp.estado_envio IN (\n                                        'PENDIENTE',\n                                        'ERROR',\n                                        'EMAIL_INVALIDO'"
        );

        String registrar =
                seccion(
                        sql,
                        "CREATE FUNCTION compras.registrar_cotizacion_prestador",
                        "CREATE FUNCTION compras.finalizar_cotizacion_prestador"
                );

        assertContains(
                "registrar valida sector-tipo",
                registrar,
                "AND p.id_tipo_prestador =\n                      stp.id_tipo_prestador"
        );
        assertContains(
                "registrar valida solicitar_cotizacion",
                registrar,
                "COALESCE(\n              p.solicitar_cotizacion,"
        );
        assertContains(
                "registrar descarta baja logica",
                registrar,
                "AND p.baja_fecha IS NULL"
        );
        assertContains(
                "registrar reserva atomica",
                registrar,
                "ON CONFLICT (\n        id_requerimiento,\n        id_prestador"
        );
        assertContains(
                "registrar no pisa enviado procesando",
                registrar,
                "WHERE compras.requerimiento_cotizacion_prestador.estado_envio\n               IN (\n               'PENDIENTE',\n               'ERROR',\n               'EMAIL_INVALIDO'"
        );

        validarMigracionEmailCotizacion();
    }

    private static void validarMigracionEmailCotizacion()
            throws Exception {

        String migracion =
                leer(
                        "docs/sql/"
                                + "20260825_corregir_email_cotizacion_prestador.sql",
                        "ISO-8859-1"
                );

        assertContains(
                "migracion reemplaza el resolver",
                migracion,
                "CREATE OR REPLACE FUNCTION\n"
                        + "compras.resolver_email_cotizacion_prestador("
        );
        assertContains(
                "migracion conserva email y factura",
                migracion,
                "IN ('E', 'F')"
        );
        assertContains(
                "migracion preserva prioridad email",
                migracion,
                "WHEN 'E' THEN 1"
        );
        assertContains(
                "migracion usa factura como fallback",
                migracion,
                "WHEN 'F' THEN 2"
        );
        assertContains(
                "migracion descarta contactos dados de baja",
                migracion,
                "AND ce.baja_fecha IS NULL"
        );
        assertContains(
                "migracion valida formato",
                migracion,
                "~* '^[^@[:space:]]+@[^@[:space:]]+\\.[^@[:space:]]+$'"
        );
        assertNotContains(
                "migracion sin cambios de datos",
                migracion,
                "UPDATE "
        );
        assertNotContains(
                "migracion sin borrado de datos",
                migracion,
                "DELETE FROM "
        );

        String prestadores =
                leer(
                        "ext-web/docroot/html/portlet/prestadores/"
                                + "prestador_lugar_atencion.jsp",
                        "ISO-8859-1"
                );

        assertContains(
                "prestadores publica F como factura",
                prestadores,
                "<option value=\"<%=ContactoElectronico.Tipo.FAX%>\">FACTURA</option>"
        );
        assertContains(
                "prestadores valida F como email",
                prestadores,
                "tipoContE == '<%=ContactoElectronico.Tipo.FAX%>'"
        );
    }

    private static String leerSchema() throws Exception {
        return leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql",
                "UTF-8"
        );
    }

    private static String leer(
            String path,
            String charset) throws Exception {

        File file = new File(path);

        String sql = new String(
                Files.readAllBytes(file.toPath()),
                Charset.forName(charset)
        );

        return sql.replace(
                "\r\n",
                "\n"
        ).replace(
                '\r',
                '\n'
        );
    }

    private static String seccion(
            String sql,
            String desde,
            String hasta) {

        int inicio =
                sql.indexOf(
                        desde
                );

        int fin =
                sql.indexOf(
                        hasta
                );

        if (inicio < 0 || fin <= inicio) {
            throw new AssertionError(
                    "No se encontro la seccion SQL: "
                            + desde
            );
        }

        return sql.substring(
                inicio,
                fin
        );
    }

    private static void assertContains(
            String descripcion,
            String texto,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion
                            + ": no se encontro ["
                            + esperado
                            + "]"
            );
        }
    }

    private static void assertNotContains(
            String descripcion,
            String texto,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion
                            + ": se encontro ["
                            + prohibido
                            + "]"
            );
        }
    }

    private ComprasPrestadoresCotizacionSqlContractTest() {
    }
}
