package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrato textual focalizado de paths, permisos y respuestas AJAX Compras.
 */
public final class ComprasAjaxConsultasContractTest {

    private static final Charset ISO_8859_1 =
            Charset.forName("ISO-8859-1");

    private static final String ACTION_DIR =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/action/";

    private ComprasAjaxConsultasContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String busquedaAfiliado = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "busqueda_afiliado.jsp"
        );
        String componenteAfiliadoCompra = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "partials/"
                        + "requerimiento_compra_afiliado_editable_componente.jsp"
        );
        String actualizarContactoLegacy = leer(
                "ext-impl/src/ar/com/ospim/crm/action/"
                        + "ActualizaDomicilioAfiliadoAction.java"
        );
        String medicamentos = leer(
                ACTION_DIR + "BuscarMedicamentosComprasAction.java"
        );
        String itemTecnico = leer(
                ACTION_DIR + "BuscarItemTecnicoComprasAction.java"
        );
        String historicos = leer(
                ACTION_DIR + "BuscarItemsHistoricosAfiliadoCompraAction.java"
        );
        String prestadores = leer(
                ACTION_DIR + "BuscarPrestadoresEnviadosComprasAction.java"
        );
        String vencimiento = leer(
                ACTION_DIR
                        + "BuscarAfiliadoFechaVtoDocumentacionCompraAction.java"
        );
        String datosAfiliado = leer(
                ACTION_DIR + "BuscarAfiliadoDatosCompraAction.java"
        );
        String actualizarContacto = leer(
                ACTION_DIR + "ActualizarContactoAfiliadoCompraAction.java"
        );
        String tieneSituacion = leer(
                ACTION_DIR + "TieneSituacionMedicaVigenteCompraAction.java"
        );
        String verSituacion = leer(
                ACTION_DIR + "VerSituacionMedicaVigenteCompraAction.java"
        );

        String[] paths = new String[] {
                "/compras/buscar_medicamentos",
                "/compras/buscar_item_tecnico",
                "/compras/buscar_items_historicos_afiliado",
                "/compras/buscar_prestadores_enviados",
                "/compras/buscar_afiliado_datos",
                "/compras/buscar_afiliado_fecha_vto_documentacion",
                "/compras/tiene_situacion_medica_vigente",
                "/compras/ver_situacion_medica_vigente"
        };

        for (int i = 0; i < paths.length; i++) {
            contiene(struts, "path AJAX Compras", "path=\"" + paths[i] + "\"");
        }

        contiene(
                medicamentos,
                "resultados medicamento",
                "\"COMPRAS_RESULTADOS_MEDICAMENTOS\""
        );
        contiene(
                medicamentos,
                "error medicamento",
                "\"COMPRAS_ERROR_BUSQUEDA_MEDICAMENTOS\""
        );
        contiene(
                medicamentos,
                "forward medicamento",
                "\"portlet.compras.buscar_medicamentos\""
        );

        contiene(
                itemTecnico,
                "consulta requerimiento persistido",
                "BusquedaRequerimientoCompraServiceUtil"
        );
        contiene(
                itemTecnico,
                "estado editable requerido",
                ".puedeEditarEstructura()"
        );
        contiene(
                itemTecnico,
                "sector persistido",
                ".getSectorDescripcion()"
        );
        contiene(
                itemTecnico,
                "callback namespaced",
                "response.getNamespace()"
        );

        validarCommonNull(historicos, "historico");
        contiene(historicos, "permiso historico", "ROL_ABM_COMPRAS");
        contiene(historicos, "campo id prestacion", "\\\"idPrestacion\\\"");
        contiene(
                historicos,
                "campo tipo nomenclador",
                "\\\"idTipoNomenclador\\\""
        );
        contiene(historicos, "campo codigo", "\\\"codigo\\\"");
        contiene(historicos, "campo descripcion", "\\\"descripcion\\\"");
        contiene(
                historicos,
                "ServiceUtil directo historico",
                "BusquedaRequerimientoCompraServiceUtil"
        );

        contiene(prestadores, "permiso cotizar", "ROL_COTIZAR_COMPRAS");
        contiene(
                prestadores,
                "atributo prestadores",
                "PRESTADORES_ENVIADOS_COTIZACION"
        );
        contiene(
                prestadores,
                "forward prestadores",
                "FORWARD_COMPRAS_PRESTADORES_ENVIADOS"
        );

        contiene(
                vencimiento,
                "adapter Autorizaciones",
                "extends BuscarAfiliadoFechaVtoDiscapacidad"
        );
        validarCommonNull(vencimiento, "vencimiento documental");

        contiene(
                datosAfiliado,
                "adapter datos afiliado",
                "extends AfiliadoDatosJSONAction"
        );
        validarCommonNull(datosAfiliado, "datos afiliado");
        contiene(
                struts,
                "mapping datos afiliado",
                "type=\"ar.com.ospim.compras.requerimientos.action."
                        + "BuscarAfiliadoDatosCompraAction\""
        );
        contiene(
                busquedaAfiliado,
                "default datos afiliado",
                "\"/autorizaciones/buscar_afiliado_datos\""
        );
        contiene(
                busquedaAfiliado,
                "allowlist datos afiliado",
                "!\"/compras/buscar_afiliado_datos\".equals"
        );
        contiene(
                componenteAfiliadoCompra,
                "endpoint datos afiliado Compras",
                "name=\"datos_afiliado_struts_action\""
        );
        contiene(
                componenteAfiliadoCompra,
                "valor endpoint datos afiliado Compras",
                "value=\"/compras/buscar_afiliado_datos\""
        );

        contiene(
                actualizarContacto,
                "vinculacion contacto",
                "if (\"bind\".equals(cmd))"
        );
        validarCommonNull(actualizarContacto, "vinculacion contacto");
        contiene(
                actualizarContactoLegacy,
                "contacto integrante sin imagen",
                "getAfiliadoEntry(cuilTitular, inte, false)"
        );
        contiene(
                actualizarContactoLegacy,
                "contacto titular sin imagen",
                "getAfiliadoEntry(cuilTitular, 0, false)"
        );

        validarCommonNull(tieneSituacion, "situacion medica");
        contiene(tieneSituacion, "permiso view", "ROL_VIEW_COMPRAS");
        contiene(tieneSituacion, "permiso ABM", "ROL_ABM_COMPRAS");
        contiene(tieneSituacion, "permiso cotizar", "ROL_COTIZAR_COMPRAS");
        contiene(
                tieneSituacion,
                "JSON situacion medica",
                "\\\"tieneSituacionMedica\\\""
        );
        contiene(
                tieneSituacion,
                "fail closed situacion medica",
                "tieneSituacionMedica =\n                    false"
        );
        contiene(
                tieneSituacion,
                "ServiceUtil directo situacion medica",
                "BusquedaRequerimientoCompraServiceUtil"
        );

        contiene(verSituacion, "permiso popup", "ROL_VIEW_COMPRAS");
        contiene(
                verSituacion,
                "atributo popup",
                "SITUACIONES_MEDICAS_VIGENTES_COMPRA"
        );
        contiene(
                verSituacion,
                "forward popup",
                "FORWARD_COMPRAS_SITUACION_MEDICA_VIGENTE"
        );

        System.out.println("CONTRATO_COMPRAS_AJAX_CONSULTAS_OK");
    }

    private static void validarCommonNull(String contenido, String nombre) {
        contiene(contenido, nombre + " usa COMMON_NULL", "ActionConstants.COMMON_NULL");
    }

    private static String leer(String ruta) throws Exception {
        Path path = Paths.get(ruta);
        return new String(Files.readAllBytes(path), ISO_8859_1);
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
}
