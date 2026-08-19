package ar.com.ospim.test;

import ar.com.ospim.compras.requerimientos.service.CotizacionPrestadorMailHelper;

import java.lang.reflect.Method;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeUtility;

public class CotizacionPrestadorMailHelperTest {

    public static void main(String[] args) throws Exception {
        assertFirmasPublicasCompatibles();
        assertMultipartHistoricoConTextoYPdf();
        assertMultipartConPdfYJpeg();
        assertMultipartConPdfYPng();
    }

    private static void assertFirmasPublicasCompatibles()
            throws Exception {

        Method historico = CotizacionPrestadorMailHelper.class.getMethod(
                "enviar",
                String.class,
                String.class,
                String.class,
                byte[].class,
                String.class
        );

        Method ordenMedica = CotizacionPrestadorMailHelper.class.getMethod(
                "enviar",
                String.class,
                String.class,
                String.class,
                byte[].class,
                String.class,
                byte[].class,
                String.class,
                String.class
        );

        if (historico == null || ordenMedica == null) {
            throw new AssertionError(
                    "Falta una firma publica del helper de correo."
            );
        }
    }

    private static void assertMultipartHistoricoConTextoYPdf()
            throws Exception {

        HelperPrueba helper = new HelperPrueba();
        Multipart multipart = helper.construir(
                "Cuerpo historico",
                pdf(),
                "PedidoPresupuesto_10.pdf",
                null,
                null,
                null
        );

        assertInt("partes historicas", 2, multipart.getCount());
        assertContains(
                "parte de texto",
                multipart.getBodyPart(0).getContentType(),
                "text/plain"
        );
        assertAdjunto(
                multipart.getBodyPart(1),
                "PedidoPresupuesto_10.pdf",
                "application/pdf"
        );
    }

    private static void assertMultipartConPdfYJpeg()
            throws Exception {

        HelperPrueba helper = new HelperPrueba();
        String nombreOriginal = "orden-médica.jpeg";
        Multipart multipart = helper.construir(
                "Cuerpo con adjuntos",
                pdf(),
                "PedidoPresupuesto_10.pdf",
                new byte[] {
                        (byte) 0xFF,
                        (byte) 0xD8,
                        (byte) 0xFF
                },
                nombreOriginal,
                "image/jpeg"
        );

        assertInt("texto + PDF + JPEG", 3, multipart.getCount());
        assertAdjunto(
                multipart.getBodyPart(1),
                "PedidoPresupuesto_10.pdf",
                "application/pdf"
        );
        assertAdjunto(
                multipart.getBodyPart(2),
                nombreOriginal,
                "image/jpeg"
        );
    }

    private static void assertMultipartConPdfYPng()
            throws Exception {

        HelperPrueba helper = new HelperPrueba();
        Multipart multipart = helper.construir(
                "Cuerpo con adjuntos",
                pdf(),
                "PedidoPresupuesto_11.pdf",
                new byte[] {
                        (byte) 0x89,
                        0x50,
                        0x4E,
                        0x47
                },
                "orden-original.png",
                "image/png"
        );

        assertInt("texto + PDF + PNG", 3, multipart.getCount());
        assertAdjunto(
                multipart.getBodyPart(1),
                "PedidoPresupuesto_11.pdf",
                "application/pdf"
        );
        assertAdjunto(
                multipart.getBodyPart(2),
                "orden-original.png",
                "image/png"
        );
    }

    private static void assertAdjunto(
            BodyPart parte,
            String nombreEsperado,
            String contentTypeEsperado) throws Exception {

        assertString(
                "nombre del adjunto",
                nombreEsperado,
                MimeUtility.decodeText(
                        parte.getFileName()
                )
        );

        assertString(
                "tipo MIME del adjunto",
                contentTypeEsperado,
                parte.getDataHandler().getContentType()
        );
    }

    private static byte[] pdf() {
        return new byte[] {
                37,
                80,
                68,
                70
        };
    }

    private static void assertContains(
            String descripcion,
            String actual,
            String esperado) {

        if (actual == null || actual.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion
                            + ": esperado contener="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
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

    private static void assertInt(
            String descripcion,
            int esperado,
            int actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static final class HelperPrueba
            extends CotizacionPrestadorMailHelper {

        private Multipart construir(
                String cuerpo,
                byte[] pedidoPresupuestoPdf,
                String nombrePedidoPresupuestoPdf,
                byte[] ordenMedica,
                String nombreOrdenMedica,
                String contentTypeOrdenMedica)
                throws Exception {

            return construirMultipart(
                    cuerpo,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf,
                    ordenMedica,
                    nombreOrdenMedica,
                    contentTypeOrdenMedica
            );
        }
    }

    private CotizacionPrestadorMailHelperTest() {
    }
}
