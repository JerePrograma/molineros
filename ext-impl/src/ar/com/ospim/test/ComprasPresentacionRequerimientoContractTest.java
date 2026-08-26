package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Contrato del contexto de presentacion publicado por las Actions Compras. */
public final class ComprasPresentacionRequerimientoContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    private static final String ACTION_DIR =
            "ext-impl/src/ar/com/ospim/compras/requerimientos/action/";

    private ComprasPresentacionRequerimientoContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String util = leer(
                ACTION_DIR + "RequerimientoCompraRenderActionUtil.java"
        );
        String editar = leer(
                ACTION_DIR + "EditarRequerimientoCompraAction.java"
        );
        String ver = leer(
                ACTION_DIR + "VerRequerimientoCompraAction.java"
        );
        String presupuestos = leer(
                ACTION_DIR + "UploadPresupuestosComprasAction.java"
        );

        Path helperEliminado = Paths.get(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "RequerimientoCompraPresentacionHelper.java"
        );
        check(
                !Files.exists(helperEliminado),
                "El ActionUtil no debe conservar el Helper de presentacion"
        );

        contiene(util, "ServiceUtil directo", "BusquedaRequerimientoCompraServiceUtil");
        contiene(util, "prestadores enviados", ".listarPrestadoresEnviados(");
        contiene(util, "presupuestos", ".listarPresupuestos(");
        contiene(util, "ordenes medicas", ".listarOrdenesMedicas(");
        contiene(util, "estado enviado", "WebKeysCompras.ENVIO_ENVIADO");
        contiene(util, "permiso de vista", ".puedeVerPresupuestos()");
        contiene(util, "permiso de cotizacion", ".puedeEditarCotizacion()");

        contiene(
                util,
                "identidad Document Library",
                "coincideIdentidadAsociacionDocumento("
        );
        contiene(util, "grupo del documento", "fileEntry.getGroupId()");
        contiene(util, "folder del documento", "fileEntry.getFolderId()");
        contiene(util, "nombre del documento", "fileEntry.getName()");
        contiene(util, "URL codificada", "HttpUtil.encodeURL(");

        String[] atributos = new String[] {
                "ATTR_PRESTADORES_ENVIADOS",
                "ATTR_ERROR_PRESTADORES_ENVIADOS",
                "ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO",
                "ATTR_PRESUPUESTOS",
                "ATTR_IDS_PRESTADORES_CON_PRESUPUESTO",
                "ATTR_ERROR_PRESUPUESTOS",
                "ATTR_PRESUPUESTO_DOCUMENTO_VALIDO",
                "ATTR_PRESUPUESTO_DOWNLOAD_URL",
                "ATTR_ORDENES_MEDICAS",
                "ATTR_ERROR_ORDENES_MEDICAS"
        };

        for (int i = 0; i < atributos.length; i++) {
            contiene(
                    util,
                    "publica atributo " + atributos[i],
                    "renderRequest.setAttribute(\n                "
                            + atributos[i]
            );
        }

        contiene(editar, "edicion publica contexto", ".publicarContexto(");
        contiene(ver, "vista publica contexto", ".publicarContexto(");
        contiene(presupuestos, "presupuestos publica contexto", ".publicarContexto(");

        System.out.println("CONTRATO_COMPRAS_PRESENTACION_REQUERIMIENTO_OK");
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

        check(
                contenido.indexOf(esperado) >= 0,
                etiqueta + ": falta [" + esperado + "]"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
