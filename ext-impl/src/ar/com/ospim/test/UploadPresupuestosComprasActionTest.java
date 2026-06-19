package ar.com.ospim.test;

import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.compras.requerimientos.action.UploadPresupuestosComprasAction;

import java.util.ArrayList;
import java.util.List;

public class UploadPresupuestosComprasActionTest {

    public static void main(String[] args)
            throws Exception {

        AccionPrueba accion =
                new AccionPrueba();

        List<TercerizadoraServicio> tercerizadoras =
                new ArrayList<TercerizadoraServicio>();

        tercerizadoras.add(
                new TercerizadoraServicio(
                        "omi",
                        "OMINT"
                )
        );

        assertString(
                "codigo canonico",
                "OMI",
                accion.resolver(
                        tercerizadoras,
                        " OMI "
                )
        );

        assertException(
                "codigo manipulado",
                accion,
                tercerizadoras,
                "../OMI"
        );

        tercerizadoras.add(
                new TercerizadoraServicio(
                        "BAD/CODE",
                        "Invalida"
                )
        );

        assertException(
                "codigo inseguro en catalogo",
                accion,
                tercerizadoras,
                "BAD/CODE"
        );

        assertString(
                "nombre sin ruta",
                "presupuesto.pdf",
                accion.nombreArchivo(
                        "..\\temporal\\presupuesto.pdf"
                )
        );

        assertString(
                "extension valida",
                ".pdf",
                accion.extension(
                        "presupuesto.pdf"
                )
        );

        assertString(
                "extension insegura",
                "",
                accion.extension(
                        "presupuesto.p/df"
                )
        );

        assertString(
                "titulo normalizado",
                "presupuesto_2026.pdf",
                accion.normalizarTitulo(
                        "presupuesto/2026.pdf"
                )
        );

        assertString(
                "nombre persistido",
                "PRESUPUESTO-COMPRA-25-OMI-abc123.pdf",
                accion.nombrePersistido(
                        "PRESUPUESTO-COMPRA-25-OMI-",
                        "abc123",
                        ".pdf"
                )
        );
    }

    private static void assertException(
            String descripcion,
            AccionPrueba accion,
            List<TercerizadoraServicio> tercerizadoras,
            String codigo)
            throws Exception {

        try {
            accion.resolver(
                    tercerizadoras,
                    codigo
            );
        } catch (Exception e) {
            return;
        }

        throw new AssertionError(
                descripcion
                        + ": se esperaba rechazo."
        );
    }

    private static void assertString(
            String descripcion,
            String esperado,
            String actual) {

        if (esperado == null
                ? actual != null
                : !esperado.equals(actual)) {

            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static class AccionPrueba
            extends UploadPresupuestosComprasAction {

        public String resolver(
                List<TercerizadoraServicio> tercerizadoras,
                String codigo)
                throws Exception {

            return resolverCodigoTercerizadora(
                    tercerizadoras,
                    codigo
            );
        }

        public String nombreArchivo(
                String nombre) {

            return obtenerNombreArchivo(nombre);
        }

        public String extension(
                String nombre) {

            return obtenerExtensionSegura(nombre);
        }

        public String normalizarTitulo(
                String nombre) {

            return normalizarComponenteTitulo(nombre);
        }

        public String nombrePersistido(
                String prefijo,
                String identificador,
                String extension) {

            return construirNombrePersistido(
                    prefijo,
                    identificador,
                    extension
            );
        }
    }

    private UploadPresupuestosComprasActionTest() {
    }
}
