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
    }

    private static String leerSchema() throws Exception {
        File file =
                new File(
                        "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
                );

        String sql = new String(
                Files.readAllBytes(
                        file.toPath()
                ),
                Charset.forName(
                        "UTF-8"
                )
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

    private ComprasPrestadoresCotizacionSqlContractTest() {
    }
}
