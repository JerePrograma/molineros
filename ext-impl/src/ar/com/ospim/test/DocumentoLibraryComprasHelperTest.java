package ar.com.ospim.test;

import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.documentos.OrdenMedicaValidada;

import com.liferay.portal.service.ServiceContext;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;

public final class DocumentoLibraryComprasHelperTest {

    public static void main(String[] args) throws Exception {
        final HelperPrueba helper = new HelperPrueba();
        File jpeg = crearArchivo(
                "orden-medica-jpeg",
                ".jpg",
                new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x01}
        );
        File png = crearArchivo(
                "orden-medica-png",
                ".png",
                new byte[] {
                        (byte) 0x89, 0x50, 0x4E, 0x47,
                        0x0D, 0x0A, 0x1A, 0x0A
                }
        );
        File vacio = File.createTempFile("orden-medica-vacia", ".jpg");
        final File firmaInvalida = crearArchivo(
                "orden-medica-firma",
                ".jpg",
                new byte[] {0x01, 0x02, 0x03, 0x04}
        );

        try {
            helper.validarFirma(jpeg, "image/jpeg");
            helper.validarFirma(png, "image/png");

            assertException("firma JPEG invalida", new Ejecucion() {
                public void ejecutar() throws Exception {
                    helper.validarFirma(firmaInvalida, "image/jpeg");
                }
            });
            assertException("fecha ausente", new Ejecucion() {
                public void ejecutar() throws Exception {
                    helper.parseFecha("");
                }
            });
            assertException("fecha inexistente", new Ejecucion() {
                public void ejecutar() throws Exception {
                    helper.parseFecha("2026-02-30");
                }
            });

            Date fecha = helper.parseFecha("2026-08-12");
            assertEquals("fecha valida", "2026-08-12", fecha.toString());

            assertDefensivaValida(
                    helper,
                    new OrdenMedicaValidada(
                            jpeg,
                            "orden-medica.jpg",
                            ".jpg",
                            "image/jpeg",
                            fecha
                    )
            );
            assertDefensivaValida(
                    helper,
                    new OrdenMedicaValidada(
                            png,
                            "orden-medica.png",
                            ".png",
                            "image/png",
                            fecha
                    )
            );

            assertDefensivaRechaza(
                    "archivo vacio",
                    helper,
                    new OrdenMedicaValidada(
                            vacio,
                            "orden-medica.jpg",
                            ".jpg",
                            "image/jpeg",
                            fecha
                    )
            );
            assertDefensivaRechaza(
                    "extension distinta al nombre",
                    helper,
                    new OrdenMedicaValidada(
                            jpeg,
                            "orden-medica.png",
                            ".jpg",
                            "image/jpeg",
                            fecha
                    )
            );
            assertDefensivaRechaza(
                    "MIME distinto a la extension",
                    helper,
                    new OrdenMedicaValidada(
                            jpeg,
                            "orden-medica.jpg",
                            ".jpg",
                            "image/png",
                            fecha
                    )
            );
            assertDefensivaRechaza(
                    "firma invalida",
                    helper,
                    new OrdenMedicaValidada(
                            firmaInvalida,
                            "orden-medica.jpg",
                            ".jpg",
                            "image/jpeg",
                            fecha
                    )
            );

            assertEquals(
                    "nombre persistido",
                    "ORDEN-MEDICA-COMPRA-25-abc123.png",
                    helper.construirNombreOrdenMedica(
                            25,
                            "abc123",
                            ".png"
                    )
            );
            assertEquals(
                    "path traversal rechazado",
                    "",
                    helper.obtenerNombreArchivo("../orden-medica.jpg")
            );
        } finally {
            jpeg.delete();
            png.delete();
            vacio.delete();
            firmaInvalida.delete();
        }
    }

    private static void assertDefensivaValida(
            HelperPrueba helper,
            OrdenMedicaValidada ordenMedica) throws Exception {

        invocarValidacionDefensiva(helper, ordenMedica);
    }

    private static void assertDefensivaRechaza(
            String descripcion,
            final HelperPrueba helper,
            final OrdenMedicaValidada ordenMedica) throws Exception {

        assertException(descripcion, new Ejecucion() {
            public void ejecutar() throws Exception {
                invocarValidacionDefensiva(helper, ordenMedica);
            }
        });
    }

    private static void invocarValidacionDefensiva(
            HelperPrueba helper,
            OrdenMedicaValidada ordenMedica) throws Exception {

        Method metodo = DocumentoLibraryComprasHelper.class
                .getDeclaredMethod(
                        "validarOrdenMedicaPreparada",
                        OrdenMedicaValidada.class
                );
        metodo.setAccessible(true);

        try {
            metodo.invoke(helper, ordenMedica);
        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();

            if (causa instanceof Exception) {
                throw (Exception) causa;
            }

            throw e;
        }
    }

    private static File crearArchivo(
            String prefijo,
            String extension,
            byte[] contenido) throws Exception {

        File archivo = File.createTempFile(prefijo, extension);
        FileOutputStream output = null;

        try {
            output = new FileOutputStream(archivo);
            output.write(contenido);
        } finally {
            if (output != null) {
                output.close();
            }
        }

        return archivo;
    }

    private static void assertException(
            String descripcion,
            Ejecucion ejecucion) throws Exception {

        try {
            ejecucion.ejecutar();
            throw new AssertionError(
                    descripcion + ": se esperaba una excepcion"
            );
        } catch (AssertionError e) {
            throw e;
        } catch (Exception esperada) {
            // Resultado esperado.
        }
    }

    private static void assertEquals(
            String descripcion,
            String esperado,
            String actual) {

        if (esperado == null ? actual != null : !esperado.equals(actual)) {
            throw new AssertionError(
                    descripcion
                            + ": esperado=" + esperado
                            + ", actual=" + actual
            );
        }
    }

    private interface Ejecucion {
        void ejecutar() throws Exception;
    }

    private static final class HelperPrueba
            extends DocumentoLibraryComprasHelper {

        HelperPrueba() throws Exception {
            super(crearContexto());
        }

        public long obtenerMaximoTamanoArchivo() {
            return 1024L;
        }

        protected String detectarContentTypePorNombre(
                String nombreOriginal) {

            return nombreOriginal != null
                    && nombreOriginal.toLowerCase().endsWith(".png")
                    ? "image/png"
                    : "image/jpeg";
        }

        Date parseFecha(String value) throws Exception {
            return parseFechaDocumento(value);
        }

        void validarFirma(File archivo, String contentType)
                throws Exception {

            validarFirmaImagen(archivo, contentType);
        }

        private static ServiceContext crearContexto() {
            ServiceContext contexto = new ServiceContext();
            contexto.setScopeGroupId(1L);
            contexto.setUserId(1L);
            return contexto;
        }
    }

    private DocumentoLibraryComprasHelperTest() {
    }
}
