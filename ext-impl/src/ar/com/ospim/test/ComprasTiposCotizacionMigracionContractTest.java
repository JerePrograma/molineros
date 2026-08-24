package ar.com.ospim.test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Contrato de despliegue del catalogo de tipos de cotizacion. */
public final class ComprasTiposCotizacionMigracionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String migration = leer(
                "docs/sql/"
                        + "20260824_habilitar_tipos_cotizacion_"
                        + "prestaciones_medicas.sql"
        );
        String schema = leer(
                "ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql"
        );
        String service = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/service/"
                        + "BusquedaRequerimientoCompraServiceImpl.java"
        );
        String scripts = leer(
                "ext-web/docroot/html/portlet/compras/requerimientos/partials/"
                        + "requerimiento_compra_detalle_scripts_edicion_componente.jsp"
        );

        contiene(
                migration,
                "busca sector por descripcion normalizada",
                "= 'PRESTACIONES MEDICAS'"
        );
        noContiene(
                migration,
                "no depende del id fisico del sector",
                "WHERE id_sector = 2"
        );
        contiene(migration, "Protesis Trauma", "(3, U&'Pr\\00F3tesis Traumatolog\\00EDa'");
        contiene(migration, "Protesis Cardio", "(4, U&'Pr\\00F3tesis Cardiolog\\00EDa'");
        contiene(migration, "Protesis General", "(5, U&'Pr\\00F3tesis General'");
        contiene(migration, "Insumos", "(6, 'Insumos'");
        contiene(migration, "Panales", "(7, U&'Pa\\00F1ales'");
        contiene(
                migration,
                "migracion idempotente",
                "ON CONFLICT (id_tipo_prestacion)"
        );
        contiene(
                migration,
                "corrige asociacion sectorial",
                "id_sector = EXCLUDED.id_sector"
        );
        contiene(
                migration,
                "valida cinco tipos",
                "IF v_cantidad_tipos <> 5"
        );
        contiene(
                service,
                "Java consulta catalogo SQL",
                "{call compras.listar_tipos_prestacion()}"
        );
        contiene(
                schema,
                "funcion publica id sector",
                "t.id_sector"
        );
        contiene(
                scripts,
                "UI filtra por sector",
                "String(tipo.idSector) != idSector"
        );

        System.out.println("COMPRAS_TIPOS_COTIZACION_MIGRACION_OK");
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

    private ComprasTiposCotizacionMigracionContractTest() {
    }
}
