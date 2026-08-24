package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Contrato focalizado del alta y consulta de la marca LEGALES. */
public final class ComprasLegalesAltaContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    private static final String JSP_BASE =
            "ext-web/docroot/html/portlet/compras/requerimientos/partials/";

    private static final String JAVA_BASE =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/";

    public static void main(String[] args) throws Exception {
        String ensamblado = leer(
                JSP_BASE + "requerimiento_compra_edicion_ensamblado.jsp"
        );
        String datos = leer(
                JSP_BASE + "requerimiento_compra_datos_basicos_componente.jsp"
        );
        String ocultos = leer(
                JSP_BASE
                        + "requerimiento_compra_campos_ocultos_formulario_componente.jsp"
        );
        String scripts = leer(
                JSP_BASE
                        + "requerimiento_compra_scripts_edicion_guardado_componente.jsp"
        );
        String action = leer(
                JAVA_BASE + "action/EditarRequerimientoCompraAction.java"
        );
        String helper = leer(
                JAVA_BASE + "helper/EditarRequerimientoCompraHelper.java"
        );
        String bean = leer(
                JAVA_BASE + "beans/RequerimientoCompra.java"
        );
        String guardar = leer(
                JAVA_BASE + "service/EditarRequerimientoCompraServiceImpl.java"
        );
        String buscar = leer(
                JAVA_BASE + "service/BusquedaRequerimientoCompraServiceImpl.java"
        );
        String editorDetalle = leer(
                JSP_BASE + "requerimiento_compra_detalle_editor_componente.jsp"
        );
        String scriptsDetalle = leer(
                JSP_BASE
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );

        validarCasoGAltaMarcadaNoFarmacia(
                datos,
                ocultos,
                scripts,
                action,
                guardar,
                buscar,
                bean
        );
        validarCasoHAltaDesmarcada(ocultos, scripts, action, bean);
        validarCasoIFarmaciaSinReglaEspecial(datos, ocultos, scripts, action);
        validarCasoJCambioSectorNoReseteaLegales(scripts);
        validarCasoKEdicionInmutable(datos, helper, bean);
        validarCasoLContratoHttp(ensamblado, datos, ocultos, scripts);
        validarPersistenciaSql(schema);
        validarTipoCotizacionSeparado(editorDetalle, scriptsDetalle, datos);

        System.out.println("CONTRATO_COMPRAS_LEGALES_ALTA_OK");
    }

    private static void validarCasoGAltaMarcadaNoFarmacia(
            String datos,
            String ocultos,
            String scripts,
            String action,
            String guardar,
            String buscar,
            String bean) {

        String checkbox = inputConId(datos, "<portlet:namespace />legales");
        String hidden = inputConId(ocultos, "<portlet:namespace />legales_hidden");

        contiene(checkbox, "checkbox visible", "type=\"checkbox\"");
        contiene(hidden, "hidden canonico", "name=\"<portlet:namespace />legales\"");
        noContiene(
                ocultos,
                "hidden LEGALES tambien existe en alta",
                "<% if (!esNuevo) { %>"
        );
        contiene(scripts, "marcado viaja true", ".is(':checked')");
        contiene(scripts, "valor true explicito", "? 'true'");
        contiene(action, "Action reconstruye legales", "getParametroBoolean(request, \"legales\")");
        contiene(guardar, "servicio transmite legales", "stmt.setBoolean(22, requerimiento.isLegales());");
        contiene(buscar, "mapper recupera legales", "r.setLegales(getBoolean(rs, \"legales\"));");
        contiene(bean, "vista muestra Si", "return isLegales() ? \"S\u00ED\" : \"No\";");
    }

    private static void validarCasoHAltaDesmarcada(
            String ocultos,
            String scripts,
            String action,
            String bean) {

        contiene(ocultos, "valor inicial false", "? \"true\" : \"false\"");
        contiene(scripts, "desmarcado viaja false", ": 'false'");
        contiene(action, "booleano ausente no es true", "\"true\".equalsIgnoreCase(value)");
        contiene(bean, "vista muestra No", ": \"No\";");
    }

    private static void validarCasoIFarmaciaSinReglaEspecial(
            String datos,
            String ocultos,
            String scripts,
            String action) {

        noContiene(datos, "LEGALES visual sin FARMACIA", "FARMACIA");
        noContiene(ocultos, "LEGALES HTTP sin FARMACIA", "FARMACIA");
        noContiene(scripts, "LEGALES JS sin FARMACIA", "FARMACIA");
        noContiene(action, "LEGALES Action sin FARMACIA", "FARMACIA");
    }

    private static void validarCasoJCambioSectorNoReseteaLegales(
            String scripts) {

        noContiene(
                scripts,
                "cambio de sector no desmarca LEGALES",
                "legales').prop('checked'"
        );
        noContiene(
                scripts,
                "cambio de sector no altera atributo checked",
                "legales').attr('checked'"
        );
    }

    private static void validarCasoKEdicionInmutable(
            String datos,
            String helper,
            String bean) {

        contiene(datos, "lectura posterior", "req.getLegalesDescripcion()");
        contiene(helper, "inmutable post alta", "s\u00F3lo puede definirse durante el alta");
        contiene(helper, "restaura valor persistido", "requerimiento.setLegales(actual.getLegales());");
        contiene(bean, "descripcion usa boolean persistido", "return isLegales()");
    }

    private static void validarCasoLContratoHttp(
            String ensamblado,
            String datos,
            String ocultos,
            String scripts) {

        antes(
                ensamblado,
                "requerimiento_compra_campos_ocultos_formulario_componente.jsp",
                "requerimiento_compra_datos_basicos_runtime_componente.jsp"
        );
        contiene(ocultos, "formulario colector abre", "id=\"<portlet:namespace />fmCompras\"");
        contiene(ocultos, "formulario colector cierra", "</form>");

        String checkbox = inputConId(datos, "<portlet:namespace />legales");
        String hidden = inputConId(ocultos, "<portlet:namespace />legales_hidden");

        noContiene(checkbox, "checkbox visual sin name exitoso", "name=");
        contiene(hidden, "unico parametro HTTP", "name=\"<portlet:namespace />legales\"");
        ocurrencias(
                datos + ocultos,
                "un solo control exitoso LEGALES",
                "name=\"<portlet:namespace />legales\"",
                1
        );
        contiene(scripts, "sincroniza antes de guardar", "sincronizarFormularioCompra()");
        contiene(scripts, "destino hidden", "#<portlet:namespace />legales_hidden");
    }

    private static void validarTipoCotizacionSeparado(
            String editorDetalle,
            String scriptsDetalle,
            String datos) {

        contiene(
                editorDetalle,
                "existe Tipo de cotizacion",
                "Tipo de cotizaci\u00F3n:"
        );
        contiene(
                editorDetalle,
                "representado por tipo de prestacion",
                "detalle_id_tipo_prestacion"
        );
        contiene(
                scriptsDetalle,
                "filtrado por sector",
                "String(tipo.idSector) != idSector"
        );
        noContiene(
                datos,
                "Tipo de cotizacion no controla LEGALES",
                "tipo_cotizacion"
        );
    }

    private static void validarPersistenciaSql(String schema) {
        contiene(
                schema,
                "funcion de guardado",
                "CREATE FUNCTION compras.guardar_requerimiento("
        );
        contiene(schema, "parametro SQL LEGALES", "p_legales BOOLEAN");
        contiene(
                schema,
                "inserta columna LEGALES",
                "recupero, surge, legales, observaciones, alta_usr"
        );
        contiene(
                schema,
                "persiste parametro LEGALES",
                "COALESCE(p_legales, FALSE)"
        );
        contiene(
                schema,
                "funcion de lectura",
                "CREATE FUNCTION compras.get_requerimiento("
        );
        contiene(
                schema,
                "lectura incluye LEGALES",
                "r.legales"
        );
    }

    private static String inputConId(String contenido, String id) {
        int marca = contenido.indexOf("id=\"" + id + "\"");

        if (marca < 0) {
            throw new AssertionError("No se encontro input con id " + id);
        }

        int inicio = contenido.lastIndexOf("<input", marca);
        int fin = contenido.indexOf("/>", marca);

        if (inicio < 0 || fin < 0) {
            throw new AssertionError("Input incompleto para id " + id);
        }

        return contenido.substring(inicio, fin + 2);
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

    private static void antes(
            String contenido,
            String primero,
            String segundo) {

        int posicionPrimero = contenido.indexOf(primero);
        int posicionSegundo = contenido.indexOf(segundo);

        if (posicionPrimero < 0
                || posicionSegundo < 0
                || posicionPrimero >= posicionSegundo) {

            throw new AssertionError(
                    "Orden invalido: [" + primero + "] antes de [" + segundo + "]"
            );
        }
    }

    private static void ocurrencias(
            String contenido,
            String etiqueta,
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
                    etiqueta + ": esperado=" + esperado + ", actual=" + cantidad
            );
        }
    }

    private ComprasLegalesAltaContractTest() {
    }
}
