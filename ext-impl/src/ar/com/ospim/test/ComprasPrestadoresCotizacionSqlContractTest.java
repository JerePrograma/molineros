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

        validarSchemaEmailCotizacion(sql);
        validarJavaMultiplesDestinatarios();
    }

    private static void validarSchemaEmailCotizacion(String sql)
            throws Exception {

        String resolverPlural =
                seccion(
                        sql,
                        "CREATE FUNCTION "
                                + "compras.resolver_emails_cotizacion_prestador(",
                        "CREATE FUNCTION\n"
                                + "compras.resolver_email_cotizacion_prestador("
                );

        assertContains(
                "schema contiene resolver plural",
                resolverPlural,
                "compras.resolver_emails_cotizacion_prestador("
        );
        assertContains(
                "resolver plural usa relacion del prestador",
                resolverPlural,
                "FROM public.prestad_contacto_e pce"
        );
        assertContains(
                "resolver plural obtiene el contacto",
                resolverPlural,
                "JOIN public.contacto_e ce"
        );
        assertContains(
                "resolver plural admite email y factura",
                resolverPlural,
                "IN ('E', 'F')"
        );
        assertContains(
                "resolver plural preserva prioridad email",
                resolverPlural,
                "WHEN 'E' THEN 1"
        );
        assertContains(
                "resolver plural preserva prioridad factura",
                resolverPlural,
                "WHEN 'F' THEN 2"
        );
        assertContains(
                "resolver plural descarta baja logica",
                resolverPlural,
                "AND ce.baja_fecha IS NULL"
        );
        assertContains(
                "resolver plural considera vigencia de la relacion",
                resolverPlural,
                "pce.vigen_desde <= LOCALTIMESTAMP"
        );
        assertContains(
                "resolver plural considera vigencia del contacto",
                resolverPlural,
                "ce.vigen_desde <= LOCALTIMESTAMP"
        );
        assertContains(
                "resolver plural valida formato",
                resolverPlural,
                "~* '^[^@[:space:]]+@[^@[:space:]]+\\.[^@[:space:]]+$'"
        );
        assertContains(
                "resolver plural deduplica sin distinguir mayusculas",
                resolverPlural,
                "SELECT DISTINCT ON (\n        lower(btrim(ce.contacto))"
        );
        assertContains(
                "resolver plural agrega todos los emails",
                resolverPlural,
                "SELECT string_agg("
        );
        assertNotContains(
                "resolver plural no limita a un destinatario",
                resolverPlural,
                "LIMIT 1"
        );
        assertNotContains(
                "resolver plural no consulta otra fuente",
                resolverPlural,
                "public.prestador"
        );

        String singularLegacy =
                seccion(
                        sql,
                        "CREATE FUNCTION\n"
                                + "compras.resolver_email_cotizacion_prestador(",
                        "CREATE FUNCTION "
                                + "compras.listar_prestadores_cotizacion_requerimiento("
                );

        assertContains(
                "resolver singular delega al plural",
                singularLegacy,
                "compras.resolver_emails_cotizacion_prestador($1)"
        );
        assertNotContains(
                "resolver singular no duplica acceso a contactos",
                singularLegacy,
                "public.prestad_contacto_e"
        );

        String listadoLegacy =
                seccion(
                        sql,
                        "CREATE FUNCTION "
                                + "compras.listar_prestadores_cotizacion_requerimiento(",
                        "CREATE FUNCTION "
                                + "compras.registrar_cotizacion_prestador("
                );

        assertContains(
                "listado base deriva su email compatible del plural",
                listadoLegacy,
                "compras.resolver_emails_cotizacion_prestador("
        );
        assertNotContains(
                "listado base no depende del resolver singular",
                listadoLegacy,
                "compras.resolver_email_cotizacion_prestador("
        );

        String tabla =
                seccion(
                        sql,
                        "CREATE TABLE "
                                + "compras.requerimiento_cotizacion_prestador (",
                        "CREATE INDEX "
                                + "ix_compras_cotizacion_requerimiento_estado"
                );

        assertContains(
                "email_destino se crea como TEXT",
                tabla,
                "email_destino TEXT,"
        );
        assertNotContains(
                "email_destino no nace limitado",
                tabla,
                "email_destino VARCHAR"
        );

        String listarNotificacion =
                seccion(
                        sql,
                        "compras.listar_prestadores_notificacion_cotizacion(",
                        "compras.diagnosticar_prestadores_notificacion_cotizacion("
                );

        assertContains(
                "listado productivo usa resolver plural",
                listarNotificacion,
                "compras.resolver_emails_cotizacion_prestador("
        );
        assertNotContains(
                "listado productivo no usa resolver singular",
                listarNotificacion,
                "compras.resolver_email_cotizacion_prestador("
        );

        String reservar =
                seccion(
                        sql,
                        "compras.reservar_notificacion_cotizacion_prestador(",
                        "compras.finalizar_notificacion_cotizacion_prestador("
                );

        assertContains(
                "reserva productiva usa resolver plural",
                reservar,
                "compras.resolver_emails_cotizacion_prestador("
        );
        assertNotContains(
                "reserva productiva no usa resolver singular",
                reservar,
                "compras.resolver_email_cotizacion_prestador("
        );

        assertContains(
                "schema crea compras",
                sql,
                "CREATE SCHEMA compras;"
        );
        assertNotContains("schema no elimina schema", sql, "DROP SCHEMA");
        assertNotContains("schema no elimina tablas", sql, "DROP TABLE");
        assertNotContains("schema no elimina funciones", sql, "DROP FUNCTION");
        assertNotContains("schema no elimina tipos", sql, "DROP TYPE");
        assertNotContains("schema no elimina triggers", sql, "DROP TRIGGER");
        assertNotContains("schema no elimina constraints", sql, "DROP CONSTRAINT");
        assertNotContains("schema no trunca datos", sql, "TRUNCATE");
        assertNotContains(
                "schema no parchea definiciones instaladas",
                sql,
                "pg_get_functiondef"
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

    private static void validarJavaMultiplesDestinatarios()
            throws Exception {

        String mailHelper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "CotizacionPrestadorMailHelper.java",
                "ISO-8859-1"
        );
        String notificar = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "NotificarCotizacionPrestadorHelper.java",
                "ISO-8859-1"
        );

        assertContains(
                "helper SMTP recibe destinatarios plurales",
                mailHelper,
                "private void enviarInterno(\n            String[] emailsDestino,"
        );
        assertContains(
                "overloads singulares delegan como arreglo",
                mailHelper,
                "new String[] {\n                        emailDestino\n                }"
        );
        assertOccurrences(
                "un unico MimeMessage",
                mailHelper,
                "new MimeMessage(session)",
                1
        );
        assertContains(
                "cada destino real se agrega como TO",
                mailHelper,
                "Message.RecipientType.TO"
        );
        assertContains(
                "BCC historico conservado",
                mailHelper,
                "Message.RecipientType.BCC"
        );
        assertOccurrences(
                "un unico envio SMTP",
                mailHelper,
                "transport.sendMessage(",
                1
        );

        assertContains(
                "flujo actual conserva arreglo plural",
                notificar,
                "String[] emailsDestino ="
        );
        assertContains(
                "flujo actual envia todos los destinos juntos",
                notificar,
                "mailHelper.enviar(\n                emails,"
        );
        assertContains(
                "QA produce un unico destinatario efectivo",
                notificar,
                "return new String[] {\n                    emailQa\n            };"
        );
        assertNotContains(
                "sin fallback ejecutable a prestador.getEmail",
                notificar,
                "prestador.getEmail();"
        );
    }

    private static String leerSchema() throws Exception {
        return leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql",
                "ISO-8859-1"
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

    private static void assertOccurrences(
            String descripcion,
            String texto,
            String buscado,
            int esperado) {

        int cantidad = 0;
        int posicion = 0;

        while ((posicion = texto.indexOf(buscado, posicion)) >= 0) {
            cantidad++;
            posicion += buscado.length();
        }

        if (cantidad != esperado) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + cantidad
            );
        }
    }

    private ComprasPrestadoresCotizacionSqlContractTest() {
    }
}
