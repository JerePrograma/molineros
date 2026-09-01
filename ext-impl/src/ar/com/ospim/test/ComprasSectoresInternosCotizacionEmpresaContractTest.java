package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class ComprasSectoresInternosCotizacionEmpresaContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String webKeys = leer(
                "ext-impl/src/ar/com/ospim/compras/WebKeysCompras.java"
        );
        String requerimiento = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompra.java"
        );
        String presupuesto = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/beans/"
                        + "RequerimientoCompraPresupuesto.java"
        );
        String cambioEstado = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "EditarRequerimientoCompraHelper.java"
        );
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "PresupuestoCompraHelper.java"
        );
        String upload = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "UploadPresupuestosComprasAction.java"
        );
        String editarAction = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "EditarRequerimientoCompraAction.java"
        );
        String buscador = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "BuscarEmpresasCotizacionCompraAction.java"
        );
        String busquedaImpl = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );
        String busquedaUtil = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceUtil.java"
        );
        String edicionImpl = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "EditarRequerimientoCompraServiceImpl.java"
        );
        String sql = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String migracion = leer(
                "docs/sql/20260827_aislar_cotizaciones_empresas_compras.sql"
        );
        String migracionBusqueda = leer(
                "docs/sql/20260901_optimizar_busqueda_empresas_compras.sql"
        );
        String acciones = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_acciones_componente.jsp"
        );
        String datosBasicos = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_datos_basicos_componente.jsp"
        );
        String detalle = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_editor_componente.jsp"
        );
        String detalleScripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );
        String detalleModelo = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_modelo_componente.jsp"
        );
        String detalleTabla = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_tabla_componente.jsp"
        );
        String documentos = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_documentos.jsp"
        );
        String documentosComponente = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_documentos_componente.jsp"
        );
        String modeloVista = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_modelo_vista_componente.jsp"
        );
        String listado = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_documentos_busqueda_resultado.jsp"
        );
        String selector = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/"
                        + "requerimiento_compra_empresa_busqueda_resultado.jsp"
        );
        String struts = leer("ext-web/docroot/WEB-INF/struts-config.xml");
        String tiles = leer("ext-web/docroot/WEB-INF/tiles-defs.xml");

        assertContains(
                "regla central de sectores internos",
                webKeys,
                "esSectorSinCotizacionPrestador("
        );
        assertContains("sector RRHH", webKeys, "\"RRHH\".equals(sector)");
        assertContains(
                "sector SISTEMAS",
                webKeys,
                "\"SISTEMAS\".equals(sector)"
        );
        assertContains(
                "envio bloqueado por modelo",
                requerimiento,
                "return !esSectorSinCotizacionPrestador()"
        );
        assertContains(
                "backend autoritativo usa modelo",
                cambioEstado,
                "if (!requerimiento.puedeEnviarACotizar())"
        );
        assertContains(
                "vista usa regla del modelo",
                acciones,
                "&& req.puedeEnviarACotizar();"
        );
        assertContains(
                "render permite documentos internos al rol Cotizar",
                editarAction,
                ".esSectorSinCotizacionPrestador()"
        );
        assertContains(
                "render exige estado administrable",
                editarAction,
                ".puedeAdministrarPresupuestos()"
        );
        assertContains(
                "reintentos bloqueados por modelo",
                requerimiento,
                "public boolean puedeReintentarNotificaciones()"
        );

        assertContains(
                "sector expuesto sin duplicar nombres en JS",
                datosBasicos,
                "data-sin-cotizacion-prestador=\"<%= sinCotizacionPrestadorAttr %>\""
        );
        assertContains(
                "fila de tipo omitida para requerimiento persistido",
                detalle,
                "|| !reqDetalle.esSectorSinCotizacionPrestador())"
        );
        assertContains(
                "selector opcional validado",
                detalleScripts,
                "selectTipoPrestacion.length > 0"
        );
        assertContains(
                "selector se rehabilita al volver a sector cotizable",
                detalleScripts,
                "selectTipoPrestacion.removeAttr('disabled')"
        );
        assertContains(
                "grilla omite tipo en internos persistidos",
                detalleModelo,
                "|| !reqDetalle.esSectorSinCotizacionPrestador()"
        );
        assertContains(
                "colspan parte de las cuatro columnas base",
                detalleModelo,
                "int detalleColspan =\n        4"
        );
        assertContains(
                "encabezado de tipo es condicional",
                detalleTabla,
                "if (mostrarTipoCotizacionDetalle)"
        );

        assertContains(
                "tipo documental empresa",
                presupuesto,
                "TIPO_DOCUMENTO_COTIZACION_EMPRESA = 3"
        );
        assertContains(
                "empresa resuelta desde consulta Compras",
                helper,
                ".buscarEmpresasCotizacion("
        );
        assertNotContains(
                "helper desacoplado de EmpresaServiceUtil",
                helper,
                "EmpresaServiceUtil"
        );
        assertContains(
                "snapshot canonico",
                helper,
                "empresa.getRazon_soc()"
        );
        assertContains(
                "identidad de empresa recibida sin razon social",
                upload,
                "\"_empresa_sucursal\""
        );
        assertNotContains(
                "razon social del navegador no se recibe",
                upload,
                "_descripcion_empresa"
        );
        assertContains(
                "mismo helper de Document Library",
                helper,
                ".obtenerOCrearFolderCompras("
        );
        assertContains(
                "nombre DL distingue Empresa",
                helper,
                "+ \"EMPRESA-\""
        );
        assertContains(
                "borrado restringe tipo segun sector",
                helper,
                ".TIPO_DOCUMENTO_COTIZACION_EMPRESA"
        );

        assertContains(
                "consulta Prestador filtra tipo uno",
                busquedaImpl,
                "+ \"AND rp.tipo_documento = 1 \""
        );
        assertContains(
                "consulta Empresa filtra tipo tres",
                busquedaImpl,
                "+ \"AND rp.tipo_documento = 3 \""
        );
        assertContains(
                "mapeo Empresa separado",
                busquedaImpl,
                "mapCotizacionEmpresa(ResultSet rs)"
        );
        assertContains(
                "lectura Empresa explicita",
                busquedaUtil,
                "listarCotizacionesEmpresa("
        );
        assertContains(
                "firma JDBC incluye SMALLINT",
                edicionImpl,
                "stmt.setShort(3, presupuesto.getTipoDocumento().shortValue())"
        );
        assertContains(
                "Prestador conserva firma JDBC historica",
                edicionImpl,
                "SQL_REGISTRAR_PRESUPUESTO"
        );

        assertContains(
                "columnas de Empresa",
                sql,
                "empresa_cuit VARCHAR(11)"
        );
        assertContains(
                "tipo 3 admitido",
                sql,
                "CHECK (tipo_documento IN (1, 2, 3))"
        );
        assertContains(
                "tipo 2 conserva fecha obligatoria",
                sql,
                "tipo_documento <> 2"
        );
        assertContains(
                "tipo 2 conserva fecha informada",
                sql,
                "OR fecha_documento IS NOT NULL"
        );
        assertContains(
                "tipo 2 conserva numero de receta",
                sql,
                "CONSTRAINT ck_compras_orden_medica_numero_receta"
        );
        assertContains(
                "unicidad de Empresa activa",
                sql,
                "ux_compras_presupuesto_requerimiento_empresa_activa"
        );
        assertContains(
                "prestador sigue en A COTIZAR",
                sql,
                "OR v_estado_requerimiento <> 2"
        );
        assertContains(
                "Empresa queda en PENDIENTE",
                sql,
                "OR v_estado_requerimiento <> 1"
        );
        assertContains(
                "SQL bloquea sectores externos para tipo 3",
                sql,
                "NOT IN ('RRHH', 'SISTEMAS')"
        );
        assertContains(
                "duplicado de Empresa se rechaza",
                sql,
                "rp.empresa_sucursal = btrim(p_empresa_sucursal)"
        );
        assertContains(
                "busqueda exclusiva de Compras",
                sql,
                "CREATE FUNCTION compras.buscar_empresas_cotizacion("
        );
        assertContains(
                "busqueda consulta tabla externa de solo lectura",
                sql,
                "FROM informacion_afip.empresa e"
        );
        assertContains(
                "busqueda rapida aditiva de Compras",
                sql,
                "CREATE FUNCTION compras.buscar_empresas_cotizacion_rapida("
        );
        assertContains(
                "busqueda por CUIT conserva identidad ordenada",
                sql,
                "ORDER BY e.cuit, e.sucursal"
        );
        assertNotContains(
                "busqueda por CUIT no recorre todo el padron como fallback",
                sql,
                "AND btrim(e.cuit) = v_cuit"
        );
        assertContains(
                "busqueda textual conserva coincidencia por contenido",
                sql,
                "LIKE '%' || upper(v_descripcion) || '%'"
        );
        assertContains(
                "busqueda textual limita antes de ordenar resultados",
                sql,
                "El subconjunto se corta sin orden interno por rendimiento"
        );
        assertContains(
                "elegibilidad focalizada del requerimiento",
                sql,
                "es_requerimiento_habilitado_busqueda_empresa_cotizacion("
        );
        assertNotContains(
                "canonico no depende de buscar_empleadores",
                sql,
                "buscar_empleadores"
        );
        assertNotContains(
                "canonico no depende de return_empleadores",
                sql,
                "return_empleadores"
        );
        assertContains(
                "flujo prestador conserva ENVIADO",
                sql,
                "v_estado_envio <> 'ENVIADO'"
        );
        assertContains(
                "firma SQL historica de Prestador sigue disponible",
                sql,
                "p_id_prestador INTEGER,\n    p_dl_group_id BIGINT"
        );
        assertContains(
                "flujo prestador conserva COTIZADO",
                sql,
                "SET estado_envio = 'COTIZADO'"
        );
        assertNotContains(
                "no reaparece matriz eliminada",
                sql,
                "sector_tipo_prestador"
        );

        assertContains(
                "UI de cotizaciones de Empresas",
                documentos,
                "Cotizaciones de empresas"
        );
        assertContains(
                "UI usa permiso de cotizacion",
                documentosComponente,
                "puedeAdministrarCotizacionEmpresaPantalla"
        );
        assertContains(
                "modo interactivo habilita documentos internos",
                modeloVista,
                "|| puedeAdministrarCotizacionEmpresaPantalla;"
        );
        assertContains(
                "selector devuelve cuit",
                selector,
                "empresa.getCuit()"
        );
        assertContains(
                "listado distingue Empresa",
                listado,
                "presupuesto.isCotizacionEmpresa()"
        );
        assertContains(
                "buscador usa Service de Compras",
                buscador,
                ".buscarEmpresasCotizacionRapida("
        );
        assertNotContains(
                "persistencia de Compras desacoplada de EmpresaServiceUtil",
                busquedaImpl,
                "EmpresaServiceUtil"
        );
        assertNotContains(
                "buscador desacoplado de EmpresaServiceUtil",
                buscador,
                "EmpresaServiceUtil"
        );
        assertContains(
                "endpoint valida requerimiento interno",
                buscador,
                "validarRequerimientoInterno("
        );
        assertContains(
                "endpoint valida elegibilidad sin cargar detalles",
                buscador,
                ".esRequerimientoHabilitadoBusquedaEmpresaCotizacion("
        );
        assertNotContains(
                "endpoint no carga requerimiento completo",
                buscador,
                ".getRequerimientoCompra("
        );
        assertContains(
                "popup conserva contexto del requerimiento",
                documentos,
                "WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA"
        );
        assertContains(
                "JavaScript Empresa queda en rama interna",
                documentos,
                "if (cotizacionEmpresaPresupuestos)"
        );
        assertContains(
                "buscador limita CUIT funcional",
                selector,
                "maxlength=\"11\""
        );
        assertContains(
                "buscador exige tres caracteres si no hay CUIT",
                selector,
                "cuit == '' && descripcion != '' && descripcion.length < 3"
        );
        assertContains(
                "endpoint exige tres caracteres si no hay CUIT",
                buscador,
                "else if (cuit == null"
        );
        assertContains(
                "buscador evita solicitudes simultaneas",
                selector,
                "boton.attr('disabled', 'disabled')"
        );
        assertContains(
                "buscador exige permiso existente",
                buscador,
                "WebKeysCompras.ROL_COTIZAR_COMPRAS"
        );
        assertContains(
                "mapping de busqueda",
                struts,
                "path=\"/compras/buscar_empresas_cotizacion\""
        );
        assertContains(
                "tile de busqueda",
                tiles,
                "portlet.compras.empresas.result.search"
        );

        assertContains(
                "migracion agrega columnas propias de Compras",
                migracion,
                "ADD COLUMN IF NOT EXISTS empresa_cuit VARCHAR(11)"
        );
        assertOccurrences(
                "migracion reemplaza ambas firmas de registro",
                migracion,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.registrar_requerimiento_presupuesto(",
                2
        );
        assertContains(
                "migracion conserva overload tipo tres",
                migracion,
                "p_tipo_documento SMALLINT"
        );
        assertContains(
                "migracion restringe baja generica a Prestador",
                migracion,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.baja_requerimiento_presupuesto("
        );
        assertContains(
                "migracion restringe reactivacion generica a Prestador",
                migracion,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.reactivar_requerimiento_presupuesto("
        );
        assertContains(
                "migracion consulta Empresa en modo lectura",
                migracion,
                "FROM informacion_afip.empresa e"
        );
        assertContains(
                "migracion conserva baja Empresa separada",
                migracion,
                "baja_cotizacion_empresa_requerimiento("
        );
        assertNotContains(
                "migracion no modifica buscar_empleadores",
                migracion,
                "buscar_empleadores"
        );
        assertNotContains(
                "migracion no modifica return_empleadores",
                migracion,
                "return_empleadores"
        );
        assertNotContains(
                "migracion no altera tabla externa",
                migracion,
                "ALTER TABLE informacion_afip.empresa"
        );
        assertNotContains(
                "migracion no escribe tabla externa",
                migracion,
                "UPDATE informacion_afip.empresa"
        );
        assertNotContains(
                "migracion no inserta en tabla externa",
                migracion,
                "INSERT INTO informacion_afip.empresa"
        );
        assertNotContains(
                "migracion no elimina de tabla externa",
                migracion,
                "DELETE FROM informacion_afip.empresa"
        );
        assertNotContains(
                "migracion no crea indices externos",
                migracion,
                "ON informacion_afip.empresa"
        );
        assertNotContains(
                "migracion no deja funciones no reejecutables",
                migracion,
                "CREATE FUNCTION compras."
        );

        assertContains(
                "migracion agrega busqueda rapida reejecutable",
                migracionBusqueda,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.buscar_empresas_cotizacion_rapida("
        );
        assertContains(
                "migracion agrega elegibilidad focalizada",
                migracionBusqueda,
                "es_requerimiento_habilitado_busqueda_empresa_cotizacion("
        );
        assertContains(
                "migracion limita antes de ordenar busqueda textual",
                migracionBusqueda,
                "El subconjunto se corta sin orden interno por rendimiento"
        );
        assertNotContains(
                "migracion no agrega fallback no indexable por CUIT",
                migracionBusqueda,
                "AND btrim(e.cuit) = v_cuit"
        );
        assertNotContains(
                "migracion conserva funcion de busqueda vigente",
                migracionBusqueda,
                "CREATE OR REPLACE FUNCTION "
                        + "compras.buscar_empresas_cotizacion("
        );
        assertNotContains(
                "optimizacion no altera tabla externa",
                migracionBusqueda,
                "ALTER TABLE informacion_afip.empresa"
        );
        assertNotContains(
                "optimizacion no escribe tabla externa",
                migracionBusqueda,
                "UPDATE informacion_afip.empresa"
        );
        assertNotContains(
                "optimizacion no inserta tabla externa",
                migracionBusqueda,
                "INSERT INTO informacion_afip.empresa"
        );
        assertNotContains(
                "optimizacion no elimina de tabla externa",
                migracionBusqueda,
                "DELETE FROM informacion_afip.empresa"
        );
        assertNotContains(
                "optimizacion no crea indices externos",
                migracionBusqueda,
                "ON informacion_afip.empresa"
        );
    }

    private static String leer(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(new File(path).toPath());
        return new String(bytes, LATIN1);
    }

    private static void assertContains(
            String descripcion,
            String texto,
            String esperado) {

        if (texto.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion + ": falta [" + esperado + "]"
            );
        }
    }

    private static void assertOccurrences(
            String descripcion,
            String texto,
            String esperado,
            int cantidadEsperada) {

        int cantidad = 0;
        int desde = 0;
        int encontrado = texto.indexOf(esperado, desde);

        while (encontrado >= 0) {
            cantidad++;
            desde = encontrado + esperado.length();
            encontrado = texto.indexOf(esperado, desde);
        }

        if (cantidad != cantidadEsperada) {
            throw new AssertionError(
                    descripcion + ": esperado=" + cantidadEsperada
                            + ", obtenido=" + cantidad
            );
        }
    }

    private static void assertNotContains(
            String descripcion,
            String texto,
            String prohibido) {

        if (texto.indexOf(prohibido) >= 0) {
            throw new AssertionError(
                    descripcion + ": contiene [" + prohibido + "]"
            );
        }
    }

    private ComprasSectoresInternosCotizacionEmpresaContractTest() {
    }
}
