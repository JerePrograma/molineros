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
                "empresa resuelta desde padron",
                helper,
                "EmpresaServiceUtil.getEmpleadores("
        );
        assertContains(
                "empresa activa",
                helper,
                "encontrada.getBaja_fecha() != null"
        );
        assertContains(
                "snapshot canonico",
                helper,
                "empresa.getRazon_soc()"
        );
        assertContains(
                "identidad de empresa recibida sin razon social",
                upload,
                "nombreParametro\n                                            + \"_empresa_sucursal\""
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
                "consulta filtra tipo",
                busquedaImpl,
                "+ \"AND rp.tipo_documento = ? \""
        );
        assertContains(
                "consulta mapea empresa",
                busquedaImpl,
                "setEmpresaCuit(getString(rs, \"empresa_cuit\"))"
        );
        assertContains(
                "overload controlado",
                busquedaUtil,
                "validarTipoDocumentoPresupuesto(tipoDocumento)"
        );
        assertContains(
                "firma JDBC incluye SMALLINT",
                edicionImpl,
                "stmt.setShort(3, presupuesto.getTipoDocumento().shortValue())"
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
                "IF v_estado_requerimiento <> 2 THEN"
        );
        assertContains(
                "Empresa queda en PENDIENTE",
                sql,
                "IF v_estado_requerimiento <> 1 THEN"
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
                "flujo prestador conserva ENVIADO",
                sql,
                "v_estado_envio <> 'ENVIADO'"
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
                "buscador usa infraestructura legacy",
                buscador,
                "EmpresaServiceUtil.getEmpleadores("
        );
        assertContains(
                "buscador limita CUIT funcional",
                selector,
                "maxlength=\"11\""
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
