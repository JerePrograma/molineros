package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contrato focalizado del cierre de los doce requerimientos de Compras.
 */
public final class ComprasDoceRequerimientosContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    private static final File COMPRAS_JSP = new File(
            "ext-web/docroot/html/portlet/compras/requerimientos"
    );

    public static void main(String[] args) throws Exception {
        validarCorreoYOrdenesMedicas();
        validarVisualizacionOrdenMedica();
        validarAccionesYEstados();
        validarReclamoPrestacional();
        validarAfiliadoYDuplicados();
        validarUltimaPrestacion();
        validarTipoCotizacion();
        validarSectoresYLegales();
        validarArquitectura();
        validarJspRenombrados();

        System.out.println("COMPRAS_DOCE_REQUERIMIENTOS_OK");
    }

    private static void validarCorreoYOrdenesMedicas() throws Exception {
        String mail = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "CotizacionPrestadorMailHelper.java"
        );
        String notificacion = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "NotificarCotizacionPrestadorHelper.java"
        );
        String schema = esquema();

        contiene(mail, "recorre todas las órdenes", "i < ordenesMedicas.size();");
        noContiene(mail, "no toma sólo la primera", "ordenesMedicas.get(0)");
        contiene(mail, "pedido PDF único", "pedidoPresupuestoPdf");
        contiene(mail, "BCC", "RecipientType.BCC");
        noContiene(mail, "sin CC visible", "RecipientType.CC");
        contiene(notificacion, "plazo 48 horas", "48 horas");
        contiene(notificacion, "límite 18:00", "18:00");
        contiene(
                notificacion,
                "override externo",
                "compras.cotizacion.email.redireccion.qa.habilitada"
        );
        contiene(
                notificacion,
                "default desactivado",
                "\"true\".equalsIgnoreCase(value.trim())"
        );
        noContiene(
                notificacion,
                "sin bandera QA hardcodeada",
                "USAR_EMAIL_DESTINO_TEMPORAL"
        );
        noContiene(
                notificacion,
                "sin dirección personal hardcodeada",
                "acomas@ospim.org.ar"
        );
        contiene(schema, "relación email prestador", "public.prestad_contacto_e");
        contiene(schema, "contacto electrónico", "public.contacto_e");
        contiene(schema, "tipo email E", "tipo_contacto_e, ''))) = 'E'");
    }

    private static void validarVisualizacionOrdenMedica() throws Exception {
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "DescargarOrdenMedicaCompraAction.java"
        );
        String vista = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_orden_medica_consulta_componente.jsp"
        );
        String documentos = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/documentos/"
                        + "DocumentoLibraryComprasHelper.java"
        );

        contiene(action, "header de disposición", "Content-Disposition");
        contiene(action, "modo inline", "inline");
        contiene(action, "modo legacy de descarga", "ServletResponseUtil.sendFile");
        contiene(action, "pertenencia", "idRequerimientoCompra");
        contiene(documentos, "firma PNG", "(firma[0] & 0xFF) == 0x89");
        contiene(documentos, "firma JPEG", "(firma[0] & 0xFF) == 0xFF");
        contiene(documentos, "MIME real", "contentTypeEsperado");
        contiene(vista, "lupa", "/common/view.png");
        contiene(vista, "visualización inline", "\"visualizar\",");
    }

    private static void validarAccionesYEstados() throws Exception {
        String acciones = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_acciones.jsp"
        );
        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "EditarRequerimientoCompraAction.java"
        );

        noContiene(acciones, "sin Ver redundante", ">Ver<");
        contiene(
                webKeys,
                "SURGE pendiente y a cotizar",
                "return esPendiente(estado) || esACotizar(estado);"
        );
        contiene(action, "SURGE llega a cotización", "boolean surge = parseSurgeObligatorio");
        contiene(helper, "sector inmutable", "El sector del requerimiento no puede modificarse");
        contiene(helper, "afiliado inmutable", "El afiliado del requerimiento no puede modificarse");
        contiene(helper, "LEGALES inmutable", "sólo puede definirse durante el alta");
    }

    private static void validarReclamoPrestacional() throws Exception {
        String documentos = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraDocumentacionHelper.java"
        );
        String cierre = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraReclamoPrestacionalHelper.java"
        );

        contiene(documentos, "todas las órdenes", "i < ordenesMedicas.size();");
        contiene(documentos, "pedido histórico", ".getPedidoCotizacionAdjudicado(");
        contiene(documentos, "cotización adjudicada", ".getPresupuestoAdjudicado(");
        contiene(documentos, "idempotencia por bytes", "Arrays.equals(");
        contiene(documentos, "grupo DL alineado", "setScopeGroupId(folder.getGroupId())");
        contiene(cierre, "compensación", "compensarDocumentacion(");
        antes(cierre, ".adjuntarDocumentacionControlada(", "finalizarCreacion(");
        antes(cierre, "finalizarCreacion(", "transaccion.commit();");
    }

    private static void validarAfiliadoYDuplicados() throws Exception {
        String afiliado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_afiliado_editable_componente.jsp"
        );
        String token = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "ActualizarContactoAfiliadoCompraToken.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "EditarRequerimientoCompraAction.java"
        );
        String schema = esquema();

        contiene(afiliado, "botón externo", "botonActualizarContactoAfiliado");
        contiene(token, "token con identidad", "cuilTitular");
        contiene(helper, "regla canónica de duplicados", "validarNuevoRequerimientoNoDuplicado");
        antes(
                action,
                ".validarNuevoRequerimientoNoDuplicado(",
                ".guardarNuevoRequerimientoCompraConOrdenesMedicas("
        );
        contiene(schema, "persona titular", "p_afiliado_cuil_titular");
        contiene(schema, "persona integrante", "p_afiliado_int");
        contiene(schema, "fecha de orden", "p_fecha_orden_medica");
        contiene(schema, "excluye actual", "p_id_requerimiento_excluir");
    }

    private static void validarUltimaPrestacion() throws Exception {
        String base = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_base_componente.jsp"
        );
        String edicion = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );
        String migration = migracion();

        contiene(base, "tachito con una fila", "puedeCotizarDetalle ? \"true\" : \"false\"");
        contiene(edicion, "IDs diferidos", "detalleDeletedIds.push(");
        contiene(edicion, "serializa bajas", "'detalle_deleted_ids'");
        contiene(edicion, "rechaza cero al guardar", "Debe conservar al menos una prestación");
        contiene(service, "función transaccional", "guardar_cotizacion_requerimiento_call");
        contiene(migration, "bloqueo de cabecera", "FOR UPDATE;");
        contiene(migration, "bloqueo de detalles", "ORDER BY d.id_detalle");
        contiene(migration, "baja y cotización atómicas", "p_ids_detalle_eliminados");
        contiene(migration, "mínimo persistido", "v_total_conservados <= 0");
    }

    private static void validarTipoCotizacion() throws Exception {
        String catalogo = esquema();
        String migration = migracion();
        String editor = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_editor_componente.jsp"
        );
        String config = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_prestadores_configuracion.jsp"
        );
        String action = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "BuscarItemTecnicoComprasAction.java"
        );

        contiene(catalogo, "Alimentación", "(1, 'Alimentación', 'FARMACIA')");
        contiene(catalogo, "Medicamentos", "(2, 'Medicamentos', 'FARMACIA')");
        contiene(catalogo, "Prótesis Trauma", "(3, 'Prótesis Traumatología'");
        contiene(catalogo, "Prótesis Cardio", "(4, 'Prótesis Cardiología'");
        contiene(catalogo, "Prótesis General", "(5, 'Prótesis General'");
        contiene(catalogo, "Insumos", "(6, 'Insumos'");
        contiene(catalogo, "Pañales", "(7, 'Pañales'");
        contiene(editor, "etiqueta funcional", "Tipo de cotización:");
        noContiene(editor, "sin selector técnico", "Tipo Nomenclador");
        contiene(action, "tipo técnico interno", "Integer.valueOf(0)");
        contiene(migration, "FK de tipo", "fk_compras_sector_tipo_prestador_cotizacion");
        contiene(migration, "PK triple", "id_tipo_prestacion,");
        contiene(migration, "candidatos por detalle", "d.id_tipo_prestacion = stp.id_tipo_prestacion");
        contiene(config, "matriz visible", "Tipo de cotización");
    }

    private static void validarSectoresYLegales() throws Exception {
        String migration = migracion();
        String schema = esquema();
        String bean = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompra.java"
        );
        String datos = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_datos_basicos_componente.jsp"
        );

        contiene(migration, "excluye LEGALES", "NOT IN ('LEGALES', 'OTROS')");
        contiene(migration, "centraliza catálogo", "es_sector_seleccionable_compras");
        contiene(schema, "columna legal", "legales BOOLEAN NOT NULL DEFAULT FALSE");
        contiene(bean, "bean legal", "private Boolean legales;");
        contiene(datos, "check alta", "id=\"<portlet:namespace />legales\"");
        contiene(datos, "sólo lectura posterior", "req.getLegalesDescripcion()");
    }

    private static void validarArquitectura() throws Exception {
        List<File> java = archivos(
                new File("ext-impl/src/ar/com/ospim/compras/requerimientos"),
                ".java"
        );

        for (int i = 0; i < java.size(); i++) {
            File file = java.get(i);
            String path = file.getPath().replace('\\', '/');
            String text = leer(file.getPath());
            boolean serviceImpl =
                    path.indexOf("/service/") >= 0
                            && path.endsWith("ServiceImpl.java");

            if (!serviceImpl) {
                noCoincide(
                        text,
                        "SQL directo solo en ServiceImpl: " + path,
                        "(?i)\"\\s*(?:select\\s|insert\\s+into\\s|"
                                + "update\\s+[a-z_]|delete\\s+from\\s)"
                );
            }

            if (path.indexOf("/action/") >= 0
                    || path.indexOf("/helper/") >= 0) {
                noContiene(text, "sin JDBC en " + path, "java.sql.Connection");
                noContiene(text, "sin ConnectionHelper en " + path, "ConnectionHelper");
            }

            if (serviceImpl) {
                noContiene(text, "sin Document Library en " + path, "DLFileEntryLocalServiceUtil");
                noContiene(text, "sin permisos en " + path, "PermissionUtil");
                noContiene(text, "sin SessionErrors en " + path, "SessionErrors");
                noContiene(text, "sin correo en " + path, "MailMessage");
            }
        }

        List<File> jsp = archivos(COMPRAS_JSP, ".jsp");

        for (int i = 0; i < jsp.size(); i++) {
            String text = leer(jsp.get(i).getPath());
            noContiene(text, "sin JDBC en JSP", "java.sql.");
            noContiene(text, "sin ServiceImpl en JSP", "ServiceImpl");
            noContiene(text, "sin ServiceUtil en JSP", "ServiceUtil");
            noContiene(text, "sin Document Library en JSP", "DLFileEntryLocalServiceUtil");
            noCoincide(text, "JavaScript ES5 sin let", "\\blet\\s+[A-Za-z_$]");
            noCoincide(text, "JavaScript ES5 sin const", "\\bconst\\s+[A-Za-z_$]");
            noContiene(text, "JavaScript ES5 sin arrow", "=>");
        }
    }

    private static void validarJspRenombrados() throws Exception {
        List<File> jsp = archivos(COMPRAS_JSP, ".jsp");
        Pattern include = Pattern.compile(
                "(?:file|page)\\s*=\\s*[\"']([^\"']+\\.jsp)[\"']"
        );

        for (int i = 0; i < jsp.size(); i++) {
            File file = jsp.get(i);
            String text = leer(file.getPath());

            String normalized = file.getPath().replace('\\', '/');

            if (normalized.indexOf("/partials/") >= 0) {
                contiene(text, "cabecera responsabilidad", "Responsabilidad:");
                contiene(text, "cabecera caller", "Incluido desde:");
                contiene(text, "cabecera estados", "Pantallas o estados de uso:");
                contiene(text, "cabecera entradas", "Entradas requeridas:");
                contiene(text, "cabecera request", "Atributos de request consumidos:");
                contiene(text, "cabecera parámetros", "Parámetros consumidos:");
                contiene(text, "cabecera JS", "IDs o funciones JavaScript expuestos:");
                contiene(text, "cabecera efectos", "Efectos secundarios:");
            }

            Matcher matcher = include.matcher(text);
            while (matcher.find()) {
                String target = matcher.group(1);
                File resolved;

                if (target.startsWith("/html/")) {
                    if (!target.startsWith("/html/portlet/compras/")) {
                        continue;
                    }
                    resolved = new File("ext-web/docroot", target.substring(1));
                } else {
                    resolved = new File(file.getParentFile(), target);
                }

                if (!resolved.isFile()) {
                    throw new AssertionError(
                            "Include inexistente: " + file + " -> " + target
                    );
                }
            }
        }

        contiene(
                leer("ext-web/docroot/WEB-INF/tiles-defs.xml"),
                "entry alta renombrado",
                "requerimiento_compra_alta.jsp"
        );
    }

    private static String esquema() throws Exception {
        return leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql",
                LATIN1
        );
    }

    private static String migracion() throws Exception {
        return esquema();
    }

    private static List<File> archivos(File base, String suffix) {
        List<File> result = new ArrayList<File>();
        File[] files = base.listFiles();

        if (files == null) {
            return result;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                result.addAll(archivos(files[i], suffix));
            } else if (files[i].getName().endsWith(suffix)) {
                result.add(files[i]);
            }
        }

        return result;
    }

    private static String leer(String path) throws Exception {
        return leer(path, LATIN1);
    }

    private static String leer(
            String path,
            Charset charset) throws Exception {

        return new String(
                Files.readAllBytes(new File(path).toPath()),
                charset
        );
    }

    private static void contiene(
            String texto,
            String descripcion,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion + ": falta [" + esperado + "]"
            );
        }
    }

    private static void noContiene(
            String texto,
            String descripcion,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion + ": contiene [" + prohibido + "]"
            );
        }
    }

    private static void noCoincide(
            String texto,
            String descripcion,
            String expresion) {

        if (Pattern.compile(expresion).matcher(texto).find()) {
            throw new AssertionError(
                    descripcion + ": coincide [" + expresion + "]"
            );
        }
    }

    private static void antes(
            String texto,
            String primero,
            String segundo) {

        int a = texto.indexOf(primero);
        int b = texto.indexOf(segundo, a + 1);

        if (a < 0 || b <= a) {
            throw new AssertionError(
                    "Orden inválido: " + primero + " / " + segundo
            );
        }
    }

    private ComprasDoceRequerimientosContractTest() {
    }
}
